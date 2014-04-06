package com.erikHolz.vertretungsplan;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {

	public static final String PREF_KEY_BACKGROUNDDATA = "pref_key_backgrounddata";
	public static final String PREF_KEY_BACKGROUNDDATA_INTERVALL = "pref_key_refresh_intervall";
	public static final String PREF_KEY_NOTIFICATION = "pref_key_notification";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.layout.settings);

		Preference connectionPref1, connectionPref2, connectionPref3;
		
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity()
						.getApplicationContext());

		if (sharedPreferences.getBoolean(PREF_KEY_BACKGROUNDDATA, false)) {
			connectionPref1 = findPreference(PREF_KEY_BACKGROUNDDATA);
			connectionPref2 = findPreference(PREF_KEY_BACKGROUNDDATA_INTERVALL);

			connectionPref1
					.setSummary("Der Download neuer Daten im Hintergrund ist derzeit aktiviert.");

			if (!sharedPreferences.getString(PREF_KEY_BACKGROUNDDATA_INTERVALL,
					"").equals(""))
				connectionPref2.setSummary("Derzeit werden alle "
						+ sharedPreferences.getString(
								PREF_KEY_BACKGROUNDDATA_INTERVALL, "")
						+ " Minuten neue Daten bezogen.");
		}
		
		if (sharedPreferences.getBoolean(PREF_KEY_NOTIFICATION, false)) {
			connectionPref3 = findPreference(PREF_KEY_NOTIFICATION);

			connectionPref3
					.setSummary("Die Anzeige von Benachrichtigungen ist momentan aktiv.");
		}

		

	}

	@Override
	public void onResume() {
		super.onResume();
		// Set up a listener whenever a key changes
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		// Unregister the listener whenever a key changes
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {

		if (key.equals(PREF_KEY_BACKGROUNDDATA)) {
			Preference connectionPref = findPreference(key);

			if (sharedPreferences.getBoolean(PREF_KEY_BACKGROUNDDATA, false))
				connectionPref
						.setSummary("Der Download von Daten im Hintergrund ist momentan aktiviert.");

			else {
				connectionPref
						.setSummary("Der Download von Daten im Hintergrund ist momentan deaktiviert.");
			}
		}

		else if (key.equals(PREF_KEY_BACKGROUNDDATA_INTERVALL)) {
			Preference connectionPref = findPreference(key);

			if (sharedPreferences.getBoolean(PREF_KEY_BACKGROUNDDATA, false))
				connectionPref.setSummary("Derzeit werden alle "
						+ sharedPreferences.getString(
								PREF_KEY_BACKGROUNDDATA_INTERVALL, "")
						+ " Minuten neue Daten bezogen.");

			else {
				connectionPref
						.setSummary("Es werden keine neuen Daten im Hintergrund bezogen.");
			}
		}

		else if (key.equals(PREF_KEY_NOTIFICATION)) {
			Preference connectionPref = findPreference(key);

			if (sharedPreferences.getBoolean(PREF_KEY_NOTIFICATION, false))
				connectionPref
						.setSummary("Die Anzeige von Benachrichtigungen ist momentan aktiv.");

			else {
				connectionPref
						.setSummary("Die Anzeige von Benachrichtigungen ist momentan nicht aktiv.");
			}
		}
	}
}
