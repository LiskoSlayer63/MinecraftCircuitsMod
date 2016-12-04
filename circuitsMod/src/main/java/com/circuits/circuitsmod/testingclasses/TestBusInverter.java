package com.circuits.circuitsmod.testingclasses;
import com.circuits.circuitsmod.busblock.BusSegment;
import com.circuits.circuitsmod.common.BlockFace;
import com.circuits.circuitsmod.common.BusData;
import com.circuits.circuitsmod.testblock.TileEntityTesting;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TestBusInverter implements PuzzleTest {
	//for this test, you have to put it on the right side of the emitter.
	BlockFace inputFace;
	BusSegment emmitterSeg;
	BusSegment dummySeg;

	@Override
	public TestTickResult test(World worldIn, TileEntityTesting testEntity) {
		System.out.println("Testing");
		TestTickResult testResult = new TestTickResult();
		
		switch (testEntity.testCounter) {
		case 1:
			TestingUtilityMethods.checkIfBusSucceeds(testEntity, testResult, new BusData(2, 3));
			break;
		case 2:
			TestingUtilityMethods.checkIfBusSucceeds(testEntity, testResult, new BusData(2, 2));
			break;
		case 3:
			TestingUtilityMethods.checkIfBusSucceeds(testEntity, testResult, new BusData(2, 1));
			break;
		case 4:
			TestingUtilityMethods.checkIfBusSucceeds(testEntity, testResult, new BusData(2, 0));
			break;
		}
		if (testEntity.testCounter >= 4) {
			testResult.setAtEndOfTest(true);
		}
		return testResult;
	}


	public void createInputData(TileEntityTesting testEntity) {
		emmitterSeg = testEntity.getEmitterSegment();
		inputFace = testEntity.getInputFace();
		dummySeg = testEntity.getDummySeg();
		emmitterSeg.addInput(inputFace);
	}

	public void setAndOutputData(World worldIn, int index) {
		BusData testingData;
		testingData = new BusData(4, index);
		emmitterSeg.accumulate(worldIn, inputFace, testingData);
		emmitterSeg.forceUpdate(worldIn);
	}

}
