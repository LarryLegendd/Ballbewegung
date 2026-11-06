package spiel1;

/**
 * manipulation der Zeit, ohne das gesamte Spielfeld mitzugeben
 */
public interface TimeController {
	void slowTime();
	void normalTime();
	boolean isTimeSlowed();
	void slowTimeFor(int millis);
	double getTimeSpeed();
}
