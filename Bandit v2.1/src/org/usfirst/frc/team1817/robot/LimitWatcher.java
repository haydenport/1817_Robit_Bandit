package org.usfirst.frc.team1817.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LimitWatcher implements Runnable{
    private DigitalInput limit;
    private String name;
    
    private Thread t;
    
    public LimitWatcher(DigitalInput limit, String name){
        this.limit = limit;
        this.name  = name;
        
        t = new Thread(this, name);
        t.start();
    }
    
    public void run(){
        while(!Thread.interrupted()){
            SmartDashboard.putBoolean(name.concat(" State"), limit.get());
            
            Timer.delay(0.005);
        }
    }
}
