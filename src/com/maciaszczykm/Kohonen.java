package com.maciaszczykm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.floreks.Neuron;
import com.floreks.functions.TestFunction;

public class Kohonen {

	private static List<Neuron> neurons = new ArrayList<Neuron>();
	private static List<Neuron> pattern = new ArrayList<Neuron>();

	@SuppressWarnings("unused")
	private int neuronCounter = 100;
	private double lambda = 0.05;
	private double learnFactor = 0.4;
	private double epochsCounter = 20;

	public Kohonen(String patternDataPath, int neuronCounter, double lambda, double learnFactor, double epochsCounter) throws IOException {
		//setting variables
		this.neuronCounter = neuronCounter;
		this.lambda = lambda;
		this.learnFactor = learnFactor;
		this.epochsCounter = epochsCounter;
		//initializing neurons
		initNeurons(neuronCounter);
		//loading pattern data
		loadPatternData(patternDataPath);
	}

	private static void initNeurons(int counter) {
		for(int i=0; i<counter; i++) {
			//neurons initialized with x and y between 0 and 1
			neurons.add(new Neuron(new TestFunction(), Math.random(), Math.random()));
		}
	}

	@SuppressWarnings("resource")
	private static void loadPatternData(String path) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
		String line = null;
		while((line = reader.readLine()) != null) {
			String[] doubles = line.split(" ");
			pattern.add(new Neuron(new TestFunction(), Double.parseDouble(doubles[0]),Double.parseDouble(doubles[1])));
		}
	}

	public void process() {
		double distance = 0;
		for(int i=0; i<epochsCounter; i++) {
			for(int j=0; j<pattern.size(); j++) {
				Collections.sort(neurons,new NeuronComparator(pattern.get(j)));
				//updating winner
				distance = NeuronComparator.countDistance(neurons.get(0),pattern.get(j));
				double[] weights = neurons.get(0).getWeights();
				for(int l=0; l<weights.length; l++) {
					weights[l] = weights[l] + (learnFactor * Math.exp(-(distance*distance)/(2.0*lambda*lambda)) * (pattern.get(j).getWeights()[l] - neurons.get(0).getWeights()[l]));
				}
				neurons.get(0).setWeights(weights);
				//updating neighbourhood
				for(int k=0; k<neurons.size(); k++) {
					//counting distance between winner and another neurons
					distance = NeuronComparator.countDistance(neurons.get(k),neurons.get(0));
					for(int l=0; l<weights.length; l++) {
						weights[l] = weights[l] + (learnFactor * Math.exp(-(distance*distance)/(2.0*lambda*lambda)) * (pattern.get(j).getWeights()[l] - neurons.get(k).getWeights()[l]));
					}
				}
			}
		}
	}
	
	public void plot() {
		Plot.plot("Kohonen", neurons, pattern);
	}
	
	public List<Neuron> getNeurons() {
		return neurons;
	}

	public static void main(String[] args) throws IOException {
		Kohonen k = new Kohonen("resource/pattern.dat",100,0.05,0.4,20);
		k.process();
		k.plot();
	}

}

