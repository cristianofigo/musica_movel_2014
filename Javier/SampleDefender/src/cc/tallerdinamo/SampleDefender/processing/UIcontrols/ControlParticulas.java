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
	private float molaShoot;
	private PVector projetilPos;
	private PVector projetilGlobalPos;
	private float anguloDeGiroDesenho;
	
	public ControlParticulas (PApplet _p5) {
		super( new PVector (_p5.width*.25f, _p5.height*.6f) );
		p5 = _p5;
		CONTROL_COUNTER++;
		PtoDeControl = new PVector (pos.x, pos.y); //pos é um PVector da clase pai
		anguloToque = 0;
		magnitude = 0;
		diamCentro = _p5.height*.05f;
		anguloDeGiroDesenho = PApplet.PI*.5f;
		setMolaShotToIni(); //distança minima da força pra atirar
	}

	private void setMolaShotToIni(){
		molaShoot = p5.height*.05f;
		projetilPos = new PVector (pos.x, pos.y);
		projetilGlobalPos = new PVector (pos.x, pos.y);
	}
	
	@Override
	public	void activeControl(float px, float py) {
		PVector posToque = new PVector (px, py);
		float distEvaluacao = posToque.dist(PtoDeControl); //distancia desde a base ao ponto de contato
//		Log.d("distEvaluacao", Float.toString(distEvaluacao) );
		if (distEvaluacao < 50) {
			visible = true;
			molaShoot++;
		} else {
			visible = false;
		}
			
//		Log.d("ControlZoom visible: ", Boolean.toString(visible) );
	}

	public void ShootReset(){
		
		
		//botamos as opçōes padrōes do shooter
		setMolaShotToIni();
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
		
		p5.pushStyle();
		p5.pushMatrix();
		p5.textAlign(PApplet.CENTER, PApplet.CENTER);
		
		p5.translate(pos.x, pos.y);
			p5.rotate(anguloDeGiroDesenho);
			float anguloDeTangente = PApplet.acos( (diamCentro * .5f ) / magnitude);
			p5.stroke(0);
			p5.fill(255);
			p5.strokeWeight(1);
			p5.ellipse(0,0, diamCentro, diamCentro); //desenho do centro
			if (visible) {
				p5.strokeWeight(2);
				p5.noStroke();
				p5.beginShape();
				p5.vertex( (diamCentro * .5f) * PApplet.cos(anguloDeTangente + anguloToque), 
						(diamCentro * .5f) * PApplet.sin(anguloDeTangente + anguloToque));
				p5.vertex( molaShoot * PApplet.cos(anguloToque),
						   molaShoot * PApplet.sin(anguloToque) );
				p5.vertex((diamCentro * .5f) * PApplet.cos(anguloToque - anguloDeTangente), 
						 (diamCentro * .5f) * PApplet.sin(anguloToque - anguloDeTangente) );
				p5.endShape(PApplet.CLOSE);
				
				p5.stroke(0);
				p5.arc(0,0, diamCentro, diamCentro, anguloDeTangente + anguloToque, anguloToque - anguloDeTangente);
				p5.line (
						(diamCentro * .5f) * PApplet.cos(anguloDeTangente + anguloToque), 
						(diamCentro * .5f) * PApplet.sin(anguloDeTangente + anguloToque),
						molaShoot * PApplet.cos(anguloToque),
						molaShoot * PApplet.sin(anguloToque) );
				p5.line (
						(diamCentro * .5f) * PApplet.cos(anguloToque - anguloDeTangente), 
						(diamCentro * .5f) * PApplet.sin(anguloToque - anguloDeTangente),
						molaShoot * PApplet.cos(anguloToque),
						molaShoot * PApplet.sin(anguloToque) );
				
				p5.fill(255,0,0);
				p5.noStroke();
				float magnitudePontoAbre = PVector.dist(new PVector (0,0), new PVector ( molaShoot * PApplet.cos(anguloToque),
						molaShoot  * PApplet.sin(anguloToque)) );
				//desenho do projetil que sera atirado
				projetilPos = new PVector (( (magnitudePontoAbre) - ( (diamCentro*.5f ) / PApplet.sin (anguloDeTangente) ) ) * PApplet.cos(anguloToque),
						( (magnitudePontoAbre) - ( (diamCentro*.5f ) / PApplet.sin (anguloDeTangente) ) ) * PApplet.sin(anguloToque));
				p5.ellipse(projetilPos.x, projetilPos.y, diamCentro/2, diamCentro/2);
				
				projetilGlobalPos = new PVector (
						pos.x + ( (magnitudePontoAbre) - ( (diamCentro*.5f ) / PApplet.sin (anguloDeTangente) ) ) * PApplet.cos(anguloToque + PApplet.PI*.5f),
						pos.y + ( (magnitudePontoAbre) - ( (diamCentro*.5f ) / PApplet.sin (anguloDeTangente) ) ) * PApplet.sin(anguloToque + PApplet.PI*.5f));
			} else {
				
			}
			p5.stroke(0);	
			p5.line( molaShoot * PApplet.cos(anguloToque), 
					 molaShoot * PApplet.sin(anguloToque), 
					(magnitude - ( diamCentro * .5f ) ) * PApplet.cos(anguloToque),
					(magnitude - ( diamCentro * .5f ) ) * PApplet.sin(anguloToque));
		p5.popStyle();
		p5.popMatrix();
		p5.fill(0);
		p5.noStroke();
		p5.ellipse( PtoDeControl.x, PtoDeControl.y, p5.height*.025f, p5.height*.025f);
	}

	public boolean reachMaxStretching() {
		//TODO implementar cuando el estiramiento toca el touch
		
		return false;
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
	public float getGlobalAngulo(){
		return anguloDeGiroDesenho+anguloToque;
	}
	public PVector getProjectilPos() {
//		projetilPos.add(pos);
	//	projetilPos = new PVector (pos.x + projetilPos.x, pos.y + projetilPos.y);
		return projetilGlobalPos;
	}
	

	
}
