package spiel1;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

public record Vector2(double x,double y) {

	public Vector2(MouseEvent arg0) {
		this((double) arg0.getX(),(double) arg0.getY());
	}

	public double length() {
		return Math.sqrt(x*x + y*y);//Satz des Pythagoras
	}

	/**
	 * Findet den Winkel am Ursprung, wobei der erste Schenkel zu diesem Vektor führt und der zweite Schenkel durch die
	 * positive Halbgerade der x-Achse beschrieben wird.
	 */
	public double angle() {

		double angle = Math.atan2(y, x);
		
		if (angle < 0) {// winkel ist immer positiv
		    angle += 2.0 * Math.PI;
		}
		return angle ;
	}

	/**
	 * Bei der Angle Method mit zwei Vektoren als Argumente wird der Winkel am Punkt des ausführenden Vektors gemessen.
	 * Die mitgegebenen Vektoren dienen als Schenkel.
	 * @param P2 Schenkel 1
	 * @param P3 Schenkel 2
	 *
	 * @return Winkel im Bogenmaß
	 */
	public double angle(Vector2 P2, Vector2 P3) {//berechnet wird der winkel bei this zwischen P2 und P3
		//this zum ursprung machen
		P2 = P2.makeLocal(this);
		P3 = P3.makeLocal(this);
		// Winkel voneinander Abziehen und 	negative winkel behandeln, indem 2pi addiert wird und modulo 2pi gerechnet hat. beide operationen verändern den betrag des Winkels nicht, schließen aber negative winkel aus.
		return ((P3.angle()-P2.angle())	+	(Math.PI*2.0))%(Math.PI*2.0);
	}
		
	public Vector2 normalize() {//Quelle: https://ceur-ws.org/Vol-1337/paper28.pdf dadurch bin ich auf die idee von normalize gekommen S.167 oben
		double angle = angle();		//polar machen
		
	    double x = Math.cos(angle);//wieder kartesisch machen, aber ohne die Länge zu berücksichtigen
	    double y = Math.sin(angle);

	    return new Vector2(x,y);
	    
	}
	
	public Vector2 reverse() {//macht beide werte *-1
		return new Vector2(this.x*-1,this.y*-1);
	}
	
	public Vector2 add(Vector2 b) {//addition der beiden Vectoren
		return new Vector2(this.x+b.x,this.y+b.y);
	}
	
	public Vector2 addX(double x) {
		return new Vector2(this.x+x,this.y);
	}
	
	public Vector2 addY(double y) {
		return new Vector2(this.x,this.y+y);
	}
	
	public Vector2 multiply(double value) {
		return new Vector2(this.x*value , this.y*value);
	}

	/**
	 * drehen dieses vektors
	 * @param angle
	 * @return
	 */
	public Vector2 rotate(double angle) {
		//polar machen und drehen
		double newAngle = ((angle()+angle) % (Math.PI*2));

		double x = Math.cos(newAngle);//kartesisch machen
	    double y = Math.sin(newAngle);

	    return new Vector2(x,y).multiply(length());
	}
	
	public Vector2 subtract(Vector2 v) {
		return new Vector2(this.x - v.x, this.y - v.y);
    }
	
	public Vector2 getPointBetween(Vector2 v) {
	    return this.add(v).multiply(0.5); 
	}

	/**Die Lerp Methode findet einen Punkt der auf einem Teil der Strecke zwischen zweier Punkte this und v liegt.
	 *
	 * @param v zweier Vektor
	 * @param x Teilungsverhältnis
	 */
	public Vector2 lerp(Vector2 v, double x) {//x soll zwischen 0 und 1 sein wenn man z.b. 3/5 macht muss man aufpassen das man das castet weil sonst auf 0 für ein int gerundet wird
		//Die Lineare Interpolation wird umgesetzt, indem zuerst der Vektor thisV zwischen zwischen den beiden Punkten
		// gebildet wird. Dieser wird dann mit faktor x multipliziert, hierbei sollte x zwischen 0 und 1 sein um ein
		// Ergebnis zu erhalten das zwischen den Punkten liegt. Anschließend wird der erhaltene vektor auf den Punkt this
		// addiert um das Endergebnis zu erhalten.
		return this.add(v.subtract(this).multiply(x));
	}

