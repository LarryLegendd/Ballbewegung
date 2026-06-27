package spiel1;

import java.awt.*;
import java.util.ArrayList;

public class EnemyArea {
   // Vector2 size; //temp
    int xpos;
    int ypos;
    ArrayList<Enemy> enemies = new ArrayList<>();
    ArrayList<Vector2> positions = new ArrayList<>();
    public EnemyArea(int xpos){
      //  this.size=size; //temp
        this.xpos=xpos;
    }

    public void GenerateEnemies(){
        for(Vector2 position : Bluenoise.generate(new Vector2(xpos*Spielfeld.AreaWidth,ypos*Spielfeld.AreaHeight),new Vector2((xpos+1)*Spielfeld.AreaWidth, (ypos+1)*Spielfeld.AreaWidth)))
        {
           enemies.add(new Enemy(new Transform(position),10,10,5));
        }
    }

    public void update(){
        for(Enemy enemy : enemies) enemy.moveGameObject(Spielfeld.getTimeMultiplyer());
        enemies.removeIf(Enemy::isDead);
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public void setEnemies(ArrayList<Enemy> enemies) {
        this.enemies = enemies;
    }

    public int getXpos() {
        return xpos;
    }

    public void setXpos(int xpos) {
        this.xpos = xpos;
    }

    protected void paintMe(Graphics g) {//TODO bild für spieler
        for(Enemy enemy: enemies) {
            enemy.paintMe(g);
            enemy.getHitbox().paintMe(g);
        }
    }
}
