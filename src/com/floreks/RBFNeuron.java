package com.floreks;

import java.util.Comparator;

import lombok.Getter;
import lombok.Setter;

public class RBFNeuron extends Point {

	public RBFNeuron(double... args) {
		super(args);
		sigma = 1;
	}

	/**
	 * Sortowanie punktow pod kontem odleglosci od zadanego punktu
	 * 
	 * @author Adam Kopaczewski 165443
	 * 
	 */
	public static class SortByDist implements Comparator<RBFNeuron> {
		public int compare(RBFNeuron o1, RBFNeuron o2) {
			return Double.compare(o1.dist, o2.dist);
		}

	}

	/**
	 * Zwraca wyjsciowa wartosc RBFNeuronu.
	 * 
	 * @param x
	 *            wektor wejsciowy
	 * @return odpowiedz RBFNeuronu
	 */
	public double getOut(Point x) {
		double dist = x.dist(this);
		return Math.exp(-dist * dist / sigma);
	}

	/**
	 * Ustawia wartosc sigmy. W przypadku gdy zazadanoustawic nowa sigme na 0
	 * program automatycznie ustawia sigme na 1, poniewaz sigma musi byc rozna
	 * od 0.
	 * 
	 * @param sigma
	 *            nowa wartosc sigmy
	 */
	public void setSigma(double sigma) {
		if (0. != sigma)
			this.sigma = sigma;
		else
			this.sigma = 1;

	}

	@Getter
	@Setter
	private double dist;
	@Getter
	private double sigma;
}
