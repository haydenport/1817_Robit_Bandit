package org.usfirst.frc.team1817.robot;

public class Toggle {
    private boolean tracker, output;
    
    public Toggle(){
        tracker = false;
        output = false;
    }
    
    public void update(boolean input){
        if(input){
            if(!tracker){
                tracker = true;
                output = !output;
            }
        } else {
            tracker = false;
        }
    }
    
    public void set(boolean value){
        output = value;
    }
    
    public boolean toggled(){
        return output;
    }
}
