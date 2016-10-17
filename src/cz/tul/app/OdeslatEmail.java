package cz.tul.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class OdeslatEmail extends Activity {

	public static int result=0;	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Odeslat(this,App_semestralni_android.mail, App_semestralni_android.path);
		
		Intent vysledek = new Intent();
		if(result==1){
			setResult(RESULT_OK,vysledek);
		}else{
			setResult(RESULT_CANCELED,vysledek);
		}		
		finish();	
	}
		
	public void Odeslat(Context context, String komu, Uri cesta){
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setType("text/plain");
		emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{komu});
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Textový soubor");
		emailIntent.putExtra(Intent.EXTRA_STREAM, cesta);
		emailIntent.putExtra(Intent.EXTRA_TEXT, "Email s pøílohou textového souboru, odeslaného ze zaøízení s OS Android.");
		try{		
			context.startActivity(Intent.createChooser(emailIntent, "Poslat soubor..."));
			result=1;
		} catch (Exception e) {
			result=0;
			e.printStackTrace();
		}	
	}
}