package cc.tallerdinamo.SampleDefender.processing;

import java.util.ArrayList;

import org.puredata.core.PdBase;

import processing.core.PApplet;

public class BufferLand extends BufferDraw {
	public ArrayList<Float> roofLand;
	public ArrayList<Float> groundLand;
	public int centerLand;
	public int landHeight;
	public int indexInicio;
	public int visibleSectionWidth;
	private float noiseTime;
	public int posicaoDoPlay;
	
	BufferLand(PApplet _p5, float[] bufferArrayIn) {
		super(_p5, bufferArrayIn);
		// TODO Auto-generated constructor stub
		roofLand = new ArrayList<Float>(p5.displayWidth);
		groundLand = new ArrayList<Float>(p5.displayWidth);
		centerLand = (int) (p5.displayHeight*.6f); //o centro do desenho
		landHeight = (int) (p5.displayHeight*.8f); //altura do desenho do Land
		indexInicio = 0; //Para indicar o inicio do desenho do buffer sengundo a posiçāo do play
		noiseTime = 0;
		getRoofGroundLand();
		
	}
	
	public int getCenterBufferLand() {
		return centerLand;
	}
	public int getHeightBufferLand() {
		return landHeight;
	}

	private void getRoofGroundLand(){
		//Clean the Arraylist if they already have elements 
		if (roofLand != null) roofLand.clear();
		if (groundLand != null) roofLand.clear();
		
		for (int i=0 ; i < bufferSize ; i++) {
			if (BufferIn[i] > 0) {
				roofLand.add(BufferIn[i]);
				groundLand.add(0f);
			} else if (BufferIn[i] < 0 ) {
				groundLand.add(BufferIn[i]);
				roofLand.add(0f);
			}
		}
		visibleSectionWidth = (int) (bufferSize*getZoomNivel()); 
	}
	
	public void editVisibleSection(){
//		setZoomNivel ( PApplet.map(p5.mouseX, 0, p5.width, .01f, 1) );
		visibleSectionWidth = (int) (bufferSize*getZoomNivel()); //establece nivel de zoom
		
//Muda a VELOCIDADE de letura do SOM
		velocidadeSom = PApplet.map(getZoomNivel(), .01f, 1, 0.502f, 1);
		velocidadeSom = PApplet.constrain(velocidadeSom,0.502f, 1);
		PdBase.sendFloat("velLetura", velocidadeSom);

	}
	
	public void updateView(){
		float posMaped = PApplet.map(posSound, 0, drawWidth, 0, bufferSize);
		float tempIndexIni = posMaped - (visibleSectionWidth / 2);
		if (tempIndexIni < 0) {//O index de inicio nāo pode ser menor que cero
			indexInicio = 0;
		} else if ((tempIndexIni + visibleSectionWidth) >  bufferSize) {
			indexInicio = bufferSize-visibleSectionWidth;//o index de inicio + a secçāo visivel do buffer nao pode ser maior que o tamanho do buffer
		} else {
			indexInicio = (int) tempIndexIni;
		}
		posicaoDoPlay =(int) PApplet.map(posMaped, indexInicio, //posiçāo do play no eixo X
				indexInicio + visibleSectionWidth, 0, drawWidth); //A posiçāo do buffer que soa
	}
	
