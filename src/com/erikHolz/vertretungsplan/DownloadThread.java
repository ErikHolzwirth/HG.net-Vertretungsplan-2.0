/*
    Copyright (C) 2014  Erik Holzwirth

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.erikHolz.vertretungsplan;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Message;

import com.erikHolz.vertretungsplan.database.DatabaseHelper;

public class DownloadThread extends Thread {

	private static final int FINISHED = 2;
	private static final int STARTED = 0;
	
	ThreadGroup group;
	String threadName;
	RefreshHandler handler;
	DatabaseHelper db;
	Context context;
	ProgressDialog dialog;

	public DownloadThread(ThreadGroup group, String threadName,
			final RefreshHandler handler, final DatabaseHelper db,
			final Context context) {
		this.group = group;
		this.threadName = threadName;
		this.handler = handler;
		this.db = db;
		this.context = context;
		
		Message msg = handler.obtainMessage(STARTED);
		handler.sendMessage(msg);

		handler.post(new Runnable() {
			public void run() {
				
				dialog = new ProgressDialog(context,
						ProgressDialog.THEME_HOLO_DARK);
				dialog.setMessage("Aktualisieren der Daten...");
				dialog.show();
				dialog.setCancelable(false);

				handler.setDialog(dialog);

			}
		});

	}

	public void run() {
		DownloadTask dt = new DownloadTask(db, handler);

		dt.createLinknames();
		dt.downloadFiles();
		dt.downloadMenu();
		dt.convertPDF();
		dt.convertMenu();
		dt.insertData();
		dt.insertMenu();
		
		Message msg = handler.obtainMessage(FINISHED);
		handler.sendMessage(msg);
		
	}

}
