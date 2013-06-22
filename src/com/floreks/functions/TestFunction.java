package com.floreks.functions;

import com.floreks.interfaces.Function;

public class TestFunction implements Function {

	@Override
	public double value(double x) {
		return x;
	}

	@Override
	public double derivative(double x) {
		return 1.0;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
	
}
