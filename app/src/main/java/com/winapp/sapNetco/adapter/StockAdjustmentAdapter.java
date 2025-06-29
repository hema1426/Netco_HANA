package com.winapp.sapNetco.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.winapp.sapNetco.R;
import com.winapp.sapNetco.model.StockAdjustmentModuleModel;
import com.winapp.sapNetco.model.StockAdjustmentPreviewModel;
import com.winapp.sapNetco.utils.Constants;
import com.winapp.sapNetco.utils.SessionManager;
import com.winapp.sapNetco.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StockAdjustmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    public static ArrayList<StockAdjustmentModuleModel> stockAdjustOrderList;
    public static ArrayList<StockAdjustmentModuleModel> salesOrderFilterList;
    Context mContext;
    CallBack callBack;

    private String companyId;
    private SessionManager session;
    private HashMap<String, String> user;
    private ArrayList<StockAdjustmentPreviewModel.StockAdjustList> adjustnewList;
    private String locationCode;

    public StockAdjustmentAdapter(Context context, RecyclerView mRecyclerView,
                                  ArrayList<StockAdjustmentModuleModel> salesOrderList, CallBack callBack) {

        this.stockAdjustOrderList = salesOrderList;
        this.salesOrderFilterList=salesOrderList;
        this.mContext=context;
        this.callBack=callBack;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        return stockAdjustOrderList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.stock_adjust_item, parent, false);
            return new adjustViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder  viewHolder,@SuppressLint("RecyclerView") int position) {
        if ( viewHolder instanceof adjustViewHolder) {
            session = new SessionManager(mContext);
            user = session.getUserDetails();
            companyId = user.get(SessionManager.KEY_COMPANY_CODE);
            locationCode=user.get(SessionManager.KEY_LOCATION_CODE);

            StockAdjustmentModuleModel stockAdjustmentModuleModel = stockAdjustOrderList.get(position);
            ((adjustViewHolder) viewHolder).date.setText(stockAdjustmentModuleModel.getDate());
            ((adjustViewHolder) viewHolder).number.setText(stockAdjustmentModuleModel.getNumber());

            if (stockAdjustmentModuleModel.getNetTotal()!=null && !stockAdjustmentModuleModel.getNetTotal().equals("null")){
                ((adjustViewHolder) viewHolder).netTotal.setText("$ "+Utils.twoDecimalPoint(Double.parseDouble(stockAdjustmentModuleModel.getNetTotal())));
            }else {
                ((adjustViewHolder) viewHolder).netTotal.setText("$ "+"0.00");
            }

           /* if (position % 2 ==1){
                ((SalesOrderViewHolder) viewHolder).mainCard.setBackgroundColor(Color.parseColor("#f3f3f3"));
            }else {
                ((SalesOrderViewHolder) viewHolder).mainCard.setBackgroundColor(Color.parseColor("#ffffff"));
            }*/


            switch (stockAdjustmentModuleModel.getDoStatus()) {
                case "C":
                    ((adjustViewHolder) viewHolder).status.setText("Closed");
                    ((adjustViewHolder) viewHolder).statusLayout.setBackgroundResource(R.drawable.invoice_closed);
                    ((adjustViewHolder) viewHolder).indicator.setBackgroundResource(R.drawable.invoice_closed);
                    break;
                case "O":
                    ((adjustViewHolder) viewHolder).status.setText("Open");
                    ((adjustViewHolder) viewHolder).statusLayout.setBackgroundResource(R.drawable.invoice_status_paid);
                    ((adjustViewHolder) viewHolder).indicator.setBackgroundResource(R.drawable.invoice_status_paid);
                    break;
                default:
                    ((adjustViewHolder) viewHolder).status.setText("Open");
                    ((adjustViewHolder) viewHolder).statusLayout.setBackgroundResource(R.drawable.invoice_status_paid);
                    ((adjustViewHolder) viewHolder).indicator.setBackgroundResource(R.drawable.invoice_status_paid);
                    break;
            }

            ((adjustViewHolder) viewHolder).showHideBottomLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (((adjustViewHolder) viewHolder).showHideBottomLayout.getTag().equals("hide")){
                        ((adjustViewHolder) viewHolder).bottomLayout.setVisibility(View.VISIBLE);
                        ((adjustViewHolder) viewHolder).showHideBottomLayout.setTag("show");
                        ((adjustViewHolder) viewHolder).showHideBottomLayout.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_keyboard_arrow_up_24));
                        try {
                            if (stockAdjustmentModuleModel.getStockAdjustList() != null &&
                                    stockAdjustmentModuleModel.getStockAdjustList().size() > 0) {

                                    setAdjustAdapter(viewHolder, position, stockAdjustmentModuleModel.getStockAdjustList());
                                    ((adjustViewHolder) viewHolder).progressLayout.setVisibility(View.GONE);
                                    ((adjustViewHolder) viewHolder).mainLayout.setVisibility(View.VISIBLE);
                                } else {
                                    getStockAdjustDetails(stockAdjustmentModuleModel.getCode(),
                                            viewHolder, position, stockAdjustmentModuleModel);
                                    stockAdjustmentModuleModel.setShow(true);
                                }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        stockAdjustmentModuleModel.setShow(false);
                        ((adjustViewHolder) viewHolder).bottomLayout.setVisibility(View.GONE);
                        ((adjustViewHolder) viewHolder).showHideBottomLayout.setTag("hide");
                        ((adjustViewHolder) viewHolder).showHideBottomLayout.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_keyboard_arrow_down_24));
                    }
                }
            });

            if (stockAdjustmentModuleModel.isShow()){
                ((adjustViewHolder) viewHolder).bottomLayout.setVisibility(View.VISIBLE);
                ((adjustViewHolder) viewHolder).showHideBottomLayout.setTag("show");
                ((adjustViewHolder) viewHolder).showHideBottomLayout.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_keyboard_arrow_up_24));
                try {
                    setAdjustAdapter(viewHolder,position,stockAdjustmentModuleModel.getStockAdjustList());
                    ((adjustViewHolder) viewHolder).progressLayout.setVisibility(View.GONE);
                    ((adjustViewHolder) viewHolder).mainLayout.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                ((adjustViewHolder) viewHolder).bottomLayout.setVisibility(View.GONE);
                ((adjustViewHolder) viewHolder).showHideBottomLayout.setTag("hide");
                ((adjustViewHolder) viewHolder).showHideBottomLayout.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_keyboard_arrow_down_24));
            }


            ((adjustViewHolder) viewHolder).moreOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callBack.showMoreOption(stockAdjustmentModuleModel.getCode(),((adjustViewHolder) viewHolder).status.getText().toString());
                }
            });

        } else if (viewHolder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder)  viewHolder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return stockAdjustOrderList == null ? 0 : stockAdjustOrderList.size();
    }

    public void setLoaded() {
        isLoading = false;
       // callBack.calculateNetTotal(deliveryOrderList);
    }

    static class adjustViewHolder extends RecyclerView.ViewHolder {

        private TextView date;
        private TextView number;
        private TextView netTotal;
        private CardView mainCard;
        private TextView status;
        private ImageView moreOption;
        private LinearLayout statusLayout;
        private View indicator;
        private RecyclerView productListView;
        private ImageView showHideBottomLayout;
        private LinearLayout mainLayout;
        private LinearLayout progressLayout;
        private LinearLayout bottomLayout;
        public adjustViewHolder(View view) {
            super(view);
             
             
            date=view.findViewById(R.id.date_adjus_item);
            number =view.findViewById(R.id.adjust_no_item);
            netTotal=view.findViewById(R.id.net_total_adjus_item);
            mainCard=view.findViewById(R.id.cardlist_item);
            productListView=view.findViewById(R.id.adjustList);
            status=view.findViewById(R.id.status_adjus_item);
            moreOption=view.findViewById(R.id.more_adjust_item);
            statusLayout=view.findViewById(R.id.status_layout);
            indicator=view.findViewById(R.id.indicator);
            showHideBottomLayout=view.findViewById(R.id.show_hide_adj_item);
            mainLayout=view.findViewById(R.id.main_layout);
            progressLayout=view.findViewById(R.id.progress_layout);
            bottomLayout=view.findViewById(R.id.bottom_layout);
        }
    }
    private void getStockAdjustDetails(String number, RecyclerView.ViewHolder  viewHolder,
                                       int position, StockAdjustmentModuleModel stockAdjustmentModuleModel)
            throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("GoodReceiptNo",number);

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        String url= Utils.getBaseUrl(mContext) +"GoodsReceiptDetails";

        Log.w("Given_url:",url+jsonObject);

        adjustnewList =new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                response -> {
                    try{
                        Log.w("adjust_Details:",response.toString());
                        String statusCode=response.optString("statusCode");
                        if (statusCode.equals("1")) {
                            JSONArray responseData = response.getJSONArray("responseData");
                            JSONObject object = responseData.optJSONObject(0);

                            StockAdjustmentPreviewModel model = new StockAdjustmentPreviewModel();
                            model.setSoNumber(object.optString("goodsReceiptNo"));
                            model.setSoDate(object.optString("docDate"));
//                            model.setAddress(object.optString("address1") + object.optString("address2") + object.optString("address3"));
//                            model.setDeliveryAddress(model.getAddress());
//                            model.setSubTotal(object.optString("subTotal"));
//                            model.setNetTax(object.optString("taxTotal"));
                            model.setNetTotal(object.optString("docTotal"));
//                            model.setTaxType(object.optString("taxType"));
//                            model.setTaxValue(object.optString("taxPerc"));
//                            model.setOutStandingAmount(object.optString("balanceAmount"));
//                            Utils.setInvoiceMode("SalesOrder");
//                            model.setBillDiscount(object.optString("billDiscount"));
//                            model.setItemDiscount(object.optString("totalDiscount"));
//                            model.setAddress1(object.optString("address1"));
//                            model.setAddress2(object.optString("address2"));
//                            model.setAddress3(object.optString("address3"));
//                            model.setAddressstate(object.optString("block")+" "+object.optString("street")+" "
//                                    +object.optString("city"));
//                            model.setAddresssZipcode(object.optString("countryName")+" "+object.optString("state")+" "
//                                    +object.optString("zipcode"));

                       //     String signFlag = object.optString("signFlag");
//                            if (signFlag.equals("Y")) {
//                                String signature = object.optString("signature");
//                                Utils.setSignature(signature);
//                            } else {
//                                Utils.setSignature("");
//                            }
                            JSONArray detailsArray = object.optJSONArray("goodsReceiptDetails");

                            for (int i=0;i<detailsArray.length();i++){
                                JSONObject detailObject=detailsArray.optJSONObject(i);

                                    StockAdjustmentPreviewModel.StockAdjustList salesListModel =
                                            new StockAdjustmentPreviewModel.StockAdjustList();

                                    salesListModel.setProductCode(detailObject.optString("productCode"));
                                    salesListModel.setDescription( detailObject.optString("productName"));
                                    salesListModel.setLqty(detailObject.optString("unitQty"));
                                    salesListModel.setCqty(detailObject.optString("cartonQty"));
                                    salesListModel.setNetQty(detailObject.optString("quantity"));
                                    salesListModel.setCartonPrice(detailObject.optString("cartonPrice"));
                                    salesListModel.setUnitPrice(detailObject.optString("price"));
//                                    salesListModel.setGrossPrice(detailObject.optString("grossPrice"));

                                    double qty=Double.parseDouble(detailObject.optString("quantity"));
                                    double price=Double.parseDouble(detailObject.optString("price"));

                                    double nettotal=qty * price;
                                    salesListModel.setTotal(String.valueOf(nettotal));
                                    salesListModel.setPricevalue(String.valueOf(price));

                                    salesListModel.setPcsperCarton(detailObject.optString("pcsPerCarton"));
                                    salesListModel.setItemtax(detailObject.optString("totalTax"));
                                    salesListModel.setSubTotal(detailObject.optString("subTotal"));
                                    adjustnewList.add(salesListModel);
                                }
                                model.setSalesList(adjustnewList);
                        }

                        if (adjustnewList.size()>0){
                            ((adjustViewHolder) viewHolder).progressLayout.setVisibility(View.GONE);
                            ((adjustViewHolder) viewHolder).mainLayout.setVisibility(View.VISIBLE);
                            stockAdjustmentModuleModel.setStockAdjustList(adjustnewList);
                            setAdjustAdapter(viewHolder,position,adjustnewList);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }, error -> {
            // Do something when error occurred
            Log.w("Error_throwing:",error.toString());
        }){
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<>();
                String creds = String.format("%s:%s", Constants.API_SECRET_CODE, Constants.API_SECRET_PASSWORD);
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }
        };
        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }
            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }
            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonObjectRequest);
    }
    public void setAdjustAdapter(@NonNull RecyclerView.ViewHolder  viewHolder, 
                                 int position, ArrayList<StockAdjustmentPreviewModel.StockAdjustList> adjustList){
        ((adjustViewHolder) viewHolder).productListView.setHasFixedSize(true);
        ((adjustViewHolder) viewHolder).productListView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        StockAdjustPrintPreviewAdapter adapter=new StockAdjustPrintPreviewAdapter(mContext, adjustList);
        ((adjustViewHolder) viewHolder).productListView.setAdapter(adapter);
        // notifyDataSetChanged();
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar1);
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        Log.w("Given_date_printed:",date);
        return date;
    }

    public interface CallBack {
      //  void calculateNetTotal(ArrayList<SalesOrderModel> salesList);
        void showMoreOption(String deliveryorderId, String status);
    }

    public void filterList(ArrayList<StockAdjustmentModuleModel> filterdNames) {
        stockAdjustOrderList = filterdNames;
        notifyDataSetChanged();
    }

    public static ArrayList<StockAdjustmentModuleModel> getNotalInvoiceList(){
        return stockAdjustOrderList;
    }

    public static ArrayList<StockAdjustmentModuleModel> getStockAdjustOrderList(){
        return salesOrderFilterList;
    }

}