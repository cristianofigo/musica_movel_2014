package cc.tallerdinamo.SampleDefender.processing.PlayShip;

import org.puredata.core.PdBase;

import processing.core.PApplet;
import processing.core.PVector;

public class Shot {
	PApplet p5;
	PVector pos;
	PVector posFinal;
	PVector vel;
	PVector acc;
	float r;
	float maxforce;    // Maximum steering force
	float maxspeed;    // Maximum speed
	boolean flying;
	public int bufferPos; 
	public static int bufferOnPlay;
	boolean Playsom;
	
	Shot (PApplet _p5, float px, float pyi,float pyf, int _bufferPos) {
		p5 = _p5;
		pos = new PVector (px,pyi);
		posFinal = new PVector (px,pyf);
		acc = new PVector(0,0);
	    vel = new PVector(0,0);
	    r = 6;
	    maxspeed = 10;
	    maxforce = .5f;
	    flying = true;
	    bufferPos = _bufferPos; //posiçāo do som  no Buffer
	    bufferOnPlay=0;
	    Playsom = false;
	}
	
	public void liveShot(int BufPlayStart, int BufPlayinfEnd) {
		boolean prevPlaySom = Playsom;
		if (flying) {
			posFinal.x = PApplet.map(bufferPos, BufPlayStart, BufPlayinfEnd, 0, p5.width);
			shootTarget(posFinal.x, posFinal.y);
		} else {
			pos.x = PApplet.map(bufferPos, BufPlayStart, BufPlayinfEnd, 0, p5.width);
			
		}
		if (bufferPos > bufferOnPlay) {
			Playsom = false;
		} else  {
			Playsom = true;
		}
		if (!prevPlaySom && Playsom)
			PdBase.sendBang("SomKick01");
	}
	
	public void setPlayingFalse() {
		Playsom = false;
	}
	
	public void shootTarget(float sx, float sy) {
		seek(new PVector (sx, posFinal.y));
		update();
		display();
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
	      float m = PApplet.map (d, 0, 10, 0, maxspeed);
	      desired.normalize();
	      desired.mult(m);
	      flying = false;
	    	  
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
		  p5.stroke(200,150,50);
		  p5.strokeWeight(1);
		  p5.pushMatrix();
		  p5.translate(pos.x,pos.y);
		  p5.rotate(theta);
		  if (flying) 
			  p5.rect(0, 0, r, r);
		  else
			  p5.rect(0, 0, r, r*10);
		  p5.popMatrix();
	  }
}
