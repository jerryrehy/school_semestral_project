package cz.tul.app;

import java.io.InputStream;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import cz.tul.app.ContactDTO;

public class DataProvider {
	public static ContactDTO lookupContact(Context ctx, Uri contactUri) {
		String[] projection = new String[] { ContactsContract.Contacts._ID,
				ContactsContract.Contacts.DISPLAY_NAME, };
		ContactDTO dto = null;
		Cursor c = ctx.getContentResolver().query(contactUri, projection, null,
				null, null);
		if (c != null && c.moveToFirst()) {
			dto = new ContactDTO(c.getLong(0), c.getString(1));
			dto.email = getEmail(ctx, dto.id);
		}
		if (c != null) {
			c.close();
		}
		return dto;
	}

	private static String getEmail(Context ctx, long contactId) {
		Cursor cursor = ctx.getContentResolver().query(
				ContactsContract.CommonDataKinds.Email.CONTENT_URI,
				new String[] { ContactsContract.CommonDataKinds.Email.DATA },
				ContactsContract.CommonDataKinds.Email.CONTACT_ID + "= ?",
				new String[] { String.valueOf(contactId) }, null);
		try {
			if (cursor.moveToFirst()) {
				return cursor.getString(0);
			} else {
				return null;
			}
		} finally {
			cursor.close();
		}
	}

	public static InputStream openPhoto(Context ctx, long contactId) {
		Uri contactUri = Uri.withAppendedPath(
				ContactsContract.Contacts.CONTENT_URI,
				String.valueOf(contactId));
		return ContactsContract.Contacts.openContactPhotoInputStream(
				ctx.getContentResolver(), contactUri);
	}
}