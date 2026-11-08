package spiel1;

import java.awt.Graphics;
import java.util.ArrayList;

public class Grapplinghook extends Weapon { // die Ranziehattacke

	
	private double grappleEnemyKnockback = 6;
	
	//Variablen fürs zeichen
	private Vector2 letzteSpitze;
	private Vector2 letzteBasis1;
	private Vector2 letzteBasis2;

	
	private Vector2 knockback;
	
	private Transform  transform = new Transform(new Vector2(0,0));
	
	private Vector2 speed;


	
	private double[][] levelArr = {
		//	Breite,hoehe,kb, Preis, shoottime
			  {8, 	9, 	7.5,	4,	15},
			  {9,	10, 8,		5,	20},
			  {10,	11, 9,		8,	25},
			  {12, 	12, 10,		10,	30},
			  {14, 	15, 11,		15,	35},
			  {16, 	18, 12,		20, 40},
			  {18, 	21, 13,		30,	45},
			  {20, 	24, 14,		40,	50},
			  {22, 	27, 15,		50,	55},
			  {24,	30,	16,		100,60},
		};
	
	private double basisBreite = levelArr[0][0]; // Breite der Basis vom Dreieck, das geschossen wird
	private double hoehe = levelArr[0][1]; // Die Hoehe vom Dreieck, das geschossen wird
	private double grappleKnockback = levelArr[0][2];// Wie stark sich der Spieler an den gegner ranzieht
	private int shoottime=(int)levelArr[0][4];
// Wie lang ein Schuss dauert
	private int currentshoottime;
	private double shootspeed=20;
	
	private final TimeController timeController;	//Controller um Spielfeld Methoden zu benutzen ohne die Methoden public zu machen
	private final CameraController cameraController;
	
	
	private TriangleHitbox hitbox;
	
	public Grapplinghook(Transform playertransform, TimeController timeController, CameraController cameraController) {
		this.playertransform = playertransform;
		hitbox = new TriangleHitbox(basisBreite, hoehe, transform);
		this.timeController = timeController;
		this.cameraController = cameraController;
	}
	
	/** Kollisionsprüfung eines Gegners
*/
	public boolean shoot(Enemy enemy) {
        //fürs zeichen position der Grapplinghook Ecken merken
  		letzteBasis1 = hitbox.getBasis1().makeGlobal(hitbox.getPosition(),transform.rotation);//links unten
  		letzteBasis2 = hitbox.getBasis2().makeGlobal(hitbox.getPosition(),transform.rotation);//rechts unten
  		letzteSpitze = hitbox.getSpitze().makeGlobal(hitbox.getPosition(),transform.rotation);


  		if(hitbox.collides(enemy.getHitbox())) {// wenn die hitbox der Grapplinghook mit der eines gegners kollidiert
      		enemy.schadenNehmen(1);
      		
      		Vector2 enemydiff = enemy.getTransform().position.makeLocal(playertransform.position); //der unterschied zwischen
			// gegner und Spielerposition wichtig für die richtung

      		enemy.addSpeed(enemydiff.normalize()// 		nimmt die richtung zum spieler
					.multiply(grappleEnemyKnockback)// 	multipliziert sie mit dem rückstoß, den der gegner kriegen soll
					.reverse());//						und dreht es um, sodass der gegner zum spieler hinfliegt.


      		knockback = enemydiff.normalize().multiply(grappleKnockback);// der Spielerknockback hat die gleiche
			// 																berechnung wie der rückstoß vom Gegner
			//																nur umgedreht und mit dem spielerrückstoß
			//																anstatt vom gegnerrückstoß

      		cameraController.shake();// schüttelt bei einem treffer den Bildschirm.
      		
      		return true;
      	}else {
      		return false;
      	}
	}

	@Override
	public void hit(Vector2 mauspos, ArrayList<Enemy> enemies, WeaponHitListener listener){// der Listener ist gebraucht,
		// weil der spieler nicht direkt beim losschiessen rückstoß kriegt, sondern erst wenn die Grappinghook geflogen
		// ist und einen gegner getroffen hat.

		startCooldown();//startet cooldown
		
		Vector2 mausdiff = mauspos.makeLocal(playertransform.position);// Richtung zur maus vom Spieler aus gesehen
		transform.position = playertransform.position;
		transform.rotation= mausdiff.angle();
		
		speed = mausdiff.normalize().multiply(shootspeed);//setzt die richtung und geschwindigkeit der Kugel
		timeController.slowTimeFor((shoottime*13));//slow für den Schuss
		currentshoottime =shoottime;

		letzteBasis1 = null;//vor show letztebasis1 null setzen, sodass nicht die alte position gezeichnet wird

		show();//nicht mit showTimer, weil die Zeit bei einem Treffer unterschiedlich lang ist


		Timer shootTimer = new Timer(13, () -> {//schiesst über längere zeit
			transform.position = transform.position.add(speed);//Der enterhaken bewegt sich

			currentshoottime--;//timer, das nach einer bestimmten flugzeit der schuss beendet wird.
			for(Enemy enemy : enemies){
				if(shoot(enemy)) {//prüft ob gegner getroffen werden
					listener.onHit(knockback);//SpielerKnockback
					timeController.normalTime();//Zeitverlangsamung vorzeitig beenden
					currentshoottime = shoottime;
					stopCooldown();
					if(isShown())showTimer();//beendet nach ein bischen extrazeit das anzeigen. If falls extern show beendet wurde
					return false;
				}
				if (currentshoottime <= 0) {//reset wenn timer ausgelaufen oder getroffen
					listener.onMiss();//kein SpielerKnockback
					currentshoottime = shoottime;
					penaltyCooldown(30);
					if(isShown())showTimer();//beendet nach ein bischen extrazeit den timer
					return false;
				}
			}
			return true;
		});
		TimerManager.addTimer(shootTimer);
	}

	@Override
	public double getNextPrice() {
		if(level+1<levelArr.length)return levelArr[level+1][3];
		else return -1;
	}
	@Override
	protected void updateLevel(int level) {
		basisBreite = levelArr[level][0];
		hoehe = levelArr[level][1];
		grappleKnockback = levelArr[level][2];
		shoottime=(int)levelArr[level][4];
		hitbox = new TriangleHitbox(basisBreite, hoehe, transform);
		System.out.println(level);
	}
	
	@Override
	public void paintMe(Graphics g) {
		if(hitbox!=null)hitbox.paintMe(g);
		else System.err.println("hitbox ist null");
		if(isShown()&&letzteBasis1!=null) {
			
			Vector2 JBasis1=letzteBasis1.toJPanel();
			Vector2 JBasis2=letzteBasis2.toJPanel();
			Vector2 JSpitze=letzteSpitze.toJPanel();
			Vector2 Jmidpoint=JBasis1.getPointBetween(JBasis2);

			Vector2 Jplayerpos=playertransform.position.toJPanel();

			//Dreieck
			g.drawLine((int)JBasis1.x(),(int)JBasis1.y(), (int) JSpitze.x(), (int) JSpitze.y());
			g.drawLine((int)JBasis2.x(),(int)JBasis2.y(), (int) JSpitze.x(), (int) JSpitze.y());
			g.drawLine((int)Jmidpoint.x(),(int)Jmidpoint.y(), (int) JSpitze.x(), (int) JSpitze.y());
		// Verbindungslinie
			g.drawLine((int)Jmidpoint.x(),(int)Jmidpoint.y(), (int) Jplayerpos.x(), (int) Jplayerpos.y());
		}
	}

	@Override
	public Hitbox getHitbox() {
		return hitbox;
	}

}
