package com.mhci.perfevalhw.server;

import java.util.concurrent.LinkedBlockingQueue;

import com.mhci.perfevalhw.Event;
import com.mhci.perfevalhw.UserInfo;
import com.mhci.perfevalhw.BaseClass.BaseEventGenerator;
import com.mhci.perfevalhw.distribution.ExponentialDistribution;
import com.mhci.perfevalhw.distribution.PositiveNormalDistribution;
import com.mhci.perfevalhw.enums.EventType;
import com.mhci.perfevalhw.enums.StaffState;
import com.mhci.perfevalhw.enums.UserType;
import com.mhci.perfevalhw.interfaces.ConditionChecker;

public class Bonus2Staff extends BasicStaff implements ConditionChecker{
	
	//Go to Restroom Event Generator
	private BaseEventGenerator staffArrivalEventGenerator;
	
	public Bonus2Staff(float interRestRate, float serviceMean, float serviceVariance, LinkedBlockingQueue<UserInfo> queue) {
		super(new PositiveNormalDistribution(serviceMean, serviceVariance), queue);
		staffArrivalEventGenerator = new BaseEventGenerator(EventType.Arrival, new ExponentialDistribution(interRestRate));
		
	}
	
	private void genGoToRestroomEventAndScheduleIt() {
		Event event = staffArrivalEventGenerator.generateEventWithDistribution(sharedTimer.currentTime());
		event.relatedUserInfo = new UserInfo(UserType.RestroomUser);
		event.relatedUserInfo.userObj = this;
		event.setConditionChecker(this);
		
		sysEventDispatcher.schedule(event);
	}
	
	@Override
	public void eventHandler(Event event) {
		System.out.println("eventHandler in Bonus2 Staff");
		//Staff leave the restroom
		if(event.relatedUserInfo.userObj == this && event.eventType == EventType.Departure) {
			setState(StaffState.Idle);
			genGoToRestroomEventAndScheduleIt();
			tryToServiceAndScheduleNextDepartureEvent();
		}
		
		if(getState() == StaffState.Absent)  { //has gone to restroom
			return;
		}
		
		//TODO : use genArrivalEventToGenerate Initial Arrival Event
		
		super.eventHandler(event);
	}
	
	@Override
	protected void tryToServiceAndScheduleNextDepartureEvent() {
		System.out.println("tryToServiceAndScheduleNextDepartureEvent in Bonus2 Staff");
		Event failedEvent = staffArrivalEventGenerator.getAFailureEvent();
		if(failedEvent != null) { 
			failedEvent.eventTime = sharedTimer.currentTime();
			sysEventDispatcher.schedule(failedEvent);
			setState(StaffState.Absent); //go to restroom immediately
		}
		else {
			super.tryToServiceAndScheduleNextDepartureEvent();
		}
	}

	@Override
	public boolean eventCanHappen(Event event) {
		//if staff is servicing a customer, he/she cannot go to restroom
		if(event.eventSource == staffArrivalEventGenerator && getState() == StaffState.Working) { 
			return false;
		}
		
		return true;
	}

}
