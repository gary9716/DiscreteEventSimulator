package com.mhci.perfevalhw;

import com.mhci.perfevalhw.enums.EventType;
import com.mhci.perfevalhw.enums.UserType;
import com.mhci.perfevalhw.interfaces.EventListener;

public class PostOffice implements EventListener{

	private QueueWithNotifer[] mSysQueues;
	private Policy mPolicy;
	
	//TODO : pass a Policy object into this service provider
	public PostOffice(Policy policy, QueueWithNotifer[] queues) {
		mSysQueues = queues;
		mPolicy = policy;
	}

	@Override
	public void eventHandler(Event event) {
		if(event.eventType == EventType.Arrival && event.relatedUserInfo.mUserType == UserType.PostOfficeCustomer) { //selective notify
			mPolicy.decide(mSysQueues).notifyEvent(event);
		}
		else { //broadcast
			broadcast(event);
		}
	}
	
	private void broadcast(Event event) {
		for(QueueWithNotifer queue : mSysQueues) {
			queue.notifyEvent(event);
		}
	}
	
}
