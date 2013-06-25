package com.floreks.tests;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.floreks.NeuralLayer;
import com.floreks.NeuralNetwork;
import com.floreks.Neuron;
import com.floreks.Point;
import com.floreks.RBFNeuron;
import com.floreks.functions.LinearFunction;
import com.floreks.functions.TestFunction;
import com.floreks.interfaces.Function;
import com.maciaszczykm.Kohonen;
import com.maciaszczykm.NeuralGas;
import com.maciaszczykm.SOM;

public class NetworkTest {

	private static void readData(boolean col, String filePath,
			ArrayList<Neuron> tab) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			String line = reader.readLine();
			while (line != null) {
				String[] xy = line.split(" ");
				if (col == false) {
					tab.add(new Neuron(new TestFunction(), Double
							.parseDouble(xy[0])));
				} else {
					tab.add(new Neuron(new TestFunction(), Double
							.parseDouble(xy[1])));
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

		network.addLayer(layers[layers.length - 1]);

		if (activateFunctions.length == 1)
			network.setActivateFunction(activateFunctions[0]);

		return network;
	}

	private static double getNearestDist(int i, List<RBFNeuron> neurons) {
		RBFNeuron tmp = neurons.get(i);
		double ret = Double.MAX_VALUE;
		double dist;
		for (int j = 0; j < neurons.size(); j++) {
			if (j == i)
				continue;
			dist = tmp.dist(neurons.get(j));
			if (dist < ret)
				ret = dist;
		}
		return ret;
	}

	private static double countSigma(List<RBFNeuron> neurons) {
		double ret = .0;
		for (int i = 0; i < neurons.size(); i++)
			ret += getNearestDist(i, neurons);

		return ret / neurons.size();
	}

	private static List<RBFNeuron> toRBFNeurons(List<Neuron> neurons) {
		List<RBFNeuron> result = new ArrayList<RBFNeuron>();
		for (Neuron n : neurons) {
			result.add(new RBFNeuron(n.getWeights()));
		}
		return result;
	}

	public static double[] getResults(Point x, List<RBFNeuron> neurons) {
		int neuronsCount = neurons.size();
		double[] ret = new double[neuronsCount];
		int i = 0;
		for (RBFNeuron n : neurons)
			ret[i++] = n.getOut(x);
		return ret;
	}

	public static void aproxSplit(SOM som, double networkAlpha,
			double networkMomentum, String aproxTrainFile, String aproxTestFile, String chartTitle)
			throws IOException {

		ArrayList<Neuron> in = new ArrayList<Neuron>();
		ArrayList<Neuron> out = new ArrayList<Neuron>();

		readData(false, aproxTrainFile, in);
		readData(true, aproxTrainFile, out);

		som.setPattern(in);
		som.teach(100);

		// Utworzenie sieci
		NeuralLayer layer = new NeuralLayer(1, som.getNeurons().size());
		layer.setActivateFunction(new LinearFunction(1d, 0d));
		layer.setBiasEnabled(true);

		Neuron.setAlpha(networkAlpha);
		Neuron.setMomentum(networkMomentum);

		NeuralNetwork network = new NeuralNetwork();
		network.initWeights(-1d, 1d);
		network.addLayer(layer);

		// Warstwa z sigma

		List<RBFNeuron> neurons = toRBFNeurons(som.getNeurons());
		double sigma = countSigma(neurons);
		for (RBFNeuron n : neurons) {
			n.setSigma(sigma);
		}

		// nauka sieci

		int epochs = 10000;
		int pointCount = in.size();
		Random rnd = new Random();
		Point random;
		boolean[] tab = new boolean[pointCount];
		int chose;

		for (int i = 0; i < epochs; i++) {
			Arrays.fill(tab, false);
			for (int k = 0; k < pointCount; k++) {
				do {
					chose = rnd.nextInt(pointCount);
				} while (tab[chose]);
				tab[chose] = true;
				random = new Point(in.get(chose).getWeights());

				network.setExpectedOutput(out.get(chose).getWeights());
				network.initSignals(getResults(random, neurons));

				network.getOutput();
				network.teach();
			}
		}

		readData(false, aproxTestFile, in);
		readData(true, aproxTestFile, out);

		// test

		XYSeries series = new XYSeries("Line");
		for (int i = 0; i < in.size(); i++) {
			network.initSignals(getResults(new Point(in.get(i).getWeights()),
					neurons));
			network.setExpectedOutput(new double[] { out.get(i).getWeights()[0] });

			series.add(in.get(i).getWeights()[0], network.getOutput()[0]);
		}

		XYSeriesCollection data = new XYSeriesCollection();
		data.addSeries(series);
		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, "x",
				"y", data, PlotOrientation.VERTICAL, true, true, false);

		ChartFrame frame1 = new ChartFrame("XYArea Chart", chart);
		frame1.setVisible(true);
		frame1.setSize(500, 400);
	}
	
