package com.mhci.perfevalhw.BaseClass;

import java.util.HashMap;
import java.util.LinkedList;

import com.mhci.perfevalhw.Event;
import com.mhci.perfevalhw.enums.EventType;
import com.mhci.perfevalhw.interfaces.EventListener;
import com.mhci.perfevalhw.interfaces.EventNotifier;

public class BaseEventNotifier implements EventNotifier, EventListener{

	private HashMap<EventType, LinkedList<EventListener>> listenersMap = new HashMap<EventType, LinkedList<EventListener>>();
	
	public BaseEventNotifier() {
	}
	
	
	public void registerEvent(EventListener listener) {
		registerEvent(EventType.Any, listener);
	}
	
	public void unregisterEvent(EventListener listener) {
		unregisterEvent(EventType.Any, listener);
	}
	
	@Override
	public void registerEvent(EventType eventType, EventListener listener) {
		LinkedList<EventListener> listeners = listenersMap.get(eventType);
		if(listeners == null) {
			listeners = new LinkedList<EventListener>();
			listenersMap.put(eventType, listeners);
		}
		listeners.add(listener);
	}
	
	@Override
	public void unregisterEvent(EventType eventType, EventListener listener) {
		LinkedList<EventListener> listeners = listenersMap.get(eventType);
		if(listeners != null) {
			listeners.remove(listener);
		}
	}

	protected void unregisterAll() {
		EventType[] allEventTypes = EventType.values();
		for(EventType type : allEventTypes) {
			LinkedList<EventListener> listeners = listenersMap.get(type);
			if(listeners != null) {
				listeners.clear();
			}
		}
	}
	
	private void notifyWithType(EventType eventType, Event event) {
		LinkedList<EventListener> listeners = listenersMap.get(eventType);
		if(listeners != null) {
			for(EventListener listener : listeners) {
				listener.eventHandler(event);
			}
		}
	}

	@Override
	public void eventHandler(Event event) {
		notifyWithType(EventType.Any, event);
		notifyWithType(event.eventType, event);
	}
	

}
