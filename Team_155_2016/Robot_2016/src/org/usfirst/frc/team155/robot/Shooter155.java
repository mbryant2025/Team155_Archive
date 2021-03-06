package org.usfirst.frc.team155.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Encoder;
//import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.RobotDrive;
//import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.CANTalon;
//import edu.wpi.first.wpilibj.CANTalon;
import com.ctre.CANTalon.*;
import com.ctre.phoenix.motorcontrol.*;

//import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
//import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

//import com.kauailabs.navx.frc.AHRS;

public class Shooter155 {

	RobotMap155 robotSystem;
	DRIVE155 robotDrive;
	//SmartDashboard SmartDashboard;
	LiveWindow lw;
	// Vision155 vision;

	// JOYSTICKS
	Joystick dsStick;
	Joystick rightStick;
	//AHRS ahrs;
	
	// MOTORS FOR COMPEITION ROBOT

	public CANTalon shooter;
	public VictorSP gatherSpin;
	public CANTalon gatherLift;
	public VictorSP feeder;
	

	// Digital Inputs
	public DigitalInput ballFront;
	public DigitalInput ballBack;
	public DigitalInput shooterSwitch;

	// Analog Inputs
	public AnalogInput armPot;
	public AnalogInput ballFinder;
	
	
	
	/*
	 * // MOTORS FOR TEST ROBOT public Jaguar left_front; public Jaguar
	 * right_front; public Jaguar left_back; public Jaguar right_back;
	 * 
	 * // MOTORS TO SUCK AND SPIT TOTE public Jaguar right_Sucker; public Jaguar
	 * left_Sucker;
	 */

	public RobotDrive myrobot;

	// DRIVE ENCODERS
	public Encoder Shooter_Encoder;

	public PIDController shooterPID;
	public PIDController armPID;
	
	//arduino control pins
	public DigitalOutput ard_0;
	public DigitalOutput ard_1;
	public DigitalOutput ard_2;
	
	public double startTimeShooter;

	// CONTROLLERS
	double ShooterKp = .05;
	double ShooterKi = 0;
	double ShooterKd = 0;

	double distVoltage = 0;
	boolean inRange = false;
	double voltTol = 0;

	double ArmKp = -4.5;
	double ArmKi = .5;
	double ArmKd = 0;

	double ArmUpSpeed = -.75;
	double ArmDownSpeed = .68;
	double ArmHignLimit = 2.2;
	double ArmLowLimit = .18;
	double SUCKHIEGHT = .28;
	
	boolean gotBall=false;

	double SHOOTINGHEIGHT = 2;

	final int SUCK = 0;
	final int SPIT = 1;
	final int HOLD = 2;
	final int PREFIRE = 3;
	final int FIRE = 4;
	int state = PREFIRE;

	double camKp = .005;

	// Digital inputs
	DigitalInput suckerSwitch;

	// Encoder Controllers
	double Shooter_Kp = .05;
	double Shooter_Ki = 0;
	double Shooter_Kd = 0;

	double suckSpeed = .65;
	double spitSpeed = -1;

	double feedSuckSpeed = .5;  //was "1" before gearbox change
	double feedSpitSpeed = -1;
	double feedCenteringSpeed = .5;
	double feedShooterSpeed = 1;
	double stop = 0;

	boolean resetpid = false;

	double motorSpeed;
	boolean atSpeed = false;
	double RPMtol = 1000;
	double stickTol = .1;

	double motorScale = 1;
	boolean manual = false;

	double dsYstick = 0;
	double motorOutput = 0;
	double shootVelocity = 0;
	double targetSpeed = 0;
	double encoderCounts = 0;

	double NOBALL = .07;
	
	boolean climbModeToggled;
	//boolean stopShooter=false;
	
	final int ON=1;
	final int OFF=0;

	// SOLENOIDS
	/*
	 * DoubleSolenoid sol; private final int UP = 0; private final int DOWN = 1;
	 * private int armState = DOWN;
	 */
	// int _loops = 0;

