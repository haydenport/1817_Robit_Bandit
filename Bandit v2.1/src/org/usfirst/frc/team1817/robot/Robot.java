package org.usfirst.frc.team1817.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.*;

public class Robot extends SampleRobot{
    private XboxController driveXbox, manipulatorXbox;
    private VictorSP left1, left2, right1, right2, climberMotor, gears, shooterFront, shooterBack;
    private RobotDrive driveMotors;
    private Compressor compressor;
    private DoubleSolenoid shifter1, shifter2, grabber;
    private DigitalInput gearSwitch, pegSwitch;
    private Encoder leftE, rightE, shootE, gearE;
//    private AnalogInput gearPOT;
    
    private Drive drive;
    private Climber climber;
    private Gear gear;
    private Rumble gearRumble;
    private RobotCam cam;
    private GyroWatcher gw;
    
    private Toggle grabberForward;
    
    private SendableChooser<Integer> autonChooser;
    
    private double shootSpeed;
    private boolean shootPeaked, gearSwitched, dontPeg;
    
    public Robot(){ // Initializations
        // Joysticks/Controllers
        driveXbox       = new XboxController(0);
        manipulatorXbox = new XboxController(1);
        
        //Camera servers temporary
        //CameraServer.getInstance().startAutomaticCapture();
        //CameraServer.getInstance().startAutomaticCapture();
        
        // Motors
        left1        = new VictorSP(0); // 5
        left2        = new VictorSP(1); // 6
        right1       = new VictorSP(5); // 0
        right2       = new VictorSP(6); // 1
        climberMotor = new VictorSP(2); // Splitter for two motors
        gears        = new VictorSP(3);
        shooterBack  = new VictorSP(8);
        shooterFront = new VictorSP(9); // Splitter for two motors
        
        // Drive Groups
        driveMotors = new RobotDrive(left1, left2, right1, right2);
        
        // Compressor
        
        compressor = new Compressor();
        
        // Solenoids
        shifter1 = new DoubleSolenoid(7, 0);
        shifter2 = new DoubleSolenoid(6, 1);
        grabber = new DoubleSolenoid(5, 2);
        
        // Digital Inputs
        gearSwitch = new DigitalInput(0);
        pegSwitch  = new DigitalInput(5);
        
        new LimitWatcher(gearSwitch, "Gear Switch");
        new LimitWatcher(pegSwitch, "Peg Switch");
        
        // Encoders
        leftE  = new Encoder(2, 1);
        rightE = new Encoder(3, 4);
        shootE = new Encoder(7, 6);
        gearE = new Encoder(8,9);
        
        double encoderPulse = Math.PI * 4 / 1450.0 / 12.0;
        leftE.setDistancePerPulse(encoderPulse);
        rightE.setDistancePerPulse(encoderPulse);
        shootE.setDistancePerPulse(60.0 / Math.PI / 2);
        gearE.setDistancePerPulse(60.0 / Math.PI / 2);
        new EncoderWatcher(leftE, "Left Encoder");
        new EncoderWatcher(rightE, "Right Encoder");
        new EncoderWatcher(shootE, "Shooter Encoder");
        new EncoderWatcher(gearE, "Gear Encoder");
        // Analog Input
//        gearPOT = new AnalogInput(2);
        
        // Gyro
        gw = new GyroWatcher(new ADXRS450_Gyro());
        
        // Separate Threads
        drive   = new Drive(driveMotors, shifter1, shifter2, driveXbox);
        climber = new Climber(climberMotor, manipulatorXbox);
        gear    = new Gear(gears, gearE, driveXbox, manipulatorXbox, 120, 0);
      //gear    = new Gear(gears, gearPOT, driveXbox, 2.1, 4.8, 4.0);
        
        gearRumble = new Rumble(driveXbox);
        
        new PowerMonitor();
        cam = new RobotCam();
        
        // Toggles
        grabberForward = new Toggle();
        
        autonChooser = new SendableChooser<Integer>();
        
        autonChooser.addObject("Gear Left", 0);
        autonChooser.addDefault("Gear Center", 1);
        autonChooser.addObject("Gear Right", 2);
        autonChooser.addObject("Drive Forward Only", 3);
        SmartDashboard.putData("Autonomous", autonChooser);
        
        SmartDashboard.putNumber("Peg Center", 155.0);
        
        shootSpeed   = 1.0;
        shootPeaked  = false;
        gearSwitched = false;
        dontPeg      = false;
    }
    
