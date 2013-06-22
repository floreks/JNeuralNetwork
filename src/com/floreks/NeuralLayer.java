package com.floreks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.floreks.interfaces.Function;

public class NeuralLayer {

	private static final String CLASSNAME = NeuralLayer.class.getName();
	private static final Logger LOGGER = Logger.getLogger(CLASSNAME);
	private List<Neuron> neurons = new ArrayList<Neuron>();

	public NeuralLayer(Integer neuronsCount, Integer inputSize,
			Function function) {

		LOGGER.debug("Creating layer - Neurons: " + neuronsCount
				+ " Input size: " + inputSize);
		for (int i = 0; i < neuronsCount; i++) {
			neurons.add(0, new Neuron(inputSize, function));
		}
	}

	public NeuralLayer(Integer neuronsCount, Integer inputSize) {

		for (int i = 0; i < neuronsCount; i++) {
			neurons.add(0, new Neuron(inputSize));
		}
	}

	public NeuralLayer() {

	}

	public void setNeurons(List<Neuron> neurons) {
		this.neurons = neurons;
	}

	public List<Neuron> getNeurons() {
		return neurons;
	}

	public void addNeuron(Neuron neuron) {
		neurons.add(neuron);
	}

	public void setActivateFunction(Function function) {
		for (int i = 0; i < neurons.size(); i++) {
			neurons.get(i).setActivateFunction(function);
		}
	}

	public void setExpectedOuput(List<Double> expectedOutput) {

		LOGGER.debug("Setting output");
		for (int i = 0; i < neurons.size(); i++) {
			neurons.get(i).setExpectedOutput(expectedOutput.get(i));
		}
	}

	public void initLayerWeights(double minRange, double maxRange) {
		for (int i = 0; i < neurons.size(); i++) {
			neurons.get(i).initWeights(minRange, maxRange);
		}
	}

	public void initLayerSignals(List<Double> signals) {
		for (int i = 0; i < neurons.size(); i++) {
			neurons.get(i).initSignals(signals);
		}
	}
	
	public void initFirstLayer(List<Double> signals) {
		for(int i=0;i<neurons.size();i++) {
			neurons.get(i).initSignals(Arrays.asList(new Double[] {signals.get(i)}));
		}
	}

	public void enableBias(Boolean bool) {
		for (int i = 0; i < neurons.size(); i++) {
			neurons.get(i).enableBias(bool);
		}
	}

	public List<Double> output() {
		List<Double> signals = new ArrayList<Double>();

		for (int i = 0; i < neurons.size(); i++) {
			LOGGER.info("Neuron nr: " + (i + 1));
			signals.add(neurons.get(i).output());
		}

		return signals;
	}

	public void teach() {
		for (int i = 0; i < neurons.size(); i++) {
			neurons.get(i).teach();
		}
	}
}
