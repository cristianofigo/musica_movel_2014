package cc.tallerdinamo.SampleDefender.processing.PlayShip;

import java.util.ArrayList;

import org.puredata.core.PdBase;

import processing.core.PApplet;
import processing.core.PVector;

public class PlayShip {
	PApplet p5;
	PVector pos;
	PVector vel;
	PVector acc;
	float r;
	float maxforce;    // Maximum steering force
	float maxspeed;    // Maximum speed
	ArrayList<Shot> soundShots;
	
	public PlayShip(PApplet _p5) {
		p5 = _p5;
		pos = new PVector (0, (int)(p5.displayHeight*.6f));
		acc = new PVector(0,0);
	    vel = new PVector(0,0);
	    r = 6;
	    maxspeed = 9;
	    maxforce = .5f;
	    soundShots = new ArrayList<Shot>();
	}
	
	public void newShot(int _bufferPos, int centerLand) {
		
		soundShots.add(new Shot(p5, pos.x, pos.y, centerLand+150, _bufferPos));
//		PApplet.println("shots count: "+ soundShots.size() );
	}
	
	public void setPosShipIni() {
		pos = new PVector (0, pos.y);
	}
	
	public void playShipMove(int posS) {
	//	seek(new PVector (posS, pos.y));
	//	update();
		pos = new PVector (posS, pos.y);
		display();
	}
	public void updateShots(float buOnPlayd, int BufPlayStart, int BufPlayinfEnd){
		if (soundShots.size() > 1){
			Shot.bufferOnPlay = (int) buOnPlayd;
			for (int ind = 0 ; ind < soundShots.size() ; ind++) {
				if (soundShots.get(ind).bufferPos > BufPlayStart &&
					soundShots.get(ind).bufferPos < BufPlayinfEnd) {
					soundShots.get(ind).liveShot(BufPlayStart,BufPlayinfEnd);
					soundShots.get(ind).display();
				}
				 
			}
		}
			
	}
	public void ShootSound() {
		
	}
	
	 public void update() {
		// Update velocity
		vel.add(acc);
		// Limit speed
		vel.limit(maxspeed);
		pos.add(vel);
		// Reset accelerationelertion to 0 each cycle
		acc.mult(0);
	 }
	 public void applyForce(PVector force) {
		 // We could add mass here if we want A = F / M
		 acc.add(force);
	}
	// A method that calculates a steering force towards a target
	// STEER = DESIRED MINUS VELOCITY
	public void seek(PVector target) {
	    PVector desired = PVector.sub(target,pos);  // A vector pointing from the location to the target
	    float d = desired.mag();
	    
	    if (d < 10) { //escalada a una magnitud variable
	      float m = 0;// PApplet.map (d, 0, 100, 0, maxspeed);
	      desired.normalize();
	      desired.mult(m);
	    } else {
	    	desired.normalize();
		    desired.mult(maxspeed);//escalada a la maxima magnitud
	    }
	    // Steering = Desired minus velocity
	    PVector steer = PVector.sub(desired,vel);
	    steer.limit(maxforce);  // Limit to maximum steering force
	    
	    applyForce(steer);
	  }	
	
	 public void display() {
		  // Draw a triangle rotated in the direction of velocity
		  float theta = vel.heading2D() + PApplet.PI/2;
		  p5.fill(255,0,0);
		  p5.stroke(255,50,50);
		  p5.strokeWeight(1);
		  p5.pushMatrix();
		  p5.translate(pos.x,pos.y);
		  p5.rotate(theta);
		  p5.beginShape();
		  p5.vertex(0, -r*2);
		  p5.vertex(-r, r*2);
		  p5.vertex(r, r*2);
		  p5.endShape(PApplet.CLOSE);
		  p5.popMatrix();
	  }
		
}
