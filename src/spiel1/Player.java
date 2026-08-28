package spiel1;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Player extends GameObject{
	
	private final Vector2 startSpeed;
	private boolean isSwinging;
	
	public Player(Transform transform, double width, double height){
		super(transform, width, height);//der Spieler wird fliegend erzeugt
		startSpeed=transform.speed;
	}
	
	public void reset() {
		setSpeed(startSpeed);//springt nach oben
		setPosition(new Vector2(10,Spielfeld.screenHeight/2));
		stopSwing();
	}
	
	public void stopSwing() {
		isSwinging = false;
	}
	public void startSwing() {
		isSwinging = true;
	}
	
	@Override
	public void moveGameObject(double time) {
		// temp if(transform.speed.length()<.12)transform.speed=new Vector2(0,0);//behandelt ein edge case hauptsächlich beim schwingen das die richtung sich nicht ändert ganz vllt temp wenn die schwungberechnung geändert wird
		
    	if(isSwinging == false) {
    		getTransform().speed= getTransform().speed.multiply((1-(0.02*time)));//normaler Luftwiderstand
			getTransform().speed= getTransform().speed.add(new Vector2(0,-.13).multiply(time));//Gravitation nur wenn nicht schwingt (beim schwingen seperat behandelt)
    	}else {
			getTransform().speed= getTransform().speed.multiply((1-(0.005*time)));//Luftwiderstand wird verringert wenn der spieler schwingt(fühlt sich besser an)
    	}
		setPosition(	getPosition().add(getSpeed().multiply(time))	);
    }
	
	@Override
	protected void paintMe(Graphics2D g2d) {//TODO bild für spieler
	    Vector2 jPos = getTransform().position.toJPanel();
	    
	    g2d.drawOval(
	        (int) (jPos.x()- width/2),
	        (int) (jPos.y() - height/2), // nach oben verschieben
	        (int) width,
	        (int) height
	    );
	}
}
