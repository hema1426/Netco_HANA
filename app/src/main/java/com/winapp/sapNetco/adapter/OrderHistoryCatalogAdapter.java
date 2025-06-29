package com.winapp.sapNetco.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateFormat;
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

import com.winapp.sapNetco.R;
import com.winapp.sapNetco.db.DBHelper;
import com.winapp.sapNetco.model.CartModel;
import com.winapp.sapNetco.model.OrderHeader;
import com.winapp.sapNetco.model.SalesOrderPrintPreviewModel;
import com.winapp.sapNetco.utils.SessionManager;
import com.winapp.sapNetco.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

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
            View view = LayoutInflater.from(mContext).inflate(R.layout.order_history_catalog_new_card_item, parent, false);
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
            ((SalesOrderViewHolder) viewHolder).orderId_txt.setText(salesOrderModel.getOrderId());
            Log.w("orderhis_nettotal",""+salesOrderModel.getNetTotal());
//            ((SalesOrderViewHolder) viewHolder).balance.setText("$ "+salesOrderModel.getBalance());
            if (salesOrderModel.getNetTotal() != null && !salesOrderModel.getNetTotal().equals("null")) {
                ((SalesOrderViewHolder) viewHolder).netTotal.setText("$ " + Utils.twoDecimalPoint(Double.parseDouble(salesOrderModel.getNetTotal())));
            } else {
                ((SalesOrderViewHolder) viewHolder).netTotal.setText("$ " + "0.00");
            }
            ArrayList<CartModel> model =  dbHelper.getAllCartItem_History_Temp2(salesOrderModel.getOrderId());

//            ((SalesOrderViewHolder) viewHolder).moreOption.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    // callBack.showMoreOption(salesOrderModel.getSalesOrderCode(),
//                    //      salesOrderModel.getName(),((SalesOrderViewHolder) viewHolder).status.getText().toString());
//                }
//            });

            ((SalesOrderViewHolder) viewHolder).syncnowLay.setOnClickListener(new View.OnClickListener() {
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


            ((SalesOrderViewHolder) viewHolder).downlistLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (((SalesOrderViewHolder) viewHolder).downlist_img.getTag().equals("hide")) {
                        Log.w("orderhisdown","");
                        ((SalesOrderViewHolder) viewHolder).bottomLayout.setVisibility(View.VISIBLE);
                        ((SalesOrderViewHolder) viewHolder).downlist_img.setTag("show");
                        ((SalesOrderViewHolder) viewHolder).downlist_img.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_keyboard_arrow_up_24));
                     try {
                        if (model != null && model.size() > 0) {

                            setOrderHistoryPdtAdapter(viewHolder, position, model);
                            ((SalesOrderViewHolder) viewHolder).progressLayout.setVisibility(View.GONE);
                            ((SalesOrderViewHolder) viewHolder).mainLayout.setVisibility(View.VISIBLE);
                        } else {
                            //    getSalesOrderDetails(salesOrderModel.getSalesOrderCode(),viewHolder,position,salesOrderModel);
                            salesOrderModel.setShow(true);
                          }
                     } catch (Exception e) {
                         e.printStackTrace();
                     }

                    } else {
                        Log.w("orderhisdown1","");
                        salesOrderModel.setShow(false);
                        ((SalesOrderViewHolder) viewHolder).bottomLayout.setVisibility(View.GONE);
                        ((SalesOrderViewHolder) viewHolder).downlist_img.setTag("hide");
                        ((SalesOrderViewHolder) viewHolder).downlist_img.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_keyboard_arrow_down_24));
                    }
                }
            });

            if (salesOrderModel.isShow()) {
                ((SalesOrderViewHolder) viewHolder).bottomLayout.setVisibility(View.VISIBLE);
                ((SalesOrderViewHolder) viewHolder).downlist_img.setTag("show");
                ((SalesOrderViewHolder) viewHolder).downlist_img.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_keyboard_arrow_up_24));
                try {
                    setOrderHistoryPdtAdapter(viewHolder, position, model);
                    ((SalesOrderViewHolder) viewHolder).progressLayout.setVisibility(View.GONE);
                    ((SalesOrderViewHolder) viewHolder).mainLayout.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ((SalesOrderViewHolder) viewHolder).bottomLayout.setVisibility(View.GONE);
                ((SalesOrderViewHolder) viewHolder).downlist_img.setTag("hide");
                ((SalesOrderViewHolder) viewHolder).downlist_img.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_keyboard_arrow_down_24));
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
        private TextView netTotal;
        private CardView mainCard;
        private TextView syncnow,orderId_txt;

        private RecyclerView productListView;
        private LinearLayout mainLayout;
        private ImageView downlist_img;
        private LinearLayout progressLayout;
        private LinearLayout downlistLay,syncnowLay,bottomLayout;


        public SalesOrderViewHolder(View view) {
            super(view);

            name = view.findViewById(R.id.oc_customerName_item);
            date = view.findViewById(R.id.oc_OrderDate_item);
            netTotal = view.findViewById(R.id.oc_nettotal_item);
            mainCard = view.findViewById(R.id.oc_cardlist_item);
            syncnowLay = view.findViewById(R.id.syncNow_layout);
            downlistLay = view.findViewById(R.id.oc_downlist_lay);
            bottomLayout = view.findViewById(R.id.oc_bottomLayout);
            downlist_img = view.findViewById(R.id.oc_downlist);
            orderId_txt = view.findViewById(R.id.oc_OrderId_item);
            mainLayout=view.findViewById(R.id.oc_main_layout);
//            indicator = view.findViewById(R.id.indicator);

            productListView = view.findViewById(R.id.oc_invoiceList_item);
            mainLayout = view.findViewById(R.id.oc_main_layout);
            progressLayout = view.findViewById(R.id.progress_layout);
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

    public void setOrderHistoryPdtAdapter(@NonNull RecyclerView.ViewHolder viewHolder, int position,
                                          ArrayList<CartModel> salesList) {
        ((SalesOrderViewHolder) viewHolder).productListView.setHasFixedSize(true);
        ((SalesOrderViewHolder) viewHolder).productListView.setLayoutManager(new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false));
        OrderHistoryCatalogProductAdapter adapter = new OrderHistoryCatalogProductAdapter(mContext, salesList);
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