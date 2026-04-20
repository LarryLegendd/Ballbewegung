package spiel1;

import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.Timer;

public class Staubsauger extends Weapon{
	//Zieht den gegner an ohne den spieler zu verändern
	
	//TODO vllt knockback abhängig von distanz machen das der gegner immer kurz vor den spieler landet
	
	//Variablen fürs zeichen
	private Vector2 letzteSpitze;
	private Vector2 letzteBasis1;
	private Vector2 letzteBasis2;
	private Vector2 midpoint;
	
	private Vector2 knockback;
	
	private Transform  transform = new Transform(new Vector2(0,0));
	
	private Vector2 speed;
	
	
	private double[][] levelArr = {
		//	Breite,range,enemykb,Preis
			  {10, 	200, 	5,		3},
			  {13,	260, 	5.3,	5},
			  {16,	290, 	5.6,	8},
			  {18, 	320, 5.9,	10},
			  {20, 	350, 6.2,	15},
			  {22, 	380, 6.5,	20},
			  {24, 	410, 6.8,	30},
			  {26, 	440, 7.1,	40},
			  {28, 	470, 7.4,	50},
			  {30,	 700, 7.7,	100},
		};
	private double breite = levelArr[0][0];
	private double range = levelArr[0][1];
	private double enemyKnockback = levelArr[0][2];
	
	private RectHitbox hitbox;
	
	public Staubsauger(Transform playertransform) {
		this.playertransform = playertransform;
		hitbox = new RectHitbox(breite, range, playertransform,true);
	}
	
	@Override
	public void hit(Vector2 mauspos, ArrayList<Enemy> enemies, WeaponHitListener listener) {
		if(getCooldown() == false) {
			
			boolean hit = false;
			
			Vector2 mausdiff = mauspos.makeLocal(playertransform.position);
			playertransform.rotation = mausdiff.angle();
			transform.position = playertransform.position;
			transform.rotation= playertransform.rotation;
			
			for(Enemy enemy : enemies){
				if(hitbox.collides(enemy.getHitbox(),32)) {
					System.out.println("getriffen");
					hit=true;
					
					// temp die zeile kann wahrscheinlich weg außer es ist unballenced enemy.schadenNehmen(0);
					
					//knockback
					Vector2 enemydiff = enemy.transform.position.makeLocal(playertransform.position);
		      		enemy.addSpeed(enemydiff.normalize().multiply(enemyKnockback).reverse());
				}
			}
			if(hit) {
				show();//beendet nach ein bischen extrazeit den timer
			}else{
                peneltyCooldown(3);
                show();//extrazeit fürs zeichnen
            }
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
		breite = levelArr[level][0];
		range = levelArr[level][1];
		enemyKnockback = levelArr[level][2];
		hitbox = new RectHitbox(breite, range, transform, true);
		System.out.println(level);
	}
	@Override
	public Hitbox getHitbox() {
		// TODO Auto-generated method stub
		return null;
	}
//TODO vllt schütteln
	
	@Override
	public void paintMe(Graphics g) {
		hitbox.paintMe(g);
	}
}
