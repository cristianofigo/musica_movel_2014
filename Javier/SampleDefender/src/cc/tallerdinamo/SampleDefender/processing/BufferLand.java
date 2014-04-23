package cc.tallerdinamo.SampleDefender.processing;

import java.util.ArrayList;

import org.puredata.core.PdBase;

import processing.core.PApplet;

public class BufferLand extends BufferDraw {
	public ArrayList<Float> roofLand; //valores positivos do buffer,
	public ArrayList<Float> groundLand;
	public int centerLand;
	public int landHeight;
	public int indexInicio;
	public int visibleSectionWidth;
	private float noiseTime;
	public int posicaoDoPlay;
	
	
	BufferLand(PApplet _p5, float[] bufferArrayIn) {
		super(_p5, bufferArrayIn);
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
		visibleSectionWidth = (int) (bufferSize*getZoomNivel()); //set tamanho de zoom
	}
	
	public void updateView(){
		
		updateNewSectionOnPlay(); //methodo da clase pai
	
//UPDATE A POSIÇAO DO PLAY NA TELA E A FAIXA DE BUFFER VISIVEL
		float posMaped = PApplet.map(posSound, 0, drawWidth, 0, bufferSize);
		float tempIndexIni = posMaped - (visibleSectionWidth / 2);
		if (tempIndexIni < 0) {//O index de inicio nāo pode ser menor que cero
			indexInicio = 0;
		} else if ((tempIndexIni + visibleSectionWidth) >  bufferSize) {
			indexInicio = bufferSize-visibleSectionWidth;//o index de inicio + a seçāo visivel do buffer nao pode ser maior que o tamanho do buffer
		} else {
			indexInicio = (int) tempIndexIni;
		}
		posicaoDoPlay =(int) PApplet.map(posMaped, indexInicio, //posiçāo do play no eixo X
				indexInicio + visibleSectionWidth, 0, drawWidth); //A posiçāo do buffer que soa
	}
	
