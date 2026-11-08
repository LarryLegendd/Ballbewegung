package spiel1;

import java.awt.Color;
import java.awt.Graphics;

public class TriangleHitbox extends Hitbox{//erstellt eine dreieckige hitbox mit nullpunkt auf der mitte der basiskante
	
	private Vector2 basisl;//links unten
	private Vector2 basisr;//rechts unten
	private Vector2 spitze;//
	
	private int standardHitboxAccuracy = 64;
	

	public TriangleHitbox(double breite, double hoehe,Transform transform) {
		super(transform,(int)Math.max(hoehe,breite/2));//entferntester punkt als size mitgeben.
		basisl=new Vector2(0,-breite/2);
		basisr=new Vector2(0,breite/2);
		//Spitze
		spitze= new Vector2(hoehe,0);
		
		this.transform=transform;
	}	
	
	
	public Vector2 getBasis1() {
		return basisl;
	}

	public void setBasis1(Vector2 basis1) {
		this.basisl = basis1;
	}

	public Vector2 getBasis2() {
		return basisr;
	}

	public void setBasis2(Vector2 basis2) {
		this.basisr = basis2;
	}

	public Vector2 getSpitze() {
		return spitze;
	}

	public void setSpitze(Vector2 spitze) {
		this.spitze = spitze;
	}

	@Override
	public Vector2[] toPoints(int hitboxAccuracy) {
		if(hitboxAccuracy<3)hitboxAccuracy=3;//untergrenze für Anzahl der Punkte
		Vector2[] hitbox = new Vector2[hitboxAccuracy];// Array, in das alles punkte geschrieben werden

// Die Punkte, an denen sich die Linien treffen, aus dem Konstruktor abgelesen
		hitbox[0]=basisl.makeGlobal(transform.position, transform.rotation);
		hitbox[1]=basisr.makeGlobal(transform.position, transform.rotation);
		hitbox[2]=spitze.makeGlobal(transform.position, transform.rotation);
		if(hitboxAccuracy>3)for(int i=3;i<hitboxAccuracy;i++) {
			if(i%3==0) {
				hitbox[i]=hitbox[0].lerp(hitbox[1],((double) (i - 2))/(hitboxAccuracy-3));

				
			}
			if(i%3==1) {
				hitbox[i]=hitbox[1].lerp(hitbox[2],((double) (i - 2))/(hitboxAccuracy-3));
				
			}
			if(i%3==2) {
				hitbox[i]=hitbox[2].lerp(hitbox[0],((double) (i - 2))/(hitboxAccuracy-3));
				
			}
		}
		
		return hitbox;
	}


	@Override
	public boolean collides(Vector2 point) {// quelle https://prlbr.de/2014/liegt-der-punkt-im-dreieck Ansatz 3(prüfen ob ein winkel größer 180° ist
		point = point.makeLocal(transform.position,transform.rotation);

		if( point.angle(basisl, basisr) < Math.PI
		&&  point.angle(spitze, basisl) < Math.PI 
		&&  point.angle(basisr, spitze) < Math.PI) {
			return true;
		}
		else {
			return false ;
		}
		
	}
	
	@Override
	protected int getStandardAccuracy(){
		return standardHitboxAccuracy;
	}
	
	public void paintMe(Graphics g) {}
}
