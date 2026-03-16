package spiel1;

public interface TimeController {
	void slowTime();
	void normalTime();
	boolean isTimeSlowed();
	void slowTimeFor(int millis);
}
