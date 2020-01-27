/*-----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                              */
/* Open Source Software - may be modified and shared by FRC teams. The code    */
/* must be accompanied by the FIRST BSD license file in the root directory of  */
/* the project.                                                                */
/*                                                                             */
/* To keep from calling SmartDashboard every 20 msec., we will create a state  */
/* controller for each system check.  After each state change, call            */
/* SmartDashboard to update states.                                            */
/*                                                                             */
/* This class checks the pressure status.  If pressure voltages is above the   */
/* target value, then pressure_ok is set to true.  This can be used by the     */
/* robot to inhibit / enable pneumatic actuation. This class also times the    */
/* match.  The clock is reset when autonomous is initialized.  Once the clock  */
/* is above 90 seconds, then the system will signal driver that it is time to  */
/* climb.                                                                      */
/*-----------------------------------------------------------------------------*/

package frc.robot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.AnalogInput;

public class SystemStatus 
{
  enum SystemStates 
  {
    wait_for_activation,
    initialize, 
    wait_for_true, 
    wait_for_false, 
  };

  private SystemStates pressure_state = SystemStates.wait_for_activation;
  private SystemStates game_state = SystemStates.wait_for_activation;
  private boolean climb_now;
  private boolean pressure_ok;
  final double TargetCountDown = 90.0;   // signal driver when countdown is 40 seconds.
  final double PressureMax = 0.252;      // change when ready to deploy
  private double PressureVolts;
  private int PressureDebounce;
  AnalogInput TankPressure = new AnalogInput(0);
  private Timer gclock = new Timer();

  public SystemStatus()
  {
    System.out.println( "SystemStatus Constructor");
  }

  public void StartPressureCheck()
  {
    pressure_state = SystemStates.initialize;
  }

  public void StartGameClock()
  {
    game_state = SystemStates.initialize;
  }

  public boolean Tanks_Pressurized()
  {
    return pressure_ok;
  }

  public boolean Time_To_Climb()
  {
     return climb_now;
  }

  public void Check_System_Status()
  {
    PressureVolts = TankPressure.getVoltage();
    switch (pressure_state)
    {
      case wait_for_activation:
        break;

      case initialize:        
        pressure_ok = false;
        SmartDashboard.putBoolean("PressureOK", pressure_ok);
        pressure_state = SystemStates.wait_for_true;
        PressureDebounce = 0;
        break;
    
      case wait_for_true:
        if( PressureVolts >= PressureMax )
        {
          if( ++PressureDebounce > 3)
          {
            pressure_ok = true;
            SmartDashboard.putBoolean("Pressure", pressure_ok);
            pressure_state = SystemStates.wait_for_false;
          } 
        }  
        else     
          PressureDebounce = 0;
        break;

      case wait_for_false:
        if( PressureVolts < PressureMax)
        {
          if( ++PressureDebounce > 3 )
          {
            pressure_ok = false;
            SmartDashboard.putBoolean("Pressure", pressure_ok);
            pressure_state = SystemStates.wait_for_true;
          }  
        }
        else 
          PressureDebounce = 0;   
        break;

      default:
        pressure_state = SystemStates.wait_for_activation;
        break;
    }

    switch (game_state)
    {
      case wait_for_activation:
        break;

      case initialize:
        gclock.reset();     
        gclock.start(); 
        climb_now = false;
        SmartDashboard.putBoolean("Climb", climb_now);
        game_state = SystemStates.wait_for_true;
        break;
    
      case wait_for_true:
        if( gclock.get() > TargetCountDown )
        {
          climb_now = true;
          SmartDashboard.putBoolean("Climb", climb_now);
          game_state = SystemStates.wait_for_activation;
        }
        break;

      default:
        climb_now = false;
        SmartDashboard.putBoolean("Climb", climb_now);
        game_state = SystemStates.wait_for_activation;
        break;
    }
  }
}
