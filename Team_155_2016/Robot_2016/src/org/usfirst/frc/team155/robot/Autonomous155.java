package org.usfirst.frc.team155.robot;

//import edu.wpi.first.wpilibj.DigitalInput;
//import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;

//import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Autonomous155 {
/*
	DRIVE155 robotDrive;
	Shooter155 robotShooter;
	RobotMap155 robotMap;

	boolean driving = true;
	boolean straighten = true;

	final int DRIVEFORWARD1 = 1;
	final int DRIVEFORWARD2 = 2;
	
	final int POSTAIMMOVE = 34;
	final int AIMFIRST = 56;
	final int AIMSECOND = 84;
	final int CAPTUREFRAME = 63;	
	final int SLOWWALL = 3;
	final int AIM = 3;
	final int FIRE = 4;
	final int STOP = 5;
	final int PRE=0;
	int state = 1;
	double DEFENSE_DIST = 75;
	double COURTYARD_DIST = 180;
	double startTimeDRIVE = 0;
	boolean moveDone = false;
	double starttime=0;
	
	int color = 2;
	
	double speed = 0;
	double heading = 0;

	public Autonomous155(DRIVE155 drive, Shooter155 shooter, RobotMap155 robot) {
		robotDrive = drive;
		robotShooter = shooter;
		robotMap = robot;
	}

	public void megaMode() { // Moving and Shooting
		double FOV_horizontal = 68.5;
		double distance=0;
		
		switch (state) {
		case DRIVEFORWARD1:
			// move to defense
			speed = 1;
			heading = 0;
			if (robotDrive.rightEncoderDistance() >= DEFENSE_DIST) {
				state = DRIVEFORWARD2;
			}
			driving = true;
			break;
		case DRIVEFORWARD2:
			// move over defense to courtyard
			speed = 1;
			heading = 0;
			if (robotDrive.rightEncoderDistance() >= COURTYARD_DIST-30) {
				state = CAPTUREFRAME;
				driving = false;
			}
			robotShooter.autoLift(.5);
			
			break;
		case CAPTUREFRAME:
			//capture a frame and figure out a heading
			if (robotDrive.targetfound>0)
			{
				heading=(robotDrive.xposTarget-320)/640*FOV_horizontal+robotDrive.ahrs.getYaw();
				distance=robotDrive.camdistance;
				driving = true;
				state=AIMFIRST;
			}
			break;
		case AIMFIRST:
			//turn to the target			
			if(robotDrive.doneTurn)
			{
				state=POSTAIMMOVE;
			}
				
			break;
		case POSTAIMMOVE:
			//move up to the target
			speed = 1;
			heading = 0;
			if (robotDrive.rightEncoderDistance() >= distance) { //are we close enough?
				if (robotDrive.targetfound>0)						//can we still find the target?
				{
					heading=(robotDrive.xposTarget-320)/640*FOV_horizontal+robotDrive.ahrs.getYaw();
					distance=robotDrive.camdistance;
					driving = true;
					state=AIMSECOND;
				}
			}
			driving = true;

			break;
		case AIMSECOND:
			//turn to the target... again to make sure it is truly lined up			
			if(robotDrive.doneTurn && robotDrive.targetHot(robotDrive.SHOOTDISTANCE))
			{
				state=FIRE;
				startTimeDRIVE = Timer.getFPGATimestamp();
				robotDrive.driveStop();
				color = 6;//party
			}
			
			
			robotShooter.shooter.set(.85);	//turn on the shooter

			//do stuff with the lights
			if (robotDrive.targetfound>0 ){	//is there a target?
				color = 3;//flashing green
			} else {
				color = 4;//red
			}

			break;
		case FIRE:
			
			if ((Timer.getFPGATimestamp()) - (startTimeDRIVE) > 2)
				robotShooter.feeder.set(robotShooter.feedShooterSpeed);
			if ((Timer.getFPGATimestamp()) - (startTimeDRIVE) > 3.5)
				state = STOP;

			break;
		case STOP:
			speed = 0;
			heading = 0;
			robotShooter.feeder.set(0);
			robotShooter.shooter.set(0);

			break;
		}
		
		robotShooter.setColorMode(color);
		
		robotDrive.driveStraight(heading, speed);
		/*
		if (driving)
			robotDrive.driveStraight(heading, speed);
		else {
			moveDone = robotDrive.aimingRobot(robotDrive.AIMPOINT,
					robotDrive.SHOOTDISTANCE); // aiming method
		
		}
	}

	public void move2Court() { // Moving and Shooting, hold ball
		

		switch (state) {
		case DRIVEFORWARD1:
			// move to defense
			speed = 1;
			heading = 0;
			if (robotDrive.rightEncoderDistance() >= DEFENSE_DIST) {
				state = DRIVEFORWARD2;
			}
			driving = true;
			break;
		case DRIVEFORWARD2:
			// move over defense to courtyard
			speed = 1;
			heading = 0;
			if (robotDrive.rightEncoderDistance() >= COURTYARD_DIST) {
				state = STOP;
				driving = false;
			}

			break;

		case STOP:
			speed = 0;
			heading = 0;
			robotShooter.feeder.set(0);
			robotShooter.shooter.set(0);

			break;
		}

		if (driving)
			robotDrive.driveStraight(heading, speed);
		else {
			robotDrive.driveStop();
			; // aiming method

		}
	}
	
	public void move2CourtNoGyro() { // Moving and Shooting, hold ball
		

		switch (state) {
		case DRIVEFORWARD1:
			// move to defense
			speed = 1;
			heading = 0;
			if (robotDrive.rightEncoderDistance() >= DEFENSE_DIST) {
				state = DRIVEFORWARD2;
			}
			driving = true;
			break;
		case DRIVEFORWARD2:
			// move over defense to courtyard
			speed = 1;
			heading = 0;
			if (robotDrive.rightEncoderDistance() >= COURTYARD_DIST) {
				state = STOP;
				driving = false;
			}

			break;

		case STOP:
			speed = 0;
			heading = 0;
			robotShooter.feeder.set(0);
			robotShooter.shooter.set(0);

			break;
		}

		if (driving)
			robotDrive.driveNoGyro(heading, speed);
		else {
			robotDrive.driveStop();
			; // aiming method

		}
	}

	public void move2CourtNoGyroTime() { // Ramparts
		
		
		switch (state) {
		case 1:
			starttime = Timer.getFPGATimestamp();
			state = 2;
			break;
		case 2:
			// move to defense
			speed = 1;
			heading = 0;
			if (Timer.getFPGATimestamp() - starttime > 1) {
				state = 3;
			}
			driving = true;
			break;
		case 3:
			// move over defense to courtyard
			speed = 1;
			heading = 0;
			if (Timer.getFPGATimestamp() - starttime > 2.5){
				state = 4;
				driving = false;
			}

			break;

		case 4:
			speed = 0;
			heading = 0;
			robotShooter.feeder.set(0);
			robotShooter.shooter.set(0);
			driving=false;

			break;
		}

		if (driving)
			robotDrive.driveNoGyro(heading, speed);
		else {
			robotDrive.driveStop();
			; // aiming method

		}
	}
	
	public void move2CourtTimeGyroEnd() { // Ramparts
		
		
		switch (state) {
		case 1:
			starttime = Timer.getFPGATimestamp();
			state = 2;
			break;
		case 2:
			// move to defense
			speed = 1;
			heading = 0;
			if (Timer.getFPGATimestamp() - starttime > 1) {
				state = 3;
			}
			driving = true;
			break;
		case 3:
			// move over defense to courtyard
			speed = 1;
			heading = 0;
			if (Timer.getFPGATimestamp() - starttime > 3){
				state = 4;
				driving = false;
				straighten = true;
			}

			break;
			
		case 4:
			// Straighten
			speed = 0;
			heading = 0;
			if (Timer.getFPGATimestamp() - starttime > 6){
				state = 5;
				driving = false;
				straighten =false;
			}

			break;

		case 5:
			speed = 0;
			heading = 0;
			robotShooter.feeder.set(0);
			robotShooter.shooter.set(0);
			driving=false;

			break;
		}

		if (driving)
			robotDrive.driveNoGyro(heading, speed);
		
		else if (straighten)
			robotDrive.driveStraight(heading, speed);
		
		else {
			robotDrive.driveStop();
			

		}
	}
	
	public void move2Def() { // Moving to the defense and stop
		

		switch (state) {
		case DRIVEFORWARD1:
			// move to defense
			speed = .75;
			heading = 0;
			if (robotDrive.rightEncoderDistance() >= DEFENSE_DIST) {
				state = STOP;
				driving = false;
			}
			driving = true;
			break;

		case STOP:
			speed = 0;
			heading = 0;
			robotShooter.feeder.set(0);
			robotShooter.shooter.set(0);

			break;
		}

		if (driving)
			robotDrive.driveStraight(heading, speed);
		else {
			robotDrive.driveStop();
			; // aiming method

		}
	}

	public void stoneWall() { // Moving and Shooting, hold ball
	

		switch (state) {
		case DRIVEFORWARD1:
			// move to defense
			speed = 1;
			heading = 0;
			if (robotDrive.rightEncoderDistance() >= DEFENSE_DIST) {
				state = SLOWWALL;
			}
			driving = true;
			break;
		case DRIVEFORWARD2:
			// move over defense to courtyard
			speed = 1;
			heading = 0;
			if (robotDrive.rightEncoderDistance() >= COURTYARD_DIST) {
				state = STOP;
				driving = false;
			}

			break;

		case SLOWWALL:

			// move over defense to courtyard
			speed = .85;
			heading = 0;
			if (robotDrive.rightEncoderDistance() >= DEFENSE_DIST + 10) {
				state = DRIVEFORWARD2;
				driving = false;
			}

			break;

		case STOP:
			speed = 0;
			heading = 0;
			robotShooter.feeder.set(0);
			robotShooter.shooter.set(0);

			break;
		}

		if (driving)
			robotDrive.driveStraight(heading, speed);
		else {
			robotDrive.driveStop();
			; // aiming method

		}
	}
	*/
}

