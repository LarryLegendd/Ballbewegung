package spiel1;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

public class Grapplinghook extends Weapon {

	
	private double grappleEnemyKnockback = 6;
	
	//Variablen fürs zeichen
	private Vector2 letzteSpitze;
	private Vector2 letzteBasis1;
	private Vector2 letzteBasis2;
	private Vector2 midpoint;
	
	private Vector2 knockback;
	
	private Transform  transform = new Transform(new Vector2(0,0));
	
	private Vector2 speed;
	
	
	private double[][] levelArr = {
		//	Breite,range,kb, Preis, shoottime
			  {2, 	3, 	7.5,		3,	15},
			  {4,	6, 	8,	5,	20},
			  {6,	9, 	9,	8,	25},
			  {8, 	12, 10,	10,	30},
			  {10, 	15, 11,	15,	35},
			  {12, 	18, 12,	20, 40},
			  {14, 	21, 13,	30,	45},
			  {16, 	24, 14,	40,	50},
			  {18, 	27, 15,	50,	55},
			  {20,	 30,16,	100,60},
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
	
	public Grapplinghook(Transform playertransform, TimeController timeController, CameraController cameraController) {
		this.playertransform = playertransform;
		hitbox = new TriangleHitbox(basisBreite, range, transform);
		this.timeController = timeController;
		this.cameraController = cameraController;
	}
	
	
	public boolean shoot(Enemy enemy) {
        //fürs zeichen 
  		letzteBasis1 = hitbox.getBasis1().makeGlobal(hitbox.getPosition(),transform.rotation);//links unten
  		letzteBasis2 = hitbox.getBasis2().makeGlobal(hitbox.getPosition(),transform.rotation);//rechts unten
  		letzteSpitze = hitbox.getSpitze().makeGlobal(hitbox.getPosition(),transform.rotation);
  		midpoint = letzteBasis1.getPointBetween(letzteBasis2);
  		isShown=true;//muss manuel gemacht werden weil es unteschiedlich lang dauert;
  		
  		if(hitbox.collides(enemy.getHitbox())) {
      		enemy.schadenNehmen(1);
      		
      		Vector2 enemydiff = enemy.transform.position.makeLocal(playertransform.position);
      		System.out.println("enemydiff: "+enemydiff+" grappleEnemyKnockback: "+grappleEnemyKnockback);
      		enemy.addSpeed(enemydiff.normalize().multiply(grappleEnemyKnockback).reverse());
      		
      		knockback = enemydiff.normalize().multiply(grappleKnockback);
      		System.out.println("knockback: " + knockback);
      		cameraController.shake();
      		
      		return true;
      	}else {
      		return false;
      	}
	}
	@Override
	public void hit(Vector2 mauspos, ArrayList<Enemy> enemies, WeaponHitListener listener){
		if(getCooldown() == false) {
			setCooldown(true);
			Vector2 mausdiff = mauspos.makeLocal(playertransform.position);
			playertransform.rotation = mausdiff.angle();
			transform.position = playertransform.position;
			transform.rotation= playertransform.rotation;
			
			speed = mausdiff.normalize().multiply(shootspeed);//setzt die richtung und geschwindigkeit der Kugel
			
			timeController.slowTimeFor(shoottime/2);//slow für maximal die hälfte der Zeit
			shoottimer=shoottime;
			Timer t = new Timer(13, new ActionListener() {//schiesst über längere zeit
				@Override
		        public void actionPerformed(ActionEvent e) {
					transform.position = transform.position.add(speed);
			        
					shoottimer--;
					for(Enemy enemy : enemies){
						if(shoot(enemy)) {
							System.out.println(knockback+"knockback");
							listener.onHit(knockback);
							timeController.normalTime();
							shoottimer = shoottime;
							setCooldown(false);
							show();//beendet nach ein bischen extrazeit den timer
							((Timer) e.getSource()).stop();
						}
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
