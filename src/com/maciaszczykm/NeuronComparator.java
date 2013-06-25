package com.maciaszczykm;

import java.util.Comparator;
import com.floreks.Neuron;

class NeuronComparator implements Comparator<Neuron> {
	
	Neuron patternNeuron = null;

	NeuronComparator(Neuron patternNeuron) {
		this.patternNeuron = patternNeuron;
	}
	
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
	
	@Override
	public int compare(Neuron n1, Neuron n2) {
		if(countDistance(patternNeuron, n1) > countDistance(patternNeuron, n2)) return 1;
		else if(countDistance(patternNeuron, n1) == countDistance(patternNeuron, n2)) return 0;
		else return -1;
	}
	
}