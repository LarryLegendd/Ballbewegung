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
	/**
	 * startet für eine bestimmte Zeit einen Cooldown
	 */
	protected void peneltyCooldown(int millis) {
		startCooldown();
		cooldownTimer = new Timer(millis, () -> {
	       		cooldown=false;
				return false;
	    });
		TimerManager.addTimer(cooldownTimer);
	}
	/**
	 * startet einen Cooldown auf unbestimmte Zeit
	 */
	protected void startCooldown() {
		cooldown=true;
	}

	public void stopCooldown(){
		stopCooldownTimer();
		cooldown=false;
	}

	protected boolean getCooldown() {
		return cooldown;
	}


	/**
	 * Zeigt für 0,5 Sekunden das Objekt an, indem es isShown auf true setzt.
	 */
	protected void showTimer() {
		isShown = true;//vllt show(time)
		TimerManager.addTimer(showTimer);
	}

	/**
	 * Setzt isShown auf unbestimmte Zeit true
	 */
	protected void show(){
		isShown=true;
	}

	protected boolean isShown(){
		return isShown;
	}

	public void hide(){
		showTimer.setFinished();
		isShown=false;
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
			hide();//das letzte anzeigen beenden
			hit(mauspos, enemies, listener);//angreifen
		}
	}

	/**
	 * angreifen in der unterklasse
	 */
	protected abstract void hit(Vector2 mauspos, ArrayList<Enemy> enemies, WeaponHitListener listener);
	/**
	 * beim schwungseil um das schwingen zu beenden
	 */
	public void clickReleased() {}


	//==========Level===============

	/**
	 * preis ausgeben
	 * -1 wenn das levelarray zu ende ist
	 */
	public abstract double getNextPrice();//muss double sein wegen levelarray
	/**
	 * verbessern
	 * returnt den preis
	 */
	public int levelUp(int money){
		int price = (int) getNextPrice();
		System.out.println("money: "+money);
		if(price != -1) {//-1 wird von getnextprice ausgegeben, wenn das levelarray zuende ist.
			System.out.println(price+" price");
			if(money >= price) {
				level++;
				updateLevel(level);
				return price;
			}else System.out.println("insufficient funds");
		}
		return 0;
	}
	/**
	 * Werte, wie Reichweite oder Rückstoß der aus dem levelarray holen und als werte setzen
	 */
	protected abstract void updateLevel(int level);
	public  int getLevel() {
		return level;
	}

	public abstract Hitbox getHitbox();
	public void paintMe(Graphics g) {}
}