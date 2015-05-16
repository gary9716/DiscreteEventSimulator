package com.mhci.perfevalhw;

import java.util.HashMap;

import com.mhci.perfevalhw.enums.EventType;
import com.mhci.perfevalhw.enums.UserType;

public class UserInfo {
	//TODO: maybe we'll need the type var here, and id var for debugging
	public final UserType mUserType;
	public Object userObj = null;
	private HashMap<EventType, Event> events = new HashMap<EventType, Event>();
	
	public void setEvent(Event event) {
		events.put(event.eventType, event);
	}
	
	public Event getEvent(EventType type) {
		return events.get(type);
	}
	
	public UserInfo(UserType userType) {
		mUserType = userType;
	}
	
}
