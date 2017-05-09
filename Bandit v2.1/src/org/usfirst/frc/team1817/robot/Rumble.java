package org.usfirst.frc.team1817.robot;

import edu.wpi.first.wpilibj.*;

public class Rumble implements Runnable{
    XboxController stick;
    double endTime;
    Thread t;
    
    public Rumble(XboxController stick){
        this.stick = stick;
        endTime = Timer.getFPGATimestamp();
        
        t = new Thread(this, String.valueOf(Math.random()));
        t.start();
    }
    
    public void rumbleForTime(double seconds){
        endTime = Timer.getFPGATimestamp() + seconds;
    }
    
    public void run(){
        while(!Thread.interrupted()){
            double time = Timer.getFPGATimestamp();
            if(endTime > time){
                stick.setRumble(XboxController.RumbleType.kLeftRumble, 1.0);
                stick.setRumble(XboxController.RumbleType.kRightRumble, 1.0);
            } else {
                stick.setRumble(XboxController.RumbleType.kLeftRumble, 0.0);
                stick.setRumble(XboxController.RumbleType.kRightRumble, 0.0);
            }
            
            Timer.delay(0.005);
        }
    }
}
