package com.miz.functions;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.miz.mizuu.UpdateMovieService;
import com.miz.mizuu.UpdateShowsService;

public class ScheduledUpdatesAlarmManager {

	public static final String prefNextMovies = "nextScheduledMovieUpdate", prefNextShows = "nextScheduledShowsUpdate";
	public static final int MOVIES = 1, SHOWS = 2;

	public static void cancelUpdate(int type, Context context) {
		try {
			AlarmManager alarmMan = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

			Intent defineIntent = new Intent(context, (type == MOVIES) ? UpdateMovieService.class : UpdateShowsService.class);
			defineIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent piWakeUp = PendingIntent.getService(context,0, defineIntent, PendingIntent.FLAG_NO_CREATE);

			if(piWakeUp != null)
				alarmMan.cancel(piWakeUp);
			
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
			Editor editor = settings.edit();
			editor.putLong((type == MOVIES) ? prefNextMovies : prefNextShows, 0);
			editor.commit();				
		} catch (Exception ignored) {}
	}

	public static void startUpdate(int type, Context context, long when) {
		AlarmManager alarmMan = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		Intent defineIntent = new Intent(context, (type == MOVIES) ? UpdateMovieService.class : UpdateShowsService.class);
		defineIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent piWakeUp = PendingIntent.getService(context,0, defineIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		if (when > -1) {
			alarmMan.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + when, piWakeUp);
			
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
			Editor editor = settings.edit();
			editor.putLong((type == MOVIES) ? prefNextMovies : prefNextShows, System.currentTimeMillis() + when);
			editor.commit();
		}
	}
}