package org.usfirst.frc.team1817.robot;

import edu.wpi.first.wpilibj.*;

public class Climber implements Runnable {
    private PWMSpeedController motor;
    private XboxController xbox;
    
    private boolean enabled;
    
    private Thread t;
    
    public Climber(PWMSpeedController motor, XboxController xbox){
        this.motor = motor;
        this.xbox  = xbox;
        
        enabled = false;
        
        t = new Thread(this, "Climber");
        t.start();
    }
    
    public void enable(){
        enabled = true;
    }
    
    public void disable(){
        enabled = false;
    }
    
    public void run(){
        while(!Thread.interrupted()){
            if(enabled){
                double LT = xbox.getTriggerAxis(GenericHID.Hand.kLeft);
                double RT = xbox.getTriggerAxis(GenericHID.Hand.kRight);
                double speed = -(LT + RT);
                
                if(Math.abs(speed) < 0.1){
                    motor.stopMotor();
                } else {
                    motor.set(speed);
                };
            }
            
            Timer.delay(0.005);
        }
    }
}
