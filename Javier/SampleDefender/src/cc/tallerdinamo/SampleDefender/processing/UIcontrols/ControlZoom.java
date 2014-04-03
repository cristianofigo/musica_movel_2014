package cc.tallerdinamo.SampleDefender.processing.UIcontrols;

import android.util.Log;
import processing.core.PApplet;
import processing.core.PVector;

public class ControlZoom extends ControlDesign {
	private boolean visible;
	PApplet p5;
	PVector pos;
	float xZoomIn;
	float percentagemZoom;
	float xZoomOut;
	private float distToqueCentro;
	float widthDial;
	float posBotaoZoom;
	float botaoDiam;
	
	public ControlZoom (PApplet _p5, float valorInicial) {
		p5 = _p5;
		pos = new PVector (p5.width*.5f, p5.height*.96f);
		xZoomIn = p5.width*.1f; 
		percentagemZoom = valorInicial;
		xZoomOut = p5.width*.9f;
		widthDial = (xZoomOut-xZoomIn);
		posBotaoZoom = (widthDial * percentagemZoom);
		distToqueCentro = xZoomIn + posBotaoZoom;
		botaoDiam = 30;
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
	
	 public float settingNivelZoom(float px, float py) {
		 PVector posToque = new PVector (px,  pos.y); //so comparamos a posiçāo X
		 PVector posInicial = new PVector (xZoomIn, pos.y);
		 float distEvaluacao = posToque.dist(posInicial);
			
//		 Log.d("distToqueCentro", Float.toString(distToqueCentro) );
		 percentagemZoom = PApplet.constrain( PApplet.map(distEvaluacao, 0, widthDial, .01f, 1 ), .01f, 1);
//		 Log.d("ControlZoom", Float.toString(distToqueCentro) );
//		 posBotaoZoom = (widthDial * percentagemZoom);
		 distToqueCentro = xZoomIn + distEvaluacao;
		 return percentagemZoom;
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
