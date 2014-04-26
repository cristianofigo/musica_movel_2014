package cc.tallerdinamo.SampleDefender.processing.buffergraphic;

import processing.core.PApplet;
import processing.core.PVector;

public class Portal {
	private PVector pos;
	PApplet p5;
	private static float bordeSup;
	private static float bordeInf;
	
	public Portal(PApplet _p5) {
		p5 = _p5;
	}
	
	public static void setBordes(float _bordeSup, float _bordeInf) {
		bordeSup = _bordeSup;
		bordeInf = _bordeInf;
	}
	public void setPos(float px, float py) {
		pos = new PVector(px, py);
	}
	
	public void update(){
		
	}
	
	public void desenhar() {
		p5.pushMatrix();
		p5.pushStyle();
		
		p5.stroke(255);
		p5.strokeWeight(8);
		p5.line(pos.x, bordeSup, pos.x, bordeInf);
		p5.strokeWeight(1);
		
		p5.popStyle();
		p5.popMatrix();
	}
	
}
