package app.vrpro.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import app.vrpro.Model.OrderModel;
import app.vrpro.R;
import app.vrpro.util.DecimalUtil;

/**
 * Created by Plooer on 6/24/2017 AD.
 */

public class ListOrderAdapter extends BaseAdapter {
    private final String LOG_TAG = "ListOrderAdapter";

    private Context context;
    List<OrderModel> orderModelList;

    public ListOrderAdapter (Context context, List<OrderModel> orderModelList) {
        this.context = context;
        this.orderModelList = orderModelList;
    }

    @Override
    public int getCount() {
        return orderModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return orderModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final OrderModel orderModel = (OrderModel) getItem(position);
        ListOrderAdapter.ViewHolder holder = null;
        if(convertView == null)
            convertView = layoutInflater.inflate(R.layout.order_row, null);
        holder = new ListOrderAdapter.ViewHolder();
        holder.txtQuotationDate = (TextView) convertView.findViewById(R.id.quotationDate);
        holder.txtQuotationNo = (TextView) convertView.findViewById(R.id.quotationNo);
        holder.txtProjectName = (TextView) convertView.findViewById(R.id.projectName);
        holder.txtCustomerName = (TextView) convertView.findViewById(R.id.customerName);
        holder.txtTotalPrice = (TextView) convertView.findViewById(R.id.totalPrice);

        holder.txtQuotationDate.setText(orderModel.getQuotationDate());
        holder.txtQuotationNo.setText(orderModel.getQuotationNo());
        holder.txtProjectName.setText(orderModel.getProjectName());
        holder.txtCustomerName.setText(orderModel.getCustomerName());
//        DecimalFormat formatter = new DecimalFormat("#,###.00");

        holder.txtTotalPrice.setText(String.valueOf(DecimalUtil.insertCommaDouble(orderModel.getTotalPrice())));

        return convertView;
    }

    private static class ViewHolder {
        TextView txtQuotationDate;
        TextView txtQuotationNo;
        TextView txtProjectName;
        TextView txtCustomerName;
        TextView txtTotalPrice;
    }


}
