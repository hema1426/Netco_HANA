package com.winapp.saperp.ReportPreview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.winapp.saperp.R;
import com.winapp.saperp.model.SettlementReceiptDetailModel;

import java.util.ArrayList;

public class ROReceiptSettleInvoiceAdapter extends RecyclerView.Adapter<ROReceiptSettleInvoiceAdapter.ViewHolder> {
    private ArrayList<SettlementReceiptDetailModel.invoiceDetailSettlement> invoices;
    public CallBack callBack;
    private Context context;

    public ROReceiptSettleInvoiceAdapter(Context context,
                                         ArrayList<SettlementReceiptDetailModel.invoiceDetailSettlement> invoices) {
        this.invoices = invoices;
        this.context=context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.receipt_settlement_invoice_items, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        SettlementReceiptDetailModel.invoiceDetailSettlement model= invoices.get(position);
        viewHolder.invoiceNo.setText(model.getInvoiceNo());
        viewHolder.invoiceDate.setText(model.getInvoiceDate());
        viewHolder.netAmoint.setText(model.getTotal());
        viewHolder.type.setText(model.getType());
//        viewHolder.paidAmount.setText(model.getPaidAmount());
    }

    @Override
    public int getItemCount() {
        return invoices.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView invoiceNo;
        private TextView invoiceDate;
        private TextView netAmoint,type;
        private TextView paidAmount;

        public ViewHolder(View view) {
            super(view);
            invoiceNo=view.findViewById(R.id.invoice_no_settle);
            invoiceDate=view.findViewById(R.id.invoice_date_settle);
            netAmoint=view.findViewById(R.id.net_amount_settle);
            type=view.findViewById(R.id.invoice_type_settle);
            //paidAmount=view.findViewById(R.id.paid_amount);

        }
    }

    public interface CallBack{
        void sortProduct(String letter);
    }

    public void resetPosition(){
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}