/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Encoder;


enum Mast_States
{
  wait_for_cmd, 
  at_home,
  down_to_home,
  up_to_Level1,
  down_to_Level1,
  at_Level1,
  up_to_Level2,
  down_to_Level2,
  at_Level2,
  up_to_Level3,
  at_Level3,
};

public class MastControls 
{
  Joystick jstick;
  Mast_States mast_state;
  Encoder enc = new Encoder(0, 1, true, Encoder.EncodingType.k1X);
  final int Home_TargetCount = 0;
  final int Level1_TargetCount = 2000;
  final int Level2_TargetCount = 4000;
  final int Level3_TargetCount = 6000;


  public MastControls( Joystick stick )
  {
    System.out.println( "MastControls constructor " );
    jstick = stick;
    mast_state = Mast_States.at_home;
    SmartDashboard.putString("Mast", "At Home");
    enc.setMaxPeriod(.1);
    enc.setMinRate(10);
    enc.setDistancePerPulse(1);
    enc.setReverseDirection(true);
    enc.setSamplesToAverage(7);
    enc.reset();
  }

  public void Mast_Controls()
  {
    int Current_Mast_Position;

    Current_Mast_Position = enc.get();

    switch (mast_state)
    {
      case wait_for_cmd:
        if (jstick.getRawButton(3))
        {
          mast_state = Mast_States.up_to_Level1;
          SmartDashboard.putString("Mast", "At Home");
        }
        break;

      case at_home:
        if (jstick.getRawButton(3))
        {
          mast_state = Mast_States.up_to_Level1;
          SmartDashboard.putString("Mast", "Up to Level1");
        }
        else 
        {
          // Manual Control of Mast
        }
        break;

      case down_to_home:
        if (Current_Mast_Position <= Home_TargetCount) 
        {
          mast_state = Mast_States.at_home;
          SmartDashboard.putString("Mast", "At Home");
          // Stop Mast
        }
        else if (Current_Mast_Position > Home_TargetCount + 200)
        {
          // Move Mast Down faster speed
        } 
        else 
        {
          // Move Mast Down slower speed
        }
        break;

      case up_to_Level1:
        if (Current_Mast_Position >= Level1_TargetCount) 
        {
          mast_state = Mast_States.at_Level1;
          SmartDashboard.putString("Mast", "At Level1");
          // Stop Mast
        }
        else if (Current_Mast_Position > Level1_TargetCount - 200)
        {
          // Move Mast Up slower speed
        } 
        else 
        {
          // Move Mast Up faster speed
        }
        break;

      case down_to_Level1:
        if (Current_Mast_Position <= Level1_TargetCount) 
        {
          mast_state = Mast_States.at_Level1;
          SmartDashboard.putString("Mast", "At Level1");
          // Stop Mast
        }
        else if (Current_Mast_Position > Level1_TargetCount + 200)
        {
          // Move Mast Down faster speed
        } 
        else 
        {
          // Move Mast Down slower speed
        }
        break;

      case at_Level1:
        if (jstick.getRawButton(3))
        {
          mast_state = Mast_States.up_to_Level2;
          SmartDashboard.putString("Mast", "Up to Level2");
        }
        else if (jstick.getRawButton(2))
        {
          mast_state = Mast_States.down_to_home;
          SmartDashboard.putString("Mast", "Down to Home");
        }
        else 
        {
          // Manual Control of Mast
        }
        break;

      case up_to_Level2:
        if (Current_Mast_Position > Level2_TargetCount) 
        {
          mast_state = Mast_States.at_Level2;
          SmartDashboard.putString("Mast", "At Level2");
          // Stop Mast
        }
        else if (Current_Mast_Position > Level2_TargetCount - 200)
        {
          // Move Mast Up slower speed
        } 
        else 
        {
          // Move Mast Up faster speed
        }
        break;

      case down_to_Level2:
        if (Current_Mast_Position <= Level2_TargetCount) 
        {
          mast_state = Mast_States.at_Level2;
          SmartDashboard.putString("Mast", "At Level2");
          // Stop Mast
        }
        else if (Current_Mast_Position > Level2_TargetCount + 200)
        {
          // Move Mast Down faster speed
        } 
        else 
        {
          // Move Mast Down slower speed
        }
        break;

      case at_Level2:
        if (jstick.getRawButton(3))
        {
          mast_state = Mast_States.up_to_Level3;
          SmartDashboard.putString("Mast", "Up to Level3");
        }
        else if (jstick.getRawButton(2))
        {
          mast_state = Mast_States.down_to_Level1;
          SmartDashboard.putString("Mast", "Down to Level1");
        }
        else 
        {
          // Manual Control of Mast
        }
        break;

      case up_to_Level3:
        if (Current_Mast_Position >= Level3_TargetCount) 
        {
          mast_state = Mast_States.at_Level3;
          SmartDashboard.putString("Mast", "At Level3");
          // Stop Mast
        }
        else if (Current_Mast_Position > Level3_TargetCount - 200)
        {
          // Move Mast Up slower speed
        } 
        else 
        {
          // Move Mast Up faster speed
        }
        break;
      
      case at_Level3:
        if (jstick.getRawButton(2))
        {
          mast_state = Mast_States.down_to_Level2;
          SmartDashboard.putString("Mast", "Down to Level2");
        }
        else 
        {
          // Manual Control of Mast
        }
        break;
      
      default:
        mast_state = Mast_States.wait_for_cmd;
        // Stop Mast
        break;

    }
  }
}
