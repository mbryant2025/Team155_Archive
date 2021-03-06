package org.usfirst.frc.team155.robot;

//import com.kauailabs.navx.frc.AHRS;


//import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.VictorSP;
//import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Joystick;
//import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.AnalogGyro;
//import edu.wpi.first.wpilibj.Timer;

//import edu.wpi.first.wpilibj.Buttons.Button;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.Relay;

import java.lang.Math;

public class DRIVE155 {
	RobotMap155 robotSystem;
	SmartDashboard SmartDashboard;
	NetworkTable table;
	LiveWindow lw;

//	AHRS ahrs;
	// Vision155 vision;

	// JOYSTICKS
	Joystick leftStick;
	Joystick rightStick;

	// MOTORS FOR COMPEITION ROBOT

	public VictorSP left_front;
	public VictorSP right_front;
	public VictorSP left_back;
	public VictorSP right_back;

	public Relay aimLightRelay;

	/*
	 * // MOTORS FOR TEST ROBOT public Jaguar left_front; public Jaguar
	 * right_front; public Jaguar left_back; public Jaguar right_back;
	 * 
	 * // MOTORS TO SUCK AND SPIT TOTE public Jaguar right_Sucker; public Jaguar
	 * left_Sucker;
	 */

	public RobotDrive myrobot;

	// DRIVE ENCODERS
	public Encoder Right_Encoder;
	public Encoder Left_Encoder;

	public PIDController RightFront_PID;
	public PIDController RightRear_PID;

	public PIDController LeftFront_PID;
	public PIDController LeftRear_PID;

	// YAW RATE SENSOR
	public AnalogGyro roboGyro;

	// CONTROLLERS
	double Kp = .05;
	double Ki = 0;
	double Kd = 0;
	double PIDGyroOut;
	public PIDController PIDGyro;
	double camKp = .005;

	// Digital inputs
	DigitalInput suckerSwitch;

	// Encoder Controllers
	double Right_Kp = .05;
	double Right_Ki = 0;
	double Right_Kd = 0;
	double Left_Kp = .05;
	double Left_Ki = 0;
	double Left_Kd = 0;

	// private boolean reset_trigger = false;
	// private boolean prev_reset_trigger = false;
	// private double start_time;

	double motorScale = 1;
	
	double[] defaultValue = new double[0];
	double[] xpos =  new double[0];
	double[] ypos =  new double[0];
	double[] width =  new double[0];
	double[] height =  new double[0];
	double[] area =  new double[0];
	double xposTarget=0;
	double yposTarget=0;
	double widthTarget=0;
	double heightTarget=0;
	double areaTarget=0;
	
	double camdistance = 0;
	double targetfound = 0;

	double SHOOTDISTANCE = 10; // Change
	double SHOOTDISTTOL = .5;

	double AIMPOINT = 310;
	double AIMPOINTTOL = 10;

	double aimKp = .0032;
	double aimKi = 0;   //not use, not tested enough yet
	double errorSum=0;

	double aimheading = AIMPOINT;
	double aimspeed;
	double speedKp = .1;

	boolean targetHot = false;
	boolean inPosition = false;
	static int MIN_AREA = 180;
	
	boolean doneTurn =false;
	
	boolean atmySpot=false;

	boolean moveDone = false;
	
	double accelvalue;

	// SOLENOIDS
	/*
	 * DoubleSolenoid sol; private final int UP = 0; private final int DOWN = 1;
	 * private int armState = DOWN;
	 */

