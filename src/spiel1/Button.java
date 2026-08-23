package spiel1;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.Timer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.RescaleOp;

public class Button extends GameObject {    // Eine Klasse mit der sich knöpfe erstellen lassen
	private BufferedImage currentImage; // die bilder für die Knöpfe
    private BufferedImage pressedImage;
    private BufferedImage neutralImage;

    public Button(Vector2 midpoint, double width, double height, BufferedImage pressed, BufferedImage neutral) { // Erstellen des Knopfes mit zwei Bildern
        super(new Transform(midpoint.add(new Vector2(-width/2, -height/2))), width, height); //Erstellt das Gameobjekt des Buttons. Die berechnung macht den mittelpunkt zum punkt links oben
        this.pressedImage = pressed;
        this.neutralImage = neutral;
        this.currentImage = neutral;
    }

    public Button(Vector2 midpoint, double width, double height, BufferedImage neutral) { // Macht das gedrückte bild automatisch dunkler
        super(new Transform(midpoint.add(new Vector2(-width/2, -height/2))), width, height);//Erstellt das Gameobjekt des Buttons. Die berechnung macht den mittelpunkt zum punkt links oben
        this.pressedImage = darkenImage(neutral, 0.5f);
        this.neutralImage = neutral;
        this.currentImage = neutral;
    }

    private BufferedImage darkenImage(BufferedImage image, float helligkeit) {
        RescaleOp op = new RescaleOp(helligkeit, 0f, null); // Erstellt ein RescaleOp, das ist eine art filter für das bild das es vergrößern und die helligkeit ändern kann. Quelle: das was da steht wenn man drüber hovert. das heisst glaube ich offizielle java dokumentation
        return op.filter(image, null);//nutzt die filtermethode um den verdunklungsfilter anzuwenden
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
    public void setneutral(BufferedImage neutral) { neutralImage = neutral; }
    
    public boolean press(Vector2 mouseWorld) { // die methode die aufgerufen um zu prüfen Knopf gedrückt wird

    	if(getHitbox().collides(mouseWorld.x,mouseWorld.y)) {// fragt in der Hitbox ab ob der button getroffen wird
    		currentImage = pressedImage;
    		buttonSpringBack(200);// setzt in 200 ms den knopf zurück
    		return true;
    	}else {
    		currentImage = neutralImage;
    		return false;
    	}
    }
    
    public void buttonSpringBack(int timeInMs) { //erstellt einen timer um das bild des knopfes zurückzusetzen
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
    public void moveGameObject(double time){//für buttonjump
        transform.speed= transform.speed.multiply((1-(0.02*time)));//Luftwiderstand
        transform.speed=transform.speed.add(new Vector2(0,-.13).multiply(time));//Gravitation
        transform.position=transform.position.add(transform.speed.multiply(time));
        //setPosition(	getPosition().add(getSpeed().multiply(time))	); // TODO Beispiel für Seminararbeit vllt
    }

    @Override
    public void paintMe(Graphics g) {
    	
    	Vector2 jPos = transform.position.toJPanel(); //wandelt die position in das von JPanel benutzte format um


        g.drawImage(currentImage, (int) jPos.x, (int) jPos.y, //position ecke links oben
                (int) (jPos.x+getWidth()), (int)(jPos.y+getHeight()),//position ecke rechts unten (das sieht falsch aus aber man muss height addieren weil JPanel -y nach oben hat)
                0, 0,//erste ecke vom bild, eine ecke vom bild ist immer 0 0
                currentImage.getWidth(), currentImage.getHeight(),// zweite ecke vom bild ist die 0+Breite, 0+Höhe
                null);// kein imageobserver

       
    }
}