	public Shooter155(Joystick dsio, Joystick right, RobotMap155 robot, DRIVE155 drive) {
		robotSystem = robot;
		robotDrive = drive;
		//SmartDashboard = new SmartDashboard();
		lw = new LiveWindow();
		rightStick = right;
		// ballFront = new DigitalInput(robotSystem.BALL_FRONT);
		//ballFront = new DigitalInput(6);
		//ballBack = new DigitalInput(7);
		//ahrs = new AHRS(SPI.Port.kMXP);
		
		climbModeToggled=false;
		
		ard_0=new DigitalOutput(robotSystem.ARDUINO_0);
		ard_1=new DigitalOutput(robotSystem.ARDUINO_1);
		ard_2=new DigitalOutput(robotSystem.ARDUINO_2);
		
		//
		// ballBack = new DigitalInput(robotSystem.BALL_BACK);
		// shooterSwitch = new DigitalInput(robotSystem.SHOOTER_SWITCH);

		armPot = new AnalogInput(robotSystem.ARM_POT);
		ballFinder = new AnalogInput(robotSystem.BALL_FINDER);

		// vision = new Vision155();
		// JOYSTICKS - NEEDS MORE APPROPRIATE NAMES
		dsStick = dsio;

		shooter = new CANTalon(robotSystem.CANSHOOTER);
		gatherSpin = new VictorSP(robotSystem.GATHERERSPIN);
		gatherLift = new CANTalon(robotSystem.CANGATHERERLIFT);
		// gatherLift.reverseOutput(true);
		feeder = new VictorSP(robotSystem.FEEDER);

		
		//shooter.setFeedbackDevice(FeedbackDevice.CTRE_MagEncoder_Relative);
	
		shooter.reverseSensor(true);

		/* set the peak and nominal outputs, 12V means full */
	//	shooter.configNominalOutputVoltage(+0.0f, -0.0f);
	//	shooter.configPeakOutputVoltage(+12.0f, 0.0f);
		/* set closed loop gains in slot0 */
	/*	shooter.setProfile(0);
		shooter.setF(0);
		shooter.setP(0);
		shooter.setI(0);
		shooter.setD(0); */

		// navx_init();

		// MOTORS FOR COMPEITION ROBOT

		/* set the peak and nominal outputs, 12V means full */
		// gatherLift.configNominalOutputVoltage(+0.0f, -0.0f);
		// gatherLift.configPeakOutputVoltage(+12.0f, -12.0f);

		// ENCODERS
		// Shooter_Encoder = new Encoder(robotSystem.RIGHT_ENCODER_A,
		// robotSystem.RIGHT_ENCODER_B);
		// Shooter_Encoder.setDistancePerPulse(6 * 3.14159265389 / 500);

		// CONTROLLER

	//	armPID = new PIDController(ArmKp, ArmKi, ArmKd, armPot, gatherLift);

		armPID.setAbsoluteTolerance(.1); // This could change base on pot
											// voltage

		armPID.setOutputRange(ArmUpSpeed, ArmDownSpeed);

		// PIDGyro = new PIDController(Kp,Ki,Kd,roboGyro,PIDGyroOut);

		// sol = new DoubleSolenoid(robot.GRIPPER_SIDE_A, robot.GRIPPER_SIDE_B);
		//PowerDistributionPanel pdp = new PowerDistributionPanel();

	}

	
	
	
	public void  runShooterManualSpeed() {
		/* get joystick axis */
		dsYstick =.5 * rightStick.getZ() + .5; // convert to just positive
											// output
		
		dsYstick=.12*dsYstick+.88;
		
		motorOutput = shooter.getOutputVoltage() / shooter.getBusVoltage();

		motorSpeed = shooter.getSpeed();
		shootVelocity = shooter.getEncVelocity();
		encoderCounts = shooter.getEncPosition();
		
		shooter.changeControlMode(TalonControlMode.PercentVbus);

			// TalonControlMode.Voltage
			shooter.set(dsYstick);

			if ((motorOutput > dsYstick - stickTol)
					&& (motorOutput < dsYstick + stickTol))
				atSpeed = true;
			else
				atSpeed = false;
		}
	
