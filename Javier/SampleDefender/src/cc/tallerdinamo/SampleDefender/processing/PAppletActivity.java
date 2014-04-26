package cc.tallerdinamo.SampleDefender.processing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import ketai.ui.KetaiGesture;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.PdListener;
import org.puredata.core.utils.IoUtils;
import org.puredata.core.utils.PdDispatcher;

import cc.tallerdinamo.SampleDefender.processing.PlayShip.PlayShip;
import cc.tallerdinamo.SampleDefender.processing.UIcontrols.ControlParticulas;
import cc.tallerdinamo.SampleDefender.processing.UIcontrols.ControlZoom;
import cc.tallerdinamo.SampleDefender.processing.buffergraphic.BufferLand;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import processing.core.PApplet;
import processing.core.PFont;

public class PAppletActivity extends PApplet {
	PdDispatcher dispatcher = new PdUiDispatcher();
//	SampleListener sampleListener = new SampleListener();
	KetaiGesture gesture;
	PlayShip playShip;
	ControlZoom controlZoom;
	ControlParticulas controlParticulas;
	
	private float[] BufferIn;
	private int bufferSize;
	private final String TAG = "SampleAdmin";
	BufferLand bufferLand;
	private double pastTime;
	String SampleIn;
	PFont font;
	
	final int INVALID_POINTER_ID = -1;
	private MultiTouchP MTlistaPontos; //lista de pontos multiTouch
	
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
		
		// The font must be located in the sketch's 
		// "data" directory to load successfully
		font = loadFont("MuseoSans-300-48.vlw");
		textFont(font, 20);
	//	colorMode(HSB);
		pastTime = millis();//vai ser o valor para comparar cada novo frame e obter fps
		gesture = new KetaiGesture(this);
		bufferLand = new BufferLand(this, BufferIn);
/*		uiControles = new UiControls(this,(width*0.8f),
				bufferLand.centerLand, bufferLand.landHeight*.4f );*/
		playShip = new PlayShip(this);
		
		controlZoom = new ControlZoom(this,bufferLand.getMinPercentZoomPosible() );
		controlZoom.setVelocitySom();
		controlParticulas = new ControlParticulas(this);
		
		bufferLand.setZoomNivel(controlZoom.getPercentagemZoom());
		bufferLand.editVisibleSection();
		
		MTlistaPontos = new MultiTouchP(); //objeto nesta mesma aba que permite o multi touch
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
		
		bufferLand.bufferDraw();
		bufferLand.updateView();
		bufferLand.drawLandSection();
		bufferLand.windowsSection();
		playShip.playShipMove(bufferLand.posicaoDoPlay);
		playShip.updateShots(bufferLand.bufferOnPlay, bufferLand.indexInicio, 
				            bufferLand.indexInicio + bufferLand.visibleSectionWidth);
		playShip.updateParticulas();
		
		controlParticulas.desenhaControl();
		controlZoom.desenhaControl();
		controlZoom.updatePercentagemZoomdeSom();//mudanza suave do zoom
		bufferLand.setZoomNivel(controlZoom.getPercentagemZoom() ); //da uma percentagem de zoom para desenha-lho
		bufferLand.editVisibleSection();
		
