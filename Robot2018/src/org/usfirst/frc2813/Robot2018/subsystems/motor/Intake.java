package org.usfirst.frc2813.Robot2018.subsystems.motor;

import org.usfirst.frc2813.Robot2018.RobotMap;
import org.usfirst.frc2813.units.Direction;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

/**
 * Intake Subsystem to move in two direction. This is positionless.
 * 
 * This subsytem can halt or move in a direction.
 * Speed can be set separately.
 */
public class Intake extends SubsystemDirectionSpeed {
	private WPI_VictorSPX motorController;
	public Intake(){
		super();
		motorController = RobotMap.intakeSpeedController;
		DEFAULT_SPEED = 0.7;
	}

    /**
    * We directly use controller speed 0-1
    */
    protected double speedToController(double speedParam) {
        return speedParam;
    }

    private void setController() {
		motorController.set(speed * direction.getMultiplier());
    }
  
	/**
	 * @Override
	 * We set speed and direction in setController
	 */
	protected void setControllerSpeed(int speedParam) {
		setController();
	}

	/**
	 * @Override
	 * We set speed and direction in setController
	 */
	protected void setControllerDirection(Direction directionParam) {
		setController();		
	}

	@Override
	protected void haltController() {
		motorController.set(0);
	}

	@Override
	protected void initDefaultCommand() {}
}
