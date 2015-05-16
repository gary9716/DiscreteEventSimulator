package com.mhci.perfevalhw.interfaces;

import com.mhci.perfevalhw.Event;
import com.mhci.perfevalhw.enums.EventType;

public interface EventNotifier {
	public void registerEvent(EventType eventType, EventListener listener);
	public void unregisterEvent(EventType eventType,EventListener listener);
	public void notifyEvent(Event event);
}
