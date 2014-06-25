package com.codepath.apps.basictwitter;

import java.util.ArrayList;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.apps.basictwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

// TimelineActivity is not the startup activity for the application.
// However, this is the main screen that the user is in, so this could 
// have been named MainActivity. It is from this view that we click the 
// compose action bar item, so this is where we return when the compose
// activity is finished. So we can send the tweet from here, just as we
// send the timeline requests from here.

public class TimelineActivity extends Activity {
	private TwitterClient client;
	private ArrayList<Tweet> tweets;
	private ArrayAdapter<Tweet> aTweets;
	private ListView lvTweets;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		client = TwitterApplication.getRestClient();
		populateTimeline();
		lvTweets = (ListView) findViewById(R.id.lvTweets);
		tweets = new ArrayList<Tweet>();
		aTweets = new TweetArrayAdapter(this, tweets);
		lvTweets.setAdapter(aTweets);
	}
	
	public void populateTimeline() {
		client.getHomeTimeline(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray json) {
				aTweets.addAll(Tweet.fromJSONArray(json));
			}
			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("debug", e.toString());
				Log.d("debug", s.toString());
			}
		});
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.compose, menu);
        return true;
    }
    
    
    private final int REQUEST_CODE = 73337;

    public void onComposeAction(MenuItem mi) {
		Intent i = new Intent(this, ComposeActivity.class);
		// No extras to put going in.
		   startActivityForResult(i, REQUEST_CODE);
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// REQUEST_CODE is defined above
    	if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
    		// Extract name value from result extras
    		String stMessage = data.getExtras().getString("message");
    		// Toast the name to display temporarily on screen
    		//         Toast.makeText(this, stToUser, Toast.LENGTH_SHORT).show();

/////    		// Before sending out our network request, make sure network is available.
/////    		if (isNetworkAvailable() && isOnline()) {



    			// Real task is to send out tweet:
    			client.postTweet(new JsonHttpResponseHandler() {
    				@Override
    				public void onSuccess(JSONArray json) {
    					Log.d("debug", "Success sending Tweet!");
//    					aTweets.addAll(Tweet.fromJSONArray(json));
    				}
    				@Override
    				public void onFailure(Throwable e, String s) {
    					Log.d("debug", e.toString());
    					Log.d("debug", s.toString());
    				}
    			}, stMessage);


/////    		} else {
/////    			// ToDo: put up dialog that we have no connectivity.
/////    			Log.d("debug", "NETWORK NOT AVAILABLE!");
/////    		}
    	}
    }
    
    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager 
              = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public Boolean isOnline() {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal==0);
            return reachable;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
