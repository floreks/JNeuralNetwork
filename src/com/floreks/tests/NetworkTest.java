package com.floreks.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.floreks.NeuralLayer;
import com.floreks.NeuralNetwork;
import com.floreks.functions.SigmoidalFunction;

public class NetworkTest {

	public static void main(String[] args) {
		NeuralLayer firstLayer = new NeuralLayer(4, 1);
		NeuralLayer hiddenLayer = new NeuralLayer(2, 4);
		NeuralLayer outputLayer = new NeuralLayer(4, 2);

		NeuralNetwork network = new NeuralNetwork();

		// Initializing vectors
		ArrayList<ArrayList<Double>> expOutput = new ArrayList<ArrayList<Double>>();

		expOutput.add(new ArrayList<Double>(Arrays.asList(new Double[] { 1.0,
				0.0, 0.0, 0.0 })));
		expOutput.add(new ArrayList<Double>(Arrays.asList(new Double[] { 0.0,
				1.0, 0.0, 0.0 })));
		expOutput.add(new ArrayList<Double>(Arrays.asList(new Double[] { 0.0,
				0.0, 1.0, 0.0 })));
		expOutput.add(new ArrayList<Double>(Arrays.asList(new Double[] { 0.0,
				0.0, 0.0, 1.0 })));

		network.addLayer(firstLayer);
		network.addLayer(hiddenLayer);
		network.addLayer(outputLayer);

		network.setActivateFunction(new SigmoidalFunction(1));
		network.initNetworkWeights(-0.5, 0.5);
		network.enableBias(true);
		network.setAlpha(0.5);

		double error = 1.0;
		List<Double> out;
		ArrayList<Double> y = new ArrayList<Double>();
		int epochs = 10000;

		while (epochs > 0) {
			error = 0.0;
			for (int j = 0; j < expOutput.size(); j++) {
				network.initNetworkSignal(expOutput.get(j));
				network.setExpectedOutput(expOutput.get(j));

				out = network.output();
				for (int k = 0; k < out.size(); k++) {
					error += (out.get(k) - expOutput.get(j).get(k))
							* (out.get(k) - expOutput.get(j).get(k));
				}
				
				if(epochs == 1) {
					System.out.println(out);
				}
				
				error /= out.size();
				y.add(error);
				network.teach();
				System.out.println(error);
			}
			epochs--;
		}

		XYSeries series = new XYSeries("Line");
		for (int i = 0; i < y.size(); i++) {
			series.add(i, y.get(i));
		}
		XYSeriesCollection data = new XYSeriesCollection();
		data.addSeries(series);
		JFreeChart chart = ChartFactory.createXYLineChart("Transformacja",
				"epoka", "blad", data, PlotOrientation.VERTICAL, true, true,
				false);

		ChartFrame frame1 = new ChartFrame("XYArea Chart", chart);
		frame1.setVisible(true);
		frame1.setSize(500, 400);

	}
}
