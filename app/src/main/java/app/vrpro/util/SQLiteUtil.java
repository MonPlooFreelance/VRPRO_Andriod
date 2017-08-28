package app.vrpro.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.vrpro.Model.EachOrderModel;
import app.vrpro.Model.OrderModel;
import app.vrpro.Model.ProfileSaleModel;

/**
 * Created by Plooer on 6/27/2017 AD.
 */

public class SQLiteUtil extends SQLiteOpenHelper {

    private final String LOG_TAG = "SQLiteUtil";
    private static final String DATABASE_NAME = "vrpro.db";
    private static final Integer DATABASE_VERSION = 1;
    private SQLiteDatabase sqLiteDatabase;

    private String PROFILE_SALE_TABLE = "profile_sale_table";
    private String PROFILE_SALE_ID = "ID";
    private String PROFILE_SALE_NAME = "sale_name";
    private String PROFILE_SALE_PHONE = "sale_phone";
    private String PROFILE_QUOTATION_NO = "quotation_no";
    private String PROFILE_QUOTATION_RUNNING_NO = "quotation_running_no";

    private String ORDER_TABLE = "order_table";
    private String ORDER_ID = "ID";
    private String ORDER_QUOTATION_NO = "quotation_no";
    private String ORDER_QUOTATION_DATE = "quotation_date";
    private String ORDER_PROJECT_NAME = "project_name";
    private String ORDER_CUSTOMER_NAME = "customer_name";
    private String ORDER_CUSTOMER_ADDRESS = "customer_address";
    private String ORDER_CUSTOMER_PHONE = "customer_phone";
    private String ORDER_CUSTOMER_EMAIL = "customer_email";
    private String ORDER_REMARKS = "remarks";
    private String ORDER_DISCOUNT = "discount";
    private String ORDER_TOTAL_PRICE = "total_price";
    private String ORDER_REAL_TOTAL_PRICE = "real_total_price";

    private String EACH_ORDER_TABLE = "each_order_list_table";
    private String EACH_ORDER_ID = "ID";
    private String EACH_ORDER_QUOTATION_NO = "quotation_no";
    private String EACH_ORDER_FLOOR = "floor";
    private String EACH_ORDER_POSITION = "position";
    private String EACH_ORDER_DW = "dw";
    private String EACH_ORDER_TYPE_OF_M = "type_of_m";
    private String EACH_ORDER_SPECIAL_WORD = "special_word";
    private String EACH_ORDER_SPECIAL_REQ = "special_req";
    private String EACH_ORDER_WIDTH = "width";
    private String EACH_ORDER_HEIGHT = "height";
    private String EACH_ORDER_TOTAL_PRICE = "total_price";
    private String EACH_ORDER_PRICE_PER_1MM = "price_per_1mm";

    public SQLiteUtil(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createProfileSaleTable(db);
    }

    private void createProfileSaleTable(SQLiteDatabase db) {
        createTableProfileSale(db);
        createTableOrder(db);
        createTableEachOrderList(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PROFILE_SALE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ORDER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + EACH_ORDER_TABLE);
        onCreate(db);
    }

    private void createTableEachOrderList(SQLiteDatabase db) {
        String CREATE_TABLE_EACH_ORDER_LIST = String.format("CREATE TABLE %s " +
                        "(%s INTEGER PRIMARY KEY  AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",
                EACH_ORDER_TABLE,
                EACH_ORDER_ID,
                EACH_ORDER_QUOTATION_NO,
                EACH_ORDER_FLOOR,
                EACH_ORDER_POSITION,
                EACH_ORDER_DW,
                EACH_ORDER_TYPE_OF_M,
                EACH_ORDER_SPECIAL_WORD,
                EACH_ORDER_SPECIAL_REQ,
                EACH_ORDER_WIDTH,
                EACH_ORDER_HEIGHT,
                EACH_ORDER_TOTAL_PRICE,
                EACH_ORDER_PRICE_PER_1MM);

        db.execSQL(CREATE_TABLE_EACH_ORDER_LIST);

        Log.i(LOG_TAG,"Create table each order list complete");
    }

