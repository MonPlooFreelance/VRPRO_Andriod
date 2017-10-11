package vrpro.vrpro.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import vrpro.vrpro.Model.ProfileSaleModel;
import vrpro.vrpro.R;
import vrpro.vrpro.util.SQLiteUtil;

public class SetProfileActivity extends AppCompatActivity {

    private final String LOG_TAG = "SetProfileActivity";
    private EditText txtSaleName;
    private EditText txtPhoneNumber;
    private EditText txtSetQuotationNo;
    private EditText txtSetQuotationRunningNo;
    private SQLiteUtil sqlLite;
    private ProfileSaleModel profileSaleModel;
    private ProfileSaleModel profileSaleModelFromDB;
    Toolbar mActionBarToolbar;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG,"SetProfileActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile);

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle("Profile");

        sharedPref = this.getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);

        txtSaleName = (EditText) findViewById(R.id.txtsaleName);
        txtPhoneNumber = (EditText) findViewById(R.id.txtphoneNumber);
        txtSetQuotationNo = (EditText) findViewById(R.id.txtSetQuotationNo);
        txtSetQuotationRunningNo = (EditText) findViewById(R.id.txtSetQuotationRunningNo);

        initialProfile();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    private void initialProfile() {
        TextView txtvwUserName = (TextView) findViewById(R.id.viewUsername);
        TextView txtvwUserPhone = (TextView) findViewById(R.id.viewPhoneNumber);
        TextView txtvwQuotationNo = (TextView) findViewById(R.id.viewQuotationNo);
        TextView txtvwQuotationRunningNo = (TextView) findViewById(R.id.viewQuotationRunningNo);

        sqlLite = new SQLiteUtil(this);
        profileSaleModelFromDB = sqlLite.getProfileSaleModel();
        if(profileSaleModelFromDB.getSaleName() != null){
            Log.i(LOG_TAG,"Ever set profile in DB");
            txtvwUserName.setText(profileSaleModelFromDB.getSaleName());
            txtvwUserPhone.setText(profileSaleModelFromDB.getSalePhone());
            txtvwQuotationNo.setText(profileSaleModelFromDB.getQuotationNo());
            txtvwQuotationRunningNo.setText(String.valueOf(profileSaleModelFromDB.getQuotationRunningNo()));
        }else{
            Log.i(LOG_TAG,"Not set profile in DB");
            txtvwUserName.setText("");
            txtvwUserPhone.setText("");
            txtvwQuotationNo.setText("");
            txtvwQuotationRunningNo.setText("");
        }
    }

    public void submit(View view) {
        if (isInputsEmpty()) {
            Toast.makeText(this, "Please enter your name, phone number and quataion.", Toast.LENGTH_SHORT).show();
        } else {
            Log.i(LOG_TAG, "sale name : " + txtSaleName.getText().toString() + " phone number : " + txtPhoneNumber.getText().toString()+ " quotation no : " + txtSetQuotationNo.getText().toString() + "running no : " + txtSetQuotationRunningNo.getText().toString());
            sqlLite = new SQLiteUtil(this);
            profileSaleModelFromDB = sqlLite.getProfileSaleModel();
            if(profileSaleModelFromDB.getSaleName() != null){
                updateProfileToDB();
            }else{
                insertProfileToDB();
            }

            editor = sharedPref.edit();
            editor.putString("quotationNoDefine", txtSetQuotationNo.getText().toString());
            editor.putInt("quotationRunningNoDefine", Integer.parseInt(txtSetQuotationRunningNo.getText().toString()));
            editor.putString("everCreateOrder", null);
            editor.commit();
            gotoHomeActicity();
        }
    }

    private void updateProfileToDB() {
        profileSaleModel = new ProfileSaleModel();
        profileSaleModel.setSaleName(txtSaleName.getText().toString());
        profileSaleModel.setSalePhone(txtPhoneNumber.getText().toString());
        profileSaleModel.setQuotationNo(txtSetQuotationNo.getText().toString());
        profileSaleModel.setQuotationRunningNo(Integer.parseInt(txtSetQuotationRunningNo.getText().toString()));
        profileSaleModel.setID(profileSaleModelFromDB.getID());
        sqlLite = new SQLiteUtil(this);
        sqlLite.updateProfileSaleModel(profileSaleModel);
    }

    private void insertProfileToDB() {
        profileSaleModel = new ProfileSaleModel();
        profileSaleModel.setSaleName(txtSaleName.getText().toString());
        profileSaleModel.setSalePhone(txtPhoneNumber.getText().toString());
        profileSaleModel.setQuotationNo(txtSetQuotationNo.getText().toString());
        profileSaleModel.setQuotationRunningNo(Integer.parseInt(txtSetQuotationRunningNo.getText().toString()));
        sqlLite = new SQLiteUtil(this);
        sqlLite.insertProfileSaleModel(profileSaleModel);
    }

    private boolean isInputsEmpty() {
        return isEmpty(txtSaleName) || isEmpty(txtPhoneNumber) || isEmpty(txtSetQuotationNo) || isEmpty(txtSetQuotationRunningNo);
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    private void gotoHomeActicity() {
        Intent myIntent = new Intent(this, HomeActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(myIntent);
        finish();
    }

}
