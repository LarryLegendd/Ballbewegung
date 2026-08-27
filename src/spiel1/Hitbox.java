package spiel1;

import java.awt.Graphics;
import java.awt.Rectangle;

public abstract class Hitbox {
	

	protected double lastangle=0;
	
	protected boolean doDraw=false;

	protected int size;   //ein Kreis um den ersten punkt der die gesamte hitbox beinhaltet. Wird zur optimierung genutzt

	protected Transform transform;

	protected Hitbox(Transform transform, int size){
		this.transform = transform;
		this.size =size;
	}

	public Vector2 getPosition() {
		return transform.position;
	}

	public void setPosition(Vector2 position) {
		this.transform.position = position;
	}
	
	protected abstract int getStandardAccuracy();
	public Vector2[] toPoints() {
		return toPoints(getStandardAccuracy());
	}
	public abstract Vector2[] toPoints(int HitboxAccuracy);

	
	public abstract boolean collides(Vector2 point);
	
	public boolean collides(double x,double y) {
		return collides(new Vector2(x,y));
	}
	
	public boolean collides(Hitbox hitbox) {//angle ist angle von hitbox minus angle von this (um es lokal zu machen)
		if(hitbox.getPosition().distanceTo(getPosition())<this.size+hitbox.size) {// für performance nur prüfen, wenn hitboxen nah genug aneinander sind
			lastangle = transform.rotation;
			for (Vector2 vertex : hitbox.toPoints()) {
				if (collides(vertex)) return true;
			}
			for (Vector2 vertex : toPoints()) {//TODO überprüfen ob es einen fenhler gibt das die gegner nicht getroffen werden können
				if (hitbox.collides(vertex)) return true;
			}
		}
		return false;
	}
	
	public boolean collides(Hitbox hitbox,int hitboxAccuracy){
		if(hitbox.getPosition().makeLocal(getPosition()).length()<this.size+hitbox.size) {// für performance nur prüfen, wenn hitboxen nah genug aneinander sind
			lastangle = transform.rotation;
			for (Vector2 vertex : hitbox.toPoints(hitboxAccuracy)) {
				if (collides(vertex)) return true;
			}
			for (Vector2 vertex : toPoints(hitboxAccuracy)) {
				if (hitbox.collides(vertex)) return true;
			}
		}
		return false;
	}
	
	
	
	public int getsize(){return size;}

	public void draw(boolean doDraw) {
		this.doDraw = doDraw;
	}
	
	
	public void paintMe(Graphics g) {
		 
		 if(doDraw) //wenn aktiviert zeichnen
		 {
			 for(Vector2 point : toPoints()) {
				point.toJPanel().draw(g);
			 }
		 }
		 
	}
	

}