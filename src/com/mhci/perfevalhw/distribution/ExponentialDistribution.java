package com.mhci.perfevalhw.distribution;

import java.util.HashMap;

public class ExponentialDistribution extends BaseDistribution {

	/* parameters */
	private float mLamda;
	
	public ExponentialDistribution(float lamda) {
		mLamda = lamda;	
	}
	
	@Override
	public float cdf(HashMap<String, Number> parameters) {
		return(float)(1 - Math.exp(-mLamda * parameters.get(RandVariableKey).floatValue()));
	}

	@Override
	public float inverseCdf(HashMap<String, Number> parameters) {
		return (float)(Math.log(1 - parameters.get(BaseDistribution.ProbabilityKey).floatValue()) / -mLamda);
	}

	@Override
	public float pdf(HashMap<String, Number> parameters) {
		return (float)(mLamda * Math.exp(-mLamda * parameters.get(RandVariableKey).floatValue()));
	}

	@Override
	public float variance() {
		return 1/(mLamda * mLamda);
	}

	@Override
	public float mean() {
		// TODO Auto-generated method stub
		return 1/mLamda;
	}

	@Override
	public boolean isDiscrete() {
		return false;
	}

}
