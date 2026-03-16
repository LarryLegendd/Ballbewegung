package spiel1;

import java.awt.Color;
import java.awt.Graphics;

public class Enemy extends GameObject {
private int health;
	public Enemy(Transform transform, double width, double height,int health){
		super(transform, width, height);//der Spieler wird fliegend erzeugt
		this.health = health;
		
		
		
	}
	
	public void schadenNehmen(int Schaden) {
		health -=Schaden;
	}
	public boolean isDead() {
		if(health<0) {
			return true;//https://stackoverflow.com/questions/13205773/how-to-destroy-an-object-in-java
		}else {
			return false;
		}
	}
	
	
	
	@Override
	protected void paintMe(Graphics g) {
	    Vector2 jPos = getPosition().toJPanel();
	    g.drawRect(
	        (int) jPos.x,
	        (int) jPos.y,
	        (int) width,
	        (int) height
	    );
//		hitbox= getHitbox(64);
//		for(Vector2 hitbox:hitbox){
//			hitbox.draw(g,Color.green);
//		}
	}
}
