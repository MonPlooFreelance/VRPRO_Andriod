package vrpro.vrpro.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import vrpro.vrpro.R;

public class LoadingActivity extends AppCompatActivity {

    private final String LOG_TAG = "SerialCodeActivity";
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        try {
            sharedPref = this.getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
            boolean isActivateSerial = sharedPref.getBoolean("activateSerial", false);
            if (isActivateSerial) {
                Intent myIntent = new Intent(LoadingActivity.this, HomeActivity.class);
                LoadingActivity.this.startActivity(myIntent);
                finish();
            } else {
                Intent myIntent = new Intent(LoadingActivity.this, SerialCodeActivity.class);
                LoadingActivity.this.startActivity(myIntent);
                finish();
            }
        } catch(Exception e){
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }
}
