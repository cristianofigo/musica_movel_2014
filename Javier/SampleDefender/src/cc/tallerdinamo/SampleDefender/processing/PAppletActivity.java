package cc.tallerdinamo.SampleDefender.processing;

import java.io.File;
import java.io.IOException;

import ketai.ui.KetaiGesture;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.PdListener;
import org.puredata.core.utils.IoUtils;
import org.puredata.core.utils.PdDispatcher;

import cc.tallerdinamo.SampleDefender.processing.PlayShip.PlayShip;
import cc.tallerdinamo.SampleDefender.processing.UIcontrols.ControlZoom;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;
import processing.core.PApplet;

public class PAppletActivity extends PApplet {
	PdDispatcher dispatcher = new PdUiDispatcher();
//	SampleListener sampleListener = new SampleListener();
	KetaiGesture gesture;
	PlayShip playShip;
	ControlZoom controlZoom;
	
	private float[] BufferIn;
	private int bufferSize;
	private final String TAG = "SampleAdmin";
	BufferLand bufferLand;
	UserInteraction ui;
	private double pastTime;
	String SampleIn;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	   super.onCreate(savedInstanceState);
	   Bundle extras=getIntent().getExtras();
	   SampleIn=extras.getString("SampleToView");
	   }
	
	public void setup () {
		println("ON SETUP!!");
		pdSetup();
		processingSetup();
	}
	void processingSetup() {
	//	colorMode(HSB);
		pastTime = millis();//vai ser o valor para comparar cada novo frame e obter fps
		ui = new UserInteraction(this);
		gesture = new KetaiGesture(this);
		bufferLand = new BufferLand(this, BufferIn);
/*		uiControles = new UiControls(this,(width*0.8f),
				bufferLand.centerLand, bufferLand.landHeight*.4f );*/
		playShip = new PlayShip(this);
		controlZoom = new ControlZoom(this,bufferLand.getZoomNivel() );
		bufferLand.editVisibleSection(); 
		background(120);
	}
	
	void pdSetup() {
		openPatch("SampleDefenderPatch.pd"); //abrir o patch
		PdBase.sendBang(SampleIn);
		PdBase.sendFloat("ligaMetro", 1);
		//PdBase.sendBang("playSample");
		bufferSize = PdBase.arraySize("array1");
		BufferIn = new float[bufferSize];
		PdBase.readArray(BufferIn, 0, "array1", 0, bufferSize);
//		println ("buffer size: "+BufferIn.length);
	}
	public void draw (){
		/*background (color(map(mouseX, 0,width, 0, 255), 
				          map(mouseY, 0,height, 0, 255), 255) );*/
		if (mousePressed) {
			if (PApplet.abs (bufferLand.centerBufferDraw - mouseY) < (bufferLand.drawHeight/2) ) {
				bufferLand.editVisibleSection(); 
			}
		}
		bufferLand.pointerPos();
		bufferLand.updateView();
		bufferLand.drawLandSection();
		bufferLand.windowsSection();
		ui.setLetureVel(bufferLand.centerLand, bufferLand.drawHeight);
		playShip.playShipMove(bufferLand.posicaoDoPlay);
		playShip.updateShots(bufferLand.bufferOnPlay, bufferLand.indexInicio, 
				            bufferLand.indexInicio + bufferLand.visibleSectionWidth);
		
		controlZoom.desenhaZoom();
		
		fill(0);
		text("fps: "+ (int)fps(), 5, height*.48f);
	}
	
	public float fps(){
		
		double atualTime = millis();
		float tempoDiferenca = (float) (atualTime - pastTime);
		float fps = 1000/tempoDiferenca;
		pastTime = atualTime;
//		println ("tempoDiferença:"+tempoDiferenca);
		return fps;
	}
	public int sketchWidth() {
	    return displayWidth;
	}
	public int sketchHeight() {
	   return displayHeight;
	}
	
