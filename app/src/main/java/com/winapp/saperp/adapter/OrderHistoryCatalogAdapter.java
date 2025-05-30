package com.winapp.saperp.adapter;

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
import com.winapp.saperp.R;
import com.winapp.saperp.db.DBHelper;
import com.winapp.saperp.model.CartModel;
import com.winapp.saperp.model.OrderHeader;
import com.winapp.saperp.model.OrderHeader;
import com.winapp.saperp.model.OrderHistoryCatalogProductModel;
import com.winapp.saperp.model.SalesOrderPrintPreviewModel;
import com.winapp.saperp.utils.Constants;
import com.winapp.saperp.utils.SessionManager;
import com.winapp.saperp.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderHistoryCatalogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private String company_code;

    public static ArrayList<OrderHeader> orderList;
    public static ArrayList<OrderHeader> salesOrderFilterList;
    public  Context mContext;
    public DBHelper dbHelper;
    CallBack callBack;

    private String companyId;
    private SessionManager session;
    private HashMap<String, String> user;
    private ArrayList<SalesOrderPrintPreviewModel.SalesList> salesOrdernewList;
    private String locationCode;

    public OrderHistoryCatalogAdapter(Context context, RecyclerView mRecyclerView,
                                      ArrayList<OrderHeader> orderList, CallBack callBack) {

        this.orderList = orderList;
        this.salesOrderFilterList = orderList;
        this.mContext = context;
        this.callBack = callBack;
        dbHelper = new DBHelper(mContext);
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
        return orderList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.order_history_list_items, parent, false);
            return new SalesOrderViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        if (viewHolder instanceof SalesOrderViewHolder) {
            session = new SessionManager(mContext);
            user = session.getUserDetails();
            companyId = user.get(SessionManager.KEY_COMPANY_CODE);
            locationCode = user.get(SessionManager.KEY_LOCATION_CODE);
            company_code = user.get(SessionManager.KEY_COMPANY_CODE);

            OrderHeader salesOrderModel = orderList.get(position);
            ((SalesOrderViewHolder) viewHolder).name.setText(salesOrderModel.getCustomerName());
            ((SalesOrderViewHolder) viewHolder).date.setText(salesOrderModel.getInvoiceDate());
//            if (salesOrderModel.getAddress().equals("null") || salesOrderModel.getAddress().isEmpty()) {
//                ((SalesOrderViewHolder) viewHolder).address.setText("Address not found");
//            } else {
//                ((SalesOrderViewHolder) viewHolder).address.setText(salesOrderModel.getAddress());
//            }
            ((SalesOrderViewHolder) viewHolder).soNumber.setText(salesOrderModel.getOrderId());
//            ((SalesOrderViewHolder) viewHolder).balance.setText("$ "+salesOrderModel.getBalance());
            if (salesOrderModel.getNetTotal() != null && !salesOrderModel.getNetTotal().equals("null")) {
                ((SalesOrderViewHolder) viewHolder).netTotal.setText("$ " + Utils.twoDecimalPoint(Double.parseDouble(salesOrderModel.getNetTotal())));
            } else {
                ((SalesOrderViewHolder) viewHolder).netTotal.setText("$ " + "0.00");
            }
            ArrayList<CartModel> model =  dbHelper.getAllCartItem_Temp2(salesOrderModel.getOrderId());

            ((SalesOrderViewHolder) viewHolder).moreOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // callBack.showMoreOption(salesOrderModel.getSalesOrderCode(),
                    //      salesOrderModel.getName(),((SalesOrderViewHolder) viewHolder).status.getText().toString());
                }
            });

            ((SalesOrderViewHolder) viewHolder).syncnow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                     callBack.syncNowCall(salesOrderModel , model);
                }
            });

            ((SalesOrderViewHolder) viewHolder).itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
