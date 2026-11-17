package spiel1;

import java.awt.*;
import java.util.ArrayList;

public class Sniper extends Weapon {

    private RectHitbox hitbox;

    private final double sniperEnemyKnockback = 10;
    private Vector2 mausPos;

    private double[][] levelArr = {
//    sichtbarkeit, kb, Munition, Preis
            {255, 	32,     1,	    3},
            {240,   34,	    1,      5},
            {210, 	36,	    1,      8},
            {190,   38,	    1,      10},
            {160,   40,     1,	    15},
            {127,   42,     1,      20},
            {103,   44,	    1,      30},
            {87,    46,	    1,      40},
            {43,    48,     1,	    50},
            {0, 	50,	    2,	    100},
    };

    private double sichtbarkeit = levelArr[0][0];
    private double sniperKnockback = levelArr[0][1];
    private double sniperMaxMunition = levelArr[0][2];

    private double munition = sniperMaxMunition;

    Vector2 knockback = new Vector2(0,0);

    private ArrayList<Enemy> enemies;
    private WeaponHitListener weaponlistener;

    private CameraController cameraController;

    private TimeController timeController;
    private MouseController mouseController;

    public Sniper(Transform playertransform, CameraController cameraController, TimeController timeController, MouseController mouseController) {
        this.playertransform= playertransform;
        hitbox = new RectHitbox(1,1,playertransform);
        this.cameraController = cameraController;
        this.timeController = timeController;
        this.mouseController = mouseController;
    }

    @Override
    public void addMunition(double munition){
        this.munition += munition;
        if(munition > sniperMaxMunition) this.munition = sniperMaxMunition;
    }

    @Override
    public void hit(Vector2 mauspos, ArrayList<Enemy> enemies, WeaponHitListener listener) {
        if(munition>=0) {
            this.enemies = enemies;
            this.weaponlistener=listener;

            timeController.slowTime();
            mouseController.cursorSniper(sichtbarkeit);
        }

    }

    @Override
    public void clickReleased(Vector2 mauspos) {
        if(munition>=0) {

            Vector2 mausAbstand = mauspos.makeLocal(playertransform.position);
            boolean hit = false;
            if(enemies != null){
                for (Enemy enemy : enemies) {

                    if (enemy.getHitbox().collides(mauspos)) {
                        enemy.schadenNehmen(5);
                        enemy.addSpeed(mausAbstand.normalize().multiply(sniperEnemyKnockback));
                        hit = true;
                        break;
                    }
                }
            }
            munition--;
            knockback = mausAbstand.normalize().multiply(sniperKnockback).reverse();//das liefert die gegenrichtung.

            if(hit) {
                cameraController.shake(100);//nur beim treffer screenshake als zeichen
            }
            weaponlistener.onHit(knockback);
            timeController.normalTime();
            mouseController.cursorX();
        }
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
        sichtbarkeit = levelArr[level][0];
        sniperKnockback = levelArr[level][1];
        sniperMaxMunition = levelArr[level][2];
    }

    @Override
    public void paintMe(Graphics g){}

    @Override
    public Hitbox getHitbox() {
        return hitbox;
    }

    @Override
    public void reset(){
        munition=sniperMaxMunition;
    }
    

}
