package cc.tallerdinamo.SampleDefender.processing.UIcontrols;

import processing.core.PApplet;

public class ControlShip extends ControlDesign {
	private boolean visible;
	PApplet p5;		//Para importar a clase de Processing
	ControlShipShot shipShot;
	ConstrolShipVelocity shipVel;
	
	ControlShip(PApplet _p5){
		p5 = _p5;
		shipShot =  new ControlShipShot ();
		shipVel = new ConstrolShipVelocity();
		setVisible(true);
	}
	
	public void desenhaControles() {
		
	}
	
	@Override
	boolean showControl() {
		// TODO Auto-generated method stub
		return visible;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
		
		
}
