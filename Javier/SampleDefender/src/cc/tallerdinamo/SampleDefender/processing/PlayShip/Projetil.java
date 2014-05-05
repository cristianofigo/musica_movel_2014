package cc.tallerdinamo.SampleDefender.processing.PlayShip;

import processing.core.PApplet;
import processing.core.PVector;

public class Projetil {
	private PVector startPoint;
	private PVector targetPoint;
	private PVector aceleracao;
	private float masaDoProjetil;
	
	PApplet p5;
	private float timeLive;
	private boolean Alive;
	PVector vel;
	private float projDim;
	private int colorPro;
	
	Projetil (PApplet _p5, float alvoX, float alvoY, float anguloTiro, float magnitudeDoTiro) {
		p5= _p5;
		startPoint = new PVector (alvoX, alvoY);
		targetPoint = new PVector (p5.width, alvoY);
		aceleracao = new PVector (0,0);
		float MAgToVel = PApplet.map(magnitudeDoTiro, 0, p5.height*.5f, .2f, 30);
		vel = new PVector (MAgToVel * PApplet.cos(anguloTiro), MAgToVel * PApplet.sin(anguloTiro));
		timeLive = 150;
		projDim = p5.height*.02f;
		Alive = true;
		colorPro = p5.color(magnitudeDoTiro, 0,0);
		masaDoProjetil = p5.random(.4f,5 );
	}
	
	public void update(){
		vel.add(aceleracao); //muda vel segundo a aceleraçāo
		startPoint.add(vel); //muda poiçāo de acordo a velocidade
		updateLive();
		
		aceleracao.mult(0);
	}
	void applyForce (PVector force) {
	    PVector f = PVector.div(force, masaDoProjetil);
	    aceleracao.add (f); //add asi acumula aceleraciones
	  }
	private void updateLive() {
		timeLive--;
		if (timeLive < 0) {
			Alive = false;
		} else {
			Alive = true;
		}
	}
	public boolean isAlive(){
		return Alive;
	}
	public void desenha(){
		p5.pushMatrix();
		p5.pushStyle();
		p5.fill(colorPro);
		p5.ellipse(startPoint.x, startPoint.y, projDim, projDim);
		p5.popStyle();
		p5.popMatrix();
		
	}
	public float getMasaDoProjetil(){
		return masaDoProjetil;
	}
	public  PVector getStartPoint() {
		return startPoint;
	}

	public  void setStartPoint(PVector startPoint) {
		startPoint = startPoint;
	}
}
