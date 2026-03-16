package spiel1;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

public abstract class Weapon {//Prozess: entscheiden ob oberklasse sinn macht mann kann toleranzwinkel in dreieck und rechteck zu basisbreite hacken aber ist das sinnvoll? oberklasse macht keinen sinn da verschiedene arten von hitbox(siehe dreieck) ERgebnis:macht sinn weil levelsystem
	//rendern	
	protected boolean isShown=false;
	protected int showTimer = 50;//vllt braucht man das garnicht weil das in show scchon geregelt ist temp
	protected int cooldownTimer;
	
	protected Transform playertransform;
	
	protected void show() {//vllt machen das das schwert auf cooldown in der zeit  ist TODO
		showTimer = 50;
		isShown = true;//vllt show(time to recharge) das verschwidene waffen andere cooldowns haben
		
	    Timer t = new Timer(13, new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            showTimer--;

	            if (showTimer <= 0) {
	                isShown = false;
	                ((Timer) e.getSource()).stop();
	                showTimer = 50; // optional: Reset für nächsten Gebrauch
	            }
	        }
	    });

	    t.start();
	}
	
	protected void peneltyCooldown(int millis) {
		cooldownTimer=millis;
		setCooldown(true);
		Timer t = new Timer(13, new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            cooldownTimer--;
	            if (cooldownTimer <= 0) {
	                setCooldown(false);
	                ((Timer) e.getSource()).stop();
	                cooldownTimer = millis;
	            }
	        }
	    });
		t.start();
	}
	
	
	protected void setCooldown(boolean b) {
		Spielfeld.cooldown=b;
	}
	protected boolean getCooldown() {
		return Spielfeld.cooldown;
	}
	
	public abstract void hit(Vector2 mauspos, ArrayList<Enemy> enemies, WeaponHitListener listener);
	public abstract void levelUp(double money);
	public abstract Hitbox getHitbox();
	public  void paintMe(Graphics g) {}
	
	public void hitReleased() {};
}
//TODO shotgun und man kriegt schüsse durch kills
//TODO passive bounce rüstung als waffe probably nicht