    @Override
    public void autonomous(){
        setSafety(false);
        drive.disable();
        climber.disable();
        
        cam.enable();
        
        gear.up();
        
        shifter1.set(DoubleSolenoid.Value.kReverse);
        shifter2.set(DoubleSolenoid.Value.kReverse);
        grabber.set(DoubleSolenoid.Value.kForward);
        
        double max = 0.6;
        switch((int)autonChooser.getSelected()){
            case 0: // Gear Left
                autoDrive(6.0, 3.0, 1.0);
                turnAngle(30.0, 0.75, 0.7);
                turnContour(0.7, 1.25);

                driveTillContours(0.6);
                centerContours(0.6);

                Timer.delay(0.5);
                
                autoDrive(3.0, 1.0, 0.6);
                autoPeg(0.8);

                break;
            case 1: // Gear Center
                driveTillContours(0.6);
                centerContours(0.6);
                
                Timer.delay(0.5);
                
                autoDrive(3.0, 1.0, max);
                autoPeg(0.8);
                break;
            case 2: // Gear Right
                autoDrive(6.0, 3.0, 1.0);
                turnAngle(-30.0, 0.75, 0.7);
                turnContour(-0.7, 1.25);

                driveTillContours(0.6);
                centerContours(0.6);

                Timer.delay(0.5);
                
                autoDrive(3.0, 1.0, 0.6);
                autoPeg(0.8);
                
                break;
            case 3: // Drive Forward Only
                autoDrive(9.25, 4.0, max);
                break;
            default: break;
        }
        
        cam.disable();
        
        setSafety(true);
    }
    
    public void setSafety(boolean enabled){ // Add EVERY Motor and Drive Group to this function
        // Motors
        left1.setSafetyEnabled(enabled);
        left2.setSafetyEnabled(enabled);
        right1.setSafetyEnabled(enabled);
        right2.setSafetyEnabled(enabled);
        climberMotor.setSafetyEnabled(enabled);
        gears.setSafetyEnabled(enabled);
        
        // Drive Groups
        driveMotors.setSafetyEnabled(enabled);
    }
    
    public double calcSpeed(double max, double current, double target){
        double diff = target - current;
        
        if(diff < -1.0){
            return -max;
        } else if(diff > 1.0){
            return max;
        }
        
        return max * diff;
    }
    
    public void turnAngle(double angle, double time, double max){
        gw.reset();
        
        double start = Timer.getFPGATimestamp();
        while((Timer.getFPGATimestamp() - start < time) && Math.abs(gw.getAngle()) < Math.abs(angle)){
            double leftSpeed  = 0.0;
            double rightSpeed = 0.0;
            
            if (angle < 0) {
                leftSpeed  = max;
                rightSpeed = -max;
            } else {
                leftSpeed  = -max;
                rightSpeed = max;
            }
            
            driveMotors.tankDrive(leftSpeed, rightSpeed);
        }
        driveMotors.stopMotor();
    }
    
    public void turnContour(double speed, double time){
        double start = Timer.getFPGATimestamp();
        while((Timer.getFPGATimestamp() - start < time) && !cam.twoContours()){
            driveMotors.tankDrive(-speed, speed);
        }
        driveMotors.stopMotor();
    }

    public void autoDrive(double dist, double time, double max){
        gw.reset();
        leftE.reset();
        rightE.reset();
        
        double start = Timer.getFPGATimestamp();
        while((Timer.getFPGATimestamp() - start < time) && Math.abs(rightE.getDistance()) < Math.abs(dist) && !pegSwitch.get()){
            double angle = gw.getAngle() / 10;
            
            double leftD = leftE.getDistance();
            double rightD = rightE.getDistance();
            
            double leftSpeed  = calcSpeed(max, leftD, dist);
            double rightSpeed = calcSpeed(max, rightD, dist);
            
            // Gyro Straight
            if(angle < 0){
                if(dist >= 0){
                    rightSpeed += angle; // Add Negative
                } else {
                    leftSpeed -= angle; // Sub Negative
                }
            } else {
                if(dist >= 0){
                    leftSpeed -= angle; // Sub Positive
                } else {
                    rightSpeed += angle; // Add Positive
                }
            }
            
            driveMotors.tankDrive(-leftSpeed, -rightSpeed);
        }
        driveMotors.stopMotor();
    }

    public void driveTillContours(double max) {
        gw.reset();
        
        boolean dir = true; // Forward
        
        double time = Timer.getFPGATimestamp();
        while((Timer.getFPGATimestamp() - time < 5.0) && !cam.twoContours() && !pegSwitch.get()){
            double angle = gw.getAngle() / 10;
            
            double leftSpeed  = -max;
            double rightSpeed = -max;
            
            // Gyro Straight
            if(angle < 0){
                if(dir){
                    rightSpeed -= angle; // Add Negative
                } else {
                    leftSpeed  += angle; // Sub Negative
                }
            } else {
                if(dir){
                    leftSpeed  += angle; // Sub Positive
                } else {
                    rightSpeed -= angle; // Add Positive
                }
            }
            
            driveMotors.tankDrive(leftSpeed, rightSpeed);
        }
        driveMotors.stopMotor();
    }
    