	public void  runShooterSetSpeed() {
		/* get joystick axis */
		//dsYstick =.5 * rightStick.getZ() + .5; // convert to just positive
													// output
		motorOutput = shooter.getOutputVoltage() / shooter.getBusVoltage();

		motorSpeed = shooter.getSpeed();
		shootVelocity = shooter.getEncVelocity();
		encoderCounts = shooter.getEncPosition();
		
		shooter.changeControlMode(TalonControlMode.PercentVbus);

			// TalonControlMode.Voltage
			shooter.set(.88);

			if ((motorOutput > dsYstick - stickTol)
					&& (motorOutput < dsYstick + stickTol))
				atSpeed = true;
			else
				atSpeed = false;
		}
	
	
	public void runShooterManualTEST() {
		/* get joystick axis */
		dsYstick =.5 * rightStick.getZ() + .5; // convert to just positive
													// output
		
		dsYstick=.12*dsYstick+.88;
		motorOutput = shooter.getOutputVoltage() / shooter.getBusVoltage();

		motorSpeed = shooter.getSpeed();
		shootVelocity = shooter.getEncVelocity();
		encoderCounts = shooter.getEncPosition();
		
		shooter.changeControlMode(TalonControlMode.PercentVbus);

			// TalonControlMode.Voltage
			shooter.set(dsYstick);

			if ((motorOutput > dsYstick - stickTol)
					&& (motorOutput < dsYstick + stickTol))
				atSpeed = true;
			else
				atSpeed = false;
		}

	
	
	
	public void runShooterManual() {
		/* get joystick axis */
		dsYstick = .5 * dsStick.getRawAxis(3) + .5; // convert to just positive
													// output
		motorOutput = shooter.getOutputVoltage() / shooter.getBusVoltage();

		motorSpeed = shooter.getSpeed();
		shootVelocity = shooter.getEncVelocity();
		encoderCounts = shooter.getEncPosition();

		if (dsStick.getRawButton(robotSystem.DS_12))
			targetSpeed = dsYstick * -31000.0;

		if (dsStick.getRawButton(robotSystem.SHOOTER_SWITCH)) {
			
			/* Speed mode */
			// targetSpeed = dsYstick * -31000.0;

			shooter.changeControlMode(TalonControlMode.Speed);

			shooter.set(targetSpeed);

			if ((motorSpeed > targetSpeed - RPMtol)
					&& (motorSpeed < targetSpeed + RPMtol))
				atSpeed = true;
			else
				atSpeed = false;
			// System.out.println("Target Speed =" + (targetSpeed));
		} else {
			// shooter.disable();
			/* Percent voltage mode */
			shooter.changeControlMode(TalonControlMode.PercentVbus);

			// TalonControlMode.Voltage
			shooter.set(dsYstick);

			if ((motorOutput > dsYstick - stickTol)
					&& (motorOutput < dsYstick + stickTol))
				atSpeed = true;
			else
				atSpeed = false;
		}

	}

	public void shooterSwitch(int shooterState){
		switch(shooterState){
		case OFF:
			
		break;
		case ON:
			
			break;
		
		}
	}
	
	
	
		
	public boolean voltShooter(double distance) {
		double distToVolts =1;
		shooter.changeControlMode(TalonControlMode.Voltage);
			//y = mx + b 
			//b= min di
		distVoltage = distance*distToVolts; //formula for setting motor voltage based in distance from target

		shooter.set(distVoltage);

		if ((shooter.getOutputVoltage() > distVoltage - voltTol)
				&& (shooter.getOutputVoltage() < distVoltage + voltTol))
			atSpeed = true;
		else
			atSpeed = false;
		
		if (atSpeed && robotDrive.targetHot(distance))
			inRange = true;
		else
			inRange = false;

		return inRange;
	}

	public void manualLift() {
		armPID.disable();
		/*
		 * if(manual = false){ armPID.disable(); manual =true; }
		 */
		// System.out.println("manualLift is running...");
		// System.out.println("highLimit.get()" + highLimit.get());
		// System.out.println("lowLimit.get()" + lowLimit.get());
		// System.out.println("rightStick.getY()" + rightStick.getY());
		// System.out.println("armpot" + armPot.getAverageVoltage());

		if ((armPot.getAverageVoltage() > ArmHignLimit)
				&& (dsStick.getRawButton(robotSystem.ARM_UP))
				|| ((armPot.getAverageVoltage() < ArmLowLimit) && (dsStick
						.getRawButton(robotSystem.ARM_DOWN))))
			gatherLift.set(stop);

		else if (dsStick.getRawButton(robotSystem.ARM_UP))
			gatherLift.set(ArmUpSpeed+.1);
		else if (dsStick.getRawButton(robotSystem.ARM_DOWN))
			gatherLift.set(ArmDownSpeed);
		else
			gatherLift.set(stop);

	}

