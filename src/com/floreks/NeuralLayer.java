package com.floreks;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.floreks.interfaces.Function;

public class NeuralLayer {

	private static final String CLASSNAME = NeuralLayer.class.getName();
	private static final Logger LOGGER = Logger.getLogger(CLASSNAME);
	private List<Neuron> neurons = new ArrayList<Neuron>();
	private NeuralLayer nextLayer;
	private boolean bias = false;

	public NeuralLayer(int neuronsCount, int inputSize) {

		for (int i = 0; i < neuronsCount; i++) {
			neurons.add(0, new Neuron(inputSize));
		}
	}

	public void setNextLayer(NeuralLayer layer) {
		this.nextLayer = layer;
	}

	public NeuralLayer getNextLayer() {
		return nextLayer;
	}

	public void addNeuron(Neuron neuron) {
		neurons.add(neuron);
	}

	public void addNeurons(List<Neuron> neurons) {
		this.neurons.addAll(neurons);
	}

	public void setActivateFunction(Function function) {
		for (Neuron neuron : neurons) {
			neuron.setActivateFunction(function);
		}
	}

	public void initWeights(double lowerBound, double upperBound) {
		int i = 1;
		for (Neuron neuron : neurons) {
			LOGGER.debug("Neuron " + (i++));
			neuron.initWeights(lowerBound, upperBound);
		}
	}

	public void initSignals(double[] signals) {
		neurons.get(0).setSignals(signals);
	}

	public void setBiasEnabled(boolean bool) {
		bias = bool;
		for (Neuron neuron : neurons) {
			neuron.setBiasEnabled(bool);
		}
	}

	public List<Neuron> getNeurons() {
		return neurons;
	}

	public double[] output() {
		double[] result = new double[neurons.size()];

		for (int i = 0; i < neurons.size(); i++) {
			result[i] = neurons.get(i).output();
		}

		return result;
	}

	public void teach() {

		for (int i = 0; i < neurons.size(); i++) {
			double error = 0d;
			for (int j = 0; j < nextLayer.getNeurons().size(); j++) {
				if (bias) {
					error += nextLayer.getNeurons().get(j).getDelta()
							* nextLayer.getNeurons().get(j).getWeights()[i + 1];
				} else {
					error += nextLayer.getNeurons().get(j).getDelta()
							* nextLayer.getNeurons().get(j).getWeights()[i];
				}
			}
			neurons.get(i).setDelta(
					error
							* neurons.get(i).getActivateFunction()
									.derivative(neurons.get(i).getResult()));
		}
	}
	
	public void updateWeights() {
		for(Neuron neuron : neurons) {
			neuron.teach();
		}
	}
}
