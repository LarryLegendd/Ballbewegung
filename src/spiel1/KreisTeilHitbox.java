package spiel1;

import java.awt.Graphics;

public class KreisTeilHitbox extends Hitbox{
	private int standardHitboxAccuracy;
	private double range;
	private double angle;
	
	public KreisTeilHitbox(double range, double angle,Transform transform) {
		this.range=range;
		this.angle=angle;
		
		this.transform=transform;
	}	
	
	@Override
	public Vector2[] toPoints(int hitboxAccuracy) {
		if(hitboxAccuracy<3)hitboxAccuracy=3;
		Vector2[] hitbox = new Vector2[hitboxAccuracy];
		hitbox[0]=new Vector2(0,0).makeGlobal(transform.position, transform.rotation);
		hitbox[1]=new Vector2(range,0).rotate(angle/2).makeGlobal(transform.position, transform.rotation);
		hitbox[2]=new Vector2(range,0).rotate(-(angle/2)).makeGlobal(transform.position, transform.rotation);
		Vector2 midpoint = new Vector2(0,0).makeGlobal(transform.position, transform.rotation);
		if(hitboxAccuracy>3)for(int i=3;i<hitboxAccuracy;i++) {
			if(i%3==0) {
				hitbox[i]=hitbox[0].lerp(hitbox[1],((double) (i - 2))/(hitboxAccuracy-3));

				
			}
			if(i%3==1) {
				hitbox[i]=hitbox[1].lerpImHalbkreis(hitbox[2],midpoint,((double) (i - 2))/(hitboxAccuracy-3));//TODO
				
			}
			if(i%3==2) {
				hitbox[i]=hitbox[2].lerp(hitbox[0],((double) (i - 2))/(hitboxAccuracy-3));
				
			}
		}
		
		return hitbox;
		// in vector2 gibt es ne methode fürs lerpen
		
	}

	@Override
	public boolean collides(Vector2 point) {
		point = point.makeLocal(transform.position,transform.rotation);
		if(point.length()<=range&&(point.angle()<angle/2||point.angle()>Math.PI*2-angle/2))return true;
		else return false;
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
				 point.draw(g);
			 }
		 }
	}
}
