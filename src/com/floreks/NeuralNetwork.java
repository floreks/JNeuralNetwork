package com.floreks;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.floreks.interfaces.Function;

public class NeuralNetwork {

	private static final String CLASSNAME = NeuralNetwork.class.getName();
	private static final Logger LOGGER = Logger.getLogger(CLASSNAME);
	private List<NeuralLayer> layers = new ArrayList<NeuralLayer>();
	private double[] expectedOutput;

	public NeuralNetwork() {

	}

	public void setExpectedOutput(double[] expOut) {
		LOGGER.debug("Setting expected output");

		this.expectedOutput = expOut.clone();
	}

	public double[] getExpectedOutput() {
		return expectedOutput;
	}

	public void addLayer(NeuralLayer layer) {
		layers.add(layer);
	}

	public void setLayers(List<NeuralLayer> layers) {
		this.layers.addAll(layers);
	}

	public List<NeuralLayer> getLayers() {
		return layers;
	}

	public void initWeights(double lowerBound, double upperBound) {
		LOGGER.debug("Initializing network weights");

		int i = 1;
		for (NeuralLayer layer : layers) {
			LOGGER.debug("Layer " + (i++));
			layer.initWeights(lowerBound, upperBound);
		}
	}

	public void initSignals(double[] signals) {
		LOGGER.debug("Initializing network signals");

		layers.get(0).initSignals(signals);
	}

	public void setBiasEnabled(boolean bool) {
		LOGGER.debug("Setting bias: " + bool);

		for (NeuralLayer layer : layers) {
			layer.setBiasEnabled(bool);
		}
	}

	public void setActivateFunction(Function function) {
		LOGGER.debug("Setting activate function: " + function);

		for (NeuralLayer layer : layers) {
			layer.setActivateFunction(function);
		}
	}

	public double[] getOutput() {
		LOGGER.debug("Counting network output.");

		for (int i = 0; i < layers.size() - 1; i++) {
			double[] layerOut = layers.get(i).output();

			for (int j = 0; j < layers.get(i + 1).getNeurons().size(); j++) {
				layers.get(i + 1).getNeurons().get(j).setSignals(layerOut);
			}
		}

		return layers.get(layers.size() - 1).output();
	}

	public void teach() {

		for (int i = 0; i < layers.get(layers.size() - 1).getNeurons().size(); i++) {
			double layerOut = layers.get(layers.size() - 1).getNeurons().get(i)
					.getResult();
			Function activateFunction = layers.get(layers.size() - 1)
					.getNeurons().get(i).getActivateFunction();
			layers.get(layers.size() - 1)
					.getNeurons()
					.get(i)
					.setDelta(
							(activateFunction.value(layerOut) - expectedOutput[i])
									* activateFunction.derivative(layerOut));
		}

		for (int i = layers.size() - 2; i >= 0; i--) {
			layers.get(i).teach();
		}
		
		for(NeuralLayer layer : layers) {
			layer.updateWeights();
		}
	}

}
