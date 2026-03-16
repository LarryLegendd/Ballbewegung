package spiel1;

import java.awt.Graphics;
import java.awt.Rectangle;

public abstract class Hitbox {
	
	
	//TODO TODO TODO TODO 
	//TODO die hitnoxen müssen eigene angle haben und die in der kollision richtig miteinander verrechnen (vllt auch eigene position)
	
	protected  double lastangle=0;
	
	protected boolean doDraw=false;
	
	protected Transform transform;
	
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
	
	public boolean collides(Hitbox hitbox) {//angle ist angle von hitbox minus angle von this(um es lokal zu machen)
		lastangle = transform.rotation;
		for(Vector2 vertex : hitbox.toPoints()) {
			if (collides(vertex))	return true;
		}
		for(Vector2 vertex : toPoints()){//überprüfen ob es einen fenhler gibt das die gegner nicht getroffen werden können
			if (hitbox.collides(vertex))	return true;
		}
		return false;
		
	}
	
	public boolean collides(Hitbox hitbox,int hitboxAccuracy){
		lastangle= transform.rotation;
		for(Vector2 vertex : hitbox.toPoints(hitboxAccuracy)) {
			if (collides(vertex))	return true;
		}
		for(Vector2 vertex : toPoints(hitboxAccuracy)) {
			if (hitbox.collides(vertex))	return true;
		}
		return false;
	}
	
	
	
	
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