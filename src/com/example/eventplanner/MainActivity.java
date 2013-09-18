package com.example.eventplanner;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity {

	private static final int RQS_PICK_CONTACT = 1;
	public Map<String, String> contacts = new HashMap<String, String>();

	private TextView contactNameView;
	public EditText eventName;
	public static TextView timeView;
	public static TextView dateView;
	public static TextView latlong;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		contactNameView = (TextView) findViewById(R.id.contactName);
		timeView = (TextView) findViewById(R.id.pickTime);
		dateView = (TextView) findViewById(R.id.pickDate);
		eventName = (EditText) findViewById(R.id.event_name);
		latlong = (TextView) findViewById(R.id.latlong);

		Button buttonPickContact = (Button) findViewById(R.id.pickcontact);
		buttonPickContact.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
				startActivityForResult(intent, 1);

			}
		});

	}

	public void sendData(View v) throws Exception {
		Intent intent = new Intent(this, RecieveData.class);
		Timestamp t = new Timestamp(DatePickerFragment.year - 1900,
				DatePickerFragment.month - 1, DatePickerFragment.day,
				TimePickerFragment.hour, TimePickerFragment.minute, 0, 0);

		JSONObject obj = new JSONObject();
		JSONArray jarr = new JSONArray();
		for (String key : contacts.keySet()) {
			JSONObject temp = new JSONObject();
			temp.put("Name", key);
			temp.put("PhoneNumber", contacts.get(key));
			jarr.put(temp);
		}
		obj.put("timeStamp", t.getTime() + "");
		obj.put("contactList", jarr);
		obj.put("eventName", eventName.getText().toString());
		intent.putExtra("latlong", latlong.getText());
		intent.putExtra("jsonData", obj.toString());
		intent.putExtra("time", t.getTime() + "");
		MainActivity.this.startActivity(intent);
	}

	public void showTimePickerDialog(View v) {
		DialogFragment newFragment = new TimePickerFragment();
		newFragment.show(getFragmentManager(), "timePicker");
	}

	public void selectLocation(View v) {
		Intent intent = new Intent(this, com.example.mapsv2.MainActivity.class);
		startActivityForResult(intent, 2);
	}

	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getFragmentManager(), "datePicker");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RQS_PICK_CONTACT) {
			if (resultCode == RESULT_OK) {
				Uri contactData = data.getData();
				Cursor cursor = managedQuery(contactData, null, null, null,
						null);
				cursor.moveToFirst();

				String number = cursor
						.getString(cursor
								.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
				String name = cursor
						.getString(cursor
								.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
				contacts.put(name, number);

				contactNameView.setText(contacts.keySet().toString());
			}
		} else if (requestCode == 2) {
			if (resultCode == RESULT_OK) {
				latlong.setText(data.getExtras().getDouble("latitude") + ","
						+ data.getExtras().getDouble("longitude"));
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