		MTlistaPontos.gerenciadorDeToques();
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

//LETURA DE EVENTOS DA TELA / Update do multi touch
	public boolean surfaceTouchEvent(MotionEvent me) {
		int action = me.getAction(); 
		float x    = me.getX();
		float y    = me.getY();
		int index  = action >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;	
		int id     = me.getPointerId(index);
		
	    switch (action & MotionEvent.ACTION_MASK) {
	    case MotionEvent.ACTION_DOWN: {
	    	MTlistaPontos.insert(id, x, y);	        
		    break;
	    }  
	   
        case MotionEvent.ACTION_UP: {
        	MTlistaPontos.delete(id);
            break;
        }

	    case MotionEvent.ACTION_MOVE: {
	        int numPointers = me.getPointerCount();
	        for (int i=0; i < numPointers; i++) {
	          id = me.getPointerId(i);
	          x  = me.getX(i);
	          y  = me.getY(i);
	          MTlistaPontos.update(id, x, y);
	        }
		    break;
	    } 
        
	    case MotionEvent.ACTION_POINTER_DOWN: {
	    	MTlistaPontos.insert(id, x, y);
	    	break;
	    }	   
        
        case MotionEvent.ACTION_POINTER_UP: {
        	
        	MTlistaPontos.delete(id);
            break;
        }

        case MotionEvent.ACTION_CANCEL: {
        	MTlistaPontos.clearMe();
            id = INVALID_POINTER_ID;
            break;
        }
	    }
	    
	    return super.surfaceTouchEvent(me);
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

//TEST MULTI TOUCH______________________________________________________________________________---
	/**
	 * This class stores all the touch points objects in a Hash List
	 * and manage them.
	 */
	class MultiTouchP {
		//Uma lista que almacena os pontos com um ID.  Name/value pairs
		private Map<Integer,Point> hashList;
		
		MultiTouchP() {
			//HashMap Let’s you store and access elements as name/value pairs
			hashList = new HashMap<Integer,Point>();
		}
		
		public synchronized void gerenciadorDeToques(){
			Set<Integer> keyList = hashList.keySet();
			Iterator<Integer> iter = keyList.iterator();
			int cnt = 0;
			ArrayList<Point> lista = new ArrayList<Point>();
			Point anchor = null;
			while(iter.hasNext()){
				anchor = hashList.get(iter.next());
	        	lista.add(anchor);
				cnt++;
			}
			if (lista.size() > 0) {
//TODO: O control das particulas é só visiel quando tem pressoado o "Play", 
				switch (cnt){
				case 1:
					setControlZoom(lista.get(0).posX, lista.get(0).posY);
					setParticulasPos(lista.get(0).posX, lista.get(0).posY);
					break;
				case 2:
					setControlZoom(lista.get(1).posX, lista.get(1).posY);
					setParticulasPos(lista.get(1).posX, lista.get(1).posY);
					break;
				case 3:
					setParticulasPos(lista.get(1).posX, lista.get(1).posY);
					setControlZoom(lista.get(0).posX, lista.get(0).posY);
					bufferLand.setNovoLoucuraNivel(PApplet.map(lista.get(2).posX, 0, width, 0, 1));
//					playShip.newShot(bufferLand.bufferOnPlay, bufferLand.centerLand);
					break;
				default:
					
				}
			}
			return;
		}
		
		private void setParticulasPos(float x, float y) {
			controlParticulas.activeControl(x, y);
			if (controlParticulas.showControl()){
				controlParticulas.applyControl(x, y);
				playShip.setParticulasPos(x, y, controlParticulas.getAngulo(), controlParticulas.getMagnitude());
			} 
			
			
		}
		private void setControlZoom(float x, float y) {
			controlZoom.activeControl(x, y);
			if (controlZoom.showControl()) {
				controlZoom.applyControl(x,y); //varia o nivel de zoom segundo a posiçāo do botāo
				controlZoom.setVelocitySom(); //envia a mudança de velocidade a Pd
				bufferLand.setZoomNivel(controlZoom.getPercentagemZoom() ); //da uma percentagem de zoom para desenha-lho
				bufferLand.editVisibleSection();//transforma o nivel de zoom na secçāo visivel do jogo
			}
			
		}
		
		public synchronized void drawInfo() {
	//HashSet Prevents duplicates in the collections, and given an element, can find that element in the collection quickly.
	        Set<Integer> keyList = hashList.keySet();
	//Iterator enables you to cycle through a collection, obtaining or removing elements
	        Iterator<Integer> iter = keyList.iterator();
	        int cnt = 0;
	        Point anchor = null;
	//linkedList: Designed to give better performance when you  insert or delete elements from the middle of the collection 
	        ArrayList<Point> lista = new ArrayList<Point>();
	        while(iter.hasNext()){
	        	anchor = hashList.get(iter.next());
	        	lista.add(anchor);
	        	anchor.drawIt();
	        	cnt++;
	        }
	        /*
	         * We draw now all the lines between nodes
	         */
	        if (lista.size() > 1) {
	        	Object[] arList = lista.toArray();

	        	for (int i = 0; i < arList.length; i++ ) {
	        		for (int j = i+1; j < arList.length; j++) {
	        			drawLine((Point) arList[i], (Point) arList[j]);
	        		}
	        	}
	        }
	        textSize(25);
	        text("Active elements: " + cnt,10,25);

	        return;
		}

		synchronized void drawLine(Point a, Point b) {
			line(a.posX,a.posY,b.posX,b.posY);
		}

		/**
		 * Remove item with the given id from the hashed list.
		 * @param id 
		 */
		synchronized void delete(int id) {
			if ( hashList.get(id) != null )
				hashList.remove(id);
		}
		
		/**
		 * Remove all items from the list. This happens when an ACTION_CANCEL event
		 * occurs.
		 */
		synchronized void clearMe() {
			hashList.clear();
		}

		/**
		 * Check if the given ID is in the list, and if not, inserts it.
		 * @param id
		 * @param x
		 * @param y
		 */
		synchronized void insert(int id, float x, float y) {
			if ( hashList.get(id) == null ){
				hashList.put(id, new Point(id,x,y));
			}
				
		}
		
		/**
		 * Updates the current position of the given ID
		 * @param id
		 * @param x
		 * @param y
		 */
		synchronized void update(int id, float x, float y) {
			hashList.get(id).update(x, y);
		}
	}
	
	/**
	 * This class contains the basic attributes that we are going to use
	 **/
	class Point {
		float posX,posY;
		int pointID;
		int textSize = 12;
		
		Point(int id, float x, float y) {
			pointID = id;
			posX    = x;
			posY    = y;
		}
		
		void update(float x, float y) {
			posX = x;
			posY = y;
		}
		
		void drawIt() {

        	fill(120);
        	textSize(textSize);
        	ellipse(posX,posY,150,150);
        	text("X: " + posX + " Y: " + posY, posX-100, posY-100);
        	text("ID: " + pointID, posX-100, posY-100-textSize);
        	fill(200);
        	ellipse(posX,posY,20,20);
		}
	}
}
