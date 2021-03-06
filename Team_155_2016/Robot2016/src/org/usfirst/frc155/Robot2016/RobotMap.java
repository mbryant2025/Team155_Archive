// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc155.Robot2016;

// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Victor;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    public static SpeedController tankDriveRightFront;
    public static SpeedController tankDriveRightRear;
    public static SpeedController tankDriveLeftFront;
    public static SpeedController tankDriveLeftRear;
    public static RobotDrive tankDriveRobotDrive41;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS

    public static void init() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
        tankDriveRightFront = new Victor(0);
        LiveWindow.addActuator("Tank Drive", "RightFront", (Victor) tankDriveRightFront);
        
        tankDriveRightRear = new Victor(1);
        LiveWindow.addActuator("Tank Drive", "RightRear", (Victor) tankDriveRightRear);
        
        tankDriveLeftFront = new Victor(2);
        LiveWindow.addActuator("Tank Drive", "LeftFront", (Victor) tankDriveLeftFront);
        
        tankDriveLeftRear = new Victor(3);
        LiveWindow.addActuator("Tank Drive", "LeftRear", (Victor) tankDriveLeftRear);
        
        tankDriveRobotDrive41 = new RobotDrive(tankDriveLeftFront, tankDriveLeftRear,
              tankDriveRightFront, tankDriveRightRear);
        
        tankDriveRobotDrive41.setSafetyEnabled(true);
        tankDriveRobotDrive41.setExpiration(0.1);
        tankDriveRobotDrive41.setSensitivity(0.5);
        tankDriveRobotDrive41.setMaxOutput(1.0);


    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
    }
}
