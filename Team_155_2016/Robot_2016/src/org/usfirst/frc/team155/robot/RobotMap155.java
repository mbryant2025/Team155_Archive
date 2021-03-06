package org.usfirst.frc.team155.robot;

public class RobotMap155 {
	//The constants to define robot connections
    //PWMs
	public final int DRIVE_LEFT_FRONT = 0;
    public final int DRIVE_LEFT_BACK = 1;
    public final int DRIVE_RIGHT_FRONT = 2;
    public final int DRIVE_RIGHT_BACK = 3;
    public final int GATHERERSPIN = 4;
    public final int FEEDER = 5;
    public final int PWMWINCH = 6;
    public final int SERVO = 7;
    public final int PWM8 = 8;
    public final int PWM9 = 9;
    public final int PWM10 = 10;
    
    //CAM MOTOR CONTROLLER
    
    public final int CANPDP = 1;
    public final int CANPCM = 2;
    public final int CANGATHERERLIFT = 3;
     public final int CANSHOOTER = 4;
    
    
    //solenoids
    public final int CLIMBARM_A = 0;
    public final int CLIMBARM_B = 1;
    public final int SOL_2 = 2;
    public final int SOL_3 = 3;
    public final int SOL_4 = 4;
    public final int SOL_5 = 5;
    public final int SOL_6 = 6;
    public final int SOL_7 = 7;
    
    //digital I/O
    public final int LIFT_ENCODER_A = 0;
    public final int LIFT_ENCODER_B = 1;
    public final int RIGHT_ENCODER_A = 2;
    public final int RIGHT_ENCODER_B = 3;
    public final int LEFT_ENCODER_A = 4;
    public final int LEFT_ENCODER_B = 5;
    //public final int BALL_FRONT = 10;
    //public final int BALL_BACK = 11;
    //public final int CLIMBHIGHLIM = 8;
    public final int CLIMBBACKSWITCH = 8;
    //public final int CLIMBLOWLIM = 12;
    public final int ARDUINO_0 = 6;
    public final int ARDUINO_1 = 7;
    public final int ARDUINO_2 = 9;
    
    //buttons
    public final int AIMINGLIGHT = 1;
    public final int Shoot_Off = 2;
    public final int ENCODER_RESET = 3;
    public final int GYRO_RESET = 4;
    public final int LEFTSTICK_4 = 5;
    public final int LEFTSTICK_6 = 6;
    public final int PID_DISABLE = 7;
    public final int AUTOAIM = 8;
    public final int LEFTSTICK_9 = 9;
    public final int LEFTSTICK_10 = 10;
    public final int LEFTSTICK_11 = 11;
    public final int FIRE = 1;
    public final int Shoot_On = 2;
    public final int RIGHTSTICK_3 = 3;
    public final int RIGHTSTICK_4 = 4;
    public final int RIGHTSTICK_5 = 5;
    public final int RIGHTSTICK_6 = 6;
    public final int RIGHTSTICK_7 = 7;
    public final int RIGHTSTICK_8 = 8;
    public final int RIGHTSTICK_9 = 9;
    public final int RIGHTSTICK_10 = 10;
    public final int RIGHTSTICK_11 = 11;
    public final int DS_1 = 1;
    public final int SUCK = 2;  
    public final int SPIT = 3;
    public final int SHOOTER_SWITCH = 6;
    public final int ARM_UP = 4;
    public final int ARM_DOWN = 5;
    public final int CLIMBMODE = 8;
    public final int CLIMB = 9;
    public final int SHOOTER_SWITCH_OFF = 7;
    public final int SETARMPOS = 10;
    public final int PREFIRE = 11;
    public final int DS_12 = 12;
   
    
    //relays
    public final int AIMLIGHT = 0;
    public final int RELAY_1 = 1;
    public final int RELAY_2 = 2;
    public final int RELAY_3 = 3;

    //ANALOGS
    public final int ARM_POT = 0;
    public final int BALL_FINDER = 1;
    public final int GYRO = 2;
    public final int ANALOG_3 = 3;
    public final int ANALOG_4 = 4;
    

    //declare inputs to be used in multiple objects here
    //	call as robotSystem.dig1.get();

    public RobotMap155() {
      // dig1 = new DigitalInput(DIG_IO_0);

    }
}
