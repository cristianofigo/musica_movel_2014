package cc.tallerdinamo.SampleDefender.processing.UIcontrols;

import processing.core.PVector;

public abstract class ControlDesign {
	static int CONTROL_COUNTER;
	PVector pos;
	boolean visible;
	
	public ControlDesign(PVector _pos){
		pos = _pos;
	}
	
	abstract public void activeControl (float px, float py);
	abstract public void applyControl(float px, float py);
	abstract public boolean showControl();
	abstract public void desenhaControl();
}
