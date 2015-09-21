package barqsoft.footballscores;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WidgetService extends RemoteViewsService {
    private static final String TAG = WidgetService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor mCursor = null;
            private Context mContext = getApplicationContext();

            @Override
            public void onCreate() {
            }

            @Override
            public void onDataSetChanged() {
                Log.d(TAG, "onDataSetChanged");
                if (mCursor != null) {
                    mCursor.close();
                }

                final long identityToken = Binder.clearCallingIdentity();

                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                Date date = new Date(System.currentTimeMillis());
                String fragmentDate = dateFormat.format(date);

                mCursor = getContentResolver().query(DatabaseContract.scores_table.buildScoreWithDate(),
                        null, null, new String[]{fragmentDate}, null);

                // Log.d(TAG, "items: " + mCursor.getCount());

                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (mCursor != null) {
                    mCursor.close();
                    mCursor = null;
                }
            }

            @Override
            public int getCount() {
                return mCursor == null ? 0 : mCursor.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION || mCursor == null || !mCursor.moveToPosition(position)) {
                    return null;
                }

                Log.d(TAG, "calling getViewAt " + Integer.toString(position));

                RemoteViews view = new RemoteViews(getPackageName(), R.layout.widget_match);

                String home = mCursor.getString(ScoresAdapter.COL_HOME);
                String away = mCursor.getString(ScoresAdapter.COL_AWAY);
                String date = mCursor.getString(ScoresAdapter.COL_MATCHTIME);

                Log.d(TAG, home + " - " + away + " at " + date);

                String score = Utilies.getScores(mCursor.getInt(ScoresAdapter.COL_HOME_GOALS), mCursor.getInt(ScoresAdapter.COL_AWAY_GOALS));

                int homeCrestResId = Utilies.getTeamCrestByTeamName(mCursor.getString(ScoresAdapter.COL_HOME));
                int awayCrestRestId = Utilies.getTeamCrestByTeamName(mCursor.getString(ScoresAdapter.COL_AWAY));
                int matchId = (int) mCursor.getDouble(ScoresAdapter.COL_ID);

                view.setImageViewResource(R.id.home_crest, homeCrestResId);
                view.setImageViewResource(R.id.away_crest, awayCrestRestId);
                view.setTextViewText(R.id.home_name, home);
                view.setTextViewText(R.id.away_name, away);
                view.setTextViewText(R.id.data_textview, date);
                view.setTextViewText(R.id.score_textview, score);

                Intent intent = new Intent();
                intent.putExtra(Utilies.SELECTED_MATCH, matchId);

                view.setOnClickFillInIntent(R.id.linear_layout_widget, intent);

                return view;
            }


            @Override
            public RemoteViews getLoadingView() {
                return null;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}