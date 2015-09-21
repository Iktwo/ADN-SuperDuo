package barqsoft.footballscores;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {
    private static final String TAG = WidgetProvider.class.getSimpleName() ;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            Intent wIntent = new Intent(context, WidgetService.class);

            wIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            wIntent.setData(Uri.parse(wIntent.toUri(Intent.URI_INTENT_SCHEME)));

            Log.d(TAG, "Setting remote adapter to: " + wIntent.getExtras().toString());

            view.setRemoteAdapter(R.id.listWidget, wIntent);
            view.setEmptyView(R.id.listWidget, R.id.noMatchText);

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            view.setOnClickPendingIntent(R.id.widgetTitle, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, view);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}