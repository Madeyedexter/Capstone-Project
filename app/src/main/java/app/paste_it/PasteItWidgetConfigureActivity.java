package app.paste_it;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.prefs.PreferenceChangeEvent;

import app.paste_it.models.Paste;
import app.paste_it.models.WidgetPreference;

/**
 * The configuration screen for the {@link PasteItWidget PasteItWidget} AppWidget.
 */
public class PasteItWidgetConfigureActivity extends Activity {

    public static final String PREFS_NAME = "app.paste_it.PasteItWidget";
    public static final String PREF_PREFIX_KEY = "appwidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    EditText mAppWidgetText;
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = PasteItWidgetConfigureActivity.this;

            String noOfItems = mAppWidgetText.getText().toString();
            int itemCount;
            try{
                itemCount = Integer.parseInt(noOfItems);
                if(itemCount < 3 || itemCount > 8){
                    mAppWidgetText.setError(getString(R.string.widget_item_count_error));
                    return;
                }
            }
            catch (NumberFormatException nfe){
                mAppWidgetText.getText().clear();
                mAppWidgetText.setError(getString(R.string.widget_item_count_error));
                return;
            }
            WidgetPreference widgetPreference = new WidgetPreference(itemCount);

            updateWidgetPreference(mAppWidgetId, widgetPreference);

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            PasteItWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    private void updateWidgetPreference( int appWidgetId, WidgetPreference widgetPreference) {
        String key = PREF_PREFIX_KEY+appWidgetId;
        Gson gson = new GsonBuilder().create();
        SharedPreferences.Editor editor =getSharedPreferences(PREFS_NAME,0).edit();
        editor.putString(key,gson.toJson(widgetPreference));
        editor.apply();
    }


    public PasteItWidgetConfigureActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.paste_it_widget_configure);
        mAppWidgetText = (EditText) findViewById(R.id.appwidget_text);
        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
    }
}