	public DRIVE155(Joystick left, Joystick right, RobotMap155 robot,
			NetworkTable robotTable) {
		robotSystem = robot;
		table = robotTable;
		// SmartDashboard = new SmartDashboard();
		 	aimLightRelay = new Relay(robotSystem.AIMLIGHT);

		// vision = new Vision155();

		// JOYSTICKS - NEEDS MORE APPROPRIATE NAMES
		leftStick = left;
		rightStick = right;

		// navx_init();
		try {
			/* Communicate w/navX MXP via the MXP SPI Bus. */
			/*
			 * Alternatively: I2C.Port.kMXP, SerialPort.Port.kMXP or
			 * SerialPort.Port.kUSB
			 */
			/*
			 * See
			 * http://navx-mxp.kauailabs.com/guidance/selecting-an-interface/
			 * for details.
			 */
		//	ahrs = new AHRS(SPI.Port.kMXP);
		} catch (RuntimeException ex) {
			DriverStation.reportError(
					"Error instantiating navX MXP:  " + ex.getMessage(), true);
		}
		// MOTORS FOR COMPEITION ROBOT
		left_front = new VictorSP(robotSystem.DRIVE_LEFT_FRONT);
		right_front = new VictorSP(robotSystem.DRIVE_RIGHT_FRONT);
		left_back = new VictorSP(robotSystem.DRIVE_LEFT_BACK);
		right_back = new VictorSP(robotSystem.DRIVE_RIGHT_BACK);

		left_front.setExpiration(.1);
		right_front.setExpiration(.1);
		left_back.setExpiration(.1);
		right_back.setExpiration(.1);

		myrobot = new RobotDrive(left_front, left_back, right_front, right_back);

		myrobot.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, true);
		myrobot.setInvertedMotor(RobotDrive.MotorType.kFrontRight, true);
		myrobot.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
		myrobot.setInvertedMotor(RobotDrive.MotorType.kRearRight, true);

		// ENCODERS
		Right_Encoder = new Encoder(robotSystem.RIGHT_ENCODER_A,
				robotSystem.RIGHT_ENCODER_B);
		Right_Encoder.setDistancePerPulse(6 * 3.14159265389 / 500);
		Left_Encoder = new Encoder(robotSystem.LEFT_ENCODER_A,
				robotSystem.LEFT_ENCODER_B);
		Left_Encoder.setDistancePerPulse(6 * 3.14159265389 / 500);

		// YAW RATE (GYRO)
		// roboGyro = new AnalogGyro(robotSystem.GYRO);

		// CONTROLLER
		Right_Encoder.setPIDSourceType(PIDSourceType.kDisplacement);

		RightFront_PID = new PIDController(Right_Kp, Right_Ki, Right_Kd,
				Right_Encoder, right_front);
		RightRear_PID = new PIDController(Right_Kp, Right_Ki, Right_Kd,
				Right_Encoder, right_back);

		Left_Encoder.setPIDSourceType(PIDSourceType.kDisplacement);
		LeftFront_PID = new PIDController(Left_Kp, Left_Ki, Left_Kd,
				Left_Encoder, left_front);
		LeftRear_PID = new PIDController(Left_Kp, Left_Ki, Left_Kd,
				Left_Encoder, left_back);

		LeftFront_PID.setAbsoluteTolerance(3);
		LeftRear_PID.setAbsoluteTolerance(3);
		RightFront_PID.setAbsoluteTolerance(3);
		RightRear_PID.setAbsoluteTolerance(3);

		LeftFront_PID.setOutputRange(-1, 1);
		LeftRear_PID.setOutputRange(-1, 1);

		RightFront_PID.setOutputRange(-1, 1);
		RightRear_PID.setOutputRange(-1, 1);

		// PIDGyro = new PIDController(Kp,Ki,Kd,roboGyro,PIDGyroOut);

