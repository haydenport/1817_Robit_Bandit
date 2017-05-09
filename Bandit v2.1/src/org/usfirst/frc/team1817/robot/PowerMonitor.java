package org.usfirst.frc.team1817.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class PowerMonitor implements Runnable {
    private PowerDistributionPanel pdp;
    
    private Thread t;
    
    public PowerMonitor(){
        pdp = new PowerDistributionPanel(0);
        
        t = new Thread(this, "Power");
        t.start();
    }
    
    public void run(){
        double maxA = 0.0;
        double minV = 18.0;
        
        SmartDashboard.putNumber("PDP Reset", 0.0);
        
        while(!Thread.interrupted()){
            double a = pdp.getTotalCurrent();
            double v = pdp.getVoltage();
            
            SmartDashboard.putNumber("PDP Volts", v);
            SmartDashboard.putNumber("PDP Amps", a);
            
            minV = Math.min(minV, v);
            maxA = Math.max(maxA, a);
            
            SmartDashboard.putNumber("PDP Max Amps", maxA);
            SmartDashboard.putNumber("PDP Min Volts", minV);
            
            if(SmartDashboard.getNumber("PDP Reset", 0.0) > 0.1){
                SmartDashboard.putNumber("PDP Reset", 0.0);
                maxA = 0.0;
            }
            
            Timer.delay(0.005);
        }
    }
}
