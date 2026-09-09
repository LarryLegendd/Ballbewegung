package spiel1;
import java.util.function.BooleanSupplier;

public class Timer {
    private double time;
    private final double maxTime;
    private final BooleanSupplier event;
    private boolean isFinished;
    private boolean shouldReset;
    private boolean resetting;
    /**
     * return true für restart vom Timer nach eventtrigger
     * return false für beenden von Timer nach eventtrigger
     * nach return false wird der Timer nicht zurückgesetzt
     * @param length
     * @param event
     */
    public Timer(double length, BooleanSupplier event){
        isFinished=false;
        maxTime=length;
        time=length;
        this.event = event;
        resetting=false;
    }

    /**
     * return true für restart vom Timer nach eventtrigger
     * return false für beenden von Timer nach eventtrigger
     * @param length
     * @param event
     * @param resetting entscheidet ob der Timer nach return false zurückgesetzt wird.
     */
    public Timer(double length, BooleanSupplier event,boolean resetting){
        isFinished=false;
        maxTime=length;
        time=length;
        this.event = event;
        this.resetting=resetting;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void update(double deltaTime){//zählt timer runter
        time-=deltaTime;
        System.out.println("Timer time: " + time);
        if(time<=0){
            isFinished=!event.getAsBoolean(); //führt das event aus und läuft weiter wenn true returnt wird
            time=maxTime;//für timer die mehrfach durchlaufen
            if(isFinished&&resetting)shouldReset=true;//zurücksetzen wenn der Timer das machen soll
        }
    }

    /**
     * führt das Event einmal aus und beendet den Timer dannach
      */
    public void Finish(){
        event.getAsBoolean();
        isFinished=true;
        if(resetting)shouldReset=true;
    }

    /**
     * beendet den Timer ohne das Event auszuführen
     */
    public void setFinished(){
        isFinished= true;
        if(resetting)shouldReset=true;
    }
    public boolean shouldReset(){return shouldReset;}


    public void reset(){
        time = maxTime;
        isFinished=false;
        shouldReset=false;
    }
}
