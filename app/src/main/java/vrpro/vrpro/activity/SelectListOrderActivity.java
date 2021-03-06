package vrpro.vrpro.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;

import vrpro.vrpro.Model.EachOrderModel;
import vrpro.vrpro.Model.OrderModel;
import vrpro.vrpro.R;
import vrpro.vrpro.util.SQLiteUtil;

public class SelectListOrderActivity extends AppCompatActivity {

    private final String LOG_TAG = "SelectListOrderActivity";

    private String CREATE_NEW_EACH_ORDER_MODEL = "CREATE NEW EACH ORDER";
    private Toolbar mActionBarToolbar;
    private String floor;
    private String position;
    private String DW;
    private String typeOfM;
    private String specialWord;
    private ArrayList<String>  specialReq;
    private Integer posPosition;
    private Integer posTypeOfM;
    private Integer sizeOfspecialReq;
    private Double totalPrice = 0.0;
    private String shared_quotationNo;
    EditText txtWidth;
    EditText txtHeight;
    private SQLiteUtil sqlLite;
    private SharedPreferences sharedPref;
    private OrderModel orderModelFromDB;
    private EachOrderModel eachOrderModelFromDB;
    private String shared_eachOrderModel_id;
    private ArrayList<String> grobalSpecialReq;
    private Double pricePer1mm;
    private RadioGroup radioGroupFloor;
    private RadioButton radioButtonFloor;
    private RadioGroup radioGroupDW;
    private RadioButton radioButtonDW;
    private RadioGroup radioGroupColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_list_order);

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle("รายการ");

        sharedPref = this.getSharedPreferences("vrpro.vrpro", Context.MODE_PRIVATE);
        shared_quotationNo = sharedPref.getString("quotationNo",null);
        shared_eachOrderModel_id = sharedPref.getString("eachOrderModel_id",null);

        Log.i(LOG_TAG,"shared_quotationNo : " + shared_quotationNo);

        txtWidth = (EditText) findViewById(R.id.txtWidthEachOrder);
        txtHeight = (EditText) findViewById(R.id.txtHeightEachOrder);


        sqlLite = new SQLiteUtil(SelectListOrderActivity.this);
        eachOrderModelFromDB = new EachOrderModel();
        eachOrderModelFromDB = sqlLite.getEachOrderModelById(shared_eachOrderModel_id);

        initialData();
        pressInsertOrder();

    }

    private void initialData() {

        if (shared_eachOrderModel_id.equals(CREATE_NEW_EACH_ORDER_MODEL)) {
            Log.i(LOG_TAG, CREATE_NEW_EACH_ORDER_MODEL);
            setFloorRadioGroup(CREATE_NEW_EACH_ORDER_MODEL);
            setPositionSpinner(CREATE_NEW_EACH_ORDER_MODEL);
            setDWRadioGroup(CREATE_NEW_EACH_ORDER_MODEL);
            setTypeOfMSpinner(CREATE_NEW_EACH_ORDER_MODEL);
            setColorRadioGroup(CREATE_NEW_EACH_ORDER_MODEL);
        } else {
            Log.i(LOG_TAG, "Not Create new each order");
            setFloorRadioGroup(eachOrderModelFromDB.getFloor());
            setPositionSpinner(eachOrderModelFromDB.getPosition());
            setDWRadioGroup(eachOrderModelFromDB.getDw());
            setTypeOfMSpinner(eachOrderModelFromDB.getTypeOfM());
            setColorRadioGroup(eachOrderModelFromDB.getSpecialWord());
            txtWidth.setText(String.valueOf(eachOrderModelFromDB.getWidth()));
            txtHeight.setText(String.valueOf(eachOrderModelFromDB.getHeight()));
        }
    }

    private void setFloorRadioGroup(String floor){
        if(floor.equals("1") || floor.equals("CREATE NEW EACH ORDER")){
            radioButtonFloor = (RadioButton) findViewById(R.id.radio_1st_floor);
        }else if(floor.equals("2")){
            radioButtonFloor = (RadioButton) findViewById(R.id.radio_2nd_floor);
        }else if(floor.equals("3")){
            radioButtonFloor = (RadioButton) findViewById(R.id.radio_3rd_floor);
        }
        radioButtonFloor.setChecked(true);
    }

    private void setDWRadioGroup(String dw){
        if(dw.equals("ประตู") || dw.equals("CREATE NEW EACH ORDER")){
            radioButtonDW = (RadioButton) findViewById(R.id.radio_door);
        }else if(dw.equals("หน้าต่าง")){
            radioButtonDW = (RadioButton) findViewById(R.id.radio_window);
        }
        radioButtonDW.setChecked(true);
    }

    private void setColorRadioGroup(String floor){
        if(floor.equals("สีขาว") || floor.equals("CREATE NEW EACH ORDER")){
            radioButtonFloor = (RadioButton) findViewById(R.id.radio_white_color);
        }else if(floor.equals("สีดำ")){
            radioButtonFloor = (RadioButton) findViewById(R.id.radio_black_color);
        }else if(floor.equals("สีน้ำตาล")){
            radioButtonFloor = (RadioButton) findViewById(R.id.radio_brown_color);
        }
        radioButtonFloor.setChecked(true);
    }

    private void pressInsertOrder() {
        Button summaryPriceBtn = (Button) findViewById(R.id.addEachOrder);
        summaryPriceBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                floor = getSelectRadioGroupFloor();
                Log.i(LOG_TAG,"----floor---- " + floor);

                DW = getSelectRadioGroupDW();
                Log.i(LOG_TAG,"----DW---- " + DW);

                if(typeOfM.equals("มุ้งกรอบเหล็กเปิด") || typeOfM.equals("มุ้งกรอบเหล็กเลื่อน")){
                    specialWord = getSelectRadioGroupColor();

                }else{
                    specialWord = "";
                }
                Log.i(LOG_TAG,"----specialWord---- " + specialWord);

                CheckBox checkbox = new CheckBox(SelectListOrderActivity.this);
                if(isInputsEmpty()){
                    Toast.makeText(SelectListOrderActivity.this, "Please select all condition", Toast.LENGTH_SHORT).show();
                }else{
                    Log.i(LOG_TAG, "Record Select List >>> ");
                    specialReq = new ArrayList<String>();
                    for(int i=0;i<sizeOfspecialReq;i++){
                        checkbox = (CheckBox) findViewById(i);
                        if(checkbox.isChecked()){
                            specialReq.add(String.valueOf(checkbox.getText()));
                            Log.i(LOG_TAG,">>>>>>> " + checkbox.getText());
                        }
                    }
                    Log.i(LOG_TAG,"specialReq >>> "+specialReq);

                if(specialReq.contains(getString(R.string.special_req_sharp))){
                    specialReq.clear();
                    specialReq.add(getString(R.string.special_req_sharp));
                }

                totalPrice = getTotalPrice(specialReq,specialWord);
                    if(eachOrderModelFromDB.getID() != null){
                        Log.i(LOG_TAG,"update eachOrderModel to DB");
                        updateEachOrderModelToDB();
                        updateOrderModelToDB(totalPrice,"update");

                    }else{
                        Log.i(LOG_TAG,"insert eachOrderModel to DB");
                        insertEachOrderModelToDB();
                        updateOrderModelToDB(totalPrice,"insert");
                    }

                    gotoCreateOrderActivity();
                    Log.i(LOG_TAG,"Floor : " + floor + " position : " + position + " DW : " + DW +" typeOfM : " + typeOfM + " specialWord : " + specialWord + " specialReq : " + specialReq);
                    Log.i(LOG_TAG,"totalprice : " + totalPrice);
                    Log.i(LOG_TAG,"pricePer1mm : " + pricePer1mm);
                }

            }
        });
    }

    private String getSelectRadioGroupFloor(){
        radioGroupFloor = (RadioGroup) findViewById(R.id.radFloor);
        int selectedId = radioGroupFloor.getCheckedRadioButtonId();
        radioButtonFloor = (RadioButton) findViewById(selectedId);
        String returnStr = radioButtonFloor.getText().toString();
        return returnStr.substring(returnStr.length()-1,returnStr.length());
    }

    private String getSelectRadioGroupDW(){
        radioGroupDW = (RadioGroup) findViewById(R.id.radDW);
        int selectedId = radioGroupDW.getCheckedRadioButtonId();
        radioButtonDW = (RadioButton) findViewById(selectedId);
        return radioButtonDW.getText().toString();
    }

    private String getSelectRadioGroupColor(){
        radioGroupFloor = (RadioGroup) findViewById(R.id.radColor);
        int selectedId = radioGroupFloor.getCheckedRadioButtonId();
        radioButtonFloor = (RadioButton) findViewById(selectedId);
        return radioButtonFloor.getText().toString();
    }

    private void updateOrderModelToDB(Double totalPrice,String activityDB) {
        sqlLite = new SQLiteUtil(SelectListOrderActivity.this);
        orderModelFromDB = new OrderModel();
        orderModelFromDB = sqlLite.getOrderModelByQuotationNo(shared_quotationNo);
        double tempTotalPrice=0.0;
        Double tempRealTotalPrice=0.0;
        Log.i(LOG_TAG,"totalPrice : " + totalPrice);
        Log.i(LOG_TAG,"discount : " + orderModelFromDB.getDiscount());
        Log.i(LOG_TAG,"realTotalPrice : " + orderModelFromDB.getRealTotalPrice());
        if(activityDB.equals("insert")){
            tempRealTotalPrice = orderModelFromDB.getRealTotalPrice();
        }else{
            tempRealTotalPrice = orderModelFromDB.getRealTotalPrice() - eachOrderModelFromDB.getTotalPrice();
        }
        if(orderModelFromDB.getDiscount() == null){
            tempTotalPrice = tempRealTotalPrice+totalPrice;
        }else{
            tempTotalPrice = tempRealTotalPrice+totalPrice-orderModelFromDB.getDiscount();
        }
        Log.i(LOG_TAG,"tempTotalPrice : " + tempTotalPrice);
        if(tempTotalPrice<0)
            tempTotalPrice =0.0;
        orderModelFromDB.setTotalPrice(tempTotalPrice);
        orderModelFromDB.setRealTotalPrice(tempRealTotalPrice+totalPrice);
        sqlLite.updateOrderModel(orderModelFromDB);
    }

    private Double getTotalPrice(ArrayList<String>  specialReq,String specialWord){
        Double areaCal = 0.0;
        Double width = Double.parseDouble(txtWidth.getText().toString());
        Double height = Double.parseDouble(txtHeight.getText().toString());
        Log.i(LOG_TAG, "width : " + width + " height : " + height);
        areaCal = (width/100)*(height/100);
        Double tempPrice=0.0;
        Log.i(LOG_TAG,"areaCal Before : " + areaCal);
        if(areaCal<0.5){
            areaCal = 0.5;
        }else if((areaCal > 0.5) && (areaCal<1)){
            areaCal = 1.0;
        }
        Log.i(LOG_TAG,"areaCal After : " + areaCal);
        Log.i(LOG_TAG,"specialReq : " + specialReq);


        if(typeOfM.equals("มุ้งกรอบเหล็กเปิด")){
            tempPrice = Double.parseDouble(getString(R.string.price_of_mung_krob_lek_perd));
            if(specialReq.contains(getString(R.string.special_req_pet_screen_mung_krob))){
                tempPrice += Double.parseDouble(getString(R.string.price_of_pet_screen_mung_krob));
            }
            if(specialReq.contains(getString(R.string.special_req_key))){
                tempPrice += Double.parseDouble(getString(R.string.price_of_key));
            }
            if(specialReq.contains(getString(R.string.special_req_acrylic))){
                tempPrice += Double.parseDouble(getString(R.string.price_of_acrylic));
            }
            if(specialReq.contains(getString(R.string.special_req_door_for_pets))){
                tempPrice += Double.parseDouble(getString(R.string.price_of_door_for_pets));
            }
            if(specialReq.contains(getString(R.string.special_req_kor_sub))){
                tempPrice += Double.parseDouble(getString(R.string.price_of_kor_sub));
            }
            Log.i(LOG_TAG,"temp price : " + tempPrice);
            totalPrice = tempPrice;
        }else if(typeOfM.equals("มุ้งกรอบเหล็กเลื่อน")){
            tempPrice = Double.parseDouble(getString(R.string.price_of_mung_krob_lek_leuan));
            if(specialReq.contains(getString(R.string.special_req_pet_screen_mung_krob))){
                tempPrice += Double.parseDouble(getString(R.string.price_of_pet_screen_mung_krob));
            }
            if(specialReq.contains(getString(R.string.special_req_key))){
                tempPrice += Double.parseDouble(getString(R.string.price_of_key));
            }
            if(specialReq.contains(getString(R.string.special_req_acrylic))){
                tempPrice += Double.parseDouble(getString(R.string.price_of_acrylic));
            }
            if(specialReq.contains(getString(R.string.special_req_door_for_pets))){
                tempPrice += Double.parseDouble(getString(R.string.price_of_door_for_pets));
            }
            if(specialReq.contains(getString(R.string.special_req_kor_sub))){
                tempPrice += Double.parseDouble(getString(R.string.price_of_kor_sub));
            }
            if(specialReq.contains(getString(R.string.special_req_meu_jub_fung))){
                tempPrice += Double.parseDouble(getString(R.string.price_of_meu_jub_fung));
            }
            Log.i(LOG_TAG,"temp price : " + tempPrice);
            totalPrice = tempPrice;
        }else if(typeOfM.equals("มุ้งประตูเปิด")){
            tempPrice = Double.parseDouble(getString(R.string.price_of_mung_pratoo_perd));
            if(specialReq.contains(getString(R.string.special_req_pet_screen_mung_krob))){
                tempPrice += Double.parseDouble(getString(R.string.price_of_pet_screen_mung_krob));
            }
            if(specialReq.contains(getString(R.string.special_req_kor_sub))){
                tempPrice += Double.parseDouble(getString(R.string.price_of_kor_sub));
            }
            if(specialReq.contains(getString(R.string.special_req_perm_krob_pratoo))){
                tempPrice += Double.parseDouble(getString(R.string.price_of_perm_krob_pratoo));
            }

            Log.i(LOG_TAG,"temp price : " + tempPrice);
            totalPrice = tempPrice;
        }else if(typeOfM.equals("มุ้งเลื่อน")){
            tempPrice = Double.parseDouble(getString(R.string.price_of_mung_leuan));
            if(specialReq.contains(getString(R.string.special_req_pet_screen_normal))){
                tempPrice += Double.parseDouble(getString(R.string.price_of_pet_screen_normal));
            }
            if(specialReq.contains(getString(R.string.special_req_perm_rang))){
                tempPrice += Double.parseDouble(getString(R.string.price_of_perm_rang));
            }
            if(specialReq.contains(getString(R.string.special_req_perm_krob))){
                tempPrice += Double.parseDouble(getString(R.string.price_of_perm_krob));
            }
            Log.i(LOG_TAG,"temp price : " + tempPrice);
            totalPrice = areaCal * tempPrice;
            if(specialReq.contains(getString(R.string.special_req_lock_mung))){
                totalPrice += Double.parseDouble(getString(R.string.price_of_lock_mung));
            }
            if(specialReq.contains(getString(R.string.special_req_kor_sub))){
                totalPrice += Double.parseDouble(getString(R.string.price_of_kor_sub));
            }

        }else if(typeOfM.equals("มุ้งเปิด")){
            tempPrice = Double.parseDouble(getString(R.string.price_of_mung_perd));
            if(specialReq.contains(getString(R.string.special_req_pet_screen_normal))) {
                tempPrice += Double.parseDouble(getString(R.string.price_of_pet_screen_normal));
            }

            if(specialReq.contains(getString(R.string.special_req_perm_krob))){
                tempPrice += Double.parseDouble(getString(R.string.price_of_perm_krob));
            }
            if(specialReq.contains(getString(R.string.special_req_ban_kred))){
                tempPrice += Double.parseDouble(getString(R.string.price_of_ban_kred));
            }
            Log.i(LOG_TAG,"temp price : " + tempPrice);
            totalPrice = areaCal * tempPrice;

            if(specialReq.contains(getString(R.string.special_req_kor_sub))){
                totalPrice += Double.parseDouble(getString(R.string.price_of_kor_sub));
            }

        }else if(typeOfM.equals("มุ้ง Fix")){
            tempPrice = Double.parseDouble(getString(R.string.price_of_mung_fix));
            if(specialReq.contains(getString(R.string.special_req_pet_screen_normal))) {
                tempPrice += Double.parseDouble(getString(R.string.price_of_pet_screen_normal));
            }
            if(specialReq.contains(getString(R.string.special_req_perm_krob))){
                tempPrice += Double.parseDouble(getString(R.string.price_of_perm_krob));
            }
            Log.i(LOG_TAG,"temp price : " + tempPrice);
            totalPrice = areaCal * tempPrice;
        }else if(typeOfM.equals("มุ้งจีบ")){
            tempPrice = Double.parseDouble(getString(R.string.price_of_mung_jeeb));
            Log.i(LOG_TAG,"temp price : " + tempPrice);
            totalPrice = areaCal * tempPrice;
        }else if(typeOfM.equals("มุ้งจีบรางเตี้ย")){
            tempPrice = Double.parseDouble(getString(R.string.price_of_mung_jeeb_rang_tere));
            Log.i(LOG_TAG,"temp price : " + tempPrice);
            totalPrice = areaCal * tempPrice;
        }else if(typeOfM.equals("มุ้งจีบ WALKER")){
            tempPrice = Double.parseDouble(getString(R.string.price_of_mung_jeeb_walker));
            Log.i(LOG_TAG,"temp price : " + tempPrice);
            totalPrice = areaCal * tempPrice;
        }
        pricePer1mm = tempPrice;
        return totalPrice;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    private void updateEachOrderModelToDB() {
        EachOrderModel eachOrderModel = new EachOrderModel();
        eachOrderModel.setID(eachOrderModelFromDB.getID());
        eachOrderModel.setQuotationNo(shared_quotationNo);
        eachOrderModel.setFloor(floor);
        eachOrderModel.setPosition(position);
        eachOrderModel.setDw(DW);
        eachOrderModel.setTypeOfM(typeOfM);
        eachOrderModel.setSpecialWord(specialWord);
        eachOrderModel.setSpecialReq(specialReq);
        eachOrderModel.setWidth(Double.parseDouble(txtWidth.getText().toString()));
        eachOrderModel.setHeight(Double.parseDouble(txtHeight.getText().toString()));
        eachOrderModel.setTotalPrice(totalPrice);
        eachOrderModel.setPricePer1mm(pricePer1mm);
        eachOrderModel.setSpecialWordReport(getSpecialWordReport(specialWord,specialReq));
        sqlLite = new SQLiteUtil(SelectListOrderActivity.this);
        sqlLite.updateEachOrderModel(eachOrderModel);
    }

    private String getSpecialWordReport(String specialWord,ArrayList<String> specialReq) {
        if(specialWord.equals("")){
            return specialReq.toString().replace("[", "").replace("]", "").replace(","," +");
        }else if(specialReq.toString().equals("[]")){
            return specialWord;
        }else{
            return specialWord + " + " + specialReq.toString().replace("[", "").replace("]", "").replace(","," +");
        }
    }

    private void insertEachOrderModelToDB() {
        EachOrderModel eachOrderModel = new EachOrderModel();
        eachOrderModel.setQuotationNo(shared_quotationNo);
        eachOrderModel.setFloor(floor);
        eachOrderModel.setPosition(position);
        eachOrderModel.setDw(DW);
        eachOrderModel.setTypeOfM(typeOfM);
        eachOrderModel.setSpecialWord(specialWord);
        eachOrderModel.setSpecialReq(specialReq);
        eachOrderModel.setWidth(Double.parseDouble(txtWidth.getText().toString()));
        eachOrderModel.setHeight(Double.parseDouble(txtHeight.getText().toString()));
        eachOrderModel.setTotalPrice(totalPrice);
        eachOrderModel.setPricePer1mm(pricePer1mm);
        eachOrderModel.setSpecialWordReport(getSpecialWordReport(specialWord,specialReq));
        sqlLite = new SQLiteUtil(SelectListOrderActivity.this);
        sqlLite.insertEachOrderModel(eachOrderModel);
    }

    private void setSelectedButtonSpecial(ArrayList<String> groupSpeacial,ArrayList<String> selectSpecialReq) {

        Log.i(LOG_TAG,"specialReq : " + selectSpecialReq);

        sizeOfspecialReq = groupSpeacial.size();
        LinearLayout specialOrderLinear = (LinearLayout) findViewById(R.id.specialOrderLinear);
        specialOrderLinear.setOrientation(LinearLayout.VERTICAL);
        specialOrderLinear.removeAllViews();
        RadioGroup.LayoutParams params
                = new RadioGroup.LayoutParams(this, null);
        params.setMargins(0, 10, 0, 10);
        CheckBox specialCheckbox;
        int i=0;
        for(String temp : groupSpeacial){
            specialCheckbox = new CheckBox(this);
            specialCheckbox.setLayoutParams(params);
            specialCheckbox.setTextSize(20);
            specialCheckbox.setText(temp);
            Log.i(LOG_TAG,"temp : " + temp);
            if(selectSpecialReq != null){
                if(selectSpecialReq.toString().replace(", ",",").contains(temp)){
                    Log.i(LOG_TAG,"check true");
                    specialCheckbox.setChecked(true);
                }else{
                    Log.i(LOG_TAG,"check false");
                    specialCheckbox.setChecked(false);
                }
            }

            specialCheckbox.setId(i);
            specialOrderLinear.addView(specialCheckbox);
            i++;
        }
    }

    private void setTypeOfMSpinner(String selectTypeOfM) {
        Spinner typeOfMDropdown = (Spinner)findViewById(R.id.spinnerTypeOfM);
        String[] typeOfMItems = getResources().getStringArray(R.array.type_of_m_array);
        ArrayAdapter<String> typeOfMAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, typeOfMItems);
        typeOfMDropdown.setAdapter(typeOfMAdapter);
        grobalSpecialReq = null;
        if(!selectTypeOfM.equals(CREATE_NEW_EACH_ORDER_MODEL)){
            int indexPos = Arrays.asList(typeOfMItems).indexOf(selectTypeOfM);
            typeOfMDropdown.setSelection(indexPos);
            grobalSpecialReq =  eachOrderModelFromDB.getSpecialReq();
        }

        typeOfMDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                ArrayList<String> groupSpeacial = new ArrayList<String>();
                typeOfM = String.valueOf(parent.getItemAtPosition(pos));
                posTypeOfM = parent.getSelectedItemPosition();
                specialWord = "";

                Log.i(LOG_TAG,"TypeOfM >>>>> position : " + posTypeOfM + " item : " + typeOfM);
                if(typeOfM.equals("มุ้งกรอบเหล็กเปิด")){
                    setRadioGroupColorVisible();
                    groupSpeacial.addAll(Arrays.asList(getResources().getStringArray(R.array.special_req_of_mung_krob_lek_perd_array)));
                }else if(typeOfM.equals("มุ้งกรอบเหล็กเลื่อน")){
                    setRadioGroupColorVisible();
                    groupSpeacial.addAll(Arrays.asList(getResources().getStringArray(R.array.special_req_of_mung_krob_lek_leuan_array)));
                }else if(typeOfM.equals("มุ้งประตูเปิด")){
                    setRadioGroupColorInvisible();
                    groupSpeacial.addAll(Arrays.asList(getResources().getStringArray(R.array.special_req_of_mung_pratoo_perd_array)));
                }else if(typeOfM.equals("มุ้งเลื่อน")){
                    setRadioGroupColorInvisible();
                    groupSpeacial.addAll(Arrays.asList(getResources().getStringArray(R.array.special_req_of_mung_leuan_array)));
                }else if(typeOfM.equals("มุ้งเปิด")){
                    setRadioGroupColorInvisible();
                    groupSpeacial.addAll(Arrays.asList(getResources().getStringArray(R.array.special_req_of_mung_perd_array)));
                }else if(typeOfM.equals("มุ้ง Fix")){
                    setRadioGroupColorInvisible();
                    groupSpeacial.addAll(Arrays.asList(getResources().getStringArray(R.array.special_req_of_mung_fix_array)));
                }else if(typeOfM.equals("มุ้งจีบ")){
                    setRadioGroupColorInvisible();
                    groupSpeacial.addAll(Arrays.asList(getResources().getStringArray(R.array.special_req_of_mung_jeeb)));
                }else if(typeOfM.equals("มุ้งจีบรางเตี้ย")){
                    setRadioGroupColorInvisible();
                    groupSpeacial.addAll(Arrays.asList(getResources().getStringArray(R.array.special_req_of_mung_jeeb_rang_tere)));
                }else if(typeOfM.equals("มุ้งจีบ WALKER")){
                    setRadioGroupColorInvisible();
                    groupSpeacial.addAll(Arrays.asList(getResources().getStringArray(R.array.special_req_of_mung_jeeb_walker)));
                }
                    setSelectedButtonSpecial(groupSpeacial,grobalSpecialReq);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setRadioGroupColorVisible() {
        radioGroupColor = (RadioGroup) findViewById(R.id.radColor);
        radioGroupColor.setVisibility(View.VISIBLE);
    }

    private void setRadioGroupColorInvisible() {
        radioGroupColor = (RadioGroup) findViewById(R.id.radColor);
        radioGroupColor.setVisibility(View.INVISIBLE);
    }

    private void setPositionSpinner(String SelectPosition) {
        Spinner positionDropdown = (Spinner)findViewById(R.id.spinnerPosition);
        String[] positionItems = getResources().getStringArray(R.array.position_array);
        ArrayAdapter<String> postionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, positionItems);
        positionDropdown.setAdapter(postionAdapter);

        if(!SelectPosition.equals(CREATE_NEW_EACH_ORDER_MODEL)){
            int indexPos = Arrays.asList(positionItems).indexOf(SelectPosition);
            positionDropdown.setSelection(indexPos);
        }

        positionDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                position = String.valueOf(parent.getItemAtPosition(pos));
                posPosition = parent.getSelectedItemPosition();
                Log.i(LOG_TAG,"Position >>>>> position : " + position + " item : " + posPosition);

            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private boolean isInputsEmpty() {
        return  posPosition == 0  || posTypeOfM == 0  || isEmpty(txtWidth) || isEmpty(txtHeight);
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    private void gotoCreateOrderActivity() {
        Intent myIntent = new Intent(this, CreateOrderActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(myIntent);
        finish();
    }


}
