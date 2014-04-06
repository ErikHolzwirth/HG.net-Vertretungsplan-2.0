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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

public class DialogClassPicker extends DialogFragment {

	public interface DialogListener {
		public void onDialogPositiveClick(String userInput);
		public void onDialogNegativeClick();
	}

	DialogListener mListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// es wird überprüft, ob der Host (= MainActivity) den Listener
		// implementiert hat,
		// um die "Antworten" des Dialogs verarbeiten zu können
		try {
			// es wird versucht, den Listener anzulegen, um Antworten senden zu
			// können
			mListener = (DialogListener) activity;
		} catch (ClassCastException e) {
			// der Listener wurde nicht implementiert und das Programm wirft
			// eine Exception
			throw new ClassCastException(activity.toString()
					+ " muss den Listener implementieren!");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// der LayoutInflater wird verwendet, um das Design darzustellen
		LayoutInflater inflater = getActivity().getLayoutInflater();

		// das Layout wird aus der XML ausgelesen
		View dialogView = inflater.inflate(R.layout.dialog_classpicker, null);

		// die NumberPicker werden aus dem Layout ausgelesen, um konfiguriert
		// werden zu können
		final NumberPicker np1 = (NumberPicker) dialogView
				.findViewById(R.id.pickerClass1);
		final NumberPicker np2 = (NumberPicker) dialogView
				.findViewById(R.id.pickerClass2);

		// für die NumberPicker wird festgelegt: höchster Wert, niedrigster Wert
		// und ob der Picker
		// nach dem Höchstwert wieder zum niedrigsten springt
		np1.setMinValue(5);
		np1.setMaxValue(12);
		np1.setWrapSelectorWheel(false);
		np2.setMinValue(1);
		np2.setMaxValue(5);
		np2.setWrapSelectorWheel(false);

		// der Dialog wird "aufgebaut"
		builder.setView(dialogView)
				// Hinzufügen der Antwort - Buttons des Dialogs
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								mListener.onDialogPositiveClick(String
										.valueOf(np1.getValue())
										+ "/"
										+ String.valueOf(np2.getValue()));
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								mListener.onDialogNegativeClick();
							}
						});
		
		builder.setCancelable(false);

		return builder.create();
	}

}
