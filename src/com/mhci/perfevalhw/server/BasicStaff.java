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
import com.mhci.perfevalhw.singleton.StatisticsManager;

public class BasicStaff extends AbstractServer{
	
	private StaffState lastState = null;
	private StaffState mState = StaffState.Idle;
	protected EventDispatcher sysEventDispatcher = EventDispatcher.instance;
	protected StatisticsManager statisticsManager = StatisticsManager.instance;
	
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
		super.eventHandler(event);
		
		if(event.eventSource == servicedUserDepartureEventGenerator) {
			setState(StaffState.Idle);
			tryToServiceAndScheduleNextDepartureEvent();
		}
		else if(getState() == StaffState.Idle && event.eventType == EventType.Arrival && event.relatedUserInfo.mUserType == UserType.PostOfficeCustomer) {
			tryToServiceAndScheduleNextDepartureEvent();
		}
		
	}
	
	@Override
	protected void tryToServiceAndScheduleNextDepartureEvent() {
		if(super.tryToService()) { //successfully service next customer
			setState(StaffState.Working);
			super.scheduleNextDepartureEvent();
		}
		else {
			setState(StaffState.Idle);
		}
	}
	
	public boolean isWorking() {
		return (mState == StaffState.Working);
	}
	
	protected StaffState getState() {
		return mState;
	}
	
	protected void setState(StaffState state) {
		lastState = mState;
		mState = state;
		if(mState == StaffState.Idle) {
			currentServicedUserInfo = null;
		}
		
		if(lastState != mState) { // state transition
			if(mState == StaffState.Working) {
				statisticsManager.currentNumWorkingStaffs++;
			}
			else if(lastState == StaffState.Working){
				statisticsManager.currentNumWorkingStaffs--;
			}
		}
		
	}
	
}
