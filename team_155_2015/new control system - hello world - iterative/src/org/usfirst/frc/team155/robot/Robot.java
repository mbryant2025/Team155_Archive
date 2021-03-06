
package org.usfirst.frc.team155.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Gyro;
/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
	
    Joystick rightStick;
    Joystick leftStick;
    Talon leftFront;
    Talon rightFront;
    Talon leftRear;
    Talon rightRear;
    Victor liftDrive;
    AnalogPotentiometer anaIn;
   
    DoubleSolenoid sol;
    RobotDrive myrobot;
    double temp;
    boolean temp2;
    Gyro roboGyro;
    
    DigitalInput lowLimit;
    DigitalInput highLimit;
    
    public void robotInit() {
        leftStick = new Joystick(0);
        rightStick = new Joystick(1);
        lowLimit = new DigitalInput(0);
        highLimit = new DigitalInput(1);
        leftFront = new Talon(0);
        rightFront = new Talon(2);
        leftRear = new Talon(1);
        rightRear = new Talon(3);
        
        anaIn = new AnalogPotentiometer(1);
        //digIn = new DigitalInput(1);
        sol = new DoubleSolenoid(0, 1);
        
        
        liftDrive = new Victor(5);
        
        roboGyro = new Gyro(0);
        roboGyro.initGyro();
        
        
        temp = 0;
        myrobot = new RobotDrive(leftFront,leftRear,rightFront,rightRear);
        
        myrobot.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, true);
        myrobot.setInvertedMotor(RobotDrive.MotorType.kFrontRight, true);
        myrobot.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
        myrobot.setInvertedMotor(RobotDrive.MotorType.kRearRight, true);
    }
    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {

    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
    	myrobot.arcadeDrive(leftStick);
    	//myrobot.mecanumDrive_Cartesian(leftStick.getX(), leftStick.getY(), rightStick.getX(), roboGyro.getAngle());

    	if  (highLimit.get() || lowLimit.get())
    		if (lowLimit.get() && (rightStick.getY()<0))
    				liftDrive.set(rightStick.getY());
    		else if  (highLimit.get() && (rightStick.getY()>0))
    				liftDrive.set(rightStick.getY());
    		else 
    			liftDrive.set(0);
    	else 
    		liftDrive.set(rightStick.getY());
    	
if (rightStick.getTrigger()) 
	sol.set(DoubleSolenoid.Value.kForward);
else sol.set(DoubleSolenoid.Value.kReverse);

	}
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    	
    	
    
    }
    
}
