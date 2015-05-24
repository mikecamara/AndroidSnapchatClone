package org.mikecamara.ribbit;

import org.mikecamara.ribbit.ui.MainActivity;
import org.mikecamara.ribbit.utils.ParseConstants;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;

public class RibbitApplication extends Application{
	
	@Override
	public void onCreate() {
		super.onCreate();
		// Enable Local Datastore.
		
		Parse.enableLocalDatastore(this);
		 
		 Parse.initialize(this, "FSjY8kIiUczvdEHT5KEsspqYdu8yEReuEQTHafjP", "UWNPYGnRjqbz4sU3JLctk6SXLfxKsYjbGdsPSAQM");
		 
		 PushService.setDefaultPushCallback(this, MainActivity.class);
		 //to change the notification icon use the line below. 
		 //PushService.setDefaultPushCallback(this, MainActivity.class, R.drawable.ic_launcher);
		 ParseInstallation.getCurrentInstallation().saveInBackground();

	}
	
	public static void updateParseInstallation(ParseUser user) {
		ParseInstallation installation = ParseInstallation.getCurrentInstallation();
		installation.put(ParseConstants.KEY_USER_ID, user.getObjectId());
		installation.saveInBackground();
	}

}
