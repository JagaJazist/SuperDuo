package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;

/**
 * Created by rcc on 19.03.16.
 */
public class TodayWidgetIntentService extends IntentService{
    public TodayWidgetIntentService() {
        super("TodayWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // Retrieve all of the Today widget ids: these are the widgets we need to update
              AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
              int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                      TodayWidgetProvider.class));
       
              Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                              location, System.currentTimeMillis());
              Cursor data = getContentResolver().query(weatherForLocationUri, FORECAST_COLUMNS, null,
                              null, WeatherContract.WeatherEntry.COLUMN_DATE" ASC");
              if (data == null) {
                      return;
                  }
              if (!data.moveToFirst()) {
                      data.close();
                      return;
                  }
       
                      // Extract the weather data from the Cursor
                              int weatherId = data.getInt(INDEX_WEATHER_ID);
              int weatherArtResourceId = Utility.getArtResourceForWeatherCondition(weatherId);
              String description = data.getString(INDEX_SHORT_DESC);
              double maxTemp = data.getDouble(INDEX_MAX_TEMP);
              String formattedMaxTemperature = Utility.formatTemperature(this, maxTemp);
              data.close();
       
                      // Perform this loop procedure for each Today widget
                              for (int appWidgetId : appWidgetIds) {
                      int layoutId = R.layout.widget_today_small;
                      RemoteViews views = new RemoteViews(getPackageName(), layoutId);
           
                              // Add the data to the RemoteViews
                                      views.setImageViewResource(R.id.widget_icon, weatherArtResourceId);
                      // Content Descriptions for RemoteViews were only added in ICS MR1
                              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                              setRemoteContentDescription(views, description);
                          }
                      views.setTextViewText(R.id.widget_high_temperature, formattedMaxTemperature);
           
                              // Create an Intent to launch MainActivity
                                      Intent launchIntent = new Intent(this, MainActivity.class);
                      PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
                      views.setOnClickPendingIntent(R.id.widget, pendingIntent);
           
                              // Tell the AppWidgetManager to perform an update on the current app widget
                                      appWidgetManager.updateAppWidget(appWidgetId, views);
                  }
          }
   
              @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
      private void setRemoteContentDescription(RemoteViews views, String description) {
              views.setContentDescription(R.id.widget_icon, description);
          }

    }
}
