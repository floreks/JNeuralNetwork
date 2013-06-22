package com.floreks.functions;

import com.floreks.interfaces.Function;

public class LinearFunction implements Function {

	private double a,b;
	
	public double value(double x) {
		return a*x+b;
	}

	public double derivative(double x) {
		return a;
	}
	
	public LinearFunction(double a, double b) {
		this.a = a;
		this.b = b;
	}
	
	@Override
	public String toString() {
		
		return this.getClass().getSimpleName();
	}

}
