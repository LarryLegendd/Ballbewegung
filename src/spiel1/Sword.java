package spiel1;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

public class Sword extends Weapon {
	
	private KreisTeilHitbox hitbox;
	
	private double swordEnemyKnockback = 4;
	
	//rendern
	private Vector2 drawEndpoint1;
	private Vector2 drawEndpoint2;
	private Vector2 drawPlayerPos;

	private double[][] levelArr = {
//			winkel,		range,	kb,	 Preis
		  {Math.PI/3,	100,	12, 	3},
		  {Math.PI/2.85,110, 	14,	5},
		  {Math.PI/2.7,	120, 	16,	8},
		  {Math.PI/2.55,130, 	18,	10},
		  {Math.PI/2.4, 	140,20,		15},
		  {Math.PI/2.25,150, 	22,	20},
		  {Math.PI/2.1, 160, 	24,	30},
		  {Math.PI/1.95,170, 	26,	40},
		  {Math.PI/1.8, 	180,28,		50},
		  {Math.PI/1.65,190,	30,		100},
	};
	
	private double angle = levelArr[0][0];
	private double range = levelArr[0][1];
	private double swordKnockback = levelArr[0][2];
	
	Vector2 knockback = new Vector2(0,0);
	private boolean hit;

	public Sword(Transform playertransform) {
		this.playertransform= playertransform;
		hitbox = new KreisTeilHitbox(range, angle , playertransform);
	}
		
	
	
	@Override
	public void hit(Vector2 mauspos, ArrayList<Enemy> enemies, WeaponHitListener listener) {
		if(getCooldown() == false) {
			setCooldown(true);
			show();//anzeigen
			for(Enemy enemy : enemies) {
				Vector2 mausdiff = mauspos.makeLocal(playertransform.position);
				playertransform.rotation = mausdiff.angle();
				System.out.println(enemy.getHitbox());
				//zeichnen
				drawEndpoint1=new Vector2(range,0).rotate(angle/2).makeGlobal(hitbox.transform.position, hitbox.transform.rotation);
				drawEndpoint2=new Vector2(range,0).rotate(-(angle/2)).makeGlobal(hitbox.transform.position, hitbox.transform.rotation);
				drawPlayerPos= new Vector2(0,0).makeGlobal(hitbox.transform.position, hitbox.transform.rotation);
				
				//colidieren
				if(hitbox.collides(enemy.getHitbox())) {
		    		enemy.schadenNehmen(1);
		    		enemy.addSpeed(mausdiff.normalize().multiply(swordEnemyKnockback));
		    		System.out.println(knockback+"iansdf");
		    		knockback = mausdiff.normalize().multiply(swordKnockback).reverse();//das liefert die gegenrichtung.
		
		    		System.out.println("knockback: "+knockback);
		    		hit = true;
				}
//	temp		System.out.println(knockback+"System.out.println(knockback+\"iansdf\");System.out.println(knockback+\"iansdf\");System.out.println(knockback+\"iansdf\");");
//			System.out.println(hit+"git");
			}
			
			if(hit)	{
				listener.onHit(knockback);
		//temp		System.out.println(knockback);
				setCooldown(false);
			}
			else{
				listener.onMiss();
				peneltyCooldown(6);
			}
			
			hit = false;
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
		angle = levelArr[level][0];
		range = levelArr[level][1];
		swordKnockback = levelArr[level][2];
		hitbox = new KreisTeilHitbox(range, angle, playertransform);
	}
	
	@Override
	public void paintMe(Graphics g){
		Graphics2D g2d = (Graphics2D) g; 
		if(isShown&&drawEndpoint1!=null) {
			Vector2 JEndpoint1 = drawEndpoint1.toJPanel();
			Vector2 JEndpoint2 = drawEndpoint2.toJPanel();
			Vector2 JPlayerPos = drawPlayerPos.toJPanel();
			g.drawLine((int)JPlayerPos.x, (int)JPlayerPos.y,(int) JEndpoint1.x, (int) JEndpoint1.y);
			g.drawLine((int)JPlayerPos.x, (int)JPlayerPos.y,(int) JEndpoint2.x, (int) JEndpoint2.y);
			
			double startangle = drawEndpoint2.makeLocal(drawPlayerPos).angle();
			System.out.println(startangle);
			g2d.drawArc((int) (JPlayerPos.x-range),(int) (JPlayerPos.y-range), (int)range*2,(int) range*2, (int) Math.toDegrees(startangle),(int) Math.toDegrees(angle));
		}
	}

	@Override
	public Hitbox getHitbox() {
		return hitbox;
	}
	
}
