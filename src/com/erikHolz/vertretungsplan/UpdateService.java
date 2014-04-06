package com.erikHolz.vertretungsplan;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.erikHolz.vertretungsplan.database.DatabaseHelper;

public class UpdateService extends Service {
	public static final String PREFS_NAME = "UserSettings";
	protected static final int FINISHED = 1;
	private static final int NOTIFICATION_ID = 1337;
	DatabaseHelper db;
	SharedPreferences sharedPref;

	SharedPreferences settings;

	@Override
	public void onCreate() {
		super.onCreate();
		db = new DatabaseHelper(this);
		settings = getSharedPreferences(PREFS_NAME, 0);
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {

		// Download nur, wenn Benachrichtigungen gewollt sind und die App
		// momentan inaktiv ist
		if (!(settings.getBoolean("isRunning", true))
				&& sharedPref.getBoolean(
						SettingsFragment.PREF_KEY_BACKGROUNDDATA, false)) {

			Thread downloadThread = new Thread(null, new Runnable() {

				@Override
				public void run() {
					DownloadTask dt = new DownloadTask(db);

					db.clearDatabaseData(true);
					db.clearDatabaseData(false);

					dt.createLinknames();
					dt.downloadFiles();
					dt.downloadMenu();
					dt.convertPDF();
					dt.convertMenu();
					dt.insertData();
					dt.insertMenu();

					if (sharedPref.getBoolean(
							SettingsFragment.PREF_KEY_NOTIFICATION, false))
						createNotification();

				}
			}, "backgroundDownload");

			downloadThread.start();

		}
	}

	public void createNotification() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		String klasse = settings.getString("lastClass", "");
		int anzahlHeute = settings.getInt("amountToday", 0);
		int anzahlMorgen = settings.getInt("amountTomorrow", 0);

		if (db.getAmountByClass(klasse, true) > anzahlHeute
				|| db.getAmountByClass(klasse, false) > anzahlMorgen) {

			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					this).setSmallIcon(R.drawable.ic_notification)
					.setContentTitle("Vertretungsplan")
					.setContentText("Es gibt neue Vertretungen!");
			
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			// mId allows you to update the notification later on.
			mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

		}
	}

}