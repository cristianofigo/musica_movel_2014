package cc.tallerdinamo.SampleDefender.processing.PlayShip;

import java.util.ArrayList;

import org.puredata.core.PdBase;

import processing.core.PApplet;
import processing.core.PVector;

public class PlayShip {
	Particula particulaA;
	PApplet p5;
	PVector pos;
	float r;
	float maxforce;    // Maximum steering force
	float maxspeed;    // Maximum speed
	float ang;
	ArrayList<PermanentShoot> soundShots;
	ArrayList <Projetil> projetiles;
	
	public PlayShip(PApplet _p5) {
		p5 = _p5;
		pos = new PVector (0, (int)(p5.displayHeight*.6f));
	    r = 7;
	    maxspeed = 9;
	    maxforce = .5f;
	    soundShots = new ArrayList<PermanentShoot>();
	    projetiles = new ArrayList<Projetil>();
	    particulaA = new Particula (p5);
	    ang = PApplet.PI/2;
	}
	
	public void setAnglePlay(float newAng) {
		ang = newAng;
	}
	public void newShot(int _bufferPos, int centerLand) {
		soundShots.add(new PermanentShoot(p5, pos.x, pos.y, centerLand+150, _bufferPos));
//		PApplet.println("shots count: "+ soundShots.size() );
	}
	
	public void newProjetil(PVector PosIni, float anguloTiro, float magnitudeDoTiro) {
		anguloTiro = anguloTiro+PApplet.PI;
		projetiles.add(new Projetil(p5, PosIni.x, PosIni.y, anguloTiro, magnitudeDoTiro ) );
//		PApplet.println("shots count: "+ soundShots.size() );
	}
	public void setPosShipIni() {
		pos = new PVector (0, pos.y);
	}
	
	public void updatePlayShip(int posS) {
		pos = new PVector (posS, pos.y);
		display();
	}
	public void updateShots(float buOnPlayd, int BufPlayStart, int BufPlayinfEnd){
		if (soundShots.size() > 1){
			PermanentShoot.bufferOnPlay = (int) buOnPlayd;
			for (int ind = 0 ; ind < soundShots.size() ; ind++) {
				//Se o sound Shot fica dentro da tela é desenhado é ativado
				if (soundShots.get(ind).bufferPos > BufPlayStart &&
					soundShots.get(ind).bufferPos < BufPlayinfEnd) {
					soundShots.get(ind).liveShot(BufPlayStart,BufPlayinfEnd);
					soundShots.get(ind).display();
				} 
			}
		}
		
		//Se tem prjetiles pra desenhar
		if (projetiles.size() > 1) {
			for (int ind = projetiles.size()-1 ; ind > 0  ; ind--) {
				if (projetiles.get(ind).isAlive()) {
					//Calculo de gravidade
					PVector gravity;
					if (projetiles.get(ind).getStartPoint().y > pos.y)
						gravity = new PVector (0, -0.3f);
					else
						gravity = new PVector (0, 0.3f);
				    gravity.mult(projetiles.get(ind).getMasaDoProjetil()); //multiplica a masa que depois divide dentro de Projetil
				    projetiles.get(ind).applyForce(gravity);
				    
				    //update
					projetiles.get(ind).update();
					projetiles.get(ind).desenha();
				} else {
					projetiles.remove(ind);
				}
				
			}
			
		}
	}
	
	public void updateParticulas() {
		
		particulaA.upDate();
	}
	
	public void setParticulasPos(float _posX, float _posY, float _ang, float _dist) {
		particulaA.daMovimento(pos.x, pos.y, _ang, _dist); //posiçao e angulo
	}
	
	public void ShootSound() {
		
	}
	
	 public void display() {
		  // Draw a triangle rotated in the direction of velocity
		  p5.fill(0,120);
		  p5.stroke(255);
//		  p5.noStroke();
		  p5.strokeWeight(2);
		  p5.pushMatrix();
		  p5.translate(pos.x,pos.y);
		  p5.rotate(ang);
		  p5.beginShape();
		  p5.vertex(0, -r*2);
		  p5.vertex(-r, r*2);
		  p5.vertex(r, r*2);
		  p5.endShape(PApplet.CLOSE);
		  p5.popMatrix();
	  }
		
}
