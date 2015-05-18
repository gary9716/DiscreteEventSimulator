package com.mhci.perfevalhw.distribution;

import java.util.HashMap;

import org.apache.commons.math3.special.Erf;

//inverse CDF would only generate positive values
public class PositiveNormalDistribution extends BaseDistribution {
	
	private float mVarianceVal;
	private float mMeanVal;
	private float mStddev;
	
	private final static float sqrtTwo = (float)Math.sqrt(2);
	private final static float sqrtPi = (float)Math.sqrt(Math.PI);
	
	public PositiveNormalDistribution(float meanVal, float varianceVal) {
		mMeanVal = meanVal;
		mVarianceVal = varianceVal;
		mStddev = (float)Math.sqrt(mVarianceVal);
	}
	
	@Override
	public float cdf(HashMap<String, Number> parameters) {
		float randVarVal = parameters.get(RandVariableKey).floatValue();
		float meanDiff = randVarVal - mMeanVal;
		return (float)((1 + Erf.erf(meanDiff / (mStddev * sqrtTwo))) / 2);
	}

	@Override
	public float inverseCdf(HashMap<String, Number> parameters) {
		return (float)(mMeanVal + mStddev * sqrtTwo * Erf.erfInv(2 * parameters.get(BaseDistribution.ProbabilityKey).floatValue() - 1));
	}

	@Override
	public float pdf(HashMap<String, Number> parameters) {
		float randVarVal = parameters.get(RandVariableKey).floatValue();
		float meanDiff = randVarVal - mMeanVal;
		return (float)(Math.exp(-meanDiff*meanDiff/(2 * mVarianceVal)) / (mStddev * sqrtTwo * sqrtPi));
	}

	@Override
	public float variance() {
		return mVarianceVal;
	}

	@Override
	public float mean() {
		return mMeanVal;
	}
	
	@Override
	public boolean isDiscrete() {
		return false;
	}
	
	@Override
	public float drawASample(HashMap<String, Number> parameters) {
		return Math.abs(super.drawASample(parameters));
	}
}
