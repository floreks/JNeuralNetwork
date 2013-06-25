package com.floreks;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import lombok.Getter;
import lombok.Setter;

public class Point implements Comparable<Point> {
	public Point(double... coordinates) {
		this.coordinates = coordinates.clone();
	}

	/**
	 * Przewidziane dla dwuwymiarowych punktow. Najpierw sortuje rosnaco
	 * wzgledem x. Gdy x sa rowne to sortuje rosnaco wzgledem y.
	 */
	public int compareTo(Point o) {
		if (o.coordinates[0] == coordinates[0])
			return Double.compare(coordinates[1], o.coordinates[1]);
		return Double.compare(coordinates[0], o.coordinates[0]);
	}

	@Override
	public boolean equals(Object obj) {
		if (null == obj)
			return false;

		if (this == obj)
			return true;

		if (getClass() != obj.getClass())
			return false;

		Point p = (Point) obj;
		if (this.coordinates.length != p.coordinates.length)
			return false;

		for (int i = 0; i < this.coordinates.length; i++)
			if (this.coordinates[i] != p.coordinates[i])
				return false;
		return true;
	}

	/**
	 * Przesuwa punkt o wektor alfa * (this, p). Dziala tylko wtedy gdy punkty
	 * maja ten sam wymiar.
	 * 
	 * @param p
	 * @param alfa
	 */
	public void move(Point p, double alfa) {
		if (p.coordinates.length != coordinates.length)
			return;
		for (int i = 0; i < coordinates.length; i++)
			coordinates[i] += alfa * (p.getCoordinate(i) - coordinates[i]);
	}

	// specjalne metody dla co najwyzej trojwymiarowych punktow
	/**
	 * Zwraca wspolrzedna x (zerowa) -- o ile punkt ja posaida
	 * 
	 * @return x
	 */
	public double getX() {
		if (coordinates.length == 0)
			throw new IllegalArgumentException(
					"Point doesn't contain x coordinate.");
		return coordinates[0];
	}

	/**
	 * Zwraca wspolrzedna y (pierwsza ) -- o ile punkt ja posaida
	 * 
	 * @return y
	 */
	public double getY() {
		if (coordinates.length < 1)
			throw new IllegalArgumentException(
					"Point doesn't contain y coordinate.");
		return coordinates[1];
	}

	/**
	 * Zwraca wspolrzedna z (druga) -- o ile punkt ja posaida
	 * 
	 * @return z
	 */
	public double getZ() {
		if (coordinates.length < 2)
			throw new IllegalArgumentException(
					"Point doesn't contain z coordinate.");
		return coordinates[2];
	}

	/**
	 * Ustawia wspolrzedna x (o ile punkt ja posaida) punktu na wartosc value.
	 * 
	 * @param value
	 */
	public void setX(double value) {
		if (coordinates.length == 0)
			throw new IllegalArgumentException(
					"Point doesn't contain x coordinate.");
		coordinates[0] = value;
	}

	/**
	 * Ustawia wspolrzedna y (o ile punkt ja posaida) na wartosc value.
	 * 
	 * @param value
	 */
	public void setY(double value) {
		if (coordinates.length < 1)
			throw new IllegalArgumentException(
					"Point doesn't contain y coordinate.");
		coordinates[1] = value;
	}

	/**
	 * Uatawia wspolrzedna z (o ile punkt ja posaida) na wartosc value.
	 * 
	 * @param value
	 */
	public void setZ(double value) {
		if (coordinates.length < 2)
			throw new IllegalArgumentException(
					"Point doesn't contain z coordinate.");
		coordinates[2] = value;
	}

	/**
	 * Zwraca rozmiar punktu (ile ma wymiarow)
	 * 
	 * @return rozmiar punktu
	 */
	public int getSize() {
		return coordinates.length;
	}

	/**
	 * Zwraca wspolrzedna punktu o numerze index
	 * 
	 * @param index
	 *            nr wspolrzednej
	 * @return wspolrzedna punktu
	 */
	public double getCoordinate(int index) {
		if (coordinates.length <= index)
			throw new IllegalArgumentException("Point doesn't contain " + index
					+ " coordinate.");
		if (index < 0)
			throw new IllegalArgumentException("Index cannot be negative.");
		return coordinates[index];
	}

	/**
	 * Zwraca tablice zawierajaca wspolrzedne punktu
	 * 
	 * @return
	 */
	public double[] getCoordinates() {
		return coordinates.clone();
	}

