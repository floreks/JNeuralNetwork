package com.maciaszczykm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.jfree.ui.RefineryUtilities;
import com.floreks.Neuron;
import com.floreks.functions.TestFunction;

public class NeuralGas {

	static List<Neuron> neurons = new ArrayList<Neuron>();
	static List<Neuron> pattern = new ArrayList<Neuron>();

	public static double countDistance(Neuron n1, Neuron n2) {
		double[] weights1 = n1.getWeights();
		double[] weights2 = n2.getWeights();
		if(weights1.length!=weights2.length) {
			return -1;
		} else {
			double tempSum = 0;
			for(int i=0; i<weights1.length; i++) {
				tempSum += (weights1[i]-weights2[i])*(weights1[i]-weights2[i]);
			}
			return Math.sqrt(tempSum);
		}
	}

	static void initNeurons(int counter) {
		for(int i=0; i<counter; i++) {
			neurons.add(new Neuron(new TestFunction(), Math.random(), Math.random()));
		}
	}

	@SuppressWarnings("resource")
	static void loadPatternData(String path) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
		String line = null;
		while((line = reader.readLine()) != null) {
			String[] doubles = line.split(" ");
			pattern.add(new Neuron(new TestFunction(), Double.parseDouble(doubles[0]),Double.parseDouble(doubles[1])));
		}
	}

	static void plot() {
		final Plot plot = new Plot("Neural Gas", neurons, pattern);
		plot.pack();
		RefineryUtilities.centerFrameOnScreen(plot);
		plot.setVisible(true);
	}

	public static void main(String[] args) throws IOException {
		//variables initialization
		int neuronCounter = 100;
		double lambdaMax = neuronCounter/2, lambdaMin = 0.01, lambda = lambdaMax;
		double learnFactorMax = 0.4, learnFactorMin = 0.003, learnFactor = learnFactorMax;
		double epochsCounter = 20;
		//neurons and pattern initialization
		initNeurons(neuronCounter);
		loadPatternData("resource/pattern.dat");
		//neural gas algorithm
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
		//plotting output
		plot();
	}

}

class NeuronComparator implements Comparator<Neuron> {
	
	Neuron patternNeuron = null;

	NeuronComparator(Neuron patternNeuron) {
		this.patternNeuron = patternNeuron;
	}
	
	@Override
	public int compare(Neuron n1, Neuron n2) {
		if(NeuralGas.countDistance(patternNeuron, n1) > NeuralGas.countDistance(patternNeuron, n2)) return 1;
		else if(NeuralGas.countDistance(patternNeuron, n1) == NeuralGas.countDistance(patternNeuron, n2)) return 0;
		else return -1;
	}
	
}

