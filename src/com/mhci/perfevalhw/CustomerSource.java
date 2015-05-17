package com.mhci.perfevalhw;

import com.mhci.perfevalhw.BaseClass.BaseEventGenerator;
import com.mhci.perfevalhw.distribution.BaseDistribution;
import com.mhci.perfevalhw.distribution.ExponentialDistribution;
import com.mhci.perfevalhw.enums.EventType;
import com.mhci.perfevalhw.enums.UserType;
import com.mhci.perfevalhw.interfaces.EventListener;
import com.mhci.perfevalhw.singleton.EventDispatcher;
import com.mhci.perfevalhw.singleton.Timer;

public class CustomerSource implements EventListener {

	private BaseEventGenerator customerArrivalEventGenerator;
	private Timer sharedTimer = Timer.instance;
	private EventDispatcher sysEventDispatcher = EventDispatcher.instance;
	
	public CustomerSource(float arrivalRate) {
		customerArrivalEventGenerator = new BaseEventGenerator(EventType.Arrival, new ExponentialDistribution(arrivalRate));
	}
	
	public CustomerSource(BaseDistribution timeDist) {
		customerArrivalEventGenerator = new BaseEventGenerator(EventType.Arrival, timeDist);
	}
	
	@Override
	public void eventHandler(Event event) {
		if(event.eventType == EventType.Arrival && event.eventSource == customerArrivalEventGenerator) {
			genEventAndScheduleIt();
		}
		else if(event.eventType == EventType.GenerateArrival) {
			genEventAndScheduleIt();
		}
		
	}

	private void genEventAndScheduleIt() {
		Event event = customerArrivalEventGenerator.generateEventWithDistribution(sharedTimer.currentTime());
		event.relatedUserInfo = new UserInfo(UserType.PostOfficeCustomer);
		event.relatedUserInfo.setEvent(event);
		
		sysEventDispatcher.schedule(event);
	}
	
}
