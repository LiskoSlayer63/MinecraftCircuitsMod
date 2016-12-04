package com.circuits.circuitsmod.testingclasses;
import com.circuits.circuitsmod.busblock.BusSegment;
import com.circuits.circuitsmod.common.BlockFace;
import com.circuits.circuitsmod.common.BusData;
import com.circuits.circuitsmod.testblock.TileEntityTesting;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TestAnd implements PuzzleTest {

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
				//setAndOutputData(worldIn, 0);
				//delayObservation();
				checkIfStillSucceeding(testEntity, testResult, false);
				break;
		case 2:
				//setAndOutputData(worldIn, 1);
				//delayObservation();
				checkIfStillSucceeding(testEntity, testResult, false); 
			break;
		case 3:
				//setAndOutputData(worldIn, 2);
				//delayObservation();
				checkIfStillSucceeding(testEntity, testResult, false);
			break;
		case 4:
			//setAndOutputData(worldIn, 3);
			checkIfStillSucceeding(testEntity, testResult, true);
			break;
		}
		//testCounter++;
		//determineOverallSuccess(testResult, testEntity);
		//testEntity.beginTesting(false);
		if (testEntity.testCounter >= 4) {
			testResult.setAtEndOfTest(true);
		}
		return testResult;
	}


	public void createInputData(TileEntityTesting testEntity) {
		segment = testEntity.getBusSegment();
		inputFace = testEntity.getInputFace();
		segment.addInput(inputFace);
	}
	
	
	public void checkIfStillSucceeding(TileEntityTesting testEntity, TestTickResult testResult, boolean isSuccessPowered) {
		if (isSuccessPowered) {
			if (TileEntityTesting.isSidePowered(testEntity, inputFace.getFacing())) {
				testResult.setCurrentlySucceeding(true);
			} else{
				testResult.setCurrentlySucceeding(false);
			}
		} else {
			if (!TileEntityTesting.isSidePowered(testEntity, inputFace.getFacing())) {
				testResult.setCurrentlySucceeding(true);
			} else {
				testResult.setCurrentlySucceeding(false);
			}
		}
	}

	public void setAndOutputData(World worldIn, int index) {
		BusData testingData;
		testingData = new BusData(4, index);
		segment.accumulate(worldIn, inputFace, testingData);
		segment.forceUpdate(worldIn);
	}

	public void determineOverallSuccess(TestTickResult testResult, TileEntityTesting testEntity) {
		if (testEntity.testCounter > 4 && testResult.getCurrentlySucceeding()) {
			testEntity.testCounter = 0;
			System.out.println("Success");
			testEntity.beginTesting(false);
			testResult.setAtEndOfTest(true);
		}
		else if (testEntity.testCounter > 4) {
			testEntity.testCounter = 0;
			System.out.println("Fail");
			testEntity.beginTesting(false);
		}
	}

}
