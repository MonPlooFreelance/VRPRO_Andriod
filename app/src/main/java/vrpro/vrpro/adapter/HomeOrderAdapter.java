package vrpro.vrpro.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;
import java.util.Map;

import vrpro.vrpro.R;

/**
 * Created by Plooer on 6/24/2017 AD.
 */

//public class HomeOrderAdapter extends ArrayAdapter<Map<String, Object>> {
public class HomeOrderAdapter extends ArrayAdapter<Map<String, Object>> {

    private final String LOG_TAG = "HomeOrderAdapter";

//    public HomeOrderAdapter(Context context, int resource, List<Map<String, Object>> items) {
    public HomeOrderAdapter(Context context, int resource, Integer items) {

        super(context, resource, items);
        Log.i(LOG_TAG,"HomeOrderAdapter");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i(LOG_TAG,"HomeOrderAdapter getView");
        try{
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater;
                inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.order_row, null);

                holder.txtQuatationDate = (TextView) convertView.findViewById(R.id.quatationDate);
                holder.txtQuatationNo = (TextView) convertView.findViewById(R.id.quatationNo);
                holder.txtProjectName = (TextView) convertView.findViewById(R.id.projectName);
                holder.txtCustomerName = (TextView) convertView.findViewById(R.id.customerName);
                holder.txtTotalPrice = (TextView) convertView.findViewById(R.id.totalPrice);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Map<String, Object> orderMap = getItem(position);

            Log.i(LOG_TAG,"orderMap : " + orderMap);

            if (orderMap != null) {
                Log.i(LOG_TAG,"if");
//                holder.txtQuatationDate.setText(orderMap.get("quatationDate").toString());
//                holder.txtQuatationNo.setText(orderMap.get("txtQuatationNo").toString());
//                holder.txtProjectName.setText(orderMap.get("txtProjectName").toString());
//                holder.txtCustomerName.setText(orderMap.get("txtCustomerName").toString());
//                holder.txtTotalPrice.setText(orderMap.get("txtTotalPrice").toString());

                holder.txtQuatationDate.setText("24/06/2017");
                holder.txtQuatationNo.setText("60#0-VR1043");
                holder.txtProjectName.setText("ม.คาซ่า ศิตี้ ดอนเมือง ศรีสมาน");
                holder.txtCustomerName.setText("จิราภัสร์ จิรเดชวิโรจน์");
                holder.txtTotalPrice.setText("10,000 บาท");

            }

            return convertView;
        } catch (Exception e){
            Log.e(LOG_TAG, e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static class ViewHolder {
        TextView txtQuatationDate;
        TextView txtQuatationNo;
        TextView txtProjectName;
        TextView txtCustomerName;
        TextView txtTotalPrice;
    }

}
