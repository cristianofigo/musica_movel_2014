package cc.tallerdinamo.SampleDefender.processing;

import org.puredata.core.PdBase;

import processing.core.PApplet;
import processing.core.PGraphics;


public class BufferDraw {
	PGraphics Bdraw;
	public static float[] BufferIn;
	public int bufferSize;
	PApplet p5;
	public int drawWidth, drawHeight, centerBufferDraw;
	public float posSound;
	public int bufferOnPlay;
	private float zoomNivel;
	
	BufferDraw (PApplet _p5, float[] bufferArrayIn) {
		p5 = _p5;
		zoomNivel=.5f;//o nivel de zoom inicial
		BufferIn = bufferArrayIn;
		bufferSize = BufferIn.length;
		drawWidth = p5.width;
		drawHeight = (int)(p5.height*.2);
		centerBufferDraw = drawHeight/2;
		posSound = 0;
		Bdraw = p5.createGraphics(drawWidth, drawHeight);
		createBufferDraw();
		bufferOnPlay=0;
	}
	
	public float getMinPercentZoomPosible(){
		float minP = (float)p5.width / (float)bufferSize;
		return minP; 
	}
	public float getZoomNivel() {
		return zoomNivel;
	}
	public void setZoomNivel(float zoomIn) { //recive uma percentagem de zoom desde ControlZoom
		zoomNivel = zoomIn;
	}
	
	private void createBufferDraw(){
		p5.pushMatrix();
		p5.pushStyle();
		Bdraw.beginDraw();
		Bdraw.fill(255);
		Bdraw.rect(0, 0, drawWidth, drawHeight);
		Bdraw.stroke(0);
		float escaleBuf = bufferSize/(int)(p5.width*2);
		for (float i=escaleBuf ; i < bufferSize ; i+=escaleBuf) {
			float Y2 = BufferIn[(int)i];
			float Y1 = BufferIn[(int)(i-escaleBuf)];
			Y2 = PApplet.map(Y2, -1,1, drawHeight, 0);
			float X1 = PApplet.map(i, 0, bufferSize, 0, drawWidth);
			Y1 = PApplet.map(Y1, -1,1, drawHeight, 0);
			float X2 = PApplet.map(i, 0, bufferSize, 0, drawWidth);
			Bdraw.line (X1, Y1, X2, Y2);
		}
		Bdraw.stroke(120);
		float horDiv = drawWidth/16;
		for (int i= 0 ; i < drawWidth ; i+=horDiv ) {
			Bdraw.line(i, drawHeight, i, 0);
		}
		Bdraw.endDraw();
		p5.popStyle();
		p5.popMatrix();
	}
	public void setPosSound(float x) {
		posSound = PApplet.map(x, 0, 1, 0, drawWidth);
		bufferOnPlay = (int) PApplet.map(x, 0, 1, 0, bufferSize);
	}
	public void bufDraw () {
		p5.image(Bdraw, 0, 0);
	}
	public void pointerPos(){
		bufDraw();
		p5.pushStyle();
		p5.stroke(255,0,0);
		p5.strokeWeight(3);
		p5.line(posSound, 0, posSound, drawHeight*.2f); //desenho do ponto de audio
		p5.popStyle();
	}
	
}
