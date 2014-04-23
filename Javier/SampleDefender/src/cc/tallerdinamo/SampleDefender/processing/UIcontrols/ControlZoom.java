package cc.tallerdinamo.SampleDefender.processing.UIcontrols;

import org.puredata.core.PdBase;

import android.util.Log;
import processing.core.PApplet;
import processing.core.PVector;

public class ControlZoom extends ControlDesign {
//	private boolean visible;
	PApplet p5;
//	PVector pos;
	float xZoomIn;
	float percentagemZoom;
	private float percentagemFinal;
	float xZoomOut;
	private float distToqueCentro;
	float widthDial;
	float posBotaoZoom;
	float botaoDiam;
	private float velocidadeSom;
	private float closestZoom;
	
	public ControlZoom (PApplet _p5, float minPercentZoom) {
		//posiçao base do control
		super( new PVector (_p5.width*.85f, _p5.height*.6f) );//(p5.width*.5f, p5.height*.96f);)
		CONTROL_COUNTER++;
		p5 = _p5;
		xZoomIn = pos.x - p5.width*.05f; //posiçao esquerda do zoom
		percentagemZoom = .5f;
		percentagemFinal = percentagemZoom;
		xZoomOut = pos.x + p5.width*.05f; //posiçao dereita do zoom
		widthDial = (xZoomOut-xZoomIn); //largura do Dial
		posBotaoZoom = (widthDial * percentagemZoom); //nivel atual do zoom em pixels
		distToqueCentro = xZoomIn + posBotaoZoom;  //posiçao do nivel atual de zoom
		botaoDiam = 35;
		closestZoom = minPercentZoom;
		mappingZoomAndVel (posBotaoZoom);
		
		PApplet.println("minPercentZoom: "+ minPercentZoom );
	}
	
	@Override
	public void desenhaControl() {
		p5.pushStyle();
		p5.pushMatrix();
		p5.textAlign(PApplet.CENTER, PApplet.CENTER);
		p5.translate(0, pos.y);
		p5.strokeWeight(2);
		p5.strokeWeight(25);
		p5.stroke(50);
		p5.line(xZoomIn, 0, xZoomOut, 0);
		p5.strokeWeight(1);
		if (visible) {
			p5.fill(200);
		} else
			p5.fill(255,125);
		p5.ellipse( distToqueCentro, 0, botaoDiam, botaoDiam);
		
		p5.fill(255,0,0);
		p5.text(" <<········>> ", pos.x, 0); //<<< REWIND | PLAY >>>
		p5.popStyle();
		p5.popMatrix();
	}
	
	public PVector getPos() {
		return pos;
	}
	
	@Override
	public	void activeControl (float px, float py) {
		PVector posToque = new PVector (px, py);
		PVector posBotao = new PVector (distToqueCentro, pos.y);
		float distEvaluacao = posToque.dist(posBotao);
//		Log.d("distEvaluacao", Float.toString(distEvaluacao) );
		if (distEvaluacao < botaoDiam) {
			visible = true;
		} else {
			visible = false;
		}
//		Log.d("ControlZoom visible: ", Boolean.toString(visible) );
	}
	
	public void setVelocitySom() {
		PdBase.sendFloat("velLetura", velocidadeSom);
	}
	
	public float getPercentagemZoom () {
		return percentagemZoom;
	}
	
	@Override
	public void applyControl(float px, float py) {
		 PVector posToque = new PVector (px,  pos.y); //so comparamos a posiçāo X
		 PVector posInicial = new PVector (xZoomIn, pos.y);
		 float distEvaluacao;
		 if (px > xZoomIn) {
			distEvaluacao = posToque.dist(posInicial) ; //posiçao do toque desde a esquerda do Dial
		 } else {
			 distEvaluacao = 0;
		 }
		 mappingZoomAndVel (distEvaluacao);
	}
	
	//Setting da velocidade de letura. Segundo a posiçao do Zoom representado na percentagemZoom
	private void mappingZoomAndVel (float valToMap) {
		float thresholdStopI = widthDial*.05f; //limiar onde muda direçao do Play
		float thresholdStopII = widthDial*.65f; //limiar onde pega maior velocidade o play
		
		 if (valToMap < thresholdStopI) { //play para atras
//			 PApplet.println("valToMap: A: " + valToMap);
			 percentagemFinal = closestZoom;
			 velocidadeSom = PApplet.constrain( PApplet.map(valToMap, 0, thresholdStopI, .495f, .5f ), .497f, .5f);
		 } else {
			 if (valToMap < thresholdStopII) { //Play na frente
//				   PApplet.println("valToMap: B: " + valToMap + " thresholdStopII: "+thresholdStopII);
		 		   percentagemFinal = PApplet.constrain( PApplet.map(valToMap, thresholdStopI, 
		 				 				thresholdStopII, closestZoom, .01f ), closestZoom, .01f);
		 		   velocidadeSom = PApplet.constrain(PApplet.map(percentagemZoom, closestZoom, .01f, .5f, .505f), .5f, .505f);
		     } else if (valToMap > thresholdStopII) {
//				   PApplet.println("valToMap: C: " + valToMap);
				   percentagemFinal = PApplet.constrain( PApplet.map(valToMap, thresholdStopII, widthDial, .01f, 1 ), .01f, 1);
				   velocidadeSom = PApplet.constrain(PApplet.map(percentagemZoom, .01f, 1, .505f, .75f), .505f, .75f);
			 } 
		 }
		 
		 updatePercentagemZoomdeSom();
		 posBotaoZoom = valToMap; //(widthDial * percentagemZoom);
		 distToqueCentro = xZoomIn + posBotaoZoom;
		 distToqueCentro = PApplet.constrain(distToqueCentro, xZoomIn, xZoomOut);
	}
	
	public void updatePercentagemZoomdeSom () {
//		percentagemZoom = percentagemFinal;
		percentagemZoom = PApplet.lerp(percentagemZoom, percentagemFinal , .1f); //amortece o movimento do zoom
//		PApplet.println("percentagemZoom: " + percentagemZoom + " percentagemAmortecedor: "+ percentagemAmortecedor);
	}
	
	@Override
	public boolean showControl() {
		// TODO Auto-generated method stub
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

}
