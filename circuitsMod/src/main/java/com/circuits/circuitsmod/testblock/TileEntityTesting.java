package com.circuits.circuitsmod.testblock;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.circuits.circuitsmod.busblock.BusSegment;
import com.circuits.circuitsmod.circuitblock.CircuitBlock;
import com.circuits.circuitsmod.circuitblock.CircuitTileEntity;
import com.circuits.circuitsmod.common.BlockFace;
import com.circuits.circuitsmod.common.PosUtils;
import com.circuits.circuitsmod.reflective.TestGeneratorInvoker;
import com.circuits.circuitsmod.telecleaner.StartupCommonCleaner;
import com.circuits.circuitsmod.testingclasses.PuzzleTest;
import com.circuits.circuitsmod.testingclasses.*;
import com.circuits.circuitsmod.testingclasses.TestTickResult;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.*;
@SuppressWarnings("unused")
public class TileEntityTesting extends TileEntity implements ITickable {

	private TestGeneratorInvoker testInvoker;
	private int levelID;
	private final String name = "tileentitytesting";
	private BusSegment emitterSeg;
	private BusSegment dummySeg;
	private BlockFace inputFace;
	
	public static final int emitterID = 7;
	public static final int dummyID = 20;

	private boolean checkResults = false;


	private boolean initialized = false;
	private boolean startTesting = false;

	public static final int DELAY = 25;
	private int testDelay = DELAY;

	private HashMap<Integer, PuzzleTest> testMap = new HashMap<Integer, PuzzleTest>();
	private HashMap<Integer, Integer> widthMap = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> dummyWidthMap = new HashMap<Integer, Integer>();

	private int[] redstoneOutputs = new int[EnumFacing.values().length];
	public int testCounter = 1;

	public static int getSidePower(TileEntityTesting testEntity, EnumFacing side) {
		return testEntity.getSidePower(side);
	}

	public static boolean isSidePowered(TileEntityTesting testEntity, EnumFacing side) {
		return testEntity.isSidePowered(side);
	}

	public void beginTesting(boolean startTesting) {
		this.startTesting = startTesting;
	}

	public int getLevelID() {
		return this.levelID;
	}

	public BusSegment getEmitterSegment() {
		return emitterSeg;
	}
	
	public BusSegment getDummySeg() {
		return dummySeg;
	}

	public BlockFace getInputFace() {
		return inputFace;
	}

	public void init(World worldIn, int levelID) {
		if (!getWorld().isRemote) {
			this.levelID = levelID;
			produceHashMap();
			Optional<BlockPos> candidatePos = this.searchForBlockPosOf(emitterID);
			Optional<BlockPos> dummyPos = this.searchForBlockPosOf(dummyID);

			//For now, we're looking for a basic emitter with a 4 bit input, so just look for that.
			//Later on, additional logic will have to be added for input greater than 4 bits.  
			Predicate<BusSegment> busPredicate = busSeg-> {
				return busSeg.getWidth() == dummyWidthMap.get(levelID);
			};

			emitterSeg = this.findBusSegment(candidatePos.get(), busPredicate).get();
			
			if (dummyPos.isPresent()) {
				dummySeg = this.findBusSegment(candidatePos.get(), busPredicate).get();
			}
			
			initialized = true;
		}
	}

	public void produceHashMap() {
		testMap.put(0, new TestAnd());
		testMap.put(1, new TestBusInverter());
		widthMap.put(0, 2);
		widthMap.put(1, 4);
		dummyWidthMap.put(1, 2);
	}

	public void update() {		
		if (!initialized || !startTesting) //called every tick, so if we're not ready to test, we need to back off.
			return;
		else if (initialized && startTesting) {
			PuzzleTest toRun = testMap.get(levelID);
			testDelay--;
			if (testDelay == 0) {
				testDelay = DELAY;
				if (!checkResults) {
					checkResults = true;
				} else {
					TestTickResult result = toRun.test(getWorld(), this);
					testCounter++;
					if(!result.getCurrentlySucceeding()) {
						startTesting = false;
						testCounter = 1;
						testDelay = DELAY;
					}
					if (result.getAtEndOfTest() && result.getCurrentlySucceeding()) {
						spawnTeleCleaner();
						startTesting = false;
						testCounter = 1;
						testDelay = DELAY;
					} 
						
				}
			}
			toRun.createInputData(this);
			toRun.setAndOutputData(getWorld(), testCounter - 1);
		}
	}



	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
	{
		return oldState.getBlock() != newState.getBlock();
	}

	private void spawnTeleCleaner() {
		getWorld().setBlockState(getPos().offset(EnumFacing.UP), StartupCommonCleaner.teleCleaner.getDefaultState(), 2);
	}

	public Optional<BusSegment> findBusSegment(BlockPos busPosition, Predicate<BusSegment> busPredicate) {
		Stream<BlockFace> faces = PosUtils.faces(busPosition);
		List<BlockFace> faceList = faces.collect(Collectors.toList());
		BusSegment maximumSegment = new BusSegment(0);
		int maxWidth = 0;
		//Find the face with the largest bus width.  This is our input.
		for (BlockFace face : faceList) {
			Optional<BusSegment> currentSegment = CircuitBlock.getBusSegmentAt(getWorld(), face);
			if (currentSegment.isPresent()) {
				if (busPredicate.test(currentSegment.get())) {
					return currentSegment;
				}
			}
		}
		return Optional.empty();
	}

	public  Optional<BlockPos> searchForBlockPosOf(int uidInt) {
		/**
		 * A predicate function to determine if the block is safe to search.
		 * A block is safe if it's less than 128 units away from the start.
		 */
		Predicate<BlockPos> safe = pos-> {
			return (pos.getDistance(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ()) < 128) && (Math.abs(this.getPos().getY() - pos.getY()) <= 3);
		};

		/*
		 * A predicate to determine if we've found an emitter.
		 * Returns true if we've found a circuit tile entity and its UID matches the emitters UID, currently 15.
		 */
		Predicate<BlockPos> success = pos-> {
			TileEntity entity = getWorld().getTileEntity(pos);
			if (entity instanceof CircuitTileEntity) {
				CircuitTileEntity circuitEntity = (CircuitTileEntity) entity;
				if (circuitEntity.getCircuitUID().getUID().toInteger() == uidInt) {
					inputFace = new BlockFace(getPos(), EnumFacing.SOUTH);
					return true;
				} else return false;
			}
			else return false;
		};
		//Search for the correct block position
		Optional<BlockPos> candidatePos = PosUtils.searchWithin(this.getPos(), safe, success);
		return candidatePos;
	}



	boolean isSidePowered(EnumFacing side) {
		return getSidePower(side) > 0;
	}

	int getSidePower(EnumFacing side) {
		BlockPos pos = getPos().offset(side);
		if (getWorld().getRedstonePower(pos, side) > 0) {
			return getWorld().getRedstonePower(pos, side);
		}
		else {
			IBlockState iblockstate1 = getWorld().getBlockState(pos);
			return iblockstate1.getBlock() == Blocks.REDSTONE_WIRE ? ((Integer)iblockstate1.getValue(BlockRedstoneWire.POWER)).intValue() : 0;
		}
	}

	public EnumFacing getParentFacing() {
		IBlockState parentState = getWorld().getBlockState(getPos());
		return (EnumFacing)parentState.getValue(BlockDirectional.FACING);
	}


}
