package spiel1;

/**
 * Interface um Rückstoß von Waffen auch nach dem Aufruf der Angriffsmethode durchzuführen
 */
public interface WeaponHitListener {//https://www.w3schools.com/java/java_interface.asp
    void onHit(Vector2 knockback);
    void onMiss();
}