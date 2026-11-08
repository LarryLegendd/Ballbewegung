package spiel1;

import java.awt.Graphics;
import java.util.ArrayList;

public class SchwungSeil extends Weapon {

	//Variablen fürs zeichen
	private Vector2 letzteSpitze;
	private Vector2 letzteBasis1;
	private Vector2 letzteBasis2;
	private Vector2 midpoint;

	
	private Transform  transform = new Transform(new Vector2(0,0));
	
	private Transform playertransform;
	private Transform originTransform;
	private boolean shouldStop;
	/**
	 * wenn daneben geschossen wird, ohne das es vom Spieler beendet werden soll wird sich gemerkt, dass ein stop
	 * noch zusätzlich abgewartet werden muss
	 */
	private boolean isAttacking;
		
	private final double[][] levelArr = {//TODO warum gibt es hier knockback und vllt stattdesen luftwiederstand einführen
		//	Breite,range,kb, Preis, shoottime
			  {4, 	5, 	5,		3,	7},
			  {4,	6, 	5.3,	5,	10},
			  {6,	9, 	5.6,	8,	15},
			  {8, 	12, 5.9,	10,	20},
			  {10, 	15, 6.2,	15,	25},
			  {12, 	18, 6.5,	20,	30},
			  {14, 	21, 6.8,	30,	35},
			  {16, 	24, 7.1,	40,	40},
			  {18, 	27, 7.4,	50,	45},
			  {20,	 30, 7.7,	100,50},
		};
	
	private double basisBreite = levelArr[0][0];
	private double range = levelArr[0][1];
	private int shoottime=(int)levelArr[0][4];
	private int shoottimer;
	private double shootspeed=20;
	
	private TimeController timeController;
	private final CameraController cameraController;
	
	private TriangleHitbox hitbox;
	private WeaponHitListener hitListener;
	private Enemy hitEnemy;
	
	private Player player;

	private Timer swingtimer = 	new Timer(13,() -> {//Timer für das Schwingen vom Spieler

		if (shouldStop) {
			player.stopSwing();
			hide();
			shouldStop = false;
			this.stopCooldown();
			isAttacking=false;
			return false;
		}
		playertransform = player.getTransform();


		//richtung korrigieren
		double speed = playertransform.speed.length();
		//richtung
		// 2D Kreuzprodukt (z-Komponente)
		double cross = hitEnemy.getTransform().position.makeLocal(playertransform.position).x() * playertransform.speed.y() - hitEnemy.getTransform().position.makeLocal(playertransform.position).y() * playertransform.speed.x();

		Vector2 dir;


		player.startSwing();

		if (cross > 0) {
			// gegen Uhrzeigersinn
			//normierter Vektor, der in die Richtung zeigt, in die der Spieler schwingen soll
			dir = hitEnemy.getTransform().position.makeLocal(playertransform.position).normalize().rotate(Math.PI / 2);
		} else {
			// im Uhrzeigersinn
			dir = hitEnemy.getTransform().position.makeLocal(playertransform.position).normalize().rotate(Math.PI / 2 * 3);
		}
		double speedrichtungsunterschied = Math.cos(playertransform.speed.angle() - dir.angle());//gibt an welcher anteil der richtung in die richtige richtung zeigt.
		Vector2 zielspeed = dir.multiply(speed); // die geschwindigkeit des spielers in die richtung des schwingens drehen. zielspeed ist die richtung in die der spieler sich bewegen soll. Dabei wird die komplette geschwindigkeit in die richtung des schwingens umgesetzt. das wäre richtig, wenn der spieler den kreis der durch das schwingen erzeugt weid tangiert, ist aber komplett falsch wenn der spieler normal zu diesem kreis auf den kreis zukommt.

		//berechnung von dem Teil der Gravitation der in die richtige richtung geht
		Vector2 gravdown = new Vector2(0, -.13).multiply(timeController.getTimeSpeed());

		double gravitationeffizienz = Math.cos(gravdown.rotate(-dir.angle()).angle());//wie viel von der gravitation wirkt(0 - 1)

		Vector2 teilgrav = gravdown.rotate(Math.PI / 2).multiply(gravitationeffizienz).rotate(dir.angle());//Teil der gravitation der in die richtige richtung geht

		zielspeed = zielspeed.add(teilgrav);//gravitation

		Vector2 speeddifference = zielspeed.subtract(playertransform.speed).multiply(speedrichtungsunterschied); //nimmt den unterschied, von dem wie der spieler fliegen soll und dem wie er fliegt und multipliziert diesen mit dem "wirkungsgrad", der durch den winkel bestimmt  wurde
		hitListener.onHit(speeddifference);//Geschwindigkeit um spieler addieren
		return true;//timer wiederholen

	});



	Timer t;
	
	public SchwungSeil(Transform playertransform, Player player, TimeController timeController, CameraController cameraController) {
		this.playertransform = playertransform;
		hitbox = new TriangleHitbox(basisBreite, range, transform);
		this.timeController = timeController;
		this.cameraController = cameraController;
		this.player = player;
		originTransform = player.findPointing().getTransform();
	}

