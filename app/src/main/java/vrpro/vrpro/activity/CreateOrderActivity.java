package vrpro.vrpro.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import vrpro.vrpro.Model.EachOrderModel;
import vrpro.vrpro.Model.OrderModel;
import vrpro.vrpro.Model.ProfileSaleModel;
import vrpro.vrpro.R;
import vrpro.vrpro.adapter.ListEachOrderAdapter;
import vrpro.vrpro.util.SQLiteEachOrderListUtil;
import vrpro.vrpro.util.SQLiteUtil;


public class CreateOrderActivity extends AppCompatActivity {

    private final String LOG_TAG = "CreateOrderActivity";
    private Toolbar mActionBarToolbar;
    private ListView listEachOrderListView;
    private SQLiteUtil sqlLite;
    private String quatationNo;
    private String quatationDate;
    private String projectName;
    private String customerName;
    private String customerAdress;
    private String customerPhone;
    private String customerEmail;
    private String remarks;
    private String discount;
    private Double totalPrice;

    private TextView txtQuatationNo;
    private TextView txtQuatationDate;
    private EditText txtProjectName;
    private EditText txtCustomerName;
    private EditText txtCustomerAdress;
    private EditText txtCustomerPhone;
    private EditText txtCustomerEmail;
    private EditText txtRemarks;
    private EditText txtDiscount;
    private TextView txtTotalPrice;

    private OrderModel orderModel;
    private OrderModel orderModelFromDB;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private String shared_quatationNo;
    private String shared_quatationNoDefine;
    private Integer shared_quatationRunningNoDefine;
    private String shared_everCreareOrder;
    private Integer runningQuataionNo;

    List<EachOrderModel> eachOrderModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG,"CreateOrderActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle("ใบเสนอราคา");

