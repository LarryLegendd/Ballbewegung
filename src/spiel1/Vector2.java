package spiel1;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

public class Vector2 {
	public double x;
	public double y;
	
	public Vector2(double x,double y) {
		this.x=x;
		this.y=y;
	}
	
	public Vector2(MouseEvent arg0) {
		x = arg0.getX();
		y = arg0.getY();
	}
	
	public double length() {
		return Math.sqrt(x*x + y*y);//Satz des Pythagoras
	}
	
	
	public double angle() {//Winkel im Bogenmaß
		double angle = Math.atan2(y, x);
		
		if (angle < 0) {// winkel ist immer positiv
		    angle += 2 * Math.PI;
		}
		return angle ;
	}
	
	public double angle(Vector2 P2, Vector2 P3) {//berechnet wird der winkel bei this zwischen P2 und P3
		//this zum ursprung machen
		P2 = P2.makeLocal(this);
		P3 = P3.makeLocal(this);
//		System.out.println("lokal zu pointpos");
//		System.out.println(P2+" b1");
//		System.out.println(P3+" b2");
//		//beide um - den winkel von P2 drehen, sodass P2 nach rechts zeigt und der winkel dazwischen nicht gestört wird
//		P3 = P3.rotate(-P2.angle());
//		System.out.println(P3+" der fertig transformierte punkt");
//		
		
		//angle von P3 finden
		return ((P2.angle()-P3.angle())+(Math.PI*2))%(Math.PI*2);
		
		// Richtungsvektoren von this zu den Punkten
//        Vector2 v1 = P2.makeLocal(this);
//        Vector2 v2 = P3.makeLocal(this);
//        System.out.println("v1 "+ v1+ "v2 "+v2);
//
//        // Skalarprodukt
//        double dot = v1.x * v2.x + v1.y * v2.y;
//        System.out.println("dot "+ dot);
//        // Längen
//        double len1 = Math.sqrt(v1.x * v1.x + v1.y * v1.y);
//        double len2 = Math.sqrt(v2.x * v2.x + v2.y * v2.y);
//        System.out.println("len1 "+ len1+ "len2 " + len2);
//        // Schutz
//        if (len1 == 0 || len2 == 0) {
//            throw new IllegalArgumentException("Punkte dürfen nicht identisch sein");
//        }
//
//        // cos(theta)
//        double cos = dot / (len1 * len2);
//        cos = Math.max(-1.0, Math.min(1.0, cos));
//
//        // Winkel im Bogenmaß
//        return Math.acos(cos);
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
		double winkelUnterschied3 = Math.abs(v.subtract(center).angle()+(2*Math.PI)-subtract(center).angle()); //TODO das macht noch keinen sinn, weil die richtung ist relevant und ich glaube das math.abs also der betrag macht die richtung kaputt
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
	
	private Vector2 toJP() {//JPanels haben y achse nach unten //TODO
		return new Vector2(x,-(y-Spielfeld.screenHeight));
	}
	
	public Vector2 toJPanel() {//JPanels haben y achse nach unten //TODO
		return makeLocal(Spielfeld.cameraPos).toJP();
	}
	
	public Vector2 toCoordinate() {
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
