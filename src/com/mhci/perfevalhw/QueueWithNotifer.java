package com.mhci.perfevalhw;

import java.util.concurrent.LinkedBlockingQueue;

import com.mhci.perfevalhw.BaseClass.BaseEventNotifier;
import com.mhci.perfevalhw.enums.EventType;
import com.mhci.perfevalhw.enums.UserType;
import com.mhci.perfevalhw.interfaces.EventListener;

public class QueueWithNotifer extends BaseEventNotifier{
	
	public LinkedBlockingQueue<UserInfo> mQueue;
	private UserType queueUserType;
	
	public QueueWithNotifer(UserType userType) {
		queueUserType = userType;
		mQueue = new LinkedBlockingQueue<UserInfo>();
	}
	
	public QueueWithNotifer(UserType userType, int capacity) {
		queueUserType = userType;
		mQueue = new LinkedBlockingQueue<UserInfo>(capacity);
	}

	
	@Override
	public void eventHandler(Event event) {
		if(event.eventType == EventType.Arrival && event.relatedUserInfo.mUserType == queueUserType) {
			mQueue.add(event.relatedUserInfo);
		}
		
		super.eventHandler(event);
	}
	
}
