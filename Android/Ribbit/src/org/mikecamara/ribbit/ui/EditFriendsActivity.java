package org.mikecamara.ribbit.ui;

import java.util.List;

import org.mikecamara.ribbit.R;
import org.mikecamara.ribbit.adapters.UserAdapter;
import org.mikecamara.ribbit.utils.ParseConstants;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class EditFriendsActivity extends Activity {

	public static final String TAG = EditFriendsActivity.class.getSimpleName();

	protected ParseRelation<ParseUser> mFriendsRelation;
	protected List<ParseUser> mUsers;
	protected ParseUser mCurrentUser;
	protected GridView mGridView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.user_grid);
		setupActionBar();

		mGridView = (GridView) findViewById(R.id.friendsGrid);
		mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
		mGridView.setOnItemClickListener(mOnItemClickListener);

		TextView emptyTextView = (TextView) findViewById(android.R.id.empty);
		mGridView.setEmptyView(emptyTextView);
	}

	@Override
	protected void onResume() {
		super.onResume();

		mCurrentUser = ParseUser.getCurrentUser();
		mFriendsRelation = mCurrentUser
				.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

		setProgressBarIndeterminateVisibility(true);

		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.orderByAscending(ParseConstants.KEY_USERNAME);
		query.setLimit(1000);
		query.findInBackground(new FindCallback<ParseUser>() {

			@Override
			public void done(List<ParseUser> users, ParseException e) {
				setProgressBarIndeterminateVisibility(false);

				if (e == null) {
					// success
					mUsers = users;
					String[] userNames = new String[mUsers.size()];
					int i = 0;
					for (ParseUser user : mUsers) {
						userNames[i] = user.getUsername();
						i++;
					}

					if (mGridView.getAdapter() == null) {

						UserAdapter adapter = new UserAdapter(
								EditFriendsActivity.this, mUsers);
						mGridView.setAdapter(adapter);
					} else {
						((UserAdapter) mGridView.getAdapter()).refill(mUsers);
					}
					addFriendCheckmarks();

				} else {
					Log.e(TAG, e.getMessage());
					AlertDialog.Builder builder = new AlertDialog.Builder(
							EditFriendsActivity.this);
					builder.setMessage(e.getMessage())
							.setTitle(R.string.error_title)
							.setPositiveButton(android.R.string.ok, null);

					AlertDialog dialog = builder.create();
					dialog.show();
				}
			}
		});
	}

	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:

			// Handle action bar item clicks here. The action bar will
			// automatically handle clicks on the Home/Up button, so long
			// as you specify a parent activity in AndroidManifest.xml.
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void addFriendCheckmarks() {
		mFriendsRelation.getQuery().findInBackground(
				new FindCallback<ParseUser>() {

					@Override
					public void done(List<ParseUser> friends, ParseException e) {
						if (e == null) {
							// list returned - look for a match
							for (int i = 0; i < mUsers.size(); i++) {
								ParseUser user = mUsers.get(i);

								for (ParseUser friend : friends) {
									if (friend.getObjectId().equals(
											user.getObjectId())) {
										mGridView.setItemChecked(i, true);
									}
								}
							}
						} else {
							Log.e(TAG, e.getMessage());
						}
					}
				});
	}

	protected OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			ImageView checkImageView = (ImageView) view.findViewById(R.id.checkImageView);

			if (mGridView.isItemChecked(position)) {
				// add friends
				mFriendsRelation.add(mUsers.get(position));
				checkImageView.setVisibility(View.VISIBLE);
			} else {
				// remove friend
				mFriendsRelation.remove(mUsers.get(position));
				checkImageView.setVisibility(View.INVISIBLE);
			}

			mCurrentUser.saveInBackground(new SaveCallback() {

				@Override
				public void done(ParseException e) {
					if (e != null) {
						Log.e(TAG, e.getMessage());
					}

				}
			});
		}
	};
}
