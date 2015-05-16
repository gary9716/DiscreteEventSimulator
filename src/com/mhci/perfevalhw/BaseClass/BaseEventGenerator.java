package com.mhci.perfevalhw.BaseClass;

import java.util.LinkedList;

import com.mhci.perfevalhw.Event;
import com.mhci.perfevalhw.distribution.BaseDistribution;
import com.mhci.perfevalhw.enums.EventType;
import com.mhci.perfevalhw.interfaces.EventGenerator;

public class BaseEventGenerator implements EventGenerator{
	protected BaseDistribution mTimeDist = null;
	protected EventType mGenEventType;
	protected LinkedList<Event> failedEvents = new LinkedList<Event>();
	
	public BaseEventGenerator(EventType genEventType, BaseDistribution dist) {
		mGenEventType = genEventType;
		mTimeDist = dist;
	}
	
	@Override
	public Event generateEventWithDistribution(float referenceTime) {
		Event event = new Event(mGenEventType, referenceTime + mTimeDist.drawASample(null));
		event.eventSource = this;
		return event;
	}

	@Override
	public Event generateEventDeterministic(float eventTime) {
		Event event = new Event(mGenEventType, eventTime);
		event.eventSource = this;
		return event;
	}
	
	@Override
	public void eventFailureHandler(Event event) {
		failedEvents.add(event);
	}
	
	@Override
	public Event getAFailureEvent() {
		if(failedEvents.isEmpty()) {
			return null;
		}
		else {
			return failedEvents.remove();
		}
	}
}
