package org.usfirst.frc2813.Robot2018.subsystems.user;

import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.buttons.Button;

/**
 *
 */
public class RoboRIOUserButton extends Button {

    public boolean get() {
        return RobotController.getUserButton();
    }
}
