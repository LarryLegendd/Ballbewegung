package spiel1;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.Timer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.RescaleOp;

public class Button extends GameObject {
	private BufferedImage currentImage;
    private BufferedImage pressedImage;
    private BufferedImage neutralImage;
    
    private Vector2 lastpos;//temp

    public Button(Vector2 midpoint, double width, double height, BufferedImage pressed, BufferedImage neutral) {
        super(new Transform(midpoint.add(new Vector2(-width/2, -height/2))), width, height);
        System.out.println(transform.position);
        this.pressedImage = pressed;
        this.neutralImage = neutral;
        this.currentImage = neutral;
    }

    public Button(Vector2 midpoint, double width, double height, BufferedImage neutral) {
        super(new Transform(midpoint.add(new Vector2(-width/2, -height/2))), width, height);
        System.out.println(transform.position);
        this.pressedImage = darkenImage(neutral, 0.5f);
        this.neutralImage = neutral;
        this.currentImage = neutral;
    }

    private BufferedImage darkenImage(BufferedImage image, float alpha) {
        RescaleOp op = new RescaleOp(alpha, 0f, null);
        return op.filter(image, null);
    }
    
    public BufferedImage getpressed() {
    	return pressedImage;
    }
    public BufferedImage getneutral() {
    	return neutralImage;
    }
    
    public Vector2 getWorldPos(){
    	return super.transform.position;
    }
    
    public void setpressed(BufferedImage pressed) {
    	pressedImage = pressed;
    }
    public void setneutral(BufferedImage neutral) {
    	neutralImage = neutral;
    }
    
    public boolean press(Vector2 mouseWorld) {//timer starten TODO das der knopf länger in dem stadium ist
    	
    	lastpos = transform.position;
    	
    	
    	
    	if(getHitbox().collides(mouseWorld.x,mouseWorld.y)) {
    		currentImage = pressedImage;
    		buttonSpringBack(200);
    		return true;
    	}else {
    		currentImage = neutralImage;
    		return false;
    	}
    }
    
    public void buttonSpringBack(int timeInMs) {
    	Timer timer = new Timer(timeInMs, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				currentImage = neutralImage;
				((Timer)e.getSource()).stop(); // Timer stoppen
			}
		});
        timer.setRepeats(false); // nur einmal ausführen
        timer.start();
    }
    
    @Override
    public void paintMe(Graphics g) {
    	
    	Vector2 jPos = transform.position.toJPanel();
    	Vector2 test = new Vector2(0 , 0);
    	Vector2 test1 = new Vector2(100 , 100);
//    	System.out.println(test1.toJPanel(abhaengig).y);
    	
    	this.getHitbox().draw(true);
    	
    	g.drawLine( (int) test.toJPanel().x, (int) test.toJPanel().y, (int) test1.toJPanel().x, (int) test1.toJPanel().y);
    	//g.drawLine( 0,0,100,100);
        g.drawImage(currentImage, (int) jPos.x, (int) jPos.y,
    		   (int) (jPos.x+getWidth()), (int)(jPos.y+getHeight()), 
    		   		
    		   		0, 0, currentImage.getWidth(), currentImage.getHeight(), null);
       //img - the specified image to be drawn. This method does nothing if img is null.
//       dx1 - the x coordinate of the first corner of the destination rectangle.
//       dy1 - the y coordinate of the first corner of the destination rectangle.
       
//       dx2 - the x coordinate of the second corner of the destination rectangle.
//       dy2 - the y coordinate of the second corner of the destination rectangle.
//       sx1 - the x coordinate of the first corner of the source rectangle.
//       sy1 - the y coordinate of the first corner of the source rectangle.
//       sx2 - the x coordinate of the second corner of the source rectangle.
//       sy2 - the y coordinate of the second corner of the source rectangle.
//       observer - object to be notified as more of the image is scaled and converted.
        
        //if(lastpos!=null)lastpos.draw(g, Color.pink);//Temp
       
    }
}