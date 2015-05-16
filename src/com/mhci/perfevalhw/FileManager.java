package com.mhci.perfevalhw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

import com.mhci.perfevalhw.SimulationConfig.SimulationName;
import com.mhci.perfevalhw.distribution.ExponentialDistribution;
import com.mhci.perfevalhw.distribution.PositiveNormalDistribution;
import com.mhci.perfevalhw.singleton.StatisticsManager;

public class FileManager {
	
	private final static String delimeter = " ";
	private final static String configFileName = "input.txt";
	private final static String resultFileName = "output.txt";
	
	private StatisticsManager statisticsManager = StatisticsManager.instance;
	private File configFilesDir;
	private File resultFilesDir;
	
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
			//TODO : get data from statistics manager
			
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
		SimulationConfig config = new SimulationConfig();
		config.simulationName = simName;
		String[] data = readFromFileAndParse(simName.toString() + "_" + configFileName);
		
		try {
			if(simName == SimulationName.Basic || simName == SimulationName.Bonus1) {
				config.setDistribution(
						SimulationConfig.customerInterArrivalTimeDistKey, 
						new ExponentialDistribution(Float.valueOf(data[0]))
				);
				
				config.setDistribution(
						SimulationConfig.staffInterServiceTimeDistKey, 
						new ExponentialDistribution(Float.valueOf(data[1]))
				);
				config.simulationTime = Integer.parseInt(data[2]);
			}
			else if(simName == SimulationName.Bonus2) {
				config.setDistribution(
						SimulationConfig.customerInterArrivalTimeDistKey, 
						new PositiveNormalDistribution(Float.valueOf(data[0]), Float.valueOf(data[1]))
				);
				
				config.setDistribution(
						SimulationConfig.staffInterServiceTimeDistKey, 
						new PositiveNormalDistribution(Float.valueOf(data[2]), Float.valueOf(data[3]))
				);
				config.simulationTime = Integer.parseInt(data[4]);
				config.setDistribution(
						SimulationConfig.staffInterRestTimeDistKey, 
						new ExponentialDistribution(Float.valueOf(data[5]))
				);
				config.setDistribution(
						SimulationConfig.restroomInterServiceTimeDistKey, 
						new ExponentialDistribution(Float.valueOf(data[6]))
				);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return config;
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
