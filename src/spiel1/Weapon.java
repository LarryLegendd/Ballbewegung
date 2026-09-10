package spiel1;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


public abstract class Weapon {//Prozess: entscheiden ob oberklasse sinn macht mann kann toleranzwinkel in dreieck und rechteck zu basisbreite hacken aber ist das sinnvoll? oberklasse macht keinen sinn da verschiedene arten von hitbox(siehe dreieck) ERgebnis:macht sinn weil levelsystem
	//rendern	
	private boolean isShown=false; //private, weil es dann zentral steuerbar ist.

	private static boolean cooldown;
	protected Transform playertransform;
	protected int level;
	private final Timer showTimer =new Timer(500,()->{
			isShown = false;
			return false;//beenden vom Timer
	});
	private Timer cooldownTimer = new Timer(0,()->{return false;});//um nullpointer zu verhindern einen temporären timer erzeugen
	public void stopCooldownTimer() {
		cooldownTimer.setFinished();
	}
	public void stopCooldown(){
		stopCooldownTimer();
		cooldown=false;
	}

	protected void peneltyCooldown(int millis) {
		startCooldown();
		cooldownTimer = new Timer(millis, () -> {
	       		cooldown=false;
				return false;
	    });
		TimerManager.addTimer(cooldownTimer);
	}

	protected void showTimer() {
		isShown = true;//vllt show(time)
		TimerManager.addTimer(showTimer);
	}
	protected boolean isShown(){
		return isShown;
	}

	public void hide(){
		showTimer.setFinished();
		isShown=false;
	}

	protected void show(){
		isShown=true;
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