//                    callBack.showMoreOption(salesOrderModel.getSalesOrderCode(),
//                            salesOrderModel.getName(),((SalesOrderViewHolder) viewHolder).status.getText().toString());
                    return false;
                }
            });


            ((SalesOrderViewHolder) viewHolder).showHideBottomLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (((SalesOrderViewHolder) viewHolder).showHideBottomLayout.getTag().equals("hide")) {
                        ((SalesOrderViewHolder) viewHolder).bottomLayout.setVisibility(View.VISIBLE);
                        ((SalesOrderViewHolder) viewHolder).showHideBottomLayout.setTag("show");
                        ((SalesOrderViewHolder) viewHolder).showHideBottomLayout.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_keyboard_arrow_up_24));
                        //    try {
                        if (model != null && model.size() > 0) {

                            setOrderHistoryPdtAdapter(viewHolder, position, model);
                            ((SalesOrderViewHolder) viewHolder).progressLayout.setVisibility(View.GONE);
                            ((SalesOrderViewHolder) viewHolder).mainLayout.setVisibility(View.VISIBLE);
                        } else {
                            //    getSalesOrderDetails(salesOrderModel.getSalesOrderCode(),viewHolder,position,salesOrderModel);
                            salesOrderModel.setShow(true);
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
                        }
                    } else {
                        salesOrderModel.setShow(false);
                        ((SalesOrderViewHolder) viewHolder).bottomLayout.setVisibility(View.GONE);
                        ((SalesOrderViewHolder) viewHolder).showHideBottomLayout.setTag("hide");
                        ((SalesOrderViewHolder) viewHolder).showHideBottomLayout.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_keyboard_arrow_down_24));
                    }
                }
            });

            if (salesOrderModel.isShow()) {
                ((SalesOrderViewHolder) viewHolder).bottomLayout.setVisibility(View.VISIBLE);
                ((SalesOrderViewHolder) viewHolder).showHideBottomLayout.setTag("show");
                ((SalesOrderViewHolder) viewHolder).showHideBottomLayout.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_keyboard_arrow_up_24));
                try {
                    setOrderHistoryPdtAdapter(viewHolder, position, model);
                    ((SalesOrderViewHolder) viewHolder).progressLayout.setVisibility(View.GONE);
                    ((SalesOrderViewHolder) viewHolder).mainLayout.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ((SalesOrderViewHolder) viewHolder).bottomLayout.setVisibility(View.GONE);
                ((SalesOrderViewHolder) viewHolder).showHideBottomLayout.setTag("hide");
                ((SalesOrderViewHolder) viewHolder).showHideBottomLayout.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_keyboard_arrow_down_24));
            }

        } else if (viewHolder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) viewHolder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return orderList == null ? 0 : orderList.size();
    }

    public void setLoaded() {
        isLoading = false;
        callBack.calculateNetTotal(orderList);
    }

    static class SalesOrderViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView date;
        private TextView soNumber;
        private TextView balance;
        private TextView netTotal;
        private TextView address;
        private CardView mainCard;
        private TextView syncnow;
        private ImageView moreOption;
        private LinearLayout statusLayout;
        private View indicator;

        private RecyclerView productListView;
        private ImageView showHideBottomLayout;
        private LinearLayout mainLayout;
        private LinearLayout progressLayout;
        private LinearLayout bottomLayout;


        public SalesOrderViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            date = view.findViewById(R.id.date);
            soNumber = view.findViewById(R.id.so_no);
            balance = view.findViewById(R.id.balance);
            address = view.findViewById(R.id.address);
            netTotal = view.findViewById(R.id.net_total);
            mainCard = view.findViewById(R.id.cardlist_item);
            syncnow = view.findViewById(R.id.syncNow);
            moreOption = view.findViewById(R.id.more);
            statusLayout = view.findViewById(R.id.status_layout);
            indicator = view.findViewById(R.id.indicator);

            productListView = view.findViewById(R.id.invoiceList);
            showHideBottomLayout = view.findViewById(R.id.show_hide);
            mainLayout = view.findViewById(R.id.main_layout);
            progressLayout = view.findViewById(R.id.progress_layout);
            bottomLayout = view.findViewById(R.id.bottom_layout);

        }
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
        Log.w("Given_date_printed:", date);
        return date;
    }

    public interface CallBack {
        void calculateNetTotal(ArrayList<OrderHeader> salesList);

        void showMoreOption(String salesorderId, String customerName, String status);


        void syncNowCall(OrderHeader orderHeader, ArrayList<CartModel> cartModel);
    }

    public void filterList(ArrayList<OrderHeader> filterdNames) {
        orderList = filterdNames;
        notifyDataSetChanged();
    }

    public static ArrayList<OrderHeader> getNotalInvoiceList() {
        return orderList;
    }

    public static ArrayList<OrderHeader> getSalesOrderList() {
        return salesOrderFilterList;
    }