	/**
	 * Ustawia wspolrzedna punktu index (o ile punkt ja posaida) na wartosc
	 * value
	 * 
	 * @param index
	 *            numer wspolrzendje punktu
	 * @param value
	 *            wartosc
	 */
	public void setCoordinate(int index, double value) {
		if (coordinates.length <= index)
			throw new IllegalArgumentException("Point doesn't contain " + index
					+ " coordinate.");
		if (index < 0)
			throw new IllegalArgumentException("Index cannot be negative.");
		coordinates[index] = value;
	}

	/**
	 * Ustawia nowe wspolrzedne punktu. Nie da sie w ten sposob zmienic wymiaru
	 * punktu. Jesli tablica z nowymi wspolrzednymi jest dluzsza od wymniaru
	 * punktu to obcina nadmiarowe wartosci, jesli jest krotsza to pozostale
	 * wspolrzedne wypelnia zerami.
	 * 
	 * @param coordinates
	 */
	public void setCoordinates(double[] coordinates) {
		this.coordinates = Arrays.copyOf(coordinates, this.coordinates.length);
	}

	/**
	 * Oblicza euklidesowska odleglosc miedzy dowam punktami
	 * 
	 * @param point
	 * @return euklidesowska odleglosc
	 */
	public double dist(Point point) {
		int length = Math.min(point.coordinates.length, coordinates.length);
		double ret = .0;
		for (int i = 0; i < length; i++)
			ret += (point.getCoordinate(i) - coordinates[i])
					* (point.getCoordinate(i) - coordinates[i]);
		return Math.sqrt(ret);
	}

	public String toString() {
		StringBuilder build = new StringBuilder();
		for (int i = 0; i < coordinates.length; i++) {
			build.append(coordinates[i]);
			build.append(" ");
		}
		return build.toString();
	}

	/**
	 * Wczytuje dwywmiarowe punkty z pliku
	 * 
	 * @param pathname
	 *            sciezka do pliku
	 * @return
	 * @throws FileNotFoundException
	 */
	public static List<Point> readPoints2D(String pathname)
			throws FileNotFoundException {
		List<Point> ret = new ArrayList<Point>();
		Scanner in = new Scanner(new File(pathname));
		String[] tmp;
		while (in.hasNextLine()) {
			tmp = in.nextLine().split(" ");
			ret.add(new Point(Double.valueOf(tmp[0]), Double.valueOf(tmp[1])));
		}
		in.close();
		return ret;
	}

	/**
	 * Wczytuje dwuwymiarowe punkty z pliku, ktory zwiera wiecej niz dwie
	 * kolumny danych
	 * 
	 * @param pathname
	 * @param index1
	 *            nr kolumny z odcietymi
	 * @param index2
	 *            nr kolumny z rzednymi
	 * @return
	 * @throws FileNotFoundException
	 */
	public static List<Point> readPoints2D(String pathname, int index1,
			int index2) throws FileNotFoundException {
		List<Point> ret = new ArrayList<Point>();
		Scanner in = new Scanner(new File(pathname));
		String[] tmp;
		while (in.hasNextLine()) {
			tmp = in.nextLine().split(" ");
			ret.add(new Point(Double.valueOf(tmp[index1]), Double
					.valueOf(tmp[index2])));
		}
		in.close();
		return ret;
	}

	/**
	 * W zbiorze punktow znajduje skrajne wartosci wspolrzednych
	 * 
	 * @param points
	 * @return
	 */
	public static Range getRange(List<Point> points) {
		double maxX, maxY, minX, minY, x, y;
		maxX = maxY = Double.MIN_VALUE;
		minX = minY = Double.MAX_VALUE;
		for (Point p : points) {
			if (p.getSize() != 2)
				throw new IllegalArgumentException(
						"One of points has invalid size.");
			x = p.getCoordinate(0);
			y = p.getCoordinate(1);
			if (x > maxX)
				maxX = x;
			if (x < minX)
				minX = x;
			if (y > maxY)
				maxY = y;
			if (y < minY)
				minY = y;
		}
		return new Range(maxX, maxY, minX, minY);
	}

	/**
	 * Ograniczenia obszaru
	 * 
	 * @author Adam Kopaczewski 165443
	 * 
	 */
	public static class Range {
		public Range(double maxX, double maxY, double minX, double minY) {
			this.maxX = maxX;
			this.maxY = maxY;
			this.minX = minX;
			this.minY = minY;
		}

		public String toString() {
			StringBuilder build = new StringBuilder();
			build.append("xRange [");
			build.append(minX);
			build.append(":");
			build.append(maxX);
			build.append("]\nxRange [");
			build.append(minY);
			build.append(":");
			build.append(maxY);
			build.append("]");
			return build.toString();
		}

		@Getter
		@Setter
		private double maxX;
		@Getter
		@Setter
		private double maxY;
		@Getter
		@Setter
		private double minX;
		@Getter
		@Setter
		private double minY;
	}

	private double[] coordinates;
}
