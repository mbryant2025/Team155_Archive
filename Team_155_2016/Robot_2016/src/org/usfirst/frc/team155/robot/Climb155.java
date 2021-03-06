

package org.usfirst.frc.team155.robot;

//import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Victor;
//import edu.wpi.first.wpilibj.VictorSP;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
//import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Joystick;

import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Servo;

import edu.wpi.first.wpilibj.Compressor;


public class Climb155 {
	RobotMap155 robotSystem;
	SmartDashboard SmartDashboard;
	LiveWindow lw;
	Compressor compressor;
	
	Shooter155 shooter;
	
	// Vision155 vision;

	// JOYSTICKS
	Joystick dsStick;

	// MOTORS FOR COMPEITION ROBOT
	public Servo servo;
	public Victor winch;
	//public CANTalon canWinch;
	
	//public VictorSP climbArm;

	public DoubleSolenoid climbSol;
	public DigitalInput climbHighLimit;
	public DigitalInput climbLowLimit;

	// Digital Inputs
	public Encoder winchEncoder;
	public RobotDrive myrobot;
	public PIDController winchPID;

	final int HOLDCLIMB = 0;
	final int AUTOCLIMB = 1;
	final int MANCLIMB = 2;
	final int STOPCLIMB = 3;
	int winchState = MANCLIMB;
	final int WINCHHEIGHT = 5;
	final int WINCHMAX = 24;
	final int CLIMBSPEED = 1;

	double ClimbKp = -100;
	double ClimbKi = 0;
	double ClimbKd = 0;

	final int DONTCLIMB = 0;
	final int READY2CLIMB = 1;
	int climbState = DONTCLIMB;
	boolean atLowLimit = false;
	boolean armExtended = false;
	boolean state = false;

	double UPSPEED = 1;
	double DOWNSPEED = -1;
	double STOP = 0;

	double motorSpeed = 0;
	double stickTol = .1;
	boolean atSpeed = false;
	boolean doneClimb=false;

	public Climb155(Joystick dsio,Shooter155 robotShooter, RobotMap155 robot) {
		robotSystem = robot;
		shooter=robotShooter;
		//SmartDashboard = new SmartDashboard();
		lw = new LiveWindow();
		compressor = new Compressor();
		// JOYSTICKS - NEEDS MORE APPROPRIATE NAMES
		dsStick = dsio;
		
		servo = new Servo(robotSystem.SERVO);
		winch = new Victor(robotSystem.PWMWINCH);
		winch.setExpiration(.1);
		climbSol = new DoubleSolenoid(robotSystem.CLIMBARM_A,
				robotSystem.CLIMBARM_B);
		
		climbSol.set(DoubleSolenoid.Value.kForward);
		

		// CONTROLLER
		winchEncoder = new Encoder(robotSystem.LIFT_ENCODER_A,
				robotSystem.LIFT_ENCODER_B);
		winchEncoder.setDistancePerPulse(1.1* 3.14159265389 / 128);
		
		
		//16.691
		
		winchPID = new PIDController(ClimbKp, ClimbKi, ClimbKd, winchEncoder,winch);
		winchPID.setAbsoluteTolerance(1); // This could change
		winchPID.setOutputRange(0, 1);

	}

	

	public void run() {

		switch (climbState) {

		case DONTCLIMB:

			if (Timer.getMatchTime() >= 130
					&& dsStick.getRawButton(robotSystem.CLIMBMODE)
					&&shooter.armPot.getAverageVoltage()<.2)
				climbState = READY2CLIMB;
			break;

		case READY2CLIMB:
			// atLowLimit = climbArmWithMotor();
			armExtended = climbArmWithSol();
			// if (atLowLimit)
			if (armExtended)
				manWinch();//runWinch();
			
			break;
		}
	}

	public void climbArmInit() {
		
			climbSol.set(DoubleSolenoid.Value.kReverse);
		
		} 
	
	
	public boolean climbArmWithSol() {

		if (dsStick.getRawButton(robotSystem.CLIMBMODE)) {
			climbSol.set(DoubleSolenoid.Value.kForward);
			state = true;
		} 
		else{
			climbSol.set(DoubleSolenoid.Value.kReverse); 
			state = false;
		}

		return state;
	}
	
	public void servoSwitch() {

		if (dsStick.getRawButton(robotSystem.CLIMBMODE)) {
			servo.setAngle(45);
		} 
		else{
			servo.setAngle(90);
		}

	
	}
/*
	public void encoderTest(){
		if (dsStick.getRawButton(robotSystem.CLIMBMODE))
			//winch.set(dsStick.getY());
		
		else winchEncoder.reset();
		
		SmartDashboard.putNumber("Winch power : ",winch.get());
		SmartDashboard.putNumber("Winch encoder : ",winchEncoder.getDistance());
	}
	*/
	
	
	public boolean autoClimb(double setpoint){
		
		if (dsStick.getRawButton(robotSystem.CLIMB)){
			if (winchEncoder.getDistance()>setpoint){
			winch.set(1);
			doneClimb = false;
			}
			else {
				winch.set(0);
				doneClimb = true;
			}
		}
				
		
		//SmartDashboard.putNumber("Winch power : ",winch.get());
		//SmartDashboard.putNumber("Winch encoder : ",winchEncoder.getDistance());
		
		return doneClimb;
		}
		
	
	public void manWinch(){
		if (dsStick.getRawButton(robotSystem.CLIMB))
		winch.set(1);
		else winch.set(0);
			
		if (winchEncoder.getDistance()<-30){
			shooter.setColorMode(6);
			}
		
			
	}
	
	public void runWinch() {

		switch (winchState) {
		case HOLDCLIMB:
			if (dsStick.getRawButton(robotSystem.CLIMB))
				winchState = MANCLIMB;
			winchEncoder.reset();

			break;

		case AUTOCLIMB:
			if (autoClimb(-24))
				winchState = MANCLIMB;
			
			break;

		case MANCLIMB:
			if (dsStick.getRawButton(robotSystem.CLIMB))
				winch.set(CLIMBSPEED);

			if (winchEncoder.getDistance() >= WINCHMAX)
				winchState = STOPCLIMB;

			break;

		case STOPCLIMB:
			winch.set(STOP);
			
			break;
		}

	}

}
