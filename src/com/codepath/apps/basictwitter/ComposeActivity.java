package com.codepath.apps.basictwitter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class ComposeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compose);
	}
	
	public void onClickSend(View v) {
		EditText etMessage = (EditText) findViewById(R.id.etMessage);
		// Prepare data intent 
		Intent data = new Intent();
		// Pass relevant data back as a result
		data.putExtra("message", etMessage.getText().toString());
		// Activity finished ok, return the data
		setResult(RESULT_OK, data); // set result code and bundle data for response
		finish(); // closes the activity, pass data to parent
	} 
}
