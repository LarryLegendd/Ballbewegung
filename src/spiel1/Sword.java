package spiel1;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class Sword extends Weapon {
	private KreisTeilHitbox hitbox;
	
	private final double swordEnemyKnockback = 4;
	
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

		Vector2 mausAbstand = mauspos.makeLocal(playertransform.position);
		playertransform.rotation = mausAbstand.angle();

		//fürs Zeichnen Punkte merken
		drawEndpoint1=new Vector2(range,0).rotate(angle/2).makeGlobal(hitbox.transform.position, hitbox.transform.rotation);
		drawEndpoint2=new Vector2(range,0).rotate(-(angle/2)).makeGlobal(hitbox.transform.position, hitbox.transform.rotation);
		drawPlayerPos= new Vector2(0,0).makeGlobal(hitbox.transform.position, hitbox.transform.rotation);

		showTimer();//anzeigen
		for(Enemy enemy : enemies) {

			//Kollidieren
			if(hitbox.collides(enemy.getHitbox())) {
				enemy.schadenNehmen(1);
				enemy.addSpeed(mausAbstand.normalize().multiply(swordEnemyKnockback));
				knockback = mausAbstand.normalize().multiply(swordKnockback).reverse();//das liefert die gegenrichtung.

				hit = true;
				break;//break für performance, das nicht jeder Gegner überprüft werden muss.
			}
		}


		if(hit)	{
			listener.onHit(knockback);
			//kein Cooldown wenn Gegner getroffen wird
		}
		else{
			listener.onMiss();
			penaltyCooldown(300);
		}

		hit = false;

	}

	@Override
	public double getNextPrice() {
		if(level+1<levelArr.length )return levelArr[level+1][3];
		else return -1;//bei maximaler länge vom array -1
	}

	/**
	 * Aktualisiert die werte der waffe aus dem levelarray
	 * @param level
	 */
	@Override
	protected void updateLevel(int level) {
		angle = levelArr[level][0];
		range = levelArr[level][1];
		swordKnockback = levelArr[level][2];
		hitbox = new KreisTeilHitbox(range, angle, playertransform);
	}
	
	@Override
	public void paintMe(Graphics g){
		Graphics2D g2d = (Graphics2D) g; 
		if(isShown()&&drawEndpoint1!=null) {
			Vector2 JEndpoint1 = drawEndpoint1.toJPanel();
			Vector2 JEndpoint2 = drawEndpoint2.toJPanel();
			Vector2 JPlayerPos = drawPlayerPos.toJPanel();
			//"Dreieck" mit zwei Linien
			g.drawLine((int)JPlayerPos.x(), (int)JPlayerPos.y(),(int) JEndpoint1.x(), (int) JEndpoint1.y());
			g.drawLine((int)JPlayerPos.x(), (int)JPlayerPos.y(),(int) JEndpoint2.x(), (int) JEndpoint2.y());
			
			double startangle = drawEndpoint2.makeLocal(drawPlayerPos).angle();
			g2d.drawArc((int) (JPlayerPos.x()-range),(int) (JPlayerPos.y()-range), (int)range*2,(int) range*2, (int) Math.toDegrees(startangle),(int) Math.toDegrees(angle));//Bogen
		}
	}

	@Override
	public Hitbox getHitbox() {
		return hitbox;
	}
	
}
