package com.codepath.apps.basictwitter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.basictwitter.models.Tweet;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TweetArrayAdapter extends ArrayAdapter<Tweet> {

	public TweetArrayAdapter(Context context, List<Tweet> tweets) {
		super(context, 0, tweets);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Get the data we want to display
		Tweet tweet = getItem(position);
		
		// Display the data
		View v;
		if (convertView == null) {
			LayoutInflater inflator = LayoutInflater.from(getContext());
			v = inflator.inflate(R.layout.tweet_item, parent, false);
		} else {
			v = convertView;
		}
		
		// Find the views within the template.
		ImageView ivProfileImage = (ImageView) v.findViewById(R.id.ivProfileImage);
		TextView tvUserName = (TextView) v.findViewById(R.id.tvUserName);
		TextView tvScreenName = (TextView) v.findViewById(R.id.tvScreenName);
		TextView tvDate = (TextView) v.findViewById(R.id.tvDate);
		TextView tvBody = (TextView) v.findViewById(R.id.tvBody);
		ivProfileImage.setImageResource(android.R.color.transparent);	// Clear previous image
		ImageLoader imageLoader = ImageLoader.getInstance();
		// Populate views with tweet data.
		imageLoader.displayImage(tweet.getUser().getProfileImageUrl(), ivProfileImage);
		tvUserName.setText(tweet.getUser().getName());
		tvScreenName.setText("@" + tweet.getUser().getScreenName());
		tvDate.setText(getTwitterDate(tweet.getCreatedAt()));
		tvBody.setText(tweet.getBody());
		return v;
	}

	static final long SECMILS = 1000;			// milliseconds in a second
	static final long MINMILS = 60 * SECMILS;	// milliseconds in a minute
	static final long HOURMILS = 60 * MINMILS;	// milliseconds in an hour
	static final long DAYMILS = 24 * HOURMILS;	// milliseconds in a day
	static final long WEEKMILS = 7 * DAYMILS;	// milliseconds in a week
	// Anything over a week appears at month and date and maybe year.
	
	public String getTwitterDate(String stCreateDate) {
		// "created_at": "Wed Mar 03 19:37:35 +0000 2010",
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
		Date createDate;
		
		try {
			createDate = dateFormat.parse(stCreateDate);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
			// If parsing error, display original create date string.
			return stCreateDate;
		}
		
		long createMils = createDate.getTime();
		Date today = new Date();				// Set to current time and date.
		long todayMils = today.getTime();
		long deltaMils = todayMils - createMils;
		
		if (deltaMils < MINMILS)
			return deltaMils / SECMILS + "s";
		if (deltaMils < HOURMILS)
			return deltaMils / MINMILS + "m";
		if (deltaMils < DAYMILS)
			return deltaMils / HOURMILS + "h";
		if (deltaMils < WEEKMILS)
			return deltaMils / DAYMILS + "d";

		// If we're more than a week, we display month and date.
		// Switch to GregorianCalendar to compare years
		GregorianCalendar todayCalDate = new GregorianCalendar();
		todayCalDate.setTime(today);
		int todayYear = todayCalDate.get(Calendar.YEAR);
		GregorianCalendar createCalDate = new GregorianCalendar();
		createCalDate.setTime(createDate);
		String stMonth = createCalDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US);
		int createDay = createCalDate.get(Calendar.DAY_OF_MONTH);
		int createYear = createCalDate.get(Calendar.YEAR);
		if (todayYear == createYear) {
			// Years are same so only display month and day.
			return createDay + " " + stMonth;
		} else {
			// Years different, so display month, day, and year.
			createYear %= 100;	// only display 2 least significant digits.
			return createDay + " " + stMonth + " " + createYear;
		}
	}
}
