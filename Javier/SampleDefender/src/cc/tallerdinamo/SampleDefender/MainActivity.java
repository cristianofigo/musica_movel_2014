package cc.tallerdinamo.SampleDefender;

import cc.tallerdinamo.SampleDefender.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class MainActivity extends Activity {
	
	String startExample = "cargaSample3"; //default
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

//Seleçāo do Sample para executar
	ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
		            this, R.array.samples, android.R.layout.simple_spinner_item);
	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	
	Spinner spinnerSamples = (Spinner)this.findViewById(R.id.samplespinner);
	spinnerSamples.setAdapter(adapter);
	spinnerSamples.setOnItemSelectedListener(new OnItemSelectedListener(){
		public void onItemSelected(AdapterView<?> parent,
	    View view, int pos, long id) {
		startExample = parent.getItemAtPosition(pos).toString();
		}
		public void onNothingSelected(AdapterView<?> arg0) {
				//nothing
		}
	});
	
//set up o botāo de lançamento
    Button bP = (Button)this.findViewById(R.id.button1);
  //Codigo para escutar o toque no botāo 1 e abrir o sketch de processing
    bP.setOnClickListener(new OnClickListener(){
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			Intent intent = new Intent();
			intent.setClassName("cc.tallerdinamo.SampleDefender", 
							    "cc.tallerdinamo.SampleDefender.processing.PAppletActivity"
					);
			intent.putExtra("SampleToView",startExample);
			startActivity(intent);
			break;
		}}
    });
	}
}
