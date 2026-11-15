package spiel1;

import java.awt.*;

public class Enemy extends GameObject {

private int health;
private MunitionGiver munitionGiver;
	public Enemy(Transform transform, double width, double height,int health, MunitionGiver munitionGiver){
		super(transform, width, height);//für erstellung von fliegende Gegner transform
		this.health = health;
		this.munitionGiver = munitionGiver;
	}
	
	public void schadenNehmen(int Schaden) {
		health -=Schaden;
		munitionGiver.giveMunition(1.0/health);
	}

	public boolean isDead() {
		if(health<0) {
			return true;//https://stackoverflow.com/questions/13205773/how-to-destroy-an-object-in-java
		}else {
			return false;
		}
	}
	
	
	
	@Override
	protected void paintMe(Graphics2D g2d) {
	    Vector2 jPos = getPosition().toJPanel();
	    g2d.drawRect(
	        (int) jPos.x(),
	        (int) jPos.y(),
	        (int) width,
	        (int) height
	    );
	}
}
