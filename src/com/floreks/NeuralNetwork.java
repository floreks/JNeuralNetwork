package com.floreks;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.floreks.interfaces.Function;

public class NeuralNetwork {

	private static final String CLASSNAME = NeuralNetwork.class.getName();
	private static final Logger LOGGER = Logger.getLogger(CLASSNAME);
	private List<NeuralLayer> layers = new ArrayList<NeuralLayer>();

	public NeuralNetwork() {

	}

	public void addLayer(NeuralLayer layer) {
		layers.add(layer);
	}

	public void setLayers(List<NeuralLayer> layers) {
		this.layers = layers;
	}

	public void setActivateFunction(Function function) {
		for (int i = 0; i < layers.size(); i++) {
			layers.get(i).setActivateFunction(function);
		}
	}
	
	public void setAlpha(double alpha) {
		Neuron.setAlpha(alpha);
	}

	public void initNetworkWeights(double minRange, double maxRange) {
		for (int i = 0; i < layers.size(); i++) {
			layers.get(i).initLayerWeights(minRange, maxRange);
		}
	}

	public void initNetworkSignal(List<Double> signal) {
		layers.get(0).initFirstLayer(signal);
	}

	public void enableBias(Boolean bool) {
		for (int i = 0; i < layers.size(); i++) {
			layers.get(i).enableBias(bool);
		}
	}

	public void setExpectedOutput(List<Double> expOut) {
		layers.get(layers.size() - 1).setExpectedOuput(expOut);
	}

	public List<Double> output() {

		List<Double> layerOutput = new ArrayList<Double>();

		for (int i = 0; i < layers.size() - 1; i++) {
			// for every layer get layer output and propagate to next layer
			LOGGER.debug("Propagating layer " + (i + 1));
			layerOutput = layers.get(i).output();
			layers.get(i + 1).initLayerSignals(layerOutput);
		}

		LOGGER.debug("Propagating layer " + (layers.size()));
		return layers.get(layers.size() - 1).output();
	}

	public void teach() {
		LOGGER.info("TEACHING Last layer");

		for (int i = 0; i < layers.get(layers.size() - 1).getNeurons().size(); i++) {
			layers.get(layers.size() - 1).getNeurons().get(i).countError(0.0);
		}

		Double delta = 0.0;
		for (int i = layers.size() - 2; i >= 0; i--) {
			LOGGER.info("TEACHING Layer " + (i + 1));
			for (int j = 0; j < layers.get(i).getNeurons().size(); j++) {
				for (int k = 0; k < layers.get(i + 1).getNeurons().size(); k++) {
					delta += layers.get(i + 1).getNeurons().get(k).getDelta()
							* layers.get(i+1).getNeurons().get(k).getWeights().get(j);
				}
				layers.get(i).getNeurons().get(j).countError(delta);
				delta = 0.0;
			}
		}
		
		for(int i=0;i<layers.size();i++) {
			layers.get(i).teach();
		}
	}
}
