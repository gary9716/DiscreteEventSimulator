package com.mhci.perfevalhw.distribution;

import java.util.HashMap;
import java.util.Random;

import com.mhci.perfevalhw.interfaces.Distribution;

public abstract class BaseDistribution implements Distribution {
	
	public final static String RandVariableKey = "RandVar";
	protected final static String ProbabilityKey = "Prob";
	protected Random uniformDist;
	protected HashMap<String, Number> mDefaultParams;

	public BaseDistribution() {
		uniformDist = new Random(System.currentTimeMillis());
		mDefaultParams = new HashMap<String, Number>();
	}

	@Override
	public float drawASample(HashMap<String, Number> parameters) {
		if(parameters == null) {
			parameters = mDefaultParams;
		}
		parameters.put(ProbabilityKey, uniformDist.nextFloat());
		return inverseCdf(parameters);
	}
	
	
	@Override
	public void setSeed(long seed) {
		uniformDist.setSeed(seed);
	}
	
	//for debugging
	public void printSampleVals(int numVals, HashMap<String, Number> parameters) {
		for(int i = 0;i < numVals;i++) {
			System.out.println(drawASample(parameters));
		}
	}
	
}
