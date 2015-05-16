
package com.mhci.perfevalhw.main;

import com.mhci.perfevalhw.BasePolicy;
import com.mhci.perfevalhw.CustomerSource;
import com.mhci.perfevalhw.FileManager;
import com.mhci.perfevalhw.PostOffice;
import com.mhci.perfevalhw.QueueWithNotifer;
import com.mhci.perfevalhw.SimulationConfig;
import com.mhci.perfevalhw.SimulationConfig.SimulationName;
import com.mhci.perfevalhw.distribution.BaseDistribution;
import com.mhci.perfevalhw.distribution.ExponentialDistribution;
import com.mhci.perfevalhw.enums.UserType;
import com.mhci.perfevalhw.server.BasicStaff;
import com.mhci.perfevalhw.server.Bonus2Staff;
import com.mhci.perfevalhw.server.Restroom;
import com.mhci.perfevalhw.singleton.EventDispatcher;
import com.mhci.perfevalhw.singleton.StatisticsManager;
import com.mhci.perfevalhw.singleton.Timer;

public class DiscreteEventSimulator {
	private BasePolicy policy = new BasePolicy();
	private QueueWithNotifer[] customerQueueWithNotifers;
	private BasicStaff[] staffs;
	private PostOffice postOffice;
	
	private Timer sharedTimer = Timer.instance;
	private EventDispatcher sysEventDispatcher = EventDispatcher.instance;
	private StatisticsManager statisticsManager = StatisticsManager.instance;
	private FileManager fileManager = null;
	
	private void initVariables() {
		sharedTimer.reset();
		sysEventDispatcher.reset();
		statisticsManager.reset();
	}
	
	private void postOfficeSetup(SimulationConfig simConfig) {
		
		int numStaffs = simConfig.numStaffs;
		BaseDistribution staffInterRestTimeDist = simConfig.getDistribution(SimulationConfig.staffInterRestTimeDistKey);
		BaseDistribution staffInterServiceTimeDist = simConfig.getDistribution(SimulationConfig.staffInterServiceTimeDistKey);
		
		Bonus2Staff[] bonus2Staffs = null;
		if(staffInterRestTimeDist != null) {
			bonus2Staffs = new Bonus2Staff[numStaffs];
		}
		else {
			staffs = new BasicStaff[numStaffs];
		}
		
		if(simConfig.customerQueueMode == SimulationConfig.CustomerQueueMode.SharedSingleQueue) {
			customerQueueWithNotifers = new QueueWithNotifer[1];
			customerQueueWithNotifers[0] = new QueueWithNotifer(UserType.PostOfficeCustomer);
			for(int i = 0;i < numStaffs;i++) {
				if(bonus2Staffs != null) {
					bonus2Staffs[i] = new Bonus2Staff(staffInterRestTimeDist, staffInterServiceTimeDist, customerQueueWithNotifers[0]);
				}
				else {
					staffs[i] = new BasicStaff(staffInterServiceTimeDist, customerQueueWithNotifers[0]);
				}
			}
		}
		else if(simConfig.customerQueueMode == SimulationConfig.CustomerQueueMode.DividualQueue){
			customerQueueWithNotifers = new QueueWithNotifer[numStaffs];
			for(int i = 0;i < numStaffs;i++) {
				customerQueueWithNotifers[i] = new QueueWithNotifer(UserType.PostOfficeCustomer);
				if(bonus2Staffs != null) {
					bonus2Staffs[i] = new Bonus2Staff(staffInterRestTimeDist, staffInterServiceTimeDist, customerQueueWithNotifers[i]);
				}
				else {
					staffs[i] = new BasicStaff(staffInterServiceTimeDist, customerQueueWithNotifers[i]);
				}
			}
		}
		
		if(bonus2Staffs != null) {
			staffs = bonus2Staffs;
		}
		
		postOffice = new PostOffice(policy, customerQueueWithNotifers);
		
	}
	
	private void doSimulationAndOutputResult(SimulationConfig simConfig) {
		
		initVariables();
		statisticsManager.setSimulationTime(simConfig.simulationTime);
		CustomerSource customerSrc = new CustomerSource(
				simConfig.getDistribution(SimulationConfig.customerInterArrivalTimeDistKey)
		);
		
		postOfficeSetup(simConfig);
		
		sysEventDispatcher.registerEvent(sharedTimer);
		sysEventDispatcher.registerEvent(customerSrc);
		sysEventDispatcher.registerEvent(postOffice);
		BaseDistribution restroomInterServiceTimeDist = simConfig.getDistribution(SimulationConfig.restroomInterServiceTimeDistKey);
		if(restroomInterServiceTimeDist != null) {
			Restroom restroom = new Restroom(restroomInterServiceTimeDist);
			sysEventDispatcher.registerEvent(restroom);
		}
		sysEventDispatcher.registerEvent(statisticsManager);
		
		System.out.println("simulation " + simConfig.simulationName + " start");
		startSimulation(simConfig.simulationTime);
		
		fileManager.outputResult(simConfig.simulationName);
		
	}
	
	private void startSimulation(int totalSimulationTime) {
		while(sharedTimer.currentTime() < totalSimulationTime) {
			sysEventDispatcher.dispatch();
		}
	}
	
	public void startExperiment() {
		String jarFileDirPath = null;
		try {
			jarFileDirPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
		}
		catch(Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		String configsDirPath = jarFileDirPath + "/inputFiles";
		String resultsDirPath = jarFileDirPath + "/outputFiles";
		FileManager fileManager = new FileManager(configsDirPath, resultsDirPath);
		doSimulationAndOutputResult(fileManager.getSimulationConfig(SimulationName.Basic));
		doSimulationAndOutputResult(fileManager.getSimulationConfig(SimulationName.Bonus1));
		doSimulationAndOutputResult(fileManager.getSimulationConfig(SimulationName.Bonus2));
		
	}
	
	public static void main(String[] args) {
		// TODO 3 simulations, IO
		(new DiscreteEventSimulator()).startExperiment();
	}

}
