package com.maciaszczykm;

import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import com.floreks.Neuron;

public class Plot extends ApplicationFrame {
	
	private static final long serialVersionUID = -7875653229675786674L;

	public Plot(final String title, List<Neuron> neurons, List<Neuron> pattern) {
		super(title);
		final XYSeries patternData = new XYSeries("Pattern data");
		final XYSeries neuronsData = new XYSeries("Neurons data");
		for(Neuron p : pattern) {
			patternData.add(p.getWeights()[0],p.getWeights()[1]);
		}
		for(Neuron n : neurons) {
			neuronsData.add(n.getWeights()[0],n.getWeights()[1]);
		}
		final XYSeriesCollection data = new XYSeriesCollection();
		data.addSeries(neuronsData);
		data.addSeries(patternData);
		final JFreeChart chart = ChartFactory.createScatterPlot(title,"X","Y", data, PlotOrientation.VERTICAL, true, true, false);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(1000,700));
		setContentPane(chartPanel);
	}

	static void plot(String title, List<Neuron> neurons, List<Neuron> pattern) {
		final Plot plot = new Plot(title, neurons, pattern);
		plot.pack();
		RefineryUtilities.centerFrameOnScreen(plot);
		plot.setVisible(true);
	}
	
}
