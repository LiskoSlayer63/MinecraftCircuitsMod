package com.circuits.circuitsmod.circuitblock;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import scala.actors.threadpool.Arrays;

import com.circuits.circuitsmod.busblock.BusBlock;
import com.circuits.circuitsmod.busblock.BusSegment;
import com.circuits.circuitsmod.busblock.StartupCommonBus;
import com.circuits.circuitsmod.circuit.CircuitInfoProvider;
import com.circuits.circuitsmod.circuit.CircuitUID;
import com.circuits.circuitsmod.circuitblock.WireDirectionMapper.WireDirectionGenerator;
import com.circuits.circuitsmod.common.ArrayUtils;
import com.circuits.circuitsmod.common.BlockFace;
import com.circuits.circuitsmod.common.BusData;
import com.circuits.circuitsmod.reflective.ChipInvoker;
import com.circuits.circuitsmod.reflective.Invoker;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CircuitTileEntity extends TileEntity {
	
	public Collection<BusSegment> getBusSegments() {
		return connectedBuses.values();
	}

	public Optional<BusSegment> getBusSegment(EnumFacing face) {
		return connectedBuses.containsKey(face) ? Optional.of(connectedBuses.get(face)) : Optional.empty();
	}
	
	public void setBusSegment(EnumFacing face, BusSegment seg) {
		connectedBuses.put(face, seg);
	}
	
	public CircuitUID getCircuitUID() {
		return this.circuitUID;
	}
	
	/**
	 * The circuit UID is the __only__ thing saved with this tile entity
	 * other than the invocation state
	 * The facing direction of the circuit block is stored in its block metadata
	 */
	private CircuitUID circuitUID = null;
	
	private final String name = "circuittileentity";
	
	/**
	 * Mapping from faces to bus segments on this circuit tile entity
	 */
	private Map<EnumFacing, BusSegment> connectedBuses = Maps.newHashMap();
	
	/**
	 * Stores the implementation invoker for this circuit
	 */
	private ChipInvoker impl = null;
	
	/**
	 * Stores the current state of the circuit (if sequential)
	 */
	private Invoker.State state = null;
	
	private WireDirectionMapper wireMapper = null;
	
	/**
	 * Buffer to store all redstone output signals delivered at this tick
	 */
	private boolean[] redstoneOuptuts = new boolean[EnumFacing.values().length];
	
	/**
	 * List of current impending inputs to this CircuitTileEntity,
	 * as passed by bus networks
	 */
	private List<BusData> inputData = null;
	
	public void receiveInput(EnumFacing face, BusData data) {
		Optional<Integer> inputIndex = wireMapper.getInputIndexOf(face);
		if (inputIndex.isPresent()) {
			inputData.set(inputIndex.get(), data);
		}
	}
	

	public void init(World worldIn, CircuitUID circuitUID) {
		this.circuitUID = circuitUID;
		
		if (!worldIn.isRemote && impl == null) {
			CircuitInfoProvider.ensureServerModelInit();
			
			IBlockState state = worldIn.getBlockState(getPos());
			update(state);
		}
	}
	
	public EnumFacing getParentFacing() {
		IBlockState parentState = getWorld().getBlockState(getPos());
		return (EnumFacing)parentState.getValue(BlockDirectional.FACING);
	}
	
	boolean isSidePowered(EnumFacing side) {
		BlockPos pos = getPos().offset(side);
		if (getWorld().getRedstonePower(pos, side) > 0) {
			return true;
		}
		else {
			IBlockState iblockstate1 = getWorld().getBlockState(pos);
			return iblockstate1.getBlock() == Blocks.REDSTONE_WIRE && ((Integer)iblockstate1.getValue(BlockRedstoneWire.POWER)).intValue() > 0;
		}
	}
	
	private void notifyNeighbor(EnumFacing side) {
        BlockPos blockpos1 = pos.offset(side);
        if(net.minecraftforge.event.ForgeEventFactory.onNeighborNotify(getWorld(), pos, getWorld().getBlockState(pos), java.util.EnumSet.of(side)).isCanceled())
            return;
        getWorld().notifyBlockOfStateChange(blockpos1, this.blockType);
        getWorld().notifyNeighborsOfStateExcept(blockpos1, this.blockType, side.getOpposite());
	}
	
	/**
	 * Clears any impending inputs from this circuit tile entity.
	 */
	private void clearInputs() {
		List<BusData> inputs = Lists.newArrayList();
		for (int width : this.impl.inputWidths()) {
			inputs.add(new BusData(width, 0));
		}
	}
	
	/**
	 * Clears all redstone outputs from this circuit tile entity
	 * @param state
	 */
	private void clearOutputs() {
		this.redstoneOuptuts = new boolean[EnumFacing.values().length];
	}
	
	/**
	 * Initializes the bus segment map for this circuit tile entity to
	 * be a collection of unique bus segments, one for each valid input/output
	 * as defined in the reflectively-called implementation
	 */
	private void initBusSegments() {	
		for (int i = 0; i < this.impl.numInputs(); i++) {
			EnumFacing inputFace = this.wireMapper.getInputFace(i);
			BusSegment inputSeg = new BusSegment(this.impl.inputWidths()[i]);
			//Input to a circuit tile entity is output from a bus segment
			inputSeg.addOutput(new BlockFace(this.getPos(), inputFace));
			this.connectedBuses.put(inputFace, inputSeg);
		}
		
		for (int i = 0; i < this.impl.numOutputs(); i++) {
			EnumFacing outputFace = this.wireMapper.getOutputFace(i);
			BusSegment outputSeg = new BusSegment(this.impl.outputWidths()[i]);
			//Output from a circuit tile entity is input to a bus segment
			outputSeg.addInput(new BlockFace(this.getPos(), outputFace));
			this.connectedBuses.put(outputFace, outputSeg);
		}
	}
	
	/**
	 * Unifies all existing bus segments on this circuit tile entity with bus segments
	 * of surrounding blocks according to incidence logic
	 */
	private void connectBuses() {
		//NOTE: For now, this is deliberately hilariously bad and inefficient, too
		//But for now, I don't care, because correctness matters more.
		
		Function<EnumFacing, Optional<BusSegment>> getSeg = (f) -> {
			return CircuitBlock.getBusSegmentAt(getWorld(), new BlockFace(getPos(), f).otherSide());
		};
		
		//We fundamentally need to handle two cases here:
		//direct connections to other circuit blocks, and connections to buses
		
		//Direct connections
		for (EnumFacing face : ArrayUtils.cat(this.wireMapper.getInputfaces(), this.wireMapper.getOutputFaces())) {
			Optional<BusSegment> seg = getSeg.apply(face);
			if (seg.isPresent()) {
				//Must be a direct connection
				this.getBusSegment(face).get().unifyWith(getWorld(), seg.get());
			}
			else {
				//Might be a bus, in which case we'll treat all surrounding buses as if they were just placed.
				BlockPos pos = this.getPos().offset(face);
				IBlockState blockState = getWorld().getBlockState(pos);
				if (blockState.getBlock() instanceof BusBlock) {
					BusBlock.connectOnPlace(getWorld(), pos, StartupCommonBus.busBlock.getMetaFromState(blockState));
				}
			}
		}
		
		
		
	}
	
	public void update(IBlockState state) {
		if (impl == null) {
			//If we're on the client, don't care about updating, we're just here
			//to look pretty
			if (getWorld() != null || !getWorld().isRemote) {
				if (CircuitInfoProvider.isServerModelInit()) {
					this.impl = CircuitInfoProvider.getInvoker(circuitUID);
					WireDirectionGenerator dirGen = CircuitInfoProvider.getWireDirectionGenerator(circuitUID);
					this.wireMapper = dirGen.getMapper(getParentFacing(), impl.numInputs(), impl.numOutputs());
					this.state = this.impl.initState();
					this.clearInputs();
					this.initBusSegments();
					this.connectBuses();
				}
				else {
					CircuitInfoProvider.ensureServerModelInit();
				}
			}
		}
		else {
			
			//By this point, we should already have received any incoming inputs from incident
			//bus segments, so we only need (for now) to deal explicitly with redstone inputs
			
			//Okay, so first, find all of the input faces with a declared
			//input width of 1, and fill the bus data values
			//with actual redstone signals coming into this block
			for (int redstoneIndex : this.impl.getRedstoneInputs()) {
				EnumFacing redstoneFace = this.wireMapper.getInputFace(redstoneIndex);
				if (isSidePowered(redstoneFace)) {
					//Active, so replace the bus data with a high signal
					this.inputData.set(redstoneIndex, new BusData(1,1));
				}
			}
			
			clearOutputs();
			
			//Okay, now that in theory, we have a complete input list, generate the output list
			//using the wrapped circuit implementation
			List<BusData> outputs = this.impl.invoke(this.state, this.inputData);
			
			clearInputs();
			
			//Okay, now we need to deliver any and all redstone output signals
			for (int redstoneIndex : this.impl.getRedstoneOutputs()) {
				if (outputs.get(redstoneIndex).getData() > 0) {
					this.redstoneOuptuts[this.wireMapper.getOutputFace(redstoneIndex).getIndex()] = true;
				}
			}
			
			//Okay, great. We've set the redstone outputs to be delivered, so now
			//we need to propagate the remaining outputs to connected bus networks.
			for (int i = 0; i < outputs.size(); i++) {
				
				EnumFacing side = wireMapper.getOutputFace(i);
				
				Optional<BusSegment> busSeg = this.getBusSegment(side);
				BusData data = outputs.get(i);
				if (busSeg.isPresent()) {
					busSeg.get().accumulate(getWorld(), new BlockFace(getPos(), side), data);
				}
				
				//If instead, we sent a redstone signal, just make sure to notify the next block over to update
				if (data.getWidth() == 1) {
					notifyNeighbor(side);
				}
			}
			
			getWorld().notifyNeighborsOfStateChange(getPos(), blockType);
		}
	}

	public int isProvidingWeakPower(IBlockState state, EnumFacing side) {
		if (impl != null) {
			return redstoneOuptuts[side.getIndex()] ? 15 : 0;
		}
		
		return 0;
	}
}
