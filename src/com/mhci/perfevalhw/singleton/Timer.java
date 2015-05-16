package com.mhci.perfevalhw.singleton;

import com.mhci.perfevalhw.Event;
import com.mhci.perfevalhw.interfaces.EventListener;

public class Timer implements EventListener{
	private float timeVal;
	public final static Timer instance = new Timer();
	
	public Timer() {
		timeVal = 0;
	}
	
	public float currentTime() {
		return timeVal;
	}

	public void makeTimeMove(float amountTime) {
		timeVal += amountTime;
	}
	
	public void setTime(float time) {
		timeVal = time;
	}
	
	public void reset() {
		timeVal = 0;
	}

	@Override
	public void eventHandler(Event event) {
		setTime(event.eventTime);
	}
	
}
