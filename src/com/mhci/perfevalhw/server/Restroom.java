package com.mhci.perfevalhw.server;

import java.util.concurrent.LinkedBlockingQueue;

import com.mhci.perfevalhw.Event;
import com.mhci.perfevalhw.QueueWithNotifer;
import com.mhci.perfevalhw.UserInfo;
import com.mhci.perfevalhw.BaseClass.BaseEventGenerator;
import com.mhci.perfevalhw.distribution.BaseDistribution;
import com.mhci.perfevalhw.distribution.ExponentialDistribution;
import com.mhci.perfevalhw.enums.EventType;
import com.mhci.perfevalhw.enums.UserType;

public class Restroom extends AbstractServer {
	
	public Restroom(float serviceRate) {
		super(new LinkedBlockingQueue<UserInfo>());
		servicedUserDepartureEventGenerator = new BaseEventGenerator(EventType.Departure, new ExponentialDistribution(serviceRate));
	}
	
	public Restroom(BaseDistribution serviceTimeDist) {
		super(new LinkedBlockingQueue<UserInfo>());
		servicedUserDepartureEventGenerator = new BaseEventGenerator(EventType.Departure, serviceTimeDist);
	}
	
	@Override
	public void eventHandler(Event event) {
		if(currentServicedUserInfo == null && event.eventType == EventType.Arrival && event.relatedUserInfo.mUserType == UserType.RestroomUser) {
			super.tryToServiceAndScheduleNextDepartureEvent();
		}
		else if(event.eventType == EventType.Departure && event.eventSource == servicedUserDepartureEventGenerator) {
			super.tryToServiceAndScheduleNextDepartureEvent();
		}
	}

}
