
package com.mhci.perfevalhw.main;

import com.mhci.perfevalhw.BasePolicy;
import com.mhci.perfevalhw.CustomerSource;
import com.mhci.perfevalhw.PostOffice;
import com.mhci.perfevalhw.QueueWithNotifer;
import com.mhci.perfevalhw.enums.UserType;
import com.mhci.perfevalhw.server.BasicStaff;
import com.mhci.perfevalhw.singleton.EventDispatcher;
import com.mhci.perfevalhw.singleton.StatisticsManager;
import com.mhci.perfevalhw.singleton.Timer;

public class DiscreteTimeSimulator {
	private BasePolicy policy = new BasePolicy();
	private QueueWithNotifer[] sysQueueWithNotifers;
	private Timer sharedTimer = Timer.instance;
	private EventDispatcher sysEventDispatcher = EventDispatcher.instance;
	private StatisticsManager statisticsManager = StatisticsManager.instance;
	
	private void basicSimulation(float serviceRate, float arrivalRate, int simulationTime) {
		//read input files to set parameters
		
		initVariables();
		
		BasicStaff[] staffs = new BasicStaff[2];
		sysQueueWithNotifers = new QueueWithNotifer[1];
		sysQueueWithNotifers[0] = new QueueWithNotifer(UserType.PostOfficeCustomer);
		
		PostOffice postOffice = new PostOffice(policy, sysQueueWithNotifers);
		staffs[0] = new BasicStaff(serviceRate, sysQueueWithNotifers[0].mQueue);
		staffs[1] = new BasicStaff(serviceRate, sysQueueWithNotifers[0].mQueue);
		
		sysQueueWithNotifers[0].registerEvent(staffs[0]); //register to all events
		sysQueueWithNotifers[0].registerEvent(staffs[1]);
		
		CustomerSource customerSrc = new CustomerSource(arrivalRate);
		
		//register first level event listeners with EventDispatcher
		sysEventDispatcher.registerEvent(sharedTimer);
		sysEventDispatcher.registerEvent(customerSrc);
		sysEventDispatcher.registerEvent(postOffice);
		sysEventDispatcher.registerEvent(statisticsManager);
		
		statisticsManager.setSimulationTime(simulationTime);
		
		doSimulation(simulationTime);
	}
	
	private void bonus1Simulation() {
		
	}
	
	private void bonus2Simulation() {
		
	}
	
	private void initVariables() {
		sharedTimer.reset();
		sysEventDispatcher.reset();
		statisticsManager.reset();
	}
	
	private void doSimulation(int totalSimulationTime) {
		while(sharedTimer.currentTime() < totalSimulationTime) {
			sysEventDispatcher.dispatch();
		}
	}
	
	public static void main(String[] args) {
		// TODO 3 simulations, IO

	}

}
