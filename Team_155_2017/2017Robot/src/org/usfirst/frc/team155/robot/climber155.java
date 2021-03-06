package org.usfirst.frc.team155.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class climber155 {
	robotMap155 robotSystem;
	SmartDashboard sDash;
	public RobotDrive myrobot;
	public DoubleSolenoid climberSol;
	public VictorSP climbMotor1;
	public VictorSP climbMotor2;
	public Encoder climbEncoder;
	public Joystick dsStick;
	public double SLOWSPEED=.5;
	public double FASTSPEED=1;
	public double STOP=0;
	public boolean climbing;
	
	LiveWindow lw;
	
	public climber155(robotMap155 robot){
		
		robotSystem = robot;
		lw = new LiveWindow();
		sDash = new SmartDashboard();
		//SmartDashboard = new SmartDashboard();
		
		
		// JOYSTICKS - NEEDS MORE APPROPRIATE NAMES
		dsStick = new Joystick(robotSystem.DSSTICK);
		climbMotor1 = new VictorSP(robotSystem.CLIMBER1);
		climbMotor2 = new VictorSP(robotSystem.CLIMBER2);
		
		//climbEncoder = new Encoder(robotSystem.CLIMBER_ENCODER_A, robotSystem.CLIMBER_ENCODER_B);
	}
	
	public void run(){
		if (dsStick.getRawButton(robotSystem.SLOWCLIMB)) {
			climbMotor1.set(SLOWSPEED);
			climbMotor2.set(-SLOWSPEED);
			climbing=true;
			robotSystem.ledOverRide(7);
			
		} 
		else if (dsStick.getRawButton(robotSystem.FASTCLIMB)) {
			climbMotor1.set(FASTSPEED);
			climbMotor2.set(-FASTSPEED);
			robotSystem.ledOverRide(7);
			climbing=true;
			
		} 
		else{
			climbMotor1.set(STOP);
			climbMotor2.set(-STOP);
			robotSystem.ledOverRide(0);
			climbing=false;
		}
		/*
		lw.addActuator("CLIMBER", "climbMotor1", climbMotor1);
		lw.addActuator("CLIMBER", "climbMotor1", climbMotor2);
		*/
	}
	
}
