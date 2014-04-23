package cc.tallerdinamo.SampleDefender.processing.UIcontrols;

import android.util.Log;
import processing.core.PApplet;
import processing.core.PVector;

public class ControlParticulas extends ControlDesign {
	PApplet p5;
	PVector PtoDeControl;
	private float anguloToque;
	private float magnitude;
	
	public ControlParticulas (PApplet _p5) {
		super( new PVector (_p5.width*.1f, _p5.height*.6f) );
		p5 = _p5;
		CONTROL_COUNTER++;
		PtoDeControl = new PVector (pos.x, pos.y);
		anguloToque = 0;
		magnitude = 0;
	}

	@Override
	public	void activeControl(float px, float py) {
		PVector posToque = new PVector (px, py);
		float distEvaluacao = posToque.dist(PtoDeControl); //distancia desde a base ao ponto de contato
//		Log.d("distEvaluacao", Float.toString(distEvaluacao) );
		if (distEvaluacao < 50) {
			visible = true;
		} else {
			visible = false;
		}
//		Log.d("ControlZoom visible: ", Boolean.toString(visible) );
		
	}

	@Override
	public boolean showControl() {
		// TODO Auto-generated method stub
		return visible;
	}

	@Override
	public void desenhaControl() {
		// TODO Auto-generated method stub
		p5.pushStyle();
		p5.pushMatrix();
		p5.textAlign(PApplet.CENTER, PApplet.RIGHT);
		p5.strokeWeight(1);
		p5.fill(255,125);
		p5.stroke(0);
		p5.ellipse(pos.x, pos.y, 40, 40);
		p5.noFill();
		p5.strokeWeight(5);
		p5.stroke(255,0,0);
		p5.ellipse( PtoDeControl.x, PtoDeControl.y, 80, 80);
		
		p5.fill(255,0,0);
		p5.text(" 0 ", pos.x, pos.y); //<<< REWIND | PLAY >>>
		p5.popStyle();
		p5.popMatrix();
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
			magnitude = PVector.dist(vectorHorBase, novoToque);
		p5.popMatrix();
	}
	
	public float getAngulo() {
		return anguloToque;
	}
	
	public float getMagnitude() {
		return magnitude;
	}
}
