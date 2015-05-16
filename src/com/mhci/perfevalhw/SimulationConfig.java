package com.mhci.perfevalhw;

import java.util.HashMap;

import com.mhci.perfevalhw.distribution.BaseDistribution;

public class SimulationConfig {
	public enum CustomerQueueMode {
		SharedSingleQueue,
		DividualQueue
	}
	
	public enum SimulationName {
		Basic,
		Bonus1,
		Bonus2
	}
	
	private HashMap<String, BaseDistribution> distributions = new HashMap<String, BaseDistribution>();
	
	public final static String staffInterServiceTimeDistKey = "staff.interServiceTime";
	public final static String staffInterRestTimeDistKey = "staff.interRestTime";
	public final static String customerInterArrivalTimeDistKey = "customer.interArrivalTime";
	public final static String restroomInterServiceTimeDistKey = "restroom.interServiceTime";
	
	public SimulationName simulationName;
	public CustomerQueueMode customerQueueMode;
	public int numStaffs;
	public int simulationTime;
	
	public void setDistribution(String distKey, BaseDistribution dist) {
		distributions.put(distKey, dist);
	}
	
	public BaseDistribution getDistribution(String distKey) {
		return distributions.get(distKey);
	}
	
	public void reset() {
		distributions.clear();
		simulationName = null;
		customerQueueMode = null;
		numStaffs = 0;
		simulationTime = 0;
	}
	
	public SimulationConfig() {
		reset();
	}
	
}
