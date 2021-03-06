package org.usfirst.frc.team155.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Autonomous {

	DRIVE155 robotDrive;
	Lift155 robotLift;
	robotMap155 robotMap;
	CameraThread robotVision;
	// Vision155 robotVision;
	Joystick rightStick;

	// arcade state
	final int DRIVE0 = 0;
	final int TURN180 = 1;
	final int DRIVE180 = 2;
	final int LIFT_TOTE = 3;

	// Mech mode 1
	final int DRIVEFORWARD1 = 0;
	final int LIFTTOTE1 = 1;
	final int DRIVEBACK1 = 2;
	final int DRIVESIDE1 = 3;
	final int STOP1 = 4;
	int BOX_COUNTER = 0;
	// mecanum state
	final int DRIVEBACK = 0;
	final int DRIVESIDE = 1;
	final int DRIVEFOWARD = 2;
	final int STOP = 3;
	public int state = 0;
	public boolean TOTE_SWITCH = false;
	public double startTimeDRIVE;
	boolean readyToCarry = false;
	DigitalInput toteSwitch;
	// start2
	final int START2 = 0;
	final int WAIT2 = 1;
	final int FIRSTTOTE2 = 2;
	final int LIFTTOTE2 = 3;
	final int DRIVEFORWARD2 = 4;
	final int TURN90 = 5;
	final int DRIVESTRAIGHT2 = 6;
	final int STOP2 = 7;
	final int DROPTOTE2 = 8;
	final int BACKUP2 = 9;
	final int FINALSTOP2 = 10;

	// MEGA MODE
	final int STARTMM = 0;
	final int LIFTMM = 1;
	final int DRIVESIDEWAYSMM = 2;
	final int LONGFORWARDMM = 3;
	final int DRIVESIDEBACKMM = 4;
	final int SHORTFORWARDMM = 5;
	final int DROPARMMM = 6;
	final int DRIVESIDELONGMM = 7;
	final int FINALDROPMM = 8;
	final int BACKUPMM = 9;
	final int FINALSTOPMM = 10;

	// start3
	final int START3 = 0;
	final int DRIVESIDEWAYS3 = 1;
	final int STOP3 = 2;
	final int BACKUP3 = 3;
	final int FINALSTOP3 = 4;

	// start4
	final int START4 = 0;
	final int WAIT4 = 1;
	final int FIRSTTOTE4 = 2;
	final int LIFTTOTE4 = 3;
	final int TURN904 = 4;
	final int DRIVESTRAIGHT4 = 5;
	final int STOP4 = 6;
	final int DROPTOTE4 = 7;
	final int BACKUP4 = 8;
	final int FINALSTOP4 = 9;
	public int drivestate = 1;

	// start barrel and tote
	final int STARTTB = 0;
	final int STOPTB = 1;
	final int BACKUPTB = 2;
	final int LIFTTB = 3;
	final int DRIVESIDEWAYSTB = 4;
	final int FINALSTOPTB = 5;

	// public Autonomous(DRIVE155 drive, Lift155 lift, robotMap155 robot,
	// Vision155 vision) {
	public Autonomous(DRIVE155 drive, Lift155 lift, robotMap155 robot, CameraThread vision) {
		robotDrive = drive;
		robotLift = lift;
		robotMap = robot;
		robotVision = vision;
		rightStick = new Joystick(2);

	}

	public void run() {

		double speed;
		double heading = 0;
		if (robotLift.measureDistance() > 36) {
			speed = .5;
		} else
			speed = 0;
		// robotLift.measureDistance();
		robotDrive.driveStraight(heading, speed);

	}

	public void centerTote() {
		if (robotVision.hasFoundTote())
			robotDrive.centerYellowTote(300, 0, robotVision.getTotePosition());

		else
			robotDrive.mecanumstop();
		SmartDashboard.putBoolean("foundtote", robotVision.hasFoundTote());
	}

	public void driveToAutoZone() {

		switch (drivestate) {
		case 1:

			robotDrive.PIDEnable();
			robotDrive.DriveStraightDistance(-160);

			if (robotDrive.DriveStraightDistance(-160))
				drivestate = 2;
			break;

		case 2:
			System.out.println("in STOP4");
			robotDrive.PIDDisable();
			drivestate = 3;
			break;
		/*
		 * case 3: System.out.println("in BACKUP4");
		 * robotDrive.DriveStraightDistance(-160);
		 * 
		 * if (robotDrive.DriveStraightDistance(-160)) state = 4; break;
		 * 
		 * case 4: System.out.println("in FINALSTOP4"); robotDrive.PIDDisable();
		 * break;
		 */

		}
	}

	public void driveBackToAutoZone() {

		switch (drivestate) {
		case 1:

			robotDrive.PIDEnable();
			robotDrive.DriveStraightDistance(-100);

			if (robotDrive.DriveStraightDistance(-100))
				drivestate = 2;
			break;

		case 2:
			System.out.println("in STOP4");
			robotDrive.PIDDisable();
			//drivestate = 3;
			break;
			
			
		//case 3 added as it was not defined previously, it was commented out	
		case 3:
			
			break;
		/*
		 * case 3: System.out.println("in BACKUP4");
		 * robotDrive.DriveStraightDistance(-160);
		 * 
		 * if (robotDrive.DriveStraightDistance(-160)) state = 4; break;
		 * 
		 * case 4: System.out.println("in FINALSTOP4"); robotDrive.PIDDisable();
		 * break;
		 */

		}
	}

	public void driveToTote() {
		double midPoint = -40;
		double finalPoint = -80;
		double fullSpeed = -.25;
		double slowSpeed = -.125;
		double speed;

		if (robotDrive.EncoderDistance() > midPoint)
			speed = fullSpeed;
		else if (robotDrive.EncoderDistance() > finalPoint)
			speed = slowSpeed;
		else
			speed = 0;

		if (robotVision.hasFoundTote())
			robotDrive.centerYellowTote(300, speed, robotVision.getTotePosition());

		else
			robotDrive.mecanumstop();
		SmartDashboard.putBoolean("foundtote", robotVision.hasFoundTote());
	}

	public boolean driveToToteRangeFinder() {
		System.out.println("In driveToToteRangeFinder");

		// double midPoint = 40;
		// ??????????????????? CHANGE THIS DEPENDING ON RANGE FINDER position in
		// new robot
		// double finalPoint = 8;
		double finalPoint = 12;
		double fullSpeed = -.18;
		// double slowSpeed = -.25;
		double speed;
		double toteDistance;
		boolean reachTote = false;

		toteDistance = robotLift.measureDistance();
		System.out.println("toteDistance = " + toteDistance);
		SmartDashboard.putNumber("RangeFinderDistance-driveToToteRangeFinder", toteDistance);

		if (toteDistance > finalPoint) {
			System.out.println("toteDistance is > 8");
			speed = fullSpeed;
			System.out.println("speed = " + speed);

			if (robotVision.hasFoundTote()) {
				System.out.println("Tote found!! goto centerYellowTote");
				System.out.println("goalposition = 300");
				System.out.println("speed = " + speed);
				robotDrive.centerYellowTote(300, speed, robotVision.getTotePosition());
			} else {
				System.out.println("Tote NOT found!! goto mecanumstop");
				robotDrive.mecanumstop();
			}
		} else {
			System.out.println("toteDistance is <= 8, Reached the tote!!!");
			speed = 0;
			System.out.println("speed = " + speed);
			reachTote = true;
		}
		SmartDashboard.putBoolean("foundtote", robotVision.hasFoundTote());
		return reachTote;

	}

	public void autoMode() {
		double speed = 0;
		double heading = 0;
		double direction = 90;

		switch (state) {
		case DRIVEFORWARD1:

			break;
		case LIFTTOTE1:
			speed = 0;
			heading = 0;
			direction = 90;
			double carryHeight;
			// lift the tote

			if (readyToCarry == false) {
				carryHeight = robotLift.GROUND_LEVEL;
				if (robotLift.onTarget() == true)
					readyToCarry = true;
			} else
				carryHeight = robotLift.CARRY_BOX;
			if ((readyToCarry == true) && (robotLift.onTarget()) && (carryHeight == robotLift.CARRY_BOX))
				state = DRIVEFORWARD1;
			robotLift.autoLift(carryHeight);

			break;
		case DRIVEBACK1:
			speed = .5;
			heading = 0;
			direction = 270;
			if ((Timer.getFPGATimestamp()) - (startTimeDRIVE) > .5) {
				state = DRIVESIDE1;
				startTimeDRIVE = Timer.getFPGATimestamp();
			}
			break;
		case DRIVESIDE1:
			speed = .5;
			heading = 0;
			direction = 180;
			if ((Timer.getFPGATimestamp()) - (startTimeDRIVE) > 1)
				if (robotLift.measureDistance() <= 36) {
					state = DRIVEFORWARD1;
				}

			break;
		case STOP1:
			speed = 0;
			heading = 0;
			direction = 180;
			break;
		}
		robotDrive.driveMecanum(heading, speed, direction);

	}

	public void liftTest() {
		// lift the tote

		double carryHeight;

		if (readyToCarry == false) {
			carryHeight = robotLift.GROUND_LEVEL;
			System.out.println("Here 1");
			if (robotLift.onTarget() == true) {
				readyToCarry = true;
				System.out.println("Here 2");
			}
		} else {
			carryHeight = robotLift.CARRY_BOX;
			System.out.println("Here 3");
		}

		if ((readyToCarry == true) && (robotLift.onTarget()) && (carryHeight == robotLift.CARRY_BOX)) {
			if (rightStick.getRawButton(1)) {
				carryHeight = robotLift.GROUND_LEVEL;
				readyToCarry = false;
				System.out.println("Here 4");
			}

		}
		robotLift.autoLift(carryHeight);
		System.out.println("Carry height " + carryHeight);
		SmartDashboard.putBoolean("Ready to Carry = ", readyToCarry);
		SmartDashboard.putBoolean("on target = ", robotLift.onTarget());
		SmartDashboard.putBoolean("Joystick trigger = ", rightStick.getRawButton(1));

	}

	public void autoForward() {
		double speed = 0;
		double heading = 0;
		double direction = 0;

		switch (state) {
		case DRIVEFOWARD:
			speed = .5;
			heading = 0;
			direction = 90;
			if (robotLift.measureDistance() <= 36) {
				state = TURN180;
			}
			break;
		case DRIVEBACK:
			speed = .5;
			heading = 0;
			direction = 180;
			if (Math.abs(robotDrive.roboGyro.getAngle() - 180) <= 5) {
				state = DRIVE180;
			}
			break;
		case DRIVESIDE:
			speed = .5;
			heading = 0;
			direction = 90;
			if (robotLift.measureDistance() <= 36) {
				state = STOP;
			}
			break;
		case STOP:
			speed = 0;
			heading = 0;
			break;
		}
		robotDrive.driveMecanum(heading, speed, direction);
	}

	// Push tote/barrel into scoring zone and stop
	public void autoLine3() {

		switch (state) {
		case START3:
			System.out.println("in START3");

			startTimeDRIVE = Timer.getFPGATimestamp();
			state = STOP3;
			// robotDrive.PIDEnable();
			break;
		case STOP3:
			robotLift.autoLift(12);
			;
			if (robotLift.onTarget()) {
				state = DRIVESIDEWAYS3;
			}
			break;

		case DRIVESIDEWAYS3:

			robotDrive.team155Mecanum_fieldOriented(.5, 0, 0);

			if (robotDrive.EncoderDistance() < -210)
				state = FINALSTOP3;

			// robotDrive.DriveSideDistance(160);

			// if (robotDrive.DriveSideDistance(160))
			// state = FINALSTOP3;
			break;

		case FINALSTOP3:
			System.out.println("in FINALSTOP2");

			robotDrive.PIDDisable();
			break;

		}
	}
	
	public void GrabBoth() {

		switch (state) {
		case START3:
			System.out.println("in START3");

			startTimeDRIVE = Timer.getFPGATimestamp();
			state = STOP3;
			// robotDrive.PIDEnable();
			break;
		case STOP3:
			robotLift.autoLift(12);
			;
			if ((Timer.getFPGATimestamp()) - (startTimeDRIVE) > 1) {
				state = DRIVESIDEWAYS3;
			}
			break;

		case DRIVESIDEWAYS3:

			robotDrive.team155Mecanum_fieldOriented(.5, 0, 0);

			if (robotDrive.EncoderDistance() < -160)
				state = FINALSTOP3;

			// robotDrive.DriveSideDistance(160);

			// if (robotDrive.DriveSideDistance(160))
			// state = FINALSTOP3;
			break;

		case FINALSTOP3:
			System.out.println("in FINALSTOP2");

			robotDrive.PIDDisable();
			break;

		}
	}
	public void GrabBarrel() {

		switch (state) {
		case START3:
			System.out.println("in START3");

			startTimeDRIVE = Timer.getFPGATimestamp();
			state = STOP3;
			// robotDrive.PIDEnable();
			break;
		case STOP3:
			robotLift.autoLift(12);
			;
			if ((Timer.getFPGATimestamp()) - (startTimeDRIVE) > 1) {
				state = DRIVESIDEWAYS3;
			}
			break;

		case DRIVESIDEWAYS3:

			robotDrive.team155Mecanum_fieldOriented(0,-.5,0);

			if (robotDrive.Back_Right_Encoder.getDistance() > 160)
				state = FINALSTOP3;

			// robotDrive.DriveSideDistance(160);

			// if (robotDrive.DriveSideDistance(160))
			// state = FINALSTOP3;
			break;

		case FINALSTOP3:
			System.out.println("in FINALSTOP2");

			robotDrive.PIDDisable();
			break;

		}
	}
	
	public void GrabBothPushBarrel() {

		switch (state) {
		case 1://Start
			System.out.println("in START3");

			startTimeDRIVE = Timer.getFPGATimestamp();
			state = 2;
			// robotDrive.PIDEnable();
			break;
		case 2://Lift
			robotLift.autoLift(12);
			;
			if ((Timer.getFPGATimestamp()) - (startTimeDRIVE) > 1) {
				state = 3;
			}
			break;

		case 3://Move Left

			robotDrive.team155Mecanum_fieldOriented(-.5, 0, 0);

			if (robotDrive.EncoderDistance() > 30)
				state = 4;

			// robotDrive.DriveSideDistance(160);

			// if (robotDrive.DriveSideDistance(160))
			// state = FINALSTOP3;
			break;
			
		case 4://Move Forward

			robotDrive.team155Mecanum_fieldOriented(0, .5, 0);

			if (robotDrive.EncoderDistance() < -30)
				state = 5;

			// robotDrive.DriveSideDistance(160);

			// if (robotDrive.DriveSideDistance(160))
			// state = FINALSTOP3;
			break;
		case 5://Move right

			robotDrive.team155Mecanum_fieldOriented(.5, 0, 0);

			if (robotDrive.EncoderDistance() < -210)
				state = 6;

			// robotDrive.DriveSideDistance(160);

			// if (robotDrive.DriveSideDistance(160))
			// state = FINALSTOP3;
			break;

		case 6://Stop
			System.out.println("in FINALSTOP2");

			robotDrive.PIDDisable();
			break;

		}
	}


	public void autoLine7() {

		switch (state) {
		case START3:
			System.out.println("in START3");

			startTimeDRIVE = Timer.getFPGATimestamp();
			state = STOP3;
			// robotDrive.PIDEnable();
			break;
		case STOP3:
			robotLift.autoLift(12);
			;
			if (robotLift.onTarget()) {
				state = DRIVESIDEWAYS3;
			}
			break;

		case DRIVESIDEWAYS3:

			robotDrive.team155Mecanum_fieldOriented(.5, 0, 0);

			if (robotDrive.EncoderDistance() < -160)
				state = FINALSTOP3;

			// robotDrive.DriveSideDistance(160);

			// if (robotDrive.DriveSideDistance(160))
			// state = FINALSTOP3;
			break;

		case FINALSTOP3:
			System.out.println("in FINALSTOP2");

			robotDrive.PIDDisable();
			break;

		}
	}

	public void autoLine() {
		double speed = 0;
		double heading = 0;
		double direction = 90;

		switch (state) {
		case START2:
			System.out.println("in START2");
			System.out.println("speed = " + speed);
			System.out.println("heading = " + heading);
			System.out.println("direction = " + direction);

			startTimeDRIVE = Timer.getFPGATimestamp();
			state = WAIT2;
			break;

		case WAIT2:
			System.out.println("in WAIT2");
			speed = 0;
			heading = 0;
			direction = 90;
			System.out.println("speed = " + speed);
			System.out.println("heading = " + heading);
			System.out.println("direction = " + direction);

			robotDrive.driveMecanum(heading, speed, direction);
			if ((Timer.getFPGATimestamp()) - (startTimeDRIVE) > 1) {
				state = FIRSTTOTE2;
				startTimeDRIVE = Timer.getFPGATimestamp();
			}

			break;
		case FIRSTTOTE2:
			System.out.println("in FIRSTTOTE2");
			heading = 0;
			direction = 0;
			System.out.println("speed = " + speed);
			System.out.println("heading = " + heading);
			System.out.println("direction = " + direction);
			SmartDashboard.putNumber("RangeFinderDistance-FIRSTTOTE2", robotLift.measureDistance());
			System.out.println("RangeFinderDistance = " + robotLift.measureDistance());

			// ???????????????? change to reflect the range finder distance on
			// new robot
			// if (robotLift.measureDistance() <= 8) {
			if (robotLift.measureDistance() <= 12) {
				speed = 0;
				state = LIFTTOTE2;
			} else
				speed = .25;

			System.out.println("speed = " + speed);
			System.out.println("heading = " + heading);
			System.out.println("direction = " + direction);

			robotDrive.driveMecanum(heading, speed, direction);

			startTimeDRIVE = Timer.getFPGATimestamp();
			// SmartDashboard.putNumber("RangeFinderDistance",
			// robotLift.measureDistance());
			break;

		case LIFTTOTE2:
			System.out.println("in LIFTTOTE2");

			speed = 0;
			heading = 0;
			direction = 90;
			System.out.println("speed = " + speed);
			System.out.println("heading = " + heading);
			System.out.println("direction = " + direction);

			// lift the tote

			double carryHeight;

			if (readyToCarry == false) {
				carryHeight = robotLift.GROUND_LEVEL;
				if (robotLift.onTarget() == true)
					readyToCarry = true;
			} else
				carryHeight = robotLift.CARRY_BOX;

			if ((readyToCarry == true) && (robotLift.onTarget()) && (carryHeight == robotLift.CARRY_BOX)) {
				if (BOX_COUNTER < 3)
					state = DRIVEFORWARD2;
				else
					state = TURN90;
			}

			robotLift.autoLift(carryHeight);

			// Pauses code to simulate lift
			/*
			 * if ((Timer.getFPGATimestamp()) - (startTimeDRIVE) > 4) {
			 * BOX_COUNTER++; if (BOX_COUNTER < 3) { state = DRIVEFORWARD2; }
			 * else state = TURN90; }
			 */
			break;

		case DRIVEFORWARD2:
			System.out.println("in DRIVEFORWARD2");
			driveToToteRangeFinder();
			if (driveToToteRangeFinder())
				state = LIFTTOTE2;
			startTimeDRIVE = Timer.getFPGATimestamp();
			break;

		case TURN90:
			System.out.println("in TURN90");
			speed = 0;
			heading = -90;
			direction = 0;
			System.out.println("heading = " + heading);
			System.out.println("speed = " + speed);
			System.out.println("direction = " + direction);

			robotDrive.driveMecanum(heading, speed, direction);

			System.out.println("Math.abs(robotDrive.getGyro() = " + Math.abs(robotDrive.getGyro()));
			if (Math.abs(robotDrive.getGyro() - heading) < 5) {
				System.out.println("Math.abs(robotDrive.getGyro() - heading is < 5");
				System.out.println("PIDEnable and Reset Encoder");

				robotDrive.PIDEnable();
				robotDrive.EncoderReset();
				state = DRIVESTRAIGHT2;
			}
			break;

		case DRIVESTRAIGHT2:
			System.out.println("in DRIVESTRAIGHT2");
			System.out.println("heading = " + heading);
			System.out.println("speed = " + speed);
			System.out.println("direction = " + direction);

			robotDrive.DriveStraightDistance(84);

			if (robotDrive.DriveStraightDistance(84))
				state = STOP2;
			break;

		case STOP2:
			System.out.println("in STOP2");
			speed = 0;
			heading = 0;
			direction = 180;
			System.out.println("heading = " + heading);
			System.out.println("speed = " + speed);
			System.out.println("direction = " + direction);
			state = DROPTOTE2;
			robotDrive.PIDDisable();
			break;

		case DROPTOTE2:
			System.out.println("in DROPTOTE2");
			System.out.println("heading = " + heading);
			System.out.println("speed = " + speed);
			System.out.println("direction = " + direction);
			carryHeight = robotLift.GROUND_LEVEL;
			System.out.println("carry height = " + carryHeight);
			robotLift.autoLift(carryHeight);
			state = BACKUP2;
			break;

		case BACKUP2:
			System.out.println("in BACKUP2");
			speed = .25;
			// heading = 0;
			// direction = 180;
			System.out.println("heading = " + heading);
			System.out.println("speed = " + speed);
			System.out.println("direction = " + direction);
			System.out.println("speed is : " + speed);
			robotDrive.DriveStraightDistance(-24);
			state = FINALSTOP2;
			break;

		case FINALSTOP2:
			System.out.println("in FINALSTOP2");
			speed = 0;
			heading = 0;
			direction = 180;
			System.out.println("heading = " + heading);
			System.out.println("speed = " + speed);
			System.out.println("direction = " + direction);
			robotDrive.PIDDisable();
			break;
		}
	}

	public void autoLine4() {
		double speed = 0;
		double heading = 0;
		double direction = 90;
		switch (state) {
		case START4:
			System.out.println("in START4");
			System.out.println("speed = " + speed);
			System.out.println("heading = " + heading);
			System.out.println("direction = " + direction);

			startTimeDRIVE = Timer.getFPGATimestamp();
			state = FIRSTTOTE4;
			robotDrive.PIDEnable();
			break;

		case FIRSTTOTE4:
			System.out.println("in FIRSTTOTE4");
			heading = 0;
			direction = 0;

			// new robot
			robotDrive.DriveStraightDistance(12);
			if (robotDrive.DriveStraightDistance(12)) {
				robotDrive.PIDDisable();
				state = LIFTTOTE4;
			}

			startTimeDRIVE = Timer.getFPGATimestamp();
			// SmartDashboard.putNumber("RangeFinderDistance",
			// robotLift.measureDistance());
			break;

		case LIFTTOTE4:
			System.out.println("in LIFTTOTE4");

			speed = 0;
			heading = 0;
			direction = 90;

			// lift the tote

			if (robotLift.onTarget()) {
				state = TURN904;
			}

			robotLift.autoLift(5);

			// Pauses code to simulate lift
			/*
			 * if ((Timer.getFPGATimestamp()) - (startTimeDRIVE) > 4) {
			 * BOX_COUNTER++; if (BOX_COUNTER < 3) { state = DRIVEFORWARD2; }
			 * else state = TURN90; }
			 */
			break;

		case TURN904:
			System.out.println("in 4TURN90");
			speed = 0;
			heading = -90;
			direction = 0;
			System.out.println("heading = " + heading);
			System.out.println("speed = " + speed);
			System.out.println("direction = " + direction);

			robotDrive.driveMecanum(heading, speed, direction);

			System.out.println("Math.abs(robotDrive.getGyro() = " + Math.abs(robotDrive.getGyro()));
			if (Math.abs(robotDrive.getGyro() - heading) < 5) {
				System.out.println("Math.abs(robotDrive.getGyro() - heading is < 5");
				System.out.println("PIDEnable and Reset Encoder");

				robotDrive.PIDEnable();
				robotDrive.EncoderReset();
				state = DRIVESTRAIGHT4;
			}
			break;

		case DRIVESTRAIGHT4:
			System.out.println("in DRIVESTRAIGHT4");
			System.out.println("heading = " + heading);
			System.out.println("speed = " + speed);
			System.out.println("direction = " + direction);

			robotDrive.DriveStraightDistance(84);

			if (robotDrive.DriveStraightDistance(84))
				state = STOP4;
			break;

		case STOP4:
			System.out.println("in STOP4");
			speed = 0;
			heading = 0;
			direction = 180;
			System.out.println("heading = " + heading);
			System.out.println("speed = " + speed);
			System.out.println("direction = " + direction);
			state = DROPTOTE4;
			robotDrive.PIDDisable();
			break;

		case DROPTOTE4:
			System.out.println("in DROPTOTE4");
			System.out.println("heading = " + heading);
			System.out.println("speed = " + speed);
			System.out.println("direction = " + direction);

			robotLift.autoLift(robotLift.GROUND_LEVEL);
			if (robotLift.onTarget()) {
				state = BACKUP4;
				robotDrive.PIDEnable();
			}

			break;

		case BACKUP4:
			System.out.println("in BACKUP4");

			System.out.println("speedd is : " + speed);
			robotDrive.DriveStraightDistance(-24);
			state = FINALSTOP4;
			break;

		case FINALSTOP4:
			System.out.println("in FINALSTOP4");
			speed = 0;
			heading = 0;
			direction = 180;
			System.out.println("heading = " + heading);
			System.out.println("speed = " + speed);
			System.out.println("direction = " + direction);
			robotDrive.PIDDisable();
			break;

		}

	}

	public void autoLine5() {
		double speed = 0;
		double heading = 0;
		double direction = 90;
		switch (state) {
		case START4:
			System.out.println("in START4");

			startTimeDRIVE = Timer.getFPGATimestamp();
			state = FIRSTTOTE4;
			robotDrive.PIDDisable();

			robotLift.liftMotorPID.enable();
			break;

		case FIRSTTOTE4:

			// lift the tote

			if (robotLift.getLowLimit()) {
				state = LIFTTOTE4;
				robotLift.autoLift(1);
			}

			robotLift.autoLift(-24);
			System.out.println("setting the lift pid to: " + robotLift.liftMotorPID.getSetpoint());
			// System.out.println("is lift PID enabled?" +
			// robotLift.liftMotorPID.isEnable());

			// Pauses code to simulate lift
			/*
			 * if ((Timer.getFPGATimestamp()) - (startTimeDRIVE) > 4) {
			 * BOX_COUNTER++; if (BOX_COUNTER < 3) { state = DRIVEFORWARD2; }
			 * else state = TURN90; }
			 */
			break;

		case LIFTTOTE4:
			System.out.println("in LIFTTOTE4");

			speed = 0;
			heading = 0;
			direction = 90;

			// lift the tote

			if (robotLift.onTarget()) {
				state = TURN904;
			}

			robotLift.autoLift(12);
			System.out.println("setting the lift pid to: " + robotLift.liftMotorPID.getSetpoint());

			// Pauses code to simulate lift
			/*
			 * if ((Timer.getFPGATimestamp()) - (startTimeDRIVE) > 4) {
			 * BOX_COUNTER++; if (BOX_COUNTER < 3) { state = DRIVEFORWARD2; }
			 * else state = TURN90; }
			 */
			break;

		case TURN904:
			System.out.println("in 4TURN90");
			speed = 0;
			heading = -90;
			direction = 0;
			System.out.println("heading = " + heading);
			System.out.println("speed = " + speed);
			System.out.println("direction = " + direction);

			robotDrive.driveMecanum(heading, speed, direction);

			System.out.println("Math.abs(robotDrive.getGyro() = " + Math.abs(robotDrive.getGyro()));
			if (Math.abs(robotDrive.getGyro() - heading) < 5) {
				System.out.println("Math.abs(robotDrive.getGyro() - heading is < 5");
				System.out.println("PIDEnable and Reset Encoder");

				robotDrive.PIDEnable();
				robotDrive.EncoderReset();
				state = DRIVESTRAIGHT4;
			}
			break;

		case DRIVESTRAIGHT4:

			robotDrive.DriveStraightDistance(-84);

			if (robotDrive.DriveStraightDistance(-84))
				state = STOP4;
			break;

		case STOP4:
			System.out.println("in STOP4");

			state = DROPTOTE4;
			// robotDrive.PIDDisable();
			break;

		case DROPTOTE4:
			System.out.println("in DROPTOTE4");
			System.out.println("heading = " + heading);
			System.out.println("speed = " + speed);
			System.out.println("direction = " + direction);

			robotLift.autoLift(robotLift.GROUND_LEVEL);
			if (robotLift.onTarget()) {
				state = BACKUP4;
				robotDrive.PIDEnable();
			}

			break;

		case BACKUP4:
			System.out.println("in BACKUP4");

			System.out.println("speedd is : " + speed);
			robotDrive.DriveStraightDistance(-70);
			state = FINALSTOP4;
			break;

		case FINALSTOP4:
			System.out.println("in FINALSTOP4");

			robotDrive.PIDDisable();
			break;

		}

	}

	public void autoLine6() {
		double speed = 0;
		double heading = 0;
		double direction = 90;
		switch (state) {
		case START4:
			System.out.println("in START4");

			startTimeDRIVE = Timer.getFPGATimestamp();
			state = FIRSTTOTE4;
			robotDrive.PIDDisable();

			robotLift.liftMotorPID.enable();
			break;

		case FIRSTTOTE4:

			// lift the tote

			robotLift.autoliftTest(true);
			if (robotLift.onTarget()) {
				state = LIFTTOTE4;
			}

			break;
		case LIFTTOTE4:

			// lift the tote

			robotLift.autoliftTest(false);
			if (robotLift.onTarget()) {
				state = TURN904;
			}

			break;

		case TURN904:
			System.out.println("in 4TURN90");
			speed = 0;
			heading = -90;
			direction = 0;
			System.out.println("heading = " + heading);
			System.out.println("speed = " + speed);
			System.out.println("direction = " + direction);

			robotDrive.driveMecanum(heading, speed, direction);

			System.out.println("Math.abs(robotDrive.getGyro() = " + Math.abs(robotDrive.getGyro()));
			if (Math.abs(robotDrive.getGyro() - heading) < 5) {
				System.out.println("Math.abs(robotDrive.getGyro() - heading is < 5");
				System.out.println("PIDEnable and Reset Encoder");

				robotDrive.PIDEnable();
				robotDrive.EncoderReset();
				state = DRIVESTRAIGHT4;
			}
			break;

		case DRIVESTRAIGHT4:

			robotDrive.DriveStraightDistance(-160);

			if (robotDrive.DriveStraightDistance(-160))
				state = STOP4;
			break;

		case STOP4:
			System.out.println("in STOP4");

			state = DROPTOTE4;
			// robotDrive.PIDDisable();
			break;

		case DROPTOTE4:
			System.out.println("in DROPTOTE4");
			System.out.println("heading = " + heading);
			System.out.println("speed = " + speed);
			System.out.println("direction = " + direction);

			robotLift.autoLift(robotLift.GROUND_LEVEL);
			if (robotLift.getLowLimit()) {
				state = BACKUP4;
				robotDrive.PIDEnable();
				robotLift.liftMotorPID.disable();
			}

			break;

		case BACKUP4:
			System.out.println("in BACKUP4");

			System.out.println("speedd is : " + speed);
			robotDrive.DriveStraightDistance(-70);
			state = FINALSTOP4;
			break;

		case FINALSTOP4:
			System.out.println("in FINALSTOP4");

			robotDrive.PIDDisable();
			break;

		}

	}

	public void displayRangeFinder() {
		SmartDashboard.putNumber("RangeFinderDistance", robotLift.measureDistance());

	}

	public void MegaMode() {
		int toteCount = 0;
		double shortSideways = -80;
		double longSiddeways = -160;
		double shortForward = 24;
		double longForward = 160;
		double backup = -24;
		switch (state) {
		case STARTMM:
			startTimeDRIVE = Timer.getFPGATimestamp();
			state = LIFTMM;
			// robotDrive.PIDEnable();
			break;
		case LIFTMM:

			robotLift.autoLift(24);
			if (robotLift.onTarget()) {
				robotDrive.EncoderReset();
				if (toteCount >= 2) {
					state = DRIVESIDELONGMM;
				} else {
					state = DRIVESIDEWAYSMM;
				}
			}
			break;

		case DRIVESIDEWAYSMM:

			robotDrive.team155Mecanum_fieldOriented(.5, 0, 0);

			if (robotDrive.EncoderDistance() < shortSideways) {
				state = LONGFORWARDMM;
				robotDrive.EncoderReset();
			}
			// robotDrive.DriveSideDistance(160);

			// if (robotDrive.DriveSideDistance(160))
			// state = FINALSTOP3;
			break;

		case LONGFORWARDMM:
			if (robotDrive.DriveStraightDistance(longForward)) {
				state = DRIVESIDEBACKMM;
				robotDrive.EncoderReset();
			}
			break;

		case DRIVESIDEBACKMM:
			robotDrive.team155Mecanum_fieldOriented(-0.5, 0, 0);

			if (robotDrive.EncoderDistance() > -shortSideways) {
				state = SHORTFORWARDMM;
				robotDrive.EncoderReset();
			}
			break;
		case SHORTFORWARDMM:
			// robotDrive.toteSuck();
			if (robotDrive.DriveStraightDistance(shortForward) || robotDrive.suckerSwitch.get()) {
				state = DROPARMMM;
				if (!robotDrive.suckerSwitch.get()) {
					state = DRIVESIDELONGMM;
				}
				robotDrive.EncoderReset();
			}
			break;
		case DROPARMMM:
			robotLift.autoLift(-5);
			if (robotLift.getLowLimit()) {
				state = LIFTMM;
				toteCount++;
				robotLift.liftEncoder.reset();
				robotLift.autoLift(0);
			}
			break;

		case DRIVESIDELONGMM:
			robotDrive.team155Mecanum_fieldOriented(.5, 0, 0);

			if (robotDrive.EncoderDistance() < longSiddeways) {
				state = FINALDROPMM;
				robotDrive.EncoderReset();
			}
			break;
		case FINALDROPMM:
			robotLift.autoLift(-5);
			if (robotLift.getLowLimit()) {
				state = BACKUPMM;
				robotLift.liftMotorPID.disable();
			}
			break;

		case BACKUPMM:
			if (robotDrive.DriveStraightDistance(backup)) {
				state = FINALSTOPMM;
				robotDrive.EncoderReset();
			}
			break;
		case FINALSTOPMM:
			System.out.println("in FINALSTOP2");

			robotDrive.PIDDisable();
			break;

		}
	}

	// pickup tote, backup bet barrel, then move sideways to scoring zone
	public void toteBarrelToAutoZone() {

		switch (state) {
		case STARTTB:
			System.out.println("in start of toteBarrelToAutoZone");

			startTimeDRIVE = Timer.getFPGATimestamp();
			state = STOPTB;
			break;

		case STOPTB:
			robotLift.autoLift(1);
			
			if (robotLift.onTarget()) {
				state = BACKUPTB;
			}
			break;
			
//barrel/tote boxes are 2ft 9in (33in) apart
		case BACKUPTB:
			//
			if (robotDrive.DriveStraightDistance(-2))
				state = LIFTTB;

			break;

		case LIFTTB:
			robotLift.autoLift(11);
			
			if (robotLift.onTarget()) {
				state = DRIVESIDEWAYSTB;
			}

			break;

		case DRIVESIDEWAYSTB:

			robotDrive.team155Mecanum_fieldOriented(.5, 0, 0);

			if (robotDrive.EncoderDistance() < -160)
				state = FINALSTOPTB;

			break;

		case FINALSTOPTB:
			System.out.println("in FINALSTOPBT");

			robotDrive.PIDDisable();
			break;

		}
	}
}
