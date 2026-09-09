package spiel1;
import java.util.ArrayList;
import java.util.function.BooleanSupplier;

public class TimerManager {
    private static ArrayList<Timer> timers =new ArrayList<Timer>();
    private static ArrayList<Timer> timersToAdd =new ArrayList<Timer>();
    private static ArrayList<Timer> timersToReset = new ArrayList<Timer>();

    public static void addTimer(Timer timer){
        if (!timers.contains(timer) && !timersToAdd.contains(timer)) {
            timersToAdd.add(timer);//Über buffer liste, da wenn ein timer beim enden eines anderen timers erstellt wird der neue timer während des durchlaufens der arraylist hinzugefügt werdenn würde.
        }
    }

    public static void update(double deltaTime){
        timers.addAll(timersToAdd);
        timersToAdd.clear();
        for(Timer timer: timers){
            if(!timer.isFinished())timer.update(deltaTime);

            if(timer.shouldReset())timersToReset.add(timer);
        }
        timers.removeIf(Timer :: isFinished);

        for(Timer timer : timersToReset)timer.reset();
        timersToReset.clear();

    }
}