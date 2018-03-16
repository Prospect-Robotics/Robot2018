package org.usfirst.frc2813.Robot2018.motor.talon;

/*
 * Talons support up to four configuration profiles for closed loop control (which includes all PID stuff: P Gain, I Gain, D Gain, Feed-Forward Gain, I Zone, and Ramp Rate)
 * You tell a particular PID (specified by index) which profile to use at any given point in time.
 * To simplify things we've standardized on using slot 0 for holding a position slot 1 for moving, though more complex examples will evolve later.   
 */
public enum TalonProfileSlot {
	// Generic names
	ProfileSlot0(0), 
	ProfileSlot1(1), 
	ProfileSlot2(2), 
	ProfileSlot3(3),
	HoldingPosition(ProfileSlot0), 
	Moving(ProfileSlot1);

	public final int profileSlotIndex;
	public final TalonProfileSlot canonicalProfileSlot;
	
	/*
	 * Get the slot index value
	 */
	public int getProfileSlotIndex() {
		return profileSlotIndex;
	}
	/*
	 * Return the canonical form of the enumeration
	 */
	public TalonProfileSlot getCanaonicalProfileSlot() {
		return canonicalProfileSlot;
	}
	
	private TalonProfileSlot(int profileSlotIndex) {
		this.profileSlotIndex     = profileSlotIndex;
		this.canonicalProfileSlot = this; 
	}
	
	private TalonProfileSlot(TalonProfileSlot profileSlotIndex) {
		this.canonicalProfileSlot = profileSlotIndex;
		this.profileSlotIndex = profileSlotIndex.getProfileSlotIndex();
	}
	
	public boolean equals(TalonProfileSlot other) {
		return other.canonicalProfileSlot == this.canonicalProfileSlot;
	}
}
