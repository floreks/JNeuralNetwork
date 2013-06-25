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

	private int neuronCounter = 100;
	private double lambdaMax = neuronCounter/2;
	private double lambdaMin = 0.01;
	private double lambda = lambdaMax;
	private double learnFactorMax = 0.4;
	private double learnFactorMin = 0.003;
	private double learnFactor = learnFactorMax;
	private double epochsCounter = 20;

	public Kohonen(String patternDataPath, int neuronCounter, double lambdaMin, double learnFactorMax, double learnFactorMin, double epochsCounter) throws IOException {
		//setting variables
		this.neuronCounter = neuronCounter;
		this.lambdaMax = this.neuronCounter/2;
		this.lambdaMin = lambdaMin;
		this.lambda = lambdaMax;
		this.learnFactorMax = learnFactorMax;
		this.learnFactorMin = learnFactorMin;
		this.learnFactor = learnFactorMax;
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
			lambda = lambdaMax * Math.pow(lambdaMin/lambdaMax,(double)i/epochsCounter);
			for(int j=0; j<pattern.size(); j++) {
				Collections.sort(neurons,new NeuronComparator(pattern.get(j)));
				//updating winner
				distance = NeuronComparator.countDistance(neurons.get(0),pattern.get(j));
				
				
//				neurons[0].w0 += e * exp(-(distance*distance)/(2.0*lambda*lambda)) * (datax[w]-neurons[0].w0);
//	            neurons[0].w1 += e * exp(-(distance*distance)/(2.0*lambda*lambda)) * (datay[w]-neurons[0].w1);
//				
//				 distance = countDistance(neurons[0],datax[w],datay[w]);
//				
//				for(int k=0; k<neurons.size(); k++) {
//					learnFactor = learnFactorMax*Math.pow(learnFactorMin/learnFactorMax,i/epochsCounter);
//					double[] weights = neurons.get(k).getWeights();
//					for(int l=0; l<weights.length; l++) {
//						weights[l] = weights[l] + (learnFactor * Math.exp((double)-k/lambda) * (pattern.get(j).getWeights()[l] - neurons.get(k).getWeights()[l]));
//					}
//					neurons.get(k).setWeights(weights);
//				}
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
		Kohonen k = new Kohonen("resource/pattern.dat",100,0.01,0.4,0.004,20);
		k.process();
		k.plot();
	}

}
