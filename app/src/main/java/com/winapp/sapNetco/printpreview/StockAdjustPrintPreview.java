package com.winapp.sapNetco.printpreview;

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

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.winapp.sapNetco.adapter.StockAdjustPrintPreviewAdapter;
import com.winapp.sapNetco.model.StockAdjustmentPreviewModel;
import com.winapp.sapNetco.utils.Constants;
import com.winapp.sapNetco.utils.ImageUtil;
import com.winapp.sapNetco.utils.SessionManager;
import com.winapp.sapNetco.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class StockAdjustPrintPreview extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener {

    private String companyId;
    private SweetAlertDialog pDialog;
    private String adjustNumber;
    private SessionManager session;
    private HashMap<String ,String> user;
    private ArrayList<StockAdjustmentPreviewModel> stockadjustHeaderDetails;
    private ArrayList<StockAdjustmentPreviewModel.StockAdjustList> stockadjustList;
    private RecyclerView salesListView;
    private StockAdjustPrintPreviewAdapter adapter;
    private TextView soNumberText;
    private TextView soDateText;
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
    private TextView companyAddress2Text;
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
    LinearLayout address4Layout;

    TextView customerAddress1;
    TextView customerAddress2;
    TextView customerAddress3;
    TextView customerAddress4;

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
        setContentView(R.layout.activity_stock_adjust_print_preview);
        setTitle();
        TscDll = new TSCActivity();
        session=new SessionManager(this);
        user=session.getUserDetails();
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

        shareLayout=findViewById(R.id.share_layout);
        printLayout=findViewById(R.id.print_layout);
        cancelButton=findViewById(R.id.cancel);

        View bottomSheet = findViewById(R.id.design_bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);

        Log.w("Printer_Mac_Id:",printerMacId);
        Log.w("Printer_Type:",printerType);

        checkPermission();

        if (getIntent() != null){
            adjustNumber = getIntent().getStringExtra("adjustNumber");
            if (adjustNumber !=null){
                try {
                    getStockAdjustDetails(adjustNumber);
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
                //showPrintAlert();
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

    private void getStockAdjustDetails(String number)
            throws JSONException {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Generating Print Preview...");
        pDialog.setCancelable(false);
        pDialog.show();
        // Initialize a new RequestQueue instance
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("GoodReceiptNo",number);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url= Utils.getBaseUrl(this) +"GoodsReceiptDetails";

        Log.w("Given_url:",url+jsonObject);

        stockadjustList =new ArrayList<>();
        stockadjustHeaderDetails = new ArrayList<>();
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
                            model.setNetTotal(object.optString("docTotal"));
//
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
                                stockadjustList.add(salesListModel);
                            }
                            model.setSalesList(stockadjustList);
                            stockadjustHeaderDetails.add(model);
                        }
                        if (stockadjustList.size()>0){
                            setStockAdjustAdapter();
                        }
                        pDialog.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }, error -> {
            pDialog.dismiss();
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
    private void createSignature(){
        if (Utils.getSignature()!=null && !Utils.getSignature().isEmpty()){
            try {
                ImageUtil.saveStamp(this,Utils.getSignature(),"Signature");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setStockAdjustAdapter(){
        for (StockAdjustmentPreviewModel model: stockadjustHeaderDetails){
            soNumberText.setText(model.getSoNumber());
            soDateText.setText(model.getSoDate());
            netTotalText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getNetTotal())));

            salesListView.setHasFixedSize(true);
            // RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
            salesListView.setLayoutManager(new LinearLayoutManager(StockAdjustPrintPreview.this,
                    LinearLayoutManager.VERTICAL, false));
            adapter = new StockAdjustPrintPreviewAdapter(StockAdjustPrintPreview.this, stockadjustList);
            salesListView.setAdapter(adapter);
            rootLayout.setVisibility(View.VISIBLE);
        }
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.print_menu, menu);

        MenuItem action_print = menu.findItem(R.id.action_print);
        action_print.setVisible(false);
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
                    //showPrintAlert();
                }else {
                    Toast.makeText(getApplicationContext(),"Please configure Printer",Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        }else if (id==android.R.id.home){
            finish();
        }else if (id == R.id.action_pdf) {
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
        File filePath = new File(dir, adjustNumber +".pdf");
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

    public void setTitle(){
        //Customize the ActionBar
        final ActionBar abar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.action_bar_title, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("Stock Adjustment");
        Objects.requireNonNull(abar).setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
        abar.setDisplayHomeAsUpEnabled(true);
        abar.setHomeButtonEnabled(true);
    }

}