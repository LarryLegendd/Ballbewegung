package spiel1;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.Timer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.RescaleOp;

public class Button extends GameObject {    // Eine Klasse mit der sich knöpfe erstellen lassen
    private String ausgabe;

		// die bilder für die Knöpfe
			private BufferedImage currentImage; 
    private BufferedImage pressedImage;
    private BufferedImage neutralImage;

//	Visuelle Drehung der Knöpfe
    private double visualAngle;
		
//  Text, der für die Erklärung verwendet wird  
		 private String ausgabe;
/**	Konstruktor für normale knöpfe
*/
public Button(Vector2 midpoint, double width, double height, BufferedImage pressed, BufferedImage neutral) { // Erstellen des Knopfes mit zwei Bildern
        super(new Transform(midpoint.add(new Vector2(-width/2, -height/2))), width, height); //Erstellt das Gameobjekt des Buttons. Die berechnung macht den mittelpunkt zum punkt links oben
//				Bilder für die Knöpfe setzen        
					this.pressedImage = pressed;
        this.neutralImage = neutral;
        this.currentImage = neutral;
        ausgabe = "";//keine Augabe
    }
		
// Konstruktor Overloading https://stackoverflow.com/questions/1182153/constructor-overloading-in-java-best-practice
/**	Konstruktor für knöpfe mit Text (für die Erklärung)
*/
    public Button(Vector2 midpoint, double width, double height, BufferedImage pressed, BufferedImage neutral, String ausgabe) { // Erstellen des Knopfes mit zwei Bildern
        super(new Transform(midpoint.add(new Vector2(-width/2, -height/2))), width, height); //Erstellt das Gameobjekt des Buttons. Die berechnung macht den mittelpunkt zum punkt links oben
        this.pressedImage = pressed;
        this.neutralImage = neutral;
        this.currentImage = neutral;
        this.ausgabe = ausgabe;
    }

			/**Knöpfe mit nur einem Bild. das gedrückte ist das gleiche in dunkler
			*/
    public Button(Vector2 midpoint, double width, double height, BufferedImage neutral) { // Macht das gedrückte bild automatisch dunkler
        super(new Transform(midpoint.add(new Vector2(-width/2, -height/2))), width, height);//Erstellt das Gameobjekt des Buttons. Die berechnung macht den mittelpunkt zum punkt links oben
        this.pressedImage = darkenImage(neutral, 0.5f);
        this.neutralImage = neutral;
        this.currentImage = neutral;
        ausgabe = "";
    }

    private BufferedImage darkenImage(BufferedImage image, float helligkeit) {
        RescaleOp op = new RescaleOp(helligkeit, 0f, null); // Erstellt ein RescaleOp, das ist eine art filter für das bild das es vergrößern und die helligkeit ändern kann. Quelle: das was da steht wenn man drüber hovert. offizielle java dokumentation
        return op.filter(image, null);//nutzt die filtermethode um den verdunklungsfilter anzuwenden
    }
    
    public BufferedImage getpressed() {
    	return pressedImage;
    }
    public BufferedImage getneutral() {
    	return neutralImage;
    }
    
    public Vector2 getWorldPos(){
    	return getTransform().position;
    }
    
    public void setpressed(BufferedImage pressed) {
    	pressedImage = pressed;
    }
    public void setneutral(BufferedImage neutral) { neutralImage = neutral; }

    /**
     * setzt das aktuelle bild auf das neutrale bild
     */
    public void refresh(){
        currentImage=neutralImage;
    }

		/**Methode die zurückgibst ob ein Punkt den Knopf drückt. Wenn der Knopf getroffen wird, verändert sich sein Bild
*/
    public boolean press(Vector2 mouseWorld) { // die methode die aufgerufen um zu prüfen Knopf gedrückt wird

    		if(getHitbox().collides(mouseWorld.x(),mouseWorld.y())) {// fragt in der Hitbox ab ob der button getroffen wird
    			currentImage = pressedImage;
    			buttonSpringBack(200);// setzt in 200 ms den knopf zurück
    			return true;
    		}else {
    			currentImage = neutralImage;
    			return false;
    		}
    }
    /**erstellt einen Timer um das Bild des Knopfes zurückzusetzen
*/
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
			/** lässt den Knopf fallen, um den Knopf am Endbildschirm springen zu lassen 
*/
    @Override
    public void moveGameObject(double time){//für buttonjump
        getTransform().speed= getTransform().speed.multiply((1-(0.02*time)));//Luftwiderstand
        getTransform().speed=getTransform().speed.add(new Vector2(0,-.13).multiply(time));//Gravitation
        getTransform().position = getTransform().position.add(getTransform().speed.multiply(time));//position verändern
    }

    /**dreht den Knopf visuell, verändert die hitbox aber nicht
     *
     */
    public void rotate(double angle){
        visualAngle=angle;
    }

    public String getAusgabe(){
        return ausgabe;
    }

    @Override
    public void paintMe(Graphics2D g2d) {
        g2d.translate(getPosition().addX(width/2).toJPanel().x(),getPosition().addY(height/2).toJPanel().y());//um die mitte drehen
        g2d.rotate(-visualAngle);
        g2d.translate(-getPosition().addX(width/2).toJPanel().x(),-getPosition().addY(height/2).toJPanel().y());
    	Vector2 jPos = getTransform().position.toJPanel(); //wandelt die position in das von JPanel benutzte format um

        g2d.drawImage(currentImage, (int) jPos.x(), (int) jPos.y(), //position ecke links oben
                (int) (jPos.x()+getWidth()), (int)(jPos.y()+getHeight()),//position ecke rechts unten (das sieht falsch aus aber man muss height addieren weil JPanel -y nach oben hat)
                0, 0,//erste ecke vom bild, eine ecke vom bild ist immer 0 0
                currentImage.getWidth(), currentImage.getHeight(),// zweite ecke vom bild ist die 0+Breite, 0+Höhe
                null);// kein imageobserver

        g2d.translate(getPosition().addX(width/2).toJPanel().x(),getPosition().addY(height/2).toJPanel().y());// wieder zurückdrehen
        g2d.rotate(visualAngle);
        g2d.translate(-getPosition().addX(width/2).toJPanel().x(),-getPosition().addY(height/2).toJPanel().y());
    }
}