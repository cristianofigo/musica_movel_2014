package cc.tallerdinamo.SampleDefender.processing.buffergraphic;

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
	private static int numOfSections;
	private int[] cortinaDeSecoes;
	private float loucuraNivel; //a porcentagem de probab. para as seçōes soar bagunçadas
	private int actualIndexSection; //O segmento do buffer que esta sendo escutado
	private int SplitSampleWidth; //largura do sample dividido 
	private float VelCortina;
	
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
		
		loucuraNivel = .1f;
		actualIndexSection = 0;
		
		SplitSampleWidth = bufferSize/numOfSections;
		bufferOnPlay=0;
		cortinaDeSecoes = new int[numOfSections];
		setVelCortina();
		for (int i = 0 ; i < cortinaDeSecoes.length ; i++)
			cortinaDeSecoes[i] = 0;
		
		createBufferDraw();
	}
	public static void setNumOfSections(int ss){
		numOfSections = ss;
	}
	public void bufDraw () {
		p5.image(Bdraw, 0, 0);
		
		p5.pushStyle();
		p5.noStroke();
		float tempW = drawWidth / numOfSections;
		for (int i = 0 ; i < numOfSections ; i++){
			p5.fill(0,cortinaDeSecoes[i]);
			p5.rect(i*tempW, 0, tempW, drawHeight);
		}
		p5.popStyle();
	}
	
	public void bufferDraw() {
		bufDraw();
		pointerPos();
	}
	
	private void pointerPos(){
		p5.pushStyle();
		p5.stroke(255,0,0);
		p5.strokeWeight(3);
		p5.line(posSound, 0, posSound, drawHeight*.2f); //desenho do ponto de audio
		p5.popStyle();
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
		float horDiv = drawWidth/numOfSections;
		for (int i= 0 ; i < drawWidth ; i+=horDiv ) {
			Bdraw.line(i, drawHeight, i, 0);
		}
		Bdraw.endDraw();
		p5.popStyle();
		p5.popMatrix();
	}
	
	public void updateNewSectionOnPlay () {
		//Determinar em qual secçāo do buffer esta
		if ( (int) (bufferOnPlay / SplitSampleWidth) != actualIndexSection) {
			actualIndexSection = (int) (bufferOnPlay / SplitSampleWidth);
//ESCOLHA DA SEÇAO SEGUINTE
			float choice = p5.random(1); //escolhe um numero random para comparar segundo a porcentagem loucuraNivel
			int randomNewPos;
			if (choice < loucuraNivel ) { //porcentagem de probabilidades para escolher a seçāo seguinte
				randomNewPos = (int) p5.random(numOfSections);
			} else {
				randomNewPos = actualIndexSection;
			}
			
			//se calcula o ponto exato para a nova posiçāo do play. o segmento + a posiçāo avançada
			float newStartPos = (float)(randomNewPos*SplitSampleWidth) + (bufferOnPlay%SplitSampleWidth);
			newStartPos = newStartPos / bufferSize;
			PdBase.sendFloat("voltaCero",newStartPos);
			setPosSound(newStartPos);
			actualIndexSection = randomNewPos;
			
			cortinaDeSecoes[actualIndexSection] = 0;
		}
		
		upDateCortinasDeSecoes(); //mudam de intensidade os retangulos pretos sobre cada seçāo
	}
	
	public void upDateCortinasDeSecoes() {
		setVelCortina();
		for (int i = 0 ; i < cortinaDeSecoes.length ; i++) {
			cortinaDeSecoes[i]+=VelCortina;
			PApplet.constrain(cortinaDeSecoes[i], 0, 255);
		}
		cortinaDeSecoes[actualIndexSection] = 0;
	}
	private void setVelCortina(){
		VelCortina = PApplet.map(zoomNivel, 0, 1, 1, 3);
	}
	
	public int getSplitSampleWidth(){
		return SplitSampleWidth;
	}
	public int getNumOfSections() {
		return numOfSections;
	}
	public float getMinPercentZoomPosible(){
		float minP = (float)p5.width / (float)bufferSize;
		return minP; 
	}
	public float getZoomNivel() {
		return zoomNivel;
	}
	public int getActualIndexSection(){
		return actualIndexSection;
	}
	public void setPosSound(float x) {
		posSound = PApplet.map(x, 0, 1, 0, drawWidth);
		bufferOnPlay = (int) PApplet.map(x, 0, 1, 0, bufferSize);
	}
	public void setNovoLoucuraNivel (float n) {
		loucuraNivel = n;
	}
	public void setZoomNivel(float zoomIn) { //recive uma percentagem de zoom desde ControlZoom
		zoomNivel = zoomIn;
	}
}