	public static void aproxTogether(SOM som, double networkAlpha,
			double networkMomentum, String aproxTrainFile, String aproxTestFile, String chartTitle)
			throws IOException {
		
		ArrayList<Neuron> in = new ArrayList<Neuron>();
		ArrayList<Neuron> out = new ArrayList<Neuron>();

		readData(false, aproxTrainFile, in);
		readData(true, aproxTrainFile, out);

		som.setPattern(in);
		som.teach(1);

		// Utworzenie sieci
		NeuralLayer layer = new NeuralLayer(1, som.getNeurons().size());
		layer.setActivateFunction(new LinearFunction(1d, 0d));
		layer.setBiasEnabled(true);

		Neuron.setAlpha(networkAlpha);
		Neuron.setMomentum(networkMomentum);

		NeuralNetwork network = new NeuralNetwork();
		network.initWeights(-1d, 1d);
		network.addLayer(layer);

		// Warstwa z sigma

		List<RBFNeuron> neurons = toRBFNeurons(som.getNeurons());
		double sigma = countSigma(neurons);
		for (RBFNeuron n : neurons) {
			n.setSigma(sigma);
		}

		// nauka sieci

		int epochs = 1000;
		int pointCount = in.size();
		Random rnd = new Random();
		Point random;
		boolean[] tab = new boolean[pointCount];
		int chose;

		for (int i = 0; i < epochs; i++) {
			som.teach(1);
			Arrays.fill(tab, false);
			for (int k = 0; k < pointCount; k++) {
				do {
					chose = rnd.nextInt(pointCount);
				} while (tab[chose]);
				tab[chose] = true;
				random = new Point(in.get(chose).getWeights());

				network.setExpectedOutput(out.get(chose).getWeights());
				network.initSignals(getResults(random, neurons));

				network.getOutput();
				network.teach();
			}
		}

		readData(false, aproxTestFile, in);
		readData(true, aproxTestFile, out);

		// test

		XYSeries series = new XYSeries("Line");
		for (int i = 0; i < in.size(); i++) {
			network.initSignals(getResults(new Point(in.get(i).getWeights()),
					neurons));
			network.setExpectedOutput(new double[] { out.get(i).getWeights()[0] });

			series.add(in.get(i).getWeights()[0], network.getOutput()[0]);
		}

		XYSeriesCollection data = new XYSeriesCollection();
		data.addSeries(series);
		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, "x",
				"y", data, PlotOrientation.VERTICAL, true, true, false);

		ChartFrame frame1 = new ChartFrame("XYArea Chart", chart);
		frame1.setVisible(true);
		frame1.setSize(500, 400);
	}

	public static void main(String[] args) throws IOException {

		// Kohonen - file small - together
		aproxTogether(new Kohonen(3), 0.001, 0.1,
				"src/resources/aprox-train-small.dat",
				"src/resources/aprox-test.dat",
				"Kohonen - file small");
		
		// Kohonen - file big - together
		aproxTogether(new Kohonen(2), 0.01, 0.1,
				"src/resources/aprox-train-big.dat",
				"src/resources/aprox-test.dat",
				"Kohonen - file big");
		
		// NeuralGas - file big - together
		aproxTogether(new NeuralGas(3), 0.01, 0.1,
				"src/resources/aprox-train-big.dat",
				"src/resources/aprox-test.dat",
				"Neural Gas - file big");
		
		// NeuralGas - file small - together
		aproxTogether(new NeuralGas(3), 0.01, 0.1,
				"src/resources/aprox-train-small.dat",
				"src/resources/aprox-test.dat",
				"Neural Gas - file small");
		
		// Kohonen - file small - split
		aproxSplit(new Kohonen(2), 0.001, 0.1,
				"src/resources/aprox-train-small.dat",
				"src/resources/aprox-test.dat",
				"Kohonen - file small");
		
		// Kohonen - file big - split
		aproxSplit(new Kohonen(4), 0.001, 0.1,
				"src/resources/aprox-train-big.dat",
				"src/resources/aprox-test.dat",
				"Kohonen - file big");
		
		// Neural gas - file big - split
		aproxSplit(new NeuralGas(2), 0.001, 0.1,
				"src/resources/aprox-train-big.dat",
				"src/resources/aprox-test.dat",
				"NeuralGas - file big");
		
		// Neural gas - file small - split
		aproxSplit(new NeuralGas(2), 0.001, 0.1,
				"src/resources/aprox-train-small.dat",
				"src/resources/aprox-test.dat",
				"NeuralGas - file small");

		
/* ============================================================= */
		
	}
}
