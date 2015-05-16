package com.mhci.perfevalhw;

import java.util.concurrent.LinkedBlockingQueue;

public class BasePolicy implements Policy {

	@Override
	public QueueWithNotifer decide(QueueWithNotifer[] queues) {
		if(queues.length == 1) {
			return queues[0];
		}
		else { 
			//basic Multiple Queues Selecting Strategy
			int LessCustomerQueueIndex = 0;
			int minNumCustomer = queues[0].mQueue.size();
			for(int i = 1;i < queues.length;i++) {
				int numCustomer = queues[i].mQueue.size();
				if(numCustomer < minNumCustomer) {
					minNumCustomer = numCustomer;
					LessCustomerQueueIndex = i;
				}
			}
			return queues[LessCustomerQueueIndex];
		}
		
	}

}
