package app.vrpro.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import app.vrpro.Model.EachOrderModel;
import app.vrpro.R;

/**
 * Created by Plooer on 6/25/2017 AD.
 */

public class ListEachOrderAdapter extends BaseAdapter{
    private final String LOG_TAG = "ListOrderAdapter";

    private Context context;
    private List<EachOrderModel> eachOrderModelList;

    public ListEachOrderAdapter (Context context, List<EachOrderModel> eachOrderModelList) {
        this.context = context;
        this.eachOrderModelList = eachOrderModelList;
    }

    @Override
    public int getCount() {
        return eachOrderModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return eachOrderModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final EachOrderModel eachOrderModel = (EachOrderModel) getItem(position);
        ListEachOrderAdapter.ViewHolder holder = null;
        if(convertView == null)
            convertView = layoutInflater.inflate(R.layout.each_order_in_order_row, null);
        holder = new ListEachOrderAdapter.ViewHolder();
        holder.txtFloor = (TextView) convertView.findViewById(R.id.floor);
        holder.txtPositon = (TextView) convertView.findViewById(R.id.position);
        holder.txtDW = (TextView) convertView.findViewById(R.id.dw);
        holder.txtTypeOfM = (TextView) convertView.findViewById(R.id.typeOfM);
        holder.txtSpecialWord = (TextView) convertView.findViewById(R.id.specialWord);
        holder.txtTotalPrices = (TextView) convertView.findViewById(R.id.totalPrices);

        holder.txtFloor.setText(eachOrderModel.getFloor());
        holder.txtPositon.setText(eachOrderModel.getPosition());
        holder.txtDW.setText(eachOrderModel.getDw());
        holder.txtTypeOfM.setText(eachOrderModel.getTypeOfM());

        Log.i(LOG_TAG,"eachOrderModel.getSpecialReq() : "  + eachOrderModel.getSpecialReq());
        if(eachOrderModel.getSpecialWord().equals("")){
            holder.txtSpecialWord.setText("# / " + eachOrderModel.getSpecialReq().toString().replace("[", "").replace("]", ""));
        }else if(eachOrderModel.getSpecialReq().isEmpty()){
            holder.txtSpecialWord.setText(eachOrderModel.getSpecialWord());
        }
        else{
            holder.txtSpecialWord.setText(eachOrderModel.getSpecialWord() + " / " + eachOrderModel.getSpecialReq().toString().replace("[", "").replace("]", ""));
        }

        holder.txtTotalPrices.setText(String.valueOf(eachOrderModel.getTotolPrice()));

        return convertView;
    }

    private static class ViewHolder {
        TextView txtFloor;
        TextView txtPositon;
        TextView txtDW;
        TextView txtTypeOfM;
        TextView txtSpecialWord;
        TextView txtTotalPrices;
    }
}
