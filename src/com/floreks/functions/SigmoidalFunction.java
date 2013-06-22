package com.floreks.functions;

import com.floreks.interfaces.Function;

public class SigmoidalFunction implements Function{
	
	private double beta;
	
	public double value(double x) {
		return (1/(1 + Math.exp(-beta*x)));
	}

	public double derivative(double x) {
		return (value(x) * (1 - value(x)));
	}
	
	public SigmoidalFunction(double beta) {
		this.beta = beta;
	}
	
	@Override
	public String toString() {
		
		return this.getClass().getSimpleName();
	}
}
