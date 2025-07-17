package com.winapp.sapNetco.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.winapp.sapNetco.R;

import java.util.ArrayList;

public class OutgoingReceiptInvoiceAdapter extends RecyclerView.Adapter<OutgoingReceiptInvoiceAdapter.ViewHolder> {
    private ArrayList<OutgoingReceiptsListAdapter.InvoiceModel> invoices;
    public CallBack callBack;
    private Context context;

    public OutgoingReceiptInvoiceAdapter(Context context, ArrayList<OutgoingReceiptsListAdapter.InvoiceModel> invoices) {
        this.invoices = invoices;
        this.context=context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.outgoing_invoice_items, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        OutgoingReceiptsListAdapter.InvoiceModel model= invoices.get(position);
        viewHolder.invoiceNo.setText(model.getInvoiceNo());
        viewHolder.invoiceDate.setText(model.getInvoiceDate());
        viewHolder.netAmoint.setText(model.getNetAmount());
        viewHolder.paidAmount.setText(model.getPaidAmount());
        viewHolder.balaceAmount.setText(model.getBalanceAmount());
    }

    @Override
    public int getItemCount() {
        return invoices.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView invoiceNo;
        private TextView invoiceDate;
        private TextView netAmoint;
        private TextView paidAmount;
        private TextView balaceAmount;

        public ViewHolder(View view) {
            super(view);
           invoiceNo=view.findViewById(R.id.invoice_no);
           invoiceDate=view.findViewById(R.id.invoice_date);
           netAmoint=view.findViewById(R.id.net_amount);
           paidAmount=view.findViewById(R.id.paid_amount);
           balaceAmount=view.findViewById(R.id.balance_amount);

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