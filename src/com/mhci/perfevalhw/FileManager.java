package com.mhci.perfevalhw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

import com.mhci.perfevalhw.distribution.ExponentialDistribution;
import com.mhci.perfevalhw.distribution.PositiveNormalDistribution;
import com.mhci.perfevalhw.enums.UserType;
import com.mhci.perfevalhw.singleton.SimulationConfig;
import com.mhci.perfevalhw.singleton.StatisticsManager;
import com.mhci.perfevalhw.singleton.SimulationConfig.SimulationName;

public class FileManager {
	
	private final static String delimeter = " ";
	private final static String configFileName = "input.txt";
	private final static String resultFileName = "output.txt";
	
	private File configFilesDir;
	private File resultFilesDir;
	private SimulationConfig simConfig = SimulationConfig.instance;
	private StatisticsManager statisticsManager = StatisticsManager.instance;
	
	public FileManager(String configsPath, String resultsPath) {
		configFilesDir = new File(configsPath);
		checkValidPathOrNot(configFilesDir);
		resultFilesDir = new File(resultsPath);
		checkValidPathOrNot(resultFilesDir);
		
	}
	
	public void checkValidPathOrNot(File file) {
		if(!file.exists()) {
			file.mkdirs();
		}
		else if(!file.isDirectory()) {
			System.out.println("given directory path refer to non-directory");
			System.exit(-1);
		}
		else {
			if(file == configFilesDir && !file.canExecute()) {
				System.out.println("cannot read file from given directory path");
				System.exit(-1);
			}
			else if(file == resultFilesDir && !file.canWrite()) {
				System.out.println("cannot write file to given directory path");
				System.exit(-1);
			}
			
		}
	}
	
	public void outputResult(SimulationName simName) {
		File file = new File(resultFilesDir, simName.toString() + "_" + resultFileName);
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(file);
			
			writer.println("Average customer waiting time: " + 
								statisticsManager.averageWaitingTime(UserType.PostOfficeCustomer)
			);
			
			writer.println("Average system time: " + 
								statisticsManager.averageSystemTime()
			);
			
			if(simName == SimulationName.Bonus2) {
				writer.println("Average staff waiting time: " + 
						statisticsManager.averageWaitingTime(UserType.RestroomUser)
				);
			}
			
			writer.println("System utilization ratio: " + 
					(1 - statisticsManager.utilizationRatio(0))
			);
			
			writer.println("Full utilization ratio: " + 
					statisticsManager.utilizationRatio(simConfig.numStaffs)
			);
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				writer.close();
			}
			catch(Exception e) {}
		}
		
		return;
	}
	
	public SimulationConfig getSimulationConfig(SimulationName simName) {
		simConfig.reset();
		simConfig.simulationName = simName;
		String[] data = readFromFileAndParse(simName.toString() + "_" + configFileName);
		
		try {
			if(simName == SimulationName.Basic || simName == SimulationName.Bonus1) {
				simConfig.setDistribution(
						SimulationConfig.customerInterArrivalTimeDistKey, 
						new ExponentialDistribution(Float.valueOf(data[0]))
				);
				
				simConfig.setDistribution(
						SimulationConfig.staffInterServiceTimeDistKey, 
						new ExponentialDistribution(Float.valueOf(data[1]))
				);
				simConfig.simulationTime = Integer.parseInt(data[2]);
			}
			else if(simName == SimulationName.Bonus2) {
				simConfig.setDistribution(
						SimulationConfig.customerInterArrivalTimeDistKey, 
						new PositiveNormalDistribution(Float.valueOf(data[0]), Float.valueOf(data[1]))
				);
				
				simConfig.setDistribution(
						SimulationConfig.staffInterServiceTimeDistKey, 
						new PositiveNormalDistribution(Float.valueOf(data[2]), Float.valueOf(data[3]))
				);
				simConfig.simulationTime = Integer.parseInt(data[4]);
				simConfig.setDistribution(
						SimulationConfig.staffInterRestTimeDistKey, 
						new ExponentialDistribution(1 / Float.valueOf(data[5]))
				);
				simConfig.setDistribution(
						SimulationConfig.restroomInterServiceTimeDistKey, 
						new ExponentialDistribution(Float.valueOf(data[6]))
				);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return simConfig;
	}
	
	private String[] readFromFileAndParse(String fileName) {
		File file = new File(configFilesDir, fileName);
		BufferedReader reader = null;
		String[] data = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			data = reader.readLine().split(delimeter);
		}
		catch(Exception e) {
			data = null;
			e.printStackTrace();
		}
		finally {
			try {
				reader.close();
			}
			catch(Exception e) { }
		}
		
		return data;
	}
	
}
