package spiel1;

public class PointingJoint extends Joint{

    private Vector2 pointingAt;
    public PointingJoint(Joint[] connectedJoints,double distance, Vector2 pointingAt){
       super(connectedJoints,distance);
       this.pointingAt=pointingAt;
    }
    @Override
    public void moveJoint(Transform originTransform, double time){
        getTransform().position= pointingAt;
        lastOriginPos=originTransform.position;
        // setzt das Object auf den richtigen Abstand, behält dabei den Winkel bei
        getTransform().position = getPosition().makeLocal(originTransform.position).normalize().multiply(distance).makeGlobal(originTransform.position);
        getTransform().rotation = getPosition().makeLocal(originTransform.position).angle();

        for(Joint joint : connectedJoints) joint.moveJoint(getTransform(),time);// am ende die anderen Joints aufrufen, dadurch bewegt sich zuerst der nächste joint vom origin, und die anderen folgen.
    }
    public void setPointingPosition(Vector2 position){
       pointingAt=position;
    }
}
