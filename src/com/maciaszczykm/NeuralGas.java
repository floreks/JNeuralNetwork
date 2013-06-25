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

public class NeuralGas implements SOM{

	private int neuronCounter;
	private double lambdaMax;
	private double lambdaMin = 0.01;
	private double lambda;
	private double learnFactorMax = 0.4;
	private double learnFactorMin = 0.003;
	private double learnFactor = learnFactorMax;
	private double epochsCounter;

	private List<Neuron> neurons = new ArrayList<Neuron>();
	private List<Neuron> pattern = new ArrayList<Neuron>();
	
	public List<Neuron> getNeurons() {
		return neurons;
	}

	public NeuralGas(int neuronCounter) {
		this.neuronCounter = neuronCounter;
		lambdaMax = neuronCounter / 2;
		lambda = lambdaMax;
	}

	protected static double countDistance(Neuron n1, Neuron n2) {
		double[] weights1 = n1.getWeights();
		double[] weights2 = n2.getWeights();
		if (weights1.length != weights2.length) {
			return -1;
		} else {
			double tempSum = 0;
			for (int i = 0; i < weights1.length; i++) {
				tempSum += (weights1[i] - weights2[i])
						* (weights1[i] - weights2[i]);
			}
			return Math.sqrt(tempSum);
		}
	}

	private void initNeurons(int counter, int size) {
		double []weights = new double[size];
		for (int i = 0; i < counter; i++) {
			for(int j=0;j<size;j++) {
				weights[j] = Math.random();
			}
			neurons.add(new Neuron(new TestFunction(), weights.clone()));
		}
	}

	@SuppressWarnings("resource")
	public void loadPatternData(String path) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(
				new File(path)));
		String line = null;
		while ((line = reader.readLine()) != null) {
			String[] doubles = line.split(" ");
			double []w = new double[doubles.length];
			for(int i=0;i<w.length;i++) {
				w[i] = Double.parseDouble(doubles[i]);
			}
			pattern.add(new Neuron(new TestFunction(), w.clone()));
		}
	}
	
	public void setPattern(List<Neuron> pattern) {
		this.pattern = pattern;
	}

	public void plot() {
		Plot.plot("Neural Gas", neurons, pattern);
	}

	public void teach(int epochs) throws IOException {
		epochsCounter = epochs;
		// neurons and pattern initialization
		initNeurons(neuronCounter,pattern.get(0).getWeights().length);
		// neural gas algorithm
		for(int i=0; i<epochsCounter; i++) {
			lambda = lambdaMax * Math.pow(lambdaMin/lambdaMax,(double)i/epochsCounter);
			for(int j=0; j<pattern.size(); j++) {
				//sorting neurons
				Collections.sort(neurons,new NeuronComparator(pattern.get(j)));
				//updating neurons weights and learn factor
				for(int k=0; k<neurons.size(); k++) {
					learnFactor = learnFactorMax*Math.pow(learnFactorMin/learnFactorMax,i/epochsCounter);
					double[] weights = neurons.get(k).getWeights();
					for(int l=0; l<weights.length; l++) {
						weights[l] = weights[l] + (learnFactor * Math.exp((double)-k/lambda) * (pattern.get(j).getWeights()[l] - neurons.get(k).getWeights()[l]));
					}
					neurons.get(k).setWeights(weights);
				}
			}
		}
	}
}
