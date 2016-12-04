package com.circuits.circuitsmod.testingclasses;

import com.circuits.circuitsmod.busblock.BusSegment;
import com.circuits.circuitsmod.common.BusData;
import com.circuits.circuitsmod.testblock.TileEntityTesting;

public class TestingUtilityMethods {
	public static void checkIfRedstoneSucceeds(TileEntityTesting testEntity, TestTickResult testResult, boolean expectedAnswer) {
		if (expectedAnswer) {
			if (TileEntityTesting.isSidePowered(testEntity, testEntity.getInputFace().getFacing())) {
				testResult.setCurrentlySucceeding(true);
			} else{
				testResult.setCurrentlySucceeding(false);
			}
		} else {
			if (!TileEntityTesting.isSidePowered(testEntity, testEntity.getInputFace().getFacing())) {
				testResult.setCurrentlySucceeding(true);
			} else {
				testResult.setCurrentlySucceeding(false);
			}
		}
	}
	
	public static void checkIfBusSucceeds(TileEntityTesting testEntity, TestTickResult testResult, BusData expectedAnswer) {
		BusSegment dummySeg = testEntity.getDummySeg();
		
		if (dummySeg.getCurrentVal().equals(expectedAnswer)) {
			testResult.setCurrentlySucceeding(true);
		} else {
			testResult.setCurrentlySucceeding(false);
		}
	}
}
