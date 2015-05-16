package com.mhci.perfevalhw.singleton;

import com.mhci.perfevalhw.Event;
import com.mhci.perfevalhw.interfaces.EventListener;

public class StatisticsManager implements EventListener{
	
	public final static StatisticsManager instance = new StatisticsManager();
	private int totalSimulationTime;
	
	public StatisticsManager() {
		
	}
	
	@Override
	public void eventHandler(Event event) {
		
	}
	
	public void setSimulationTime(int time) {
		totalSimulationTime = time;
	}
	
	public void reset() {
		//TODO : reset all variables
	}

}
