package spiel1;

public class Transform {
	public Vector2 position;
	public double rotation;
	public Vector2 speed;
	
	public Transform(Vector2 position, double rotation,Vector2 speed){
		this.position =position;
		this.rotation=rotation;
		this.speed = speed;
	}
	
	public Transform(Vector2 position, double rotation){
		this.position =position;
		this.rotation=rotation;
		this.speed = new Vector2(0,0);
	}
	public Transform(Vector2 position){
		this.position =position;
		this.rotation=0;
		this.speed = new Vector2(0,0);
	}
}