    private void createTableOrder(SQLiteDatabase db) {
        String CREATE_ORDER = String.format("CREATE TABLE %s " +
                        "(%s INTEGER PRIMARY KEY  AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",
                ORDER_TABLE,
                ORDER_ID,
                ORDER_QUOTATION_NO,
                ORDER_QUOTATION_DATE,
                ORDER_PROJECT_NAME,
                ORDER_CUSTOMER_NAME,
                ORDER_CUSTOMER_ADDRESS,
                ORDER_CUSTOMER_PHONE,
                ORDER_CUSTOMER_EMAIL,
                ORDER_REMARKS,
                ORDER_DISCOUNT,
                ORDER_TOTAL_PRICE,
                ORDER_REAL_TOTAL_PRICE);

        db.execSQL(CREATE_ORDER);

        Log.i(LOG_TAG,"Create table order complete");
    }

    private String createTableProfileSale(SQLiteDatabase db) {
        String CREATE_TABLE_PROFILE_SALE = String.format("CREATE TABLE %s " +
                        "(%s INTEGER PRIMARY KEY  AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT, %s INTEGER)",
                PROFILE_SALE_TABLE,
                PROFILE_SALE_ID,
                PROFILE_SALE_NAME,
                PROFILE_SALE_PHONE,
                PROFILE_QUOTATION_NO,
                PROFILE_QUOTATION_RUNNING_NO);

        db.execSQL(CREATE_TABLE_PROFILE_SALE);

        Log.i(LOG_TAG,"Create table profile sale complete");
        return CREATE_TABLE_PROFILE_SALE;
    }


    public void insertProfileSaleModel(ProfileSaleModel profileSaleModel) {
        Log.i(LOG_TAG,"setProfileSale");
        sqLiteDatabase = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PROFILE_SALE_NAME, profileSaleModel.getSaleName());
        values.put(PROFILE_SALE_PHONE, profileSaleModel.getSalePhone());
        values.put(PROFILE_QUOTATION_NO, profileSaleModel.getQuotationNo());
        values.put(PROFILE_QUOTATION_RUNNING_NO, profileSaleModel.getQuotationRunningNo());

        sqLiteDatabase.insert(PROFILE_SALE_TABLE, null, values);

