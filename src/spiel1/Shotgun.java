package spiel1;

import java.awt.*;
import java.util.ArrayList;

public class Shotgun extends Weapon{

    private KreisTeilHitbox hitbox;

    private final double shotgunEnemyKnockback = 4;

    //rendern
    private Vector2 drawEndpoint1;
    private Vector2 drawEndpoint2;
    private Vector2 drawPlayerPos;

    private double[][] levelArr = {
//			winkel,		range,	    kb, Munition, Preis
            {Math.PI/5,	    200,	22, 2,	    3},
            {Math.PI/4.85,  210, 	24,	2,      5},
            {Math.PI/4.7,   220, 	26,	2,      8},
            {Math.PI/4.55,  230, 	28,	3,      10},
            {Math.PI/4.4, 	240,    30, 3,	    15},
            {Math.PI/4.25,  250, 	32,	3,      20},
            {Math.PI/4.1,   260, 	34,	4,      30},
            {Math.PI/3.95,  270, 	36,	4,      40},
            {Math.PI/3.8, 	280,    38, 4,	    50},
            {Math.PI/3.65,  300,	40,	5,	    100},
    };

    private double angle = levelArr[0][0];
    private double range = levelArr[0][1];
    private double shotgunKnockback = levelArr[0][2];
    private double shotgunMaxMunition = levelArr[0][3];

    private double munition = shotgunMaxMunition;

    Vector2 knockback = new Vector2(0,0);

    private CameraController cameraController;

    public Shotgun(Transform playertransform, CameraController cameraController) {
        this.playertransform= playertransform;
        hitbox = new KreisTeilHitbox(range, angle , playertransform);
        this.cameraController = cameraController;
    }

    @Override
    public void addMunition(double munition){
        this.munition += munition;
        if(munition > shotgunMaxMunition) this.munition = shotgunMaxMunition;
    }

    @Override
    public void hit(Vector2 mauspos, ArrayList<Enemy> enemies, WeaponHitListener listener) {
        if(munition>=0) {
            munition--;
            Vector2 mausAbstand = mauspos.makeLocal(playertransform.position);

            //fürs Zeichnen Punkte merken
            drawEndpoint1 = new Vector2(range, 0).rotate(angle / 2).makeGlobal(hitbox.transform.position, hitbox.transform.rotation);
            drawEndpoint2 = new Vector2(range, 0).rotate(-(angle / 2)).makeGlobal(hitbox.transform.position, hitbox.transform.rotation);
            drawPlayerPos = new Vector2(0, 0).makeGlobal(hitbox.transform.position, hitbox.transform.rotation);


            showTimer();//anzeigen

            double hitEnemies = 0;

            for (Enemy enemy : enemies) {

                //Kollidieren
                if (hitbox.collides(enemy.getHitbox())) {
                    enemy.schadenNehmen(1);
                    enemy.addSpeed(mausAbstand.normalize().multiply(shotgunEnemyKnockback));

                    hitEnemies++;
                }
            }
            knockback = mausAbstand.normalize().multiply(shotgunKnockback).reverse();//das liefert die gegenrichtung.

            munition += hitEnemies/5;
            if(munition > shotgunMaxMunition) munition = shotgunMaxMunition;

            cameraController.shake(100);
            listener.onHit(knockback);

        }
    }

    @Override
    public double getNextPrice() {
        if(level+1<levelArr.length )return levelArr[level+1][4];
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
        shotgunKnockback = levelArr[level][2];
        shotgunMaxMunition = levelArr[level][3];
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

    @Override
    public void reset(){
        munition=shotgunMaxMunition;
    }
}
