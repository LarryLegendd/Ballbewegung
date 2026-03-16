package spiel1;

import java.awt.Color;
import java.awt.Graphics;

public class TriangleHitbox extends Hitbox{
	
	private Vector2 basisl;//links unten
	private Vector2 basisr;//rechts unten
	private Vector2 spitze;//
	
	private int standardHitboxAccuracy = 64;
	
	private Vector2 lastpoint;//temp
	private Vector2 lastppre;
	
//	public TriangleHitbox(Vector2 ursprung, double breite, double hoehe, double drehWinkel) {
//		basis1=new Vector2(0,breite/2).rotate(drehWinkel).add(ursprung);//richtige länge und dann drehen und dann auf spieler verschieben
//		basis2=new Vector2(0,-breite/2).rotate(drehWinkel).add(ursprung);
//		//Spitze
//		spitze= new Vector2(hoehe,0).rotate(drehWinkel).add(ursprung);
//	}
	public TriangleHitbox(double breite, double hoehe,Transform transform) {
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
		if(hitboxAccuracy<3)hitboxAccuracy=3;
		Vector2[] hitbox = new Vector2[hitboxAccuracy];
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
		
		//if(enemy.getPosition().distanceTo(playerpos)<range+Math.sqrt(enemy.getWidth()*enemy.getWidth() + enemy.getHeight()*enemy.getHeight())){//optimierung das nur nah genuge gegner geprüft werden
		lastppre=point;
		point = point.makeLocal(transform.position,transform.rotation);
		lastpoint=point;
//		System.out.println("Punkt1: "+lastpoint+" Punkt2: "+lastppre+" position: "+transform.position+" winkel: "+transform.rotation);
//		System.out.println(point.angle(basis1, basis2)/Math.PI*180+" b1 b2");
//		System.out.println(point+" pointpos");
//		System.out.println(basis1+" basis1");
//		System.out.println(basis2+" basis2");
		
//		System.out.println(point.angle(spitze, basis1)> Math.PI);
//		System.out.println(point.angle(basis2, spitze)> Math.PI );
//		System.out.println("point: "+point+" basis1: "+basisl +" basis2: "+basisr);
//		System.out.println(point.angle(basisl, basisr)/Math.PI*180+" p12");
//		System.out.println(point.angle(spitze, basisl)/Math.PI*180+" ps1");
//		System.out.println(point.angle(basisr, spitze)/Math.PI*180+" p2s");	
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
	protected int getStandardAccuracy() {
		// TODO Auto-generated method stub
		return standardHitboxAccuracy;
	}
	
	public void paintMe(Graphics g) {
		 
		 if(doDraw) //wenn aktiviert zeichnen
		 {
			 if(lastpoint!=null) {
				 lastpoint.toJPanel().draw(g);
				 
				 lastppre.toJPanel().draw(g);
				 
			 }
		 }
		 
	}
}
