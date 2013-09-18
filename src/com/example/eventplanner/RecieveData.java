package com.example.eventplanner;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;

public class RecieveData extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recieve_data);
		Intent intent1 = this.getIntent();

		if (intent1 != null) {
			String eventName = null;
			String strdata = intent1.getExtras().getString("jsonData");
			Map<String, String> map = new HashMap<String, String>();
			try {
				JSONObject obj = new JSONObject(strdata);
				eventName = obj.getString("eventName");
				JSONArray jarr = new JSONArray(obj.getString("contactList"));
				for (int ix = 0; ix < jarr.length(); ix++) {
					JSONObject obj1 = (JSONObject) jarr.get(ix);
					map.put(obj1.getString("Name"),
							obj1.getString("PhoneNumber"));
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			long ms = Long.parseLong(intent1.getExtras().getString("time"));
			String latlong = intent1.getExtras().getString("latlong");
			createEvent(ms, eventName, map, latlong);

		}
	}

	void createEvent(long ms, String eventName, Map<String, String> map,
			String latlong) {

		String eventUriString = "content://com.android.calendar/events";
		ContentValues eventValues = new ContentValues();

		eventValues.put("calendar_id", 1); // id, We need to choose from
											// our mobile for primary
											// its 1
		eventValues.put("title", eventName);
		eventValues.put("eventLocation", "https://maps.google.com/?q=loc:"
				+ latlong);

		eventValues.put("dtstart", ms);
		eventValues.put("eventTimezone", TimeZone.getDefault().getID());

		long endDate = ms + 1000 * 60 * 60; // For next 1hr
		eventValues.put("dtend", endDate);

		eventValues.put("eventStatus", 0); // This information is
		eventValues.put("hasAlarm", 1); // 0 for false, 1 for true

		Uri eventUri = getApplicationContext().getContentResolver().insert(
				Uri.parse(eventUriString), eventValues);

		long eventID = Long.parseLong(eventUri.getLastPathSegment());

		String attendeuesesUriString = "content://com.android.calendar/attendees";

		/********
		 * To add multiple attendees need to insert ContentValues multiple times
		 ***********/
		for (String name : map.keySet()) {
			ContentValues attendeesValues = new ContentValues();
			attendeesValues.put("event_id", eventID);
			attendeesValues.put("attendeeName", name); // Attendees
			attendeesValues.put("attendeeRelationship", 0); // Relationship_Attendee(1),
			attendeesValues.put("attendeeStatus", 0); // NOne(0), Accepted(1),
			attendeesValues.put("attendeeEmail", name + "@gmail.com");// A
			getApplicationContext().getContentResolver().insert(
					Uri.parse(attendeuesesUriString), attendeesValues);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recieve_data, menu);
		return true;
	}

}