//KETAI
	public void onTap(float x, float y) {	}
	public void onDoubleTap(float x, float y) {}
	public void onLongPress(float x, float y) {}
	public void onFlick( float x, float y, float px, float py, float v) { }
	public void onPinch(float x, float y, float d) {}
	public void onRotate(float x, float y, float angle) {}
	public void mouseDragged() {}
	
	public boolean surfaceTouchEvent(MotionEvent event) {  //(20)
		//call to keep mouseX and mouseY constants updated
		super.surfaceTouchEvent(event);
		  
		int action = event.getAction(); 
		float x    = event.getX();
		float y    = event.getY();
	  	int index  = action >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
	    int id     = event.getPointerId(index);
/* action:
 * 261 = ACTION_DOWN ; 262 = ACTION_UP
 * index:
 * 0 = primeir toque ; 1 = segundo toque
 */
//		println ("id MotionEvent:"+id+" action: "+ action+ " index: "+index);

	    if (index == 1 && action == 262)
	    	playShip.newShot(bufferLand.bufferOnPlay, bufferLand.centerLand);
	    
	    controlZoom.activeZoom(x, y);
		if (controlZoom.isVisible()) {
			bufferLand.setZoomNivel(controlZoom.settingNivelZoom(x,y) );
			bufferLand.editVisibleSection();
		}
		
		//forward events
		return gesture.surfaceTouchEvent(event);
	}
	
// PD De aqui para baixo e todo o negocio para o funcionamento de PureData
	private static final int SAMPLE_RATE = 44100;

	private Toast toast = null;
	
	private void toast(final String msg) {
		this.runOnUiThread(new Runnable() {
		@Override
		public void run() {
		if (toast == null) {
		toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
		}
		toast.setText(TAG + ": " + msg);
		toast.show();
		}
		});
	}
	public void onResume() {
		super.onResume();
		Log.d(TAG, "Starting LibPD");
		if (AudioParameters.suggestSampleRate() < SAMPLE_RATE) {
		toast("required sample rate not available; exiting");
		finish();
		return;
		}
		final int nOut = Math.min(AudioParameters.suggestOutputChannels(), 2);
		if (nOut == 0) {
		toast("audio output not available; exiting");
		finish();
		return;
		}
		try {
			PdAudio.initAudio(SAMPLE_RATE, 0, nOut, 1, true);
			PdAudio.startAudio(this);
			PdBase.setReceiver(dispatcher);
			//dispatcher.addListener("pos", sampleListener);
			dispatcher.addListener("pos", new PdListener.Adapter() {
				@Override
				public void receiveFloat(String source, float x) {
//				Log.i("Mensaje de Entrada", "pitch: " + x);
				if (bufferLand != null) {//pasa o dado só quando tiver objeto bufferLand pronto
					bufferLand.setPosSound(x); //temos a posicao do som no Sample
					}
				}
			});

		} catch (final IOException e) {
			Log.e(TAG, e.toString());
		}
	}
	
	public void onPause() {
		super.onPause();
		PdAudio.stopAudio();
	}
	
	public void onDestroy() {
		cleanup();
		super.onDestroy();
	}
		
	public void finish() {
		Log.d(TAG, "Finishing for some reason");
		cleanup();
		super.finish();
	}
	
	public int openPatch(final String patch) {
		final File dir = this.getFilesDir();
		final File patchFile = new File(dir, patch);
		int out=-1;
		try {
			IoUtils.extractZipResource(this.getResources().openRawResource(cc.tallerdinamo.SampleDefender.R.raw.patch), dir, true);
			out = PdBase.openPatch(patchFile.getAbsolutePath());
		} catch (final IOException e) {
			e.printStackTrace();
			Log.e(TAG, e.toString() + "; exiting now");
			finish();
		}
		return out;
	}
	
	public void cleanup() {
		// make sure to release all resources
		PdAudio.stopAudio();
		PdBase.release();
	}

}
