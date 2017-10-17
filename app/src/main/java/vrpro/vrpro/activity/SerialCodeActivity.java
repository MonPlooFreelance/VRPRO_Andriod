package vrpro.vrpro.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.szagurskii.patternedtextwatcher.PatternedTextWatcher;

import java.io.IOException;

import vrpro.vrpro.R;
import vrpro.vrpro.util.PropertiesUtils;

public class SerialCodeActivity extends AppCompatActivity {

    private final String LOG_TAG = "SerialCodeActivity";
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private Button submitSerialButton;
    private EditText txtSerialCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_code);

        sharedPref = this.getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
        submitSerialButton = (Button) findViewById(R.id.btnSubmitSerial);
        txtSerialCode = (EditText) findViewById(R.id.txtSerialCode);
        txtSerialCode.addTextChangedListener(new PatternedTextWatcher("####-####-####-####"));

        submitSerialButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try{
                    if(checkSerialNumber()){
                        editor = sharedPref.edit();
                        editor.putBoolean("activateSerial", true);
                        editor.commit();
                        Intent myIntent = new Intent(SerialCodeActivity.this, HomeActivity.class);
                        SerialCodeActivity.this.startActivity(myIntent);
                        finish();
                    } else {
                        Toast.makeText(SerialCodeActivity.this, "Wrong Serial Number!!!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){
                    Log.e(LOG_TAG, e.getMessage(), e);
                }
            }
        });
    }

    private boolean checkSerialNumber() throws IOException {
        boolean result = false;
        String serialString = txtSerialCode.getText().toString();
        if(!("".equals(serialString.trim())) && (checkSerialWithKey(serialString))){
            result = true;
        }
        return result;
    }

    private boolean checkSerialWithKey(String serialString) throws IOException {
        String serialKey = PropertiesUtils.getProperty("serialKey", SerialCodeActivity.this);
        Log.i(LOG_TAG, "serial : "+serialKey+","+serialString);
        return serialKey.equals(serialString);
    }
}
