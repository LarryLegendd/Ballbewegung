package spiel1;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class RectHitbox extends Hitbox {
		
		private int standardHitboxAccuracy=16;//zu wie vielen punkten die hitbox umgewandelt wird
		
		private double extradrehwinkel= 0;
		
		private Vector2 lastpoint;//temp
		private Vector2 lastppre;//temp
		private Vector2 lastpos;//temp
		
		private double lastangle = 0;
		
		private Vector2 lio;//links oben
		private Vector2 reo;//rechts oben
		private Vector2 liu;//links unten
		private Vector2 reu;//rechts unten
		
		
		public RectHitbox(Rectangle r,Transform transform) {
			lio = new Vector2(0,0);
			reu = new Vector2(r.getWidth(),-r.getHeight());
			reo = new Vector2(r.getWidth(),0);
			liu = new Vector2(0,-r.getHeight());
			this.transform=transform;
		}
		
		public RectHitbox(GameObject g) {
			lio = new Vector2(0,0);
			reu = new Vector2(g.getWidth(),-g.getHeight());
			reo = new Vector2(g.getWidth(),0);
			liu = new Vector2(0,-g.getHeight());
			transform =g.transform;
		}
		
		public RectHitbox(double width, double height,Transform transform) {
			lio = new Vector2(0,0);
			reu = new Vector2(width,-height);
			reo = new Vector2(width,0);
			liu = new Vector2(0,-height);
			this.transform = transform;
		}
		
		//für waffenhitbox
		public RectHitbox(double width, double height,Transform transform, boolean isWeapon) {//dreht die hitbox um 90° und setzt den 0punkt auf die mitte der unteren kante
			if(isWeapon) {
				extradrehwinkel=Math.PI/2;
				lio = new Vector2(-width/2,height);
				reu = new Vector2(width/2,0);
				reo = new Vector2(width/2,height);
				liu = new Vector2(-width/2,0);
				this.transform = transform;
			}else{
				lio = new Vector2(0,0);
				reu = new Vector2(width,-height);
				reo = new Vector2(width,0);
				liu = new Vector2(0,-height);
				this.transform = transform;
			}
		}
		
		

		public Vector2 getRechtsUnten() {
			return reo;
		}

		public void setRechtsUnten(Vector2 reu) {
			this.reu = reu;
			reo = new Vector2(reu.x,0);//nicht getestet
			liu = new Vector2(0,reu.y);
		}

		
		@Override
		public boolean collides(Vector2 point) {//theoretisch efficcienter die hitbox global abzuspeichern oder so das in collides hitbox für die anderen punkte gemerkt ist
			lastangle = transform.rotation;//Das ist der eigene angle weil der von der anderen ist schon in topoints
						
			lastpos=transform.position;
			lastppre = point;
			point = point.makeLocal(transform.position).rotate(-transform.rotation+extradrehwinkel);
			lastpoint=point;
			return (point.x >= lio.x && point.x <= reu.x &&
			        point.y <= lio.y && point.y >= reu.y);
		}
		
		
		@Override
		public Vector2[] toPoints(int hitboxAccuracy) {//wenn es zum problem wird in collides methode in beide richtungen aufrufen
			
			/*3. Konzeptionelles Problem: toPoints() ist teuer & instabil
Aktuell

toPoints() erzeugt jedes Mal neue Punkte

lerp + rotate + print

wird in Schleifen mehrfach aufgerufen

❌ Das skaliert schlecht und macht Debugging schwer.

Besseres Modell

Hitbox ist lokal

Transformation passiert einmal pro Frame

Empfehlung
Vector2[] getWorldPoints(Vector2 position, double angle)


statt:

collides(point, position, angle)


Dann:

Transformation außerhalb

Kollision = reine Geometrie*/
				if(hitboxAccuracy<4)hitboxAccuracy=4;
				Vector2[] hitbox = new Vector2[hitboxAccuracy];//irgendwo ist ein fehler das die punkte innerhalb generieren TODO
				hitbox[0]=lio.makeGlobal(transform.position, transform.rotation - extradrehwinkel);
				hitbox[1]=reo.makeGlobal(transform.position, transform.rotation - extradrehwinkel);
				hitbox[2]=liu.makeGlobal(transform.position, transform.rotation - extradrehwinkel);
				hitbox[3]=reu.makeGlobal(transform.position, transform.rotation - extradrehwinkel);
				if(hitboxAccuracy>4)for(int i=4;i<hitboxAccuracy;i++) {
					if(i%4==0) {
						hitbox[i]=hitbox[0].lerp(hitbox[1],((double) (i - 3))/(hitboxAccuracy-4));

//						hitbox[i].print(i+":");
					}
					if(i%4==1) {
						hitbox[i]=hitbox[2].lerp(hitbox[0],((double) (i - 3))/(hitboxAccuracy-4));
//						hitbox[i].print(i+":");
					}
					if(i%4==2) {
						hitbox[i]=hitbox[2].lerp(hitbox[3],((double) (i - 3))/(hitboxAccuracy-4));
//						hitbox[i].print(i+":");
					}
					if(i%4==3) {
						hitbox[i]=hitbox[1].lerp(hitbox[3],((double) (i - 4))/(hitboxAccuracy-4));
//						hitbox[i].print(i+":");
					}
				}
				
				return hitbox;
			}
		
		@Override
		protected int getStandardAccuracy() {
			return standardHitboxAccuracy;
		}

		@Override
		public void paintMe(Graphics g) {
			 
			 if(doDraw) //wenn aktiviert zeichnen
			 {
				 for(Vector2 point : toPoints()) {
					point.toJPanel().draw(g);
				 }
				 
				 g.drawRect( (int) lio.toJPanel().x, (int) lio.toJPanel().y, (int) reu.toJPanel().x, (int) reu.toJPanel().y);
				 if(lastpoint!=null) {
					 lastpoint.toJPanel().draw(g);
					// lastppre.draw(g, Color.green);
					 lastpos.toJPanel().draw(g, Color.blue);
				 }
			 }
			 
		}
}
		