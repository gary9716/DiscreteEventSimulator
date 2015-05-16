package com.mhci.perfevalhw.interfaces;

import com.mhci.perfevalhw.Event;

public interface ConditionChecker {
	
	//when an event is going to happen, the generator needs to make sure it fulfills all conditions.
	public boolean eventCanHappen(Event event);  

	
}
