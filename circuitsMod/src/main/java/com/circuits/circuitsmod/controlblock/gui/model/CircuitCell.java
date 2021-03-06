package com.circuits.circuitsmod.controlblock.gui.model;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.circuits.circuitsmod.circuit.CircuitInfo;
import com.circuits.circuitsmod.circuit.CircuitUID;
import com.circuits.circuitsmod.circuitblock.WireDirectionMapper.WireDirectionGenerator;

/**
 * Class which contains all information necessary for displaying a circuit (not specialized!)
 * [also not responsible for displaying the cost of the circuit]
 * in the GUI
 * @author bubble-07
 *
 */
public class CircuitCell implements CircuitTreeNode {
	private static final long serialVersionUID = 1L;
	
	private CircuitInfo info;
	private CircuitUID uid;
	private CircuitDirectory parent;
	
	public CircuitInfo getInfo() {
		return info;
	}
	public CircuitUID getUid() {
		return uid;
	}
	public CircuitCell(CircuitDirectory parent, CircuitUID uid, CircuitInfo info) {
		this.parent = parent;
		this.info = info;
		this.uid = uid;
	}
	
	public BufferedImage getImage() {
		return info.getImage();
	}
	public String getName() {
		return info.getName();
	}
	public String getDescription() {
		return info.getDescription();
	}
	public WireDirectionGenerator getWireDirectionGenerator() {
		return info.getWireDirectionGenerator();
	}
	
	@Override
	public List<CircuitTreeNode> getChildren() {
		return new ArrayList<>();
	}
	@Override
	public Optional<CircuitDirectory> getParent() {
		return Optional.of(parent);
	}
}
