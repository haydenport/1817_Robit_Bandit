package org.usfirst.frc.team1817.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class EncoderWatcher implements Runnable{
    private Encoder encoder;
    private String name;
    
    private Thread t;
    
    public EncoderWatcher(Encoder encoder, String name){
        this.encoder = encoder;
        this.name    = name;
        
        t = new Thread(this, name);
        t.start();
    }
    
    public void run(){
        while(!Thread.interrupted()){
            SmartDashboard.putNumber(name.concat(" Value"), encoder.getDistance());
            SmartDashboard.putNumber(name.concat(" Rate"), encoder.getRate());
            
            Timer.delay(0.005);
        }
    }
}
