package org.usfirst.frc.team155.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
//import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DigitalInput;
//import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Joystick;
//import edu.wpi.first.wpilibj.CANTalon;
import com.ctre.phoenix.*;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
//import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
//import edu.wpi.first.wpilibj.PowerDistributionPanel;

/*
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	//final String defaultAuto = "Default";
	//final String customAuto = "My Auto";
	//String autoSelected;
	SendableChooser chooser;

	// SmartDashboard sDash;
	RobotMap155 robotMap;
	Shooter155 robotShooter;
	DRIVE155 robotDrive;
	Climb155 robotClimb;
	Autonomous155 robotAuto;
	NetworkTable robotTable;
	LiveWindow lw;
	CameraServer server;

	public Joystick leftStick;
	public Joystick rightStick;
	public Joystick dsStick;
	/*
	 * public final int DRIVE_LEFT_1 = 0; public final int DRIVE_LEFT_2 = 1;
	 * public final int DRIVE_RIGHT_1 = 2; public final int DRIVE_RIGHT_2 = 3;
	 * public final int SHOOTER_MOTOR = 4; public final int RGATHERER_MOTOR = 5;
	 * public final int LGATHERER_MOTOR = 6; public final int FEEDER_MOTOR = 7;
	 * public final int CLIMBER_MOTOR = 8;
	 * 
	 * public Victor leftDrive1; public Victor leftDrive2; public Victor
	 * rightDrive1; public Victor rightDrive2; public Victor shooterMotor;
	 * 
	 * public Victor rightGathererMotor; public Victor leftGathererMotor; public
	 * Victor feederMotor; public Victor climberMotor;
	 * 
	 * public double driveSpeed = 0; public double feederSpeed = 0; public
	 * double climbSpeed = 0; public double shooterSpeed = 0; public double
	 * gatherSpeed = 0;
	 */

	public double startTimeShooter = 0;
	public boolean hookState = false;

	public boolean set_speed = false;
	public boolean shoot = false;
	public boolean buttonShootPrev = false;
	public boolean buttonSetPrev = false;

	public boolean stopShooter = true;

	// public DriverStation ds;

	public double startTime;
	public double speed;
	public boolean upToSpeed;
	public double motorTimer = 0;

	// public boolean shoot=false;

	// for the SRX testing
	CANTalon shooterSRX;
	int encoderSpeed;

	private int mode = 1;
	private int modename = 0;

	// PowerDistributionPanel pdp;
	
	public DigitalInput backSwitch;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	public void robotInit() {
		robotMap = new RobotMap155();
		backSwitch = new DigitalInput(robotMap.CLIMBBACKSWITCH);
		// sDash = new SmartDashboard();
		leftStick = new Joystick(0);
		rightStick = new Joystick(1);
		dsStick = new Joystick(2);

		
		chooser = new SendableChooser();
		chooser.addDefault("default, do nothing ", 0);
		chooser.addObject("move thru defense to courtyard, BRICKWALL?", 1); //move thru defense to courtyard, aim, fire
		chooser.addObject("move Thru Moat, and Cobble, Gyro", 2); //move thru defense to courtyard
		chooser.addObject("MEGAMODE", 3); //move thru defense to courtyard
		chooser.addObject("move To Defense", 4); //move t0 defense
		chooser.addObject("move Thru RAMPARTs", 5); //move t0 defense
		
		SmartDashboard.putData("Auto choices",chooser);
		
		
		server = CameraServer.getInstance();
		// server.setQuality(50);
		// the camera name (ex "cam0") can be found through the roborio web
		// interface
		//server.startAutomaticCapture("cam1");

		// PowerDistributionPanel pdp = new PowerDistributionPanel();

		robotTable = NetworkTable.getTable("GRIP/VisionTable");

		robotDrive = new DRIVE155(leftStick, rightStick, robotMap, robotTable);
		robotShooter = new Shooter155(dsStick, rightStick, robotMap, robotDrive);
		robotClimb = new Climb155(dsStick, robotShooter, robotMap);
	//	robotAuto = new Autonomous155(robotDrive, robotShooter, robotMap);
		
		
		
		 
		shooterSRX = new CANTalon(0);
		// ds = DriverStation.getInstance().getInstance();
		robotDrive.safteyEnabled();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */

	public void autonomousInit() {
		


		// robotDrive.PIDEnable();
		mode = (int) chooser.getSelected();

		robotDrive.EncoderReset();

		robotClimb.climbArmInit();

		startTime = Timer.getMatchTime();

		robotDrive.safteyEnabled();

	//	robotDrive.GyroReset();

	}

	/**
	 * This function is called periodically during autonomous
	 */
	public void autonomousPeriodic() {
		//robotClimb.climbArmInit();
		// TESTING
		robotDrive.cameraOutput();
		//robotDrive.aimingRobot(robotDrive.AIMPOINT, robotDrive.SHOOTDISTANCE);
		robotClimb.climbSol.set(DoubleSolenoid.Value.kReverse);
		// robotAuto.megaMode();
		//robotAuto.move2Court();
		// robotAuto.move2Def();

/*		
		 switch (mode) { 
		 case 1: // Mode 1 is to go forward thru defense,  
			 modename = 1; 
			 robotAuto.stoneWall(); 
			 break;
		 case 2: modename = 2; // Moving over defense to courtyard
		 	robotAuto.move2Court(); 
		 	break; 
		 case 3: modename = 3; // Moving ovedefense to courtyard 
		 	//robotAuto.megaMode(); 
		 	break;
		 case 4: modename = 4; // Moving ovedefense to courtyard 
		 	robotAuto.move2Def(); 
		 	break;
		 case 5: modename = 5; // Moving ovedefense to courtyard 
		  	robotAuto.move2CourtNoGyroTime();
		 	break;	
		 
		 
		  default: // Default is to do nothing 
			  robotAuto.move2CourtNoGyroTime();
		 
		 // use camera to find target // line up on target
		 
		 // move the robot to the sweet spot // shoot the ball
		  
		 break; } 
	*/
		SmartDashboard.putNumber("Mode name = ", modename);
		

	}

	/*
	 * This function is called periodically during operator control
	 */

	public void teleopInit() {
		robotDrive.EncoderReset();
		robotClimb.climbArmInit();
		startTimeShooter = Timer.getFPGATimestamp();
		upToSpeed = false;
		
	}

	public void teleopPeriodic() {
		// TESTING
		SmartDashboard.putBoolean("Touching Wall?  ",backSwitch.get());

		if (rightStick.getRawButton(3))
			robotDrive.EncoderReset();

		robotDrive.safteyEnabled();

		// Drive controls

		
		// robotDrive.EncoderDistance();
		robotDrive.rightEncoderDistance();

		robotDrive.cameraOutput();		//this gets stuff from the kangaroo
		
		//this is for getting the robot to track the target
		if (leftStick.getRawButton(robotMap.AUTOAIM)) {   //button 8 pressed?
			if (robotDrive.targetfound > 0) {				//have target?
				robotDrive.aimingRobot(robotDrive.AIMPOINT,	robotDrive.SHOOTDISTANCE);  //this make the robot turn
				robotDrive.targetHot(robotDrive.SHOOTDISTANCE);		//called to set boolean targetHot
			}
			else							//no target, don't 
				robotDrive.errorSum=0;
		} else{												//drive normally
			robotDrive.errorSum=0;
			robotDrive.tankDrive(leftStick.getY(), rightStick.getY());
		}
	
		robotDrive.aimLight(leftStick.getRawButton(robotMap.AIMINGLIGHT));

		// SHOOTER CONTROLS
	//	stopShooter = robotShooter.gatherSystemTEST(stopShooter, upToSpeed);

		if (leftStick.getRawButton(robotMap.Shoot_Off)) {
			stopShooter = true;
			upToSpeed = false;
			motorTimer = Timer.getFPGATimestamp();
		}

		else if (rightStick.getRawButton(robotMap.Shoot_On)) {
			stopShooter = false;
			motorTimer = Timer.getFPGATimestamp();
		}

		if (stopShooter) {
			upToSpeed = false;
			// motorTimer = 0;
			motorTimer = Timer.getFPGATimestamp();
		} else {
			// robotShooter.voltShooter(robotDrive.camdistance);
			// robotShooter.runShooterSetSpeed();
			robotShooter.runShooterManualTEST();
			// robotShooter.runShooterManualSpeed();
		}

		if (Timer.getFPGATimestamp() - motorTimer > 3.25) {
			upToSpeed = true;
		}
		SmartDashboard.putBoolean("SHOOTER On", !stopShooter);
		SmartDashboard.putBoolean("Up to SPEED", upToSpeed);
		// SmartDashboard.putNumber("MotorTimer",motorTimer);
		// SmartDashboard.putNumber("Timer",Timer.getFPGATimestamp());
		// robotShooter.gatherSystemTEST(stopShooter);

		// robotShooter.runShooterManual();
		// robotDrive.aimingRobot();
		// robotShooter.manualLift();
		// robotShooter.manFeeder();
		// robotShooter.manGather();
		robotShooter.shootSensors();

		// hookState= robotClimb.climbArmWithSol();

		// CLIMB CONTROL]
		robotClimb.servoSwitch();
		
		
		/*
		hookState = robotClimb.climbArmWithSol();
		
		if (dsStick.getRawButton(robotMap.CLIMBMODE)) {

			robotClimb.winch.set(0);
		}

		if (!hookState) {
			robotClimb.manWinch();
		}

		*/
		
		
		if ((Timer.getFPGATimestamp() - startTimeShooter > 115)||(dsStick.getRawButton(robotMap.SHOOTER_SWITCH))) {// 115 for match
			hookState = robotClimb.climbArmWithSol();
			
			if (dsStick.getRawButton(robotMap.CLIMBMODE)) {

				robotClimb.winch.set(0);
			}

			if (!hookState) {
				robotClimb.manWinch();
			}

		} else {
			robotClimb.climbSol.set(DoubleSolenoid.Value.kReverse);
			robotClimb.winch.set(0);
		}

		// robotClimb.autoClimb(-24);
		// robotClimb.manWinch();
		// robotClimb.climbArmWithSol();

		/*
		 * if (1==0){ SmartDashboard.putNumber("Left 1 drive current",
		 * pdp.getCurrent(0)); SmartDashboard.putNumber("Left 2 drive current",
		 * pdp.getCurrent(1)); SmartDashboard.putNumber("Right 1 drive current",
		 * pdp.getCurrent(14));
		 * SmartDashboard.putNumber("Right 2 drive current",
		 * pdp.getCurrent(15));
		 * 
		 * SmartDashboard.putNumber("Shooter current", pdp.getCurrent(0));
		 * 
		 * SmartDashboard.putNumber("Gatherer current", pdp.getCurrent(1));
		 * SmartDashboard.putNumber("Feed current", pdp.getCurrent(12));
		 * SmartDashboard.putNumber("arm current", pdp.getCurrent(13));
		 * 
		 * SmartDashboard.putNumber("Lift", pdp.getCurrent(13));
		 * SmartDashboard.putNumber("pdp voltage", pdp.getVoltage()); }
		 */

	}

	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic() {
		// robotShooter.runLiveWindow();

	}
	
	public void disabledPeriodic()
	{
		robotDrive.cameraOutput();
		robotShooter.setColorMode(7);
		robotClimb.climbArmInit();
		//robotClimb.
	}

}