	/**
	 * Findet einen Punkt der auf einem Teil der Strecke zwischen zweier Punkte this und v liegt. Die Strecke ist dabei Kreisförmig.
	 * Der Mittelpunkt des Kreises ist center.
	 *
	 * @param v
	 * @param center
	 * @param x
	 */
	public Vector2 lerpImHalbkreis(Vector2 v, Vector2 center, double x) {

		//findet den Winkel zwischen den beiden Vektoren
		double winkelUnterschied = center.angle(v,this);

		//den Ursprung auf den Mittelpunkt des kreises setzen,		dann drehen von this 		um den gefundenen Winkel (so würde es den Vektor v ausgeben) multipliziert mit dem Teilungsverhältnis		dann global machen
		return makeLocal(center).									rotate(					winkelUnterschied*x).					makeGlobal(center);
		
	}

	/**
	 * @param v zweiter Vektor
	 * @return Distanz zu v
	 */
	public double distanceTo(Vector2 v) {
		return Math.abs(this.subtract(v).length());
	}

	/**
	 * Macht makeLocal(Vector2 abhaengig) rückgängig
	 * @param abhaengig
	 * @return
	 */
	public Vector2 makeGlobal(Vector2 abhaengig) {//nur add-Methode aber macht den Code lesbarer
		return add(abhaengig);
	}

	/**
	 * Diese Methode macht  makeLocal(Vector2 abhaengig,double andererAngle) rückgängig
	 * @param abhaengig
	 * @param EigenerAngle
	 * @return
	 */
	public Vector2 makeGlobal(Vector2 abhaengig, double EigenerAngle) {
		return rotate(EigenerAngle).add(abhaengig);
	}

	/**
	 * setzt den Ursprung auf abhaengig.
	 * @param abhaengig
	 * @return den Punkt this in diesem neuen Koordinatensystem.
	 */
	public Vector2 makeLocal(Vector2 abhaengig) {//nur subtract aber macht den Code lesbarer
		return subtract(abhaengig);
	}

	/**
	 * Diese Methode geht davon aus, dass abhaengig ein punkt ist und zusätzlich eine Drehrichtung hat.
	 * Dies ist zum Beispiel bei einem Eckpunkt eines gedrehten Quadrats der Fall. Diese Methode geht davon aus, das this Lokal zu abhaengig gemacht werden soll und um den winkel abhaengigs, EigenerAngle, gedreht werden soll.
	 * Im Beispiel des Quadrates wäre, wenn diese Methode bei allen Ecken relativ zur Ausgangsecke durchgeführt wurde das quadrat nicht mehr gedreht und die Ausgangsecke wäre am Ursprung.
	 */
	public Vector2 makeLocal(Vector2 abhaengig,double andererAngle) {
		return subtract(abhaengig).rotate(-andererAngle);
	}

	private Vector2 toJP() {//JPanels haben die y achse nach unten. diese methode macht wandelt einen Punkt in das JPanel Koordiantensystem um
		return new Vector2(x,Spielfeld.screenHeight-y);
	}
	
	public Vector2 toJPanel() {//JPanels haben y achse nach unten. Diese methode macht das rückgängig und berücksichtigt dabei die Kameraposition, sodass der ursprung bei der Kameraposition ist.
		return makeLocal(Spielfeld.cameraPos).toJP();
	}
	
	public Vector2 toCoordinate() {// Wandelt von JPanel zu Koordianten um unter berücksichtigung von der Kameraposition
	    Vector2 v = new Vector2(x, Spielfeld.screenHeight - y);
	    return  v.makeGlobal(Spielfeld.cameraPos);
	}

	/**
	 * Zeichnet den punkt
	 * @param g
	 */
	public void draw(Graphics g) {
		g.setColor(Color.RED);
		g.drawOval(((int) x)-1, ((int) y)-1, 2, 2);
		g.setColor(Color.BLACK);
	}

	/**
	 * Zeichnet den punkt mit einer Farbe
	 * @param g
	 */
	public void draw(Graphics g, Color c) {
		g.setColor(c);
		g.drawOval(((int) x)-1, ((int) y)-1, 2, 2);
		g.setColor(Color.BLACK);
	}
	
	@Override
	public String toString() {
		return ("x: "+x+" y: "+y);
		
	}
}