	public void drawLandSection () {
		float bordeSup = -landHeight/2;
		float bordeInf = landHeight/2;
		//Variaveis para colocaçāo da terra de jogo	
		float OffsetGround = landHeight*.1f; //separaçao do borde ao começo do desenho
		float groundEdge = -landHeight*.025f; // 0; //landHeight*.4f;
		float OffsetRoof = -landHeight*.1f;
		float roofEdge = landHeight*.025f; // 0; // -landHeight*.4f; //40% da altura da janela  
		int variableAjusteBuf = visibleSectionWidth / p5.displayWidth; //quantos samples por pixel
		int indexBuffer=indexInicio;
		
//Desenho da tela de fundo
		p5.pushMatrix();
		p5.pushStyle();
		p5.translate(0,centerLand); 
		
//desenho  fundo
//		p5.fill(0,100,0);
		
//		p5.rect(0, roofEdge, p5.displayWidth, OffsetRoof);//rect desenha teto fixo
//		p5.rect(0, groundEdge, p5.displayWidth, OffsetGround);//rect desenha chāo fixo
//		p5.strokeWeight(1);
		
		float rY1Roof;
		float rX1Roof;
		float rY1ground;
		float rX1ground;
		float RoofXVariavel;
		float RoofYVariavel;
		float GroundXVariavel;
		float GroundYVariavel;
		float amplitudeMovimento=0;
		int constMult = 1; //resoluçāo do desenho 
		
		int div = (int)(visibleSectionWidth / getSplitSampleWidth());
		if (div <= 1) {
			p5.fill(150*((float)(indexBuffer / getSplitSampleWidth())/getNumOfSections()), 
					0, 
					150 - (150*((float)(indexBuffer / getSplitSampleWidth())/getNumOfSections())) );
			p5.noStroke();
			p5.rect(0, bordeSup, p5.displayWidth, landHeight);//desenho a partir do canto superior esquerdo
			p5.stroke(255,0,0,50); //a cor da linha
			p5.line(0, 0, p5.displayWidth, 0);
			div = 1;
		}

		float sectionWidth = p5.displayWidth / div;
		p5.strokeWeight(1);
		for (int i = 0 ; i < p5.displayWidth ; i+= constMult){
			rY1Roof = roofLand.get(indexBuffer) * bordeInf;
			rX1Roof = i;
			rY1ground = groundLand.get(indexBuffer) * bordeInf;
			rX1ground = i;
			p5.stroke(255);
			//Desenho das linheas de diviçāo e os fondos de cor
			if (indexBuffer % getSplitSampleWidth() < variableAjusteBuf) { //methodo do pai
				p5.fill(150*((float)(indexBuffer / getSplitSampleWidth())/getNumOfSections()), 
						0, 
						150 - (150*((float)(indexBuffer / getSplitSampleWidth())/getNumOfSections())) );
				p5.noStroke();
				p5.rect(i, bordeSup, sectionWidth, landHeight);//desenho a partir do canto superior esquerdo*
//				p5.stroke(255,20);
//				p5.line(i, bordeSup, i, bordeInf);
				p5.stroke(255);
			}
			
		   //Se o buffer já foi escutado
			if ( i < posicaoDoPlay) { // i é a variavel que vai desde 0 até o width
				float amortecimentoFechaOndaPaso = PApplet.constrain(PApplet.map(posicaoDoPlay -i, 
						posicaoDoPlay,0, .4f, 1), .4f, 1);
				//varia segundo o nivel de zoom, mas com um novo minimo é .1f, porque o Zoom pega volores muito baixos 
				amortecimentoFechaOndaPaso = PApplet.pow(amortecimentoFechaOndaPaso,3) * 
											 PApplet.constrain(getZoomNivel(), .3f, 1); 
				
				float amortecimentoMov = PApplet.constrain(PApplet.map(posicaoDoPlay -i, 
						posicaoDoPlay, 0, 0, 1),	0, 1) ; //da posiçāo do play ao zero no Width
				//Calculo do Noise
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
					
					p5.line (rX1Roof, roofEdge * amortecimentoFechaOndaPaso , 
							RoofXVariavel, RoofYVariavel ); //crece uma percentagem de seu tamanho
				} else if (rY1ground != 0) {
					GroundYVariavel = (groundEdge+rY1ground) - ( ( ( groundEdge + rY1ground ) * (0.6f* amortecimentoMov) ) * amortecimentoMov) ;
					if ( (posicaoDoPlay - i) <= (posicaoDoPlay/2) && (posicaoDoPlay - i) > 0 ) {
						GroundYVariavel = GroundYVariavel + ( GroundYVariavel * (PApplet.map((posicaoDoPlay - i), (posicaoDoPlay/2), 0, 0, 1) ) );
					}
					GroundXVariavel = rX1ground + amplitudeMovimento;
					p5.line (rX1ground, groundEdge * amortecimentoFechaOndaPaso ,
							GroundXVariavel, GroundYVariavel);
				}
			} else { //o buffer ainda nāo foi escutado
				float amortecimentoAbreOndaPaso = PApplet.constrain(PApplet.map(i, 
						posicaoDoPlay,posicaoDoPlay*1.05f, 1, .4f),	.4f, 1) ;
				//varia segundo o nivel de zoom
				amortecimentoAbreOndaPaso = (PApplet.pow(amortecimentoAbreOndaPaso,3) * getZoomNivel() ) ; 
				
				if (rY1Roof != 0 ) {
					RoofXVariavel = rX1Roof;
					p5.line (rX1Roof, roofEdge * amortecimentoAbreOndaPaso, RoofXVariavel, roofEdge+rY1Roof);
				} else if (rY1ground != 0) {
					GroundXVariavel = rX1ground;
					p5.line (rX1ground, groundEdge * amortecimentoAbreOndaPaso, GroundXVariavel, groundEdge+rY1ground);
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