	public void drawLandSection (){
		float maxSup = landHeight/2;
		float maxInf = landHeight/2;
		p5.pushMatrix();
		p5.pushStyle();
		p5.translate(0,centerLand); //desenho a partir do canto superior esquerdo
		p5.fill(255);
		p5.noStroke();
		p5.rect(0, -maxSup, p5.displayWidth, landHeight);
		p5.stroke(255,0,0,50); //a cor da linha
		p5.line(0, 0, p5.displayWidth, 0);
		
//Variaveis para colocaçāo da terra de jogo	
		float OffsetGround = landHeight*.1f;
		float groundEdge = landHeight*.4f;
		float OffsetRoof = -landHeight*.1f;
		float roofEdge = -landHeight*.4f; //40% da altura da janela  
		int variableAjusteBuf = visibleSectionWidth / p5.displayWidth; //também poderia ser visibleGroundWidth / p5.displayWidth;
		int indexBuffer=indexInicio;
		
		p5.fill(0,100,0);
		p5.stroke(0,100,0);
		p5.rect(0, roofEdge, p5.displayWidth, OffsetRoof);//rect desenha teto fixo
		p5.rect(0, groundEdge, p5.displayWidth, OffsetGround);//rect desenha chāo fixo
		p5.strokeWeight(1);
		
		float rY1Roof;
		float rX1Roof;
		float rY1ground;
		float rX1ground;
		float RoofXVariavel;
		float RoofYVariavel;
		float GroundXVariavel;
		float GroundYVariavel;
		float amplitudeMovimento=0;
		int constMult = 1;
		for (int i = 0 ; i < p5.displayWidth ; i+= constMult){
			rY1Roof = roofLand.get(indexBuffer) * maxInf;
			rX1Roof = i;
			rY1ground = groundLand.get(indexBuffer) * maxInf;
			rX1ground = i;
				
		   //Se o buffer já foi escutado
			if ( i < posicaoDoPlay) { 
				//Calculo do Noise
				float amortecimentoMov = posicaoDoPlay -i ;
				amortecimentoMov = PApplet.constrain(PApplet.map(amortecimentoMov, 
						posicaoDoPlay, 0, 0, 1),	0, 1) ; //da posiçāo do play ao zero no Width
				amplitudeMovimento = PApplet.map( p5.noise(noiseTime+i), 0, 1,
									-(p5.displayWidth*.02f), (p5.displayWidth*.02f) ) ;
				noiseTime += .0001f;
				amplitudeMovimento *= amortecimentoMov;
				
				if (rY1Roof != 0 ) {
					RoofYVariavel = (roofEdge+rY1Roof) - ( ( ( roofEdge + rY1Roof ) * (0.6f * amortecimentoMov) ) * amortecimentoMov);
					//os 20 pixels mais proximos ao ponto de Som pu posiçāo so play
					if ( (posicaoDoPlay - i) <= (posicaoDoPlay/2) && (posicaoDoPlay - i) > 0 ) {
						RoofYVariavel = RoofYVariavel + ( RoofYVariavel * (PApplet.map((posicaoDoPlay - i), (posicaoDoPlay/2), 0, 0, 1) ) );
					}
					RoofXVariavel = rX1Roof  + amplitudeMovimento;
					
					p5.line (rX1Roof, roofEdge,RoofXVariavel, RoofYVariavel ); //crece uma percentagem de seu tamanho
				} else if (rY1ground != 0) {
					GroundYVariavel = (groundEdge+rY1ground) - ( ( ( groundEdge + rY1ground ) * (0.6f* amortecimentoMov) ) * amortecimentoMov) ;
					if ( (posicaoDoPlay - i) <= (posicaoDoPlay/2) && (posicaoDoPlay - i) > 0 ) {
						GroundYVariavel = GroundYVariavel + ( GroundYVariavel * (PApplet.map((posicaoDoPlay - i), (posicaoDoPlay/2), 0, 0, 1) ) );
					}
					GroundXVariavel = rX1ground + amplitudeMovimento;
					p5.line (rX1ground, groundEdge,GroundXVariavel, GroundYVariavel);
				}
			} else { //o buffer ainda nāo foi escutado
				if (rY1Roof != 0 ) {
					RoofXVariavel = rX1Roof;//  + amplitudeMovimento;
					p5.line (rX1Roof, roofEdge,RoofXVariavel, roofEdge+rY1Roof);
				} else if (rY1ground != 0) {
					GroundXVariavel = rX1ground;// + amplitudeMovimento;
					p5.line (rX1ground, groundEdge,GroundXVariavel, groundEdge+rY1ground);
				}
			}
				
			
			
	    	
			indexBuffer += (variableAjusteBuf * constMult);
			if ( (indexBuffer + variableAjusteBuf) >= roofLand.size()) {
				indexBuffer = (roofLand.size()-1) - variableAjusteBuf;
			}
		}
		p5.popStyle();
		p5.popMatrix();
	}
	
	public void windowsSection(){
		p5.pushMatrix();
		p5.pushStyle();
		p5.translate(0,centerBufferDraw);
		p5.fill(0,120);
//		p5.strokeWeight(3);
		p5.stroke(255,0,0);
		float wWidth = PApplet.map(visibleSectionWidth, 0, bufferSize,  0, p5.displayWidth);
		float mapX1 = PApplet.map(indexInicio, 0, bufferSize, 0, p5.displayWidth);
		float mapX2 = wWidth ;
		p5.rect(0, -drawHeight/2, mapX1, drawHeight);
		p5.rect(mapX1+mapX2, -drawHeight/2, drawWidth-(mapX1+mapX2), drawHeight);
//		p5.rect(mapX1,-drawHeight/2, mapX2, drawHeight);
		p5.popStyle();
		p5.popMatrix();
	}
	
}
