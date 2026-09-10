package spiel1;
import java.util.ArrayList;
import java.util.function.BooleanSupplier;

public class TimerManager {
    private static ArrayList<Timer> timers =new ArrayList<Timer>();
    private static ArrayList<Timer> timersToAdd =new ArrayList<Timer>();

    /**
     * Startet einen Timer. Der Timer wird zurückgesetzt, um wiederverwendung möglich zu machen.
     * @param timer
     */
    public static void addTimer(Timer timer){
        if (!timers.contains(timer) && !timersToAdd.contains(timer)) {
            timer.reset();
            timersToAdd.add(timer);//Über buffer liste, da wenn ein timer beim enden eines anderen timers erstellt wird der neue timer während des durchlaufens der arraylist hinzugefügt werdenn würde.
        }
    }

    public static void update(double deltaTime){
        timers.addAll(timersToAdd);
        timersToAdd.clear();
        for(Timer timer: timers){
            if(!timer.isFinished())timer.update(deltaTime);
        }
        timers.removeIf(Timer :: isFinished);
    }
}