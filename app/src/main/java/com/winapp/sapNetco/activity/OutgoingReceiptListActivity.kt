package com.winapp.sapNetco.activity

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.toptoche.searchablespinnerlibrary.SearchableSpinner
import com.winapp.sapNetco.R
import com.winapp.sapNetco.adapter.OutgoingReceiptsListAdapter
import com.winapp.sapNetco.adapter.SelectCustomerAdapter
import com.winapp.sapNetco.db.DBHelper
import com.winapp.sapNetco.iminPrinter.IminPrinterV2
import com.winapp.sapNetco.model.CustomerDetails
import com.winapp.sapNetco.model.CustomerGroupModel
import com.winapp.sapNetco.model.CustomerModel
import com.winapp.sapNetco.model.OutgoingReceiptsModel
import com.winapp.sapNetco.model.SupplierModel
import com.winapp.sapNetco.receipts.ReceiptPrintPreviewModel
import com.winapp.sapNetco.receipts.ReceiptPrintPreviewModel.ReceiptsDetails
import com.winapp.sapNetco.utils.BarCodeScanner
import com.winapp.sapNetco.utils.Constants
import com.winapp.sapNetco.utils.ImageUtil
import com.winapp.sapNetco.utils.SessionManager
import com.winapp.sapNetco.utils.Utils
import com.winapp.sapNetco.zebraprinter.TSCPrinter
import com.winapp.sapNetco.zebraprinter.ZebraPrinterActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.sql.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Date
import java.util.Locale
import java.util.Objects
import java.util.regex.Pattern

class OutgoingReceiptListActivity : NavigationActivity() {
    private var receiptsListView: RecyclerView? = null
    private var receiptsAdapter: OutgoingReceiptsListAdapter? = null
    private var receiptsList: ArrayList<OutgoingReceiptsModel>? = null
    private var pDialog: SweetAlertDialog? = null
      var sessiona: SessionManager? = null
      var usera: HashMap<String, String>? = null
    private var companyId: String? = null
    private val pageNo = 1
    private var behavior: BottomSheetBehavior<*>? = null
    private var customerList: ArrayList<CustomerModel>? = null
    private var customerNameAdapter: SelectCustomerAdapter? = null
    private var customerView: RecyclerView? = null
    private val btnCancel: Button? = null
    private val customerName: TextView? = null
    private var customerNameEdittext: EditText? = null
    private var dbHelper: DBHelper? = null
    private var netTotalText: TextView? = null
    private var customerDetails: ArrayList<CustomerDetails>? = null
    private var transLayout: LinearLayout? = null
    private var customerLayout: View? = null
    private var receiptsOptions: View? = null
    private var soCustomerName: TextView? = null
    private var receiptNumber: TextView? = null
    private var optionCancel: TextView? = null
    private var cancelSheet: TextView? = null
    var searchButton: Button? = null
    var cancelSearch: Button? = null
    var userListSpinner: Spinner? = null
    var searchFilterView: View? = null
    var customerNameText: EditText? = null
    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private val mHour = 0
    private val mMinute = 0
    var fromDateText: EditText? = null
    var toDateText: EditText? = null
    var isSearchCustomerNameClicked = false
    var addnewCustomer = false
    var userName: String? = null
    var recyclerViewLayout: FrameLayout? = null

    /** Items entered by the user is stored in this ArrayList variable  */
    var userList = ArrayList<String?>()

    /** Declaring an ArrayAdapter to set items to ListView  */
    var userAdapter: ArrayAdapter<String?>? = null
    var receiptDetailsLayout: LinearLayout? = null
    var printPreviewLayout: LinearLayout? = null
    var deleteLayout: LinearLayout? = null
    var receiptDetails: FloatingActionButton? = null
    var printPreview: FloatingActionButton? = null
    var deleteReceipt: FloatingActionButton? = null
    var receiptsModel: OutgoingReceiptsModel? = null
    var searchableCustomerList: ArrayList<String>? = null
    var customerListSpinner: SearchableSpinner? = null
    var progressLayout: View? = null
    private var printerMacId: String? = null
    private var printerType: String? = null
    private var sharedPreferences: SharedPreferences? = null
    var receiptNo: String? = null
    var noofCopy: String? = null
    private var receiptsHeaderDetails: ArrayList<ReceiptPrintPreviewModel>? = null
    private var receiptsPrintList: ArrayList<ReceiptsDetails>? = null
    var payMode: String? = null
    var customerCode: String? = null
    var receiptDate: String? = null
    var customername: String? = null
    private var customerGroupSpinner: Spinner? = null
    private var customersGroupList: ArrayList<CustomerGroupModel?>? = null
    var dialog: ProgressDialog? = null
    private val FILTER_CUSTOMER_CODE = 134
    private var customerNameTextView: TextView? = null
    private var selectCustomerCode: String? = ""
    private var selectCustomerName: String? = ""
    var currentDate = ""
    var locationCodea: String? = null
    var supplierList: ArrayList<SupplierModel>? = ArrayList()
    private var selectSupplierName: String? = ""
    private var selectSuppliercode: String? = ""
    private var supplierSpinner: SearchableSpinner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        val contentFrameLayout = findViewById<FrameLayout>(R.id.content_frame)
        //Remember this is the FrameLayout area within your activity_main.xml
        layoutInflater.inflate(R.layout.activity_outgoing_receipts_list, contentFrameLayout)
        // setContentView(R.layout.activity_invoice_list);
        Objects.requireNonNull(supportActionBar)!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle("Outgoing Payment")
        receiptsListView = findViewById(R.id.salesOrderList)
        dbHelper = DBHelper(this)
        sessiona = SessionManager(this)
        usera = sessiona!!.getUserDetails()
        companyId = usera!!.get(SessionManager.KEY_COMPANY_CODE)
        locationCodea = usera!!.get(SessionManager.KEY_LOCATION_CODE)
        userName = usera!!.get(SessionManager.KEY_USER_NAME)
        customerView = findViewById(R.id.customerList)
        netTotalText = findViewById(R.id.net_total)
        customerNameEdittext = findViewById(R.id.customer_search)
        transLayout = findViewById(R.id.trans_layout)
        customerDetails = dbHelper!!.getCustomer()
        customerLayout = findViewById(R.id.customer_layout)
        receiptsOptions = findViewById(R.id.receipt_options_layout)
        soCustomerName = findViewById(R.id.name)
        receiptNumber = findViewById(R.id.so_no)
        optionCancel = findViewById(R.id.option_cancel)
        cancelSheet = findViewById(R.id.cancel_sheet)
        fromDateText = findViewById(R.id.from_date)
        toDateText = findViewById(R.id.to_date)
        userListSpinner = findViewById(R.id.user_list_spinner)
        emptyLayout = findViewById(R.id.empty_layout)
        outstandingLayout = findViewById(R.id.outstanding_layout)
        cancelSearch = findViewById(R.id.btn_cancel)
        searchButton = findViewById(R.id.btn_search)
        searchFilterView = findViewById(R.id.search_filter)
        customerNameText = findViewById(R.id.customer_name_value)
        recyclerViewLayout = findViewById(R.id.reclerview_layout)
        progressLayout = findViewById(R.id.progress_layout)
        receiptDetailsLayout = findViewById(R.id.receipt_details_layout)
        receiptDetails = findViewById(R.id.receipts_details)
        deleteLayout = findViewById(R.id.delete_receipt_layout)
        deleteReceipt = findViewById(R.id.delete_receipt)
        printPreviewLayout = findViewById(R.id.print_preview_layout)
        printPreview = findViewById(R.id.print_preview)
        customerListSpinner = findViewById(R.id.customer_list_spinner)
        customerGroupSpinner = findViewById(R.id.customer_group)
        customerNameTextView = findViewById(R.id.customer_name_text)
        supplierSpinner = findViewById(R.id.supplier_name_spinner_outg)

