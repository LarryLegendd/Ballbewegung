package spiel1;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.RadialGradientPaint;

//TODO Button bilder bei einem klick fixen
public class Spielfeld extends JPanel implements MouseListener, TimeController, CameraController{ // JPanel ist eine Klasse, in der gezeichnet werden kann

	//jpanel
	private final Dimension prefSize = new Dimension(1920,1080);
	
	private int shaketimer=2;
	
	private int slowTimeRemaining;
	Timer slowingTimer = new Timer(13, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {


			if (timeMultiplyer <= .25) {
				((Timer) e.getSource()).stop();
			}
			timeMultiplyer-=.05;
			System.out.println("time"+timeMultiplyer);
		}
	});

	Timer speedUpTimer = new Timer(13, new ActionListener() {//TODO das prozentual machen
		@Override
		public void actionPerformed(ActionEvent e) {

			timeMultiplyer+=.1;

			if (timeMultiplyer >= 1) {
				timeMultiplyer=1;// da es wegen floating point error größer ist
				((Timer) e.getSource()).stop();
			}


		}
	});


	private Player player = new Player(new Transform(new Vector2(1000,250),0, new Vector2(40,30)), 10, 10);
	private boolean drawPlayer;

	private EnemyArea[]	currentArea = new EnemyArea[2];
	static int AreaWidth = 5000;
	static int AreaHeight = 4000;

	private  ArrayList<EnemyArea> Areas = new ArrayList<>();

		// Vector2 playerpos, Vector2 enemypos, Vector2 mauspos, double toleranzWinkel, double range, Enemy enemy, Graphics g //vllt temp
	
	public static boolean cooldown;
	
	private Sword sword;
	private Spear spear;
	private Grapplinghook grapple;
	private Staubsauger staubsauger;
	private SchwungSeil schwungSeil;
	private Weapon[] weapons;
	
	private Weapon leftWeapon = null;
	private Weapon rightWeapon = null;


	private static double timeMultiplyer = 1;
	
	//camera für scrollen (links unten die ecke ist die kamerapos
	static Vector2 cameraPos= new Vector2(0,0);
	private double cameraPosMaxY;
	private double cameraPosMaxX;
	
	public static double screenHeight;//wird in SpielFenster gesetzt
	public static double screenWidth;

	//temp
	private int framecounter = 0;
	
	static int money = 1000;//vllt nicht static weil es so schon mitgegeben wird
    
	
	//Buttons
    Button startButton;
    private BufferedImage startButtonNeutral;
    private BufferedImage startButtonPressed;
    
    Button leftUpgradeButton;
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

	boolean isEnded= false;

    private int score=0;
    private int highscore=0;

	// definition vom rechteck das runterfällt.
	private Vector2 endscreenPanelfinaltopLeft;
	private Vector2 endscreenPaneltopLeft;
	private Vector2 endscreenSpeed;
	int framesUntillScreenIsCentered;
	private Vector2 endscreenSize = new Vector2(screenWidth/2,screenHeight/4*3);//	Vector 2 wird nur gebraucht
	// 																				um die beiden informationen
	// 																				gleichtzeitig zu benutzen
	private Vector2 endscreenTextPos;
	private Button endscreenShopButton;
	private Vector2 endscreenShopButtonPos;
	private boolean endscreenPressed= false;//fixt fehler beim zweimal drücken


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
		if(timeMultiplyer<1)return true;
		return false;
    }
    @Override
    public void slowTime() {
		speedUpTimer.stop();
		slowingTimer.start();
    }
    @Override
    public void normalTime() {
		slowingTimer.stop();
		speedUpTimer.start();
    	slowTimeRemaining = 0;
    }
	public static double getTimeMultiplyer() {
		return timeMultiplyer;
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
    
    
    @Override
    public double getTimeSpeed() {
    	return timeMultiplyer;
    }
    
    
    private void initGame() {
    	/*
    	 * 			shop
    	 */
        try {
        	URL neutralURL = getClass().getResource("/images/lanze_temp.png");//TODO Bilder einfuegen
            URL pressedURL = getClass().getResource("/images/schwert_temp.png");
            URL startNeutralURL = getClass().getResource("/images/start_temp.png");
            URL staubSaugerURL = getClass().getResource("/images/staubsauger_temp.png");
            URL seilURL = getClass().getResource("/images/Seil_temp.png");
            URL grapplingURL = getClass().getResource("/images/grappling_hook.png");
            
            if (neutralURL == null || pressedURL == null) {
                throw new RuntimeException("Button-Bilder nicht gefunden!");
            }

            startButtonPressed = ImageIO.read(pressedURL);
            startButtonNeutral = ImageIO.read(startNeutralURL);//TODO Bilder hinzufügen

            swordButtonPressed = ImageIO.read(neutralURL);
            swordButtonNeutral = ImageIO.read(pressedURL);

            spearButtonPressed = ImageIO.read(pressedURL);
            spearButtonNeutral = ImageIO.read(neutralURL);

            grapplingButtonPressed= ImageIO.read(pressedURL);
            grapplingButtonNeutral= ImageIO.read(grapplingURL);

            staubsaugerButtonPressed= ImageIO.read(pressedURL);
            staubsaugerButtonNeutral= ImageIO.read(staubSaugerURL);

            schwungSeilButtonPressed= ImageIO.read(pressedURL);
            schwungSeilButtonNeutral= ImageIO.read(seilURL);

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
		
		//==========	Button	==========
		startButton = new Button(new Vector2(prefSize.getWidth()/2,prefSize.getHeight()/5),200,20,startButtonPressed,startButtonNeutral);
		leftUpgradeButton = new Button(new Vector2(prefSize.getWidth()/3,prefSize.getHeight()/2),200,20,swordButtonPressed,swordButtonNeutral);
		rightUpgradeButton = new Button(new Vector2(prefSize.getWidth()/3*2,prefSize.getHeight()/2),200,20,spearButtonPressed,spearButtonNeutral);
		
		swordButton			= new Button(new Vector2(prefSize.getWidth()/50,prefSize.getHeight()/(buttons.length+2)*2),200,20,swordButtonPressed,swordButtonNeutral);
		spearButton			= new Button(new Vector2(prefSize.getWidth()/50,prefSize.getHeight()/(buttons.length+2)*3),200,20,spearButtonPressed,spearButtonNeutral);
	    grapplingButton		= new Button(new Vector2(prefSize.getWidth()/50,prefSize.getHeight()/(buttons.length+2)*4),200,20,grapplingButtonPressed,grapplingButtonNeutral);
	    staubsaugerButton	= new Button(new Vector2(prefSize.getWidth()/50,prefSize.getHeight()/(buttons.length+2)*5),200,20,staubsaugerButtonPressed,staubsaugerButtonNeutral);
		schwungSeilButton	= new Button(new Vector2(prefSize.getWidth()/50,prefSize.getHeight()/(buttons.length+2)*6),200,20,schwungSeilButtonPressed,schwungSeilButtonNeutral);

		endscreenShopButton	= new Button(new Vector2(prefSize.getWidth()/50,prefSize.getHeight()/(buttons.length+2)*6),200,20,schwungSeilButtonPressed,schwungSeilButtonNeutral);


		for(int i = 0;i<2;i++) {
			Areas.add(new EnemyArea(i));
			Areas.get(i).GenerateEnemies();
		}
        
        // Maus-Events (z.B. Klick) werden registriert und verarbeitet
        addMouseListener(this);
        
        // Mauszeiger wird zu Fadenkreuz
        c = new Cursor(Cursor.CROSSHAIR_CURSOR); // erzeugen eines Cursor-Objektes
        this.setCursor(c); // setCursor ist eine Methode der JPanel Klasse
        
        
        spear=new Spear(player.transform);
        sword=new Sword(player.transform);
        grapple = new Grapplinghook(player.transform, this, this);//this ist der timecontroller/cameracontroller
        staubsauger = new Staubsauger(player.transform);
        schwungSeil = new SchwungSeil(player.transform, player, this, this);
        weapons = new Weapon[]{
    			sword,
    			spear,
    			grapple,
    			staubsauger,
    			schwungSeil};
        
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


        for(int i = 0;i<2;i++) {
			currentArea[i]=Areas.get(i);
		}
    }
    
    private void resetGame() {
    	System.out.println("spielreset");

    	player.reset();
		drawPlayer=true;
    	currentScreen = "spiel";
		normalTime();

    	for(int i = 0; i < weapons.length; i++) if(weapons[i]!= null) {
    		weapons[i].reset();
    		System.out.println("waffe "+i+" geresettet");
    	}else System.out.println("waffe "+i+" ist null");//TODO cooldown bug wenn man mit cooldown stirbt kann man nicht mehr schiessen(glaube ich) aber ist nicht replezierbar

		//Gegner initialisieren & Löschen
		Areas.clear();

		for(int i = 0;i<2;i++) {
			Areas.add(new EnemyArea(i));
			Areas.get(i).GenerateEnemies();
		}

		currentArea[0] = Areas.get(0);
		currentArea[1] = Areas.get(1);

		isEnded=false;

		/*
    	//cooldown
    	for(Weapon weapon: weapons)weapon.stopCooldownTimer();
    	cooldown=true;
    	System.out.println(cooldown);
    	t = new Timer(13, new ActionListener() {
    		@Override
		    public void actionPerformed(ActionEvent e) {//für einen frame cooldown, dass der startklick kein hit ist //temp  TODO das funktioniert nicht
    			int i=0;
    			System.out.println(cooldown);
    		if(i>1) {
    			cooldown = false;
    			t.stop();
    		}
    		i++;
    		}});
    	t.start();
    	*/
    }
    
    
    private void startGame() {
        t.start(); // Timer wird gestartet
    }

    public void pauseGame() {
        t.stop(); // Timer wird pausiert
    }

    public void continueGame() {
        if (currentScreen.equals("spiel")) { // Spiel läuft nur weiter, wenn es nicht gestoppt ist, sondern pausiert wurde
            t.start();
        }
    }

    // Diese Methode wird in regelmäßigen Abständen vom Timer aufgerufen und sorgt für ein Spiel update
    private void doOnTick() {

    	screenHeight = this.getHeight();
    	screenWidth = this.getWidth();
    	
    	
    	
        if (currentScreen == "spiel") { // Spiel läuft
        	//Player
        	player.moveGameObject( timeMultiplyer);
        	cameraPos=cameraPos.lerp(player.transform.position.subtract(new Vector2(screenWidth/2,screenHeight/2)),0.1);//camera smooth folgen lassen



			int currScreenNumber =(int)player.getPosition().x/AreaWidth;//TODO irgendwann kommt nichts mehr

			if(currScreenNumber+1>Areas.toArray().length-1){//immer vorausgenerieren
				Areas.add(new EnemyArea(currScreenNumber+1));
				Areas.get(currScreenNumber+1).GenerateEnemies();
			}

			if(currentArea[1].getXpos() == currScreenNumber) { //wenn der spieler auf einen neuen bereich kommt wird der alte ersetzt
				currentArea[0] = currentArea[1];
			}

			int screen = (int) player.getPosition().x% AreaWidth;


			if (screen < AreaWidth/2	&& 	currScreenNumber-1>0) {// spieler ist in der linken hälfte vom aktuellen gegnerbereich && es gibt einen bereich zum laden
				currentArea[1] = Areas.get(currScreenNumber - 1);//links //FEHLER liegt hier
			}
			else{//der spieler geht nach rechts oder ist ganz links dann ist das ein fallback
				currentArea[1] = Areas.get(currScreenNumber + 1);//rechts
			}




			for(int i = 0; i<2;i++) {
				currentArea[i].update();
			}

        	//Das spawnen hier sollte von dem oben ersetzt werden temp ,aber TODO das oben funktioniert nicht (und ist falsch weil wenn man schon oben rechts(und lu) war ist unten rechts nicht neu obwohle es das sein sollte) und weil dann enemis leer ist geht gar nichts mehr
        	//framecounter++;//TODO das soll mit einer zahl ersetzt werden die sich beim scrollen erhöht


        	
        //wenn spieler nach unten fällt
           if(player.getPosition().y<0&&!isEnded) {//isended das es nur einmal aufgerufen wird
			   normalTime();
        	   score= (int) player.getPosition().x;//berechnung score
        	   if(score>highscore)highscore=score;

			   isEnded=true;
			   drawPlayer=false;
			   cooldown=true;//Spieler kann nicht mehr attackieren
			   // definition vom rechteck das runterfällt.
        	   endscreenPanelfinaltopLeft = new Vector2(screenWidth/4,screenHeight/8);//finale position schon in JPanel
			   endscreenSize = new Vector2(screenWidth/2,screenHeight/4*3);//	Vector 2 wird nur gebraucht
			   // 																			um die beiden informationen
			   // 																			gleichtzeitig zu benutzen

			   endscreenSpeed = player.getTransform().speed;
			   endscreenSpeed = new Vector2(endscreenSpeed.x,-endscreenSpeed.y).reverse();// wegen jpanel in die andere richtung
			   System.out.println("endscreenSpeed"+endscreenSpeed);
			   framesUntillScreenIsCentered=200;
			   endscreenPaneltopLeft = endscreenPanelfinaltopLeft.add(endscreenSpeed.reverse().multiply(framesUntillScreenIsCentered));


			   // ab hier alles relativ zu endscreentopmiddle
			   endscreenTextPos = new Vector2(endscreenSize.x/8,endscreenSize.y/8);
			   endscreenShopButtonPos =new Vector2(endscreenSize.x/8,endscreenSize.y/8*7);



           }
		   if(isEnded){
			  // EndscreenTextPos=EndscreenTextPos.makeGlobal(endscreenPaneltopLeft);

			   if(framesUntillScreenIsCentered<100)cameraPos= new Vector2(0,-2000);//setzt die Kamera unter das Spielfeld


			   if(framesUntillScreenIsCentered>0){
				   endscreenPaneltopLeft = endscreenPaneltopLeft.add(endscreenSpeed);
				   endscreenShopButton.setPosition(endscreenShopButtonPos.makeGlobal(endscreenPaneltopLeft).toCoordinate());// ist hier für Buttonjump

			   }
			   framesUntillScreenIsCentered--;
			   if(endscreenPressed)endscreenShopButton.moveGameObject(3);//für den Buttonjump
		   }

        }
        if (currentScreen == "shop") {
        	cameraPos=new Vector2(0,0);
        }
        
       
        
        repaint(); // ruft die paintComponent Methode auf, um das Spielfeld neu zu zeichnen
    }


// quellen finden
	public void drawSlowVignette(Graphics2D g2d, int width, int height, boolean isTimeSlowed) {
		double slowpercentage = ((1-getTimeMultiplyer())*100);//https://stackoverflow.com/questions/16656651/does-java-have-a-clamp-function/75707634#75707634 & https://stackoverflow.com/questions/16656651/does-java-have-a-clamp-function/75707634#75707634
		if(slowpercentage>100)slowpercentage=100;
		if(slowpercentage<0)slowpercentage=0;
		System.out.println("coloralpha"+ (int)(slowpercentage/100*130.3));
		//Vignette
		float[] fractions = {0.0f, 0.8f, 1.0f};
		Color[] colors = {
				new Color(0, 0, 0,0),        // durchsichtig für mitte
				new Color(0, 30, 100,(int)(slowpercentage/100*40)),    // Leichtes Blau
				new Color(0, 10, 50, (int)(slowpercentage/100*60))     // intensives Blau
		};

		RadialGradientPaint mask = new RadialGradientPaint(
				new Point((int) screenWidth/2, (int) screenHeight/2),
				(float)(width * 0.8), // Radius des Effekts
				fractions,
				colors
		);

		g2d.setPaint(mask);
		g2d.fillRect(0, 0, width, height);
		g2d.setColor(Color.BLACK);//farbe resetten
	}


    public void paintComponent(Graphics g) {
        super.paintComponent(g); // löscht das Spielfeld

        Graphics2D g2d = (Graphics2D) g.create();//das es sauber zurückgesetzt wird und das normal g von java unverändert bleibt

		// Um die Kanten des Objekts zu glätten
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if(currentScreen == "spiel") { // Spiel läuft
			if (isTimeSlowed()) {
				double zoomFactor = 1.125-getTimeMultiplyer()/8;//Berechnung für die Menge vom zoom
				g2d.translate(screenWidth / 2, screenHeight / 2);//mittelpunkt von g2d versetzen, sodass zoom in der mitte ist
				g2d.scale(zoomFactor, zoomFactor);//zoom
				g2d.translate(-screenWidth / 2, -screenHeight / 2);//g2d zurückpositionieren
			}
        	Vector2 bodenLinks = new Vector2(0,0).toJPanel();
        	Vector2 bodenRechts = new Vector2(99999999,0).toJPanel();
        	//Boden
        	g2d.setColor(Color.red);
        	g2d.setStroke(new BasicStroke(5.0f));
        	g2d.drawLine((int) bodenLinks.x, (int) bodenLinks.y, (int) bodenRechts.x, (int) bodenRechts.y);
        	g2d.setStroke(new BasicStroke());
        	g2d.setColor(Color.black);
        	//Player
        	if(drawPlayer)player.paintMe(g2d);

        	//Weapons
        	if(leftWeapon != null)leftWeapon.paintMe(g);
        	else System.out.println("leftweapon ist" + leftWeapon);
        	if(leftWeapon.getHitbox() != null) leftWeapon.getHitbox().paintMe(g);
        	if(rightWeapon != null) {rightWeapon.paintMe(g);
        		if(rightWeapon.getHitbox() != null) rightWeapon.getHitbox().paintMe(g);
        	}
        	else System.out.println("rightweapon ist" + rightWeapon);

        	// Enemy(Vector2 objectPosition, double width, double height, int health){
			for(int i = 0; i<2;i++) {
				currentArea[i].paintMe(g2d);
			}

			drawSlowVignette(g2d,(int) screenWidth,(int) screenHeight,isTimeSlowed());

			if(isEnded){
				Vector2 endscreendrawTextPos = endscreenTextPos.makeGlobal(endscreenPaneltopLeft);
				System.out.println("endscreenPaneltopLeft "+endscreenPaneltopLeft);
				System.out.println("buttonScreenposition "+ endscreenShopButton.getPosition());
				g2d.drawRect((int) (endscreenPaneltopLeft.x),(int)endscreenPaneltopLeft.y
						,(int)endscreenSize.x,(int)endscreenSize.y);
				g2d.drawString("Score: "+score+" ".replaceAll("\\s+",System.getProperty("line.separator"))+"money gained: "+(int)(score/1000),
						(int)endscreendrawTextPos.x,(int)endscreendrawTextPos.y);//TODO line seperator fixen und moneten richtig einfuegen
				//https://stackoverflow.com/questions/7833689/how-can-i-print-a-string-adding-newlines-in-java
				endscreenShopButton.paintMe(g2d);
			}

		}

        if(currentScreen.equals("shop")){

        	g.drawString(("Score: "+score),(int) screenWidth/2,(int) screenHeight/3);
        	g.drawString(("Highscore: "+highscore),(int) screenWidth/2,(int) screenHeight/3 -20);

        	startButton.paintMe(g2d);

        	leftUpgradeButton.paintMe(g2d);
        	rightUpgradeButton.paintMe(g2d);

        	g.drawString(("Level: "+leftWeapon.getLevel()),(int) leftUpgradeButton.getPosition().x,(int) leftUpgradeButton.getPosition().y+20);
        	g.drawString(("Level: "+rightWeapon.getLevel()),(int) rightUpgradeButton.getPosition().x,(int) rightUpgradeButton.getPosition().y+20);


        	//for(Button button : buttons) button.paintMe(g);
        	swordButton.paintMe(g2d);
        	spearButton.paintMe(g2d);
        	grapplingButton.paintMe(g2d);
        	staubsaugerButton.paintMe(g2d);
        	schwungSeilButton.paintMe(g2d);

			g2d.dispose();//das es sauber zurückgesetzt wird und das normal g von java unverändert bleibt
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
		if (arg0.getButton() == 3) //rechtsklick
		{
			if(currentScreen.equals("spiel")) {
				for (int i = 0; i < 2; i++) {
					rightWeapon.attack(mouseScreenPos, currentArea[i].getEnemies(), new WeaponHitListener() {

						@Override
						public void onHit(Vector2 knockback) {
							player.addSpeed(knockback);
							System.out.println(knockback + "knockback");
						}

						@Override
						public void onMiss() {
						}
					});

				}
			}
		}

		if(arg0.getButton()==1) //linksklick
		{
			if(currentScreen.equals("spiel")) {
				for (int i = 0; i < 2; i++) {
					leftWeapon.attack(mouseScreenPos, currentArea[i].getEnemies(), new WeaponHitListener() {

						@Override
						public void onHit(Vector2 knockback) {
							player.addSpeed(knockback);
						}

						@Override
						public void onMiss() {
						}
					});
				}
			}
		}
    	if(currentScreen.equals("spiel")&&isEnded){

			if(endscreenShopButton.press(new Vector2(arg0).toCoordinate())&&!endscreenPressed){
				endscreenPressed=true;
				endscreenShopButton.addSpeed(new Vector2(Math.random()*10,Math.random()*10));
				System.out.println("speedbutton"+endscreenShopButton.getSpeed());
				t = new Timer(1000, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
							currentScreen = "shop";
							endscreenPressed=false;
							t.stop();
					}});
				t.start();
			}
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
    			if(swordButton.press(mouseScreenPos)) {
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
			rightWeapon.clickReleased();
		}
		if(arg0.getButton()==1) { //linksklick
			leftWeapon.clickReleased();
		}
    }
	@Override
	public void shake() {
		
		Timer t = new Timer(26, new ActionListener() {//schiesst über längere zeit
			@Override
	        public void actionPerformed(ActionEvent e) {
				shaketimer--;
			if(currentScreen=="spiel"){
				Vector2 randompos = new Vector2((Math.random()*100)-50,(Math.random()*100)-50);
					System.out.println("shake"+randompos);
					cameraPos = cameraPos.add(randompos);
			}
		            if (shaketimer <= 0) {//reset wenn timer ausgelaufen oder getroffen
		            	shaketimer=3;
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

