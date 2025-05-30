package com.winapp.saperp.adapter;

import static com.winapp.saperp.activity.SalesOrderListActivity.shortCodeStr;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.winapp.saperp.R;
import com.winapp.saperp.activity.SalesOrderListActivity;
import com.winapp.saperp.model.CartModel;
import com.winapp.saperp.model.OrderHistoryCatalogProductModel;
import com.winapp.saperp.utils.Utils;

import java.util.ArrayList;

public class OrderHistoryProductAdapter extends RecyclerView.Adapter<OrderHistoryProductAdapter.ViewHolder> {

    private ArrayList<CartModel> OrderPdtList;
    private Context context;
    View view;
    public OrderHistoryProductAdapter(Context context, ArrayList<CartModel> sales) {
        this.context=context;
        this.OrderPdtList = sales;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (context instanceof SalesOrderListActivity){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sales_details_view_items, viewGroup, false);
        }else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sales_order_print_preview, viewGroup, false);
        }
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        CartModel products = this.OrderPdtList.get(position);

        viewHolder.slNo.setText(String.valueOf(position + 1));
        viewHolder.code.setText(products.getCART_COLUMN_PID());
        if (products.getUomCode() != null && !products.getUomCode().equals("null") && !products.getUomCode().isEmpty()) {
            viewHolder.description.setText(products.getCART_COLUMN_PNAME() + "(" + products.getUomCode() + ")");
        } else {
            viewHolder.description.setText(products.getCART_COLUMN_PNAME());
        }
        if (Double.parseDouble(products.CART_COLUMN_QTY) < 0.00) {
            viewHolder.qtyValue.setText((int) Double.parseDouble(products.CART_COLUMN_QTY) + " (as Return)");
        } else if (Double.parseDouble(products.CART_COLUMN_QTY) == 0.00) {
            viewHolder.qtyValue.setText((int) Double.parseDouble(products.CART_COLUMN_QTY) + " ( as FOC)");
        } else {
            viewHolder.qtyValue.setText((int) Double.parseDouble(products.CART_COLUMN_QTY) + "");
        }
        //  viewHolder.price.setText(Utils.twoDecimalPoint(Double.parseDouble(products.getPricevalue())));

        viewHolder.price.setText(products.getCART_COLUMN_PRICE());
        viewHolder.total.setText(Utils.twoDecimalPoint(Double.parseDouble(products.getCART_COLUMN_NET_PRICE())));
    }

    @Override
    public int getItemCount() {
        return OrderPdtList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView slNo;
        private TextView description;
        private TextView code;
        private TextView qtyValue;
        private TextView price;
        private TextView total;
        public ViewHolder(View view) {
            super(view);
            slNo=view.findViewById(R.id.sl_no);
            code=view.findViewById(R.id.code);
            description=view.findViewById(R.id.description);
            qtyValue=view.findViewById(R.id.qty);
            price=view.findViewById(R.id.price);
            total=view.findViewById(R.id.total);
        }
    }

}