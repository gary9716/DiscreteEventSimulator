package com.mhci.perfevalhw.singleton;

import java.util.Comparator;
import java.util.PriorityQueue;

import com.mhci.perfevalhw.Event;
import com.mhci.perfevalhw.BaseClass.BaseEventNotifier;

public class EventDispatcher extends BaseEventNotifier implements Comparator<Event> {
	public final static EventDispatcher instance  = new EventDispatcher();
	
	private PriorityQueue<Event> eventQueue = new PriorityQueue<Event>(0, this);
	public EventDispatcher() {
	}
	
	public void schedule(Event event) {
		try {
			eventQueue.add(event);
		}
		catch(IllegalStateException e) {
			System.out.println("event queue cannot accept more event");
		}
	}
	
	public void dispatch() {
		if(!eventQueue.isEmpty()) {
			Event event = eventQueue.remove();
			if(event.isEventConditionFulfilled()) {
				notifyEvent(event);
			}
		}
	}
	
	public void reset() {
		eventQueue.clear();
		unregisterAll();
	}
	
	@Override
	public int compare(Event e1, Event e2) {
		if(e1.eventTime < e2.eventTime ) { //e1 should be dequeued first. => e1 should be closer to head than e2
			return -1;
		}
		else {
			return 1;
		}
	}
	
}
