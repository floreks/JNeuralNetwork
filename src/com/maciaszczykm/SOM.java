package com.maciaszczykm;

import java.io.IOException;
import java.util.List;

import com.floreks.Neuron;

public interface SOM {
	void setPattern(List<Neuron> pattern);
	void teach(int epochs) throws IOException;
	List<Neuron> getNeurons();
}
