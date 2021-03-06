package org.usfirst.frc.team155.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Talon;


import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DrivePrac155 {
	private static final double Kp = 0;
	robotMap155 robotSystem;
	SmartDashboard sDash;
	PixyVision155 camera;
	public RobotDrive myrobot;
	
	LiveWindow lw;

	// DRIVE MODE SELECTION
	public int driveMode;
	public int driveState;
	public final int TANK_MODE = 0;
	public final int MECANUM_MODE = 1;
	public double startTime;
	public double deltaTime = 0.25;
	public final double THRESHOLD = .1;
	public boolean preTank = false;

	// JOYSTICKS
	Joystick leftStick; 
	Joystick rightStick; 

	// MOTORS FOR Practice ROBOT
	 
	public Talon left_front;
	public Talon right_front;
	public Talon left_back;
	public Talon right_back;

	// DRIVE ENCODERS
	public Encoder Front_Right_Encoder;
	public Encoder Front_Left_Encoder;
	public Encoder Back_Right_Encoder;
	public Encoder Back_Left_Encoder;
	public PIDController Front_Right_PID;
	public PIDController Front_Left_PID;
	public PIDController Rear_Right_PID;
	public PIDController Rear_Left_PID;

	// YAW RATE SENSOR
	public Gyro roboGyro;
	public AnalogInput rangefinder;

	double motorScale = 1;

	// Encoder Controllers
	double Front_Right_Kp = .05;
	double Front_Right_Ki = 0;
	double Front_Right_Kd = 0;
	double Front_Left_Kp = .05;
	double Front_Left_Ki = 0;
	double Front_Left_Kd = 0;
	double Rear_Right_Kp = .05;
	double Rear_Right_Ki = 0;
	double Rear_Right_Kd = 0;
	double Rear_Left_Kp = .05;
	double Rear_Left_Ki = 0;
	double Rear_Left_Kd = 0;

	// for the gyro stabilized field oriented drive
	private double foo = 2; // more of an integration scaling factor.
	private double headingSetPoint;
	private double error;
	private double PIDoutput;
	private double Kp_FieldOrientedControl = .02; // not tuned
	private boolean holdHeading;
	private boolean prevCentered;
	private boolean centered;

	

	Boolean reset_trigger;
	Boolean prev_reset_trigger;

	// SOLENOIDS

	DoubleSolenoid sol;
	private final int UP = 0;
	private final int DOWN = 1;
	double start_time = Timer.getFPGATimestamp();
	// navx stuffs
	SerialPort serial_port;
	AHRS ahrs;

	public DrivePrac155(robotMap155 robot, PixyVision155 cameraSystem) {
		//ahrs = new AHRS(SerialPort.Port.kMXP);
		robotSystem = robot;
		camera = cameraSystem;
		sDash = new SmartDashboard();
		lw = new LiveWindow();
		
		// vision = new Vision155();
		leftStick = new Joystick(robotSystem.LEFTSTICK);
		rightStick = new Joystick(robotSystem.RIGHTSTICK);

		// DRIVE STATE
		driveMode = MECANUM_MODE;
		driveState = 4;


		// navx_init();
//*****************************************************************************
 
		
		//Motors for Practice
		left_front = new Talon(robotSystem.DRIVE_LEFT_FRONT);
		right_front = new Talon(robotSystem.DRIVE_RIGHT_FRONT);
		left_back = new Talon(robotSystem.DRIVE_LEFT_BACK);
		right_back = new Talon(robotSystem.DRIVE_RIGHT_BACK);
		// myrobot.setSafetyEnabled(false); //don't like way, but trying
	
		 
		// temporarily
		/*
		myrobot.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, true);
		myrobot.setInvertedMotor(RobotDrive.MotorType.kFrontRight, false);
		myrobot.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
		myrobot.setInvertedMotor(RobotDrive.MotorType.kRearRight, false);
		*/
		// ENCODERS
		Front_Right_Encoder = new Encoder(robotSystem.FRONT_RIGHT_ENCODER_A,
				robotSystem.FRONT_RIGHT_ENCODER_B);
		Front_Right_Encoder.setDistancePerPulse(6 * 3.14159265389 / 500);
		Front_Left_Encoder = new Encoder(robotSystem.FRONT_LEFT_ENCODER_A,
				robotSystem.FRONT_LEFT_ENCODER_B);
		Front_Left_Encoder.setDistancePerPulse(6 * 3.14159265389 / 500);
		Back_Right_Encoder = new Encoder(robotSystem.BACK_RIGHT_ENCODER_A,
				robotSystem.BACK_RIGHT_ENCODER_B);
		Back_Right_Encoder.setDistancePerPulse(6 * 3.14159265389 / 500);
		Back_Left_Encoder = new Encoder(robotSystem.BACK_LEFT_ENCODER_A,
				robotSystem.BACK_LEFT_ENCODER_B);
		Back_Left_Encoder.setDistancePerPulse(6 * 3.14159265389 / 500);

		
		
		// CONTROLLER
		Front_Right_Encoder.setPIDSourceType(PIDSourceType.kDisplacement);
		Front_Right_Encoder.setPIDSourceType(PIDSourceType.kDisplacement);
		Front_Right_PID = new PIDController(Front_Right_Kp, Front_Right_Ki,
				Front_Right_Kd, Front_Right_Encoder, right_front);
		Front_Left_Encoder.setPIDSourceType(PIDSourceType.kDisplacement);
		Front_Left_PID = new PIDController(Front_Left_Kp, Front_Left_Ki,
				Front_Left_Kd, Front_Left_Encoder, left_front);
		Back_Right_Encoder.setPIDSourceType(PIDSourceType.kDisplacement);
		Rear_Right_PID = new PIDController(Rear_Right_Kp, Rear_Right_Ki,
				Rear_Right_Kd, Back_Right_Encoder, right_back);
		Back_Left_Encoder.setPIDSourceType(PIDSourceType.kDisplacement);
		Rear_Left_PID = new PIDController(Rear_Left_Kp, Rear_Left_Ki,
				Rear_Left_Kd, Back_Left_Encoder, left_back);

		Front_Left_PID.setAbsoluteTolerance(3);
		Front_Right_PID.setAbsoluteTolerance(3);
		Rear_Left_PID.setAbsoluteTolerance(3);
		Rear_Right_PID.setAbsoluteTolerance(3);
		Front_Left_PID.setOutputRange(-.5, .5);
		Front_Right_PID.setOutputRange(-.5, .5);
		Rear_Left_PID.setOutputRange(-.5, .5);
		Rear_Right_PID.setOutputRange(-.5, .5);

		// PIDGyro = new PIDController(Kp,Ki,Kd,roboGyro,PIDGyroOut);

		headingSetPoint = 0;
		holdHeading = true;
		prevCentered = false;
		centered = false;
		
		myrobot = new RobotDrive(left_front, left_back, right_front, right_back);
		
		// YAW RATE (GYRO)
		roboGyro = new AnalogGyro(robotSystem.GYRO);

		sol = new DoubleSolenoid(robot.DRIVE_SOL_A, robot.DRIVE_SOL_B);
	}

	private void mecanum_fieldOriented() {
		
		
		myrobot.mecanumDrive_Cartesian(leftStick.getX(), leftStick.getY(), rightStick.getX(), roboGyro.getAngle());
		
	}

	public void driveStraight(double heading, double speed) {
		double error = heading - roboGyro.getAngle();
		double turnRate = error * Kp;
		double maxturnRate = .33;
		if (turnRate > maxturnRate)
			turnRate = maxturnRate;
		else if (turnRate < -maxturnRate)
			turnRate = -maxturnRate;
		myrobot.arcadeDrive(speed, turnRate);
		//
		//SmartDashboard.putNumber("Gyro", roboGyro.getAngle());
		
	}

	public void driveMecanum(double heading, double speed, double direction) {
		System.out.println("In driveMecanum");
		System.out.println("heading = " + heading);
		System.out.println("speed = " + speed);
		System.out.println("direction = " + direction);

		//System.out.println("roboGyro.getAngle() = " + roboGyro.getAngle());
		double error = heading - roboGyro.getAngle();
		
		//double error = heading - ahrs.getAngle();
		System.out.println("error = " + error);

		PowerDistributionPanel pdp = new PowerDistributionPanel();
		double turnRate = error * Kp;
		System.out.println("turnRate = " + turnRate);

		pdp.clearStickyFaults(); // clear the brownouts from last run, if there
									// are any

		double maxturnRate = .25;

		if (turnRate > maxturnRate)
			turnRate = maxturnRate;
		else if (turnRate < -maxturnRate)
			turnRate = -maxturnRate;
		System.out.println("turnRate(after max compare) = " + turnRate);
		System.out.println("Going to mecanumDrive_Polar");
		System.out.println("speed = " + speed);
		System.out.println("direction = " + direction);
		System.out.println("turnRate(after max compare) = " + turnRate);

		myrobot.mecanumDrive_Polar(speed, direction, turnRate);
		// SmartDashboard.putNumber("Gyro", roboGyro.getAngle());
		// SmartDashboard.putNumber("Motor1 current", pdp.getCurrent(12));
	}

	public void centerYellowTote(double goalposition, double speed,
			double toteposition) {

		System.out.println("In centerYellowTote");
		System.out.println("goalposition = " + goalposition);
		System.out.println("speed = " + speed);
		System.out.println("toteposition = " + toteposition);

		double error = goalposition - toteposition;
		System.out.println("error(= goalposition - toteposition) = " + error);

		double camKp = 0;
		System.out.println("camKp = " + camKp);
		double slideRate = -error * camKp;
		System.out.println("slideRate(= -error * camKp) = " + slideRate);

		double maxslideRate = .5;
		// double minslideRate = .1;

		if (slideRate > maxslideRate)
			slideRate = maxslideRate;
		else if (slideRate < -maxslideRate)
			slideRate = -maxslideRate;

		System.out.println("slideRate(adj for maxslideRate) = " + slideRate);

		// if ((slideRate < minslideRate)&&(slideRate > -minslideRate))
		// slideRate = 0;
		// Timer.delay(0.005);

		System.out.println("Going to mecanumDrive_Cartesian");
		System.out.println("x = " + slideRate);
		System.out.println("y = 0");
		System.out.println("speed = " + speed);
		//System.out.println("GyroAngle = " + roboGyro.getAngle());
		
		myrobot.mecanumDrive_Cartesian(slideRate, speed, 0, roboGyro.getAngle());
		// SmartDashboard.putNumber("toteposition=", toteposition);
		// SmartDashboard.putNumber("error=", error);
		// SmartDashboard.putNumber("sliderate = ", slideRate);
	}

	public double getGyro() {
		return roboGyro.getAngle();
		
	}

	public void GyroReset() {
		roboGyro.reset();
		headingSetPoint = 0;
		holdHeading = true;
		prevCentered = false;
		centered = false;

	}

	// drive selector
	public void mecanumstop() {
		//myrobot.mecanumDrive_Polar(Speed, direction, rotation);
		myrobot.mecanumDrive_Polar(0, 0, 0);
	}

	public void run() {
		
		//EncoderDistance();
		PowerDistributionPanel pdp = new PowerDistributionPanel();
		// / SmartDashboard.putNumber(key, value);
		// SmartDashboard.putNumber("Motor0 current", pdp.getCurrent(0));
		// SmartDashboard.putNumber("Motor1 current", pdp.getCurrent(1));
		// SmartDashboard.putNumber("Motor12 current", pdp.getCurrent(12));
		// SmartDashboard.putNumber("Motor13 current", pdp.getCurrent(13));
		// SmartDashboard.putNumber("pdp voltage", pdp.getVoltage());

		// mecanum_fieldOriented();
		
		
		/*
		if (!rightStick.getRawButton(robotSystem.DRIVE_MODE_TRIGGER)) {
			myrobot.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, true);
			myrobot.setInvertedMotor(RobotDrive.MotorType.kFrontRight, false);
			myrobot.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
			myrobot.setInvertedMotor(RobotDrive.MotorType.kRearRight, false);
			//myrobot.mecanumDrive_Cartesian(leftStick.getX(), leftStick.getY(),rightStick.getX(), roboGyro.getAngle());
			
			team155Mecanum_fieldOriented(leftStick.getX(), leftStick.getY(), rightStick.getX(), preTank );
			sol.set(DoubleSolenoid.Value.kReverse);
			preTank=false;
			
		} else {
			myrobot.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, false);
			myrobot.setInvertedMotor(RobotDrive.MotorType.kFrontRight, false);
			myrobot.setInvertedMotor(RobotDrive.MotorType.kRearLeft, false);
			myrobot.setInvertedMotor(RobotDrive.MotorType.kRearRight, false);
			myrobot.tankDrive(leftStick, rightStick);
			sol.set(DoubleSolenoid.Value.kForward);
			preTank=true;
			
		}
		*/
		
		if (leftStick.getRawButton(robotSystem.GYRORESET))
			GyroReset();
		

		switch (driveState) {
		// tank mode - button press
		case 0: {
			driveMode = TANK_MODE;
			if (rightStick.getRawButton(robotSystem.DRIVE_MODE_TRIGGER)) {
				driveState = 1;
				startTime = Timer.getFPGATimestamp();
			}

			break;
		}
		// mecanum mode - second button press release
		case 1: {
			if (driveMode == MECANUM_MODE) {
				if (!rightStick.getRawButton(robotSystem.DRIVE_MODE_TRIGGER)) {
					driveState = 0;
				}
				// If there's joystick input, the drive mode is set to mecanum
				if ((Math.abs(leftStick.getX()) > THRESHOLD)
						|| (Math.abs(leftStick.getY()) > THRESHOLD)
						|| (Math.abs(rightStick.getX()) > THRESHOLD)
						|| (Math.abs(rightStick.getY()) > THRESHOLD)) {
					driveState = 4;
				}
				// timeout
				if (Timer.getFPGATimestamp() - startTime > deltaTime) {
					driveState = 4;
				}
				// tank mode - button press release
			} else {
				if (!rightStick.getRawButton(robotSystem.DRIVE_MODE_TRIGGER)) {
					driveState = 2;
					startTime = Timer.getFPGATimestamp();
				}
				// If there's joystick input, the drive mode is set to tank
				if ((Math.abs(leftStick.getX()) > THRESHOLD)
						|| (Math.abs(leftStick.getY()) > THRESHOLD)
						|| (Math.abs(rightStick.getX()) > THRESHOLD)
						|| (Math.abs(rightStick.getY()) > THRESHOLD)) {
					driveState = 0;
				}
				// timeout
				if (Timer.getFPGATimestamp() - startTime > deltaTime) {
					driveState = 0;
				}

			}

			break;
		}
		// mecanum - second button press
		case 2: {
			if (driveMode == MECANUM_MODE) {
				if (rightStick.getRawButton(robotSystem.DRIVE_MODE_TRIGGER)) {
					driveState = 1;
					startTime = Timer.getFPGATimestamp();
				}
				// If there's joystick input, the drive mode is set to mecanum
				if ((Math.abs(leftStick.getX()) > THRESHOLD)
						|| (Math.abs(leftStick.getY()) > THRESHOLD)
						|| (Math.abs(rightStick.getX()) > THRESHOLD)
						|| (Math.abs(rightStick.getY()) > THRESHOLD)) {
					driveState = 4;
				}
				// timeout
				if (Timer.getFPGATimestamp() - startTime > deltaTime) {
					driveState = 4;
				}
				// tank mode - second button press
			} else {
				if (rightStick.getRawButton(robotSystem.DRIVE_MODE_TRIGGER)) {
					driveState = 3;
					startTime = Timer.getFPGATimestamp();
				}
				// If there's joystick input, the drive mode is set to tank
				if ((Math.abs(leftStick.getX()) > THRESHOLD)
						|| (Math.abs(leftStick.getY()) > THRESHOLD)
						|| (Math.abs(rightStick.getX()) > THRESHOLD)
						|| (Math.abs(rightStick.getY()) > THRESHOLD)) {
					driveState = 0;
				}
				// timeout
				if (Timer.getFPGATimestamp() - startTime > deltaTime) {
					driveState = 0;
				}
			}

			break;
		}
		// mecanum - first button press release
		case 3: {
			if (driveMode == MECANUM_MODE) {
				if (!rightStick.getRawButton(robotSystem.DRIVE_MODE_TRIGGER)) {
					driveState = 2;
					startTime = Timer.getFPGATimestamp();
				}
				// If there's joystick input, the drive mode is set to mecanum
				if ((Math.abs(leftStick.getX()) > THRESHOLD)
						|| (Math.abs(leftStick.getY()) > THRESHOLD)
						|| (Math.abs(rightStick.getX()) > THRESHOLD)
						|| (Math.abs(rightStick.getY()) > THRESHOLD)) {
					driveState = 4;
				}
				// timeout
				if (Timer.getFPGATimestamp() - startTime > deltaTime) {
					driveState = 4;
				}
				// tank mode - second button press release
			} else {
				if (!rightStick.getRawButton(robotSystem.DRIVE_MODE_TRIGGER)) {
					driveState = 4;
				}
				// If there's joystick input, the drive mode is set to tank
				if ((Math.abs(leftStick.getX()) > THRESHOLD)
						|| (Math.abs(leftStick.getY()) > THRESHOLD)
						|| (Math.abs(rightStick.getX()) > THRESHOLD)
						|| (Math.abs(rightStick.getY()) > THRESHOLD)) {
					driveState = 0;
				}
				// timeout
				if (Timer.getFPGATimestamp() - startTime > deltaTime) {
					driveState = 0;
				}
			}
			break;
		}
		// mecanum - first button press
		case 4: {
			driveMode = MECANUM_MODE;
			if (rightStick.getRawButton(robotSystem.DRIVE_MODE_TRIGGER)) {
				driveState = 3;
				startTime = Timer.getFPGATimestamp();
			}
			break;
		}
		}
		System.out.println("Drive Mode = " + driveMode + "Drive State = " + driveState);
		if (driveMode == MECANUM_MODE) {
			myrobot.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, true);
			myrobot.setInvertedMotor(RobotDrive.MotorType.kFrontRight, false);
			myrobot.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
			myrobot.setInvertedMotor(RobotDrive.MotorType.kRearRight, false);
			//team155Mecanum_fieldOriented(leftStick.getX(), leftStick.getY(), rightStick.getX(), preTank );
			myrobot.mecanumDrive_Cartesian(leftStick.getX(), leftStick.getY(), rightStick.getX(),0);
			
			sol.set(DoubleSolenoid.Value.kReverse);
			preTank=false;
		} else {
			myrobot.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, false);
			myrobot.setInvertedMotor(RobotDrive.MotorType.kFrontRight, false);
			myrobot.setInvertedMotor(RobotDrive.MotorType.kRearLeft, false);
			myrobot.setInvertedMotor(RobotDrive.MotorType.kRearRight, false);
			myrobot.tankDrive(leftStick, rightStick);
			sol.set(DoubleSolenoid.Value.kForward);
			preTank=true;
		}
		// System.out.println("motorscale is "+ motorScale);
		if (leftStick.getRawButton(robotSystem.GYRORESET) == true)
			GyroReset();
		// this does a on-the-fly gyro init
		// hold both triggers for 5 seconds to make this work
		// robot must not be moving for those 5 seconds and the next 6 seconds

		prev_reset_trigger = reset_trigger;

		if ((leftStick.getRawButton(robotSystem.GYRORESET) == true)
				&& (rightStick.getRawButton(robotSystem.GYRORESET) == true))
			reset_trigger = true;
		else
			reset_trigger = false;

		if (reset_trigger && (!prev_reset_trigger))
			start_time = Timer.getFPGATimestamp();

		if (!reset_trigger)
			start_time = Timer.getFPGATimestamp();

		if ((Timer.getFPGATimestamp() - start_time) > 5.0) {
			System.out
					.println("re-initializing the gyro..........................................................................");
			// roboGyro.initGyro();?????????????
		
			
			roboGyro.calibrate();
			roboGyro.reset();
			GyroReset();
			System.out
					.println("done re-initializing gyro.........................................................................");
		}

		if (leftStick.getRawButton(robotSystem.ENCODER_RESET) == true)
			EncoderReset();
		if (leftStick.getRawButton(robotSystem.PID_DISABLE) == true)
			PIDDisable();
		// DriveStraightDistance(36);
		// toteArm();
		
		
		//lw.addActuator("Drive", "left_front", left_front);
		//lw.addActuator("Drive", "right_front", right_front);
		//lw.addActuator("Drive", "left_back", left_back);
		//lw.addActuator("Drive", "right_back", right_back);
	}

	void team155Mecanum_fieldOriented(double LSgetX, double LSgetY,
			double RSgetX, boolean wasTank) {

		prevCentered = centered;

		// is it centered?
		if (Math.abs(RSgetX) < .05) // may need to be upped
			centered = true;
		else {
			headingSetPoint = RSgetX * foo / motorScale + headingSetPoint; // must
																			// not
																			// be
			// centered,
			// so... command
			// to turn
			centered = false;
		}

		// rising edge detector
		if ((!prevCentered) && centered)
			holdHeading = true;
		else
			holdHeading = false; // was implied to be false

		if(wasTank)
			holdHeading =true;
		
		// on a rising edge, set the heading to hold
		if (holdHeading) {
			headingSetPoint = roboGyro.getAngle();
			holdHeading = false;
		}

		error = -roboGyro.getAngle() + headingSetPoint;
		
		PIDoutput = error * Kp_FieldOrientedControl;

		// SmartDashboard.putNumber("heading setpoint is ", headingSetPoint);
		// SmartDashboard.putNumber("PIDoutput is ", PIDoutput);
		// SmartDashboard.putNumber("rightStick.getX is ", rightStick.getX());

		
		myrobot.mecanumDrive_Cartesian(LSgetX / motorScale, LSgetY / motorScale, PIDoutput, roboGyro.getAngle());
	}

	public void EncoderRate() {
		double rate;
		System.out.println("in EncoderRate");

		rate = Front_Left_Encoder.getRate();
		rate = Back_Left_Encoder.getRate();
		rate = Back_Right_Encoder.getRate();
		rate = Front_Right_Encoder.getRate();

		System.out.println("rate = " + rate);
		/*
		 * SmartDashboard.putNumber("Back left Encoder Rate : ",
		 * Back_Left_Encoder.getRate());
		 * SmartDashboard.putNumber("Front left Encoder Rate: ",
		 * Front_Left_Encoder.getRate());
		 * SmartDashboard.putNumber("Back Right Encoder Rate : ",
		 * Back_Right_Encoder.getRate());
		 * SmartDashboard.putNumber("Front Right Encoder Rate: ",
		 * Front_Right_Encoder.getRate());
		 */
	}

	public void EncoderReset() {

		Front_Left_Encoder.reset();
		Back_Left_Encoder.reset();
		Front_Right_Encoder.reset();
		Back_Right_Encoder.reset();

	}

	public void RangeFinderDistance() {

	}

	public boolean DriveStraightDistance(double distance) {
		System.out.println("Drive straight for x distance");
		SmartDashboard.putNumber("Back left Encoder Distance : ",
				Back_Left_Encoder.getDistance());
		SmartDashboard.putNumber("Front left Encoder Distance : ",
				Front_Left_Encoder.getDistance());
		SmartDashboard.putNumber("Back Right Encoder Distance : ",
				Back_Right_Encoder.getDistance());
		SmartDashboard.putNumber("Front Right Encoder Distance : ",
				Front_Right_Encoder.getDistance());
		Front_Right_PID.setSetpoint(-distance); // this is done because the
												// motors on opposite side of
												// the robot
		Front_Left_PID.setSetpoint(distance);
		Rear_Right_PID.setSetpoint(-distance); // this is done because the
												// motors on opposite side of
												// the robot
		Rear_Left_PID.setSetpoint(distance);
		return Rear_Left_PID.onTarget();
	}

	public boolean DriveSideDistance(double distance) {
		double adjusted;
		double scale = 1;
		adjusted = scale * distance;
		Front_Right_PID.setSetpoint(-adjusted);
		Front_Left_PID.setSetpoint(-adjusted);
		Rear_Right_PID.setSetpoint(adjusted);
		Rear_Left_PID.setSetpoint(adjusted);
		return Rear_Right_PID.onTarget();
	}

	public void PIDEnable() {
		Front_Left_PID.enable();
		Front_Right_PID.enable();
		Rear_Left_PID.enable();
		Rear_Right_PID.enable();
	}

	public void PIDDisable() {
		Front_Left_PID.disable();
		Front_Right_PID.disable();
		Rear_Left_PID.disable();
		Rear_Right_PID.disable();
		Front_Left_PID.reset();
		Front_Right_PID.reset();
		Rear_Left_PID.reset();
		Rear_Right_PID.reset();
	}

	public double EncoderDistance() {
		double distance_Back_Left;
		double distance_Front_Left;
		double distance_Back_Right;
		double distance_Front_Right;
		double averageDistance;
		// System.out.println("in EncoderDistance ");
		distance_Front_Left = Front_Left_Encoder.getDistance();
		distance_Back_Left = Back_Left_Encoder.getDistance();
		distance_Front_Right = Front_Right_Encoder.getDistance();
		distance_Back_Right = Back_Right_Encoder.getDistance();

		// System.out.println("distance_Front_Left = " + distance_Front_Left);
		// System.out.println("distance_Back_Left = " + distance_Back_Left);
		// System.out.println("distance_Front_Right = " + distance_Front_Right);
		// System.out.println("distance_Back_Right = " + distance_Back_Right);

		// averageDistance = (distance_Front_Left + distance_Back_Left +
		// distance_Front_Right + distance_Back_Right) / 4;
		averageDistance = (-distance_Back_Left + -distance_Back_Right) / 2;
		// System.out.println("averageDistance = " + averageDistance);

		// SmartDashboard.putNumber("Average of left side encoder : ",
		// averageDistance);
		// SmartDashboard.putNumber("Back left Encoder Distance2 : ",
		// distance_Front_Left);
		// SmartDashboard.putNumber("Front left Encoder Distance2 : ",
		// distance_Front_Left);
		// SmartDashboard.putNumber("Back Right Encoder Distance2 : ",
		// distance_Back_Right);
		// SmartDashboard.putNumber("Front Right Encoder Distance2 : ",
		// distance_Front_Right);

		return averageDistance;
	}
	
	// Enable Disable Controller Buttons (Lights on or off according to the
	// robot)

}
