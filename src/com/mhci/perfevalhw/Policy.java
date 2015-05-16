package com.mhci.perfevalhw;

public interface Policy {
	public QueueWithNotifer decide(QueueWithNotifer[] queues); //Selecting Queue For Arriving User Method
}