		// sol = new DoubleSolenoid(robot.GRIPPER_SIDE_A, robot.GRIPPER_SIDE_B);
		// PowerDistributionPanel pdp = new PowerDistributionPanel();
		errorSum=0;
	}

	public void aimLight(boolean turnOn) {
		if (turnOn)
			aimLightRelay.set(Relay.Value.kForward);
		else
			aimLightRelay.set(Relay.Value.kOff);
	}

	public void tankDrive(double left, double right) {
		/*
		 * left_front.set(left); left_back.set(left); right_front.set(right);
		 * right_back.set(right);
		 */
		myrobot.tankDrive(left, right);
	}

	public void driveStop() {

		left_front.set(0);
		left_back.set(0);
		right_front.set(0);
		right_back.set(0);

	}

	public void safteyEnabled() {
		left_front.setSafetyEnabled(true);
		left_back.setSafetyEnabled(true);
		right_front.setSafetyEnabled(true);
		right_back.setSafetyEnabled(true);
		myrobot.setSafetyEnabled(true);

	}

	public void safteyDisabled() {
		left_front.setSafetyEnabled(false);
		left_back.setSafetyEnabled(false);
		right_front.setSafetyEnabled(false);
		right_back.setSafetyEnabled(false);
		myrobot.setSafetyEnabled(false);

	}

	public double getAccel(){
	//	accelvalue =  ahrs.getWorldLinearAccelZ();
				
		return accelvalue;
	}
	
	/*
	public boolean driveStraight(double heading, double speed) {
	//	double error = heading - ahrs.getYaw();
	//	double turnRate = error * Kp;
		double maxturnRate = .33;
		if (turnRate > maxturnRate)
			turnRate = maxturnRate;
		else if (turnRate < -maxturnRate)
			turnRate = -maxturnRate;
		myrobot.arcadeDrive(-speed, turnRate);
		
		
		if (Math.abs(error)<5)
			doneTurn =true;
		else
			doneTurn = false;

		// SmartDashboard.putNumber("Gyro", ahrs.getYaw());
		return doneTurn;
	}
	
	public void driveNoGyro(double heading, double speed) {
		
		myrobot.arcadeDrive(-speed, heading);

		// SmartDashboard.putNumber("Gyro", ahrs.getYaw());

	}
*/
	public boolean aimingRobot(double aimHeading, double targetDistance) {

		/*
		 * xpos =table.getNumber("x_position", defaultValue); ypos
		 * =table.getNumber("y_position", defaultValue); distance
		 * =table.getNumber("distance", defaultValue); targetfound
		 * =table.getNumber("found_target", defaultValue);
		 */
		double headingError = xposTarget - aimHeading;
		errorSum=errorSum+headingError;
		double turnRate = headingError * aimKp+errorSum*aimKi;
		double maxturnRate = .6;
		
		//System.out.println("error, errorSum, turnRate:  " + headingError + ",  "+ errorSum + ",  "+ turnRate);
		
		
		if (turnRate > maxturnRate)
			turnRate = maxturnRate;
		else if (turnRate < -maxturnRate)
			turnRate = -maxturnRate;

		double speedError = camdistance- targetDistance;
		double speedRate = speedError * speedKp;
		double maxSpeedRate = .666;

		if (speedRate > maxSpeedRate)
			speedRate = maxSpeedRate;
		else if (speedRate < -maxSpeedRate)
			speedRate = -maxSpeedRate;

		if (speedError <= 0) {
			speedRate = 0;
			moveDone = true;
		} else
			moveDone = false;
		/*
		 * System.out.println("xpos = " + xpos); System.out.println("ypos = " +
		 * ypos); System.out.println("distance = " + distance);
		 * System.out.println("targetfound = " + targetfound);
		 */
		System.out.println("speedRate:  " + speedRate);
		
		myrobot.arcadeDrive(0, turnRate);
		//myrobot.arcadeDrive(-speedRate, 0);
		
		//myrobot.arcadeDrive(speedRate, turnRate);

		return moveDone;
	}

	public boolean targetHot(double targetDist) {
		/*
		 * xpos =table.getNumber("x_position", defaultValue); ypos
		 * =table.getNumber("y_position", defaultValue); distance
		 * =table.getNumber("distance", defaultValue); targetfound
		 * =table.getNumber("found_target", defaultValue);
		 */

		if ((camdistance >= (targetDist - SHOOTDISTTOL))
				&& (camdistance <= (targetDist + SHOOTDISTTOL))
				&& (xposTarget >= (AIMPOINT - AIMPOINTTOL))
				&& (xposTarget <= (AIMPOINT + AIMPOINTTOL)))
			targetHot = true;
		else
			targetHot = false;

		/*
		 * SmartDashboard.putNumber("Xpos", xpos);
		 * SmartDashboard.putNumber("Ypos", ypos);
		 * SmartDashboard.putNumber("Distance", distance);
		 * SmartDashboard.putNumber("Targetfound", targetfound);
		 */
		return targetHot;
	}

	public void cameraOutput() {
		xpos = table.getNumberArray("centerX", defaultValue);
		ypos = table.getNumberArray("centerY", defaultValue);
		width = table.getNumberArray("width", defaultValue);
		height = table.getNumberArray("height", defaultValue);
		area = table.getNumberArray("area", defaultValue);
		
		targetfound = 0;
		xposTarget=0;
		yposTarget=0;
		widthTarget=0;
		heightTarget=0;
		areaTarget=0;
		int len;
		
		//can't be sure if all the arrays are the same length, so lets pick the smallest one
		len=Math.min(xpos.length, Math.min(ypos.length, Math.min(width.length, Math.min(height.length, area.length))));
		
		
		System.out.println("xpos, area len is: " + xpos.length + "  " + area.length);
		if (len>0){
			if (len==1){
				if (area[0]>MIN_AREA){
					targetfound = 1;
					xposTarget=xpos[0];
					yposTarget=ypos[0];
					widthTarget=width[0];
					heightTarget=height[0];
					areaTarget=area[0];
				}
			} else {
				//find the biggest
				int previous = 0;
				
				for (int i=1;i<len;i++){
					if (area[i]>area[previous]){
						previous=i;
					}
				}
				
				//previous is now the biggest
				if (area[previous]>MIN_AREA){
					targetfound = 1;
					xposTarget=xpos[previous];
					yposTarget=ypos[previous];
					widthTarget=width[previous];
					heightTarget=height[previous];
					areaTarget=area[previous];
				}
			}
		}
		camdistance=0;	
		if (targetfound==1){
					//	fudgefactor * targetsize in feet * 360 pixels wide / (2 * widthTarget * tangent(68.5))
			camdistance=(7.5/.45) * (20/12) * 360 / (2 * widthTarget * 2.538647);
		}

		atmySpot=targetHot(SHOOTDISTANCE);
			
		SmartDashboard.putNumber("Xpos", xposTarget);
		SmartDashboard.putNumber("Ypos", yposTarget);
		SmartDashboard.putNumber("Width", widthTarget);
		SmartDashboard.putNumber("Height", heightTarget);
		SmartDashboard.putNumber("area", areaTarget);
		SmartDashboard.putNumber("Distance", camdistance);
		SmartDashboard.putNumber("Targetfound", targetfound);
		SmartDashboard.putBoolean("TargetHot", atmySpot);
			

	}

