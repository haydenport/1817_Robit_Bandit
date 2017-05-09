package org.usfirst.frc.team1817.robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class RobotCam implements Runnable {
    private boolean enabled;
    private double[] pos;
    private double[] c;
    private double twoTimer;
    private Thread t;
    
    public RobotCam(){
        enabled = false;
        twoTimer = 0.0;
        
        pos = new double[2];
        pos[0] = -1;
        pos[1] = -1;
        
        c = new double[0];
    
        t = new Thread(this, "Cam");
        t.start();
    }
    
    public void enable(){
        enabled = true;
    }
    
    public void disable(){
        enabled = false;
    }
    
    public double[] getPos(){
        return pos;
    }
    
    public boolean twoContours(){
        return (Timer.getFPGATimestamp() - twoTimer) < 0.25;
    }
    
    public void run(){
        while(!Thread.interrupted()){
            if (enabled) {
                c = NetworkTable.getTable("SmartDashboard").getNumberArray("Contours", c);
                
                if (c.length == 4) {
                    twoTimer = Timer.getFPGATimestamp();
                    
                    pos[0] = (c[0] + c[2]) / 2;
                    pos[1] = (c[1] + c[3]) / 2;
                    
                    SmartDashboard.putNumber("Peg X", pos[0]);
                    SmartDashboard.putNumber("Peg Y", pos[1]);
                }
            } else {
                twoTimer = 0.0;
            }
            
            Timer.delay(0.005);
        }
    }
}
