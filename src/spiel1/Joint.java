package spiel1;

import java.awt.*;

public class Joint extends GameObject {
    protected Joint[] connectedJoints;
    private double distance;
    private Vector2 lastOriginPos;//fürs zeichnen
    private Vector2 offsetSpeed;

    /**
     * für den Hauptjoint
     * @param transform
     * @param width
     * @param height
     * @param connectedJoints
     */
    public Joint(Transform transform, double width, double height, Joint[] connectedJoints){
        super(transform ,width ,height );//die Initiale Position wird durch distance im ersten Frame gesetzt
        this.connectedJoints= connectedJoints;
        this.distance = -1;
        this.offsetSpeed=new Vector2(0,0);
    }

    /**
     * für normalen Joint
     * @param connectedJoints
     * @param distance
     */
    public Joint(Joint[] connectedJoints,double distance){
        super(new Transform (new Vector2(0,0)),1,1);//die Initiale Position wird durch distance im ersten Frame gesetzt
        this.connectedJoints= connectedJoints;
        this.distance = distance;
        this.offsetSpeed=new Vector2(0,0);
    }

    /**
     * für Offset joint. offsetSpeed sollte unter .13 gehalten werden, da es sonst stärker ist als die gravitation
     * @param connectedJoints
     * @param distance
     * @param offsetSpeed
     */
    public Joint(Joint[] connectedJoints,double distance,Vector2 offsetSpeed){
        super(new Transform (new Vector2(0,0)),1,1);//die Initiale Position wird durch distance im ersten Frame gesetzt
        this.connectedJoints= connectedJoints;
        this.distance = distance;
        this.offsetSpeed = offsetSpeed;
    }

    public void moveJoint(Transform originTransform, double time) {
        if(distance<0)System.err.println("moveJoint should not be called at the head joint or distance is negative");
        for(Joint joint : connectedJoints){
            //addiert die Differenz der Geschwindigkeiten von diesem Gelenk und den Anderen gelenken zu seiner eigenen Geschwindigkeit, um alle Kräfte zu Berücksichtigen
            getTransform().speed = getSpeed().add(joint.getSpeed().makeLocal(getSpeed())).multiply(1);//alle Geschwindigkeiten Addieren
        }
        getTransform().speed = getSpeed().add(originTransform.speed.makeLocal(getSpeed())).multiply(1);//das gleiche wie drüber mit Originspeed


        //Bewegung mit Gravitation
        getTransform().speed= getTransform().speed.multiply((1-(0.52*time)));//Luftwiderstand
        getTransform().speed= getTransform().speed.add(new Vector2(0,-.13).multiply(time));
        getTransform().position=getPosition().add(getSpeed().multiply(time));
        System.out.println(getPosition().makeLocal(originTransform.position));

        //fügt den offset hinzu
        getTransform().position = getPosition().makeLocal(getTransform().position,getTransform().rotation).add(offsetSpeed).makeGlobal(getTransform().position,getTransform().rotation);

        // setzt das Object auf den richtigen Abstand, behält dabei den Winkel bei
        getTransform().position = getPosition().makeLocal(originTransform.position).normalize().multiply(distance).makeGlobal(originTransform.position);lastOriginPos=originTransform.position;
        getTransform().rotation = getPosition().makeLocal(originTransform.position).angle();


        for(Joint joint : connectedJoints) joint.moveJoint(getTransform(),time);// am ende die anderen Joints aufrufen, dadurch bewegt sich zuerst der nächste joint vom origin, und die anderen folgen.
    }
    @Override
    protected void paintMe(Graphics2D g2d) {
        g2d.drawLine((int) lastOriginPos.toJPanel().x(),(int) lastOriginPos.toJPanel().y(),(int) getPosition().toJPanel().x(),(int) getPosition().toJPanel().y());
        for(Joint joint : connectedJoints)joint.paintMe(g2d);//andere joints zeichnenx
    }
}
