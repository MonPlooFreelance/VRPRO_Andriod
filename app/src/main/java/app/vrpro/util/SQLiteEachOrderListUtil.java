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

/**
 * Created by Plooer on 6/27/2017 AD.
 */

public class SQLiteEachOrderListUtil extends SQLiteOpenHelper {

    private final String LOG_TAG = "SQLiteEachOrderListUtil";
    private static final String DATABASE_NAME = "vrpro.db";
    private static final  Integer DATABASE_VERSION = 1;
    private SQLiteDatabase sqLiteDatabase;

    private String EACH_ORDER_TABLE = "each_order_list";
    private String EACH_ORDER_ID = "ID";
    private String EACH_ORDER_QUATATION_NO = "quotation_no";
    private String EACH_ORDER_FLOOR = "floor";
    private String EACH_ORDER_POSITION = "position";
    private String EACH_ORDER_DW = "dw";
    private String EACH_ORDER_TYPE_OF_M = "type_of_m";
    private String EACH_ORDER_SPECIAL_WORD = "special_word";
    private String EACH_ORDER_SPECIAL_REQ = "special_req";
    private String EACH_ORDER_WIDTH = "width";
    private String EACH_ORDER_HEIGHT = "height";
    private String EACH_ORDER_TOTAL_PRICE = "total_price";

    public SQLiteEachOrderListUtil(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.i(LOG_TAG,"SQLiteEachOrderListUtil constructor");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(LOG_TAG,"On create");
        createEachOrderListTable(db);
    }

    private void createEachOrderListTable(SQLiteDatabase db) {
        String CREATE_TABLE_EACH_ORDER_LIST = String.format("CREATE TABLE %s " +
                        "(%s INTEGER PRIMARY KEY  AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",
                EACH_ORDER_TABLE,
                EACH_ORDER_ID,
                EACH_ORDER_QUATATION_NO,
                EACH_ORDER_FLOOR,
                EACH_ORDER_POSITION,
                EACH_ORDER_DW,
                EACH_ORDER_TYPE_OF_M,
                EACH_ORDER_SPECIAL_WORD,
                EACH_ORDER_SPECIAL_REQ,
                EACH_ORDER_WIDTH,
                EACH_ORDER_HEIGHT,
                EACH_ORDER_TOTAL_PRICE);

        db.execSQL(CREATE_TABLE_EACH_ORDER_LIST);

        Log.i(LOG_TAG,"Create table each order list complete");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_FRIEND_TABLE = "DROP TABLE IF EXISTS profile_sale";

        db.execSQL(DROP_FRIEND_TABLE);

        onCreate(db);
    }

    public void setEachOrderList(EachOrderModel eachOrderModel) {
        Log.i(LOG_TAG,"setEachOrderList");
        sqLiteDatabase = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(EACH_ORDER_QUATATION_NO, eachOrderModel.getQuotationNo());
        values.put(EACH_ORDER_FLOOR, eachOrderModel.getFloor());
        values.put(EACH_ORDER_POSITION, eachOrderModel.getPosition());
        values.put(EACH_ORDER_DW, eachOrderModel.getDw());
        values.put(EACH_ORDER_TYPE_OF_M, eachOrderModel.getTypeOfM());
        values.put(EACH_ORDER_SPECIAL_WORD, eachOrderModel.getSpecialWord());
        values.put(EACH_ORDER_SPECIAL_REQ, eachOrderModel.getSpecialReq().toString());
        values.put(EACH_ORDER_WIDTH, eachOrderModel.getWidth());
        values.put(EACH_ORDER_HEIGHT, eachOrderModel.getHeight());
        values.put(EACH_ORDER_TOTAL_PRICE,eachOrderModel.getTotalPrice());


        sqLiteDatabase.insert(EACH_ORDER_TABLE, null, values);

        sqLiteDatabase.close();
    }

    public List<EachOrderModel> getEachOrderList(String quotationNo) {
        Log.i(LOG_TAG,"getEachOrderList >>> quotationNo : " + quotationNo);
        List<EachOrderModel> eachOrderModelList = new ArrayList<EachOrderModel>();
        EachOrderModel eachOrderModel;

        sqLiteDatabase = this.getWritableDatabase();

        String selection = EACH_ORDER_QUATATION_NO+ " = ?"; // MISSING in your update!!
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
            eachOrderModel.setSpecialReq(new ArrayList<String>(Arrays.asList(cursor.getString(7).split(","))));
            eachOrderModel.setWidth(cursor.getDouble(8));
            eachOrderModel.setHeight(cursor.getDouble(9));
            eachOrderModel.setTotalPrice(cursor.getDouble(10));
            eachOrderModelList.add(eachOrderModel);
            cursor.moveToNext();
        }
        sqLiteDatabase.close();

//        Log.i(LOG_TAG,"------------------------------");
//        for(EachOrderModel model : eachOrderModelList) {
//            Log.i(LOG_TAG,"id : " + model.getID());
//            Log.i(LOG_TAG,"floor : " + model.getFloor());
//        }
//        Log.i(LOG_TAG,"------------------------------");
//        Log.i(LOG_TAG,"size of list : "+eachOrderModelList.size());


        return eachOrderModelList;
    }


}
