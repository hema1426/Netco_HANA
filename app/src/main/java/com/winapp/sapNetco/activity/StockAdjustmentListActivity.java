package com.winapp.sapNetco.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.winapp.sapNetco.R;
import com.winapp.sapNetco.adapter.StockAdjustmentAdapter;
import com.winapp.sapNetco.db.DBHelper;
import com.winapp.sapNetco.fragments.CustomerFragment;
import com.winapp.sapNetco.model.CustomerDetails;
import com.winapp.sapNetco.model.SalesOrderModel;
import com.winapp.sapNetco.model.SalesOrderPrintPreviewModel;
import com.winapp.sapNetco.model.StockAdjustmentModuleModel;
import com.winapp.sapNetco.printpreview.StockAdjustPrintPreview;
import com.winapp.sapNetco.utils.Constants;
import com.winapp.sapNetco.utils.SessionManager;
import com.winapp.sapNetco.utils.Utils;
import com.winapp.sapNetco.zebraprinter.TSCPrinter;
import com.winapp.sapNetco.zebraprinter.ZebraPrinterActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class StockAdjustmentListActivity extends NavigationActivity implements StockAdjustmentAdapter.CallBack {

    public static RecyclerView stockAdjustListView;
    public static StockAdjustmentAdapter stockAdjustAdapter;
    private ArrayList<StockAdjustmentModuleModel> stockAdjustmentList;
    private SweetAlertDialog pDialog;
    private SessionManager session;
    private HashMap<String,String > user;
    private String companyId;
    int pageNo=1;
    private BottomSheetBehavior behavior;
    Button btnCancel;
    TextView customerName;
    LinearLayout outstandingLayout;
    DBHelper dbHelper;
    TextView netTotalText;
    private SharedPreferences sharedPref_billdisc;
    private SharedPreferences.Editor myEdit;
    private ArrayList<CustomerDetails> customerDetails;
    LinearLayout transLayout;
    View adjustOptionLayout;
    TextView soCustomerName;
    TextView number;
    TextView optionCancel;
    TextView cancelSheet;
    String userName;
    FloatingActionButton editSalesOrder;
    FloatingActionButton deleteSaleOrder;
    FloatingActionButton printPreview;
    String locationCode;
    String deliveryOrderStatus;
    LinearLayout editLayout;
    LinearLayout deleteLayout;
    LinearLayout printPreviewLayout;
    boolean isSearchCustomerNameClicked;
    boolean addnewCustomer;
    private int request_cust_code=81;

    View searchFilterView;
    private int mYear, mMonth, mDay, mHour, mMinute;
    EditText fromDate;
    EditText toDate;
    String oldToDatel = "";
    Date fromDatel ;
    Button searchButton;
    Button cancelSearch;
    Spinner statusSpinner;
    public static LinearLayout emptyLayout;
    public String printSoNumber;
    public String noOfCopy;

    private ArrayList<SalesOrderPrintPreviewModel> salesOrderHeaderDetails;
    private ArrayList<SalesOrderPrintPreviewModel.SalesList> salesPrintList;
    private String printerMacId;
    private String printerType;
    private SharedPreferences sharedPreferences;
    View progressLayout;
    boolean redirectInvoice;
    public static String selectedCustomerId = "";
    String isFound="true";
    private int customerSelectCode=24;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_stock_adjustment_list, contentFrameLayout);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Stock Adjustment");


        dbHelper=new DBHelper(this);
        session=new SessionManager(this);
        user=session.getUserDetails();
        companyId=user.get(SessionManager.KEY_COMPANY_CODE);
        userName=user.get(SessionManager.KEY_USER_NAME);
        locationCode=user.get((SessionManager.KEY_LOCATION_CODE));

        sharedPref_billdisc = getSharedPreferences("BillDiscPref", MODE_PRIVATE);
        myEdit = sharedPref_billdisc.edit();

        stockAdjustListView =findViewById(R.id.stockAdjustList);
        netTotalText=findViewById(R.id.net_totalAdjust);
        transLayout=findViewById(R.id.trans_layoutAdjust);
        adjustOptionLayout =findViewById(R.id.sales_option);
        soCustomerName=findViewById(R.id.name);
        number =findViewById(R.id.so_no);
        optionCancel=findViewById(R.id.option_cancel);
        cancelSheet=findViewById(R.id.cancel_sheet);
        editSalesOrder=findViewById(R.id.edit_salesorder);
        deleteSaleOrder=findViewById(R.id.delete_salesorder);
        editLayout=findViewById(R.id.edit_layout);
        deleteLayout=findViewById(R.id.delete_layout);
        printPreview=findViewById(R.id.print_preview);
        printPreviewLayout=findViewById(R.id.print_preview_layout);
        searchFilterView=findViewById(R.id.search_filteradjust);
        fromDate=findViewById(R.id.from_dateadj);
        toDate =findViewById(R.id.to_dateadj);
        statusSpinner =findViewById(R.id.statusadjust);
        emptyLayout=findViewById(R.id.empty_layoutadj);
        cancelSearch=findViewById(R.id.btn_canceladjust);
        searchButton=findViewById(R.id.btn_searchadjust);
        outstandingLayout=findViewById(R.id.outstanding_layout);
        progressLayout=findViewById(R.id.progress_layout);

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        fromDate.setText(formattedDate);
        toDate.setText(formattedDate);
        oldToDatel=toDate.getText().toString();

        try {
            fromDatel = new SimpleDateFormat("dd/MM/yyyy").parse(oldToDatel);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        String toDateString = new SimpleDateFormat("yyyyMMdd").format(fromDatel);

        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE);
        printerType=sharedPreferences.getString("printer_type","");
        printerMacId=sharedPreferences.getString("mac_address","");

        Log.w("Printer_Mac_Id:",printerMacId);
        Log.w("Printer_Type:",printerType);

        //dbHelper.removeAllProducts();

       /* if (getIntent() !=null){
            printSoNumber=getIntent().getStringExtra("printSoNumber");
            noOfCopy=getIntent().getStringExtra("noOfCopy");
            if (printSoNumber!=null && !printSoNumber.isEmpty()){
                try {
                    getSalesOrderDetails(printSoNumber,Integer.parseInt(noOfCopy));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }*/

   /*     customerList=dbHelper.getAllCustomers();
        if (customerList!=null && customerList.size()>0){
            setAdapter(customerList);
        }else {
            getCustomers();
            // new GetCustomersTask().execute();
        }*/


        getStockAdjustList("1","",toDateString,toDateString);
        //setDOAdapter();

      /*  deliveryOrderAdapter.setOnLoadMoreListener(new DeliveryOrderAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e("haint", "Load More");
                deliveryOrderList.add(null);
                deliveryOrderAdapter.notifyItemInserted(deliveryOrderList.size() - 1);
                //Load more data for reyclerview
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("haint", "Load More 2");

                        //Remove loading item
                        deliveryOrderList.remove(deliveryOrderList.size() - 1);
                        deliveryOrderAdapter.notifyItemRemoved(deliveryOrderList.size());
                        //Load data
                        int index = deliveryOrderList.size();
                        int end = index + 20;
                        pageNo=pageNo+1;
                        //getSalesOrderList(companyId, String.valueOf(pageNo));
                    }
                }, 5000);
            }
        });*/


        View bottomSheet = findViewById(R.id.design_bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_DRAGGING");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_SETTLING");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_EXPANDED");
                        if (adjustOptionLayout.getVisibility()==View.VISIBLE){
                            getSupportActionBar().setTitle("Select Option");
//                        }else {
//                            getSupportActionBar().setTitle("Select Customer");
                        }
                        transLayout.setVisibility(View.VISIBLE);
                        transLayout.setClickable(false);
                        transLayout.setEnabled(false);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_COLLAPSED");
                        getSupportActionBar().setTitle("Stock Adjustment");
                        transLayout.setVisibility(View.GONE);
                        if (redirectInvoice){
                            CustomerFragment.isLoad=true;
                            Intent intent=new Intent(StockAdjustmentListActivity.this, AddInvoiceActivityOld.class);
                            intent.putExtra("customerId",selectedCustomerId);
                            intent.putExtra("activityFrom","DeliveryOrder");
                            startActivity(intent);
                            finish();
                        }
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_HIDDEN");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.i("BottomSheetCallback", "slideOffset: " + slideOffset);
            }
        });

        optionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
            }
        });

