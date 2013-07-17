package com.marakana.android.helloleak;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Register the Activity as the button click listener
		Button buttonDoit = (Button) findViewById(R.id.button_doit);
		buttonDoit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// The user clicked the Do it! button. Start a long-running background
		// operation.
		new OperationTask().execute();
	}

	public void showResult(boolean result) {
		// Post a Toast with the result
		Toast.makeText(this, "The result was: " + result, Toast.LENGTH_LONG)
				.show();
	}

	/*
	 * This non-static inner class is the subtle cause of a memory leak. The
	 * Java compiler provides an implicit reference to the containing object, in
	 * this case the MainActivity instance. This provides the code in the inner
	 * class access to the fields and methods of the containing object,
	 * including the private fields and methods.
	 * 
	 * Unfortunately, if the system tries to destroy the instance of the
	 * activity and release its reference for it to be garbage collected -- for
	 * example, because of a runtime configuration change (e.g., an orientation
	 * change) -- the OperationTask maintains a reference that keeps the
	 * activity from being garbage collected. The OperationTask is kept in
	 * memory by the Thread object it creates internally to run its
	 * doInBackground() method code. Only when the doInBackground() method
	 * returns will the Thread terminate, thus releasing its reference to the
	 * OperationTask object, which in turn finally releases its reference to the
	 * old MainActivity object.
	 */
	class OperationTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			// Emulate an operation that takes a long time to complete, running
			// on a worker thread.
			boolean result = true;
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				// We were interrupted
				result = false;
			}
			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// On the main thread, update the UI with the result
			showResult(result);
		}

	}

}