	public void autoLift(double setPoint) {
		/*
		if ((armPot.getAverageVoltage() > ArmHignLimit)
				|| (armPot.getAverageVoltage() < ArmLowLimit)) {

			if ((armPot.getAverageVoltage() < ArmLowLimit)
					&& (armPID.get() >= 0)) {
				armPID.enable();
				// liftMotorPID.setSetpoint(setPoint);
			} else if ((armPot.getAverageVoltage() > ArmHignLimit)
					&& (armPID.get() <= 0)) {
				armPID.enable();
				// liftMotorPID.setSetpoint(setPoint);
			} else {
				armPID.disable();
				// liftMotorPID.reset();
				gatherLift.set(0);
			}
		} else {
			armPID.enable();
			// liftMotorPID.setSetpoint(setPoint);// You Will Never Find A Pony
			// More Majestic Than Waffles
		}
		*/
		armPID.enable();
		armPID.setSetpoint(setPoint);
		// SmartDashboard.putNumber("lift motor PID is", liftMotorPID.get());
		// SmartDashboard.putNumber("Lift Height = ",
		// liftEncoder.getDistance());
	}

	public void shootSensorsOld() {
		//SmartDashboard.putBoolean("Ball Front", ballFront.get());
		//SmartDashboard.putBoolean("Ball Back", ballBack.get());
		//SmartDashboard.putNumber("Arm Pot", armPot.getAverageVoltage());
		//SmartDashboard.putNumber("Shooter Velocity", shootVelocity);
		//SmartDashboard.putNumber("motorOutput", motorOutput);
		//SmartDashboard.putNumber("motorSpeed", motorSpeed);
		//SmartDashboard.putNumber("targetSpeed", targetSpeed);
		//SmartDashboard.putNumber("dsYstick", dsYstick);
		//SmartDashboard.putNumber("EncoderCounts", encoderCounts);
		//SmartDashboard.putNumber("State", state);
		//SmartDashboard.putBoolean("OnTarget", armPID.onTarget());
		//SmartDashboard.putBoolean("atSpeed", atSpeed);
		//SmartDashboard.putNumber("Ball Finder", ballFinder.getAverageVoltage());
		//SmartDashboard.putBoolean("GOT BALL", gotBall);
		//SmartDashboard.putBoolean("atSpeed", atSpeed);

	}
	public void shootSensors() {
		
		SmartDashboard.putNumber("Arm Pot", armPot.getAverageVoltage());
		//SmartDashboard.putNumber("Shooter Velocity", shootVelocity);
		//SmartDashboard.putNumber("motorOutput", motorOutput);
		//SmartDashboard.putNumber("motorSpeed", motorSpeed);
		//SmartDashboard.putNumber("targetSpeed", targetSpeed);
		//SmartDashboard.putNumber("dsYstick", dsYstick);
		//SmartDashboard.putNumber("EncoderCounts", encoderCounts);
		//SmartDashboard.putNumber("State", state);
		//SmartDashboard.putBoolean("OnTarget", armPID.onTarget());
		//SmartDashboard.putBoolean("atSpeed", atSpeed);
		SmartDashboard.putNumber("Ball Finder", ballFinder.getAverageVoltage());
		SmartDashboard.putBoolean("GOT BALL", gotBall);
		SmartDashboard.putBoolean("atSpeed", atSpeed);

	}

	public void manFeeder() {
		if (dsStick.getRawButton(robotSystem.SUCK))
			feeder.set(feedSuckSpeed);
		else if (dsStick.getRawButton(robotSystem.SPIT))
			feeder.set(feedSpitSpeed);
		else
			feeder.set(stop);
	}

