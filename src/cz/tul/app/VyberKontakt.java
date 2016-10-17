package cz.tul.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Toast;

public class VyberKontakt extends Activity {
	private static final int ACTIVITY_PICK_CONTACT = 42;
	public static String INTENT_BUNDLE_MAIL = "mail";
	private static Uri pickedContact;
	private static ContactDTO contact;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (contact == null) { pickContact(); }
	}

	private void pickContact() {
		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(intent, ACTIVITY_PICK_CONTACT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case (ACTIVITY_PICK_CONTACT):
			if (resultCode == Activity.RESULT_OK) {
				pickedContact = data.getData();
				ContactDTO detail = DataProvider.lookupContact(this, pickedContact);
				Intent appResultIntent = new Intent();
				appResultIntent.putExtra(INTENT_BUNDLE_MAIL, detail.email);
				setResult(RESULT_OK, appResultIntent);
				Toast.makeText(this, "E-Mailová adresa kontaktu byla vybrána: " + detail.email , Toast.LENGTH_LONG).show();
			}
			finish();
		}
	}
}