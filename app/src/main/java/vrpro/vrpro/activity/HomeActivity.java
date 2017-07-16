package vrpro.vrpro.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import vrpro.vrpro.Model.OrderModel;
import vrpro.vrpro.R;
import vrpro.vrpro.adapter.ListOrderAdapter;
import vrpro.vrpro.util.SQLiteUtil;

public class HomeActivity extends AppCompatActivity {

    private final String LOG_TAG = "SetProfileActivity";
    private ListView listOrderListView;
    private Toolbar mActionBarToolbar;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private SQLiteUtil sqlLite;
    List<OrderModel> orderModelList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.i(LOG_TAG,"onCreate");
        Fabric.with(this, new Crashlytics());

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle("VRPRO");

        SQLiteUtil sqlLite = new SQLiteUtil(this);
        sqlLite.getWritableDatabase();
        sharedPref = this.getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG,"onStart");
        initialData();
        Button gotoCreateOrderBtn = (Button) findViewById(R.id.gotoCreateOrder);
        gotoCreateOrderBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LOG_TAG, "click to create new order");
                gotoCreateOrderActivity("CREATE NEW ORDER");
            }
        });

    }

    private void initialData() {
        sqlLite = new SQLiteUtil(this);
        orderModelList = sqlLite.getOrderModelList();
        listOrderListView = (ListView) findViewById(R.id.listViewOrder);
        ListOrderAdapter listOrderAdapter = new ListOrderAdapter(this, orderModelList);
        listOrderListView.setAdapter(listOrderAdapter);
        listOrderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                gotoCreateOrderActivity(String.valueOf(position));
            }
        });

        listOrderListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int pos, long id) {
                // TODO Auto-generated method stub
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(HomeActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(HomeActivity.this);
                }
                builder.setTitle("คุณต้องการจะลบรายการนี้ใช่หรือไม่")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deleteOrderModel(pos);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return true;
            }
        });
    }

    private void deleteOrderModel(int position){
        Log.i(LOG_TAG,"deleteEachOrderList >>>> ID : " + orderModelList.get(position).getID());
        sqlLite = new SQLiteUtil(this);
        sqlLite.deleteOrderModel(orderModelList.get(position).getID().toString());
        initialData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.goto_set_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnProfile:
                Log.i(LOG_TAG, "click to set profile");
                gotoSetProfileActicity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void gotoCreateOrderActivity(String position) {
        Log.i(LOG_TAG,"gotoCreateOrderActivity >>>. " + position );
        String shared_quotationNoDefine = sharedPref.getString("quotationNoDefine",null);
        if(shared_quotationNoDefine == null){
            Toast.makeText(this, "Please set quotation number before create new order.", Toast.LENGTH_SHORT).show();
        }else{
            Intent myIntent = new Intent(HomeActivity.this, CreateOrderActivity.class);
            editor = sharedPref.edit();
            if(position.equals("CREATE NEW ORDER")){
                editor.putString("quotationNo", "CREATE NEW ORDER");
            }else{
                editor.putString("quotationNo",orderModelList.get(Integer.parseInt(position)).getQuotationNo());
            }
            editor.commit();
            startActivity(myIntent);
        }

    }

    private void gotoSetProfileActicity() {
        Intent myIntent = new Intent(this, SetProfileActivity.class);
        this.startActivity(myIntent);
    }

}
