package cc.tallerdinamo.SampleDefender.processing.UIcontrols;

import org.puredata.core.PdBase;

import android.util.Log;
import processing.core.PApplet;
import processing.core.PVector;

public class ControlZoom extends ControlDesign {
	private boolean visible;
	PApplet p5;
	PVector pos;
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
		p5 = _p5;
		pos = new PVector (p5.width*.5f, p5.height*.96f);
		xZoomIn = p5.width*.4f; //posiçao esquerda do zoom
		percentagemZoom = .5f;
		percentagemFinal = percentagemZoom;
		xZoomOut = p5.width*.6f; //posiçao dereita do zoom
		widthDial = (xZoomOut-xZoomIn); //largura do Dial
		posBotaoZoom = (widthDial * percentagemZoom); //nivel atual do zoom em pixels
		distToqueCentro = xZoomIn + posBotaoZoom;  //posiçao do nivel atual de zoom
		botaoDiam = 30;
		closestZoom = minPercentZoom;
		mappingZoomAndVel (posBotaoZoom);
		
		PApplet.println("minPercentZoom: "+ minPercentZoom );
	}
	
	public void desenhaZoom () {
		
		p5.pushStyle();
		p5.pushMatrix();
		p5.textAlign(PApplet.CENTER, PApplet.CENTER);
		p5.translate(0, pos.y);
		p5.strokeWeight(2);
		p5.stroke(255);
		p5.line(xZoomIn, 0, xZoomOut, 0);
		
		if (visible) {
			p5.fill(0);
		} else
			p5.fill(0,125);
		p5.ellipse( distToqueCentro, 0, botaoDiam, botaoDiam);
		
		p5.fill(255);
		p5.text(" <<< ZOOM IN | ZOOM OUT >>> ", p5.width*.5f, (botaoDiam*.3f));
		p5.popStyle();
		p5.popMatrix();
	}
	
	public PVector getPos() {
		return pos;
	}
	
	public void activeZoom (float px, float py) {
		PVector posToque = new PVector (px, pos.y);
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
	
	public void settingNivelZoom(float px, float py) {
		 PVector posToque = new PVector (px,  pos.y); //so comparamos a posiçāo X
		 PVector posInicial = new PVector (xZoomIn, pos.y);
		 float distEvaluacao = posToque.dist(posInicial); //posiçao do toque desde a esquena esquerda do Dial
		 mappingZoomAndVel (distEvaluacao);
	}
	
	//Setting da velocidade de letura. Segundo a posiçao do Zoom representado na percentagemZoom
	private void mappingZoomAndVel (float valToMap) {
		float thresholdStop = widthDial*.2f; //limiar onde muda direçao do Play
//		PApplet.println("valToMap: "+valToMap);

		 if (valToMap < thresholdStop) {
			 percentagemFinal = PApplet.constrain( PApplet.map(valToMap, 0, thresholdStop, .25f, closestZoom ), closestZoom, .25f);
			 velocidadeSom = PApplet.map(percentagemZoom, .25f, closestZoom, .45f, .5f);
		 } else if (valToMap > thresholdStop){
			 percentagemFinal = PApplet.constrain( PApplet.map(valToMap, thresholdStop, widthDial, closestZoom, 1 ), closestZoom, 1);
			 velocidadeSom = PApplet.map(percentagemZoom, closestZoom, 1, .5f, .75f);
		 } 
		 if ( PApplet.abs(valToMap - thresholdStop) < widthDial*.05 ) {
			 velocidadeSom = .5f;
		 }
//		 else if (valToMap > widthDial*.2f) {
//			 percentagemZoom = PApplet.constrain( PApplet.map(valToMap, widthDial*.35f, widthDial, .01f, 1 ), .01f, 1);
//			 velocidadeSom = PApplet.map(percentagemZoom, .01f, 1, .5f, .75f);
//		 } else {
//			 percentagemZoom = .01f;//PApplet.constrain( PApplet.map(distEvaluacao, 0, widthDial, .01f, 1 ), .01f, 1);
//			 velocidadeSom = .5f;
//		 }
		 
		 updatePercentagemZoomdeSom();
		 posBotaoZoom = (widthDial * percentagemZoom);
		 distToqueCentro = xZoomIn + posBotaoZoom;
		 distToqueCentro = PApplet.constrain((xZoomIn + valToMap), xZoomIn, xZoomOut);
	}
	
	public void updatePercentagemZoomdeSom () {
		percentagemZoom = percentagemFinal;
		//PApplet.abs(PApplet.lerp(percentagemZoom, percentagemFinal , .1f) );
//		PApplet.println("percentagemZoom: " + percentagemZoom + " percentagemAmortecedor: "+ percentagemAmortecedor);
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
