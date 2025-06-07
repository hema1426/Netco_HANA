package com.winapp.saperp.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.winapp.saperp.R;
import com.winapp.saperp.adapter.OrderHistoryCatalogAdapter;
import com.winapp.saperp.adapter.SelectCustomerAdapter;
import com.winapp.saperp.db.DBHelper;
import com.winapp.saperp.fragments.CustomerFragment;
import com.winapp.saperp.model.CartModel;
import com.winapp.saperp.model.CustomerDetails;
import com.winapp.saperp.model.CustomerModel;
import com.winapp.saperp.model.OrderHeader;
import com.winapp.saperp.model.SalesOrderPrintPreviewModel;
import com.winapp.saperp.model.SettingsModel;
import com.winapp.saperp.model.UserListModel;
import com.winapp.saperp.printpreview.SalesOrderPrintPreview;
import com.winapp.saperp.utils.BarCodeScanner;
import com.winapp.saperp.utils.Constants;
import com.winapp.saperp.utils.ImageUtil;
import com.winapp.saperp.utils.LocationTrack;
import com.winapp.saperp.utils.SessionManager;
import com.winapp.saperp.utils.SettingUtils;
import com.winapp.saperp.utils.SharedPreferenceUtil;
import com.winapp.saperp.utils.UserAdapter;
import com.winapp.saperp.utils.Utils;
import com.winapp.saperp.zebraprinter.TSCPrinter;
import com.winapp.saperp.zebraprinter.ZebraPrinterActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class OrderHistoryListActivity extends NavigationActivity implements AdapterView.OnItemSelectedListener {

    public static RecyclerView orderHistoryView;
    public static OrderHistoryCatalogAdapter orderHistoryCatalogAdapter;
    private ArrayList<OrderHeader> orderHistoryList;
    private SweetAlertDialog pDialog;
    private SessionManager session;
    private HashMap<String, String> user;
    private String companyId,companyName;
    double currentLocationLatitude = 0.0;
    double currentLocationLongitude = 0.0;
    public static String signatureString = "";
    int pageNo = 1;
    private BottomSheetBehavior behavior;
    private ArrayList<CustomerModel> customerList;
    private SelectCustomerAdapter customerNameAdapter;
    private RecyclerView customerView;
    public static TextView selectCustomer;
    public Button btnCancel;
    public TextView customerName;
    public EditText customerNameEdittext;
    public DBHelper dbHelper;
    public TextView netTotalText;
    public boolean isPrintEnable = false;
    double netTotalApi = 0.00;
    public static String netSubtottalValue;
    public static String netTotalValue;
    public static String totalValue;
    private String subTotalValue;
    public static String itemDiscountAmount = "0.00";
    public static String netTaxvalue;
    public static String billDiscountAmount = "0.00";

    private String currentSaveDateTime = "";
    public TextView subTotalTextValue;
    public TextView itemDiscountText;
    public EditText billDiscAmount;
    public EditText billDiscPercentage;

    private ArrayList<CustomerDetails> customerDetails;
    public LinearLayout transLayout;
    public View customerLayout;
    public View salesOrderOptionLayout;
    public TextView soCustomerName;
    public TextView soNumber;
    public TextView optionCancel;
    public TextView cancelSheet;
    public String userName;
    public FloatingActionButton editSalesOrder;
    public FloatingActionButton deleteSaleOrder;
    public FloatingActionButton convertToInvoice;
    public FloatingActionButton printPreview;
    public String locationCode;
    public static String current_latitude = "0.00";
    public static String current_longitude = "0.00";
    public static String billDiscountPercentage;
    public static String current_addr = "";
    TextView locationText;

    public String salesOrderStatus;
    public LinearLayout editLayout;
    public LinearLayout deleteLayout;
    public LinearLayout convertLayout;
    public LinearLayout printPreviewLayout;
    boolean isSearchCustomerNameClicked;
    boolean addnewCustomer;
    public View searchFilterView;
    public EditText customerNameText;
    private int mYear, mMonth, mDay, mHour, mMinute;
    public EditText fromDate;
    public EditText toDate;
    public Button searchButton;
    public Button cancelSearch;
    public Spinner salesOrderStatusSpinner;
    public static LinearLayout emptyLayout;
    public static LinearLayout outstandingLayout;
    public String printSoNumber;
    public String noOfCopy;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private ArrayList<SalesOrderPrintPreviewModel> salesOrderHeaderDetails;
    private ArrayList<SalesOrderPrintPreviewModel.SalesList> salesPrintList;
    private String printerMacId;
    private String printerType;
    private SharedPreferences sharedPreferences;
    public View progressLayout;
    boolean redirectInvoice;
    public static String selectedCustomerId = "";
    public String isFound = "true";
    private Button createSalesOrder;
    private int customerSelectCode = 13;
    public String createInvoiceSetting = "true";
    public String editSo = "false";
    private int FILTER_CUSTOMER_CODE = 134;
    String currentDate;
    LocationTrack locationTrack;

    private ArrayList<UserListModel> usersList;
    private Spinner salesManSpinner;
    private String selectedUser = "";
    public static String shortCodeStr = "";
    public String userPermission = "";
    public String currentDateString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_catalog_order_history, contentFrameLayout);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Order History");

        orderHistoryView = findViewById(R.id.orderHistoryList);
        dbHelper = new DBHelper(this);
        session = new SessionManager(this);
        user = session.getUserDetails();
        sharedPreferenceUtil = new SharedPreferenceUtil(this);

        companyId = user.get(SessionManager.KEY_COMPANY_CODE);
        userName = user.get(SessionManager.KEY_USER_NAME);
        locationCode = user.get((SessionManager.KEY_LOCATION_CODE));
        companyName = user.get(SessionManager.KEY_COMPANY_NAME);

        customerView = findViewById(R.id.customerList);
        netTotalText = findViewById(R.id.net_total_List);
        customerNameEdittext = findViewById(R.id.customer_search);
        transLayout = findViewById(R.id.trans_layout);
        customerDetails = dbHelper.getCustomer();
        customerLayout = findViewById(R.id.customer_layout);
        salesOrderOptionLayout = findViewById(R.id.sales_option);
        soCustomerName = findViewById(R.id.name);
        soNumber = findViewById(R.id.so_no);
        optionCancel = findViewById(R.id.option_cancel);
        cancelSheet = findViewById(R.id.cancel_sheet);
        editSalesOrder = findViewById(R.id.edit_salesorder);
        deleteSaleOrder = findViewById(R.id.delete_salesorder);
        convertToInvoice = findViewById(R.id.convert_to_invoice);
        editLayout = findViewById(R.id.edit_layout);
        deleteLayout = findViewById(R.id.delete_layout);
        convertLayout = findViewById(R.id.convert_layout);
        printPreview = findViewById(R.id.print_preview);
        printPreviewLayout = findViewById(R.id.print_preview_layout);
        searchFilterView = findViewById(R.id.search_filter);
        customerNameText = findViewById(R.id.customer_name_value);
        fromDate = findViewById(R.id.from_date);
        toDate = findViewById(R.id.to_date);
        salesOrderStatusSpinner = findViewById(R.id.invoice_status);
        emptyLayout = findViewById(R.id.empty_layout);
        cancelSearch = findViewById(R.id.btn_cancel);
        searchButton = findViewById(R.id.btn_search);
        outstandingLayout = findViewById(R.id.outstanding_layout);
        progressLayout = findViewById(R.id.progress_layout);
        createSalesOrder = findViewById(R.id.create_sales);
        salesManSpinner = findViewById(R.id.salesman_spinner);
        salesManSpinner.setOnItemSelectedListener(this);

        shortCodeStr = sharedPreferenceUtil.getStringPreference(sharedPreferenceUtil
                .KEY_SHORT_CODE, "");
        // userPermission = sharedPreferenceUtil.getStringPreference(sharedPreferenceUtil.KEY_ADMIN_PERMISSION,"");

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        fromDate.setText(formattedDate);
        toDate.setText(formattedDate);

        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE);
        printerType = sharedPreferences.getString("printer_type", "");
        printerMacId = sharedPreferences.getString("mac_address", "");

        Log.w("Printer_Mac_Id:", printerMacId);
        Log.w("Printer_Type:", printerType);

        SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        currentDateString = df1.format(c);

        ArrayList<SettingsModel> settings = dbHelper.getSettings();
        if (settings != null) {
            if (settings.size() > 0) {
                for (SettingsModel model : settings) {
                    if (model.getSettingName().equals("create_invoice_switch")) {
                        Log.w("SettingName:", model.getSettingName());
                        Log.w("SettingValue:", model.getSettingValue());
                        if (model.getSettingValue().equals("1")) {
                            createInvoiceSetting = "true";
                        } else {
                            createInvoiceSetting = "false";
                        }
                    } else if (model.getSettingName().equals("editSO")) {
                        Log.w("SettingName:", model.getSettingName());
                        Log.w("SettingValue:", model.getSettingValue());
                        if (model.getSettingValue().equals("True")) {
                            editSo = "true";
                        } else {
                            editSo = "false";
                        }
                    }
                }
            }
        }

        //dbHelper.removeAllProducts();

        orderHistoryList = new ArrayList<>();
        orderHistoryList = dbHelper.getCatelogOrderHistory();
        Log.w("orderHisSize:", ""+orderHistoryList);
    /*    customerList=dbHelper.getAllCustomers();
        if (customerList!=null && customerList.size()>0){
            setAdapter(customerList);
        }else {
            getCustomers();
           // new GetCustomersTask().execute();
        }*/


        Date c1 = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c1);
        SimpleDateFormat dfa = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        currentDate = dfa.format(c1);

        orderHistoryView.setHasFixedSize(true);
        orderHistoryView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        orderHistoryCatalogAdapter = new OrderHistoryCatalogAdapter(this, orderHistoryView,
                orderHistoryList, new OrderHistoryCatalogAdapter.CallBack() {
            @Override
            public void calculateNetTotal(ArrayList<OrderHeader> salesList) {

            }

            //            @Override
//            public void calculateNetTotal(ArrayList<OrderHeader>  salesList) {
//                setNettotalFun(salesList);
//            }
            @Override
            public void showMoreOption(String salesorderId, String customerName, String status) {
                customerLayout.setVisibility(View.GONE);
                salesOrderOptionLayout.setVisibility(View.VISIBLE);
                soNumber.setText(salesorderId);
                soCustomerName.setText(customerName);
                salesOrderStatus = status;
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                // viewCloseBottomSheet();
            }

            @Override
            public void syncNowCall(OrderHeader order, ArrayList<CartModel> cartModel) {
               syncNow(order,cartModel);
            }
        });
        orderHistoryView.setAdapter(orderHistoryCatalogAdapter);


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
                        if (salesOrderOptionLayout.getVisibility() == View.VISIBLE) {
                            getSupportActionBar().setTitle("Select Option");
                        } else {
                            getSupportActionBar().setTitle("Select Customer");
                        }
                        transLayout.setVisibility(View.VISIBLE);
                        transLayout.setClickable(false);
                        transLayout.setEnabled(false);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_COLLAPSED");
                        getSupportActionBar().setTitle("Order History");
                        transLayout.setVisibility(View.GONE);
                        if (redirectInvoice) {
                            if (createInvoiceSetting.equals("true")) {
                                Intent intent = new Intent(getApplicationContext(), CreateNewInvoiceActivity.class);
                                intent.putExtra("customerName", soCustomerName.getText().toString());
                                intent.putExtra("customerCode", selectedCustomerId.toString());
                                startActivity(intent);
                                finish();
                            } else {
                                CustomerFragment.isLoad = true;
                                Intent intent = new Intent(OrderHistoryListActivity.this, AddInvoiceActivityOld.class);
                                intent.putExtra("customerId", selectedCustomerId);
                                intent.putExtra("activityFrom", "SalesOrder");
                                startActivity(intent);
                                finish();
                            }
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

        customerNameEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String cusname = editable.toString();
                if (!cusname.isEmpty()) {
                    filter(cusname);
                }
            }
        });

        optionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
            }
        });

        cancelSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
            }
        });

        createSalesOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSearchCustomerNameClicked = false;
                customerLayout.setVisibility(View.VISIBLE);
                salesOrderOptionLayout.setVisibility(View.GONE);
                searchFilterView.setVisibility(View.GONE);
                //  viewCloseBottomSheet();
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                try {
//                    viewCloseBottomSheet();
//                    if (!salesOrderStatus.equals("Closed") && !salesOrderStatus.equals("InProgress Invoice")) {
//                        if (editSo.equals("true")) {
//                            getSalesOrderDetails(soNumber.getText().toString(), "Edit");
//                        } else {
//                            Toast.makeText(getApplicationContext(),
//                                    "You Don't have permission to Edit", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(getApplicationContext(),
//                                "This Sales order already Closed", Toast.LENGTH_SHORT).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        });

//        deleteLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                viewCloseBottomSheet();
//                if (salesOrderStatus.equals("Open")) {
//                    if (editSo.equals("true")) {
//                        showRemoveAlert(soNumber.getText().toString());
//                    } else {
//                        Toast.makeText(getApplicationContext(),
//                                "You Don't have permission to Delete", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(getApplicationContext(), "Can't Delete Closed SalesOrder", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });


        customerNameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSearchCustomerNameClicked = true;
                // viewCloseBottomSheet();
                Intent intent = new Intent(getApplicationContext(), FilterCustomerListActivity.class);
                startActivityForResult(intent, FILTER_CUSTOMER_CODE);
            }
        });

        printPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  viewCloseBottomSheet();
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                Intent intent = new Intent(OrderHistoryListActivity.this, SalesOrderPrintPreview.class);
                intent.putExtra("soNumber", soNumber.getText().toString());
                startActivity(intent);
            }
        });

        printPreviewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  viewCloseBottomSheet();
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                Intent intent = new Intent(OrderHistoryListActivity.this, SalesOrderPrintPreview.class);
                intent.putExtra("soNumber", soNumber.getText().toString());
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
                String customer_name = customerNameText.getText().toString();
                SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/yyyy");
                Date d1 = null;
                Date d2 = null;
                try {
                    d1 = sdformat.parse(fromDate.getText().toString());
                    d2 = sdformat.parse(toDate.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (d1.compareTo(d2) > 0) {
                    Toast.makeText(getApplicationContext(), "From date should not be greater than to date", Toast.LENGTH_SHORT).show();
                } else {
                    searchFilterView.setVisibility(View.GONE);
                    isSearchCustomerNameClicked = true;
                    try {
                        String oldFromDate = fromDate.getText().toString();
                        String oldToDate = toDate.getText().toString();
                        Date fromDate = new SimpleDateFormat("dd/MM/yyyy").parse(oldFromDate);
                        Date toDate = new SimpleDateFormat("dd/MM/yyyy").parse(oldToDate);
                        // Use SimpleDateFormat#format() to format a Date into a String in a certain pattern.

                        String fromDateString = new SimpleDateFormat("yyyyMMdd").format(fromDate);
                        String toDateString = new SimpleDateFormat("yyyyMMdd").format(toDate);
                        System.out.println(fromDateString + "-" + toDateString); // 2011-01-18
                        String invoice_status = "";
                        String usernamel = "";
                        if (salesOrderStatusSpinner.getSelectedItem().equals("ALL")) {
                            invoice_status = "";
                        } else if (salesOrderStatusSpinner.getSelectedItem().equals("CLOSED")) {
                            invoice_status = "C";
                        } else if (salesOrderStatusSpinner.getSelectedItem().equals("OPEN")) {
                            invoice_status = "O";
                        }
//                        if (userPermission.equalsIgnoreCase("True")) {
//                            usernamel = "All" ;
//                        }else {
//                            usernamel  = userName;
//                        }
                        // setFilterSearch(OrderHistoryListActivity.this,userName,companyId,selectedCustomerId,invoice_status,fromDateString,toDateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    ///filterSearch(customer_name, salesOrderStatusSpinner.getSelectedItem().toString(),fromDate.getText().toString(),toDate.getText().toString());
                    salesOrderStatusSpinner.setSelection(0);
                }
            }
        });

        cancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSearchCustomerNameClicked = false;
                customerNameText.setText("");
                fromDate.setText(formattedDate);
                toDate.setText(formattedDate);
                searchFilterView.setVisibility(View.GONE);
                salesOrderStatusSpinner.setSelection(0);
                setFilterAdapeter();
            }
        });
    }
    public void syncNow(OrderHeader orderHeader, ArrayList<CartModel> cartModel) {

            JSONObject rootJsonObject = new JSONObject();
            JSONObject invoiceHeader = new JSONObject();
            JSONObject signatureObject = new JSONObject();
            JSONObject invoiceImageObject = new JSONObject();
            JSONArray invoiceDetailsArray = new JSONArray();
            JSONObject invoiceObject = new JSONObject();
            JSONArray returnProductArray = new JSONArray();

            // Sales Header Add values
            ArrayList<CartModel> localCart = dbHelper.getAllCartItem_Temp2(orderHeader.getOrderId());
            double net_sub_total = 0.0;
            double net_tax = 0.0;
            double net_total = 0.0;
            double net_discount = 0;
            double total_value = 0;
            if (localCart.size() > 0) {
                for (CartModel model : localCart) {
                    if (model.getSubTotal() != null && !model.getSubTotal().isEmpty()) {
                        net_sub_total += Double.parseDouble(model.getSubTotal());
                    }
                    if (model.getCART_TAX_VALUE() != null && !model.getCART_TAX_VALUE().isEmpty()) {
                        net_tax += Double.parseDouble(model.getCART_TAX_VALUE());
                    }
                    if (model.getCART_COLUMN_NET_PRICE() != null && !model.getCART_COLUMN_NET_PRICE().isEmpty()) {
                        net_total += Double.parseDouble(model.getCART_COLUMN_NET_PRICE());
                    }
                    if (model.getDiscount() != null && !model.getDiscount().equals("null") && !model.getDiscount().isEmpty()) {
                        net_discount += Double.parseDouble(model.getDiscount());
                    }
                    if (model.getCART_TOTAL_VALUE() != null && !model.getCART_TOTAL_VALUE().equals("null")) {
                        total_value += Double.parseDouble(model.getCART_TOTAL_VALUE());
                    }
                }
            }


            String currentTimestamp = String.valueOf(System.currentTimeMillis());

//            custNameShared = sharedPreferenceUtil.getStringPreference(
//                    sharedPreferenceUtil.KEY_CUSTOMER_NAME, "");
//            custCodeShared = sharedPreferenceUtil.getStringPreference(
//                    sharedPreferenceUtil.KEY_CUSTOMER_CODE, "");
//            custTaxTypeShared = sharedPreferenceUtil.getStringPreference(
//                    sharedPreferenceUtil.KEY_CUSTOMER_TAXTYPE, "");
//            custTaxPercentShared = sharedPreferenceUtil.getStringPreference(
//                    sharedPreferenceUtil.KEY_CUSTOMER_TAXPERCENTAGE, "");
//            custTaxCodeShared = sharedPreferenceUtil.getStringPreference(
//                    sharedPreferenceUtil.KEY_CUSTOMER_TAXCODE, "");
//            custHavetaxShared = sharedPreferenceUtil.getStringPreference(
//                    sharedPreferenceUtil.KEY_CUSTOMER_HAVETAX,  "");

            if (orderHeader.taxType.equals("I")) {
                double sub_total = net_total - net_tax;
                double sub_total1 = sub_total + net_tax;

                netSubtottalValue = Utils.twoDecimalPoint(sub_total1);
                netTaxvalue = Utils.twoDecimalPoint(net_tax);
                netTotalValue = Utils.twoDecimalPoint(sub_total1);
                itemDiscountAmount = Utils.twoDecimalPoint(net_discount);
                totalValue = Utils.twoDecimalPoint(total_value);

            } else {
                netSubtottalValue = Utils.twoDecimalPoint(net_sub_total);
                netTaxvalue = Utils.twoDecimalPoint(net_tax);
                netTotalValue = Utils.twoDecimalPoint(net_total);
                itemDiscountAmount = Utils.twoDecimalPoint(net_discount);
                totalValue = Utils.twoDecimalPoint(total_value);
            }
//        JSONArray detailsArray = customerResponse.optJSONArray("responseData");
//        JSONObject object = detailsArray.optJSONObject(0);

            try {


                // Sales Header Add values
              //  Log.w("custcode..cart ",""+custCodeShared);

//                orderHeader.setInvoiceNumber("");
//                orderHeader.setMode("I");
//                orderHeader.setInvoiceDate(currentDate);
//                orderHeader.setCustomerCode(custCodeShared);
//                orderHeader.setCustomerName(custNameShared);
//                orderHeader.setCurrentAddress(current_addr);
//                orderHeader.setHaveTax(custHavetaxShared);
//                orderHeader.setTaxType(custTaxTypeShared);
//                orderHeader.setTaxPerc(custTaxPercentShared);
//                orderHeader.setTaxCode(custTaxCodeShared);
//                orderHeader.setCurrencyName("Singapore Dollar");
//                orderHeader.setCurrencyRate("1");
//                orderHeader.setTaxTotal(netTaxvalue);
//                orderHeader.setSubTotal(subTotalValue);
//                orderHeader.setTotal(totalValue);
//                orderHeader.setNetTotal(netTotalValue);
//                orderHeader.setNetTotal(itemDiscountAmount);
//                orderHeader.setBillDiscount(billDiscountAmount);
                rootJsonObject.put("CurrentAddress", current_addr);
                rootJsonObject.put("invoiceNumber", "");
            rootJsonObject.put("companyCode", companyId);
            rootJsonObject.put("companyName", companyName);
            rootJsonObject.put("createUser", userName);
            rootJsonObject.put("signature", "");
            rootJsonObject.put("subTotal", orderHeader.subTotal);
            rootJsonObject.put("creditLimit", "");
            rootJsonObject.put("CurrencyRate", "");
            rootJsonObject.put("invoiceType", orderHeader.invoiceType);
            rootJsonObject.put("stockUpdated", orderHeader.getStockUpdated());
            rootJsonObject.put("latitude", "0.00");
            rootJsonObject.put("modifyUser", userName);
            rootJsonObject.put("locationCode", locationCode);
            rootJsonObject.put("Paymode", "");
            rootJsonObject.put("totalDiscount", "");
            rootJsonObject.put("signature", orderHeader.signature);
            rootJsonObject.put("mode", "I");
            rootJsonObject.put("soNo", "");
            rootJsonObject.put("doNo", "");
            rootJsonObject.put("invoiceDate", currentDate);
            rootJsonObject.put("customerCode", orderHeader.getCustomerCode());
            rootJsonObject.put("customerName", orderHeader.getCustomerName());
            rootJsonObject.put("address", "");
            rootJsonObject.put("street", "");
            rootJsonObject.put("city", "");
            rootJsonObject.put("creditLimit", "");
            rootJsonObject.put("remark", "");
            rootJsonObject.put("delCustomerName", "");
            rootJsonObject.put("delAddress1", "");
            rootJsonObject.put("delAddress2 ", "");
            rootJsonObject.put("delAddress3 ", "");
            rootJsonObject.put("delPhoneNo", "");
            rootJsonObject.put("haveTax", orderHeader.getHaveTax());
            rootJsonObject.put("taxType", orderHeader.taxType);
            rootJsonObject.put("taxPerc", orderHeader.taxPerc);
            rootJsonObject.put("taxCode", orderHeader.taxCode);
            rootJsonObject.put("currencyCode", "");
            rootJsonObject.put("currencyValue", "");
            rootJsonObject.put("currencyRate", "1");
            rootJsonObject.put("postalCode", "");
            rootJsonObject.put("currencyName", "Singapore Dollar");
            rootJsonObject.put("Remark", "");
            rootJsonObject.put("customerReferenceNo", "");
            rootJsonObject.put("taxTotal", netTaxvalue);
            rootJsonObject.put("subTotal", subTotalValue);
            rootJsonObject.put("total", totalValue);
            rootJsonObject.put("netTotal", netTotalValue);
            rootJsonObject.put("itemDiscount", itemDiscountAmount);
            rootJsonObject.put("billDiscount", billDiscountAmount);
                if (currentSaveDateTime == null || currentSaveDateTime.isEmpty()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());
                    currentSaveDateTime = currentDateandTime;
                }
                rootJsonObject.put("currentDateTime", currentSaveDateTime);
                rootJsonObject.put("deliveryCode", SettingUtils.getDeliveryAddressCode());


//            rootJsonObject.put("totalDiscount", "0");
                rootJsonObject.put("billDiscountPercentage", billDiscountPercentage);
//            rootJsonObject.put("deliveryCode", SettingUtils.getDeliveryAddressCode());
//            rootJsonObject.put("delCustomerName", "");
//            rootJsonObject.put("currencyValue", "");
//            rootJsonObject.put("CurrencyRate", "1");
                rootJsonObject.put("status", "0");
//            rootJsonObject.put("createUser", userName);
//            rootJsonObject.put("modifyUser", userName);
//            rootJsonObject.put("companyName", companyName);
//            rootJsonObject.put("stockUpdated", "1");
//            rootJsonObject.put("invoiceType", "M");
//            rootJsonObject.put("companyCode", companyCode);
//            rootJsonObject.put("locationCode", locationCode);
//            rootJsonObject.put("latitude", current_latitude);
//            rootJsonObject.put("longitude", current_longitude);
//            rootJsonObject.put("CurrentAddress", current_addr);
//            rootJsonObject.put("Paymode", "");
//            rootJsonObject.put("ChequeDateString", "");
//            rootJsonObject.put("BankCode", "");
//            rootJsonObject.put("AccountNo", "");
//            rootJsonObject.put("ChequeNo", "");
//            rootJsonObject.put("image", imageString);
//            rootJsonObject.put("signature", signatureString);
             //   orderHeader.setOrderId(currentTimestamp);

             //   dbHelper.insertOrderHeader(orderHeader);
                // Sales Details Add to the Objects
//                dbHelper.updateCartTemp2OrderId(currentTimestamp,selectCustomerId);
//                localCart = dbHelper.getAllCartItems2();
                localCart = cartModel;


                int index = 1;
                for (CartModel model : localCart) {
                    invoiceObject = new JSONObject();
                    rootJsonObject.put("invoiceNumber", "");
                    invoiceObject.put("companyCode", companyId);
                    invoiceObject.put("invoiceDate", currentDateString);
                    invoiceObject.put("slNo", index);
                    invoiceObject.put("productCode", model.getCART_COLUMN_PID());
                    invoiceObject.put("productName", model.getCART_COLUMN_PNAME());
                    invoiceObject.put("cartonQty", model.getCART_COLUMN_CTN_QTY());
                    invoiceObject.put("unitQty", model.getCART_COLUMN_QTY());
                    double data = Double.parseDouble(model.getCART_PCS_PER_CARTON());
                    double cn_qty = Double.parseDouble(model.getCART_COLUMN_CTN_QTY());
                    double lqty = Double.parseDouble(model.getCART_COLUMN_QTY());
                    double net_qty = (cn_qty * data) + lqty;
                    invoiceObject.put("qty", String.valueOf(net_qty));
                    // convert into int
                    int value = (int) data;
                    invoiceObject.put("pcsPerCarton", String.valueOf(value));
                    //    double priceValue=Double.parseDouble(model.getCART_UNIT_PRICE()) / net_qty;
//                if (object.optString("taxType").equals("I")){
//                    invoiceObject.put("price",Utils.twoDecimalPoint(priceValue));
//                }else {
//                    invoiceObject.put("price",Utils.twoDecimalPoint(priceValue));
//                }
                    invoiceObject.put("price", model.getCART_COLUMN_CTN_PRICE());
                    //  invoiceObject.put("cartonPrice",model.getCART_COLUMN_CTN_PRICE());
                    invoiceObject.put("total", model.getCART_TOTAL_VALUE());
                    if (model.getDiscount() != null && !model.getDiscount().isEmpty()) {
                        invoiceObject.put("itemDiscount", model.getDiscount());
                        //     invoiceObject.put("DiscountPercentage",model.getDiscount());

                    } else {
                        invoiceObject.put("itemDiscount", "0.00");
                        //   invoiceObject.put("DiscountPercentage","0.00");
                    }
                    invoiceObject.put("totalTax", model.getCART_TAX_VALUE());
                    invoiceObject.put("subTotal", model.getSubTotal());
                    invoiceObject.put("netTotal", model.getCART_COLUMN_NET_PRICE());
                    invoiceObject.put("taxType", orderHeader.taxType);
                    invoiceObject.put("taxPerc", orderHeader.taxPerc);
                    invoiceObject.put("taxCode", orderHeader.taxCode);
                    double return_subtotal = 0;
                    if (model.getReturn_qty() != null && !model.getReturn_qty().isEmpty() && !model.getReturn_qty().equals("null")) {
                        return_subtotal = Double.parseDouble(model.getReturn_qty()) * Double.parseDouble(model.getCART_UNIT_PRICE());
                    }

                    assert model.getReturn_qty() != null;
                    if (!model.getReturn_qty().isEmpty() && !model.getReturn_qty().toString().equals("null")) {
                        invoiceObject.put("returnLQty", model.getReturn_qty());
                        invoiceObject.put("returnQty", model.getReturn_qty());
                    } else {
                        invoiceObject.put("returnLQty", "0");
                        invoiceObject.put("returnQty", "0");
                    }

                    if (!model.getFoc_qty().toString().isEmpty() && !model.getFoc_qty().equals("null")) {
                        invoiceObject.put("focQty", model.getFoc_qty());
                    } else {
                        invoiceObject.put("focQty", "0");
                    }

                    if (!model.getExchange_qty().isEmpty() && !model.getExchange_qty().equals("null")) {
                        invoiceObject.put("exchangeQty", model.getExchange_qty());
                    } else {
                        invoiceObject.put("exchangeQty", "0");

                    }

                    invoiceObject.put("returnSubTotal", return_subtotal + "");
                    invoiceObject.put("returnNetTotal", return_subtotal + "");
                    invoiceObject.put("returnReason", "");
                    invoiceObject.put("uomCode", model.getUomCode());
                    invoiceObject.put("retailPrice", model.getCART_COLUMN_CTN_PRICE());
                    invoiceObject.put("itemRemarks", "");
                    invoiceObject.put("locationCode", locationCode);
                    invoiceObject.put("createUser", userName);
                    invoiceObject.put("modifyUser", userName);

                    returnProductArray=new JSONArray();
                    JSONObject returnProductObject = new JSONObject();

                    if (!model.getReturn_qty().isEmpty() && !model.getReturn_qty().toString().equals("null")) {
                        returnProductObject=new JSONObject();
                        returnProductObject.put("ReturnReason","Saleable Return");
                        returnProductObject.put("ReturnQty",model.getReturn_qty());
                        returnProductArray.put(returnProductObject);
                    }
                    invoiceObject.put("ReturnDetails", returnProductArray);

                    invoiceDetailsArray.put(invoiceObject);
                    index++;
                }

                signatureObject.put("InvoiceNo", "");
                signatureObject.put("CompanyCode", companyId);
                signatureObject.put("Latitude", currentLocationLatitude);
                signatureObject.put("Longitude", currentLocationLongitude);
                signatureObject.put("RefSignature", signatureString);
                signatureObject.put("ModifyUser", userName);
                signatureObject.put("Modifydate", "");
                signatureObject.put("TranType", "IN");
                signatureObject.put("Address1", "");
                signatureObject.put("Address2", "");
                signatureObject.put("SlNo", 0);
                signatureObject.put("RefSignaturestring", null);


//            invoiceImageObject.put("InvoiceNo", "");
//            invoiceImageObject.put("CompanyCode", companyCode);
//            invoiceImageObject.put("SlNo", 0);
//            invoiceImageObject.put("TranType", "IN");
//            invoiceImageObject.put("CustomerCode", object.get("customerCode"));
//            invoiceImageObject.put("CustomerName", object.get("customerName"));
//            invoiceImageObject.put("DeliveryCode", SettingUtils.getDeliveryAddressCode());
//            invoiceImageObject.put("CompanyName", user.get(SessionManager.KEY_COMPANY_NAME));
//            invoiceImageObject.put("ModifyUser", userName);
//            invoiceImageObject.put("RefPhotostring", null);


                // rootJsonObject.put("IsSaveSO",false);
                //  rootJsonObject.put("InvoiceHeader", invoiceHeader);
                //rootJsonObject.put("ReturnDetails", returnProductArray);
                rootJsonObject.put("PostingInvoiceDetails", invoiceDetailsArray);
                //  rootJsonObject.put("InvoiceSignature",signatureObject);
                // rootJsonObject.put("InvoicePhoto",invoiceImageObject);

                Log.w("RootJsonForSave:", rootJsonObject.toString());


               // Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show();
//                redirectActivity();
            saveSalesOrder(rootJsonObject, "Invoice", 1);

            } catch (JSONException e) {
                e.printStackTrace();
                Log.w("Given_Error:", Objects.requireNonNull(e.getMessage()));
            }
        }





        public void saveSalesOrder(JSONObject jsonBody, String action, int copy) {
        try {
            SweetAlertDialog pDialog = new SweetAlertDialog(OrderHistoryListActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            if (action.equals("SalesOrder")) {
                pDialog.setTitleText("Saving Sales Order...");
            } else if (action.equals("DeliveryOrder")) {
                pDialog.setTitleText("Saving Delivery Order...");
            } else {
                pDialog.setTitleText("Saving Invoice...");
            }
            pDialog.setCancelable(false);
            pDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(OrderHistoryListActivity.this);
            Log.w("GivenInvoiceReqCart:", jsonBody.toString());
            String URL = "";
            if (action.equals("SalesOrder")) {
                URL = Utils.getBaseUrl(this) + "PostingSalesOrder";
            } else if (action.equals("DeliveryOrder")) {
                URL = Utils.getBaseUrl(this) + "PostingDeliveryOrder";
            } else {
                URL = Utils.getBaseUrl(this) + "PostingInvoice";
            }
            Log.w("Given_URL_InvApiCart:", URL);
            //    {"statusCode":2,"statusMessage":"Failed","responseData":{"docNum":null,"error":"Invoice :One of the base documents has already been closed  [INV1.BaseEntry][line: 1]"}}
            JsonObjectRequest salesOrderRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, response -> {
                Log.w("Invoice_Res_cartSap:", response.toString());
                Utils.clearCustomerSession(this);
                // dbHelper.removeCustomer();
                // {"statusCode":1,"statusMessage":"Invoice Created Successfully","responseData":{"docNum":"35","error":null}}
                pDialog.dismiss();
                String statusCode = response.optString("statusCode");
                String message = response.optString("statusMessage");
                JSONObject responseData = null;
                try {
                    responseData = response.getJSONObject("responseData");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
             //   Log.w("printenab_cart",""+isPrintEnable);
                if (statusCode.equals("1")) {
                    if (action.equals("SalesOrder") || action.equals("SalesEdit")) {
                        if (isPrintEnable) {
                            try {
                                dbHelper.removeAllItemsTemp2();
                                JSONObject object = response.optJSONObject("responseData");
                                String doucmentNo = object.optString("docNum");
                                //   String result=object.optString("Result");
                                if (!doucmentNo.isEmpty()) {
                                    // getSalesOrderDetails(doucmentNo, copy);
//                                    Intent intent = new Intent(this, SalesOrderListActivity.class);
                                    Intent intent = new Intent(this, CategoriesTemp2Activity.class);
                                    intent.putExtra("printSoNumber", doucmentNo);
                                    intent.putExtra("noOfCopy", String.valueOf(copy));
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error in getting printing data", Toast.LENGTH_SHORT).show();
                                  //  redirectActivity();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            dbHelper.removeAllItemsTemp2();
                          //  redirectActivity();
                        }
                        isPrintEnable = false;
                    } else {
                        if (isPrintEnable) {
                            try {
                                //updateStockQty();
                                dbHelper.removeAllItemsTemp2();
                                JSONObject object = response.optJSONObject("responseData");
                                String doucmentNo = object.optString("docNum");
                                //   String result=object.optString("Result");
                                if (!doucmentNo.isEmpty()) {
                                    // getInvoicePrintDetails(doucmentNo, copy);
//                                    Intent intent = new Intent(getApplicationContext(), NewInvoiceListActivity.class);
                                    Intent intent = new Intent(this, CategoriesTemp2Activity.class);

                                    intent.putExtra("printInvoiceNumber", doucmentNo);
                                    intent.putExtra("noOfCopy", String.valueOf(copy));
                                  //  intent.putExtra("DOPrint", isDeliveryPrint);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error in getting printing data", Toast.LENGTH_SHORT).show();
                                //    redirectActivity();
                                }
                                Log.w("cartSavEntr", "");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.w("cartSavEntr1", "");

                            //updateStockQty();
                            dbHelper.removeAllItemsTemp2();
                          //  redirectActivity();

//                            if (message.equals("Invoice Created Successfully")){
//                                try {
//                                    redirectActivity();
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
                        }
                        isPrintEnable = false;
                    }
                } else {
                    //  Log.w("ErrorValues:", responseData.optString("error"));
                    if (responseData != null) {
                        Toast.makeText(getApplicationContext(), responseData.optString("error"), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error in Saving Data...", Toast.LENGTH_SHORT).show();
                    }
                }
            }, error -> {
                Log.w("SalesOrder_Response:", error.toString());
                pDialog.dismiss();
            }) {
                /* @Override
                 public byte[] getBody() {
                     return jsonBody.toString().getBytes();
                 }*/
                @Override
                public String getBodyContentType() {
                    return "application/json";
                }

                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> params = new HashMap<>();
                    String creds = String.format("%s:%s", Constants.API_SECRET_CODE, Constants.API_SECRET_PASSWORD);
                    String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                    params.put("Authorization", auth);
                    return params;
                }
            };
            salesOrderRequest.setRetryPolicy(new RetryPolicy() {
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
            requestQueue.add(salesOrderRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setCustomerDetails(String customerId) {
        SharedPreferences sharedPreferences = getSharedPreferences("customerPref", MODE_PRIVATE);
        SharedPreferences.Editor customerPredEdit = sharedPreferences.edit();
        customerPredEdit.putString("customerId", customerId);
        customerPredEdit.apply();
    }


   /* public void getSalesOrderDetails(String soNumber,int copy) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("SalesOrderNo", soNumber);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url= Utils.getBaseUrl(this) +"SalesOrderDetails";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:",url);
        salesOrderHeaderDetails =new ArrayList<>();
        salesPrintList =new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try{
                        Log.w("Sales_DetailsSAP::",response.toString());
                        if (response.length()>0) {
                            SalesOrderPrintPreviewModel model = new SalesOrderPrintPreviewModel();
                            model.setSoNumber(response.optString("SoNo"));
                            model.setSoDate(response.optString("SoDateString"));
                            model.setCustomerCode(response.optString("CustomerCode"));
                            model.setCustomerName(response.optString("CustomerName"));
                            model.setAddress(response.optString("Address1"));
                            model.setDeliveryAddress(response.optString("Address1"));
                            model.setSubTotal(response.optString("SubTotal"));
                            model.setNetTax(response.optString("Tax"));
                            model.setNetTotal(response.optString("NetTotal"));
                            model.setTaxType(response.optString("TaxType"));
                            model.setTaxValue(response.optString("TaxPerc"));
                            model.setOutStandingAmount(response.optString("BalanceAmount"));
                            model.setBillDiscount(response.optString("BillDIscount"));
                            model.setItemDiscount(response.optString("ItemDiscount"));
                            JSONArray products = response.getJSONArray("SoDetails");
                            for (int i = 0; i < products.length(); i++) {
                                JSONObject object = products.getJSONObject(i);
                                if (Double.parseDouble(object.optString("LQty")) > 0) {
                                    SalesOrderPrintPreviewModel.SalesList salesListModel = new SalesOrderPrintPreviewModel.SalesList();

                                    salesListModel.setProductCode(object.optString("ProductCode"));
                                    salesListModel.setDescription(object.optString("ProductName"));
                                    salesListModel.setLqty(object.optString("LQty"));
                                    salesListModel.setCqty(object.optString("CQty"));
                                    salesListModel.setNetQty(object.optString("LQty"));
                                    salesListModel.setCartonPrice(object.optString("CartonPrice"));
                                    salesListModel.setUnitPrice(object.optString("Price"));
                                    double qty = Double.parseDouble(object.optString("LQty"));
                                    double price = Double.parseDouble(object.optString("Price"));

                                    double nettotal = qty * price;
                                    salesListModel.setTotal(String.valueOf(nettotal));
                                    salesListModel.setPricevalue(String.valueOf(price));

                                    salesListModel.setPcsperCarton(object.optString("PcsPerCarton"));
                                    salesListModel.setItemtax(object.optString("Tax"));
                                    salesListModel.setSubTotal(object.optString("SubTotal"));
                                    salesPrintList.add(salesListModel);


                                    if (Double.parseDouble(object.optString("CQty")) > 0) {
                                        salesListModel = new SalesOrderPrintPreviewModel.SalesList();
                                        salesListModel.setProductCode(object.optString("ProductCode"));
                                        salesListModel.setDescription(object.optString("ProductName"));
                                        salesListModel.setLqty(object.optString("LQty"));
                                        salesListModel.setCqty(object.optString("CQty"));
                                        salesListModel.setNetQty(object.optString("CQty"));

                                        double qty1 = Double.parseDouble(object.optString("CQty"));
                                        double price1 = Double.parseDouble(object.optString("CartonPrice"));
                                        double nettotal1 = qty1 * price1;
                                        salesListModel.setTotal(String.valueOf(nettotal1));
                                        salesListModel.setPricevalue(String.valueOf(price1));

                                        salesListModel.setUomCode(object.optString("UOMCode"));
                                        salesListModel.setCartonPrice(object.optString("CartonPrice"));
                                        salesListModel.setUnitPrice(object.optString("Price"));
                                        salesListModel.setPcsperCarton(object.optString("PcsPerCarton"));
                                        salesListModel.setItemtax(object.optString("Tax"));
                                        salesListModel.setSubTotal(object.optString("SubTotal"));
                                        salesPrintList.add(salesListModel);
                                    }

                                    if (!object.optString("ReturnQty").isEmpty() && Double.parseDouble(object.optString("ReturnQty")) > 0) {
                                        salesListModel = new SalesOrderPrintPreviewModel.SalesList();
                                        salesListModel.setProductCode(object.optString("ProductCode"));
                                        salesListModel.setDescription(object.optString("ProductName"));
                                        salesListModel.setLqty(object.optString("LQty"));
                                        salesListModel.setCqty(object.optString("CQty"));
                                        salesListModel.setNetQty(object.optString("ReturnQty"));

                                        double qty1 = Double.parseDouble(object.optString("ReturnQty"));
                                        double price1 = Double.parseDouble(object.optString("Price"));
                                        double nettotal1 = qty1 * price1;
                                        salesListModel.setTotal(String.valueOf(nettotal1));
                                        salesListModel.setPricevalue(String.valueOf(price1));

                                        salesListModel.setUomCode(object.optString("UOMCode"));
                                        salesListModel.setCartonPrice(object.optString("CartonPrice"));
                                        salesListModel.setUnitPrice(object.optString("Price"));
                                        salesListModel.setPcsperCarton(object.optString("PcsPerCarton"));
                                        salesListModel.setItemtax(object.optString("Tax"));
                                        salesListModel.setSubTotal(object.optString("SubTotal"));
                                        salesPrintList.add(salesListModel);
                                    }

                                } else {
                                    if (Double.parseDouble(object.optString("CQty")) > 0) {
                                        SalesOrderPrintPreviewModel.SalesList salesListModel = new SalesOrderPrintPreviewModel.SalesList();
                                        salesListModel.setProductCode(object.optString("ProductCode"));
                                        salesListModel.setDescription(object.optString("ProductName"));
                                        salesListModel.setLqty(object.optString("LQty"));
                                        salesListModel.setCqty(object.optString("CQty"));
                                        salesListModel.setNetQty(object.optString("Qty"));
                                        salesListModel.setCartonPrice(object.optString("CartonPrice"));
                                        salesListModel.setUnitPrice(object.optString("Price"));

                                        double qty1 = Double.parseDouble(object.optString("CQty"));
                                        double price1 = Double.parseDouble(object.optString("CartonPrice"));
                                        double nettotal1 = qty1 * price1;
                                        salesListModel.setTotal(String.valueOf(nettotal1));
                                        salesListModel.setPricevalue(String.valueOf(price1));

                                        salesListModel.setUomCode(object.optString("UOMCode"));
                                        salesListModel.setPcsperCarton(object.optString("PcsPerCarton"));
                                        salesListModel.setItemtax(object.optString("Tax"));
                                        salesListModel.setSubTotal(object.optString("SubTotal"));
                                        salesPrintList.add(salesListModel);


                                        if (!object.optString("ReturnQty").isEmpty() && Double.parseDouble(object.optString("ReturnQty")) > 0) {
                                            salesListModel = new SalesOrderPrintPreviewModel.SalesList();
                                            salesListModel.setProductCode(object.optString("ProductCode"));
                                            salesListModel.setDescription(object.optString("ProductName"));
                                            salesListModel.setLqty(object.optString("LQty"));
                                            salesListModel.setCqty(object.optString("CQty"));
                                            salesListModel.setNetQty("-" + object.optString("ReturnQty"));

                                            double qty12 = Double.parseDouble(object.optString("ReturnQty"));
                                            double price12 = Double.parseDouble(object.optString("Price"));
                                            double nettotal12 = qty12 * price12;
                                            salesListModel.setTotal(String.valueOf(nettotal12));
                                            salesListModel.setPricevalue(String.valueOf(price12));

                                            salesListModel.setUomCode(object.optString("UOMCode"));
                                            salesListModel.setCartonPrice(object.optString("CartonPrice"));
                                            salesListModel.setUnitPrice(object.optString("Price"));
                                            salesListModel.setPcsperCarton(object.optString("PcsPerCarton"));
                                            salesListModel.setItemtax(object.optString("Tax"));
                                            salesListModel.setSubTotal(object.optString("SubTotal"));
                                            salesPrintList.add(salesListModel);
                                        }

                                    }
                                }
                            }
                            model.setSalesList(salesPrintList);
                            salesOrderHeaderDetails.add(model);
                        }
                        sentPrintDate(copy);
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
    }*/


    private void getAllUsers() throws JSONException {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Utils.getBaseUrl(this) + "UserList";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url_UserList:", url);
        usersList = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("User", userName);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> {
                    try {
                        Log.w("UserListResponse:", response.toString());
                        String statusCode = response.optString("statusCode");
                        String message = response.optString("statusMessage");
                        if (statusCode.equals("1")) {
                            JSONArray responseData = response.getJSONArray("responseData");
                            for (int i = 0; i < responseData.length(); i++) {
                                JSONObject object = responseData.optJSONObject(i);
                                UserListModel model = new UserListModel();
                                model.setUserName(object.optString("userName"));
                                model.setGender(object.optString("sex"));
                                model.setJobTitle(object.optString("jobTitle"));
                                usersList.add(model);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                        if (usersList.size() > 0) {
                            setUserListAdapter(usersList);
                        } else {
                            Toast.makeText(getApplicationContext(), "No User Found...", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, error -> {
            // Do something when error occurred
            pDialog.dismiss();
            Log.w("Error_throwing:", error.toString());
        }) {
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

    public void setUserListAdapter(ArrayList<UserListModel> usersList) {
        UserAdapter customAdapter = new UserAdapter(getApplicationContext(), usersList);
        salesManSpinner.setAdapter(customAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedUser = usersList.get(position).getUserName();
        Log.w("UserSelected:", selectedUser);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        selectedUser = "";
    }


    private void createSignature() {
        if (Utils.getSignature() != null && !Utils.getSignature().isEmpty()) {
            try {
                ImageUtil.saveStamp(this, Utils.getSignature(), "Signature");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE);
        printerType = sharedPreferences.getString("printer_type", "");
        printerMacId = sharedPreferences.getString("mac_address", "");
        super.onResume();
    }


    private void sentPrintDate(int copy) throws IOException {
        if (Utils.validatePrinterConfiguration(this, printerType, printerMacId)) {

            if (printerType.equals("TSC Printer")) {
                TSCPrinter printer = new TSCPrinter(OrderHistoryListActivity.this, printerMacId, "SalesOrder");
                printer.printSalesOrder(copy, salesOrderHeaderDetails, salesPrintList);
                printer.setOnCompletionListener(() -> {
                    Utils.setSignature("");
                    Toast.makeText(getApplicationContext(), "SalesOrder printed successfully!", Toast.LENGTH_SHORT).show();
                });
            } else if (printerType.equals("Zebra Printer")) {
                ZebraPrinterActivity zebraPrinterActivity = new ZebraPrinterActivity(OrderHistoryListActivity.this, printerMacId);
                zebraPrinterActivity.printSalesOrder(copy, salesOrderHeaderDetails, salesPrintList);
            }
        }
    }


    public static void filterSearch(String customerName, String invoiceStatus, String fromdate, String todate) {
        try {
            ArrayList<OrderHeader> filterdNames = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date from_date = null;
            Date to_date = null;
            try {
                from_date = sdf.parse(fromdate);
                to_date = sdf.parse(todate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            for (OrderHeader model : OrderHistoryCatalogAdapter.getSalesOrderList()) {
                Date compareDate = sdf.parse(model.getInvoiceDate());
                if (from_date.equals(to_date)) {
                    if (from_date.equals(compareDate)) {
                        if (!customerName.isEmpty()) {
                            if (model.getCustomerName().toLowerCase().contains(customerName.toLowerCase())) {
                                switch (invoiceStatus) {
                                    case "ALL":
                                        filterdNames.add(model);
                                        break;
                                    case "OPEN":
                                        if (model.getStatus().equals("0")) {
                                            filterdNames.add(model);
                                        }
                                        break;
                                    case "CLOSED":
                                        if (!model.getStatus().equals("0")) {
                                            filterdNames.add(model);
                                        }
                                        break;
                                }
                                orderHistoryCatalogAdapter.filterList(filterdNames);
                            }
                        } else {
                            switch (invoiceStatus) {
                                case "ALL":
                                    filterdNames.add(model);
                                    break;
                                case "OPEN":
                                    if (model.getStatus().equals("0")) {
                                        filterdNames.add(model);
                                    }
                                    break;
                                case "CLOSED":
                                    if (!model.getStatus().equals("0")) {
                                        filterdNames.add(model);
                                    }
                                    break;
                            }
                            orderHistoryCatalogAdapter.filterList(filterdNames);
                        }
                    }
                } else if (compareDate.compareTo(from_date) >= 0 && compareDate.compareTo(to_date) <= 0) {
                    System.out.println("Compare date occurs after from date");
                    if (!customerName.isEmpty()) {
                        if (model.getCustomerName().toLowerCase().contains(customerName.toLowerCase())) {
                            switch (invoiceStatus) {
                                case "ALL":
                                    filterdNames.add(model);
                                    break;
                                case "OPEN":
                                    if (model.getStatus().equals("0")) {
                                        filterdNames.add(model);
                                    }
                                    break;
                                case "CLOSED":
                                    if (!model.getStatus().equals("0")) {
                                        filterdNames.add(model);
                                    }
                                    break;
                            }
                            orderHistoryCatalogAdapter.filterList(filterdNames);
                        }
                    } else {
                        switch (invoiceStatus) {
                            case "ALL":
                                filterdNames.add(model);
                                break;
                            case "OPEN":
                                if (model.getStatus().equals("0")) {
                                    filterdNames.add(model);
                                }
                                break;
                            case "CLOSED":
                                if (!model.getStatus().equals("0")) {
                                    filterdNames.add(model);
                                }
                                break;
                        }
                        orderHistoryCatalogAdapter.filterList(filterdNames);
                    }
                }
                orderHistoryCatalogAdapter.filterList(filterdNames);
            }

            Log.w("FilteredSize:", filterdNames.size() + "");

            if (filterdNames.size() > 0) {
                orderHistoryView.setVisibility(View.VISIBLE);
                outstandingLayout.setVisibility(View.VISIBLE);
                emptyLayout.setVisibility(View.GONE);
                //  setNettotal(filterdNames);
                // invoiceAdapter.filterList(filterdNames);
            } else {
                orderHistoryView.setVisibility(View.GONE);
                outstandingLayout.setVisibility(View.GONE);
                emptyLayout.setVisibility(View.VISIBLE);
            }


        } catch (Exception ex) {
            Log.e("Error_in_filter", Objects.requireNonNull(ex.getMessage()));
        }
    }

    public void setNettotalFun(ArrayList<OrderHeader> salesOrderList) {
        double net_amount = 0.0;
        for (OrderHeader model : salesOrderList) {
            if (model.getNetTotal() != null && !model.getNetTotal().equals("null")) {
                net_amount = net_amount + Double.parseDouble(model.getNetTotal());
            }
        }
        netTotalText.setText("$ " + Utils.twoDecimalPoint(net_amount));
    }


    public void redirectActivity(String action, String customer_code, String customer_name, String salesorder_code,
                                 String order_no, String customerBill_Disc) {
        //  if (products.length()==dbHelper.numberOfRowsInInvoice()){
        Log.w("acttionSO", "" + action);
        Utils.setCustomerSession(OrderHistoryListActivity.this, customer_code);
        if (action.equals("Edit")) {
            Intent intent = new Intent(getApplicationContext(), CreateNewInvoiceActivity.class);
            intent.putExtra("customerName", customer_name);
            intent.putExtra("customerCode", customer_code);
            intent.putExtra("editSoNumber", salesorder_code);
            intent.putExtra("customerBillDisc", customerBill_Disc);
            intent.putExtra("orderNo", order_no);
            intent.putExtra("from", "SalesEdit");
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(getApplicationContext(), CreateNewInvoiceActivity.class);
            intent.putExtra("customerName", customer_name);
            intent.putExtra("customerCode", customer_code);
            intent.putExtra("editSoNumber", salesorder_code);
            intent.putExtra("orderNo", order_no);
            intent.putExtra("customerBillDisc", customerBill_Disc);
            Log.w("acttionSOdisc", "" + customerBill_Disc);
            intent.putExtra("from", "ConvertInvoice");
            startActivity(intent);
            finish();
        }
    }



    public void setShowHide() {
        if (orderHistoryList.size() > 0) {
            orderHistoryView.setVisibility(View.VISIBLE);
            outstandingLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        } else {
            orderHistoryView.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
            outstandingLayout.setVisibility(View.GONE);
        }

        if (getIntent() != null) {
            printSoNumber = getIntent().getStringExtra("printSoNumber");
            noOfCopy = getIntent().getStringExtra("noOfCopy");
            if (printSoNumber != null && !printSoNumber.isEmpty()) {
//                try {
//                    getSalesOrderDetails(printSoNumber, Integer.parseInt(noOfCopy));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        }
    }


    public void setFilterAdapeter() {
        orderHistoryView.setVisibility(View.VISIBLE);
        emptyLayout.setVisibility(View.GONE);
        outstandingLayout.setVisibility(View.VISIBLE);
        orderHistoryView.setHasFixedSize(true);
        orderHistoryView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        orderHistoryCatalogAdapter = new OrderHistoryCatalogAdapter(this, orderHistoryView, orderHistoryList,
                new OrderHistoryCatalogAdapter.CallBack() {
                    @Override
                    public void calculateNetTotal(ArrayList<OrderHeader> salesList) {
                        setNettotalFun(salesList);
                    }

                    @Override
                    public void showMoreOption(String salesorderId, String customerName, String status) {
                        customerLayout.setVisibility(View.GONE);
                        salesOrderOptionLayout.setVisibility(View.VISIBLE);
                        soNumber.setText(salesorderId);
                        soCustomerName.setText(customerName);
                        salesOrderStatus = status;
                        //viewCloseBottomSheet();
                        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                    }

                    @Override
                    public void syncNowCall(OrderHeader orderHeader, ArrayList<CartModel> cartModel) {
                        syncNow(orderHeader,cartModel);
                    }

//

                });
        orderHistoryView.setAdapter(orderHistoryCatalogAdapter);
        if (orderHistoryList.size() > 0) {
            setNettotalFun(orderHistoryList);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //   getMenuInflater().inflate(R.menu.sorting_menu, menu);

        //  MenuItem action_save = menu.findItem(R.id.action_filter);
        // action_save.setVisible(false);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {//finish();
            onBackPressed();

         /*   case R.id.action_remove:
                showRemoveAlert();
                break;*/
        } else if (item.getItemId() == R.id.action_customer_name) {
            Collections.sort(orderHistoryList, new Comparator<OrderHeader>() {
                public int compare(OrderHeader obj1, OrderHeader obj2) {
                    // ## Ascending order
                    return obj1.getCustomerName().compareToIgnoreCase(obj2.getCustomerName()); // To compare string values
                    // return Integer.valueOf(obj1.empId).compareTo(Integer.valueOf(obj2.empId)); // To compare integer values

                    // ## Descending order
                    // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                    // return Integer.valueOf(obj2.empId).compareTo(Integer.valueOf(obj1.empId)); // To compare integer values
                }
            });
            orderHistoryCatalogAdapter.notifyDataSetChanged();
        } else if (item.getItemId() == R.id.action_amount) {
            Collections.sort(orderHistoryList, new Comparator<OrderHeader>() {
                public int compare(OrderHeader obj1, OrderHeader obj2) {
                    // ## Ascending order
                    //  return obj1.getNetTotal().compareToIgnoreCase(obj2.getNetTotal()); // To compare string values
                    return Double.valueOf(obj1.getNetTotal()).compareTo(Double.valueOf(obj2.getNetTotal())); // To compare integer values

                    // ## Descending order
                    // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                    // return Integer.valueOf(obj2.empId).compareTo(Integer.valueOf(obj1.empId)); // To compare integer values
                }
            });
            orderHistoryCatalogAdapter.notifyDataSetChanged();
        } else if (item.getItemId() == R.id.action_date) {

            try {
                Collections.sort(orderHistoryList, new Comparator<OrderHeader>() {
                    public int compare(OrderHeader obj1, OrderHeader obj2) {
                        SimpleDateFormat sdfo = new SimpleDateFormat("yyyy-MM-dd");
                        // Get the two dates to be compared
                        Date d1 = null;
                        Date d2 = null;
                        try {
                            d1 = sdfo.parse(obj1.getInvoiceDate());
                            d2 = sdfo.parse(obj2.getInvoiceDate());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        // ## Ascending order
                        //  return obj1.getNetTotal().compareToIgnoreCase(obj2.getNetTotal()); // To compare string values
                        return d1.compareTo(d2); // To compare integer values

                        // ## Descending order
                        // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                        // return Integer.valueOf(obj2.empId).compareTo(Integer.valueOf(obj1.empId)); // To compare integer values
                    }
                });
                orderHistoryCatalogAdapter.notifyDataSetChanged();

            } catch (Exception ex) {
                Log.w("Error:", ex.getMessage());
            }
        } else if (item.getItemId() == R.id.action_add) {

            Intent intent = new Intent(getApplicationContext(), CustomerListActivity.class);
            intent.putExtra("from", "so");
            startActivityForResult(intent, customerSelectCode);


        } else if (item.getItemId() == R.id.action_barcode) {
            Intent intent = new Intent(getApplicationContext(), BarCodeScanner.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.action_filter) {
            if (searchFilterView.getVisibility() == View.VISIBLE) {
                searchFilterView.setVisibility(View.GONE);
                customerNameText.setText("");
                isSearchCustomerNameClicked = false;
                if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                //slideUp(searchFilterView);
            } else {
                customerNameText.setText("");
                isSearchCustomerNameClicked = false;
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
                Intent intent = new Intent(OrderHistoryListActivity.this, AddInvoiceActivityOld.class);
                intent.putExtra("customerId", result);
                intent.putExtra("activityFrom", "SalesOrder");
                startActivity(intent);
                // finish();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        } else if (requestCode == FILTER_CUSTOMER_CODE && resultCode == Activity.RESULT_OK) {
            selectedCustomerId = data.getStringExtra("customerCode");
            String selectCustomerName = data.getStringExtra("customerName");
            customerNameText.setText(selectCustomerName);
        }
    } //onActivityResult

    private void showCustomerDialog(Activity activity, String customer_name, String customer_code, String desc) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.cutom_alert_dialog, viewGroup, false);

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        TextView customerName = dialogView.findViewById(R.id.customer_name_value);
        TextView description = dialogView.findViewById(R.id.description);

        customerName.setText(customer_name + " - " + customer_code);
        description.setText("Do you want to continue this customer ?");

        Button yesButton = dialogView.findViewById(R.id.buttonYes);
        Button noButton = dialogView.findViewById(R.id.buttonNo);

        //finally creating the alert dialog and displaying it
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        alertDialog.setCancelable(false);
        alertDialog.show();
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                dbHelper.removeCustomer();
                dbHelper.removeAllItems();
                AddInvoiceActivityOld.customerId = customer_code;
                setCustomerDetails(customer_code);
                selectedCustomerId = customer_code;
                redirectInvoice = false;
                Intent intent = new Intent(OrderHistoryListActivity.this, AddInvoiceActivityOld.class);
                intent.putExtra("customerId", customer_code);
                intent.putExtra("activityFrom", "SalesOrder");
                startActivity(intent);
                finish();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                customerLayout.setVisibility(View.VISIBLE);
                salesOrderOptionLayout.setVisibility(View.GONE);
                searchFilterView.setVisibility(View.GONE);
                //  viewCloseBottomSheet();
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
    }


    private void setAdapter(ArrayList<CustomerModel> customerNames) {
        customerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        customerNameAdapter = new SelectCustomerAdapter(this, customerNames, new SelectCustomerAdapter.CallBack() {
            @Override
            public void searchCustomer(String customer, String customername, int pos) {
                customerLayout.setVisibility(View.VISIBLE);
                if (isSearchCustomerNameClicked) {
                    viewCloseBottomSheet();
                    //searchFilterView.setVisibility(View.GONE);
                    setCustomerDetails(customer);
                    customerNameText.setText(customername);
                    selectedCustomerId = customer;
                    redirectInvoice = false;
                } else if (addnewCustomer) {
                    int count = dbHelper.numberOfRows();
                    if (count > 0) {
                        showProductDeleteAlert(customer);
                    } else {
                        viewCloseBottomSheet();
                        dbHelper.removeAllItems();
                        addnewCustomer = false;
                        setCustomerDetails(customer);
                        selectedCustomerId = customer;
                        redirectInvoice = false;
                        //Intent intent = new Intent(SalesOrderListActivity.this, AddInvoiceActivity.class);
                        //  intent.putExtra("customerId", customer);
                        //  intent.putExtra("activityFrom", "SalesOrder");
                        //  startActivity(intent);
                        //   finish();
                    }
                }
                Log.w("Customer_id:", customer);
            }
        });
        customerView.setAdapter(customerNameAdapter);
    }
    public void getCurrentLocation() {
        locationTrack = new LocationTrack(OrderHistoryListActivity.this);
        if (locationTrack.canGetLocation()) {
            double longitude = locationTrack.getLongitude();
            double latitude = locationTrack.getLatitude();
            current_latitude = String.valueOf(latitude);
            current_longitude = String.valueOf(longitude);
            String currentAddress = Utils.getCompleteAddress(OrderHistoryListActivity.this, latitude, longitude);
            if (currentAddress != null && !currentAddress.isEmpty()) {
                locationText.setText(currentAddress);
                current_addr = currentAddress ;
            }
        } else {
            // locationTrack.showSettingsAlert();
        }
    }

    public void closeView() {
        if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

    }

    public void showProductDeleteAlert(String customerId) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Warning !");
        builder1.setMessage("Products in Cart will be removed..");
        builder1.setCancelable(false);
        builder1.setPositiveButton(
                "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        dbHelper.removeAllItems();
                        addnewCustomer = false;
                        setCustomerDetails(customerId);
                        viewCloseBottomSheet();
                        selectedCustomerId = customerId;
                        redirectInvoice = false;
                        // Intent intent=new Intent(SalesOrderListActivity.this,AddInvoiceActivity.class);
                        // intent.putExtra("customerId",customerId);
                        // intent.putExtra("activityFrom","SalesOrder");
                        // startActivity(intent);
                        // finish();
                    }
                });
        builder1.setNegativeButton(
                "CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


    private void filter(String text) {
        try {
            //new array list that will hold the filtered data
            ArrayList<CustomerModel> filterdNames = new ArrayList<>();
            //looping through existing elements
            for (CustomerModel s : customerList) {
                //if the existing elements contains the search input
                if (s.getCustomerName().toLowerCase().contains(text.toLowerCase()) || s.getCustomerCode().toLowerCase().contains(text.toLowerCase())) {
                    //adding the element to filtered list
                    filterdNames.add(s);
                }
            }
            //calling a method of the adapter class and passing the filtered list
            customerNameAdapter.filterList(filterdNames);

        } catch (Exception ex) {
            Log.e("Error_in_filter", Objects.requireNonNull(ex.getMessage()));
        }
    }


    public void viewCloseBottomSheet() {
        hideKeyboard();
        if (isSearchCustomerNameClicked || addnewCustomer) {
            customerLayout.setVisibility(View.VISIBLE);
            salesOrderOptionLayout.setVisibility(View.GONE);
            redirectInvoice = false;
        } else {
            customerLayout.setVisibility(View.GONE);
            salesOrderOptionLayout.setVisibility(View.VISIBLE);
        }
        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        customerList = dbHelper.getAllCustomers();
        setAdapter(customerList);
        // get the Customer name from the local db
    }

    public void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void getDate(EditText dateEditext) {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(OrderHistoryListActivity.this,
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