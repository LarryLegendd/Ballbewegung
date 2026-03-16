package spiel1;

public interface WeaponHitListener {//TODO Quelle finden für interface
    void onHit(Vector2 knockback);
    void onMiss();
}
