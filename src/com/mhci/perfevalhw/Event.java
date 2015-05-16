package com.mhci.perfevalhw;

import com.mhci.perfevalhw.enums.EventType;
import com.mhci.perfevalhw.interfaces.ConditionChecker;
import com.mhci.perfevalhw.interfaces.EventFailureHandler;
import com.mhci.perfevalhw.interfaces.EventGenerator;

public class Event {	
	public EventType eventType;
	public float eventTime;
	public EventGenerator eventSource = null;
	public UserInfo relatedUserInfo = null; //for StatisticsManager, it could use this calculate info.
	
	private ConditionChecker relatedConditionChecker = null;
	//for more flexible design, somebody can use HashMap<String, Object> otherRelatedInfo
	
	public void setConditionChecker(ConditionChecker checker) {
		relatedConditionChecker = checker;
	}
	
	//when an event is going to happen, it needs to check for the necessary conditions
	public boolean isEventConditionFulfilled() {
		if(relatedConditionChecker != null) {
			boolean eventResult = relatedConditionChecker.eventCanHappen(this);
			if(!eventResult && eventSource != null) {
				eventSource.eventFailureHandler(this);
			}
			return eventResult;
		}
		else {
			return true;
		}
	}
	
	public Event(EventType type, float time) {
		eventType = type;
		eventTime = time;
	}
	
}
