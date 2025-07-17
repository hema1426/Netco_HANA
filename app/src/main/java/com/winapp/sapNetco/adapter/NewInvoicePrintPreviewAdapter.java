package com.winapp.sapNetco.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.winapp.sapNetco.R;
import com.winapp.sapNetco.model.InvoicePrintPreviewModel;
import com.winapp.sapNetco.utils.Utils;

import java.util.ArrayList;

public class NewInvoicePrintPreviewAdapter extends RecyclerView.Adapter<NewInvoicePrintPreviewAdapter.ViewHolder> {

    private ArrayList<InvoicePrintPreviewModel.InvoiceList> invoiceLists;
    private String companyName;
    private Context context;
    View view;
    private String printView;
    ArrayList<InvoicePrintPreviewModel> invoiceHeaderDetails ;
    public NewInvoicePrintPreviewAdapter(Context context, ArrayList<InvoicePrintPreviewModel.InvoiceList> invoices,
                                         String printView , String companyName,ArrayList<InvoicePrintPreviewModel> invoiceHeader) {
        this.context=context;
        this.invoiceLists = invoices;
        this.printView=printView;
        this.companyName=companyName;
        this.invoiceHeaderDetails=invoiceHeader;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.invoice_print_preview_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        InvoicePrintPreviewModel.InvoiceList invoiceList=invoiceLists.get(position);
        viewHolder.slNo.setText(String.valueOf(position+1));
//        viewHolder.product.setText(invoiceList.getDescription()+" ("+invoiceList.getUomCode()+")");
        if(invoiceHeaderDetails.get(0).getDocType().equals("S")){
            viewHolder.price.setVisibility(View.GONE);
            viewHolder.netQty.setVisibility(View.GONE);
        }else{
            viewHolder.price.setVisibility(View.VISIBLE);
            viewHolder.netQty.setVisibility(View.VISIBLE);

            if (invoiceList.getSaleType().equals("Return")){
                viewHolder.netQty.setText((int)Double.parseDouble(invoiceList.getNetQuantity())+" (as Return)");
            }else if (invoiceList.getSaleType().equals("FOC")){
                viewHolder.netQty.setText((int)Double.parseDouble(invoiceList.getNetQuantity())+" ( as FOC)");
            } else if (invoiceList.getSaleType().equals("Exchange")){
                viewHolder.netQty.setText((int)Double.parseDouble(invoiceList.getNetQuantity())+" ( as Exch)");
            }else {
                viewHolder.netQty.setText((int)Double.parseDouble(invoiceList.getNetQuantity())+"");
            }
            viewHolder.price.setText(Utils.twoDecimalPoint(Double.parseDouble(invoiceList.getPricevalue())));
        }

        if (invoiceList.getUomCode()!=null && !invoiceList.getUomCode().equals("null") && !invoiceList.getUomCode().isEmpty()){
          if(invoiceList.getCustomerItemCode()!=null && !invoiceList.getCustomerItemCode().equals("null")
                  && !invoiceList.getCustomerItemCode().isEmpty()){
              viewHolder.product.setText(invoiceList.getDescription()+" ("+invoiceList.getUomCode()+")"+"-"+invoiceList.getCustomerItemCode());
          }else {
              viewHolder.product.setText(invoiceList.getDescription() + " (" + invoiceList.getUomCode() + ")");
          }
        }else {
            if(invoiceList.getCustomerItemCode()!=null && !invoiceList.getCustomerItemCode().equals("null")
                    && !invoiceList.getCustomerItemCode().isEmpty()){
                viewHolder.product.setText(invoiceList.getDescription()+"-"+invoiceList.getCustomerItemCode());
            }else {
                viewHolder.product.setText(invoiceList.getDescription());
            }
        }

        viewHolder.total.setText(Utils.twoDecimalPoint(Double.parseDouble(invoiceList.getTotal())));

    }

    @Override
    public int getItemCount() {
        return invoiceLists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView slNo;
        private TextView product;
        private TextView rtn;
        private TextView netQty,iss;
        private TextView price;
        private TextView total;
        public ViewHolder(View view) {
            super(view);
            slNo=view.findViewById(R.id.itemsno);
            product=view.findViewById(R.id.itemproduct);
            rtn=view.findViewById(R.id.itemrtn);
            iss=view.findViewById(R.id.itemiss);
            netQty =view.findViewById(R.id.itemnet);
            price=view.findViewById(R.id.itemprice);
            total=view.findViewById(R.id.itemtotal);
        }
    }

}