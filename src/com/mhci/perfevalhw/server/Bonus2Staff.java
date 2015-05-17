package com.mhci.perfevalhw.server;

import com.mhci.perfevalhw.Event;
import com.mhci.perfevalhw.QueueWithNotifer;
import com.mhci.perfevalhw.UserInfo;
import com.mhci.perfevalhw.BaseClass.BaseEventGenerator;
import com.mhci.perfevalhw.distribution.BaseDistribution;
import com.mhci.perfevalhw.distribution.ExponentialDistribution;
import com.mhci.perfevalhw.distribution.PositiveNormalDistribution;
import com.mhci.perfevalhw.enums.EventType;
import com.mhci.perfevalhw.enums.StaffState;
import com.mhci.perfevalhw.enums.UserType;
import com.mhci.perfevalhw.interfaces.ConditionChecker;

public class Bonus2Staff extends BasicStaff implements ConditionChecker{
	
	private BaseEventGenerator goRestroomEventGenerator;
	
	public Bonus2Staff(float interRestRate, float serviceMean, float serviceVariance, QueueWithNotifer queue) {
		super(new PositiveNormalDistribution(serviceMean, serviceVariance), queue);
		goRestroomEventGenerator = new BaseEventGenerator(EventType.Arrival, new ExponentialDistribution(interRestRate));
	}
	
	public Bonus2Staff(BaseDistribution interRestTimeDist, BaseDistribution serviceTimeDist, QueueWithNotifer queue) {
		super(serviceTimeDist, queue);
		goRestroomEventGenerator = new BaseEventGenerator(EventType.Arrival, interRestTimeDist);
	}
	
	private void genGoToRestroomEventAndScheduleIt() {
		Event event = goRestroomEventGenerator.generateEventWithDistribution(sharedTimer.currentTime());
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
		
		if(event.eventType == EventType.GenerateArrival) {
			genGoToRestroomEventAndScheduleIt();
		}
		
		super.eventHandler(event);
	}
	
	@Override
	protected void tryToServiceAndScheduleNextDepartureEvent() {
		System.out.println("tryToServiceAndScheduleNextDepartureEvent in Bonus2 Staff");
		Event failedEvent = goRestroomEventGenerator.getAFailureEvent();
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
		if(event.eventSource == goRestroomEventGenerator && getState() == StaffState.Working) { 
			return false;
		}
		
		return true;
	}

}
