package com.example.eventplanner;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

@SuppressLint("NewApi")
public class TimePickerFragment extends DialogFragment implements
		TimePickerDialog.OnTimeSetListener {

	public static int hour = 0;
	public static int minute = 0;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current time as the default values for the picker
		final Calendar c = Calendar.getInstance();
		c.getTime();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);

		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, hour, minute,
				DateFormat.is24HourFormat(getActivity()));
	}

	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		TimePickerFragment.hour = hourOfDay;
		TimePickerFragment.minute = minute;
		MainActivity.timeView.setText(hourOfDay + ":" + minute);
	}
}
