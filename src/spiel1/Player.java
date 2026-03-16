package spiel1;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Player extends GameObject{
	
	public Player(Transform transform, double width, double height){
		super(transform, width, height);//der Spieler wird fliegend erzeugt
	}
	
	public void reset() {
		setSpeed(transform.speed);//springt nach oben
		setPosition(new Vector2(10,Spielfeld.screenHeight/2));
	}
	
	@Override
	public void moveGameObject(double time) {
		transform.speed= transform.speed.multiply((1-(0.02*time)));//Luftwiderstand
    	setPosition(	getPosition().add(getSpeed().multiply(time))	);
    	setSpeed(getSpeed().add(new Vector2(0,-.13).multiply(time)));//Gravitation
    }
	
	@Override
	protected void paintMe(Graphics g) {//TODO bild für spieler
	    Vector2 jPos = transform.position.toJPanel();
	    
	    g.drawOval(
	        (int) jPos.x,
	        (int) (jPos.y - height), // nach oben verschieben
	        (int) width,
	        (int) height
	    );
	}
}
