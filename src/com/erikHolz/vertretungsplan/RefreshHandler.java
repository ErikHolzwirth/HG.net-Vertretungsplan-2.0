package com.erikHolz.vertretungsplan;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class RefreshHandler extends Handler {

	private static final int PROGRESS = 1;
	private static final int STARTED = 0;
	private static final int FINISHED = 2;
	
	ProgressDialog dialog;
	int progress;
	Context context;

	ThreadListener listener;

	public interface ThreadListener {
		public void onThreadStarted();
		public void onThreadFinished();
	}

	public void setContext(Context context) {
		this.context = context;
		listener = (ThreadListener) this.context;
	}

	public void setDialog(ProgressDialog dialog) {
		this.dialog = dialog;
	}

	public void setProgress(int value) {
		progress = value;
	}

	public void handleMessage(Message msg) {
		switch (msg.what) {
		case STARTED:
			listener.onThreadStarted();
			break;
			
		case PROGRESS:
			if (progress < 21)
				dialog.setMessage(Integer.toString(progress)
						+ "% Generieren der Linknamen...");
			else if (progress < 37)
				dialog.setMessage(Integer.toString(progress)
						+ "% Herunterladen des Vertretungsplanes...");
			else if (progress < 41)
				dialog.setMessage(Integer.toString(progress)
						+ "% Herunterladen des Essensplans...");

			else if (progress < 67)
				dialog.setMessage(Integer.toString(progress)
						+ "% Konvertieren des Vertretungsplans...");

			else if (progress < 71)
				dialog.setMessage(Integer.toString(progress)
						+ "% Konvertieren des Essenplans...");

			else if (progress < 91)
				dialog.setMessage(Integer.toString(progress)
						+ "% Speichern der Daten...");

			else if (progress < 101)
				dialog.setMessage(Integer.toString(progress)
						+ "% Speichern des Essenplans...");
			break;
		
		case FINISHED:
			dialog.dismiss();
			listener.onThreadFinished();
			break;
			
		}
	}
}
