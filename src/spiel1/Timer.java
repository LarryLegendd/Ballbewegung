package spiel1;
import java.util.function.BooleanSupplier;

public class Timer {
    private double time;
    private final double maxTime;
    private final BooleanSupplier event;
    private boolean isFinished;

    String name;


    /**
     * return true für restart vom Timer nach eventtrigger
     * return false für beenden von Timer nach eventtrigger
     * @param length
     * @param event
     */
    public Timer(double length, BooleanSupplier event){
        isFinished=false;
        maxTime=length;
        time=length;
        this.event = event;
        this.name=name;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void update(double deltaTime){//zählt timer runter
        time-=deltaTime;
        if(time<=0){
            isFinished=!event.getAsBoolean(); //führt das event aus und läuft weiter wenn true returnt wird
            time=maxTime;//für timer die mehrfach durchlaufen
        }
    }

    /**
     * führt das Event einmal aus und beendet den Timer dannach
      */
    public void Finish(){
        event.getAsBoolean();
        isFinished=true;
    }

    /**
     * beendet den Timer ohne das Event auszuführen
     */
    public void setFinished(){
        isFinished= true;
    }

    public void reset(){
        time = maxTime;
        isFinished=false;
    }
}