	/**
	 * startet einen Angriff
	 * @param mauspos
	 * @param enemies
	 * @param listener
	 */
	@Override
	public void hit(Vector2 mauspos, ArrayList<Enemy> enemies, WeaponHitListener listener){
		Vector2 mausdiff = mauspos.makeLocal(playertransform.position);
		transform.position = originTransform.position;
		transform.rotation= originTransform.rotation;
		transform.speed = mausdiff.normalize().multiply(shootspeed);//setzt die richtung und geschwindigkeit des Projektils
		hitListener = listener;

		isAttacking = true;

		timeController.slowTime();

		startCooldown();// startet Cooldown während geschossen wird

		shoottimer=shoottime;
		t = 	new Timer(13, () -> {//Timer für das geradeaus fliegen des Projektils
			transform.position = transform.position.add(transform.speed); // bewegen des "Pfeils"

			shoottimer--;

			if (shouldStop) {//aufhören, wenn der Spieler das verlangt
				shoottimer=0;
			}

			for(Enemy enemy : enemies)//prüfen, ob es einen Gegner trifft.
			{
				if(shoot(enemy))
				{
					hitEnemy = enemy;
					timeController.normalTime();//zeitverlangsamung deaktivieren

					if (swingtimer != null) {//schwingen starten
						TimerManager.addTimer(swingtimer);
					}
					return false;

				}
			}
			if (shoottimer <= 0) {//reset wenn timer ausgelaufen, weil daneben geschossen wurde.
				listener.onMiss();
				shoottimer = shoottime;
				penaltyCooldown(30);//Strafcooldown
				timeController.normalTime();//zeitverlangsamung deaktivieren
				if(isShown()) showTimer();//beendet nach ein bisschen extrazeit den timer. Nur showtimer, wenn nicht manuell beendet
				isAttacking=false;
				shouldStop = false;
				return false;
			}

			return true;
		});
		TimerManager.addTimer(t);

	}

	/**
	 * prüft, ob ein gegner getroffen wird
	 * @param enemy
	 * @return
	 */
	public boolean shoot(Enemy enemy) {
        //fürs zeichen positionen merken
  		letzteBasis1 = hitbox.getBasis1().makeGlobal(hitbox.getPosition(),transform.rotation);//links unten
  		letzteBasis2 = hitbox.getBasis2().makeGlobal(hitbox.getPosition(),transform.rotation);//rechts unten
  		letzteSpitze = hitbox.getSpitze().makeGlobal(hitbox.getPosition(),transform.rotation);
  		midpoint = letzteBasis1.getPointBetween(letzteBasis2);

  		show();//muss manuell (nicht mit showTimer()) gemacht werden weil es unterschiedlich lang dauert;
  		
  		if(hitbox.collides(enemy.getHitbox())) {//wenn getroffen
      		enemy.schadenNehmen(1);
      		cameraController.shake();
      		
      		return true;
      	}else {
      		return false;
      	}
	}

	@Override
	public void clickReleased() {
		if(isAttacking){
			shouldStop =true;//Attacke abbrechen
		}
	}

	
	@Override
	public double getNextPrice() {
		if(level+1<levelArr.length)return levelArr[level+1][3];
		else return -1;
	}

	/**
	 * Aktualisiert die werte der waffe aus dem levelarray
	 * @param level
	 */
	@Override
	protected void updateLevel(int level) {
		basisBreite = levelArr[level][0];
		range = levelArr[level][1];
		shoottime=(int)levelArr[level][4];
		hitbox = new TriangleHitbox(basisBreite, range, transform);
		System.out.println(level);
	}
	
	@Override
	public void paintMe(Graphics g) {
		if(hitbox!=null)hitbox.paintMe(g);
		else System.err.println("hitbox ist null");
		if(isShown()&&letzteBasis1!=null) {
			//positionen ins JPanel koordinatesystem umwandeln
			Vector2 JBasis1=letzteBasis1.toJPanel();
			Vector2 JBasis2=letzteBasis2.toJPanel();
			Vector2 JSpitze=letzteSpitze.toJPanel();
			Vector2 Jmidpoint=midpoint.toJPanel();
			Vector2 Jplayerpos= originTransform.position.toJPanel();

			//dreieck
			g.drawLine((int)JBasis1.x(),(int)JBasis1.y(), (int) JSpitze.x(), (int) JSpitze.y());
			g.drawLine((int)JBasis2.x(),(int)JBasis2.y(), (int) JSpitze.x(), (int) JSpitze.y());
			g.drawLine((int)JBasis2.x(),(int)JBasis2.y(), (int) JBasis1.x(), (int) JBasis1.y());

			//seil
			g.drawLine((int)Jmidpoint.x(),(int)Jmidpoint.y(), (int) Jplayerpos.x(), (int) Jplayerpos.y());
		}
	}

	@Override
	public Hitbox getHitbox() {
		return hitbox;
	}
	
	@Override
	public void reset() {
		shouldStop =false;
		swingtimer.setFinished();
		player.stopSwing();
		stopCooldown();
		hide();
	}
}