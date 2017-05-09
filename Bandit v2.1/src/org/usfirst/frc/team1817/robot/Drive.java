package org.usfirst.frc.team1817.robot;

import edu.wpi.first.wpilibj.*;

public class Drive implements Runnable {
    private RobotDrive drive;
    private DoubleSolenoid shifter1, shifter2;
    private XboxController xbox;
    
    private Toggle tankDrive;
    
    private boolean enabled;
    
    private Thread t;
    
    public Drive(RobotDrive drive, DoubleSolenoid shifter1, DoubleSolenoid shifter2, XboxController xbox){
        this.drive   = drive;
        this.shifter1 = shifter1;
        this.shifter2 = shifter2;
        this.xbox    = xbox;
        
        tankDrive = new Toggle();
        
        enabled = false;
        
        t = new Thread(this, "Drive Thread");
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
                double LY = xbox.getY(GenericHID.Hand.kLeft);
                double RX = xbox.getX(GenericHID.Hand.kRight);
                double RY = xbox.getY(GenericHID.Hand.kRight);
                
                boolean RB = xbox.getBumper(GenericHID.Hand.kRight);
                
                tankDrive.update(xbox.getStartButton());
                
                if(tankDrive.toggled() && (Math.abs(LY) > 0.0 || Math.abs(RY) > 0.0)){
                    drive.tankDrive(LY, RY);
                } else if(Math.abs(LY) > 0.1 || Math.abs(RX) > 0.1){
                    if(RB){
                        drive.arcadeDrive(LY, RX * 0.66); // Half speed turn on high gear
                    } else {
                        drive.arcadeDrive(LY, RX);
                    }
                } else {
                    drive.stopMotor();
                }
                
                shifter1.set(RB ? DoubleSolenoid.Value.kForward : DoubleSolenoid.Value.kReverse);
                shifter2.set(RB ? DoubleSolenoid.Value.kForward : DoubleSolenoid.Value.kReverse);
            }
            
            Timer.delay(0.005);
        }
    }
}
