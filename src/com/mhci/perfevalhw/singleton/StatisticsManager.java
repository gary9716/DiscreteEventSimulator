package com.mhci.perfevalhw.singleton;

import java.util.concurrent.LinkedBlockingQueue;

import com.mhci.perfevalhw.Event;
import com.mhci.perfevalhw.UserInfo;
import com.mhci.perfevalhw.enums.EventType;
import com.mhci.perfevalhw.enums.UserType;
import com.mhci.perfevalhw.interfaces.EventListener;
import com.mhci.perfevalhw.server.BasicStaff;

public class StatisticsManager implements EventListener{
	
	public final static StatisticsManager instance = new StatisticsManager();
	public int currentNumWorkingStaffs = 0;
	public int numDeparturedPostOfficeCustomers = 0;
	//When EndSimulation event happen, all server would dequeue rest of userInfos To this queue.
	public LinkedBlockingQueue<UserInfo> endSimUserQueue = new LinkedBlockingQueue<UserInfo>(); 
	
	private int totalSimulationTime = 0;
	private int totalNumStaffs = 0;
	private int lastNumWorkingStaffs = 0;
	private float lastEventTime = 0;
	
	//statistics info
	private float[] timeLen = null; //time taken in different number of staff state.
	private int[] numArrivedUsers = null; //number of users who have been to a server
	private float[] totalWaitingTime = null;
	private float totalPostOfficeCustomersSystemTime = 0;
	
	private StatisticsManager() {
	}
	
	public void reset(SimulationConfig config) {
		totalSimulationTime = config.simulationTime;
		totalNumStaffs = config.numStaffs;
		lastNumWorkingStaffs = 0;
		lastEventTime = 0;
		timeLen = new float[totalNumStaffs + 1];
		for(int i = 0;i <= totalNumStaffs;i++) {
			timeLen[i] = 0;
		}
		numArrivedUsers = new int[UserType.NumUserTypes.ordinal()];
		totalWaitingTime = new float[UserType.NumUserTypes.ordinal()];
		for(int i = 0;i < UserType.NumUserTypes.ordinal();i++) {
			totalWaitingTime[i] = 0;
			numArrivedUsers[i] = 0;
		}
		totalPostOfficeCustomersSystemTime = 0;
		
		currentNumWorkingStaffs = 0;
		numDeparturedPostOfficeCustomers = 0;
		endSimUserQueue.clear();
	}
	
	private int numWorkingStaffsNow() {
		return currentNumWorkingStaffs;
	}
	
	@Override
	public void eventHandler(Event event) {
		UserInfo userInfo = event.relatedUserInfo;
		EventType eventType = event.eventType;
		timeLen[lastNumWorkingStaffs] += (event.eventTime - lastEventTime);
		
		if(eventType == EventType.Arrival) {
			numArrivedUsers[userInfo.mUserType.ordinal()]++;
		}
		else if(eventType == EventType.StartServiced) {
			totalWaitingTime[userInfo.mUserType.ordinal()] += (event.eventTime - userInfo.getEvent(EventType.Arrival).eventTime);
		}
		else if(eventType == EventType.Departure && userInfo.mUserType == UserType.PostOfficeCustomer) {
			numDeparturedPostOfficeCustomers++;
			totalPostOfficeCustomersSystemTime += (event.eventTime - userInfo.getEvent(EventType.Arrival).eventTime);
		}
		else if(eventType == EventType.EndSimulation) {
			//Get all users that haven't been serviced at the end of simulation time
			while((userInfo = endSimUserQueue.poll()) != null) {
				totalWaitingTime[userInfo.mUserType.ordinal()] += (event.eventTime - userInfo.getEvent(EventType.Arrival).eventTime);
			}
		}
		
		lastNumWorkingStaffs = numWorkingStaffsNow();
		lastEventTime = event.eventTime;
	}
	
	//calculate metrics
	
	public float averageWaitingTime(UserType userType) {
		if(numArrivedUsers[userType.ordinal()] != 0) {
//			if(userType == UserType.PostOfficeCustomer) {
//				System.out.println("arrival:" + numArrivedUsers[userType.ordinal()]);
//				System.out.println("totalWaiting:" + totalWaitingTime[userType.ordinal()]);
//				System.out.println("departure:" + numDeparturedPostOfficeCustomers);
//				System.out.println("totalSystem:" + totalPostOfficeCustomersSystemTime);
//			}
			return totalWaitingTime[userType.ordinal()] / numArrivedUsers[userType.ordinal()];
		}
		else {
			return 0;
		}
	}
	
	public float averageSystemTime() {
		return totalPostOfficeCustomersSystemTime / numDeparturedPostOfficeCustomers;
	}
	
	public float utilizationRatio(int numStaffs) {
		if(numStaffs <= totalNumStaffs) {
			return timeLen[numStaffs] / totalSimulationTime;
		}
		else {
			return 0;
		}
	}
	
}