        sharedPref = this.getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);

        txtQuatationNo = (TextView) findViewById(R.id.txtQuatationNo);
        txtQuatationDate = (TextView) findViewById(R.id.txtQuatationDate);
        txtProjectName = (EditText) findViewById(R.id.txtProjectName);
        txtCustomerName = (EditText) findViewById(R.id.txtCustomerName);
        txtCustomerAdress = (EditText) findViewById(R.id.txtCustomerAddress);
        txtCustomerPhone = (EditText) findViewById(R.id.txtCustomerPhone);
        txtCustomerEmail = (EditText) findViewById(R.id.txtCustomerEmail);
        txtRemarks = (EditText) findViewById(R.id.txtRemarks);
        txtDiscount = (EditText) findViewById(R.id.txtDiscount);
        txtTotalPrice = (TextView) findViewById(R.id.txtTotalPrice);

        shared_quatationNo = sharedPref.getString("quatationNo",null);
        shared_quatationNoDefine = sharedPref.getString("quatationNoDefine",null);
        shared_quatationRunningNoDefine = sharedPref.getInt("quatationRunningNoDefine",0);
        shared_everCreareOrder = sharedPref.getString("everCreateOrder",null);


        txtDiscount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    double tempTotalPrice;
                    if(isEmpty(txtDiscount)){
                        tempTotalPrice = orderModelFromDB.getRealTotalPrice() - 0;
                    }else{
                        tempTotalPrice = orderModelFromDB.getRealTotalPrice() - Double.parseDouble(txtDiscount.getText().toString());
                    }

                    Log.i(LOG_TAG,"realTotalPrice : " + orderModelFromDB.getRealTotalPrice());
                    Log.i(LOG_TAG,"textDiscount : " + txtDiscount.getText().toString());
                    Log.i(LOG_TAG,"tempPrice : " + tempTotalPrice);
                    if(tempTotalPrice>=0){
                        txtTotalPrice.setText(String.valueOf(tempTotalPrice));
                    }else{
                        txtTotalPrice.setText("0.0");
                    }

                }
            }
        });

        Button summaryPriceBtn = (Button) findViewById(R.id.summaryPrice);
        summaryPriceBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LOG_TAG, "summary price");
            }
        });

        Button saveOrder = (Button) findViewById(R.id.saveOrder);
        saveOrder.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isInputsEmpty()){
                    Toast.makeText(CreateOrderActivity.this, "กรอกข้อมูลให้ครบทุกช่อง", Toast.LENGTH_SHORT).show();
                }else{

                    if(orderModelFromDB.getQuatationNo() != null){
                        Log.i(LOG_TAG,"Update Order");
                        totalPrice = Double.parseDouble(txtTotalPrice.getText().toString());
                        updateOrderModelToDB(totalPrice,orderModelFromDB.getRealTotalPrice());
                        gotoHomeActivity();

                    }else{
                        Log.i(LOG_TAG,"Save Order");
                        saveOrderToDB();
                        setQuatationNoDefineToSharedPref();
                        setEverCreateOrderToSharedPref();
                        gotoHomeActivity();
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        sqlLite = new SQLiteUtil(CreateOrderActivity.this);
        orderModelFromDB = new OrderModel();
        orderModelFromDB = sqlLite.getOrderModelByQuatationNo(shared_quatationNo);
        initialData();
    }

    private void updateOrderModelToDB(Double totalPrice,Double realTotalPrice) {
        Log.i(LOG_TAG,"updateOrderModelToDB");
        Log.i(LOG_TAG,"quatationNo : " +  txtQuatationNo.getText().toString());
        Log.i(LOG_TAG,"quatationDate : " + txtQuatationDate.getText().toString());
        Log.i(LOG_TAG,"projectName : " + txtProjectName.getText().toString());
        Log.i(LOG_TAG,"customerName : " + txtCustomerName.getText().toString());
        Log.i(LOG_TAG,"customerAdress : " + txtCustomerAdress.getText().toString());
        Log.i(LOG_TAG,"customerPhone : " + txtCustomerPhone.getText().toString());
        Log.i(LOG_TAG,"customerEmail : " + txtCustomerEmail.getText().toString());
        Log.i(LOG_TAG,"remarks : " + txtRemarks.getText().toString());
        Log.i(LOG_TAG,"discount : " + txtDiscount.getText().toString());
        Log.i(LOG_TAG,"totalPrice : " + totalPrice);
        Log.i(LOG_TAG,"realTotalPrice : " + realTotalPrice);

        orderModel = new OrderModel();
        orderModel.setID(orderModelFromDB.getID());
        orderModel.setQuatationNo( txtQuatationNo.getText().toString());
        orderModel.setQuatationDate(txtQuatationDate.getText().toString());
        orderModel.setProjectName(txtProjectName.getText().toString());
        orderModel.setCustomerName(txtCustomerName.getText().toString());
        orderModel.setCustomerAdress(txtCustomerAdress.getText().toString());
        orderModel.setCustomerPhone(txtCustomerPhone.getText().toString());
        orderModel.setCustomerEmail(txtCustomerEmail.getText().toString());
        orderModel.setRemarks(txtRemarks.getText().toString());
        orderModel.setDiscount(txtDiscount.getText().toString());
        orderModel.setTotalPrice(totalPrice);
        orderModel.setRealTotalPrice(realTotalPrice);
        sqlLite = new SQLiteUtil(CreateOrderActivity.this);
        sqlLite.updateOrderModel(orderModel);
    }

    private void initialData(){
        Log.i(LOG_TAG,"shared_quatationNo : " + shared_quatationNo);
        Log.i(LOG_TAG,"shared_quatationNoDefine : " + shared_quatationNoDefine);
        Log.i(LOG_TAG,"shared_quatationRunningNoDefine : " + shared_quatationRunningNoDefine);
        Log.i(LOG_TAG,"shared_everCreateOrder : " + shared_everCreareOrder);
        if(shared_quatationNo.equals("CREATE NEW ORDER")){
            Log.i(LOG_TAG,"Create New Order");
            if(shared_quatationRunningNoDefine == 0){
                shared_quatationRunningNoDefine = shared_quatationRunningNoDefine + 1;
            }
            sqlLite = new SQLiteUtil(this);
            ProfileSaleModel profileSaleModelFromDB = sqlLite.getProfileSaleModel();
            Log.i(LOG_TAG,">> getQuatationRunningNo " + profileSaleModelFromDB.getQuatationRunningNo());
            if(shared_everCreareOrder == null){
                Log.i(LOG_TAG,"First Order Of Sale");
                runningQuataionNo = shared_quatationRunningNoDefine;
                txtQuatationNo.setText(shared_quatationNoDefine+shared_quatationRunningNoDefine);
                txtTotalPrice.setText("0.0");
            }else {
                Log.i(LOG_TAG, "Not First Order Of Sale");
                runningQuataionNo = shared_quatationRunningNoDefine + 1;
                String showQautationNo = shared_quatationNoDefine + String.valueOf(runningQuataionNo);
                Log.i(LOG_TAG, "showQautationNo : " + showQautationNo);
                txtQuatationNo.setText(showQautationNo);
                txtTotalPrice.setText("0.0");
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Bangkok"));
            txtQuatationDate.setText(sdf.format(calendar.getTime()));
            setQuatationNoToSharedPref();

        }else {
            Log.i(LOG_TAG, "Open Order Quatation No : " + shared_quatationNo);
            sqlLite = new SQLiteUtil(CreateOrderActivity.this);
            txtQuatationNo.setText(orderModelFromDB.getQuatationNo());
            txtQuatationDate.setText(orderModelFromDB.getQuatationDate());
            txtProjectName.setText(orderModelFromDB.getProjectName());
            txtCustomerName.setText(orderModelFromDB.getCustomerName());
            txtCustomerAdress.setText(orderModelFromDB.getCustomerAdress());
            txtCustomerPhone.setText(orderModelFromDB.getCustomerPhone());
            txtCustomerEmail.setText(orderModelFromDB.getCustomerEmail());
            txtRemarks.setText(orderModelFromDB.getRemarks());
            txtDiscount.setText(String.valueOf(orderModelFromDB.getDiscount()));
            txtTotalPrice.setText(String.valueOf(orderModelFromDB.getTotalPrice()));
            getDataToListView(shared_quatationNo);
        }
    }

    private void setQuatationNoToSharedPref() {
        editor = sharedPref.edit();
        editor.putString("quatationNo", txtQuatationNo.getText().toString());
        editor.commit();
    }

    private void setQuatationNoDefineToSharedPref() {
        editor = sharedPref.edit();
        editor.putString("quatationNoDefine", shared_quatationNoDefine);
        editor.putInt("quatationRunningNoDefine",runningQuataionNo);
        editor.commit();
    }

    private void setEverCreateOrderToSharedPref() {
        editor = sharedPref.edit();
        editor.putString("everCreateOrder", "Not First Order Of Sale");
        editor.commit();
    }

    private void saveOrderToDB() {
        quatationNo = txtQuatationNo.getText().toString();
        quatationDate =  txtQuatationDate.getText().toString();
        projectName = txtProjectName.getText().toString();
        customerName = txtCustomerName.getText().toString();
        customerAdress = txtCustomerAdress.getText().toString();
        customerPhone = txtCustomerPhone.getText().toString();
        customerEmail = txtCustomerEmail.getText().toString();

        remarks = txtRemarks.getText().toString();
        discount = txtDiscount.getText().toString();
        totalPrice = Double.parseDouble(txtTotalPrice.getText().toString());

        Log.i(LOG_TAG,"quatationNo : " + quatationNo);
        Log.i(LOG_TAG,"quatationDate : " + quatationDate);
        Log.i(LOG_TAG,"projectName : " + projectName);
        Log.i(LOG_TAG,"customerName : " + customerName);
        Log.i(LOG_TAG,"customerAdress : " + customerAdress);
        Log.i(LOG_TAG,"customerPhone : " + customerPhone);
        Log.i(LOG_TAG,"customerEmail : " + customerEmail);
        Log.i(LOG_TAG,"remarks : " + remarks);
        Log.i(LOG_TAG,"discount : " + discount);
        Log.i(LOG_TAG,"totalPrice : " + totalPrice);

        orderModel = new OrderModel();
        orderModel.setQuatationNo(quatationNo);
        orderModel.setQuatationDate(quatationDate);
        orderModel.setProjectName(projectName);
        orderModel.setCustomerName(customerName);
        orderModel.setCustomerAdress(customerAdress);
        orderModel.setCustomerPhone(customerPhone);
        orderModel.setCustomerEmail(customerEmail);
        orderModel.setRemarks(remarks);
        orderModel.setDiscount(discount);
        orderModel.setTotalPrice(totalPrice);

        sqlLite = new SQLiteUtil(CreateOrderActivity.this);
        sqlLite.insertOrderModel(orderModel);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (txtDiscount.isFocused()) {
                Rect outRect = new Rect();
                txtDiscount.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    txtDiscount.clearFocus();
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    private void getDataToListView(String quatationNo) {
        eachOrderModelList = new ArrayList<EachOrderModel>();
        sqlLite = new SQLiteUtil(this);

        eachOrderModelList  = sqlLite.getEachOrderModelListByQuatationNo(quatationNo);
        listEachOrderListView = (ListView) findViewById(R.id.listViewEachOrder);

        ListEachOrderAdapter listEachOrderAdaper = new ListEachOrderAdapter(this, eachOrderModelList);
        listEachOrderListView.setAdapter(listEachOrderAdaper);
        listEachOrderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                gotoSelectListActivity(String.valueOf(position));
            }
        });

        listEachOrderListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int pos, long id) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(CreateOrderActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(CreateOrderActivity.this);
                }
                builder.setTitle("คุณต้องการจะลบรายการนี้ใช่หรือไม่")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deleteEachOrderModel(pos);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_each_order, menu);
        return true;
    }


    private void gotoSelectListActivity(String position) {
        Log.i(LOG_TAG,"gotoSelectListActivity >>>. " + position );
            Intent myIntent = new Intent(CreateOrderActivity.this, SelectListOrderActivity.class);
            editor = sharedPref.edit();
            if(position.equals("CREATE NEW EACH ORDER")){
                editor.putString("eachOrderModel_id", "CREATE NEW EACH ORDER");
            }else{
                editor.putString("eachOrderModel_id",String.valueOf(eachOrderModelList.get(Integer.parseInt(position)).getID()));
            }
            editor.commit();
            startActivity(myIntent);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnCreateEachOrder:
                Log.i(LOG_TAG, "click to create each order");
                if(isInputsEmpty()){
                    Toast.makeText(CreateOrderActivity.this, "กรอกข้อมูลให้ครบทุกช่อง", Toast.LENGTH_SHORT).show();
                }else{
                    sqlLite = new SQLiteUtil(CreateOrderActivity.this);
                    orderModelFromDB = new OrderModel();
                    orderModelFromDB = sqlLite.getOrderModelByQuatationNo(shared_quatationNo);
                    if(orderModelFromDB.getQuatationNo() == null){
                        Log.i(LOG_TAG,"Save Order");
                        saveOrderToDB();
                        setQuatationNoDefineToSharedPref();
                        setEverCreateOrderToSharedPref();
                    }
                    gotoSelectListActivity("CREATE NEW EACH ORDER");
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteEachOrderModel(int position){
        Log.i(LOG_TAG,"deleteEachOrderList >>>> ID : " + eachOrderModelList.get(position).getID());
        sqlLite = new SQLiteUtil(this);
        sqlLite.deleteEachOrderModel(eachOrderModelList.get(position).getID().toString());

        totalPrice = orderModelFromDB.getTotalPrice() - eachOrderModelList.get(position).getTotolPrice();
        Double realTotalPrice = orderModelFromDB.getRealTotalPrice() - eachOrderModelList.get(position).getTotolPrice();
        if(totalPrice<0){
            Log.i(LOG_TAG,"totalPrice<0");
            totalPrice = 0.0;
        }

        updateOrderModelToDB(totalPrice,realTotalPrice);
        getDataToListView(shared_quatationNo);
        onStart();
    }

    private boolean isInputsEmpty() {
        return isEmpty(txtProjectName) || isEmpty(txtCustomerName) || isEmpty(txtCustomerAdress) || isEmpty(txtCustomerPhone) || isEmpty(txtCustomerEmail);
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

//    private void gotoSelectListActivity() {
//        Intent myIntent = new Intent(this, SelectListOrderActivity.class);
////        myIntent.putExtra("quatationNo",quatationNo);
//        this.startActivity(myIntent);
//    }

    private void gotoHomeActivity() {
        Intent myIntent = new Intent(this, HomeActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(myIntent);
        finish();
    }
}
