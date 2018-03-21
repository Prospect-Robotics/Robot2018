package org.usfirst.frc2813.Robot2018.motor;

public class SimulatedMotorControllerUnitConversionAdapter extends MotorControllerUnitConversionAdapter	implements ISimulatedMotorController {
	private final ISimulatedMotorController simulatedMotorController;
	
	public SimulatedMotorControllerUnitConversionAdapter(IMotorConfiguration configuration, ISimulatedMotorController simulatedController) {
		super(configuration, simulatedController);
		this.simulatedMotorController = simulatedController;
	}

}