    public void centerPeriodic(double max){
        boolean dir = true; // Forward
        
        if(cam.twoContours() && !pegSwitch.get()){
            double angle = (SmartDashboard.getNumber("Peg Center", 155.0) - cam.getPos()[0]) / 100;
            
            double leftSpeed  = -max;
            double rightSpeed = -max;
            
            // Gyro Straight
            if(angle < 0){
                if(dir){
                    rightSpeed -= angle; // Add Negative
                } else {
                    leftSpeed  += angle; // Sub Negative
                }
            } else {
                if(dir){
                    leftSpeed  += angle; // Sub Positive
                } else {
                    rightSpeed -= angle; // Add Positive
                }
            }
            
            driveMotors.tankDrive(leftSpeed, rightSpeed);
        }
    }
    
    public void centerContours(double max){
        boolean dir = true; // Forward
        
        double time = Timer.getFPGATimestamp();
        while((Timer.getFPGATimestamp() - time < 6.0) && cam.twoContours() && !pegSwitch.get()){
            double angle = (SmartDashboard.getNumber("Peg Center", 155.0) - cam.getPos()[0]) / 100;
            
            double leftSpeed  = -max;
            double rightSpeed = -max;
            
            // Gyro Straight
            if(angle < 0){
                if(dir){
                    rightSpeed -= angle; // Add Negative
                } else {
                    leftSpeed  += angle; // Sub Negative
                }
            } else {
                if(dir){
                    leftSpeed  += angle; // Sub Positive
                } else {
                    rightSpeed -= angle; // Add Positive
                }
            }
            
            driveMotors.tankDrive(leftSpeed, rightSpeed);
        }
        driveMotors.stopMotor();
    }
    
    public void autoPeg(double max){
        driveMotors.stopMotor();
        
        gear.down();
        Timer.delay(0.25);
        
        grabber.set(DoubleSolenoid.Value.kReverse);
        grabberForward.set(false);
        Timer.delay(0.25);
        
        double time = Timer.getFPGATimestamp();
        double dist = -2.0;
        
        rightE.reset();
        leftE.reset();
        
        while((Timer.getFPGATimestamp() - time < 0.5) && Math.abs(rightE.getDistance()) < Math.abs(dist) && Math.abs(leftE.getDistance()) < Math.abs(dist)){
            double leftSpeed  = -calcSpeed(max, leftE.getDistance(), dist);
            double rightSpeed = -calcSpeed(max, rightE.getDistance(), dist);
            
            driveMotors.tankDrive(leftSpeed, rightSpeed);
        }
        
        gear.up();
    }
    
    public void shooter(){
        if(manipulatorXbox.getAButton()){
            dontPeg = true;
            double ideal = SmartDashboard.getNumber("Shooter Velocity", 5500);
            
            //shootSpeed += Math.PI((shootE.getRate() - ideal) / 10000, 1.0);
            
            if(shootE.getRate() > ideal){
                shootPeaked = true;
            }
            
            if(shootPeaked){
                shooterBack.set(-0.8);
            } else {
                shooterBack.stopMotor();
            }
            
            shooterFront.set(shootSpeed);
        } else if(manipulatorXbox.getYButton()){
            shooterFront.stopMotor();
            shooterBack.set(0.5);
        } else {
            shootSpeed = 1.0;
            shootPeaked = false;
            if(!pegSwitch.get()) dontPeg = false;
            shooterFront.stopMotor();
            shooterBack.stopMotor();
        }
    }
    
    public void gearGrab(){
        grabberForward.update(driveXbox.getBButton());
        
        if(gearSwitch.get()){
            if(!gearSwitched){
                gearSwitched = true;
                grabberForward.set(true);
                gearRumble.rumbleForTime(1.0);
            }
        } else {
            gearSwitched = false;
        }
        
        grabber.set(grabberForward.toggled() ? DoubleSolenoid.Value.kForward : DoubleSolenoid.Value.kReverse);
    }
    
    @Override
    public void operatorControl(){
    	setSafety(true);
        compressor.start();
        
        SmartDashboard.putNumber("Shooter Velocity", 5500);
        
        shootSpeed   = 0.5;
        shootPeaked  = false;
        gearSwitched = false;
        dontPeg      = false;
        
        while(isEnabled() && isOperatorControl()){
            if(pegSwitch.get() && !dontPeg){
                drive.disable();
                climber.disable();
                
                dontPeg = true;
                autoPeg(1.0);
            } else {
                if(driveXbox.getBackButton()){
                    drive.disable();
                    cam.enable();
                    centerPeriodic(0.6);
                } else {
                    cam.disable();
                    drive.enable();
                }
                
                climber.enable();
                
                shooter();
                
                gearGrab();
            }
            
            Timer.delay(0.005);
        }
    }
    
    @Override
    public void test(){
        
    }
    
    @Override
    public void disabled(){
        drive.disable();
        climber.disable();
    }
}