//    private void getSalesOrderDetails(String soNumber,RecyclerView.ViewHolder  viewHolder, int position,OrderHistoryCatalogCustModel salesOrderModel) throws JSONException {
//        // Initialize a new RequestQueue instance
//        JSONObject jsonObject=new JSONObject();
//        jsonObject.put("SalesOrderNo",soNumber);
//        // jsonObject.put("SoNo", soNumber);
//        //jsonObject.put("LocationCode",locationCode);
//        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
//        String url= Utils.getBaseUrl(mContext) +"SalesOrderDetails";
//        // Initialize a new JsonArrayRequest instance
//        Log.w("Given_url:",url+jsonObject);
//        salesOrdernewList =new ArrayList<>();
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
//                Request.Method.POST,
//                url,
//                jsonObject,
//                response -> {
//                    try{
//                        Log.w("Sales_Details:",response.toString());
//                        String statusCode=response.optString("statusCode");
//                        if (statusCode.equals("1")) {
//                            JSONArray responseData = response.getJSONArray("responseData");
//                            JSONObject object = responseData.optJSONObject(0);
//
//                            SalesOrderPrintPreviewModel model = new SalesOrderPrintPreviewModel();
//                            model.setSoNumber(object.optString("soNumber"));
//                            model.setSoDate(object.optString("soDate"));
//                            model.setCustomerCode(object.optString("customerCode"));
//                            model.setCustomerName(object.optString("customerName"));
//                            model.setAddress(object.optString("address1") + object.optString("address2") + object.optString("address3"));
//                            model.setDeliveryAddress(model.getAddress());
//                            model.setSubTotal(object.optString("subTotal"));
//                            model.setNetTax(object.optString("taxTotal"));
//                            model.setNetTotal(object.optString("netTotal"));
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
//
//                            String signFlag = object.optString("signFlag");
//                            if (signFlag.equals("Y")) {
//                                String signature = object.optString("signature");
//                                Utils.setSignature(signature);
//                            } else {
//                                Utils.setSignature("");
//                            }
//
//
//                            //    deliveryAddressText.setText(model.getAddress());
//
//                            JSONArray detailsArray = object.optJSONArray("salesOrderDetails");
//
//
//                            for (int i=0;i<detailsArray.length();i++){
//                                JSONObject detailObject=detailsArray.optJSONObject(i);
//                                if (Double.parseDouble(detailObject.optString("quantity"))>0){
//                                    SalesOrderPrintPreviewModel.SalesList salesListModel =new SalesOrderPrintPreviewModel.SalesList();
//                                    salesListModel.setProductCode(detailObject.optString("productCode"));
//                                    salesListModel.setDescription( detailObject.optString("productName"));
//                                    salesListModel.setLqty(detailObject.optString("unitQty"));
//                                    salesListModel.setCqty(detailObject.optString("cartonQty"));
//                                    salesListModel.setNetQty(detailObject.optString("quantity"));
//                                    salesListModel.setCartonPrice(detailObject.optString("cartonPrice"));
//                                    salesListModel.setUnitPrice(detailObject.optString("price"));
//                                    salesListModel.setGrossPrice(detailObject.optString("grossPrice"));
//
//                                    double qty=Double.parseDouble(detailObject.optString("quantity"));
//                                    double price=Double.parseDouble(detailObject.optString("price"));
//
//                                    double nettotal=qty * price;
//                                    salesListModel.setTotal(String.valueOf(nettotal));
//                                    salesListModel.setPricevalue(String.valueOf(price));
//
//                                    salesListModel.setPcsperCarton(detailObject.optString("pcsPerCarton"));
//                                    salesListModel.setItemtax(detailObject.optString("totalTax"));
//                                    salesListModel.setSubTotal(detailObject.optString("subTotal"));
//                                    salesOrdernewList.add(salesListModel);
//
//                                   /* if (Double.parseDouble(detailObject.optString("quantity")) > 0) {
//                                        salesListModel = new SalesOrderPrintPreviewModel.SalesList();
//                                        salesListModel.setProductCode(detailObject.optString("productCode"));
//                                        salesListModel.setDescription(detailObject.optString("productName"));
//                                        salesListModel.setLqty(detailObject.optString("unitQty"));
//                                        salesListModel.setCqty(detailObject.optString("cartonQty"));
//                                        salesListModel.setNetQty(detailObject.optString("cartonQty"));
//
//                                        double qty1 = Double.parseDouble(detailObject.optString("cartonQty"));
//                                        double price1 = Double.parseDouble(detailObject.optString("cartonPrice"));
//                                        double nettotal1 = qty1 * price1;
//                                        salesListModel.setTotal(String.valueOf(nettotal1));
//                                        salesListModel.setPricevalue(String.valueOf(price1));
//
//                                        salesListModel.setUomCode(detailObject.optString("uomCode"));
//                                        salesListModel.setCartonPrice(detailObject.optString("cartonPrice"));
//                                        salesListModel.setUnitPrice(detailObject.optString("price"));
//                                        salesListModel.setPcsperCarton(detailObject.optString("pcsPerCarton"));
//                                        salesListModel.setItemtax(detailObject.optString("totalTax"));
//                                        salesListModel.setSubTotal(detailObject.optString("subTotal"));
//                                        salesOrderList.add(salesListModel);
//                                    }*/
//
//                                    if (!detailObject.optString("ReturnQty").isEmpty() && Double.parseDouble(detailObject.optString("ReturnQty")) > 0) {
//                                        salesListModel = new SalesOrderPrintPreviewModel.SalesList();
//                                        salesListModel.setProductCode(detailObject.optString("ProductCode"));
//                                        salesListModel.setDescription(detailObject.optString("ProductName"));
//                                        salesListModel.setLqty(detailObject.optString("LQty"));
//                                        salesListModel.setCqty(detailObject.optString("CQty"));
//                                        salesListModel.setNetQty(detailObject.optString("ReturnQty"));
//                                        salesListModel.setGrossPrice(detailObject.optString("grossPrice"));
//
//                                        double qty1 = Double.parseDouble(detailObject.optString("ReturnQty"));
//                                        double price1 = Double.parseDouble(detailObject.optString("Price"));
//                                        double nettotal1 = qty1 * price1;
//                                        salesListModel.setTotal(String.valueOf(nettotal1));
//                                        salesListModel.setPricevalue(String.valueOf(price1));
//
//                                        salesListModel.setUomCode(detailObject.optString("uomCode"));
//                                        salesListModel.setCartonPrice(detailObject.optString("CartonPrice"));
//                                        salesListModel.setUnitPrice(detailObject.optString("Price"));
//                                        salesListModel.setPcsperCarton(detailObject.optString("PcsPerCarton"));
//                                        salesListModel.setItemtax(detailObject.optString("Tax"));
//                                        salesListModel.setSubTotal(detailObject.optString("subTotal"));
//                                        salesOrdernewList.add(salesListModel);
//                                    }
//
//                                }
//
//                            /*    else {
//                                    if (Double.parseDouble(detailObject.optString("quantity")) > 0) {
//                                        SalesOrderPrintPreviewModel.SalesList salesListModel = new SalesOrderPrintPreviewModel.SalesList();
//                                        salesListModel.setProductCode(detailObject.optString("productCode"));
//                                        salesListModel.setDescription(detailObject.optString("productName"));
//                                        salesListModel.setLqty(detailObject.optString("unitQty"));
//                                        salesListModel.setCqty(detailObject.optString("cartonQty"));
//                                        salesListModel.setNetQty(detailObject.optString("quantity"));
//                                        salesListModel.setCartonPrice(detailObject.optString("cartonPrice"));
//                                        salesListModel.setUnitPrice(detailObject.optString("price"));
//
//                                        double qty1 = Double.parseDouble(detailObject.optString("cartonQty"));
//                                        double price1 = Double.parseDouble(detailObject.optString("cartonPrice"));
//                                        double nettotal1 = qty1 * price1;
//                                        salesListModel.setTotal(String.valueOf(nettotal1));
//                                        salesListModel.setPricevalue(String.valueOf(price1));
//
//                                        salesListModel.setUomCode(detailObject.optString("uomCode"));
//                                        salesListModel.setPcsperCarton(detailObject.optString("pcsPerCarton"));
//                                        salesListModel.setItemtax(detailObject.optString("totalTax"));
//                                        salesListModel.setSubTotal(detailObject.optString("subTotal"));
//                                        salesOrderList.add(salesListModel);
//
//
//                                        if (!detailObject.optString("ReturnQty").isEmpty() && Double.parseDouble(detailObject.optString("ReturnQty")) > 0) {
//                                            salesListModel = new SalesOrderPrintPreviewModel.SalesList();
//                                            salesListModel.setProductCode(detailObject.optString("ProductCode"));
//                                            salesListModel.setDescription(detailObject.optString("ProductName"));
//                                            salesListModel.setLqty(detailObject.optString("LQty"));
//                                            salesListModel.setCqty(detailObject.optString("CQty"));
//                                            salesListModel.setNetQty("-"+detailObject.optString("ReturnQty"));
//
//                                            double qty12 = Double.parseDouble(detailObject.optString("ReturnQty"));
//                                            double price12 = Double.parseDouble(detailObject.optString("Price"));
//                                            double nettotal12 = qty12 * price12;
//                                            salesListModel.setTotal(String.valueOf(nettotal12));
//                                            salesListModel.setPricevalue(String.valueOf(price12));
//
//                                            salesListModel.setUomCode(detailObject.optString("UOMCode"));
//                                            salesListModel.setCartonPrice(detailObject.optString("CartonPrice"));
//                                            salesListModel.setUnitPrice(detailObject.optString("Price"));
//                                            salesListModel.setPcsperCarton(detailObject.optString("PcsPerCarton"));
//                                            salesListModel.setItemtax(detailObject.optString("Tax"));
//                                            salesListModel.setSubTotal(detailObject.optString("subTotal"));
//                                            salesOrderList.add(salesListModel);
//                                        }
//
//                                    }
//                                }*/
//
//                                model.setSalesList(salesOrdernewList);
//                          //      salesOrderHeaderDetails.add(model);
//                            }
//                        }
//
//                        if (salesOrdernewList.size()>0){
//                            ((SalesOrderViewHolder) viewHolder).progressLayout.setVisibility(View.GONE);
//                            ((SalesOrderViewHolder) viewHolder).mainLayout.setVisibility(View.VISIBLE);
//                            salesOrderModel.setSalesList(salesOrdernewList);
//                            setSalesAdapter(viewHolder,position,salesOrdernewList);
//                        }
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }, error -> {
//            // Do something when error occurred
//            Log.w("Error_throwing:",error.toString());
//        }){
//            @Override
//            public Map<String, String> getHeaders() {
//                HashMap<String, String> params = new HashMap<>();
//                String creds = String.format("%s:%s", Constants.API_SECRET_CODE, Constants.API_SECRET_PASSWORD);
//                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
//                params.put("Authorization", auth);
//                return params;
//            }
//        };
//        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
//            @Override
//            public int getCurrentTimeout() {
//                return 50000;
//            }
//            @Override
//            public int getCurrentRetryCount() {
//                return 50000;
//            }
//            @Override
//            public void retry(VolleyError error) throws VolleyError {
//
//            }
//        });
//        // Add JsonArrayRequest to the RequestQueue
//        requestQueue.add(jsonObjectRequest);
//    }

    public void setOrderHistoryPdtAdapter(@NonNull RecyclerView.ViewHolder viewHolder, int position,
                                          ArrayList<CartModel> salesList) {
        ((SalesOrderViewHolder) viewHolder).productListView.setHasFixedSize(true);
        ((SalesOrderViewHolder) viewHolder).productListView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        OrderHistoryProductAdapter adapter = new OrderHistoryProductAdapter(mContext, salesList);
        ((SalesOrderViewHolder) viewHolder).productListView.setAdapter(adapter);
        // notifyDataSetChanged();
    }

    /*private Date getDate(long time) {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();//get your local time zone.
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        sdf.setTimeZone(tz);//set time zone.
        String localTime = sdf.format(new Date(time) * 1000));
        Date date = new Date();
        try {
            date = sdf.parse(localTime);//get local date
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }*/
}