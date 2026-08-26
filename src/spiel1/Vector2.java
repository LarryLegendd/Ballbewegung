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
		    angle += 2 * Math.PI;
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

		return ((P2.angle()-P3.angle())+(Math.PI*2))%(Math.PI*2);//TODO das sieht genuently falsch aus
	}
		
		
	public Vector2 normalize() {//Quelle: https://ceur-ws.org/Vol-1337/paper28.pdf dadurch bin ich auf die idee von normalize gekommen
		double angle = angle();		//polar machen
		
	    double x = Math.cos(angle);//wieder kartesisch machen
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
	
//	public void print() {
//		System.out.println("x: "+x+" y: "+y);
//	}
//	public void print(String name) {
//		System.out.println(name + " x: "+x+" y: "+y);
//	}
	
	public Vector2 rotate(double angle) {
//		double newAngle = ((angle()+angle) % (Math.PI*2));
//		
//		double x = Math.cos(newAngle);//kartesisch machen
//	    double y = Math.sin(newAngle);
//	    
//	    return new Vector2(x,y).multiply(length());
//		
		double cos = Math.cos(angle);
	    double sin = Math.sin(angle);
	    
	    double newX = this.x * cos - this.y * sin;
	    double newY = this.x * sin + this.y * cos;
	    
	    return new Vector2(newX, newY);
	}
	
	public Vector2 subtract(Vector2 v) {
		return new Vector2(this.x - v.x, this.y - v.y);
    }
	
	public Vector2 getPointBetween(Vector2 v) {
	    return this.add(v).multiply(0.5); 
	}
	
	public Vector2 lerp(Vector2 v, double x) {//x soll zwischen 0 und 1 sein wenn man z.b. 3/5 macht muss man aufpassen das man das castet weil sonst auf 0 für ein int gerundet wird
		return this.add(v.subtract(this).multiply(x));
	}
	
	public Vector2 lerpImHalbkreis(Vector2 v, Vector2 center, double x) {//sehr skurrile klasse
		
		double winkelUnterschied = Math.abs(v.subtract(center).angle()-subtract(center).angle());
		double winkelUnterschied2 = Math.abs(v.subtract(center).angle()-subtract(center).angle()+(2*Math.PI));
		double winkelUnterschied3 = Math.abs(v.subtract(center).angle()+(2*Math.PI)-subtract(center).angle()); //TODO prüfen ob das passt
		if(winkelUnterschied>winkelUnterschied2)winkelUnterschied = winkelUnterschied2;
		if(winkelUnterschied>winkelUnterschied3)winkelUnterschied = winkelUnterschied3;
		
		//lokal machen dann drehen dann global machen
		return subtract(center).rotate(winkelUnterschied*x).add(center);
		
	}
	
	public double distanceTo(Vector2 v) {
		return Math.abs(this.subtract(v).length());
	}

	public Vector2 makeGlobal(Vector2 abhaengig) {//nur add methode aber leichter zu verstehen
		return add(abhaengig);
	}
	
	public Vector2 makeGlobal(Vector2 abhaengig, double EigenerAngle) {
		return rotate(EigenerAngle).add(abhaengig);
	}
	
	public Vector2 makeLocal(Vector2 abhaengig) {//nur subtract aber macht mehr sinn
		return subtract(abhaengig);
	}
	public Vector2 makeLocal(Vector2 abhaengig,double andererAngle) {
		return subtract(abhaengig).rotate(-andererAngle);
	}
	
	private Vector2 toJP() {//JPanels haben y achse nach unten
		return new Vector2(x,-(y-Spielfeld.screenHeight));
	}
	
	public Vector2 toJPanel() {//JPanels haben y achse nach unten
		return makeLocal(Spielfeld.cameraPos).toJP();
	}
	
	public Vector2 toCoordinate() {// Wandelt von JPanel zu Koordianten um
	    Vector2 v = new Vector2(x, Spielfeld.screenHeight - y);
	    return  v.makeGlobal(Spielfeld.cameraPos);
	}
	
	public void draw(Graphics g) {
		g.setColor(Color.RED);
		g.drawOval(((int) x)-1, ((int) y)-1, 2, 2);
		g.setColor(Color.BLACK);
	}
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
