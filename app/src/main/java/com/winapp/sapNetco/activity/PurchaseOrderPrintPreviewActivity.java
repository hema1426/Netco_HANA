package com.winapp.sapNetco.activity;

import static com.winapp.sapNetco.activity.SalesOrderListActivity.shortCodeStr;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.winapp.sapNetco.BuildConfig;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tscdll.TSCActivity;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.winapp.sapNetco.R;
import com.winapp.sapNetco.adapter.PurchaseOrderPrintPreviewAdapter;
import com.winapp.sapNetco.model.SalesOrderPrintPreviewModel;
import com.winapp.sapNetco.tscprinter.TSCPrinterActivity;
import com.winapp.sapNetco.utils.Constants;
import com.winapp.sapNetco.utils.ImageUtil;
import com.winapp.sapNetco.utils.SessionManager;
import com.winapp.sapNetco.utils.Utils;
import com.winapp.sapNetco.zebraprinter.TSCPrinter;
import com.winapp.sapNetco.zebraprinter.ZebraPrinterActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PurchaseOrderPrintPreviewActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener {

    private String companyId;
    private SweetAlertDialog pDialog;
    private String soNumber;
    private SessionManager session;
    private HashMap<String ,String> user;
    private ArrayList<SalesOrderPrintPreviewModel> salesOrderHeaderDetails;
    private ArrayList<SalesOrderPrintPreviewModel.SalesList> salesOrderList;
    private RecyclerView salesListView;
    private PurchaseOrderPrintPreviewAdapter adapter;
    private TextView soNumberText;
    private TextView soDateText;
    private TextView customerCodetext;
    private TextView customerNameText;
    private TextView addressText;
    private TextView deliveryAddressText;
    private TextView billDiscountText;
    private TextView subtotalText;
    private TextView taxValueText;
    private TextView netTotalText;
    private TextView outstandingText;
    private TextView taxTitle;
    private TextView itemDiscount;
    private TextView companyNametext;
    private TextView companyAddress1Text;
    private TextView companyAddress2Text,referNo;
    private String company_name;
    private String company_address1;
    private String company_address2;
    private String company_address3;
    private RelativeLayout rootLayout;
    private LinearLayout addressLayout;
    private SharedPreferences sharedPreferences;
    private String printerMacId;
    private String printerType;
    private TSCActivity TscDll;
    private String locationCode;
    private DialogInterface alertInterface;
    private AlertDialog alert11;
    boolean boolean_permission;
    ProgressDialog progressDialog;
    Bitmap bitmap;
    boolean boolean_save;
    String pdfFileName;
    static BottomSheetBehavior behavior;
    LinearLayout shareLayout;
    File pdfFile;
    PDFView pdfView;
    Integer pageNumber = 0;
    LinearLayout printLayout;
    Button cancelButton;
    private static final int PERMISSION_REQUEST_CODE = 100;

    LinearLayout address1Layout;
    LinearLayout address2Layout;
    LinearLayout address3Layout;
    LinearLayout address4Layout , reference_layl;

    TextView customerAddress1;
    TextView customerAddress2;
    TextView customerAddress3;
    TextView customerAddress4;
    private ProgressDialog progressDg;

    private TextView companyAddress3Text;
    private TextView companyPhoneText;
    private TextView companyGstText;
    String company_phone="";
    String company_gst="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        setContentView(R.layout.activity_purchase_order_print_preview);
        setTitle();
        TscDll = new TSCActivity();
        session=new SessionManager(this);
        user=session.getUserDetails();
        Log.w("activity_cg",getClass().getSimpleName().toString());

        companyId=user.get(SessionManager.KEY_COMPANY_CODE);
        company_name=user.get(SessionManager.KEY_COMPANY_NAME);
        company_address1=user.get(SessionManager.KEY_ADDRESS1);
        company_address2=user.get(SessionManager.KEY_ADDRESS2);
        company_address3=user.get(SessionManager.KEY_ADDRESS3);
        locationCode=user.get((SessionManager.KEY_LOCATION_CODE));
        company_phone=user.get(SessionManager.KEY_PHONE_NO);
        company_gst=user.get(SessionManager.KEY_COMPANY_REG_NO);

        salesListView =findViewById(R.id.salesList);
        soNumberText =findViewById(R.id.so_no);
        soDateText =findViewById(R.id.so_date);
        customerCodetext=findViewById(R.id.customer_code);
        customerNameText=findViewById(R.id.customer_name_value);
        deliveryAddressText=findViewById(R.id.delivery_address);
        billDiscountText=findViewById(R.id.bill_disc);
        subtotalText=findViewById(R.id.balance_value);
        taxValueText=findViewById(R.id.tax);
        netTotalText=findViewById(R.id.net_total);
        outstandingText=findViewById(R.id.outstanding_amount);
        taxTitle=findViewById(R.id.tax_title);
        itemDiscount=findViewById(R.id.item_disc);
        companyNametext=findViewById(R.id.company_name);
        companyAddress1Text=findViewById(R.id.address1);
        companyAddress2Text=findViewById(R.id.address2);
        addressLayout=findViewById(R.id.adressLayout);
        referNo=findViewById(R.id.reference_no_po_prev);
        reference_layl = findViewById(R.id.reference_lay_po);
        rootLayout=findViewById(R.id.rootLayout);
        pdfView= (PDFView)findViewById(R.id.pdfView);

        companyAddress3Text=findViewById(R.id.address3);
        companyGstText=findViewById(R.id.gst_no);
        companyPhoneText=findViewById(R.id.mobile_no);

        address1Layout=findViewById(R.id.address1Layout);
        address2Layout=findViewById(R.id.address2Layout);
        address3Layout=findViewById(R.id.address3Layout);
        address4Layout=findViewById(R.id.address4Layout);

        customerAddress1=findViewById(R.id.cus_address1);
        customerAddress2=findViewById(R.id.cus_address2);
        customerAddress3=findViewById(R.id.cus_address3);
        customerAddress4=findViewById(R.id.cus_address4);

        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE);
        printerType=sharedPreferences.getString("printer_type","");
        printerMacId=sharedPreferences.getString("mac_address","");

        progressDg = new ProgressDialog(PurchaseOrderPrintPreviewActivity.this);
        progressDg.setMessage("Downloading Product Image, please wait ...");
        progressDg.setIndeterminate(true);
        progressDg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDg.setCancelable(false);
        progressDg.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        progressDg.setProgressNumberFormat("%1d KB/%2d KB");

        shareLayout=findViewById(R.id.share_layout);
        printLayout=findViewById(R.id.print_layout);
        cancelButton=findViewById(R.id.cancel);

        View bottomSheet = findViewById(R.id.design_bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);

        Log.w("Printer_Mac_Id:",printerMacId);
        Log.w("Printer_Type:",printerType);

        checkPermission();

        if (getIntent() != null){
            soNumber = getIntent().getStringExtra("soNumber");
            if (soNumber !=null){
                try {
                    getSalesOrderDetails(soNumber);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                sharePdfView(pdfFile);
            }
        });

        printLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                printPreview();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
    }

    public void printPreview(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(getApplicationContext(), "This device does not support bluetooth", Toast.LENGTH_SHORT).show();
        } else if (!mBluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled :)
            Toast.makeText(getApplicationContext(), "Enable bluetooth and connect the printer", Toast.LENGTH_SHORT).show();
        } else {
            // Bluetooth is enabled
            if (!printerType.isEmpty()) {
                showPrintAlert();
            } else {
                Toast.makeText(getApplicationContext(), "Please configure Printer", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void sharePdfView(File pdfFile){
        //  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        //   pdfUri = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", pdfFile);
        //  } else {
        //   pdfUri = Uri.fromFile(pdfFile);
        // }
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.setType("application/pdf");
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(pdfFile));
        startActivity(Intent.createChooser(share, "Share"));
    }


    private void getSalesOrderDetails(String soNumber) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("PurchaseOrderNo", soNumber);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url= Utils.getBaseUrl(this) +"PurchaseOrderDetails";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url_po_Pr:",url+jsonObject.toString());
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Generating Print Preview...");
        pDialog.setCancelable(false);
        pDialog.show();
        salesOrderHeaderDetails =new ArrayList<>();
        salesOrderList =new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonObject,
                response -> {
                    try{

                     Log.w("po_Details:",response.toString());
                        String statusCode=response.optString("statusCode");
                        if (statusCode.equals("1")){
                            JSONArray responseData=response.getJSONArray("responseData");
                            JSONObject object=responseData.optJSONObject(0);

                            SalesOrderPrintPreviewModel model=new SalesOrderPrintPreviewModel();
                            model.setSoNumber(object.optString("poNo"));
                            model.setSoDate(object.optString("poDate"));
                            model.setCustomerCode(object.optString("vendorCode"));
                            model.setCustomerName(object.optString("vendorName"));
                            model.setAddress(object.optString("address1") + object.optString("address2") + object.optString("address3"));
                            model.setDeliveryAddress(model.getAddress());
                            model.setSubTotal(object.optString("subTotal"));
                            model.setNetTax(object.optString("taxTotal"));
                            model.setNetTotal(object.optString("netTotal"));
                            model.setTaxType(object.optString("taxType"));
                            model.setTaxValue(object.optString("taxPerc"));
                            model.setOutStandingAmount(object.optString("outstandingAmount"));
                            Utils.setInvoiceMode("SalesOrder");
                            model.setBillDiscount(object.optString("billDiscount"));
                            model.setItemDiscount(object.optString("totalDiscount"));
                            model.setReferenceNo(object.optString("customerRefNo"));

                            model.setAddress1(object.optString("address1"));
                            model.setAddress2(object.optString("address2"));
                            model.setAddress3(object.optString("address3"));
                            model.setAddressstate(object.optString("block")+" "+object.optString("street")+" "
                                    +object.optString("city"));
                            model.setAddresssZipcode(object.optString("countryName")+" "+object.optString("state")+" "
                                    +object.optString("zipcode"));

                            String signFlag=object.optString("signFlag");
                            if (signFlag.equals("Y")){
                                String signature=object.optString("signature");
                                Utils.setSignature(signature);
                                createSignature();
                            }else {
                                Utils.setSignature("");
                            }

                            //    deliveryAddressText.setText(model.getAddress());

                            JSONArray detailsArray=object.optJSONArray("purchaseOrderDetails");
                            for (int i=0;i<detailsArray.length();i++){
                                JSONObject detailObject=detailsArray.optJSONObject(i);
                                    SalesOrderPrintPreviewModel.SalesList salesListModel =new SalesOrderPrintPreviewModel.SalesList();
                                    salesListModel.setProductCode(detailObject.optString("productCode"));
                                    salesListModel.setDescription( detailObject.optString("productName"));
                                    salesListModel.setLqty(detailObject.optString("unitQty"));
                                    salesListModel.setCqty(detailObject.optString("cartonQty"));
                                    salesListModel.setNetQty(detailObject.optString("quantity"));
                                    salesListModel.setCartonPrice(detailObject.optString("cartonPrice"));
                                    salesListModel.setUnitPrice(detailObject.optString("price"));
                                    double qty=Double.parseDouble(detailObject.optString("quantity"));
                                    double price=Double.parseDouble(detailObject.optString("price"));

                                    double nettotal=qty * price;
                                    salesListModel.setTotal(String.valueOf(nettotal));
                                    salesListModel.setPricevalue(String.valueOf(price));

                                    salesListModel.setPcsperCarton(detailObject.optString("pcsPerCarton"));
                                    salesListModel.setItemtax(detailObject.optString("totalTax"));
                                    salesListModel.setSubTotal(detailObject.optString("subTotal"));
                                    salesOrderList.add(salesListModel);

                                model.setSalesList(salesOrderList);
                                salesOrderHeaderDetails.add(model);
                            }
                            if (salesOrderList.size()>0){
                                setSalesOrderAdapter();
                            }else {
                                Log.w("purchaseListSize:",salesOrderList.size()+"");
                            }
                            pDialog.dismiss();
                        }else {

                        }
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

    private void createSignature(){
        if (Utils.getSignature()!=null && !Utils.getSignature().isEmpty()){
            try {
                ImageUtil.saveStamp(this,Utils.getSignature(),"Signature");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setSalesOrderAdapter(){
        for (SalesOrderPrintPreviewModel model: salesOrderHeaderDetails){
            soNumberText.setText(model.getSoNumber());
            soDateText.setText(model.getSoDate());
            customerCodetext.setText(model.getCustomerCode());
            customerNameText.setText(model.getCustomerName());

            if(model.getReferenceNo() != null && !model.getReferenceNo().equals("NA")
            ) {
                reference_layl.setVisibility(View.VISIBLE);
                referNo.setText(model.getReferenceNo());
            }else{
                reference_layl.setVisibility(View.GONE);
            }

            if (!model.getAddress1().isEmpty()){
                address1Layout.setVisibility(View.VISIBLE);
                customerAddress1.setText(model.getAddress1());
            }
            if (!model.getAddress2().isEmpty()){
                address2Layout.setVisibility(View.VISIBLE);
                customerAddress2.setText(model.getAddress2());
            }
            if (!model.getAddress3().isEmpty()){
                address3Layout.setVisibility(View.VISIBLE);
                customerAddress3.setText(model.getAddress3());
            }
            if (!model.getAddressstate().isEmpty() ) {
                if (!model.getAddresssZipcode().isEmpty()) {
                    address4Layout.setVisibility(View.VISIBLE);
                    customerAddress4.setText(model.getAddressstate() + " " + model.getAddresssZipcode());
                } else {
                    address4Layout.setVisibility(View.VISIBLE);
                    customerAddress4.setText(model.getAddressstate());
                }
            }
            else{
                if (!model.getAddresssZipcode().isEmpty() ) {
                    address4Layout.setVisibility(View.VISIBLE);
                    customerAddress4.setText(model.getAddresssZipcode());
                }
            }

           /* if (!model.getAddress1().isEmpty()){
                address1Layout.setVisibility(View.VISIBLE);
                customerAddress1.setText(model.getAddress1());
            }
            if (!model.getAddress2().isEmpty()){
                address2Layout.setVisibility(View.VISIBLE);
                customerAddress2.setText(model.getAddress2());
            }
            if (!model.getAddress3().isEmpty()){
                address3Layout.setVisibility(View.VISIBLE);
                customerAddress3.setText(model.getAddress3());
            }*/
          /*  if (!model.getDeliveryAddress().isEmpty()) {
                addressLayout.setVisibility(View.VISIBLE);
                deliveryAddressText.setText(model.getDeliveryAddress());
            }*/


            if (!model.getDeliveryAddress().isEmpty()){
                addressLayout.setVisibility(View.VISIBLE);
                deliveryAddressText.setText(model.getDeliveryAddress());
            }
            if (model.getBillDiscount()!=null && !model.getBillDiscount().isEmpty() && !model.getBillDiscount().equals("null")){
                billDiscountText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getBillDiscount())));
            }else {
                billDiscountText.setText("0.00");
            }
            if(shortCodeStr.equalsIgnoreCase("FUXIN")) {
                taxTitle.setText("GST ( " + model.getTaxType() + " : " + Utils.fourDecimalPoint(Double.parseDouble(model.getTaxValue())) + " % ) ");
                if (model.getTaxType().equals("I")) {
                    double sub_total = Double.parseDouble(model.getNetTotal()) - Double.parseDouble(model.getNetTax());
                    subtotalText.setText(Utils.fourDecimalPoint(sub_total));
                } else {
                    subtotalText.setText(Utils.fourDecimalPoint(Double.parseDouble(model.getSubTotal())));
                }
                taxValueText.setText(Utils.fourDecimalPoint(Double.parseDouble(model.getNetTax())));
                netTotalText.setText(Utils.fourDecimalPoint(Double.parseDouble(model.getNetTotal())));
                outstandingText.setText(Utils.fourDecimalPoint(Double.parseDouble(model.getOutStandingAmount())));
                itemDiscount.setText(Utils.fourDecimalPoint(Double.parseDouble(model.getItemDiscount())));
            }else {

                taxTitle.setText("GST ( " + model.getTaxType() + " : " + Utils.twoDecimalPoint(Double.parseDouble(model.getTaxValue())) + " % ) ");
                if (model.getTaxType().equals("I")) {
                    double sub_total = Double.parseDouble(model.getNetTotal()) - Double.parseDouble(model.getNetTax());
                    subtotalText.setText(Utils.twoDecimalPoint(sub_total));
                } else {
                    subtotalText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getSubTotal())));
                }
                taxValueText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getNetTax())));
                netTotalText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getNetTotal())));
                outstandingText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getOutStandingAmount())));
                itemDiscount.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getItemDiscount())));
            }
        }

        companyNametext.setText(company_name);
        if (!company_address1.isEmpty()){
            companyAddress1Text.setVisibility(View.VISIBLE);
            companyAddress1Text.setText(company_address1);
        }

        if (!company_address2.isEmpty()){
            companyAddress2Text.setVisibility(View.VISIBLE);
            companyAddress2Text.setText(company_address2);
        }

        if (!company_address3.isEmpty()){
            companyAddress3Text.setVisibility(View.VISIBLE);
            companyAddress3Text.setText(company_address3);
        }

        if (!company_phone.isEmpty()){
            companyPhoneText.setText("TEL : "+company_phone);
            companyPhoneText.setVisibility(View.VISIBLE);
        }
        if (!company_gst.isEmpty()){
            companyGstText.setText("CO REG NO : "+company_gst);
            companyGstText.setVisibility(View.VISIBLE);
        }
        salesListView.setHasFixedSize(true);
        // RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        salesListView.setLayoutManager(new LinearLayoutManager(PurchaseOrderPrintPreviewActivity.this, LinearLayoutManager.VERTICAL, false));
        adapter = new PurchaseOrderPrintPreviewAdapter(PurchaseOrderPrintPreviewActivity.this, salesOrderList);
        salesListView.setAdapter(adapter);
        rootLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.print_whatap_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_print) {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                // Device does not support Bluetooth
                Toast.makeText(getApplicationContext(),"This device does not support bluetooth",Toast.LENGTH_SHORT).show();
            } else if (!mBluetoothAdapter.isEnabled()) {
                // Bluetooth is not enabled :)
                Toast.makeText(getApplicationContext(),"Enable bluetooth and connect the printer",Toast.LENGTH_SHORT).show();
            } else {
                // Bluetooth is enabled
                if (!printerType.isEmpty()){
                    showPrintAlert();
                }else {
                    Toast.makeText(getApplicationContext(),"Please configure Printer",Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        }
        else if (id==android.R.id.home){
            finish();
        }
        else if (id==R.id.action_whatapp){
            getPdfApi("");
        }
        else if (id == R.id.action_pdf) {
            if (boolean_permission) {
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Please wait");
                bitmap = Utils.loadBitmapFromView(rootLayout, rootLayout.getWidth(), rootLayout.getHeight());
                createPdf();
            } else {
                Toast.makeText(getApplicationContext(),"Permission required to share content",Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void getPdfApi(String invoiceno) {

        try {
            // Initialize a new RequestQueue instance
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            // Initialize a new JsonArrayRequest instance
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("InvoiceNo", invoiceno);

            String url = Utils.getBaseUrl(this) + "DownloadPDFInvoice";

            Log.w("Given_url_PdfDownload:", url + "/" + jsonObject.toString());
            pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Generating Pdf...");
            pDialog.setCancelable(false);
            pDialog.show();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonObject,
                    response -> {
                        try {
                            pDialog.dismiss();
                            Log.w("InvoicePdfResponse:", response.toString());
                            //    {"statusCode":1,"statusMessage":"Success",
                            //    "responseData":{"pdfURL":"http:\/\/172.16.5.60:8349\/PDF\/InvoiceNo_15031.pdf"}}
                            if (response.length() > 0) {
                                String statusCode = response.optString("statusCode");
                                if (statusCode.equals("1")) {
                                    JSONObject object = response.optJSONObject("responseData");
                                    String pdfUrl = object.optString("pdfURL");
                                   // shareMode = mode;
                                    new pdfDownload(this).execute(pdfUrl, "invoice", invoiceno);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error in Getting report..", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, error -> {
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
        } catch (Exception ex) {
        }
    }

    private class pdfDownload extends AsyncTask<String, Integer, String> {

        private Context c;
        private int file_progress_count = 0;
        File newFile;

        public pdfDownload(Context c) {
            this.c = c;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream is = null;
            OutputStream os = null;
            HttpURLConnection con = null;
            int length;
            try {
                URL url = new URL(sUrl[0]);
                con = (HttpURLConnection) url.openConnection();
                con.connect();

                if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "HTTP CODE: " + con.getResponseCode() + " " + con.getResponseMessage();
                }

                length = con.getContentLength();
                progressDg.setMax(length / (1000));
                is = con.getInputStream();

                Log.w("DownloadImageURL:", url.toString());

                //String folderPath = Environment.getExternalStorageDirectory() + "/CatalogErp/Products";
                File folder = new File(Constants.getFolderPath(PurchaseOrderPrintPreviewActivity.this));
                if (!folder.exists()) {
                    File productsDirectory = new File(Constants.getFolderPath(PurchaseOrderPrintPreviewActivity.this));
                    productsDirectory.mkdirs();
                }

                //create a new file
                String filepath = sUrl[1] + "_" + sUrl[2];
                String newfilePath = filepath.replace("/", "_");
                newFile = new File(Constants.getFolderPath(PurchaseOrderPrintPreviewActivity.this), newfilePath + ".pdf");
                if (newFile.exists()) {
                    newFile.delete();
                }
                Log.w("GivenFilePath:", newFile.toString());
                newFile.createNewFile();
                //os = new FileOutputStream(Environment.getExternalStorageDirectory()+File.separator+"CatalogImages" + File.separator + "a-computer-engineer.jpg");
                os = new FileOutputStream(newFile);
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = is.read(data)) != -1) {
                    if (isCancelled()) {
                        is.close();
                        return null;
                    }
                    total += count;
                    if (length > 0) {
                        publishProgress((int) total);
                    }
                    this.file_progress_count = (int) ((100 * total) / ((long) length));
                    os.write(data, 0, count);
                }
            } catch (Exception e) {
                Log.w("File_Write_Error:", e.getMessage());
                return e.toString();
            } finally {
                try {
                    if (os != null)
                        os.close();
                    if (is != null)
                        is.close();
                } catch (IOException ioe) {
                }
                if (con != null)
                    con.disconnect();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDg.setMessage("Downloading Pdf..");
            progressDg.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            progressDg.setIndeterminate(false);
            progressDg.setProgress(progress[0] / 1000);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                Log.w("ProgressCount:", file_progress_count + "");
                if (file_progress_count == 100) {
                    progressDg.dismiss();
                    if (newFile.exists()) {
//                        if (shareMode.equals("Share")) {
//                            pdfFile = newFile;
//                            displayFromAsset(newFile);
//                        } else {
                            shareWhatsapp(newFile);
                    //    }
                    } else {
                        Toast.makeText(getApplicationContext(), "NO file Download", Toast.LENGTH_SHORT).show();
                    }
                }
                if (result != null) {

                }
            } catch (Exception exception) {
            }
        }
    }


    public void shareWhatsapp(File file) {
        Intent shareIntent = new Intent();
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setAction(Intent.ACTION_SEND);
        //without the below line intent will show error
        shareIntent.setType("application/pdf");
        shareIntent.putExtra(android.content.Intent.EXTRA_STREAM,
                FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                        BuildConfig.APPLICATION_ID + ".provider", file));
        // Target whatsapp:
        shareIntent.setPackage("com.whatsapp");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(shareIntent);
        } catch (android.content.ActivityNotFoundException ex) {
           // shareWhatsappBusiness(file);
            //Toast.makeText(NewInvoiceListActivity.this, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            boolean_permission=true;
            return true;
        } else {
            boolean_permission=false;
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    private void createPdf(){
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels ;
        float width = displaymetrics.widthPixels ;

        int convertHighet = (int) hight, convertWidth = (int) width;

//        Resources mResources = getResources();
//        Bitmap bitmap = BitmapFactory.decodeResource(mResources, R.drawable.screenshot);

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHighet, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();


        Paint paint = new Paint();
        canvas.drawPaint(paint);


        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHighet, true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0 , null);
        document.finishPage(page);


        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/PdfTett/";
        File dir = new File(path);
        if (!dir.exists())
            dir.mkdirs();
        File filePath = new File(dir,soNumber+".pdf");
        try {
            document.writeTo(new FileOutputStream(filePath));
            // btn_generate.setText("Check PDF");
            pdfFile=filePath;
            displayFromAsset(filePath);
            // viewPdfFile(filePath);
            //  openPDFFiles(filePath.toString());
            boolean_save=true;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        // close the document
        document.close();
    }

    private void displayFromAsset(File assetFileName) {
        Log.w("PrintPreViewPath:",assetFileName.toString());
        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        //  pdfFileName = assetFileName;
        //   Log.w(getFilesDir()+assetFileName,"FilePath");
        pdfView.fromFile(assetFileName)
                .defaultPage(pageNumber)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
    }


    @Override
    public void loadComplete(int nbPages) {
        // PdfDocument.Meta meta = pdfView.getDocumentMeta();
        // printBookmarksTree(pdfView.getTableOfContents(), "-");

    }

    public void showPrintAlert(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(PurchaseOrderPrintPreviewActivity.this);
        builder1.setMessage("Do you want to print this Purchase Order ?.");
        builder1.setCancelable(false);
        builder1.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                alertInterface=dialog;
                if (printerType.equals("TSC Printer")){
                    dialog.dismiss();
                  //  printInvoice();
                    // TSCPrinter tscPrinter=new TSCPrinter(SalesOrderPrintPreview.this,printerMacId);
                    //  tscPrinter.printInvoice(invoiceHeaderDetails,invoiceList);
                    TSCPrinter printer=new TSCPrinter(PurchaseOrderPrintPreviewActivity.this,printerMacId,"SalesOrder");
                    try {
                        printer.printPurchaseInvoice(1,salesOrderHeaderDetails,salesOrderList);
                        printer.setOnCompletionListener(new TSCPrinter.OnCompletionListener() {
                            @Override
                            public void onCompleted() {
                                Utils.setSignature("");
                                Toast.makeText(getApplicationContext(),"SalesOrder printed successfully!",Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if (printerType.equals("Zebra Printer")){
                    ZebraPrinterActivity zebraPrinterActivity=new ZebraPrinterActivity(PurchaseOrderPrintPreviewActivity.this,printerMacId);
                    try {
                        zebraPrinterActivity.printSalesOrder(1,salesOrderHeaderDetails,salesOrderList);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    try {
                        final TSCPrinterActivity print = new TSCPrinterActivity(PurchaseOrderPrintPreviewActivity.this, printerMacId, printerType);
                        print.initGenericPrinter();
                        print.setInitCompletionListener(() -> {
                            print.printSalesOrder(1, salesOrderHeaderDetails, salesOrderList);
                            print.setOnCompletedListener(() -> {
                                //helper.showLongToast(R.string.printed_successfully);
                                Toast.makeText(getApplicationContext(), "Printed Successfully..!", Toast.LENGTH_SHORT).show();
                            });
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        builder1.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        alert11 = builder1.create();
        alert11.show();
    }

    public void setTitle(){
        //Customize the ActionBar
        final ActionBar abar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.action_bar_title, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("Purchase Order");
        Objects.requireNonNull(abar).setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
        abar.setDisplayHomeAsUpEnabled(true);
        abar.setHomeButtonEnabled(true);
    }

}