/*	public double getGyro() {
		return ahrs.getYaw();
	}

	public void GyroReset() {
		ahrs.zeroYaw();
	}
*/
	public double rightEncoderDistance() {
		double distance_Right;

		distance_Right = Right_Encoder.getDistance();
		
		// SmartDashboard.putNumber("Average of left side encoder : ",averageDistance);
		// SmartDashboard.putNumber("left Encoder Distance2 : ",distance_Left);
		 SmartDashboard.putNumber("Right Encoder Distance2 : ",distance_Right);

		return distance_Right;
	}

	public double EncoderDistance() {

		double distance_Left;
		double distance_Right;

		double averageDistance;
		// System.out.println("in EncoderDistance ");
		distance_Left = Left_Encoder.getDistance();
		distance_Right = Right_Encoder.getDistance();

		/*
		 * System.out.println("distance_Left = " + distance_Left);
		 * System.out.println("distance_Right = " + distance_Right);
		 */
		averageDistance = (-distance_Left + -distance_Right) / 2;
		// System.out.println("averageDistance = " + averageDistance);

		// SmartDashboard.putNumber("Average of left side encoder : ",averageDistance);
		// SmartDashboard.putNumber("left Encoder Distance2 : ",distance_Left);
		// SmartDashboard.putNumber("Right Encoder Distance2 : ",distance_Right);

		return averageDistance;
	}

	public void EncoderRate() {
		double rate;
		System.out.println("in EncoderRate");

		rate = Left_Encoder.getRate();
		rate = Right_Encoder.getRate();

		System.out.println("rate = " + rate);
		/*
		 * SmartDashboard.putNumber("left Encoder Rate: ",
		 * Left_Encoder.getRate());
		 * SmartDashboard.putNumber("Right Encoder Rate : ",
		 * Right_Encoder.getRate());
		 */
	}

	public void EncoderReset() {
		Left_Encoder.reset();
		Right_Encoder.reset();
	}

	public boolean DriveStraightDistance(double distance) {
		// SmartDashboard.putNumber("left Encoder Distance : ",
		// Left_Encoder.getDistance());
		// SmartDashboard.putNumber("Right Encoder Distance : ",
		// Right_Encoder.getDistance());
		RightFront_PID.setSetpoint(-distance);
		RightRear_PID.setSetpoint(-distance);

		LeftFront_PID.setSetpoint(distance);
		LeftRear_PID.setSetpoint(distance);

		return LeftRear_PID.onTarget();
	}

	public void PIDEnable() {
		LeftFront_PID.enable();
		LeftRear_PID.enable();
		RightFront_PID.enable();
		RightRear_PID.enable();
	}

	public void PIDDisable() {
		LeftFront_PID.disable();
		LeftRear_PID.disable();
		RightFront_PID.disable();
		RightRear_PID.disable();

	}

}
