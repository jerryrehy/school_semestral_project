package cz.tul.app;

/* Jméno: Jaroslav Øehák
 * Roèník: 3.
 * Fakulta: Mechatroniky, informatiky a meziob. studií
 * Škola: TU v Liberci
 * Program Accelero v2
*/

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class App_semestralni_android extends Activity implements SensorEventListener {
   
	private static final int ACTIVITY_INTENT_RETURNTEXT = 1;
	private static final int ACTIVITY_INTENT_RETURNMAIL = 2;
	private static final int ACTIVITY_INTENT_RETURNRESULT = 3;
	
	public static StringBuilder txt_file_out = new StringBuilder();
	public static String cesta = "";
	public static Uri path= null;
	public static String mail = "";
	
	public ProgressDialog progdial;
	public TextView t, tex;
	public ScrollView tacc;
	public int postup;
	public int akce;
	
	private SensorManager sensorManager;
	double ay;   // souradnice akcelerometru y
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        Toast.makeText(this, "Zaènìte stiskem HW tlaèítka MENU",Toast.LENGTH_LONG).show();
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item1:
			akce = 0;
			Intent intent1 = new Intent(App_semestralni_android.this, FileManager.class);
			startActivityForResult(intent1, ACTIVITY_INTENT_RETURNTEXT);
			return true;
		case R.id.item2:
			akce = 1;
			Intent intent2 = new Intent(App_semestralni_android.this, FileManager.class);
			startActivityForResult(intent2, ACTIVITY_INTENT_RETURNTEXT);
			return true;
		case R.id.item3:		
			Dialog di = new Dialog(this);
			TextView tvi = new TextView(this);
			tvi.setBackgroundColor(Color.TRANSPARENT);
			tvi.setText("Copyright: Øehák Jaroslav\n\nFunkce:\n\t\todeslání pošty\n\t\tsouborový manažer\n\t\tposouvání textu naklonìním\n\t\tvýbìr e-adresy z kontaktu\n\nVerze: Alpha 2.0.0");
			di.setTitle("O aplikaci Semestral Accelero v2 :D");
			di.setContentView(tvi);
			di.show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private Runnable meVlakno = new Runnable() {
		
		@Override
		public void run() {
			postup = 0;
			while (true) {
				try {
					handler1.sendMessage(handler1.obtainMessage());
					Thread.sleep(3000);
				} catch (Throwable t) {}
				if (postup >= 2) {
					return;
				}
			}
		}

		Handler handler1 = new Handler() {
			
			@Override
			public void handleMessage(Message msg) {
				postup++;
				if (postup >= 2) {
					progdial.cancel();
					try {
						zobraz_text();
					} catch (IOException e) {
						e.printStackTrace();
					}
					t.setVisibility(0);
					postup = 3;

				}

			}
		};
	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case ACTIVITY_INTENT_RETURNTEXT:
				if (resultCode == RESULT_OK) {
					if(akce == 0) {
						Bundle extras = data.getExtras();
						cesta = extras.getString(FileManager.INTENT_BUNDLE_TEXT);
						progdial = ProgressDialog.show(this, "Naèítání souboru", "naèítám...");
						new Thread(meVlakno).start();
					}
					if(akce == 1) {
						Bundle extras = data.getExtras();
						cesta = extras.getString(FileManager.INTENT_BUNDLE_TEXT);
						Intent intent3 = new Intent(App_semestralni_android.this, VyberKontakt.class);
						startActivityForResult(intent3, ACTIVITY_INTENT_RETURNMAIL);
						Toast.makeText(this, mail, Toast.LENGTH_LONG);
					}
				}
				break;
			case ACTIVITY_INTENT_RETURNMAIL: 
				if (resultCode == RESULT_OK) {
					Bundle obsah = data.getExtras();
					mail = obsah.getString(VyberKontakt.INTENT_BUNDLE_MAIL);
					path = Uri.parse("file://mnt" + cesta);
					Intent result = new Intent(App_semestralni_android.this, OdeslatEmail.class);        
                	startActivityForResult(result,ACTIVITY_INTENT_RETURNRESULT);
				}
				break;
			case ACTIVITY_INTENT_RETURNRESULT:
				if(resultCode == RESULT_OK){
					Toast.makeText(this, "Email byl úspìšnì odeslán.", Toast.LENGTH_LONG).show();
				}
				if(resultCode == RESULT_CANCELED){
					Toast.makeText(this, "Email se neodeslal!", Toast.LENGTH_LONG).show();
				}
				txt_file_out.delete(0, txt_file_out.length());
				break;
			}
	}
	
	private void zobraz_text() throws IOException {
		String s;
		try {
			BufferedReader buf = new BufferedReader(new FileReader(cesta));
			while ((s = buf.readLine()) != null) {
				txt_file_out.append(s);
				txt_file_out.append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		t = (TextView) findViewById(R.id.textView1);
		t.setText(txt_file_out);
		t.setVisibility(1);
		txt_file_out.delete(0, txt_file_out.length());
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
        	tacc = (ScrollView) findViewById(R.id.scrollView1);
        	ay=event.values[1]-6;
        	tacc.smoothScrollBy(0, (int)ay*10);
        }
	}
}