//        cancelSheet.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                viewCloseBottomSheet();
//            }
//        });
        printPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
                Intent intent=new Intent(StockAdjustmentListActivity.this, StockAdjustPrintPreview.class);
                intent.putExtra("adjustNumber", number.getText().toString());
                startActivity(intent);
            }
        });

        printPreviewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
                Intent intent=new Intent(StockAdjustmentListActivity.this, StockAdjustPrintPreview.class);
                intent.putExtra("adjustNumber", number.getText().toString());
                startActivity(intent);
            }
        });


        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDate(fromDate);
            }
        });

        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDate(toDate);
            }
        });


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/yyyy");
                Date d1 = null;
                Date d2=null;
                try {
                    d1 = sdformat.parse(fromDate.getText().toString());
                    d2 = sdformat.parse(toDate.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(d1.compareTo(d2) > 0) {
                    Toast.makeText(getApplicationContext(),"From date should not be greater than to date",Toast.LENGTH_SHORT).show();
                } else{
                    searchFilterView.setVisibility(View.GONE);
                    isSearchCustomerNameClicked=true;
                    try {
                        String oldFromDate = fromDate.getText().toString();
                        String oldToDate=toDate.getText().toString();
                        Date fromDate = new SimpleDateFormat("dd/MM/yyyy").parse(oldFromDate);
                        Date toDate = new SimpleDateFormat("dd/MM/yyyy").parse(oldToDate);
                        // Use SimpleDateFormat#format() to format a Date into a String in a certain pattern.

                        String fromDateString = new SimpleDateFormat("yyyyMMdd").format(fromDate);
                        String toDateString = new SimpleDateFormat("yyyyMMdd").format(toDate);
                        System.out.println(fromDateString+"-"+toDateString); // 2011-01-18
                        String invoice_status="";
                        if (statusSpinner.getSelectedItem().equals("ALL")){
                            invoice_status="";
                        }else if (statusSpinner.getSelectedItem().equals("CLOSED")){
                            invoice_status="C";
                        }else if (statusSpinner.getSelectedItem().equals("OPEN")){
                            invoice_status="O";
                        }
                        getStockAdjustList("",invoice_status,fromDateString,toDateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    ///filterSearch(customer_name, salesOrderStatusSpinner.getSelectedItem().toString(),fromDate.getText().toString(),toDate.getText().toString());
                    statusSpinner.setSelection(0);
                }
            }
        });

        cancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSearchCustomerNameClicked=false;
                statusSpinner.setSelection(0);
                fromDate.setText(formattedDate);
                toDate.setText(formattedDate);
                selectedCustomerId ="";
                searchFilterView.setVisibility(View.GONE);
                statusSpinner.setSelection(0);
                setFilterAdapeter();
            }
        });
    }
    private void sentPrintDate(int copy) throws IOException {
        if (printerType.equals("TSC Printer")){
            //        dialog.dismiss();
//            printInvoice();
           // Toast.makeText(getApplicationContext(),"TSC printer in progress",Toast.LENGTH_SHORT).show();
            // TSCPrinter tscPrinter=new TSCPrinter(SalesOrderPrintPreview.this,printerMacId);
            //  tscPrinter.printInvoice(invoiceHeaderDetails,invoiceList);
            TSCPrinter printer=new TSCPrinter(StockAdjustmentListActivity.this,printerMacId,"DO");
          //  printer.printDeliveryOrder1(copy,salesOrderHeaderDetails,salesPrintList);
            printer.setOnCompletionListener(new TSCPrinter.OnCompletionListener() {
                @Override
                public void onCompleted() {
                    Utils.setSignature("");
                    Toast.makeText(getApplicationContext(),"Delivery Order printed successfully!",Toast.LENGTH_SHORT).show();
                }
            });
        }else if (printerType.equals("Zebra Printer")){
            ZebraPrinterActivity zebraPrinterActivity=new ZebraPrinterActivity(StockAdjustmentListActivity.this,printerMacId);
            zebraPrinterActivity.printSalesOrder(copy,salesOrderHeaderDetails,salesPrintList);
        }
    }
    public void setNettotal(ArrayList<SalesOrderModel> salesOrderList){
        try {
            double net_amount=0.0;
            for (SalesOrderModel model:salesOrderList){
                if (model.getNetTotal()!=null && !model.getNetTotal().equals("null")){
                    net_amount=net_amount+Double.parseDouble(model.getNetTotal());
                }
            }
            netTotalText.setText("$ "+Utils.twoDecimalPoint(net_amount));
        }catch (Exception ex){}
    }

    public void showRemoveAlert(String salesOrderId){
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                // .setTitleText("Are you sure?")
                .setContentText("Are you sure want Delete SalesOrder ?")
                .setConfirmText("YES")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        try {
                            sDialog.dismiss();
                            // viewCloseBottomSheet();
                            setDeleteSalesOrder(salesOrderId);
                            if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).showCancelButton(true)
                .setCancelText("No")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }}).show();
    }


    public void setAdapter() {

        stockAdjustListView.setHasFixedSize(true);
        stockAdjustListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        stockAdjustAdapter = new StockAdjustmentAdapter(this, stockAdjustListView, stockAdjustmentList, this);
        stockAdjustListView.setAdapter(stockAdjustAdapter);
    }

    public void redirectActivity(String action,String customer_code,String customer_name,String do_code,String customerBill_Disc){
        //  if (products.length()==dbHelper.numberOfRowsInInvoice()){
        Log.w("acttionDO",""+action);
        Utils.setCustomerSession(StockAdjustmentListActivity.this,customer_code);
        if (action.equals("Edit")){
            Intent intent=new Intent(getApplicationContext(),CreateNewInvoiceActivity.class);
            intent.putExtra("customerName",customer_name);
            intent.putExtra("customerCode",customer_code);
            intent.putExtra("editDoNumber", do_code);
           // intent.putExtra("customerBillDisc", customerBill_Disc);
            intent.putExtra("from","DoEdit");
            startActivity(intent);
            finish();
        }else {
            Intent intent=new Intent(getApplicationContext(),CreateNewInvoiceActivity.class);
            intent.putExtra("customerName",customer_name);
            intent.putExtra("customerCode",customer_code);
            intent.putExtra("editDoNumber", do_code);
            intent.putExtra("editDOInvDate", do_code);

            //intent.putExtra("customerBillDisc", customerBill_Disc);
            intent.putExtra("from","ConvertInvoiceFromDO");
            Log.w("acttionDODisc",""+customerBill_Disc);

            startActivity(intent);
            finish();
        }
    }



    private void setDeleteSalesOrder(String soNumber) throws JSONException {
        // Initialize a new RequestQueue instance

        JSONObject jsonObject=new JSONObject();
        jsonObject.put("CompanyCode",companyId);
        jsonObject.put("SoNo",soNumber);
        jsonObject.put("CreateUser",userName);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url= Utils.getBaseUrl(this) +"SalesApi/DeleteSO?Requestdata="+jsonObject.toString();
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:",url);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Deleting SalesOrder...");
        pDialog.setCancelable(false);
        pDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                response -> {
                    try{
                        Log.w("SaleOrder_Response_is:",response.toString());
                        if (response.length()>0){
                            boolean issaved=response.optBoolean("IsSaved");
                            String result=response.optString("Result");
                            boolean isDeleted=response.optBoolean("IsDeleted");
                            if (result.equals("pass") && isDeleted){
                                finish();
                                startActivity(getIntent());
                            }else {
                                Toast.makeText(getApplicationContext(),"Error in Deleting SalesOrder",Toast.LENGTH_LONG).show();
                            }
                        }
                        pDialog.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }, error -> {
            // Do something when error occurred
            pDialog.dismiss();
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
        public void getStockAdjustList(String pageNo, String status, String fromdate, String todate) {

            // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonObject=new JSONObject();

            try {
                jsonObject.put("FromDate",fromdate);
                jsonObject.put("ToDate", todate);
                jsonObject.put("DocStatus",status);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            stockAdjustmentList =new ArrayList<>();

            // Initialize a new JsonArrayRequest instance
        String url = Utils.getBaseUrl(this) + "GoodsReceiptList";
        Log.w("Given_url_adjustList:",url+jsonObject);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Getting stock adjustment...");
        pDialog.setCancelable(false);
        if (pageNo.equals("1")){
            pDialog.show();
        }
        @SuppressLint("NotifyDataSetChanged") JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                response -> {
                    try{
                        Log.w("adjustResponse:",response.toString());

                        pDialog.dismiss();
                        String statusCode=response.optString("statusCode");
                        if (statusCode.equals("1")){
                            JSONArray salesOrderArray=response.optJSONArray("responseData");
                            for (int i=0;i<salesOrderArray.length();i++){
                                JSONObject object=salesOrderArray.optJSONObject(i);
                                StockAdjustmentModuleModel model=new StockAdjustmentModuleModel();

                                model.setDate(object.optString("docDate"));
                                model.setCode(object.optString("code"));
                                model.setNumber(object.optString("docNum"));
                                model.setNetTotal(object.optString("netTotal"));
                                model.setDoStatus(object.optString("docStatus"));

                                stockAdjustmentList.add(model);
                            }
//
                            if (stockAdjustmentList.size()>0){
                                if(stockAdjustAdapter !=null) {
                                    stockAdjustAdapter.notifyDataSetChanged();
                                }
                                stockAdjustListView.setVisibility(View.VISIBLE);
                                emptyLayout.setVisibility(View.GONE);
                                setAdapter();

                            }else {
                                stockAdjustListView.setVisibility(View.GONE);
                                emptyLayout.setVisibility(View.VISIBLE);
                            }
                            selectedCustomerId ="";

                        }else {
                            stockAdjustListView.setVisibility(View.GONE);
                            emptyLayout.setVisibility(View.VISIBLE);
                            selectedCustomerId ="";

                         // Toast.makeText(getApplicationContext(),"Error in getting SalesOrder Data",Toast.LENGTH_LONG).show();
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }, error -> {
            pDialog.dismiss();
            // Do something when error occurred
            Log.w("Error_throwing:",error.toString());
            Toast.makeText(getApplicationContext(),"Server Error,Please try again..",Toast.LENGTH_LONG).show();
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

    public void setShowHide(){
        if (stockAdjustmentList.size()>0){
            stockAdjustListView.setVisibility(View.VISIBLE);
            //outstandingLayout.setVisibility(View.VISIBLE);
        }else {
            stockAdjustListView.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
            //outstandingLayout.setVisibility(View.GONE);
        }
    }


    public void setFilterAdapeter(){
        stockAdjustListView.setVisibility(View.VISIBLE);
        emptyLayout.setVisibility(View.GONE);
        outstandingLayout.setVisibility(View.GONE);
        stockAdjustListView.setHasFixedSize(true);
        stockAdjustListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        stockAdjustAdapter =new StockAdjustmentAdapter(this, stockAdjustListView,
                stockAdjustmentList,this);

        stockAdjustListView.setAdapter(stockAdjustAdapter);

        stockAdjustAdapter.setOnLoadMoreListener(new StockAdjustmentAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e("haint", "Load More");
                stockAdjustmentList.add(null);
                stockAdjustAdapter.notifyItemInserted(stockAdjustmentList.size() - 1);
                //Load more data for reyclerview
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("haint", "Load More 2");

                        //Remove loading item
                        stockAdjustmentList.remove(stockAdjustmentList.size() - 1);
                        stockAdjustAdapter.notifyItemRemoved(stockAdjustmentList.size());
                        //Load data
                        int index = stockAdjustmentList.size();
                        int end = index + 20;
                        pageNo=pageNo+1;
                       // getSalesOrderList(companyId, String.valueOf(pageNo));
                    }
                }, 5000);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sorting_menu, menu);
        // MenuItem action_save = menu.findItem(R.id.action_filter);
        // action_save.setVisible(false);

        MenuItem action_barcode = menu.findItem(R.id.action_barcode);
        action_barcode.setVisible(false);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {//finish();
            onBackPressed();
        }else if (item.getItemId()==R.id.action_add){
            Intent intent=new Intent(getApplicationContext(), NewStockAdjustmentProductAddActivity.class);
            startActivity(intent);
//            intent.putExtra("from","do");
//            startActivityForResult(intent,customerSelectCode);
        }
        else if (item.getItemId()==R.id.action_filter){
            if (searchFilterView.getVisibility()==View.VISIBLE){
                searchFilterView.setVisibility(View.GONE);
                isSearchCustomerNameClicked=false;
                if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                //slideUp(searchFilterView);
            }else {
                isSearchCustomerNameClicked=false;
                searchFilterView.setVisibility(View.VISIBLE);
                if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                // slideDown(searchFilterView);
            }
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == customerSelectCode) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("customerCode");
                Utils.setCustomerSession(this, result);
                Intent intent = new Intent(StockAdjustmentListActivity.this, AddInvoiceActivityOld.class);
                intent.putExtra("customerId", result);
                intent.putExtra("activityFrom", "DeliveryOrder");
                startActivity(intent);
                //  finish();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }
//        else if (requestCode == request_cust_code && resultCode == Activity.RESULT_OK) {
//                    selectedCustomerId = data.getStringExtra("customerCode");
//                    String selectCustomerName = data.getStringExtra("customerName");
//                    Log.w("custCodfiltaa", "" + selectedCustomerId);
//                    customerNameText.setText(selectCustomerName);
//                }

    } //onActivityResult

    @Override
    public void showMoreOption(String deliveryorderId, String status) {
        adjustOptionLayout.setVisibility(View.VISIBLE);
        number.setText(deliveryorderId);
        deliveryOrderStatus =status;
        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    public void closeView(){
        if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

    }
    public void viewCloseBottomSheet(){
        hideKeyboard();
        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public void hideKeyboard(){
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void getDate(EditText dateEditext){
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(StockAdjustmentListActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateEditext.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }


    @Override
    public void onBackPressed() {
        //Execute your code here
        // Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        // startActivity(intent);
        finish();

    }

}