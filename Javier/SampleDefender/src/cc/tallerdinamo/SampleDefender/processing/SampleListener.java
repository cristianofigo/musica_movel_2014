package cc.tallerdinamo.SampleDefender.processing;

import org.puredata.core.PdListener;

import android.util.Log;

public class SampleListener implements PdListener {
	
	SampleListener(){
	}

	@Override
	public void receiveBang(String source) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveFloat(String source, float x) {
		// TODO Auto-generated method stub
		Log.i("Mensaje de Entrada", "posiçāo: " + x);
	}

	@Override
	public void receiveSymbol(String source, String symbol) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveList(String source, Object... args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveMessage(String source, String symbol, Object... args) {
		// TODO Auto-generated method stub
		
	}
	
	
}
