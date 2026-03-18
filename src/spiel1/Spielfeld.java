package spiel1;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.awt.FlowLayout;

import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Spielfeld extends JPanel implements MouseListener, TimeController, CameraController{ // JPanel ist eine Klasse, in der gezeichnet werden kann

	//jpanel
	private final Dimension prefSize = new Dimension(1920,1080);
	
	private int shaketimer=2;
	
	private int slowTimeRemaining;
	
	private Player player = new Player(new Transform(new Vector2(1000,250),0, new Vector2(40,30)), 10, 10);
	
	private ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	
	// Vector2 playerpos, Vector2 enemypos, Vector2 mauspos, double toleranzWinkel, double range, Enemy enemy, Graphics g //vllt temp
	
	public static boolean cooldown;
	private Sword sword;
	private Spear spear;
	private Grapplinghook grapple;
	private Staubsauger staubsauger;
	private SchwungSeil schwungSeil;
	
	private Weapon leftWeapon = null;//TODO die zuteilung
	private Weapon rightWeapon = null;
	
	private boolean isTimeSlowed;
	private double timeMultiplyer = 1;
	
	//camera für scrollen (links unten die ecke ist die kamerapos
	static Vector2 cameraPos= new Vector2(0,0);
	private double cameraPosMaxY;
	private double cameraPosMaxX;
	
	public static double screenHeight;//wird in SpielFenster gesetzt
	public static double screenWidth;
	
    /**
	 * Das soll eine gelbe unterringelung fixen
	 */
	private static final long serialVersionUID = 1L;

	//temp
	private int framecounter = 0;
	
	static int money = 1000;//vllt nicht static weil es so schon mitgegeben wird
	
	// Shop-Panel
    private final JPanel shopPanel = new JPanel();
    
	
	//Buttons
    Button startButton;
    private BufferedImage startButtonNeutral;
    private BufferedImage startButtonPressed;
    
    Button leftUpgradeButton;//TODO Bilder die sich nach ausgwähltem ändern
    Button rightUpgradeButton;

    
    Button swordButton;
    private BufferedImage swordButtonNeutral;
    private BufferedImage swordButtonPressed;
    
	Button spearButton;
    private BufferedImage spearButtonNeutral;
    private BufferedImage spearButtonPressed;

    Button grapplingButton;
    private BufferedImage grapplingButtonNeutral;
    private BufferedImage grapplingButtonPressed;
    
	Button staubsaugerButton;
    private BufferedImage staubsaugerButtonNeutral;
    private BufferedImage staubsaugerButtonPressed;
    
    Button schwungSeilButton;
    private BufferedImage schwungSeilButtonNeutral;
    private BufferedImage schwungSeilButtonPressed;
	
    private Button[] buttons = {swordButton, spearButton, grapplingButton, staubsaugerButton,schwungSeilButton};

	private String currentScreen= "spiel"; // aktueller Bildschirmstatus
    /*
    * start: Spiel ist gestoppt, wartet auf Doppelklick
    * spiel: Spiel läuft
    * gameOver: Spiel ist beendet, wartet auf doppelklick zum neustart
    * */

     
   

    private Timer t; // Timer, der in regelmäßigen Abständen die Methode doOnTick() aufruft

    private Cursor c; // Cursor-Objekt, um den Mauszeiger zu verändern

    public Spielfeld() {
        setFocusable(true);
        setPreferredSize(prefSize);
        setLayout(null);//test
        
        initGame(); // zum Erstellen der Oberfläche (Ausgangszustand)
        startGame(); // Starten des Timers. Dieser ruft die Methode doOnTick() auf, in der die
        // Veränderungen passieren.

    }
    @Override
    public boolean isTimeSlowed() {
    	return isTimeSlowed;
    }
    @Override
    public void slowTime() {
    	timeMultiplyer = .25;
    }
    @Override
    public void normalTime() {
    	timeMultiplyer = 1; 
    	slowTimeRemaining = 0;
    }
    @Override
    public void slowTimeFor(int millis){//TODO das es so rein und rausfaded
    	slowTimeRemaining = millis;
    	slowTime();
    	Timer t = new Timer(13, new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            slowTimeRemaining--;

	            if (slowTimeRemaining <= 0) {
	                normalTime();
	                ((Timer) e.getSource()).stop();
	            }
	        }
	    });

	    t.start();
    }
    
    
    
    private void initGame() {
    	
//    	//setLayout(new BorderLayout());
//    	// === Shop Panel ===
//        shopPanel.setLayout(new FlowLayout());
//        shopPanel.setBounds(0, 0, prefSize.width, prefSize.height);
//        shopPanel.setOpaque(false);
////
////        startButton.setPreferredSize(new Dimension(150, 50));
////        startButton.addActionListener(e -> resetGame());
////
////        shopPanel.add(startButton);
//        shopPanel.setVisible(false); // standardmäßig versteckt

    	/*
    	 * 			shop
    	 */
        try {
            URL neutralURL = getClass().getResource("/images/lanze_temp.png");//TODO Bilder einfuegen
            URL pressedURL = getClass().getResource("/images/schwert_temp.png");
            URL startNeutralURL = getClass().getResource("/images/start_temp.png");
            URL staubSaugerURL = getClass().getResource("/images/staubsauger_temp.png");
            
            if (neutralURL == null || pressedURL == null) {
                throw new RuntimeException("Button-Bilder nicht gefunden!");
            }

            startButtonNeutral = ImageIO.read(startNeutralURL);//TODO Bilder hinzufügen
            startButtonPressed = ImageIO.read(pressedURL);
            swordButtonPressed = ImageIO.read(neutralURL);
            swordButtonNeutral = ImageIO.read(pressedURL);
            spearButtonPressed = ImageIO.read(startNeutralURL);
            spearButtonNeutral = ImageIO.read(neutralURL);
            grapplingButtonPressed= ImageIO.read(pressedURL);
            grapplingButtonNeutral= ImageIO.read(pressedURL);
            staubsaugerButtonPressed= ImageIO.read(pressedURL);
            staubsaugerButtonNeutral= ImageIO.read(staubSaugerURL);
            schwungSeilButtonPressed= ImageIO.read(neutralURL); 
            schwungSeilButtonNeutral= ImageIO.read(pressedURL); 

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
		
		//==========	Button	==========
		startButton = new Button(new Vector2(prefSize.getWidth()/2,prefSize.getHeight()/5),200,20,startButtonPressed,startButtonNeutral);
		leftUpgradeButton = new Button(new Vector2(prefSize.getWidth()/3,prefSize.getHeight()/2),200,20,swordButtonPressed,swordButtonNeutral);
		rightUpgradeButton = new Button(new Vector2(prefSize.getWidth()/3*2,prefSize.getHeight()/2),200,20,spearButtonPressed,spearButtonNeutral);
		
		swordButton			= new Button(new Vector2(prefSize.getWidth()/30,prefSize.getHeight()/(buttons.length+2)*2),200,20,swordButtonPressed,swordButtonNeutral);
	    System.out.println(swordButton.getWorldPos()+" sword");
		spearButton			= new Button(new Vector2(prefSize.getWidth()/50,prefSize.getHeight()/(buttons.length+2)*3),200,20,spearButtonPressed,spearButtonNeutral);
	    grapplingButton		= new Button(new Vector2(prefSize.getWidth()/50,prefSize.getHeight()/(buttons.length+2)*4),200,20,grapplingButtonPressed,grapplingButtonNeutral);
	    staubsaugerButton	= new Button(new Vector2(prefSize.getWidth()/50,prefSize.getHeight()/(buttons.length+2)*5),200,20,staubsaugerButtonPressed,staubsaugerButtonNeutral);
		schwungSeilButton	= new Button(new Vector2(prefSize.getWidth()/50,prefSize.getHeight()/(buttons.length+2)*6),200,20,staubsaugerButtonPressed,staubsaugerButtonNeutral);

    	
        
        // Maus-Events (z.B. Klick) werden registriert und verarbeitet
        addMouseListener(this);
        
        // Mauszeiger wird zu Fadenkreuz
        c = new Cursor(Cursor.CROSSHAIR_CURSOR); // erzeugen eines Cursor-Objektes
        this.setCursor(c); // setCursor ist eine Methode der JPanel Klasse
        
        
        spear=new Spear(player.transform);
        sword=new Sword(player.transform);
        grapple = new Grapplinghook(player.transform, this, this);//this ist der timecontroller/cameracontroller
        staubsauger = new Staubsauger(player.transform);
        leftWeapon=sword;
        rightWeapon=grapple;
        
        //im shop starten
        currentScreen="shop";
        
        // Erzeugen eines Timers
        t = new Timer(13, new ActionListener() { // Timer, der alle timeSpeed ms die Methode doOnTick() aufruft normal ~60fps
            public void actionPerformed(ActionEvent e) {
                doOnTick();
            }
        });
       
        
        
    }
    
    private void resetGame() {
    	System.out.println("spielreset");
    	enemies.clear();
    	player.reset();
    	currentScreen = "spiel";
    	cooldown = false;
    }
    
    
    private void startGame() {
        t.start(); // Timer wird gestartet
    }

    public void pauseGame() {
        t.stop(); // Timer wird pausiert
    }

    public void continueGame() {
        if (currentScreen == "spiel") { // Spiel läuft nur weiter, wenn es nicht gestoppt ist, sondern pausiert wurde
            t.start();
        }
    }

    // Diese Methode wird in regelmäßigen Abständen vom Timer aufgerufen und sorgt für ein Spiel update
    private void doOnTick() {
    	
    	
    	
    	//TODO theoretisch nur bei änderung nötig 
    	screenHeight = this.getHeight();
    	screenWidth = this.getWidth();
    	
    	
    	
        if (currentScreen == "spiel") { // Spiel läuft
        	//Player
        	player.moveGameObject( timeMultiplyer);
        	System.out.println(screenHeight/2 +" screm , camerapops "+cameraPos.y/2);
        	cameraPos=cameraPos.lerp(player.transform.position.subtract(new Vector2(screenWidth/2,screenHeight/2)),0.1);//camera smooth folgen lassen
        	
        	if(cameraPos.x>cameraPosMaxX) {//neues Terrain in x - richtung
        		cameraPosMaxX=cameraPos.x;
        		
        		if(cameraPosMaxX % 20==0) {//gegner in x-richtung spawnen
            		//enemies.add(new Enemy(new Transform(new Vector2(Math.random()*this.getWidth(),Math.random()*this.getHeight())),20,20,5));//temp das darunter ist richtig //für debugen new Enemy(new Vector2(600,660),20,20,5));//
            		enemies.add(new Enemy(new Transform(new Vector2(this.getWidth()+cameraPos.x, (Math.random()*this.getHeight()) +cameraPos.y)),20,20,5));//spawnt gegner auf einer random höhe am rechten randgröse:20 20, 5leben
            	}
        	}
        	if(cameraPos.y>cameraPosMaxY) {//neues Terrain in y - richtung
        		cameraPosMaxY=cameraPos.y;
        		
        		if(cameraPosMaxY % 20==0) {//gegner in y-richtung spawnen
            		//enemies.add(new Enemy(new Transform(new Vector2(Math.random()*this.getWidth(),Math.random()*this.getHeight())),20,20,5));//temp das darunter ist richtig //für debugen new Enemy(new Vector2(600,660),20,20,5));//
            		enemies.add(new Enemy(new Transform(new Vector2(Math.random()* this.getWidth()+cameraPos.x,this.getHeight()+cameraPos.y)),20,20,5));//spawnt gegner auf einer random höhe am rechten randgröse:20 20, 5leben
            	}
        	}
        	
        	//enemy zeug
        	framecounter++;//TODO das soll mit einer zahl ersetzt werden die sich beim scrollen erhöht
        	if(framecounter % 20==0) {
        		//enemies.add(new Enemy(new Transform(new Vector2(Math.random()*this.getWidth(),Math.random()*this.getHeight())),20,20,5));//temp das darunter ist richtig //für debugen new Enemy(new Vector2(600,660),20,20,5));//
        		enemies.add(new Enemy(new Transform(new Vector2(this.getWidth()+cameraPos.x,Math.random()*(this.getHeight()+cameraPos.x))),20,20,5));//spawnt gegner auf einer random höhe am rechten randgröse:20 20, 5leben
        	}
        	
        	for(Enemy enemy : enemies) enemy.moveGameObject(timeMultiplyer);
        	enemies.removeIf(Enemy::isDead);
        	
        	
        //wenn spieler nach unten fällt
           if(player.getPosition().y<0)	currentScreen="shop";
        }
        if (currentScreen == "shop") {
        	cameraPos=new Vector2(0,0);
        }
        
       
        
        repaint(); // ruft die paintComponent Methode auf, um das Spielfeld neu zu zeichnen
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g); // löscht das Spielfeld
        
        

        Graphics2D g2d = (Graphics2D) g;
        // Um die Kanten des Objekts zu glätten
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if(currentScreen == "spiel") { // Spiel läuft
        	Vector2 bodenLinks = new Vector2(0,0).toJPanel();
        	Vector2 bodenRechts = new Vector2(99999999,0).toJPanel();
        	//Boden
        	g2d.setColor(Color.green);
        	g2d.setStroke(new BasicStroke(5.0f));
        	g2d.drawLine((int) bodenLinks.x, (int) bodenLinks.y, (int) bodenRechts.x, (int) bodenRechts.y);
        	g2d.setStroke(new BasicStroke());
        	g2d.setColor(Color.black);
        	//Player
        	player.paintMe(g);
        	
        	//Weapons
        	if(leftWeapon != null)leftWeapon.paintMe(g);
        	else System.out.println("leftweapon ist" + leftWeapon);
//        	leftWeapon.getHitbox().paintMe(g);
        	if(leftWeapon != null) {rightWeapon.paintMe(g);
        	rightWeapon.getHitbox().paintMe(g);
        	}
        	else System.out.println("rightweapom ist" + rightWeapon);
        	
        	// Enemy(Vector2 objectPosition, double width, double height, int health){
        	for(Enemy enemy: enemies)
        	{
        		enemy.paintMe(g);
        		enemy.getHitbox().paintMe(g);
        	}
        	
        }
        
        if(currentScreen.equals("shop")){
        	startButton.paintMe(g);
        	
        	leftUpgradeButton.paintMe(g);
        	rightUpgradeButton.paintMe(g);
        	
        	//for(Button button : buttons) button.paintMe(g);
        	swordButton.paintMe(g);
        	spearButton.paintMe(g);
        	grapplingButton.paintMe(g);
        	staubsaugerButton.paintMe(g);   	
        	schwungSeilButton.paintMe(g);
        }
    }
    // Diese Methoden müssen implementiert werden, da die Klasse das MouseListener Interface implementiert
    // Es werden nur die Methoden genutzt, die benötigt werden. Die anderen bleiben leer.
    @Override
    public void mouseClicked(MouseEvent arg0) {}

    @Override
    public void mouseEntered(MouseEvent arg0) {}

    @Override
    public void mouseExited(MouseEvent arg0) {}

    @Override
    public void mousePressed(MouseEvent arg0) {
    	Vector2 mouseScreenPos = new Vector2(
    		    arg0.getX(),
    		    arg0.getY()
    		).toCoordinate();
    	System.out.println(mouseScreenPos);
    	if(arg0.getButton()==3) //rechtsklick
		{
			
			rightWeapon.hit(mouseScreenPos, enemies ,new WeaponHitListener() {
				
				@Override
				public void onHit(Vector2 knockback) {
					player.addSpeed(knockback);
					System.out.println(knockback+"knockback");
				}

				@Override
				public void onMiss() {}
			});
		}
		if(arg0.getButton()==1) //linksklick
		{
			leftWeapon.hit(mouseScreenPos, enemies ,new WeaponHitListener() {

				@Override
				public void onHit(Vector2 knockback) {
					player.addSpeed(knockback);
				}

				@Override
				public void onMiss() {}
			});
		}
    	
    	if(currentScreen.equals("shop")) {
    		
    		
    		
    		if(startButton.press(mouseScreenPos)) {
    			resetGame();
    		}
    		
    		if(leftUpgradeButton.press(mouseScreenPos)) {
    			leftWeapon.levelUp(money);
    		}
    		
    		if(rightUpgradeButton.press(mouseScreenPos)) {
    			rightWeapon.levelUp(money);
    		}
    		
    		if(arg0.getButton()==1) { //linksklick
    			if(swordButton.press(mouseScreenPos)) {//TODO Fehler: man muss zweimal drücken vllt noch ein fehler mit auswählen
    				leftUpgradeButton.setneutral(swordButton.getneutral());
    				leftUpgradeButton.setpressed(swordButton.getpressed());
    				leftWeapon=sword;
    			}
    			if(spearButton.press(mouseScreenPos)) {
    				leftUpgradeButton.setneutral(spearButton.getneutral());
    				leftUpgradeButton.setpressed(spearButton.getpressed());
    				leftWeapon = spear;
    			}
    			if(grapplingButton.press(mouseScreenPos)) {
    				leftUpgradeButton.setneutral(grapplingButton.getneutral());
    				leftUpgradeButton.setpressed(grapplingButton.getpressed());
    				leftWeapon = grapple;
    			}
    			if(staubsaugerButton.press(mouseScreenPos)) {
    				leftUpgradeButton.setneutral(staubsaugerButton.getneutral());
    				leftUpgradeButton.setpressed(staubsaugerButton.getpressed());
    				leftWeapon = staubsauger;
    			}
    			if(schwungSeilButton.press(mouseScreenPos)) {
    				leftUpgradeButton.setneutral(schwungSeilButton.getneutral());
    				leftUpgradeButton.setpressed(schwungSeilButton.getpressed());
    				leftWeapon = schwungSeil;
    			}
    		}
    		if(arg0.getButton()==3) { //rechtsklick
    			if(swordButton.press(mouseScreenPos)) {
    				rightUpgradeButton.setneutral(swordButton.getneutral());
    				rightUpgradeButton.setpressed(swordButton.getpressed());
    				rightWeapon=sword;
    				System.out.println("sword right");
    			}
    			if(spearButton.press(mouseScreenPos)) {
    				rightUpgradeButton.setneutral(spearButton.getneutral());
    				rightUpgradeButton.setpressed(spearButton.getpressed());
    				rightWeapon = spear;
    				System.out.println("spear right");
    			}
    			if(grapplingButton.press(mouseScreenPos)) {
    				rightUpgradeButton.setneutral(grapplingButton.getneutral());
    				rightUpgradeButton.setpressed(grapplingButton.getpressed());
    				rightWeapon = grapple;
    			}
    			if(staubsaugerButton.press(mouseScreenPos)) {
    				rightUpgradeButton.setneutral(staubsaugerButton.getneutral());
    				rightUpgradeButton.setpressed(staubsaugerButton.getpressed());
    				rightWeapon = staubsauger;
    			}
    			if(schwungSeilButton.press(mouseScreenPos)) {
    				rightUpgradeButton.setneutral(schwungSeilButton.getneutral());
    				rightUpgradeButton.setpressed(schwungSeilButton.getpressed());
    				rightWeapon = schwungSeil;
    			}
    		}
    	}
    	
    }

    @Override
    public void mouseReleased(MouseEvent arg0){ // wird aufgerufen, wenn die Maustaste losgelassen wird
    	if(arg0.getButton()==3) //rechtsklick
		{
			rightWeapon.hitReleased();
		}
		if(arg0.getButton()==1) { //linksklick
			leftWeapon.hitReleased();
		}
    }
	@Override
	public void shake() {
		
		Timer t = new Timer(13, new ActionListener() {//schiesst über längere zeit
			@Override
	        public void actionPerformed(ActionEvent e) {
				shaketimer--;
			if(currentScreen=="spiel"){
				Vector2 randompos = new Vector2((Math.random()*50)-25,(Math.random()*50)-25);
					System.out.println("shake"+randompos);
					cameraPos = cameraPos.add(randompos);
			}
		            if (shaketimer <= 0) {//reset wenn timer ausgelaufen oder getroffen
		            	shaketimer=2;
		                ((Timer) e.getSource()).stop();
		            }
				}
	   	    });
		t.start();
		
	}
	//TODO spielführung also ein tutorial das man lernt was man machen muss
	//TODO gut komentierter quellcode
	/*allgemeiner überblick
	 * mindestens ein klassendiagramm
	 * überblick über die wichtigsten klassen
	 * 3oder4 wesentliche stellen genauer beschreiben
	 * es heißt 10-15 seiten, aber realsitisch mehr. unter 30 bleiben.
	 */

}//falls lag vllt zu viele timer und man sollte einen globalen machen

