package spiel1;

import java.awt.Graphics;
import java.util.ArrayList;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SchwungSeil extends Weapon {

	
	private double grappleEnemyKnockback = 6;
	
	//Variablen fürs zeichen
	private Vector2 letzteSpitze;
	private Vector2 letzteBasis1;
	private Vector2 letzteBasis2;
	private Vector2 midpoint;
	
	private Vector2 knockback;
	
	private Transform  transform = new Transform(new Vector2(0,0));
	
	private Transform playertransform;
	
	//level
	private int level = 0;
	
	private double[][] levelArr = {
		//	Breite,range,kb, Preis, shoottime
			  {2, 	3, 	5,		3,	5},
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
	private double grappleKnockback = levelArr[0][2];
	private int shoottime=(int)levelArr[0][4];
	private int shoottimer;
	private double shootspeed=20;
	
	private final TimeController timeController;
	private final CameraController cameraController;
	
	private TriangleHitbox hitbox;
	private WeaponHitListener hitListener;
	private Enemy hitEnemy;
	
	private Player player;

	private Timer swingtimer = 	new Timer(13, new ActionListener(){//schiesst über längere zeit
		@Override
	    public void actionPerformed(ActionEvent e) {
			
			playertransform = player.getTransform();
			
			//richtung korrigieren
			double speed = playertransform.speed.length();
			//richtung
			// 2D Kreuzprodukt (z-Komponente)
			double cross = hitEnemy.transform.position.makeLocal(playertransform.position).x * playertransform.speed.y - hitEnemy.transform.position.makeLocal(playertransform.position).y * playertransform.speed.x;
			
			Vector2 dir;
			
			
			player.startSwing();
			
			if (cross > 0) {
			    // gegen Uhrzeigersinn
			    System.out.println("geguhr");
			    dir = hitEnemy.transform.position.makeLocal(playertransform.position).normalize().rotate(Math.PI / 2);
			} else {
			    // im Uhrzeigersinn
			    System.out.println("uhr");
			    dir = hitEnemy.transform.position.makeLocal(playertransform.position).normalize().rotate(Math.PI / 2 *3);
			}
			double speedrichtungsunterschied = Math.cos(playertransform.speed.angle()-dir.angle());
			Vector2 zielspeed=dir.multiply(speed);
			
			//berechnung von dem teil der Gravitation der in die richtige richtung geht
			Vector2 gravdown = new Vector2(0,-.13).multiply(timeController.getTimeSpeed());
			
			double gravitationeffizienz = Math.cos(gravdown.rotate(-dir.angle()).angle());//wie viel von der gravitation wirkt(0 - 1)
			//							
			Vector2 teilgrav = gravdown.rotate(Math.PI/2).multiply(gravitationeffizienz).rotate(dir.angle());//teil der gravitation der in die richtige richtung geht
			
			zielspeed=zielspeed.add(teilgrav);//gravitation
			
			Vector2 speeddifference = zielspeed.subtract(playertransform.speed).multiply(speedrichtungsunterschied);
			hitListener.onHit(speeddifference);//////////temp-->new Vector2(0,0));//temp 
			//hitListener.onHit(playertransform.speed=playertransform.speed.add(new Vector2(0,.13).multiply(timeController.getTimeSpeed())));//Gravitation entgegenwirken

			
		}});
	
	Timer t;
	
	public SchwungSeil(Transform playertransform, Player player, TimeController timeController, CameraController cameraController) {
		this.playertransform = playertransform;
		hitbox = new TriangleHitbox(basisBreite, range, transform);
		this.timeController = timeController;
		this.cameraController = cameraController;
		this.player = player;
	}
	
	
	public boolean shoot(Enemy enemy) {
        //fürs zeichen 
  		letzteBasis1 = hitbox.getBasis1().makeGlobal(hitbox.getPosition(),transform.rotation);//links unten
  		letzteBasis2 = hitbox.getBasis2().makeGlobal(hitbox.getPosition(),transform.rotation);//rechts unten
  		letzteSpitze = hitbox.getSpitze().makeGlobal(hitbox.getPosition(),transform.rotation);
  		midpoint = letzteBasis1.getPointBetween(letzteBasis2);
  		isShown=true;//muss manuell(nicht mit show()) gemacht werden weil es unteschiedlich lang dauert;
  		
  		if(hitbox.collides(enemy.getHitbox())) {
      		enemy.schadenNehmen(1);
      		
      		Vector2 enemydiff = enemy.transform.position.makeLocal(playertransform.position);//temp braucht man vielleicht nicht
      		
      		knockback = enemydiff.normalize().multiply(grappleKnockback);
      		System.out.println("knockback: " + knockback);
      		cameraController.shake();
      		
      		return true;
      	}else {
      		return false;
      	}
	}
	@Override
	public void hitReleased() {
		if (swingtimer != null) {
			swingtimer.stop();
			player.stopSwing();
		}
		setCooldown(false);
		show();//beendet nach ein bischen extrazeit den timer
	}
	
	@Override
	public void hit(Vector2 mauspos, ArrayList<Enemy> enemies, WeaponHitListener listener){
		if(getCooldown() == false) {
			setCooldown(true);
			Vector2 mausdiff = mauspos.makeLocal(playertransform.position);
			playertransform.rotation = mausdiff.angle();
			transform.position = playertransform.position;
			transform.rotation= playertransform.rotation;
			transform.speed = mausdiff.normalize().multiply(shootspeed);//setzt die richtung und geschwindigkeit der Kugel
			hitListener = listener;

			
//			timeController.slowTimeFor(shoottime/2);//slow für maximal die hälfte der Zeit

			
			shoottimer=shoottime;
			t = 	new Timer(13, new ActionListener() {//schiesst über längere zeit
				@Override
			    public void actionPerformed(ActionEvent e) {
						transform.position = transform.position.add(transform.speed);
				        
						shoottimer--;
												
						for(Enemy enemy : enemies){
							if(shoot(enemy)) 
							{
								hitEnemy = enemy;
								t.stop();
								if (swingtimer != null) {
									swingtimer.restart();
								}
								
							
							}else
							if (shoottimer <= 0) {//reset wenn timer ausgelaufen oder getroffen
				                listener.onMiss();
				                shoottimer = shoottime;
				                peneltyCooldown(30);
				                show();//beendet nach ein bischen extrazeit den timer
				                ((Timer) e.getSource()).stop();
				            }
						}
			        }
			    });
			t.start();
		}
	}
	
	@Override
	public void levelUp(double money) {
		System.out.println(level);
		if(level<levelArr.length-1) {
			if(money > getNextPrice()) {//das -1 ist weil .length quasi +1 rechnet
				level++;
		
				updateLevel(level);
			}else System.out.println("insufficient funds");//TODO das im spiel anzeigen lassen
		}else System.out.println("maximales level wurde ereicht");
	}
	
	
	public double getNextPrice() {
		if(level<levelArr.length)return levelArr[level+1][3];
		else return 0;
	}
	
	private void updateLevel(int level) {
		basisBreite = levelArr[level][0];
		range = levelArr[level][1];
		grappleKnockback = levelArr[level][2];
		shoottime=(int)levelArr[level][4];
		hitbox = new TriangleHitbox(basisBreite, range, transform);
		System.out.println(level);
	}
	
	@Override
	public void paintMe(Graphics g) {
		if(hitbox!=null)hitbox.paintMe(g);
		else System.out.println("hitbox ist null");
		if(isShown&&letzteBasis1!=null) {
			
			Vector2 JBasis1=letzteBasis1.toJPanel();
			Vector2 JBasis2=letzteBasis2.toJPanel();
			Vector2 JSpitze=letzteSpitze.toJPanel();
			Vector2 Jmidpoint=midpoint.toJPanel();
			Vector2 Jplayerpos=playertransform.position.toJPanel();
//			System.out.println(letzteSpitze+" Lastpeek");
//			System.out.println(hitbox.getSpitze()+" spitze");
//			System.out.println(hitbox.getPosition()+" position");
			g.drawLine((int)JBasis1.x,(int)JBasis1.y, (int) JSpitze.x, (int) JSpitze.y);
			g.drawLine((int)JBasis2.x,(int)JBasis2.y, (int) JSpitze.x, (int) JSpitze.y);
			g.drawLine((int)JBasis2.x,(int)JBasis2.y, (int) JBasis1.x, (int) JBasis1.y);
			g.drawLine((int)Jmidpoint.x,(int)Jmidpoint.y, (int) Jplayerpos.x, (int) Jplayerpos.y);
		}
	}

	@Override
	public Hitbox getHitbox() {
		return hitbox;
	}
}