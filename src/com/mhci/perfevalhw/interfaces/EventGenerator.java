package com.mhci.perfevalhw.interfaces;

import com.mhci.perfevalhw.Event;

public interface EventGenerator {
	
	//public void setAvailability(boolean isAvailable);
	//public boolean isAvailable();
	public Event generateEventWithDistribution(float referenceTime);
	public Event generateEventDeterministic(float eventTime);
	public void eventFailureHandler(Event event);
	public Event getAFailureEvent();
}
