package org.mikecamara.ribbit.ui;

import java.util.Timer;
import java.util.TimerTask;

import org.mikecamara.ribbit.R;
import org.mikecamara.ribbit.R.id;
import org.mikecamara.ribbit.R.layout;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ViewImageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_image);
		// show the up button in the action bar
		setupActionBar();

		ImageView imageView = (ImageView) findViewById(R.id.imageView);
		Uri imageUri = getIntent().getData();

		Picasso.with(this).load(imageUri.toString()).into(imageView);
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				finish();
			}
		}, 10*1000);

	}

	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
