package org.usfirst.frc2813.Robot2018.subsystems;

/*
 * NB: This is a JUnit Test but junit classes aren't available on the target, causing the build to fail
 * So I removed the JUnit classes.  The function can still be called manually from somewhere as needed.
 */
class ElevatorTest  {
	void test() {
		ElevatorAxisConfiguration.mathReport();
		Elevator.axisConfiguration.dumpDescription();
	}
}
