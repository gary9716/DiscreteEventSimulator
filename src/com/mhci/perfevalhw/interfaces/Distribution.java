package com.mhci.perfevalhw.interfaces;

import java.util.HashMap;

public interface Distribution {
	
	public float cdf(HashMap<String, Number> parameters);
	public float inverseCdf(HashMap<String, Number> parameters);
	public float pdf(HashMap<String, Number> parameters);
	public float drawASample(HashMap<String, Number> parameters);
	public float variance();
	public float mean();
	public void setSeed(long seed);
	public boolean isDiscrete();
	
}