	public void manGather() {
		if (dsStick.getRawButton(robotSystem.SUCK))
			gatherSpin.set(suckSpeed);
		else if (dsStick.getRawButton(robotSystem.SPIT))
			gatherSpin.set(spitSpeed);
		else
			gatherSpin.set(stop);
	}

	public boolean gatherSystem(boolean stopShooter) {
		if (dsStick.getRawButton(robotSystem.SHOOTER_SWITCH_OFF))
			manualLift();
		else if (dsStick.getRawButton(robotSystem.SUCK)
				|| dsStick.getRawButton(robotSystem.SPIT))
			autoLift(SUCKHIEGHT); // lifts arm to spit
		else if (dsStick.getRawButton(robotSystem.SETARMPOS))
			autoLift(SHOOTINGHEIGHT); // lifts arm to shoot position
		else if (dsStick.getRawButton(robotSystem.CLIMBMODE))
			autoLift(SUCKHIEGHT); // lifts arm to shoot position

		else {
			manualLift();
		}

		switch (state) {
		case SUCK:
			/*
			ard_0.set(false);
			ard_1.set(false);
			ard_2.set(false);
			*/
			
			// Run Motors based on DSIO button to suck
			// if (dsStick.getRawButton(robotSystem.SUCK) &&
			// (armPID.onTarget())) {
			if (dsStick.getRawButton(robotSystem.SUCK)) {
				
				if(armPot.getAverageVoltage()<SUCKHIEGHT){
					gatherSpin.set(suckSpeed);
				}
				else gatherSpin.set(stop);
				feeder.set(feedSuckSpeed);
			}

			else {
				gatherSpin.set(stop);
				feeder.set(stop);
			}
			

			if (ballFinder.getAverageVoltage() < NOBALL) {
				gatherSpin.set(stop);
				feeder.set(stop);
				state = HOLD;
			} else if (dsStick.getRawButton(robotSystem.SPIT))
				state = SPIT;
			else
				state = SUCK;

			break;
		case SPIT:
			// Run Motors to spit
			if (!dsStick.getRawButton(robotSystem.SPIT))
				state = SUCK;
			// if (armPID.onTarget())
			if(armPot.getAverageVoltage()<SUCKHIEGHT){
				gatherSpin.set(spitSpeed);
			}
			else gatherSpin.set(stop);
			feeder.set(feedSpitSpeed);
			/*
			ard_0.set(true);
			ard_1.set(true);
			ard_2.set(true);
			*/
			break;

		case HOLD:
			
			stopShooter = false;
			if (ballFinder.getAverageVoltage() < NOBALL) {
				// Stop Motors Wait for PREFIRE or SPIT
				gatherSpin.set(stop);
				feeder.set(stop);
			}

			if (dsStick.getRawButton(robotSystem.SHOOTER_SWITCH))
				state = PREFIRE;
			else if (dsStick.getRawButton(robotSystem.SPIT))
				state = SPIT;
			break;

		case PREFIRE:
			
			// Check if ready to fire(Motor up to speed)
			if (inRange && rightStick.getRawButton(robotSystem.FIRE)) {
				state = FIRE;
				startTimeShooter = Timer.getFPGATimestamp();
			}

			if (dsStick.getRawButton(robotSystem.SPIT))
				state = SPIT;

			//autoLift(SHOOTINGHEIGHT); // lifts arm to catch from human loader

			break;
		case FIRE:
			// Run Feeder to shooter
			feeder.set(feedShooterSpeed);

			if (Timer.getFPGATimestamp() - startTimeShooter > 1){
				state = SUCK;
				stopShooter = true;
			}

			break;

		}

		return stopShooter;
	}

	

/*	public boolean gatherSystemTEST(boolean stopShooter, boolean upToSpeed) {

		if (dsStick.getRawButton(robotSystem.SHOOTER_SWITCH_OFF))
			manualLift();
		else if (dsStick.getRawButton(robotSystem.SUCK))
			autoLift(SUCKHIEGHT); // lifts arm to spit
		else if (dsStick.getRawButton(robotSystem.SPIT))
		autoLift(SUCKHIEGHT+.12); // lifts arm to spit
		
		else if (!stopShooter){
			autoLift(SHOOTINGHEIGHT);
			if((armPot.getAverageVoltage()>SHOOTINGHEIGHT-.1)&&(armPot.getAverageVoltage()<SHOOTINGHEIGHT+.1))
				//gatherLift.set(0);// lifts arm to shoot position
			
		//else {autoLift(SHOOTINGHEIGHT);
			
			}
		}
		else if (dsStick.getRawButton(robotSystem.CLIMBMODE))
			autoLift(SUCKHIEGHT); // lifts arm to shoot position

		else {
			manualLift();
		}
		if (ballFinder.getAverageVoltage() < NOBALL) {
			gotBall =true;
			
		}
		else {
			gotBall = false;
		}
		
		
		if (dsStick.getRawButton(robotSystem.CLIMBMODE))
			climbModeToggled=true;
		
		if (!climbModeToggled)
			modeSelect(gotBall,!stopShooter,upToSpeed);
		
		
		
		switch (state) {
		case SUCK:
			
			// Run Motors based on DSIO button to suck
			// if (dsStick.getRawButton(robotSystem.SUCK) &&
			// (armPID.onTarget())) {
			if (dsStick.getRawButton(robotSystem.SUCK)) {
				if(armPot.getAverageVoltage()<SUCKHIEGHT+1){
					gatherSpin.set(suckSpeed);
				}
				else gatherSpin.set(stop);
				feeder.set(feedSuckSpeed);
			}

			else {
				gatherSpin.set(stop);
				feeder.set(stop);
			}

			if (ballFinder.getAverageVoltage() < NOBALL) {
				gatherSpin.set(stop);
				feeder.set(stop);
				state = HOLD;
			} else if (dsStick.getRawButton(robotSystem.SPIT))
				state = SPIT;
			else
				state = SUCK;
			break;
		case SPIT:
			// Run Motors to spit
			if (!dsStick.getRawButton(robotSystem.SPIT))
				state = SUCK;
			// if (armPID.onTarget())
			if(armPot.getAverageVoltage()<SUCKHIEGHT+1){
				gatherSpin.set(spitSpeed);
			}
			else gatherSpin.set(stop);
			feeder.set(feedSpitSpeed);
			
			break;

		case HOLD:
			
			//stopShooter = true;
			if (ballFinder.getAverageVoltage() < NOBALL) {
				// Stop Motors Wait for PREFIRE or SPIT
				gatherSpin.set(stop);
				feeder.set(stop);
			}
			//if(stopShooter=false) {
			
			//if (dsStick.getRawButton(robotSystem.SHOOTER_SWITCH))
				state = PREFIRE;
			//}
			if (dsStick.getRawButton(robotSystem.SPIT))
				state = SPIT;
			break;

		case PREFIRE:

			// Check if ready to fire(Motor up to speed)
			if (rightStick.getRawButton(robotSystem.FIRE)) {
				state = FIRE;
				startTimeShooter = Timer.getFPGATimestamp();
			}

			if (dsStick.getRawButton(robotSystem.SPIT))
				state = SPIT;

			//autoLift(SHOOTINGHEIGHT); // lifts arm to catch from human loader

			break;
		case FIRE:
			// Run Feeder to shooter
			feeder.set(feedShooterSpeed);

			if (Timer.getFPGATimestamp() - startTimeShooter > 1){
				state = SUCK;
				stopShooter = true;
			}

			break;

		}

		return stopShooter;
	}
	*/
	/*
	public void gatherSystem1() {

		if (dsStick.getRawButton(robotSystem.SUCK)
				|| dsStick.getRawButton(robotSystem.SPIT))
			autoLift(SUCKHIEGHT); // lifts arm to catch from human loader
		else {
			manualLift();
		}

		switch (state) {
		case SUCK:
			// Run Motors based on DSIO button to suck
			// if (dsStick.getRawButton(robotSystem.SUCK) &&
			// (armPID.onTarget())) {
			if (dsStick.getRawButton(robotSystem.SUCK)) {
				gatherSpin.set(suckSpeed);
				feeder.set(feedSuckSpeed);
			}

			else {
				gatherSpin.set(stop);
				feeder.set(stop);
			}

			if (ballFront.get())
				state = HOLD;
			else if (dsStick.getRawButton(robotSystem.SPIT))
				state = SPIT;
			else
				state = SUCK;

			break;
		case SPIT:
			// Run Motors to spit
			if (!dsStick.getRawButton(robotSystem.SPIT))
				state = SUCK;
			// if (armPID.onTarget())
			gatherSpin.set(spitSpeed);
			feeder.set(feedSpitSpeed);

			break;

		case HOLD:

			if (ballFront.get() && ballBack.get()) {
				// Stop Motors Wait for PREFIRE or SPIT
				gatherSpin.set(stop);
				feeder.set(stop);
			}

			else if (ballBack.get()) {
				// Run Motor Reverse slowly
				gatherSpin.set(stop);
				feeder.set(feedCenteringSpeed * feedSpitSpeed);
			} else {
				// Run Motor to suck in
				gatherSpin.set(suckSpeed);
				feeder.set(feedSuckSpeed);
			}

			if (dsStick.getRawButton(PREFIRE))
				state = PREFIRE;
			else if (dsStick.getRawButton(robotSystem.SPIT))
				state = SPIT;
			break;

		case PREFIRE:
			// Check if ready to fire(Motor up to speed)
			if (atSpeed && rightStick.getRawButton(robotSystem.FIRE)) {
				state = FIRE;
				startTimeShooter = Timer.getFPGATimestamp();
			}

			if (dsStick.getRawButton(robotSystem.SPIT))
				state = SPIT;
			break;
		case FIRE:
			// Run Feeder to shooter
			feeder.set(feedShooterSpeed);

			if (Timer.getFPGATimestamp() - startTimeShooter > 1)
				state = SUCK;

			break;

		}

	}*/
	
