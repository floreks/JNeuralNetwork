package com.floreks.tests;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.floreks.NeuralLayer;
import com.floreks.NeuralNetwork;
import com.floreks.Neuron;
import com.floreks.functions.LinearFunction;
import com.floreks.functions.SigmoidalFunction;
import com.floreks.interfaces.Function;

public class NetworkTest {

	private static void readData(String filePath, ArrayList<Double> tab) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			String line = reader.readLine();
			while (line != null) {
				String[] xy = line.split(" ");
				for (String i : xy) {
					tab.add(Double.parseDouble(i));
				}

				line = reader.readLine();
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void print(double[] tab) {
		for (int i = 0; i < tab.length; i++) {
			System.out.print(tab[i] + " ");
			;
		}
		System.out.println();
	}

	public static NeuralNetwork networkBuilder(int layersCount,
			int[] neuronsCount, int[] inputsCount, Function[] activateFunctions) {

		NeuralNetwork network = new NeuralNetwork();

		NeuralLayer[] layers = new NeuralLayer[layersCount];
		for (int i = 0; i < layersCount; i++) {
			layers[i] = new NeuralLayer(neuronsCount[i], inputsCount[i]);
			if (activateFunctions.length != 1)
				layers[i].setActivateFunction(activateFunctions[i]);
		}

		for (int i = 0; i < layersCount - 1; i++) {
			layers[i].setNextLayer(layers[i + 1]);
			network.addLayer(layers[i]);
		}
		
		network.addLayer(layers[layers.length-1]);

		if (activateFunctions.length == 1)
			network.setActivateFunction(activateFunctions[0]);

		return network;
	}

	public static void main(String[] args) {

		NeuralNetwork network = networkBuilder(3, new int[] { 1, 12, 1 },
				new int[] { 1, 1, 12 }, new Function[] { new LinearFunction(1d,0d), new SigmoidalFunction(
						0.3), new LinearFunction(1d,0d) });

		network.setBiasEnabled(true);
		network.initWeights(-0.5, 0.5);
		Neuron.setAlpha(0.001);
		Neuron.setMomentum(0.4);

		double error = 1.0;
		double[] out;
		int epochs = 150000;
		ArrayList<Double> train, test;
		train = new ArrayList<Double>();
		test = new ArrayList<Double>();
		readData("C:\\train2.txt",train);
		readData("C:\\test.txt",test);

		while (epochs > 0) {
			error = 0.0;
			for (int j = 0; j < train.size(); j+=2) {
				network.initSignals(new double[] {train.get(j)});
				network.setExpectedOutput(new double[] {train.get(j+1)});

				network.getOutput();
				network.teach();
			}
			epochs--;
		}
		
		XYSeries series = new XYSeries("Line");
		for (int i = 0; i < test.size() ; i+=2) {
			network.initSignals(new double[] {test.get(i)});
			network.setExpectedOutput(new double[] {test.get(i+1)});
			
			series.add(test.get(i).doubleValue(), network.getOutput()[0]);
		}
		XYSeriesCollection data = new XYSeriesCollection();
		data.addSeries(series);
		JFreeChart chart = ChartFactory.createXYLineChart("Aproksymacja",
				"x", "y", data, PlotOrientation.VERTICAL, true, true,
				false);

		ChartFrame frame1 = new ChartFrame("XYArea Chart", chart);
		frame1.setVisible(true);
		frame1.setSize(500, 400);

	}
}
