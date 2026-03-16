package spiel1;

public class Test {
	
	
	public static void main(String[] args) {
		
		
//		Vector2 testPoint1 = new Vector2(-7.400030, -19.797); 
//		Vector2 testPoint2 = new Vector2(10.21921,-10.46142); 
//		Vector2 testPoint3 = new Vector2(-16.32274, -2.95693); 
//		
//		System.out.println(testPoint1.angle(testPoint2,testPoint3)/Math.PI*180);
		
		
		
		
		Vector2 testPoint = new Vector2(99.999999, 0);       // Punkt in Weltkoordinaten
		Vector2 position = new Vector2(0, 0);         // Hitbox position
		double angle = 0;//Math.PI;      
		Hitbox testbox = new TriangleHitbox(50, 100, new Transform(position, angle));
		testbox.setPosition(position);
		System.out.println(testbox.collides(testPoint));
		
	}
}
