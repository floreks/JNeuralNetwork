package com.floreks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import com.floreks.interfaces.Function;

public class Neuron {

	private static final String CLASSNAME = Neuron.class.getName();
	private static final Logger LOGGER = Logger.getLogger(CLASSNAME);

	private static double ALPHA = 0.6;
	private double delta;
	private List<Double> weights = new ArrayList<Double>();
	private List<Double> signals = new ArrayList<Double>();
	private Function activateFunction;
	private Boolean biasEnabled = false;
	private Boolean lastLayer = false;
	private double expectedOutput;
	private double bias;

	public Neuron(Integer inputSize) {
		LOGGER.debug("Creating neuron - Input size: " + inputSize);

		for (int i = 0; i < inputSize; i++) {
			weights.add(0.0);
			signals.add(0.0);
		}

	}

	public Neuron(Integer inputSize, Function function) {
		LOGGER.debug("Creating neuron - Input size: " + inputSize);

		for (int i = 0; i < inputSize; i++) {
			weights.add(0.0);
			signals.add(0.0);
		}

		this.activateFunction = function;
	}

	public void enableBias(Boolean bool) {

		biasEnabled = bool;
	}

	public Boolean isBiasEnabled() {
		return biasEnabled;
	}

	public void setExpectedOutput(double output) {
		lastLayer = true;
		this.expectedOutput = output;
	}

	public double getExpectedOutput() {
		return expectedOutput;
	}

	public List<Double> getWeights() {
		return weights;
	}

	public double getDelta() {
		return delta;
	}

	public static void setAlpha(double alpha) {
		Neuron.ALPHA = alpha;
	}

	public static double getAlpha(double alpha) {
		return alpha;
	}

	public void setActivateFunction(Function function) {
		LOGGER.debug("Activate function - " + function);
		this.activateFunction = function;
	}

	public void initWeights(double minRange, double maxRange) {
		Random random = new Random();

		LOGGER.debug("Initializing weights, size: " + weights.size());
		for (int i = 0; i < weights.size(); i++) {
			weights.set(i,
					minRange + (maxRange - minRange) * random.nextDouble());
		}

		bias = minRange + (maxRange - minRange) * random.nextDouble();
	}

	public void initWeights(List<Double> weights) {
		this.weights = new ArrayList<Double>(weights);
	}

	public void initSignals(List<Double> signals) {
		LOGGER.debug("Initializing signals");
		this.signals = new ArrayList<Double>(signals);

	}

	public double output() {

		double neuronOutput = 0.0;

		for (int i = 0; i < weights.size(); i++) {
			LOGGER.info("Weight " + (i + 1) + ": " + weights.get(i));
			LOGGER.info("Signal " + (i + 1) + ": " + signals.get(i));
			neuronOutput += signals.get(i) * weights.get(i);
		}

		if (biasEnabled) {
			neuronOutput += bias;
		}

		LOGGER.info("Neuron output: " + activateFunction.value(neuronOutput));
		return activateFunction.value(neuronOutput);
	}

	public double countError(double delta) {
		if (lastLayer) {
			this.delta = (expectedOutput - output())
					* activateFunction.derivative(output());
			LOGGER.info("Expected out: " + expectedOutput);
			LOGGER.info("Delta: " + this.delta);
			return (expectedOutput - output())
					* activateFunction.derivative(output());
		} else {
			this.delta = delta * activateFunction.derivative(output());
			LOGGER.info("Expected out: " + expectedOutput);
			LOGGER.info("Delta: " + this.delta);
			return delta * activateFunction.derivative(output());
		}
	}

	public void teach() {

		double result = output();
		double deltaWeight = ALPHA * delta
				* activateFunction.derivative(result);
		;
		for (int i = 0; i < weights.size(); i++) {
			weights.set(i, weights.get(i) + (deltaWeight * signals.get(i)));
		}

		if (biasEnabled) {
			bias += deltaWeight;
		}
	}

}
