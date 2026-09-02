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
	private boolean stopcooldown;
	private boolean cooldown;
	protected Transform playertransform;
	protected int level;
	public void stopCooldownTimer() {
		stopcooldown = true;
	}
	public void stopCooldown(){
		stopCooldownTimer();
		cooldown=false;
	}

	protected void show() {//vllt machen das das schwert auf cooldown in der zeit  ist
		showTimer = 50;
		isShown = true;//vllt show(time to recharge) das verschiedene waffen andere cooldowns haben
		
	    Timer t = new Timer(13, new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            showTimer--;

	            if (showTimer <= 0) {
	                if(isShown ==true) isShown = false;//falls es schon geändert wurde
	                ((Timer) e.getSource()).stop();
	            }
	        }
	    });

	    t.start();
	}
	
	protected void peneltyCooldown(int ticks) {
		cooldownTimer=ticks;
		startCooldown();
		Timer t = new Timer(13, new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	if(stopcooldown) {
	        		((Timer) e.getSource()).stop();
	                cooldownTimer = 0;
	                stopcooldown = false;
	        	}
	            cooldownTimer--;
	            if (cooldownTimer <= 0) {
	                cooldown=false;
	                ((Timer) e.getSource()).stop();
	                cooldownTimer = 0;
	            }
	        }
	    });
		t.start();
	}
	
	
	protected void startCooldown() {
		cooldown=true;
	}
	protected boolean getCooldown() {
		return cooldown;
	}
	
	public void reset() {
		cooldown = false;
		stopCooldownTimer();
		isShown = false;
	}

	/**Es wird um eine Attacke einer Waffe auszuführen immer die attack Methode in der Oberklasse Weapon aufgerufen. Diese attack Methode prüft den Cooldown und wenn die Waffe attackieren darf wird die abstrakte hit Methode Aufgerufen, die von der eigentlichen Waffe ausgeführt wird.
	 *
	 * @param mauspos
	 * @param enemies
	 * @param listener
	 */
	public void attack(Vector2 mauspos, ArrayList<Enemy> enemies, WeaponHitListener listener){
		if(!cooldown){//cooldownprüfung
			hit(mauspos, enemies, listener);
		}
	}
	protected abstract void hit(Vector2 mauspos, ArrayList<Enemy> enemies, WeaponHitListener listener);
	public abstract double getNextPrice();
	public abstract void levelUp(double money);
	protected abstract void updateLevel(int level);
	public abstract Hitbox getHitbox();
	public void paintMe(Graphics g) {}
	
	public  int getLevel() {
		return level;
	}
	
	public void clickReleased() {};
}
//TODO eine nichts mehr machen klasse machen

