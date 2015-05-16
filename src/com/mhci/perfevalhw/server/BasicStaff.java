package com.mhci.perfevalhw.server;

import com.mhci.perfevalhw.Event;
import com.mhci.perfevalhw.QueueWithNotifer;
import com.mhci.perfevalhw.BaseClass.BaseEventGenerator;
import com.mhci.perfevalhw.distribution.BaseDistribution;
import com.mhci.perfevalhw.distribution.ExponentialDistribution;
import com.mhci.perfevalhw.enums.EventType;
import com.mhci.perfevalhw.enums.StaffState;
import com.mhci.perfevalhw.enums.UserType;
import com.mhci.perfevalhw.singleton.EventDispatcher;

public class BasicStaff extends AbstractServer{
	
	private StaffState mState = StaffState.Idle;
	protected EventDispatcher sysEventDispatcher = EventDispatcher.instance;
	
	public BasicStaff(float serviceRate, QueueWithNotifer queue) {
		super(queue);
		setState(StaffState.Idle);
		servicedUserDepartureEventGenerator = new BaseEventGenerator(EventType.Departure, new ExponentialDistribution(serviceRate));
	}
	
	public BasicStaff(BaseDistribution serviceTimeDist, QueueWithNotifer queue) {
		super(queue);
		setState(StaffState.Idle);
		servicedUserDepartureEventGenerator = new BaseEventGenerator(EventType.Departure, serviceTimeDist);
	}
	
	@Override
	public void eventHandler(Event event) {
		System.out.println("eventHandler in Basic Staff");
		if(event.eventSource == servicedUserDepartureEventGenerator) {
			tryToServiceAndScheduleNextDepartureEvent();
		}
		else if(getState() == StaffState.Idle && event.eventType == EventType.Arrival && event.relatedUserInfo.mUserType == UserType.PostOfficeCustomer) {
			tryToServiceAndScheduleNextDepartureEvent();
		}
		
	}
	
	@Override
	protected void tryToServiceAndScheduleNextDepartureEvent() {
		System.out.println("tryToServiceAndScheduleNextDepartureEvent in Basic Staff");
		if(super.tryToService()) { //successfully service next customer
			setState(StaffState.Working);
			super.scheduleNextDepartureEvent();
		}
		else {
			setState(StaffState.Idle);
		}
	}
	
	protected StaffState getState() {
		return mState;
	}
	
	protected void setState(StaffState state) {
		mState = state;
		if(mState == StaffState.Idle) {
			currentServicedUserInfo = null;
		}
	}
	
}
