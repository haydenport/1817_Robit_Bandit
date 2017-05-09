package org.usfirst.frc.team1817.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class GyroWatcher implements Runnable {
    ADXRS450_Gyro gyro;
    Thread t;
    
    public GyroWatcher(ADXRS450_Gyro gyro){
        this.gyro = gyro;
        
        gyro.calibrate();
        
        Timer.delay(2); //TODO: Recalibrate when connected to DS on FMS
        
        gyro.reset();
        
        t = new Thread(this, "GyroWatcher");
        t.start();
    }
    
    public void reset(){
        gyro.reset();
    }
    
    public double getAngle(){
        return gyro.getAngle();
    }
    
    public double getRate(){
        return gyro.getRate();
    }
    
    public void run(){
        while(!Thread.interrupted()){
            SmartDashboard.putNumber("Gyro Angle", gyro.getAngle());
            SmartDashboard.putNumber("Get Rate", gyro.getRate());
            
            Timer.delay(0.005);
        }
    }
}
