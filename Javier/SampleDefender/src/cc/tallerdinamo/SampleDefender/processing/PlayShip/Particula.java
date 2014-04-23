package cc.tallerdinamo.SampleDefender.processing.PlayShip;

import processing.core.PApplet;
import processing.core.PVector;

public class Particula {
	float posX, posY;
	float vel;
	float acc;
	PApplet p5;
	boolean vive;
	PVector[] posEspelho;
	float angGuia;
	float dist;
	
	Particula(PApplet _p5) {
		p5 = _p5;
		posX = 0;
		posY = 0;
		vive = true; 
		posEspelho = new PVector[2];
		angGuia = 0;
		dist = 0;
	}
	//a posiçāo base sera a posiçāo do PlayShip
	public void daMovimento(float _posX, float _posY, float _ang, float _dist) {
		posX = _posX;
		posY = _posY;
		angGuia = _ang;
		dist = _dist;
	}
	
	public void upDate(){
		
		if (!vive) {
			//TODO: codigo para matar a particula
		} else {
			desenha();
		}
		
	}
	
	public void desenha() {
		p5.pushMatrix();
		p5.pushStyle();
		p5.translate(posX, posY);
		p5.rotate(-PApplet.PI/2);
		p5.fill(0);
		p5.stroke(255,255,0);
		p5.line((dist)*PApplet.cos(angGuia), (dist)*PApplet.sin(angGuia),
				(-dist)*PApplet.cos(angGuia), (dist)*PApplet.sin(angGuia));
		p5.ellipse(dist*PApplet.cos(angGuia), dist*PApplet.sin(angGuia), 20,20);
		p5.ellipse(-dist*PApplet.cos(angGuia), dist*PApplet.sin(angGuia), 20,20);
		p5.popStyle();
		p5.popMatrix();
	}
	
	public void destruir(){
		vive = false;
	}
	
}
