package com.mhci.perfevalhw.server;

import java.util.concurrent.LinkedBlockingQueue;

import com.mhci.perfevalhw.Event;
import com.mhci.perfevalhw.QueueWithNotifer;
import com.mhci.perfevalhw.UserInfo;
import com.mhci.perfevalhw.BaseClass.BaseEventGenerator;
import com.mhci.perfevalhw.enums.EventType;
import com.mhci.perfevalhw.interfaces.EventListener;
import com.mhci.perfevalhw.singleton.EventDispatcher;
import com.mhci.perfevalhw.singleton.StatisticsManager;
import com.mhci.perfevalhw.singleton.Timer;

public abstract class AbstractServer implements EventListener{
	protected UserInfo currentServicedUserInfo = null;
	protected BaseEventGenerator servicedUserDepartureEventGenerator = null;
	protected EventDispatcher sysEventDispatcher = EventDispatcher.instance;
	protected Timer sharedTimer = Timer.instance;
	protected StatisticsManager statisticsManager = StatisticsManager.instance;
	protected LinkedBlockingQueue<UserInfo> mQueue;
	
	public AbstractServer(QueueWithNotifer queueWithNotifer) {
		mQueue = queueWithNotifer.mQueue;
		queueWithNotifer.registerEvent(this); //register to all event passed to Queue
	}
	
	public AbstractServer(LinkedBlockingQueue<UserInfo> queue) {
		mQueue = queue;
	}
	
	protected UserInfo dequeue() {
		return mQueue.poll();
	}
	
	@Override
	public void eventHandler(Event event) {
		if(event.eventType == EventType.EndSimulation) {
			UserInfo userInfo = null;
			while((userInfo = dequeue()) != null) {
				statisticsManager.endSimUserQueue.offer(userInfo);
			}
		}
	}
	
	protected boolean tryToService() {
		currentServicedUserInfo = dequeue();
		if(currentServicedUserInfo != null) { //successfully service a customer
			Event event = new Event(EventType.StartServiced, sharedTimer.currentTime());
			event.relatedUserInfo = currentServicedUserInfo;
			currentServicedUserInfo.setEvent(event);
			
			sysEventDispatcher.schedule(event);
			return true; // successfully service next customer
		}
		
		return false;
	}
	
	protected void tryToServiceAndScheduleNextDepartureEvent() {
		if(tryToService()) {
			scheduleNextDepartureEvent();
		}
	}

	protected void scheduleNextDepartureEvent() {
		Event event = servicedUserDepartureEventGenerator.generateEventWithDistribution(sharedTimer.currentTime());
		event.relatedUserInfo = currentServicedUserInfo;
		currentServicedUserInfo.setEvent(event);
		sysEventDispatcher.schedule(event);
	}
	
}