	public void modeSelect(boolean ball, boolean motor, boolean fire){
		int total=0;
		
		if (ball)
			total=total+4;
		if (motor)
			total=total+2;
		if (fire)
			total=total+1;
		
		switch (total){		//ball		motor	fire
		case 0:				//false		false	false
			setColorMode(0);
			break;			//ball		motor	fire
		case 1:				//false		false	true
			
			break;			//ball		motor	fire
		case 2:				//false		true	false
			setColorMode(5);
			break;			//ball		motor	fire
		case 3:				//false		true	true
			setColorMode(4);
			break;			//ball		motor	fire
		case 4:				//true		false	false
			setColorMode(1);
			break;			//ball		motor	fire
		case 5:				//true		false	true
			
			break;			//ball		motor	fire
		case 6:				//true		true	false
			setColorMode(2);
			break;			//ball		motor	fire
		case 7:				//true		true	true
			setColorMode(3);
			break;			//ball		motor	fire
			
		}
		
	}
	
	public void setColorMode(int colormode){
//		System.out.println("colormode =" + (colormode));
		switch (colormode){
		case 0:					//no ball
			ard_0.set(false);
			ard_1.set(false);
			ard_2.set(false);
//			System.out.println("trying 0");	
			break;
		case 1:					//have ball
			ard_0.set(true);
			ard_1.set(false);
			ard_2.set(false);
//			System.out.println("trying 1");
			break;
		case 2:					//shooter not up to speed
			ard_0.set(false);
			ard_1.set(true);
			ard_2.set(false);
//			System.out.println("trying 2");
			break;
		case 3:					//shooter up to speed and have ball
			ard_0.set(true);
			ard_1.set(true);
			ard_2.set(false);
//			System.out.println("trying 3");
			break;
		case 4:				//shooter up to speed and no ball
			ard_0.set(false);
			ard_1.set(false);
			ard_2.set(true);
//			System.out.println("trying 4");
			break;
		case 5:				//shooter on and no ball
			ard_0.set(true);
			ard_1.set(false);
			ard_2.set(true);
//			System.out.println("trying 5");
			break;
		case 6:				//climb mode
			ard_0.set(false);
			ard_1.set(true);
			ard_2.set(true);
//			System.out.println("trying 6");
			break;
		case 7:				//disabled	
			ard_0.set(true);
			ard_1.set(true);
			ard_2.set(true);
//			System.out.println("trying 7");
			break;
		
		
		}
		
		
	}
}
