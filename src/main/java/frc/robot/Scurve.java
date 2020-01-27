/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Add your docs here.
 */
public class Scurve 
{
  static double[] acc_dec_array = 
  {
    0.00240, 0.00640, 0.01200, 0.01920, 0.02800,          // index 0 - 4
    0.03840, 0.05040, 0.06400, 0.07920, 0.09600,          // index 5 - 9
    0.11440, 0.13440, 0.15600, 0.17920, 0.20400,          // index 10 - 14
    0.23040, 0.25840, 0.28800, 0.31920, 0.35200,          // index 15 - 19
    0.38640, 0.42240, 0.46000, 0.49920, 0.54000,          // index 20 - 24
    0.57760, 0.61360, 0.64800, 0.68080, 0.71200,          // index 25 - 29
    0.74160, 0.76960, 0.79600, 0.82080, 0.84400,          // index 30 - 34
    0.86560, 0.88560, 0.90400, 0.92080, 0.93600,          // index 35 - 39
    0.94960, 0.96160, 0.97200, 0.98080, 0.98800,          // index 40 - 44
    0.99360, 0.99760, 1.00000, 1.00000, 1.00000,          // index 45 - 49
    0.99760, 0.99360, 0.98800, 0.98080, 0.97200,          // index 50 - 54
    0.96160, 0.94960, 0.93600, 0.92080, 0.90400,          // index 55 - 59
    0.88560, 0.86560, 0.84400, 0.82080, 0.79600,          // index 60 - 64
    0.76960, 0.74160, 0.71200, 0.68080, 0.64800,          // index 65 - 69
    0.61360, 0.57760, 0.54000, 0.50080, 0.46000,          // index 70 - 74
    0.42240, 0.38640, 0.35200, 0.31920, 0.28800,          // index 75 - 79
    0.25840, 0.23040, 0.20400, 0.17920, 0.15600,          // index 80 - 84
    0.13440, 0.11440, 0.09600, 0.07920, 0.06400,          // index 85 - 89
    0.05040, 0.03840, 0.02800, 0.01920, 0.01200,          // index 90 - 94
    0.00640, 0.00240, 0.00000, 0.00000, 0.00000           // index 95 - 99
  };

  enum Controlled_Move 
  {
    wait_for_cmd, 
    accel_to_speed, 
    move_distance, 
    decel_to_stop
  };

  private Controlled_Move scurve_move_state = Controlled_Move.wait_for_cmd;
  private int acc_dec_index = 0;
  TalonSRX RightMotor;
  VictorSPX LeftMotor;
  public double forward_speed;
  public double turn_speed;
  public double max_speed;
  public boolean travel_forward;
  public int move_time_target_count;
  public int scurve_time_counter;

 
  public Scurve( TalonSRX rmotor, VictorSPX lmotor )
  {
     RightMotor = rmotor;
     LeftMotor = lmotor;
  }


  public void Start_Move( boolean direction, double target_speed, int move_time )
  {
    acc_dec_index = 0;
    travel_forward = direction;                // set to true for FWD, false for REV
    max_speed = target_speed;
    move_time_target_count = move_time;        // how many seconds for move after accel and decel
    scurve_move_state = Controlled_Move.accel_to_speed;  
    System.out.println( "accel_to_speed " );
  }


  public boolean busy()
  {
    if( scurve_move_state == Controlled_Move.wait_for_cmd )
      return false;
    else
      return true;
  }

  /*
    scurve_move - moves forward or backward based on direction set.  
                              
  */
  public void scurve_move( )
  {
    switch ( scurve_move_state )
    {
      case wait_for_cmd:
        break;

      case accel_to_speed:  // Accelerate to target speed
        turn_speed = 0;
        forward_speed = acc_dec_array[acc_dec_index];
        
        if( travel_forward == true )
            forward_speed = -forward_speed;
        
        forward_speed = forward_speed * max_speed;
        SmartDashboard.putNumber("Speed Profile", forward_speed );  
        RightMotor.set(ControlMode.PercentOutput, forward_speed,  DemandType.ArbitraryFeedForward, +turn_speed);
        LeftMotor.set(ControlMode.PercentOutput, forward_speed, DemandType.ArbitraryFeedForward, -turn_speed);

        if( ++acc_dec_index > 48 )
        {
          scurve_move_state = Controlled_Move.move_distance;
          scurve_time_counter = 0;
          System.out.println( "move_distance" );
        }    
        
        break;

      case move_distance:  // Move at target speed for duration 
        turn_speed = 0;
        forward_speed = acc_dec_array[acc_dec_index];
        
        if( travel_forward == true )
          forward_speed = -forward_speed;
    
        forward_speed = forward_speed * max_speed;
        SmartDashboard.putNumber("Speed Profile", forward_speed );  
        RightMotor.set(ControlMode.PercentOutput, forward_speed,  DemandType.ArbitraryFeedForward, +turn_speed);
        LeftMotor.set(ControlMode.PercentOutput, forward_speed, DemandType.ArbitraryFeedForward, -turn_speed);
        if( scurve_time_counter++ > move_time_target_count )
        {
          scurve_move_state = Controlled_Move.decel_to_stop;
          acc_dec_index++;
          System.out.println( "decel_to_stop" );          
        }
       
        break;

      case decel_to_stop:  // Decelerate to stop
        turn_speed = 0;
        forward_speed = acc_dec_array[acc_dec_index];
        
        if( travel_forward == true )
          forward_speed = -forward_speed;
      
        forward_speed = forward_speed * max_speed;
        SmartDashboard.putNumber("Speed Profile", forward_speed );  
        RightMotor.set(ControlMode.PercentOutput, forward_speed,  DemandType.ArbitraryFeedForward, +turn_speed);
        LeftMotor.set(ControlMode.PercentOutput, forward_speed, DemandType.ArbitraryFeedForward, -turn_speed);

        if( ++acc_dec_index > 99 )
        {
          scurve_move_state = Controlled_Move.wait_for_cmd;
          scurve_time_counter = 0;
          RightMotor.set(ControlMode.PercentOutput, 0.0,  DemandType.ArbitraryFeedForward, 0.0);
          LeftMotor.set(ControlMode.PercentOutput, 0.0, DemandType.ArbitraryFeedForward, 0.0);
          System.out.println( "move complete " );
        }  
          
        break;

      default:
        scurve_move_state = Controlled_Move.wait_for_cmd;
        RightMotor.set(ControlMode.PercentOutput, 0.0,  DemandType.ArbitraryFeedForward, 0.0);
        LeftMotor.set(ControlMode.PercentOutput, 0.0, DemandType.ArbitraryFeedForward, 0.0);
        System.out.println( "lost, so reset state 0" );
        break;
    }
    //System.out.println( "speed " + forward_speed );
  
  }
    
}
