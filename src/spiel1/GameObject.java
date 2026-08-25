package spiel1;

public abstract class GameObject{//Die oberklasse für gameobjekte
    
    protected double width;
    protected double height;
    //private Vector2 speed;
    private RectHitbox hitbox;
    private Transform transform;
    
    public GameObject(Transform transform, double width, double height) {//speed wird automatisch 0 quelle: https://stackoverflow.com/questions/1182153/constructor-overloading-in-java-best-practice
    	this.transform = transform;
        this.width = width;
        this.height = height;
       
        hitbox = new RectHitbox(this);
    }


    public Vector2 getPosition() {
        return transform.position;
    }
    
    public Transform getTransform() {
    	return transform;
    }
    
    public RectHitbox getHitbox() {
    	return hitbox;
    }

    public void setPosition(Vector2 objectPosition) {
        this.transform.position = objectPosition;
        hitbox.setPosition(objectPosition);
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
    
    public Vector2 getSpeed() {
        return transform.speed;
    }

    public void setSpeed(Vector2 s) {
        this.transform.speed = s;
    }

    public void addSpeed(Vector2 s) {
    	transform.speed = transform.speed.add(s);
    	
    }
    
    
    
    
    public void moveGameObject(double time) {// bewegt das objekt
    	transform.speed = transform.speed.multiply(1-(0.02*time));//Luftwiederstand
    	transform.position = transform.position.add(transform.speed.multiply(time));// bewegung mit korrektur, wenn die zeit verlangsamt ist
        hitbox.setPosition(transform.position);// Hitboxposition aktualisieren
    }

    
    protected abstract void paintMe(java.awt.Graphics2D g2d);
}