        sqLiteDatabase.close();
    }

    public ProfileSaleModel getProfileSaleModel() {
        Log.i(LOG_TAG,"getProfileSale");
        ProfileSaleModel profileSaleModel = new ProfileSaleModel();

        sqLiteDatabase = this.getWritableDatabase();

        Cursor cursor = sqLiteDatabase.query
                (PROFILE_SALE_TABLE, null, null, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        while(!cursor.isAfterLast()) {
            profileSaleModel.setID(cursor.getInt(0));
            profileSaleModel.setSaleName(cursor.getString(1));
            profileSaleModel.setSalePhone(cursor.getString(2));
            profileSaleModel.setQuotationNo(cursor.getString(3));
            profileSaleModel.setQuotationRunningNo(cursor.getInt(4));
            cursor.moveToNext();
        }
        sqLiteDatabase.close();

        return profileSaleModel;
    }

    public void updateProfileSaleModel(ProfileSaleModel profileSaleModel) {
        Log.i(LOG_TAG,"updateProfileSale >>>>> ID : " + profileSaleModel.getID());
        sqLiteDatabase  = this.getWritableDatabase();

        ContentValues values = new ContentValues();
//        values.put(PROFILE_SALE_ID, profileSaleModeodel.getID());
        values.put(PROFILE_SALE_NAME, profileSaleModel.getSaleName());
        values.put(PROFILE_SALE_PHONE, profileSaleModel.getSalePhone());
        values.put(PROFILE_QUOTATION_NO, profileSaleModel.getQuotationNo());
        values.put(PROFILE_QUOTATION_RUNNING_NO, profileSaleModel.getQuotationRunningNo());

        int row = sqLiteDatabase.update(PROFILE_SALE_TABLE,
                values,
                PROFILE_SALE_ID + " = ? ",
                new String[] { String.valueOf(profileSaleModel.getID()) });

        sqLiteDatabase.close();
    }

    public void insertOrderModel(OrderModel orderModel) {
        Log.i(LOG_TAG,"setOrder");
        sqLiteDatabase = this.getWritableDatabase();
        Log.i(LOG_TAG,"orderModel.getRemarks() : " + orderModel.getRemarks());
        ContentValues values = new ContentValues();
        values.put(ORDER_QUOTATION_NO, orderModel.getQuotationNo());
        values.put(ORDER_QUOTATION_DATE, orderModel.getQuotationDate());
        values.put(ORDER_PROJECT_NAME, orderModel.getProjectName());
        values.put(ORDER_CUSTOMER_NAME, orderModel.getCustomerName());
        values.put(ORDER_CUSTOMER_ADDRESS, orderModel.getCustomerAdress());
        values.put(ORDER_CUSTOMER_PHONE, orderModel.getCustomerPhone());
        values.put(ORDER_CUSTOMER_EMAIL, orderModel.getCustomerEmail());
        values.put(ORDER_REMARKS, orderModel.getRemarks());
        values.put(ORDER_DISCOUNT, orderModel.getDiscount());
        values.put(ORDER_TOTAL_PRICE, orderModel.getTotalPrice());
        values.put(ORDER_REAL_TOTAL_PRICE, orderModel.getRealTotalPrice());

        sqLiteDatabase.insert(ORDER_TABLE, null, values);

        sqLiteDatabase.close();
        Log.i(LOG_TAG,"insert order complete");
    }

    public List<OrderModel> getOrderModelList() {
        Log.i(LOG_TAG,"getOrderList");
        List<OrderModel> orderModelList = new ArrayList<OrderModel>();
        OrderModel orderModel;

        sqLiteDatabase = this.getWritableDatabase();

        Cursor cursor = sqLiteDatabase.query
                (ORDER_TABLE, null, null, null, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        while(!cursor.isAfterLast()) {
            orderModel = new OrderModel();
            orderModel.setID(cursor.getInt(0));
            orderModel.setQuotationNo(cursor.getString(1));
            orderModel.setQuotationDate(cursor.getString(2));
            orderModel.setProjectName(cursor.getString(3));
            orderModel.setCustomerName(cursor.getString(4));
            orderModel.setCustomerAdress(cursor.getString(5));
            orderModel.setCustomerPhone(cursor.getString(6));
            orderModel.setCustomerEmail(cursor.getString(7));
            orderModel.setRemarks(cursor.getString(8));
            orderModel.setDiscount(cursor.getDouble(9));
            orderModel.setTotalPrice(cursor.getDouble(10));
            orderModel.setRealTotalPrice(cursor.getDouble(11));
            orderModelList.add(orderModel);
            cursor.moveToNext();
        }
        sqLiteDatabase.close();

        return orderModelList;
    }

    public OrderModel getOrderModelByQuotationNo(String quotationNo) {
        Log.i(LOG_TAG,"getOrderByQuotationNo >>>> quotationNo : " + quotationNo);
        OrderModel orderModel = new OrderModel();

        sqLiteDatabase = this.getWritableDatabase();

        String selection = ORDER_QUOTATION_NO + " = ?"; // MISSING in your update!!
        String[] selectionArgs = new String[] { quotationNo };


        Cursor cursor = sqLiteDatabase.query
                (ORDER_TABLE, null, selection, selectionArgs, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        while(!cursor.isAfterLast()) {
            orderModel.setID(cursor.getInt(0));
            orderModel.setQuotationNo(cursor.getString(1));
            orderModel.setQuotationDate(cursor.getString(2));
            orderModel.setProjectName(cursor.getString(3));
            orderModel.setCustomerName(cursor.getString(4));
            orderModel.setCustomerAdress(cursor.getString(5));
            orderModel.setCustomerPhone(cursor.getString(6));
            orderModel.setCustomerEmail(cursor.getString(7));
            orderModel.setRemarks(cursor.getString(8));
            orderModel.setDiscount((cursor.getDouble(9)));
            orderModel.setTotalPrice(cursor.getDouble(10));
            orderModel.setRealTotalPrice(cursor.getDouble(11));
            cursor.moveToNext();
        }
        sqLiteDatabase.close();
        return orderModel;
    }

    public void updateOrderModel(OrderModel orderModel) {
        Log.i(LOG_TAG,"updateOrder >>>>> ID : " + orderModel.getID());
        sqLiteDatabase  = this.getWritableDatabase();
        ContentValues values = new ContentValues();
//        values.put(PROFILE_SALE_ID, profileSaleModeodel.getID());
        values.put(ORDER_QUOTATION_NO, orderModel.getQuotationNo());
        values.put(ORDER_QUOTATION_DATE, orderModel.getQuotationDate());
        values.put(ORDER_PROJECT_NAME, orderModel.getProjectName());
        values.put(ORDER_CUSTOMER_NAME, orderModel.getCustomerName());
        values.put(ORDER_CUSTOMER_ADDRESS, orderModel.getCustomerAdress());
        values.put(ORDER_CUSTOMER_PHONE, orderModel.getCustomerPhone());
        values.put(ORDER_CUSTOMER_EMAIL, orderModel.getCustomerEmail());
        values.put(ORDER_REMARKS, orderModel.getRemarks());
        values.put(ORDER_DISCOUNT, orderModel.getDiscount());
        values.put(ORDER_TOTAL_PRICE, orderModel.getTotalPrice());
        values.put(ORDER_REAL_TOTAL_PRICE, orderModel.getRealTotalPrice());

        int row = sqLiteDatabase.update(ORDER_TABLE,
                values,
                ORDER_ID + " = ? ",
                new String[] { String.valueOf(orderModel.getID()) });

        sqLiteDatabase.close();
    }

    public void deleteOrderModel(String id) {

        sqLiteDatabase = this.getWritableDatabase();

    /*sqLiteDatabase.delete(Friend.TABLE, Friend.Column.ID + " = ? ",
            new String[] { String.valueOf(friend.getId()) });*/
        sqLiteDatabase.delete(ORDER_TABLE, ORDER_ID + " = " + id, null);

        sqLiteDatabase.close();

        Log.i(LOG_TAG,"Delete OrderModel ID : " + id + " success.");
    }

    public void insertEachOrderModel(EachOrderModel eachOrderModel) {
        Log.i(LOG_TAG,"setEachOrderList");
        sqLiteDatabase = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(EACH_ORDER_QUOTATION_NO, eachOrderModel.getQuotationNo());
        values.put(EACH_ORDER_FLOOR, eachOrderModel.getFloor());
        values.put(EACH_ORDER_POSITION, eachOrderModel.getPosition());
        values.put(EACH_ORDER_DW, eachOrderModel.getDw());
        values.put(EACH_ORDER_TYPE_OF_M, eachOrderModel.getTypeOfM());
        values.put(EACH_ORDER_SPECIAL_WORD, eachOrderModel.getSpecialWord());
        values.put(EACH_ORDER_SPECIAL_REQ, eachOrderModel.getSpecialReq().toString());
        values.put(EACH_ORDER_WIDTH, eachOrderModel.getWidth());
        values.put(EACH_ORDER_HEIGHT, eachOrderModel.getHeight());
        values.put(EACH_ORDER_TOTAL_PRICE,eachOrderModel.getTotolPrice());
        values.put(EACH_ORDER_PRICE_PER_1MM,eachOrderModel.getPricePer1mm());

        sqLiteDatabase.insert(EACH_ORDER_TABLE, null, values);

        sqLiteDatabase.close();
    }

    public List<EachOrderModel> getEachOrderModelListByQuotationNo(String quotationNo) {
        Log.i(LOG_TAG,"getEachOrderList >>> quotationNo : " + quotationNo);
        List<EachOrderModel> eachOrderModelList = new ArrayList<EachOrderModel>();
        EachOrderModel eachOrderModel;

        sqLiteDatabase = this.getWritableDatabase();

        String selection = EACH_ORDER_QUOTATION_NO + " = ?"; // MISSING in your update!!
        String[] selectionArgs = new String[] { quotationNo };

        Cursor cursor = sqLiteDatabase.query
                (EACH_ORDER_TABLE, null, selection, selectionArgs, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        while(!cursor.isAfterLast()) {
            eachOrderModel = new EachOrderModel();
            eachOrderModel.setID(cursor.getInt(0));
            eachOrderModel.setQuotationNo(cursor.getString(1));
            eachOrderModel.setFloor(cursor.getString(2));
            eachOrderModel.setPosition(cursor.getString(3));
            eachOrderModel.setDw(cursor.getString(4));
            eachOrderModel.setTypeOfM(cursor.getString(5));
            eachOrderModel.setSpecialWord(cursor.getString(6));
            eachOrderModel.setSpecialReq(new ArrayList<String>(Arrays.asList(cursor.getString(7).substring(1,cursor.getString(7).length()-1).split(","))));
            eachOrderModel.setWidth(cursor.getDouble(8));
            eachOrderModel.setHeight(cursor.getDouble(9));
            eachOrderModel.setTotolPrice(cursor.getDouble(10));
            eachOrderModel.setPricePer1mm(cursor.getDouble(11));
            eachOrderModelList.add(eachOrderModel);
            cursor.moveToNext();

        }
        sqLiteDatabase.close();

        return eachOrderModelList;
    }


    public EachOrderModel getEachOrderModelById(String id) {
        Log.i(LOG_TAG,"getEachOrderModelById >>> ID : " + id);
        EachOrderModel eachOrderModel = new EachOrderModel();;

        sqLiteDatabase = this.getWritableDatabase();

        String selection = EACH_ORDER_ID+ " = ?"; // MISSING in your update!!
        String[] selectionArgs = new String[] { id };

        Cursor cursor = sqLiteDatabase.query
                (EACH_ORDER_TABLE, null, selection, selectionArgs, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        while(!cursor.isAfterLast()) {
            eachOrderModel.setID(cursor.getInt(0));
            eachOrderModel.setQuotationNo(cursor.getString(1));
            eachOrderModel.setFloor(cursor.getString(2));
            eachOrderModel.setPosition(cursor.getString(3));
            eachOrderModel.setDw(cursor.getString(4));
            eachOrderModel.setTypeOfM(cursor.getString(5));
            eachOrderModel.setSpecialWord(cursor.getString(6));
            eachOrderModel.setSpecialReq(new ArrayList<String>(Arrays.asList(cursor.getString(7).substring(1,cursor.getString(7).length()-1).split(","))));
            eachOrderModel.setWidth(cursor.getDouble(8));
            eachOrderModel.setHeight(cursor.getDouble(9));
            eachOrderModel.setTotolPrice(cursor.getDouble(10));
            eachOrderModel.setPricePer1mm(cursor.getDouble(11));
            cursor.moveToNext();
        }
        sqLiteDatabase.close();

        return eachOrderModel;
    }

    public void deleteEachOrderModel(String id) {

        sqLiteDatabase = this.getWritableDatabase();

    /*sqLiteDatabase.delete(Friend.TABLE, Friend.Column.ID + " = ? ",
            new String[] { String.valueOf(friend.getId()) });*/
        sqLiteDatabase.delete(EACH_ORDER_TABLE, EACH_ORDER_ID + " = " + id, null);

        sqLiteDatabase.close();

        Log.i(LOG_TAG,"Delete EachOrderModel ID : " + id + " success.");
    }

    public void updateEachOrderModel(EachOrderModel eachOrderModel) {
        Log.i(LOG_TAG,"updateEachOrderModel >>>>> ID : " + eachOrderModel.getID());
        sqLiteDatabase  = this.getWritableDatabase();
        ContentValues values = new ContentValues();

//        values.put(PROFILE_SALE_ID, profileSaleModeodel.getID());
        values.put(EACH_ORDER_QUOTATION_NO, eachOrderModel.getQuotationNo());
        values.put(EACH_ORDER_FLOOR, eachOrderModel.getFloor());
        values.put(EACH_ORDER_POSITION, eachOrderModel.getPosition());
        values.put(EACH_ORDER_DW, eachOrderModel.getDw());
        values.put(EACH_ORDER_TYPE_OF_M, eachOrderModel.getTypeOfM());
        values.put(EACH_ORDER_SPECIAL_WORD, eachOrderModel.getSpecialWord());
        values.put(EACH_ORDER_SPECIAL_REQ, eachOrderModel.getSpecialReq().toString());
        values.put(EACH_ORDER_WIDTH, eachOrderModel.getWidth());
        values.put(EACH_ORDER_HEIGHT, eachOrderModel.getHeight());
        values.put(EACH_ORDER_TOTAL_PRICE, eachOrderModel.getTotolPrice());
        values.put(EACH_ORDER_PRICE_PER_1MM, eachOrderModel.getPricePer1mm());

        int row = sqLiteDatabase.update(EACH_ORDER_TABLE,
                values,
                EACH_ORDER_ID + " = ? ",
                new String[] { String.valueOf(eachOrderModel.getID()) });

        sqLiteDatabase.close();
    }

}
