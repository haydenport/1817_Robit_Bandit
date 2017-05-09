package org.usfirst.frc.team1817.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Gear implements Runnable {
    private PWMSpeedController motor;
    private XboxController xbox, reset;
    //private AnalogInput pot;
    private Encoder encoder;
    
    private int state, offset;
    private double min, max, score;
    
    private Thread t;
    
    //public Gear(PWMSpeedController motor, AnalogInput pot, XboxController xbox, double min, double max, double score){
    public Gear(PWMSpeedController motor, Encoder encoder, XboxController xbox, XboxController reset, int min, int max){
        int score = (( Math.abs( max - min )) / 2 );
    	this.motor = motor;
        //this.pot   = pot;
        this.encoder = encoder;
        this.xbox  = xbox;
        this.reset = reset;
        
        this.offset = 0;
        
        this.state = 0;
        this.min   = min;
        this.max   = max;
        this.score = score;
        
        t = new Thread(this, "Blah");
        t.start();
    }
    
    public double getSpeed(){
        double encoderVal = encoder.get() - offset;
    	switch(state){
            case 3: // Score
                return this.score - encoderVal;
            	//return this.score - pot.pidGet();
            case 2: // Up
            	return this.max - encoderVal;
                //return this.max - pot.pidGet();
            case 1: // Down
            	return this.min - encoderVal;
            	//return this.min - pot.pidGet();
            case 0: // Disabled
            default: break;
        }
        
        return 0.0;
    }
    
    public void score(){
        state = 3;
    }
    
    public void up(){
        state = 2;
    }
    
    public void down(){
        state = 1;
    }
    
    public void disable(){
        state = 0;
    }
    
    public void run(){
        while(!Thread.interrupted()){
        	double speed = 0.0;
            if(xbox.getAButton()){
                this.down();
            } else if(xbox.getXButton()){
                this.score();
            } else if(xbox.getYButton()){
                this.up();
            } else if(reset.getBumper(GenericHID.Hand.kRight)){
            	this.disable();
            	offset = encoder.get() + 16;
            	speed = -1.0;
            }
            
	        if(state != 0){
	            double max   = 1.0;
	            speed = this.getSpeed() / 12.0;
	            
	            if(speed < -max){
	                speed = -max;
	            } else if(speed > max){
	                speed = max;
	            }
        	}

            motor.set(speed);
            
            SmartDashboard.putNumber("Gear Encoder", encoder.get());
            //SmartDashboard.putNumber("Gear POT", pot.pidGet());
            SmartDashboard.putNumber("Gear Speed", speed);
            
            Timer.delay(0.005);
        }
    }
}
