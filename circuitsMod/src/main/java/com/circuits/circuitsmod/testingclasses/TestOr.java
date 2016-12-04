package com.circuits.circuitsmod.testingclasses;
import com.circuits.circuitsmod.busblock.BusSegment;
import com.circuits.circuitsmod.common.BlockFace;
import com.circuits.circuitsmod.common.BusData;
import com.circuits.circuitsmod.testblock.TileEntityTesting;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TestOr implements PuzzleTest {

	//int testCounter = 1;
	BlockFace inputFace;
	BusSegment segment;

	@Override
	public TestTickResult test(World worldIn, TileEntityTesting testEntity) {
		System.out.println("Testing");
		TestTickResult testResult = new TestTickResult();
		//createInputData(testEntity);

		switch (testEntity.testCounter) {
		case 1:
			TestingUtilityMethods.checkIfRedstoneSucceeds(testEntity, testResult, false);
			break;
		case 2:
			TestingUtilityMethods.checkIfRedstoneSucceeds(testEntity, testResult, true);
			break;
		case 3:
			TestingUtilityMethods.checkIfRedstoneSucceeds(testEntity, testResult, true);
			break;
		case 4:
			TestingUtilityMethods.checkIfRedstoneSucceeds(testEntity, testResult, true);
			break;
		}

		if (testEntity.testCounter >= 4) {
			testResult.setAtEndOfTest(true);
		}
		return testResult;
	}


	public void createInputData(TileEntityTesting testEntity) {
		segment = testEntity.getEmitterSegment();
		inputFace = testEntity.getInputFace();
		segment.addInput(inputFace);
	}

	public void setAndOutputData(World worldIn, int index) {
		BusData testingData;
		testingData = new BusData(2, index);
		segment.accumulate(worldIn, inputFace, testingData);
		segment.forceUpdate(worldIn);
	}

}