        customerListSpinner!!.setTitle("Select Customer")
        receiptsList = ArrayList()
        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE)
        printerType = sharedPreferences!!.getString("printer_type", "")
        printerMacId = sharedPreferences!!.getString("mac_address", "")
        Log.w("Printer_Mac_Id:", printerMacId!!)
        Log.w("Printer_Type:", printerType!!)
        val c = Calendar.getInstance().time
        println("Current time => $c")
        val df = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = df.format(c)
        fromDateText!!.setText(formattedDate)
        toDateText!!.setText(formattedDate)
        userList.add(userName)
        /** Defining the ArrayAdapter to set items to Spinner Widget  */
        userAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, userList)
        /** Setting the adapter to the ListView  */
        userListSpinner!!.setAdapter(userAdapter)
        /** Adding radio buttons for the spinner items  */
        userAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        getCustomersGroups("")
        getVendorList()
        /*  customerList=dbHelper.getAllCustomers();
        if (customerList!=null && customerList.size()>0){
            setAdapter(customerList);
        }else {
            getCustomers();
        }*/

        // getCustomers();
        if (intent != null) {
            receiptNo = intent.getStringExtra("receiptNumber")
            noofCopy = intent.getStringExtra("noOfCopy")
            if (receiptNo != null) {
                try {
                    getReceiptsDetails(receiptNo!!, noofCopy!!.toInt())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        try {
            val c1 = Calendar.getInstance().time
            println("Current time => $c1")
            val df1 = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            currentDate = df1.format(c1)
            getReceiptsList("", currentDate, currentDate)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        /*  receiptsListView.setAdapter(receiptsAdapter);
        receiptsAdapter.setOrder();
        receiptsAdapter.setOnLoadMoreListener(new ReceiptsListAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e("haint", "Load More");
                receiptsList.add(null);
                receiptsAdapter.notifyItemInserted(receiptsList.size() - 1);
                //Load more data for reyclerview
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("haint", "Load More 2");

                        //Remove loading item
                        receiptsList.remove(receiptsList.size() - 1);
                        receiptsAdapter.notifyItemRemoved(receiptsList.size());
                        //Load data
                        int index = receiptsList.size();
                        int end = index + 20;
                        pageNo=pageNo+1;
                        try {
                            //getReceiptsList(companyId, String.valueOf(pageNo));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 5000);
            }
        });*/
        val bottomSheet = findViewById<View>(R.id.design_bottom_sheet)
        behavior = BottomSheetBehavior.from(bottomSheet)
        behavior!!.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_DRAGGING -> Log.i(
                        "BottomSheetCallback",
                        "BottomSheetBehavior.STATE_DRAGGING"
                    )

                    BottomSheetBehavior.STATE_SETTLING -> Log.i(
                        "BottomSheetCallback",
                        "BottomSheetBehavior.STATE_SETTLING"
                    )

                    BottomSheetBehavior.STATE_EXPANDED -> {
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_EXPANDED")
                        if (receiptsOptions!!.getVisibility() == View.VISIBLE) {
                            supportActionBar!!.setTitle("Select Option")
                        } else {
                            supportActionBar!!.setTitle("Select Customer")
                        }
                        transLayout!!.setVisibility(View.VISIBLE)
                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_COLLAPSED")
                        supportActionBar!!.setTitle("Outgoing Payment")
                        transLayout!!.setVisibility(View.GONE)
                    }

                    BottomSheetBehavior.STATE_HIDDEN -> Log.i(
                        "BottomSheetCallback",
                        "BottomSheetBehavior.STATE_HIDDEN"
                    )
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                Log.i("BottomSheetCallback", "slideOffset: $slideOffset")
            }
        })
        customerNameEdittext!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                val cusname = editable.toString()
                if (!cusname.isEmpty()) {
                    filter(cusname)
                }
            }
        })
        optionCancel!!.setOnClickListener(View.OnClickListener { viewCloseBottomSheet() })
        cancelSheet!!.setOnClickListener(View.OnClickListener { viewCloseBottomSheet() })
        cancelSheet!!.setOnClickListener(View.OnClickListener { viewCloseBottomSheet() })
        fromDateText!!.setOnClickListener(View.OnClickListener { getDateValueSearch(fromDateText) })
        toDateText!!.setOnClickListener(View.OnClickListener { getDateValueSearch(toDateText) })
        searchButton!!.setOnClickListener(View.OnClickListener {
            val sdformat = SimpleDateFormat("dd/MM/yyyy")
            var d1: Date? = null
            var d2: Date? = null
            try {
                d1 = sdformat.parse(fromDateText!!.getText().toString())
                d2 = sdformat.parse(toDateText!!.getText().toString())
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            if (d1!!.compareTo(d2) > 0) {
                Toast.makeText(
                    applicationContext,
                    "From date should not be greater than to date",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val oldFromDate = fromDateText!!.getText().toString()
                val oldToDate = toDateText!!.getText().toString()
                var fromDate: Date? = null
                var toDate: Date? = null
                try {
                    fromDate = SimpleDateFormat("dd/MM/yyyy").parse(oldFromDate)
                    toDate = SimpleDateFormat("dd/MM/yyyy").parse(oldToDate)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }

                // Use SimpleDateFormat#format() to format a Date into a String in a certain pattern.
                val fromDateString = SimpleDateFormat("yyyyMMdd").format(fromDate)
                val toDateString = SimpleDateFormat("yyyyMMdd").format(toDate)
                println("$fromDateString-$toDateString") // 2011-01-18
                searchFilterView!!.setVisibility(View.GONE)
                isSearchCustomerNameClicked = false
                try {
                    if (selectSuppliercode!!.isEmpty()) {
                        getReceiptsList("", fromDateString, toDateString)
                    } else {
                        getReceiptsList(selectSuppliercode, fromDateString, toDateString)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                //  filterSearch(customer_name, userListSpinner.getSelectedItem().toString(), fromDateString, toDateString);
                userListSpinner!!.setSelection(0)
            }
        })
        cancelSearch!!.setOnClickListener(View.OnClickListener {
            isSearchCustomerNameClicked = false
            customerNameText!!.setText("")
            userListSpinner!!.setSelection(0)
            fromDateText!!.setText(formattedDate)
            toDateText!!.setText(formattedDate)
            searchFilterView!!.setVisibility(View.GONE)
            userListSpinner!!.setSelection(0)
            supplierSpinner!!.setSelection(0)
            setFilterAdapeter()
        })
        printPreview!!.setOnClickListener(View.OnClickListener {
            viewCloseBottomSheet()
            val intent =
                Intent(this@OutgoingReceiptListActivity, OutgoingPaymentPrintPreview::class.java)
            intent.putExtra("receiptNumber", receiptNo.toString())
            intent.putExtra("customerCode", customerCode)
            intent.putExtra("customerName", soCustomerName!!.getText().toString())
            intent.putExtra("date", receiptDate)
            intent.putExtra("payMode", payMode)
            startActivity(intent)
        })
        printPreviewLayout!!.setOnClickListener(View.OnClickListener {
            viewCloseBottomSheet()
            val intent =
                Intent(this@OutgoingReceiptListActivity, OutgoingPaymentPrintPreview::class.java)
            intent.putExtra("receiptNumber", receiptNo.toString())
            intent.putExtra("customerCode", customerCode)
            intent.putExtra("customerName", soCustomerName!!.getText().toString())
            intent.putExtra("date", receiptDate)
            intent.putExtra("payMode", payMode)
            startActivity(intent)
        })
        deleteReceipt!!.setOnClickListener(View.OnClickListener {
            viewCloseBottomSheet()
            showRemoveAlert(receiptsModel!!.receiptNumber)
        })
        deleteLayout!!.setOnClickListener(View.OnClickListener {
            viewCloseBottomSheet()
            showRemoveAlert(receiptsModel!!.receiptNumber)
        })
        receiptDetails!!.setOnClickListener(View.OnClickListener {
            viewCloseBottomSheet()
            showCustomerDialog(
                receiptsModel!!.receiptNumber,
                receiptsModel!!.invoiceNumber,
                receiptsModel!!.netTotal,
                receiptsModel!!.netTotal,
                "0.00"
            )
        })
        receiptDetailsLayout!!.setOnClickListener(View.OnClickListener {
            viewCloseBottomSheet()
            showCustomerDialog(
                receiptsModel!!.receiptNumber,
                receiptsModel!!.invoiceNumber,
                receiptsModel!!.netTotal,
                receiptsModel!!.netTotal,
                "0.00"
            )
        })
        customerNameTextView!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(applicationContext, FilterCustomerListActivity::class.java)
            startActivityForResult(intent, FILTER_CUSTOMER_CODE)
        })
    }

    override fun onResume() {
        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE)
        printerType = sharedPreferences!!.getString("printer_type", "")
        printerMacId = sharedPreferences!!.getString("mac_address", "")
        super.onResume()
    }

    fun getCustomersGroups(groupCode: String?) {
        // Initialize a new RequestQueue instance
        val requestQueue = Volley.newRequestQueue(this)
        val url = Utils.getBaseUrl(this) + "BPGroupList"
        customerList = ArrayList()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("GroupCode", groupCode)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.w("Given_urlcustGroup:", url + jsonObject)
        dialog = ProgressDialog(this@OutgoingReceiptListActivity)
        dialog!!.setMessage("Loading Customers Groups...")
        dialog!!.setCancelable(false)
        dialog!!.show()
        customersGroupList = ArrayList()
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET,
            url,
            null,
            Response.Listener { response: JSONObject ->
                try {
                    Log.w("SAP_CUSTOMERS_GROUP:", response.toString())
                    val statusCode = response.optString("statusCode")
                    if (statusCode == "1") {
                        val customerDetailArray = response.optJSONArray("responseData")
                        for (i in 0 until customerDetailArray.length()) {
                            val `object` = customerDetailArray.optJSONObject(i)
                            val model = CustomerGroupModel()
                            model.customerGroupCode = `object`.optString("groupCode")
                            model.customerGroupName = `object`.optString("groupName")
                            customersGroupList!!.add(model)
                        }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Error,in getting Customer Group list",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    dialog!!.dismiss()
                    if (customersGroupList!!.size > 0) {
                        setCustomerGroupSpinner(customersGroupList!!)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error: VolleyError ->
                dialog!!.dismiss()
                // Do something when error occurred
                Log.w("Error_throwing:", error.toString())
            }) {
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                val creds =
                    String.format("%s:%s", Constants.API_SECRET_CODE, Constants.API_SECRET_PASSWORD)
                val auth = "Basic " + Base64.encodeToString(creds.toByteArray(), Base64.DEFAULT)
                params["Authorization"] = auth
                return params
            }
        }
        jsonObjectRequest.setRetryPolicy(object : RetryPolicy {
            override fun getCurrentTimeout(): Int {
                return 50000
            }

            override fun getCurrentRetryCount(): Int {
                return 50000
            }

            @Throws(VolleyError::class)
            override fun retry(error: VolleyError) {
            }
        })
        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonObjectRequest)
    }

    fun setCustomerGroupSpinner(customersGroupList: ArrayList<CustomerGroupModel?>) {
        val adapter: ArrayAdapter<CustomerGroupModel?> = ArrayAdapter<CustomerGroupModel?>(
            applicationContext,
            android.R.layout.simple_list_item_1,
            customersGroupList
        )
        customerGroupSpinner!!.setAdapter(adapter)
        customerGroupSpinner!!.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    val groupCode = customersGroupList[position]!!.customerGroupCode
                    val groupName = customersGroupList[position]!!.customerGroupName
                    getCustomers(groupCode)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    fun sendWhatsapp() {
        val pm = packageManager
        try {
            val waIntent = Intent(Intent.ACTION_SEND)
            waIntent.setType("text/plain")
            val text = "This is  a Test" // Replace with your own message.
            val info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA)
            //Check if package exists or not. If not then code
            //in catch block will be called
            waIntent.setPackage("com.whatsapp")
            waIntent.putExtra(Intent.EXTRA_TEXT, text)
            startActivity(Intent.createChooser(waIntent, "Share with"))
        } catch (e: PackageManager.NameNotFoundException) {
            Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Send the Message to the particular Number
    fun openWhatsAppView() {
        val pm = packageManager
        try {
            val toNumber =
                "+919790664487" // Replace with mobile phone number without +Sign or leading zeros, but with country code.
            //Suppose your country is India and your phone number is “xxxxxxxxxx”, then you need to send “91xxxxxxxxxx”.
            val sendIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$toNumber?body="))
            sendIntent.setPackage("com.whatsapp")
            startActivity(sendIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                this@OutgoingReceiptListActivity,
                "it may be you dont have whats app",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun openWhatsAppWithNumber() {
        try {
            val text = "This is a test" // Replace with your message.
            val toNumber =
                "+919790664487" // Replace with mobile phone number without +Sign or leading zeros, but with country code
            //Suppose your country is India and your phone number is “xxxxxxxxxx”, then you need to send “91xxxxxxxxxx”.
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=$toNumber&text=$text"))
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun shareContent() {
        val shareIntent: Intent
        val bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)
        var path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            .toString() + "/Share.png"
        var out: OutputStream? = null
        val file = File(path)
        try {
            out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        path = file.path
        val bmpUri = Uri.parse("file://$path")
        shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri)
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Hey please check this application https://play.google.com/store/apps/details?id=$packageName"
        )
        shareIntent.setType("image/png")
        startActivity(Intent.createChooser(shareIntent, "Share with"))
    }

    /*
    public void shareIntentSpecificApps(String articleName, String articleContent, String imageURL) {
        List<Intent> intentShareList = new ArrayList<Intent>();
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        //shareIntent.setType("image/ *");
        List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentActivities(shareIntent, 0);

        for (ResolveInfo resInfo : resolveInfoList) {
            String packageName = resInfo.activityInfo.packageName;
            String name = resInfo.activityInfo.name;
            Log.d("System Out", "Package Name : " + packageName);
            Log.d("System Out", "Name : " + name);

            if (packageName.contains("com.facebook") || packageName.contains("com.whatsapp")) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, name));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, articleName);
                intent.putExtra(Intent.EXTRA_TEXT, articleName + "\n" + articleContent);
               // Drawable dr = ivArticleImage.getDrawable();
              //  Bitmap bmp = ((BitmapDrawable) dr.getCurrent()).getBitmap();
                intent.putExtra(Intent.EXTRA_STREAM, ImageUtil.getLocalBitmapUri());
                intent.setType("image/ *");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intentShareList.add(intent);
            }
        }

        if (intentShareList.isEmpty()) {
            Toast.makeText(this, "No apps to share !", Toast.LENGTH_SHORT).show();
        } else {
            Intent chooserIntent = Intent.createChooser(intentShareList.remove(0), "Share Articles");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentShareList.toArray(new Parcelable[]{}));
            startActivity(chooserIntent);
        }
    }
*/
    fun showRemoveAlert(receiptNo: String?) {
        SweetAlertDialog(
            this@OutgoingReceiptListActivity,
            SweetAlertDialog.WARNING_TYPE
        ) // .setTitleText("Are you sure?")
            .setContentText("Are you sure want Delete this Receipt ?")
            .setConfirmText("YES")
            .setConfirmClickListener {
                //                        try {
//                            sDialog.dismiss();
//                          //  setReceiptDelete(receiptNo);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
            }
            .showCancelButton(true)
            .setCancelText("No")
            .setCancelClickListener { sDialog -> sDialog.cancel() }.show()
    }

    @Throws(JSONException::class)
    private fun getReceiptsDetails(receiptNumber: String, copy: Int) {
        // Initialize a new RequestQueue instance
        val jsonObject = JSONObject()
        //   jsonObject.put("CompanyCode", companyId);
        jsonObject.put("ReceiptNo", receiptNumber)
        jsonObject.put("LocationCode", locationCodea)
        val requestQueue = Volley.newRequestQueue(this)
        val url = Utils.getBaseUrl(this) + "ReceiptDetails"
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:", url)
        Log.w("JsonObjectPrint:", jsonObject.toString())
        // pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        //  pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        //  pDialog.setTitleText("Getting Printing Data..");
        // pDialog.setCancelable(false);
        // pDialog.show();
        receiptsHeaderDetails = ArrayList()
        receiptsList = ArrayList()
        receiptsPrintList = ArrayList()
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(Method.POST,
            url,
            jsonObject,
            Response.Listener { response: JSONObject ->
                try {
                    Log.w("Invoice_Details:", response.toString())

                    //  {"statusCode":1,"statusMessage":"Success","responseData":[{"customerCode":"C1001","customerName":"BRINDAS PTE LTD",
                    //  "receiptNumber":"9","receiptStatus":"O","receiptDate":"18\/8\/2021 12:00:00 am","netTotal":"5.350000",
                    //  "balanceAmount":5.35,"totalDiscount":0,"paidAmount":0,"contactPersonCode":"","createDate":"18\/8\/2021 12:00:00 am",
                    //  "updateDate":"18\/8\/2021 12:00:00 am","remark":"","fDocTotal":0,"fTaxAmount":0,"receivedAmount":0,"total":5.35,
                    //  "fTotal":0,"iTotalDiscount":0,"taxTotal":0.35,"iPaidAmount":0,"currencyCode":"SGD","currencyName":"Singapore Dollar",
                    //  "companyCode":"AATHI_LIVE_DB","docEntry":"9","address1":null,"taxPercentage":null,"discountPercentage":null,
                    //  "subTotal":5,"taxType":"I","taxCode":"IN","taxPerc":"7.000000","billDiscount":0,"signFlag":null,"signature":null,
                    //
                    //  "receiptDetails":[{"paymentNo":"9","paymentDate":"29\/8\/2021 12:00:00 am","cardCode":"C1001",
                    //  "cardName":"BRINDAS PTE LTD","paymentTot":225.02,"invoiceNo":"25","invoiceDate":"24\/8\/2021 12:00:00 am",
                    //  "slpName":"-No Sales Employee-","invoiceTot":225.02,"totDiscount":0,"invoiceAcct":null}]}]}
                    val statusCode = response.optString("statusCode")
                    if (statusCode == "1") {
                        val responseArray = response.optJSONArray("responseData")
                        val responseObject = responseArray.optJSONObject(0)
                        val model = ReceiptPrintPreviewModel()
                        model.receiptNumber = responseObject.optString("receiptNumber")
                        model.receiptDate = responseObject.optString("receiptDate")
                        model.payMode = responseObject.optString("payMode")
                        model.address =
                            responseObject.optString("address1") + responseObject.optString("address2") + responseObject.optString(
                                "address3"
                            )
                        model.address1 = responseObject.optString("address1")
                        model.address2 = responseObject.optString("address2")
                        model.address3 = responseObject.optString("address3")
                        model.addressstate =
                            (responseObject.optString("block") + " " + responseObject.optString("street") + " "
                                    + responseObject.optString("city"))
                        model.addresssZipcode =
                            (responseObject.optString("countryName") + " " + responseObject.optString(
                                "state"
                            ) + " "
                                    + responseObject.optString("zipcode"))
                        model.customerCode = responseObject.optString("customerCode")
                        model.customerName = responseObject.optString("customerName")
                        model.totalAmount = responseObject.optString("netTotal")
                        model.paymentType = responseObject.optString("paymentType")
                        model.bankCode = responseObject.optString("bankCode")
                        model.bankName = responseObject.optString("bankName")
                        model.chequeDate = responseObject.optString("checkDueDate")
                        model.chequeNo = responseObject.optString("checkNumber")
                        model.bankTransferDate = responseObject.optString("bankTransferDate")
                        model.balanceAmount = responseObject.optString("balanceAmount")
                        model.creditAmount = responseObject.optString("totalDiscount")
                        // model.setSignFlag(responseObject.optString("signFlag"));
                        // String signFlag = responseObject.optString("signFlag");
                        if (responseObject.optString("signature") != null && responseObject.optString(
                                "signature"
                            ) != "null" && !responseObject.optString("signature").isEmpty()
                        ) {
                            val signature = responseObject.optString("signature")
                            Utils.setSignature(signature)
                            createSignature()
                        } else {
                            Utils.setSignature("")
                        }
                        val detailsArray = responseObject.optJSONArray("receiptDetails")
                        for (i in 0 until detailsArray.length()) {
                            val `object` = detailsArray.getJSONObject(i)
                            val invoiceListModel = ReceiptsDetails()
                            invoiceListModel.invoiceNumber = `object`.optString("invoiceNo")
                            invoiceListModel.invoiceDate = `object`.optString("invoiceDate")
                            invoiceListModel.amount = `object`.optString("paidAmount")
                            invoiceListModel.discountAmount = `object`.optString("discountAmount")
                            receiptsPrintList!!.add(invoiceListModel)
                        }
                        model.setReceiptsDetailsList(receiptsPrintList)
                        receiptsHeaderDetails!!.add(model)
                    } else {
                    }
                    if (printerType == "iMin Printer V2") {
                        val printLayer = IminPrinterV2(this@OutgoingReceiptListActivity)
                        printLayer.printReceipts(1, receiptsHeaderDetails, receiptsPrintList)
                    } else {
                        printReceipt(copy)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error: VolleyError ->
                // Do something when error occurred
                // pDialog.dismiss();
                Log.w("Error_throwing:", error.toString())
            }) {
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                val creds =
                    String.format("%s:%s", Constants.API_SECRET_CODE, Constants.API_SECRET_PASSWORD)
                val auth = "Basic " + Base64.encodeToString(creds.toByteArray(), Base64.DEFAULT)
                params["Authorization"] = auth
                return params
            }
        }
        jsonObjectRequest.setRetryPolicy(object : RetryPolicy {
            override fun getCurrentTimeout(): Int {
                return 50000
            }

            override fun getCurrentRetryCount(): Int {
                return 50000
            }

            @Throws(VolleyError::class)
            override fun retry(error: VolleyError) {
            }
        })
        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonObjectRequest)
    }

    private fun createSignature() {
        if (Utils.getSignature() != null && !Utils.getSignature().isEmpty()) {
            try {
                ImageUtil.saveStamp(this, Utils.getSignature(), "Signature")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun setFilterAdapeter() {
        startActivity(intent)
        finish()
    }

    fun filterSearch(customerName: String, createUser: String, fromdate: String?, todate: String?) {
        try {
            val filterdNames = ArrayList<OutgoingReceiptsModel>()
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            var from_date: Date? = null
            var to_date: Date? = null
            try {
                from_date = sdf.parse(fromdate)
                to_date = sdf.parse(todate)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            for (model in OutgoingReceiptsListAdapter.getReceiptsList()) {
                val compareDate = sdf.parse(model.date)
                if (from_date == to_date) {
                    if (from_date == compareDate) {
                        if (!customerName.isEmpty()) {
                            if (model.name.lowercase(Locale.getDefault()).contains(
                                    customerName.lowercase(
                                        Locale.getDefault()
                                    )
                                )
                            ) {
                                if (model.user != null && model.user != "null") {
                                    if (model.user == createUser) {
                                        filterdNames.add(model)
                                    }
                                }
                                receiptsAdapter!!.filterList(filterdNames)
                            }
                        } else {
                            if (model.name.lowercase(Locale.getDefault()).contains(
                                    customerName.lowercase(
                                        Locale.getDefault()
                                    )
                                )
                            ) {
                                if (model.user != null && model.user != "null") {
                                    if (model.user == createUser) {
                                        filterdNames.add(model)
                                    }
                                }
                                receiptsAdapter!!.filterList(filterdNames)
                            }
                            receiptsAdapter!!.filterList(filterdNames)
                        }
                    }
                } else if (compareDate.compareTo(from_date) >= 0 && compareDate.compareTo(to_date) <= 0) {
                    println("Compare date occurs after from date")
                    if (!customerName.isEmpty()) {
                        if (model.name.lowercase(Locale.getDefault()).contains(
                                customerName.lowercase(
                                    Locale.getDefault()
                                )
                            )
                        ) {
                            if (model.name.lowercase(Locale.getDefault()).contains(
                                    customerName.lowercase(
                                        Locale.getDefault()
                                    )
                                )
                            ) {
                                if (model.user != null && model.user != "null") {
                                    if (model.user == createUser) {
                                        filterdNames.add(model)
                                    }
                                }
                                receiptsAdapter!!.filterList(filterdNames)
                            }
                            receiptsAdapter!!.filterList(filterdNames)
                        }
                    } else {
                        if (model.name.lowercase(Locale.getDefault()).contains(
                                customerName.lowercase(
                                    Locale.getDefault()
                                )
                            )
                        ) {
                            if (model.user != null && model.user != "null") {
                                if (model.user == createUser) {
                                    filterdNames.add(model)
                                }
                            }
                            receiptsAdapter!!.filterList(filterdNames)
                        }
                        receiptsAdapter!!.filterList(filterdNames)
                    }
                }
                receiptsAdapter!!.filterList(filterdNames)
            }
            Log.w("FilteredSize:", filterdNames.size.toString() + "")
            if (filterdNames.size > 0) {
                recyclerViewLayout!!.visibility = View.VISIBLE
                outstandingLayout!!.visibility = View.VISIBLE
                emptyLayout!!.visibility = View.GONE
                //  setNettotal(filterdNames);
                // invoiceAdapter.filterList(filterdNames);
            } else {
                recyclerViewLayout!!.visibility = View.GONE
                outstandingLayout!!.visibility = View.GONE
                emptyLayout!!.visibility = View.VISIBLE
            }
        } catch (ex: Exception) {
            Log.e("Error_in_filter", Objects.requireNonNull(ex.message)!!)
        }
    }

    fun setNettotal(salesOrderList: ArrayList<OutgoingReceiptsModel>) {
        try {
            var net_amount = 0.0
            for (model in salesOrderList) {
                if (model.netTotal != null && model.netTotal != "null") net_amount += model.netTotal.toDouble()
            }
            netTotalText!!.text = "$ " + Utils.twoDecimalPoint(net_amount)
        } catch (ex: Exception) {
        }
    }

    @Throws(JSONException::class)
    fun getReceiptsList(customerCode: String?, fromdate: String?, todate: String?) {
        // Initialize a new RequestQueue instance
        val requestQueue = Volley.newRequestQueue(this)
        // Initialize a new JsonArrayRequest instance
        val jsonObject = JSONObject()
        jsonObject.put("User", "")
        jsonObject.put("LocationCode", locationCodea)
        jsonObject.put("CustomerCode", customerCode)
        jsonObject.put("FromDate", fromdate)
        jsonObject.put("ToDate", todate)
        val url = Utils.getBaseUrl(this) + "OutgoingPaymentList"
        Log.w("Given_url_outgoin:", "$url/$jsonObject")
        receiptsList = ArrayList()
        pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog!!.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog!!.setTitleText("Getting All Outgoing Payment...")
        pDialog!!.setCancelable(false)
        pDialog!!.show()
        val jsonArrayRequest: JsonObjectRequest = object : JsonObjectRequest(Method.POST,
            url,
            jsonObject,
            Response.Listener { response: JSONObject ->
                try {
                    Log.w("Response_isReceipt:", response.toString())
                    pDialog!!.dismiss()
                    val statusCode = response.optString("statusCode")
                    if (statusCode == "1") {
                        val receiptArray = response.optJSONArray("responseData")
                        for (i in 0 until receiptArray.length()) {
                            val receiptsObject = receiptArray.optJSONObject(i)
                            val model = OutgoingReceiptsModel()
                            model.name = receiptsObject.optString("customerName")
                            model.receiptNumber = receiptsObject.optString("receiptNo")
                            model.transactionMode = receiptsObject.optString("payMode")
                            model.customerCode = receiptsObject.optString("customerCode")
                            model.customerName = receiptsObject.optString("customerName")
                            model.netTotal = receiptsObject.optString("paidAmount")
                            model.invoiceNumber = receiptsObject.optString("invoiceNo")
                            model.invoiceDate = receiptsObject.optString("invoiceDate")
                            model.receiptCode = receiptsObject.optString("code")
                            model.referenceNo = receiptsObject.optString("referenceno")
                            //  model.setUser(receiptsObject.optString("CreateUser"));
                            //  model.setCreditLimit(receiptsObject.optString("Credit"));
                            model.date = receiptsObject.optString("receiptDate")
                            val invoiceList = ArrayList<OutgoingReceiptsListAdapter.InvoiceModel>()
                            model.invoiceList = invoiceList
                            receiptsList!!.add(model)
                        }
                        Log.w("outgoingSize:", receiptsList!!.size.toString() + "")
                        setNettotal(receiptsList!!)
                        //receiptsAdapter.notifyDataSetChanged();
                        //receiptsAdapter.setLoaded();
                        if (receiptsList!!.size > 0) {
                            setReceiptsAdapter(receiptsList)
                            recyclerViewLayout!!.visibility = View.VISIBLE
                            emptyLayout!!.visibility = View.GONE
                        } else {
                            recyclerViewLayout!!.visibility = View.GONE
                            emptyLayout!!.visibility = View.VISIBLE
                        }
                    } else {
                        recyclerViewLayout!!.visibility = View.GONE
                        emptyLayout!!.visibility = View.VISIBLE
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error: VolleyError ->
                pDialog!!.dismiss()
                // Do something when error occurred
                Log.w("Error_throwing:", error.toString())
                Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_LONG).show()
            }) {
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                val creds =
                    String.format("%s:%s", Constants.API_SECRET_CODE, Constants.API_SECRET_PASSWORD)
                val auth = "Basic " + Base64.encodeToString(creds.toByteArray(), Base64.DEFAULT)
                params["Authorization"] = auth
                return params
            }
        }
        jsonArrayRequest.setRetryPolicy(object : RetryPolicy {
            override fun getCurrentTimeout(): Int {
                return 50000
            }

            override fun getCurrentRetryCount(): Int {
                return 50000
            }

            @Throws(VolleyError::class)
            override fun retry(error: VolleyError) {
            }
        })
        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonArrayRequest)
    }

    fun setReceiptsAdapter(receiptsList: ArrayList<OutgoingReceiptsModel>?) {
        receiptsListView!!.setHasFixedSize(true)
        receiptsListView!!.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        receiptsAdapter = OutgoingReceiptsListAdapter(
            this,
            receiptsListView,
            receiptsList,
            object : OutgoingReceiptsListAdapter.CallBack {
                override fun calculateNetTotal(receiptsList: ArrayList<OutgoingReceiptsModel>) {
                    setNettotal(receiptsList)
                }

                override fun showMoreOption(
                    receipt_number: String,
                    customerName: String,
                    customercode: String,
                    mode: String,
                    receipts: OutgoingReceiptsModel
                ) {
                    customerLayout!!.visibility = View.GONE
                    receiptsOptions!!.visibility = View.VISIBLE
                    customerCode = customercode
                    payMode = mode
                    receiptNumber!!.text = receipt_number
                    receiptNo = receipt_number
                    soCustomerName!!.text = customerName
                    receiptsModel = receipts
                    receiptDate = receipts.date
                    customerLayout!!.visibility = View.GONE
                    receiptsOptions!!.visibility = View.VISIBLE
                    if (behavior!!.state == BottomSheetBehavior.STATE_COLLAPSED) {
                        behavior!!.setState(BottomSheetBehavior.STATE_EXPANDED)
                    } else {
                        behavior!!.setState(BottomSheetBehavior.STATE_COLLAPSED)
                    }
                }

                override fun openWhatsapp() {
                    //sendWhatsapp();
                    //openWhatsAppView();
                    // openWhatsAppWithNumber();
                    shareContent()
                }
            })
        receiptsListView!!.adapter = receiptsAdapter
    }

    fun getTimeStamp(timestamp: String?): String? {
        var timeValue: String? = null
        val m = Pattern.compile("\\((.*?)\\)").matcher(timestamp)
        while (m.find()) {
            println(m.group(1))
            timeValue = m.group(1)
            getDate(timeValue.toLong())
        }
        return timeValue
    }

    private fun getDate(time: Long): String {
        val stamp = Timestamp(time)
        val date = Date(stamp.getTime())
        val df1 = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
        val datevalue = df1.format(date)
        Log.w("TimeValue:", datevalue)
        return datevalue
    }

    private fun getDateValue(time: Long): Date {
        val stamp = Timestamp(time)
        return Date(stamp.getTime())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.sorting_menu, menu)
        val barcode = menu.findItem(R.id.action_barcode)
        val addcustomer = menu.findItem(R.id.action_add)
        val filter = menu.findItem(R.id.action_filter)
        filter.setVisible(true)
        barcode.setVisible(false)
        addcustomer.setVisible(false)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) { //finish();
            onBackPressed()
            /*   case R.id.action_remove:
                showRemoveAlert();
                break;*/
        } else if (item.itemId == R.id.action_customer_name) {
            Collections.sort(receiptsList, object : Comparator<OutgoingReceiptsModel?> {
                override fun compare(obj1: OutgoingReceiptsModel?, obj2: OutgoingReceiptsModel?): Int {
                    // ## Ascending order
                    return obj1!!.name.compareTo(
                        obj2!!.name,
                        ignoreCase = true
                    ) // To compare string values
                    // return Integer.valueOf(obj1.empId).compareTo(Integer.valueOf(obj2.empId)); // To compare integer values

                    // ## Descending order
                    // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                    // return Integer.valueOf(obj2.empId).compareTo(Integer.valueOf(obj1.empId)); // To compare integer values
                }

            })
            receiptsAdapter!!.notifyDataSetChanged()
        } else if (item.itemId == R.id.action_amount) {
            Collections.sort(receiptsList, object : Comparator<OutgoingReceiptsModel?> {
                override fun compare(obj1: OutgoingReceiptsModel?, obj2: OutgoingReceiptsModel?): Int {

                    // ## Ascending order
                    //  return obj1.getNetTotal().compareToIgnoreCase(obj2.getNetTotal()); // To compare string values
                    return obj1!!.netTotal.toDouble()
                        .compareTo(obj2!!.netTotal.toDouble()) // To compare integer values

                    // ## Descending order
                    // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                    // return Integer.valueOf(obj2.empId).compareTo(Integer.valueOf(obj1.empId)); // To compare integer values
                }

            })
            receiptsAdapter!!.notifyDataSetChanged()
        } else if (item.itemId == R.id.action_date) {
            try {
                Collections.sort(receiptsList, object : Comparator<OutgoingReceiptsModel?> {
                    override fun compare(
                        obj1: OutgoingReceiptsModel?,
                        obj2: OutgoingReceiptsModel?
                    ): Int {
                        val sdfo = SimpleDateFormat("yyyy-MM-dd")
                        // Get the two dates to be compared
                        var d1: Date? = null
                        var d2: Date? = null
                        try {
                            d1 = sdfo.parse(obj1!!.date)
                            d2 = sdfo.parse(obj2!!.date)
                        } catch (e: ParseException) {
                            e.printStackTrace()
                        }
                        // ## Ascending order
                        //  return obj1.getNetTotal().compareToIgnoreCase(obj2.getNetTotal()); // To compare string values
                        return d1!!.compareTo(d2) // To compare integer values

                        // ## Descending order
                        // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                        // return Integer.valueOf(obj2.empId).compareTo(Integer.valueOf(obj1.empId)); // To compare integer values
                    }
                })
                receiptsAdapter!!.notifyDataSetChanged()
            } catch (ex: Exception) {
                Log.w("Error:", ex.message!!)
            }
        } else if (item.itemId == R.id.action_add) {
            customerDetails = dbHelper!!.getCustomer()
            if (customerDetails!!.size > 0) {
                //  showCustomerDialog(this,customerDetails.get(0).getCustomerName(),customerDetails.get(0).getCustomerCode(),customerDetails.get(0).getCustomerAddress1());
            } else {
                customerLayout!!.visibility = View.VISIBLE
                receiptsOptions!!.visibility = View.GONE
                viewCloseBottomSheet()
            }
        } else if (item.itemId == R.id.action_barcode) {
            val intent = Intent(applicationContext, BarCodeScanner::class.java)
            startActivity(intent)
        } else if (item.itemId == R.id.action_filter) {
            if (searchFilterView!!.visibility == View.VISIBLE) {
                searchFilterView!!.visibility = View.GONE
                customerNameText!!.setText("")
                supplierSpinner!!.setSelection(0)
                isSearchCustomerNameClicked = false
                if (behavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
                    behavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
                }
                //slideUp(searchFilterView);
            } else {
                customerNameText!!.setText("")
                isSearchCustomerNameClicked = false
                supplierSpinner!!.setSelection(0)
                searchFilterView!!.visibility = View.VISIBLE
                if (behavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
                    behavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
                }
                // slideDown(searchFilterView);
            }
        }
        return true
    }

    private fun sortArray(arraylist: ArrayList<OutgoingReceiptsModel>?) {
        val simpleDateFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") //your own date format
        if (arraylist != null) {
            Collections.sort<OutgoingReceiptsModel>(
                arraylist,
                object : Comparator<OutgoingReceiptsModel?> {
                    override fun compare(
                        o1: OutgoingReceiptsModel?,
                        o2: OutgoingReceiptsModel?
                    ): Int {
                        return try {
                            simpleDateFormat.parse(o2!!.dateSortingString)
                                .compareTo(simpleDateFormat.parse(o1!!.dateSortingString))
                        } catch (e: ParseException) {
                            e.printStackTrace()
                            0
                        }
                    }
                })
        }
    }

    private fun showCustomerDialog(
        receipt_no: String,
        invoice_no: String,
        net_total: String,
        paid_amount: String,
        credit_amount: String
    ) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)

        //then we will inflate the custom alert dialog xml that we created
        val dialogView =
            LayoutInflater.from(this).inflate(R.layout.receipts_details_layout, viewGroup, false)

        //Now we need an AlertDialog.Builder object
        val builder = AlertDialog.Builder(this)

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView)
        val receiptNumber = dialogView.findViewById<TextView>(R.id.receipt_no)
        val invoiceNumber = dialogView.findViewById<TextView>(R.id.invoice_no)
        val netTotal = dialogView.findViewById<TextView>(R.id.net_total)
        val paidAmount = dialogView.findViewById<TextView>(R.id.paid_amount)
        val creditAmount = dialogView.findViewById<TextView>(R.id.credit_amount)
        receiptNumber.text = receipt_no
        invoiceNumber.text = invoice_no
        netTotal.text = net_total
        if (paid_amount == "null") {
            paidAmount.text = "0.00"
        } else {
            paidAmount.text = paid_amount
        }
        if (credit_amount == "null") {
            creditAmount.text = "0.00"
        } else {
            creditAmount.text = credit_amount
        }
        val yesButton = dialogView.findViewById<Button>(R.id.buttonOk)
        //finally creating the alert dialog and displaying it
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
        yesButton.setOnClickListener { alertDialog.dismiss() }
    }

    fun getCustomers(groupCode: String?) {
        // Initialize a new RequestQueue instance
        val requestQueue = Volley.newRequestQueue(this)
        val url = Utils.getBaseUrl(this) + "CustomerList"
        customerList = ArrayList()
        searchableCustomerList = ArrayList()
        searchableCustomerList!!.add("Select Customer")
        val jsonObject = JSONObject()
        try {
            jsonObject.put("GroupCode", groupCode)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.w("Given_url_customer:", url + jsonObject)
        val jsonArrayRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            url,
            jsonObject,
            Response.Listener { response: JSONObject ->
                try {
                    Log.w("Response_Customer:", response.toString())
                    // pDialog.dismiss();
                    // Loop through the array elements
                    val statusCode = response.optString("statusCode")
                    if (statusCode == "1") {
                        val customerDetailArray = response.optJSONArray("responseData")
                        for (i in 0 until customerDetailArray.length()) {
                            val `object` = customerDetailArray.optJSONObject(i)
                            //  if (customerObject.optBoolean("IsActive")) {
                            val model = CustomerModel()
                            model.customerCode = `object`.optString("customerCode")
                            model.customerName = `object`.optString("customerName")
                            model.address1 = `object`.optString("address")
                            model.address2 = `object`.optString("street")
                            model.address3 = `object`.optString("city")
                            model.customerAddress = `object`.optString("address")
                            model.haveTax = `object`.optString("HaveTax")
                            model.taxType = `object`.optString("taxType")
                            model.taxPerc = `object`.optString("taxPercentage")
                            model.taxCode = `object`.optString("taxCode")
                            model.billDiscPercentage = `object`.optString("discountPercentage")
                            //  model.setCustomerBarcode(object.optString("BarCode"));
                            // model.setCustomerBarcode(String.valueOf(i));
                            if (`object`.optString("outstandingAmount") == "null" || `object`.optString(
                                    "outstandingAmount"
                                ).isEmpty()
                            ) {
                                model.outstandingAmount = "0.00"
                            } else {
                                model.outstandingAmount = `object`.optString("outstandingAmount")
                            }
                            customerList!!.add(model)
                            searchableCustomerList!!.add(
                                `object`.optString("customerName") + "-" + `object`.optString(
                                    "customerCode"
                                )
                            )
                            // }
                        }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Error,in getting Customer list",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    if (customerList!!.size > 0) {
                        setAdapter(customerList!!)
                        setDataToAdapter(searchableCustomerList)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error: VolleyError ->
                // Do something when error occurred
                Log.w("Error_throwing:", error.toString())
            }) {
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                val creds =
                    String.format("%s:%s", Constants.API_SECRET_CODE, Constants.API_SECRET_PASSWORD)
                val auth = "Basic " + Base64.encodeToString(creds.toByteArray(), Base64.DEFAULT)
                params["Authorization"] = auth
                return params
            }
        }
        jsonArrayRequest.setRetryPolicy(object : RetryPolicy {
            override fun getCurrentTimeout(): Int {
                return 50000
            }

            override fun getCurrentRetryCount(): Int {
                return 50000
            }

            @Throws(VolleyError::class)
            override fun retry(error: VolleyError) {
            }
        })
        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonArrayRequest)
    }

    fun setDataToAdapter(arrayList: ArrayList<String>?) {
        // Creating ArrayAdapter using the string array and default spinner layout
        val arrayAdapter = ArrayAdapter(
            this@OutgoingReceiptListActivity,
            android.R.layout.simple_spinner_item,
            arrayList!!
        )
        // Specify layout to be used when list of choices appears
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Applying the adapter to our spinner
        customerListSpinner!!.adapter = arrayAdapter
        // customerListSpinner.setOnItemSelectedListener(this);
    }

    private fun setAdapter(customerNames: ArrayList<CustomerModel>) {
        progressLayout!!.visibility = View.GONE
        customerView!!.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        customerNameAdapter =
            SelectCustomerAdapter(this, customerNames) { customer, customername, pos ->
                customerLayout!!.visibility = View.VISIBLE
                receiptsOptions!!.visibility = View.GONE
                val count = dbHelper!!.numberOfRows()
                if (count > 0) {
                    showProductDeleteAlert(customer)
                } else {
                    viewCloseBottomSheet()
                    dbHelper!!.removeAllItems()
                    val intent =
                        Intent(this@OutgoingReceiptListActivity, AddInvoiceActivityOld::class.java)
                    intent.putExtra("customerId", customer)
                    intent.putExtra("activityFrom", "SalesOrder")
                    startActivity(intent)
                }
                Log.w("Customer_id:", customer)
            }
        customerView!!.adapter = customerNameAdapter
    }

    fun showProductDeleteAlert(customerId: String?) {
        val builder1 = AlertDialog.Builder(this)
        builder1.setTitle("Warning !")
        builder1.setMessage("Products in Cart will be removed..")
        builder1.setCancelable(false)
        builder1.setPositiveButton(
            "OK"
        ) { dialog, id ->
            dialog.cancel()
            dbHelper!!.removeAllItems()
            viewCloseBottomSheet()
            val intent = Intent(this@OutgoingReceiptListActivity, AddInvoiceActivityOld::class.java)
            intent.putExtra("customerId", customerId)
            intent.putExtra("activityFrom", "SalesOrder")
            startActivity(intent)
        }
        builder1.setNegativeButton(
            "CANCEL"
        ) { dialog, id -> dialog.cancel() }
        val alert11 = builder1.create()
        alert11.show()
    }

    private fun filter(text: String) {
        try {
            //new array list that will hold the filtered data
            val filterdNames = ArrayList<CustomerModel>()
            //looping through existing elements
            for (s in customerList!!) {
                //if the existing elements contains the search input
                if (s.customerName.lowercase(Locale.getDefault())
                        .contains(text.lowercase(Locale.getDefault())) || s.customerCode.lowercase(
                        Locale.getDefault()
                    ).contains(text.lowercase(Locale.getDefault()))
                ) {
                    //adding the element to filtered list
                    filterdNames.add(s)
                }
            }
            //calling a method of the adapter class and passing the filtered list
            customerNameAdapter!!.filterList(filterdNames)
        } catch (ex: Exception) {
            Log.e("Error_in_filter", Objects.requireNonNull(ex.message)!!)
        }
    }

    fun viewCloseBottomSheet() {
        hideKeyboard()
        if (behavior!!.state == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior!!.setState(BottomSheetBehavior.STATE_EXPANDED)
        } else {
            behavior!!.setState(BottomSheetBehavior.STATE_COLLAPSED)
        }
        // get the Customer name from the local db
    }

    fun hideKeyboard() {
        try {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this.currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
            // TODO: handle exception
        }
    }

    fun getDateValueSearch(dateEditext: EditText?) {
        // Get Current Date
        val c = Calendar.getInstance()
        mYear = c[Calendar.YEAR]
        mMonth = c[Calendar.MONTH]
        mDay = c[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(this@OutgoingReceiptListActivity,
            { view, year, monthOfYear, dayOfMonth -> dateEditext!!.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year) },
            mYear,
            mMonth,
            mDay
        )
        datePickerDialog.show()
    }

    @Throws(IOException::class)
    fun printReceipt(copy: Int) {
        if (printerType == "TSC Printer") {
            //  TSCPrinter tscPrinter = new TSCPrinter(ReceiptsPrintPreview.this, printerMacId);
            //  tscPrinter.printInvoice(receiptsHeaderDetails, receiptsList);
            val printer = TSCPrinter(this@OutgoingReceiptListActivity, printerMacId, "Receipt")
            printer.printReceipts(copy, receiptsHeaderDetails, receiptsPrintList)
            printer.setOnCompletionListener {
                Utils.setSignature("")
                Toast.makeText(
                    applicationContext,
                    "Receipt printed successfully!",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        } else if (printerType == "Zebra Printer") {
            val zebraPrinterActivity =
                ZebraPrinterActivity(this@OutgoingReceiptListActivity, printerMacId)
            try {
                zebraPrinterActivity.printReceipts(copy, receiptsHeaderDetails, receiptsPrintList)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == FILTER_CUSTOMER_CODE) {
            assert(data != null)
            val customername = data!!.getStringExtra("customerName")
            val customercode = data.getStringExtra("customerCode")
            customerNameTextView!!.text = customername
            selectCustomerCode = customercode
            selectCustomerName = customername
        }
    }
    @Throws(JSONException::class)
    private fun getVendorList() {
        // CommonMethods.showProgressDialog(this)
        val jsonObject = JSONObject()
        //  jsonObject.put("WhsCode", whsCode)

        val requestQueue = Volley.newRequestQueue(this)

        val url = Utils.getBaseUrl(this) + "vendorList"
        Log.w("url_vendorlist:", url)

        supplierList = ArrayList()

        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET,
            url,
            null,
            Response.Listener { response: JSONObject ->
                try {
                    GlobalScope.launch {

                        Log.w("url_vendorlist_res:", response.toString())
                        val statusCode = response.optString("statusCode")
                        val statusMsg = response.optString("statusMessage")

                        if (statusCode == "1") {
                            val responseData = response.optJSONArray("responseData")!!

                            if (responseData!!.length() > 0) {
                                for (i in 0 until responseData.length()) {
                                    val obj = responseData.optJSONObject(i)

                                    val model = SupplierModel(
                                        obj.optString("vendorCode"),
                                        obj.optString("vendorName"),
                                        obj.optString("currencyCode"),
                                        obj.optString("currencyName"),
                                        obj.optString("taxType"),
                                        obj.optString("taxCode"),
                                        obj.optString("taxName"),
                                        obj.optString("taxPercentage")
                                    )

                                    supplierList!!.add(model)
                                }

                                withContext(Dispatchers.Main) {
                                    if (supplierList!!.size > 0) {
                                        setSupplierSpinner(supplierList!!)
                                    }
                                }
                            }
                        }
                        //  CommonMethods.cancelProgressDialog()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error: VolleyError ->
                // Do something when error occurred
                //  CommonMethods.cancelProgressDialog()
                Log.w("Error_throwing:", error.toString())
            }) {
            override fun getHeaders(): Map<String, String> {
                val params = java.util.HashMap<String, String>()
                val creds = java.lang.String.format(
                    "%s:%s",
                    Constants.API_SECRET_CODE,
                    Constants.API_SECRET_PASSWORD
                )
                val auth = "Basic " + Base64.encodeToString(creds.toByteArray(), Base64.DEFAULT)
                params["Authorization"] = auth
                return params
            }
        }
        jsonObjectRequest.retryPolicy = object : RetryPolicy {
            override fun getCurrentTimeout(): Int {
                return 50000
            }

            override fun getCurrentRetryCount(): Int {
                return 50000
            }

            @Throws(VolleyError::class)
            override fun retry(error: VolleyError) {
            }
        }
        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonObjectRequest)
    }
    fun setSupplierSpinner(spinnerlist: ArrayList<SupplierModel>) {
        val spinnerlst = SupplierModel(
            customerName = "Select Supplier",
            customerCode = "", currencyCode = "", currencyName = "",
            taxCode = "", taxName = "", taxPercentage = "", taxType = ""
        )
        spinnerlist.add(0, spinnerlst)

        Log.w("spinnnVal", "" + spinnerlist)
        val adapter = ArrayAdapter<String>(this, R.layout.cust_spinner_item)
        for (i in spinnerlist.indices) {
            adapter.add(spinnerlist[i].customerName)
        }
        supplierSpinner!!.adapter = adapter
        supplierSpinner!!.setTitle("")
        supplierSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapter: AdapterView<*>, v: View, position: Int, id: Long) {
                // On selecting a spinner item
                Log.w("spinnnVal1", "" + spinnerlist[position].customerName)
                selectSuppliercode = spinnerlist[position].customerCode
                selectSupplierName = spinnerlist[position].customerName
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }
    }
    override fun onBackPressed() {
        //Execute your code here
        // Intent intent=new Intent(ReceiptsListActivity.this, NewInvoiceListActivity.class);
        // startActivity(intent);
        finish()
    }

    companion object {
        var selectCustomer: TextView? = null
        var emptyLayout: LinearLayout? = null
        var outstandingLayout: LinearLayout? = null
    }
}