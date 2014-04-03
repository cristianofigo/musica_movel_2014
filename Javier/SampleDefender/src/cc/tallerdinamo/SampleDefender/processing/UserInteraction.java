package cc.tallerdinamo.SampleDefender.processing;

import org.puredata.core.PdBase;

import processing.core.PApplet;

public class UserInteraction {
	PApplet p5;
	
	UserInteraction(PApplet _p5){
		p5 = _p5;
	}
	
	public void setLetureVel(int xVal, int limiarSel){
		if (p5.mousePressed){
			
			//PdBase.sendBang("playSample")+;
/*			if (PApplet.abs (xVal -p5.mouseY) < 100 ) {
				float vel = PApplet.map(p5.mouseX, 0, p5.width, 0, 1);
				vel = PApplet.constrain(vel, 0, 1);
				PdBase.sendFloat("velLetura", vel);
			}	*/
		} else {
//			PdBase.sendFloat("velLetura", 0.505f);
		}
		
	}
	
	public void onTap(float x, float y) 	{
		
	}
	public void onSecondTouch(float x, float y)  //(14)
	{
		
	}
		
}