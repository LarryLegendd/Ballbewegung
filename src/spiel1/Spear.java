package spiel1;

import java.awt.Graphics;
import java.util.ArrayList;

public class Spear extends Weapon {
	private double spearKnockback = 3;
	private double spearEnemyKnockback = 2;
	
	private Vector2 letzteSpitze;
	private Vector2 letzteBasis1;
	private Vector2 letzteBasis2;
	
	//level
	private int level = 0;
	
	private double[][] levelArr = {
		//	Breite,range,kb, Preis
			  {10, 70, 5,		3},
			  {13, 80, 5.3,		5},
			  {16, 90, 5.6,		8},
			  {19, 100, 5.9,	10},
			  {22, 110, 6.2,	15},
			  {25, 120, 6.5,	20},
			  {28, 140, 6.8,	30},
			  {31, 160, 7.1,	40},
			  {34, 180, 7.4,	50},
			  {37, 200, 7.7,	100},
		};
	
	private double basisBreite = levelArr[0][0];
	private double range = levelArr[0][1];
	
	private TriangleHitbox hitbox;
	
	public Spear(Transform playertransform) {
		this.playertransform = playertransform;
		hitbox = new TriangleHitbox(basisBreite, range, playertransform);
	}
	
	
	
	@Override
	public void hit(Vector2 mauspos, ArrayList<Enemy> enemies, WeaponHitListener listener){
		if(getCooldown() == false) {
			setCooldown(true);
			boolean hit = false;
			Vector2 knockback= new Vector2(0,0);
			show();
			for(Enemy enemy: enemies) {
				Vector2 mausdiff = mauspos.makeLocal(playertransform.position);
				playertransform.rotation = mausdiff.angle();
				
				
		//		//rendern neue makeglobal mit winkel das 
		//    	g.drawLine((int)B1.x, (int)B1.y,(int) S.x, (int) S.y);
		//		g.drawLine((int)B2.x, (int)B2.y,(int) S.x, (int) S.y);
				
				//fürs zeichen 
				letzteBasis1 = hitbox.getBasis1().makeGlobal(hitbox.getPosition(),playertransform.rotation);//links unten
				letzteBasis2 = hitbox.getBasis2().makeGlobal(hitbox.getPosition(),playertransform.rotation);//rechts unten
				letzteSpitze = hitbox.getSpitze().makeGlobal(hitbox.getPosition(),playertransform.rotation);
				//TODO irgenddwie timer das es länger da ist
			
				
				if(hitbox.collides(enemy.getHitbox())) {
		    		enemy.schadenNehmen(1);
		    		enemy.addSpeed(mausdiff.normalize().multiply(spearEnemyKnockback));
		    		
		    		knockback = mausdiff.normalize().multiply(spearKnockback).reverse();//das liefert die gegenrichtung.
		    		
		    		
		    		
		    		System.out.println("knockback: "+knockback);
		    		hit =true;
		    	}
			}
			if(hit) {
				listener.onHit(knockback);
				setCooldown(false);
			}
			else {
				listener.onMiss();
				peneltyCooldown(16);
			}	
		
//		
//		//Basisvariablen//wahrscheinlich temp
//		Vector2 mausdiff = mauspos.subtract(playerpos);
//		double mausangle = mausdiff.angle();
//		
//		boolean hit = false;
//		
//
//		
//		//dreiec- eckpunkt bestimmung
//		//basis eckpunkte
//		Vector2 B1=new Vector2(0,basisBreite/2).rotate(mausangle).add(playerpos);//richtige länge und dann drehen und dann auf spieler verschieben
//		Vector2 B2=new Vector2(0,-basisBreite/2).rotate(mausangle).add(playerpos);
//		//Spitze
//		Vector2 S= new Vector2(range,0).rotate(mausangle).add(playerpos);
//		if(enemy.getPosition().distanceTo(playerpos)<range+Math.sqrt(enemy.getWidth()*enemy.getWidth() + enemy.getHeight()*enemy.getHeight())){//optimierung das nur nah genuge gegner geprüft werden
//		//Hitbox
//			Vector2[] hitbox = enemy.getHitbox().toPoints(64);
//			for(Vector2 hitboxPos : hitbox) {
//				if(hitboxPos.angle(B1, B2) > Math.PI  ||  hitboxPos.angle(S, B1) > Math.PI  ||  hitboxPos.angle(B2, S) > Math.PI ) {
//				}
//				else {
//					hit=true ;
//				break;
//				}
//			}
//		}
//		
		
		}
	}
	
	@Override
	public void levelUp(double money) {
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
		spearKnockback = levelArr[level][2];
		hitbox = new TriangleHitbox(basisBreite, range, playertransform);
	}
	
	@Override
	public void paintMe(Graphics g) {
		if(isShown&& letzteBasis1 != null) {
			
			Vector2 JBasis1=letzteBasis1.toJPanel();
			Vector2 JBasis2=letzteBasis2.toJPanel();
			Vector2 JSpitze=letzteSpitze.toJPanel();
//			System.out.println(letzteSpitze+" Lastpeek");
//			System.out.println(hitbox.getSpitze()+" spitze");
//			System.out.println(hitbox.getPosition()+" position");
			g.drawLine((int)JBasis1.x,(int)JBasis1.y, (int) JSpitze.x, (int) JSpitze.y);
			g.drawLine((int)JBasis2.x,(int)JBasis2.y, (int) JSpitze.x, (int) JSpitze.y);
		}
	}

	@Override
	public Hitbox getHitbox() {
		return hitbox;
	}
}