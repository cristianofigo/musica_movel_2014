package cc.tallerdinamo.SampleDefender.processing.UIcontrols;

import android.util.Log;
import processing.core.PApplet;
import processing.core.PVector;

public class ControlParticulas extends ControlDesign {
	PApplet p5;
	PVector PtoDeControl;
	private float anguloToque;
	private float magnitude;
	private float diamCentro;
	private float diamToque;
	private float diamToqueAnterior;
	
	public ControlParticulas (PApplet _p5) {
		super( new PVector (_p5.width*.1f, _p5.height*.6f) );
		p5 = _p5;
		CONTROL_COUNTER++;
		PtoDeControl = new PVector (pos.x, pos.y);
		anguloToque = 0;
		magnitude = 0;
		diamCentro = _p5.height*.05f;
		diamToque = _p5.height*.05f;
	}

	public void setDiamToqueIni(){
		diamToque = p5.height*.05f;
	}
	
	@Override
	public	void activeControl(float px, float py) {
		PVector posToque = new PVector (px, py);
		float distEvaluacao = posToque.dist(PtoDeControl); //distancia desde a base ao ponto de contato
//		Log.d("distEvaluacao", Float.toString(distEvaluacao) );
		if (distEvaluacao < 50) {
			visible = true;
			diamToque++;
		} else {
			visible = false;
		}
			
//		Log.d("ControlZoom visible: ", Boolean.toString(visible) );
	}

	public void ShootReset(){
		setDiamToqueIni();
		PtoDeControl = new PVector (pos.x, pos.y);
		applyControl(pos.x, pos.y);
	}
	@Override
	public boolean showControl() {
		// TODO Auto-generated method stub
		return visible;
	}

	@Override
	public void desenhaControl() {
		// TODO Auto-generated method stub
		if (diamToqueAnterior == diamToque ) {
			ShootReset();
		}
		
		p5.pushStyle();
		p5.pushMatrix();
		p5.textAlign(PApplet.CENTER, PApplet.CENTER);
		p5.strokeWeight(1);
		p5.fill(255,125);
		p5.stroke(0);
		p5.ellipse(pos.x, pos.y, diamCentro, diamCentro);
		p5.noFill();
		p5.strokeWeight(2);
		p5.stroke(255);
		p5.ellipse( PtoDeControl.x, PtoDeControl.y, diamToque, diamToque);
		p5.text(" 0 ", pos.x, pos.y);
		p5.translate(pos.x, pos.y);
		p5.rotate(PApplet.PI*.5f);
		p5.line( diamToque * .5f * PApplet.cos(anguloToque), 
				 diamToque * .5f * PApplet.sin(anguloToque), 
				(magnitude - ( diamToque * .5f ) ) * PApplet.cos(anguloToque),
				(magnitude - ( diamToque * .5f ) ) * PApplet.sin(anguloToque));
		p5.fill(255,0,0);
		p5.popStyle();
		p5.popMatrix();
		
		diamToqueAnterior = diamToque;
	}

	@Override
	public void applyControl(float px, float py) {
		// Atualiza o novo ponto de control
		PtoDeControl = new PVector (px, py);
		
		p5.pushMatrix();
			p5.translate(pos.x, pos.y);
			PVector novoToque = new PVector (px-pos.x, py-pos.y);
			PVector vectorHorBase = new PVector (0, 1);
			anguloToque = PVector.angleBetween(vectorHorBase, novoToque);
//			PApplet.println("ANGULO: " + anguloToque + "anguloToqueGrados: "+ PApplet.degrees(anguloToque) );
			PVector dir = PVector.sub(vectorHorBase, novoToque);
			magnitude = dir.mag();
			dir.normalize();
			if (dir.x<0)
				anguloToque *= -1; //aqui o angulo fica entre 0 e 360°
			
//			PApplet.println("magnitude: " + magnitude + " dir X: "+dir.x );
			
		p5.popMatrix();
	}
	
	public float getAngulo() {
		return anguloToque;
	}
	
	public float getMagnitude() {
		return magnitude;
	}

	
}
