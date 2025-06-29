package com.winapp.sapNetco.iminPrinter;

import static com.winapp.sapNetco.utils.Utils.fourDecimalPoint;
import static com.winapp.sapNetco.utils.Utils.twoDecimalPoint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.imin.printer.PrinterHelper;
import com.winapp.sapNetco.db.DBHelper;
import com.winapp.sapNetco.model.InvoicePrintPreviewModel;
import com.winapp.sapNetco.model.SalesOrderPrintPreviewModel;
import com.winapp.sapNetco.model.SettingsModel;
import com.winapp.sapNetco.receipts.ReceiptPrintPreviewModel;
import com.winapp.sapNetco.salesreturn.SalesReturnPrintPreviewModel;
import com.winapp.sapNetco.utils.Constants;
import com.winapp.sapNetco.utils.SessionManager;
import com.winapp.sapNetco.utils.SharedPreferenceUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import com.winapp.sapNetco.R;
import com.winapp.sapNetco.utils.Utils;

public class IminPrinterV2 {
    private Context context;
    private String company_name;
    private String company_code;
    private String company_address1;
    private String company_address2;
    private String company_address3;
    private String company_phone;
    private String company_gst;
    private SessionManager session;
    private HashMap<String, String> user;
    public ArrayList<SettingsModel> settingsList;
    private DBHelper dbHelper;
    private String showUserName = "";
    private String showSignature = "";
    public static String shortCodeStr = "" ;
    private String userName;
    private int height = 100;

    private String showLogo = "";
    private String showReturn = "";
    private String showUom = "";
    private String showQrCode = "";
    private String showStamp = "";
    private String latLongLoc = "";
    private String payNow = "";
    private String salesManName = "";
    private String userMiddleName = "";
    private String salesManPhone = "";
    private String salesManMail = "";
    private String salesManOffice = "";
    private SharedPreferenceUtil sharedPreferenceUtil;
    int finalHeight = 0;
    private final int invoiceDefaultHeight = 60;

    private int invoiceBottomLine = 50;
    private final int invoiveSubTotalHeight = 30;
    private int invoiveReturnHeight = 0;
    private int invoivePaynowHeight = 0;
    private int invoiveSalesManHeight = 0;

    private final int invoiceLineHeight = 10;
    private String bankCode = "";
    private String cheque = "";
    Bitmap printLineSmall;
    double qtyVal = 0.0;

    public IminPrinterV2(Context context) {
        try {
            printLineSmall = BitmapFactory.decodeResource(context.getResources(), R.drawable.grid_line);

            user = session.getUserDetails();
            userName = user.get(SessionManager.KEY_USER_NAME);
            company_name = user.get(SessionManager.KEY_COMPANY_NAME);
            company_code = user.get(SessionManager.KEY_COMPANY_CODE);
            company_address1 = user.get(SessionManager.KEY_ADDRESS1);
            company_address2 = user.get(SessionManager.KEY_ADDRESS2);
            company_address3 = user.get(SessionManager.KEY_ADDRESS3);
            company_phone = user.get(SessionManager.KEY_PHONE_NO);
            company_gst = user.get(SessionManager.KEY_COMPANY_REG_NO);
            payNow = user.get(SessionManager.KEY_PAY_NOW);
            cheque = user.get(SessionManager.KEY_CHEQUE);
            bankCode = user.get(SessionManager.KEY_BANK);
            salesManName = user.get(SessionManager.KEY_SALESMAN_NAME);
            salesManPhone = user.get(SessionManager.KEY_SALESMAN_PHONE);
            salesManMail = user.get(SessionManager.KEY_SALESMAN_EMAIL);
            salesManOffice = user.get(SessionManager.KEY_SALESMAN_OFFICE);
            userMiddleName = sharedPreferenceUtil.getStringPreference(sharedPreferenceUtil
                    .KEY_USER_MIDDLE_NAME,"");
            shortCodeStr = sharedPreferenceUtil.getStringPreference(sharedPreferenceUtil
                    .KEY_SHORT_CODE,"");

            settingsList = dbHelper.getSettings();
            if (settingsList != null) {
                if (settingsList.size() > 0) {
                    for (SettingsModel model : settingsList) {
                        if (model.getSettingName().equals("showUserName")) {
                            Log.w("SettingName:", model.getSettingName());
                            Log.w("SettingValue:", model.getSettingValue());
                            if (model.getSettingValue().equals("True")) {
                                showUserName = "true";
                            } else {
                                showUserName = "false";
                            }
                        } else if (model.getSettingName().equals("showLogo")) {
                            Log.w("SettingName:", model.getSettingName());
                            Log.w("SettingValue:", model.getSettingValue());
                            if (model.getSettingValue().equals("True")) {
                                showLogo = "true";
                            } else {
                                showLogo = "false";
                            }
                        } else if (model.getSettingName().equals("showReturnDetails")) {
                            Log.w("SettingName:", model.getSettingName());
                            Log.w("SettingValue:", model.getSettingValue());

                            if (company_code.equalsIgnoreCase("Trans Orient Singapore Pte Ltd") ||
                                    company_code.equalsIgnoreCase("RAYMANG EGGS & POULTRY SUPPLIER")) {
                                showReturn = "false";
                            } else {
                                if (model.getSettingValue().equals("True")) {
                                    showReturn = "true";
                                } else {
                                    showReturn = "false";
                                }
                            }
                        } else if (model.getSettingName().equals("showUom")) {
                            Log.w("SettingName:", model.getSettingName());
                            Log.w("SettingValue:", model.getSettingValue());
                            if (model.getSettingValue().equals("True")) {
                                showUom = "true";
                            } else {
                                showUom = "false";
                            }
                        } else if (model.getSettingName().equals("showSignature")) {
                            Log.w("SettingName:", model.getSettingName());
                            Log.w("SettingValue:", model.getSettingValue());
                            if (model.getSettingValue().equals("True")) {
                                showSignature = "true";
                            } else {
                                showSignature = "false";
                            }
                        } else if (model.getSettingName().equals("showQRCode")) {
                            Log.w("SettingName:", model.getSettingName());
                            Log.w("SettingValue:", model.getSettingValue());
                            if (model.getSettingValue().equals("True")) {
                                showQrCode = "true";
                            } else {
                                showQrCode = "false";
                            }
                        } else if (model.getSettingName().equals("showPaidOrUnpaidImage")) {
                            Log.w("SettingName:", model.getSettingName());
                            Log.w("SettingValue:", model.getSettingValue());
                            if (model.getSettingValue().equals("True")) {
                                showStamp = "true";
                            } else {
                                showStamp = "false";
                            }
                        } else if (model.getSettingName().equals("latlong_LocSwitch")) {
                            Log.w("SettingName:", model.getSettingName());
                            Log.w("SettingValue:", model.getSettingValue());
                            if (model.getSettingValue().equals("True")) {
                                latLongLoc = "true";
                            } else {
                                latLongLoc = "false";
                            }
                        }
                    }
                }
            }

    } catch (Exception ex) {
        }
    }


    public void printInvoice(int copy, ArrayList<InvoicePrintPreviewModel> invoiceHeaderDetails,
                                   ArrayList<InvoicePrintPreviewModel.InvoiceList> invoiceList, String isDoPrint){

        try {
            for (int i = 0; i < copy; i++) {
                Log.w("invoiceimin1", "");
                Toast.makeText(context, "invoiceimin1 " + invoiceList.size(), Toast.LENGTH_SHORT).show();

                PrinterHelper.getInstance().setTextBitmapSize(25);
                PrinterHelper.getInstance().setTextBitmapTypeface("Typeface.DEFAULT_BOLD");
                PrinterHelper.getInstance().printTextBitmapWithAli(company_name, 1, null);
                PrinterHelper.getInstance().printAndFeedPaper(10);

                printCompanyDetails();

                PrinterHelper.getInstance().setTextBitmapSize(18);
                PrinterHelper.getInstance().setCodeAlignment(1);
                PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
                PrinterHelper.getInstance().printText("UEN No / GST REG NO: " + company_gst + "\n", null);

                PrinterHelper.getInstance().printAndFeedPaper(20);

                if (Double.parseDouble(invoiceHeaderDetails.get(0).getNetTax()) > 0) {
                    PrinterHelper.getInstance().setTextBitmapSize(25);
                    PrinterHelper.getInstance().setTextBitmapTypeface("Typeface.DEFAULT");
                    PrinterHelper.getInstance().printTextBitmapWithAli("Tax Invoice", 1, null);
                } else {
                    PrinterHelper.getInstance().setTextBitmapSize(25);
                    PrinterHelper.getInstance().setTextBitmapTypeface("Typeface.DEFAULT");
                    PrinterHelper.getInstance().printTextBitmapWithAli("Invoice", 1, null);
                }
                PrinterHelper.getInstance().printAndFeedPaper(20);

                PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
                String[] bilDateString = new String[]{"Invoice No :", invoiceHeaderDetails.get(0).getInvoiceNumber()};
                int[] colsWidthArr3 = new int[]{5, 10};
                int[] colsAlign3 = new int[]{0, 0};
                int[] colsSize3 = new int[]{22, 20};
                PrinterHelper.getInstance().printColumnsString(bilDateString, colsWidthArr3, colsAlign3, colsSize3, null);

                String[] bilDateTime = new String[]{"Inv Date :", invoiceHeaderDetails.get(0).getInvoiceDate()};
                int[] colsWidthArr3DateTime = new int[]{5, 10};
                int[] colsAlign3DateTime = new int[]{0, 0};
                int[] colsSize3DateTime = new int[]{22, 20};
                PrinterHelper.getInstance().printColumnsString(bilDateTime, colsWidthArr3DateTime, colsAlign3DateTime, colsSize3DateTime, null);

                String[] tableId = new String[]{"Cus Code :", invoiceHeaderDetails.get(0).getCustomerCode()};
                int[] colsWidth = new int[]{5, 10};
                int[] colsAlign = new int[]{0, 0};
                int[] colsSize = new int[]{22, 20};
                PrinterHelper.getInstance().printColumnsString(tableId, colsWidth, colsAlign, colsSize, null);

                String[] tableIdString = new String[]{"Cus Name :", invoiceHeaderDetails.get(0).getCustomerName()};
                int[] colsWidthTable = new int[]{5, 10};
                int[] colsAlignTable = new int[]{0, 0};
                int[] colsSizeTable = new int[]{22, 20};
                PrinterHelper.getInstance().printColumnsString(tableIdString, colsWidthTable, colsAlignTable,
                        colsSizeTable, null);

                if (invoiceHeaderDetails.get(0).getAddress() != null &&
                        !invoiceHeaderDetails.get(0).getAddress().isEmpty()) {
                    String[] tableIdString1 = new String[]{"ADDR :", invoiceHeaderDetails.get(0).getAddress()};
                    int[] colsWidthTable1 = new int[]{5, 10};
                    int[] colsAlignTable1 = new int[]{0, 0};
                    int[] colsSizeTable1 = new int[]{22, 20};
                    PrinterHelper.getInstance().printColumnsString(tableIdString1, colsWidthTable1, colsAlignTable1,
                            colsSizeTable1, null);
                }
                 if (invoiceHeaderDetails.get(0).getDeliveryAddress() != null && !invoiceHeaderDetails.get(0).getDeliveryAddress().isEmpty()) {

                        String[] tableIdString2 = new String[]{"DEL ADDR :", invoiceHeaderDetails.get(0).getDeliveryAddress()};
                        int[] colsWidthTable2 = new int[]{5, 10};
                        int[] colsAlignTable2 = new int[]{0, 0};
                        int[] colsSizeTable2 = new int[]{22, 20};
                        PrinterHelper.getInstance().printColumnsString(tableIdString2, colsWidthTable2, colsAlignTable2,
                                colsSizeTable2, null);
                    }
                if (invoiceHeaderDetails.get(0).getPaymentTerm() != null &&
                        !invoiceHeaderDetails.get(0).getPaymentTerm().isEmpty()) {

                    String[] bilDateTerm = new String[]{"Payment Terms :", invoiceHeaderDetails.get(0).getPaymentTerm()};
                    int[] colsWidthArr3DateTerm = new int[]{5, 10};
                    int[] colsAlign3DateTerm = new int[]{0, 0};
                    int[] colsSize3DateTerm = new int[]{22, 20};
                    PrinterHelper.getInstance().printColumnsString(bilDateTerm, colsWidthArr3DateTerm, colsAlign3DateTerm, colsSize3DateTerm, null);
                }

                PrinterHelper.getInstance().printAndFeedPaper(5);
                PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);

                String[] tableIdString5 = new String[]{"User :", userName};
                int[] colsWidthTable5 = new int[]{5, 10};
                int[] colsAlignTable5 = new int[]{0, 0};
                int[] colsSizeTable5 = new int[]{22, 20};
                PrinterHelper.getInstance().printColumnsString(tableIdString5, colsWidthTable5, colsAlignTable5,
                        colsSizeTable5, null);
                PrinterHelper.getInstance().printAndFeedPaper(10);

                if (printLineSmall != null) {
                    PrinterHelper.getInstance().printBitmapWithAlign(printLineSmall, 1, null);
                } else {
                    PrinterHelper.getInstance().printTextBitmap("----------------------------------", null);
                }
                PrinterHelper.getInstance().printAndFeedPaper(5);

                PrinterHelper.getInstance().setTextBitmapStyle(Typeface.BOLD);
                String[] titleString = new String[]{"Sn", "Desc", "Qty", "Price", "Total"};
                int[] colsWidthTitle = new int[]{2, 7, 4, 4, 4};
                int[] colsAlignTitle = new int[]{0, 0, 1, 1, 1};
                int[] colsSizeTitle = new int[]{22, 22, 22, 22, 22};
                PrinterHelper.getInstance().printColumnsString(titleString, colsWidthTitle,
                        colsAlignTitle, colsSizeTitle, null);

                PrinterHelper.getInstance().printAndFeedPaper(5);
                if (printLineSmall != null) {
                    PrinterHelper.getInstance().printBitmapWithAlign(printLineSmall, 1, null);
                } else {
                    PrinterHelper.getInstance().printTextBitmap("----------------------------------", null);
                }

                PrinterHelper.getInstance().printAndFeedPaper(5);
                PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
                int index = 1;
                double exQty = 0.0;
                double cqty = 0.0;
                double overallItemDiscount = 0.0;

                for (InvoicePrintPreviewModel.InvoiceList invoice : invoiceList) {

                    Toast.makeText(context, "invoiceimin2 " + invoiceList.size(), Toast.LENGTH_SHORT).show();

                    String productName = "";
                    String priceValue = "0.00";
                    String totalValue = "0.00";
                    String uomCode = "";

                    if (invoice.getSaleType().equals("Cqty") || invoice.getSaleType().equals("Lqty")) {
                        productName = invoice.getDescription();
                    } else if (invoice.getSaleType().equals("Return")) {
                        productName = invoice.getDescription() + " (as Ret)";
                    } else if (invoice.getSaleType().equals("FOC")) {
                        productName = invoice.getDescription() + " (as FOC)";
                    }

                    if(shortCodeStr.equalsIgnoreCase("FUXIN")) {
                        priceValue = fourDecimalPoint(Double.parseDouble(invoice.getPricevalue()));
                        totalValue = fourDecimalPoint(Double.parseDouble(invoice.getTotal()));
                    } else {
                        if (shortCodeStr.equalsIgnoreCase("SUPERSTAR")) {
                            priceValue = fourDecimalPoint(Double.parseDouble(invoice.getPricevalue()));
                            totalValue = twoDecimalPoint(Double.parseDouble(invoice.getTotal()));
                        }
                        else{
                            priceValue = twoDecimalPoint(Double.parseDouble(invoice.getPricevalue()));
                            totalValue = twoDecimalPoint(Double.parseDouble(invoice.getTotal()));
                        }
                    }

                    if (invoice.getUomCode() != null && !invoice.getUomCode().equals("null") && !invoice.getUomCode().isEmpty()) {
                        uomCode = "(" + invoice.getUomCode() + ")";
                    }

                    String[] itemString = new String[]{String.valueOf(index), productName + uomCode,
                            invoice.getNetQty(), priceValue, totalValue + "\n"};
                    int[] colsWidthItem = new int[]{2, 7, 4, 4, 4};
                    int[] colsAlignItem = new int[]{0, 0, 1, 1, 1};
                    int[] colsSizeItem = new int[]{20, 17, 20, 20, 20};
                    PrinterHelper.getInstance().printColumnsString(itemString, colsWidthItem, colsAlignItem,
                            colsSizeItem, null);

                    index++;
                }
                PrinterHelper.getInstance().printAndFeedPaper(20);

                if (printLineSmall != null) {
                    //  PrinterHelper.getInstance().setBitmapWidth(AlignmentUtils.getLineWidth());
                    PrinterHelper.getInstance().printBitmapWithAlign(printLineSmall, 1, null);
                } else {
                    PrinterHelper.getInstance().printTextBitmap("----------------------------------", null);
                }
                if (company_code.equals("SUPERSTAR TRADERS PTE LTD")) {
                    PrinterHelper.getInstance().printAndFeedPaper(10);
                    PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);

                    String[] titleTicket = new String[]{"Sub Total :$ " , twoDecimalPoint(Double.parseDouble(invoiceHeaderDetails.get(0).getSubTotal()))};
                    int[] colsWidthTicket = new int[]{5, 5};
                    int[] colsAlignTicket = new int[]{2, 2};
                    int[] colsSizeTicket = new int[]{20, 20};
                    PrinterHelper.getInstance().printColumnsString(titleTicket, colsWidthTicket, colsAlignTicket, colsSizeTicket, null);
                    PrinterHelper.getInstance().printAndFeedPaper(5);

                    String[] titleGst = new String[]{"GST(" + invoiceHeaderDetails.get(0).getTaxType() + ":" + (int) Double.parseDouble(invoiceHeaderDetails.get(0).getTaxValue()) + " % ):$ "
                            , invoiceHeaderDetails.get(0).getNetTax()};
                    int[] colsWidthGst = new int[]{5, 5};
                    int[] colsAlignGst = new int[]{2, 2};
                    int[] colsSizeGst = new int[]{20, 20};
                    PrinterHelper.getInstance().printColumnsString(titleGst, colsWidthGst, colsAlignGst, colsSizeGst, null);

                    PrinterHelper.getInstance().printAndFeedPaper(15);
                    PrinterHelper.getInstance().setTextBitmapStyle(Typeface.BOLD);
                    String[] titleNet = new String[]{"GRAND TOTAL :" +twoDecimalPoint(Double.parseDouble(invoiceHeaderDetails.get(0).getNetTotal()))};
                    int[] colsWidthNet = new int[]{5, 5};
                    int[] colsAlignNet = new int[]{2, 2};
                    int[] colsSizeNet = new int[]{20, 23};
                    PrinterHelper.getInstance().printColumnsString(titleNet, colsWidthNet, colsAlignNet, colsSizeNet, null);

                }
                else{

                    if(shortCodeStr.equalsIgnoreCase("FUXIN")){
                        if (invoiceHeaderDetails.get(0).getTaxType().equals("I")) {
                            PrinterHelper.getInstance().printAndFeedPaper(10);
                            PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);

                            String[] titleTicket = new String[]{"Sub Total :$ " , fourDecimalPoint(Double.parseDouble(invoiceHeaderDetails.get(0).getSubTotal()))};
                            int[] colsWidthTicket = new int[]{5, 5};
                            int[] colsAlignTicket = new int[]{2, 2};
                            int[] colsSizeTicket = new int[]{20, 20};
                            PrinterHelper.getInstance().printColumnsString(titleTicket, colsWidthTicket, colsAlignTicket, colsSizeTicket, null);
                            PrinterHelper.getInstance().printAndFeedPaper(5);

                            if (!invoiceHeaderDetails.get(0).getBillDiscount().isEmpty() &&
                                    invoiceHeaderDetails.get(0).getBillDiscount() != null &&
                                    Double.parseDouble(invoiceHeaderDetails.get(0).getBillDiscount()) > 0.00) {
                                String[] titleTickets = new String[]{"BILL DISC : $ " , fourDecimalPoint(Double.parseDouble(invoiceHeaderDetails.get(0).getBillDiscount()))};
                                int[] colsWidthTickets = new int[]{5, 5};
                                int[] colsAlignTickets = new int[]{2, 2};
                                int[] colsSizeTickets = new int[]{20, 20};
                                PrinterHelper.getInstance().printColumnsString(titleTickets, colsWidthTickets, colsAlignTickets, colsSizeTickets, null);
                                PrinterHelper.getInstance().printAndFeedPaper(5);
                            }

                            String[] titleGst = new String[]{"GST(" + invoiceHeaderDetails.get(0).getTaxType() + ":" + (int) Double.parseDouble(invoiceHeaderDetails.get(0).getTaxValue()) + " % ):$ "
                                    ,  fourDecimalPoint(Double.parseDouble(invoiceHeaderDetails.get(0).getNetTax()))};
                            int[] colsWidthGst = new int[]{5, 5};
                            int[] colsAlignGst = new int[]{2, 2};
                            int[] colsSizeGst = new int[]{20, 20};
                            PrinterHelper.getInstance().printColumnsString(titleGst, colsWidthGst, colsAlignGst, colsSizeGst, null);

                            PrinterHelper.getInstance().printAndFeedPaper(15);
                            PrinterHelper.getInstance().setTextBitmapStyle(Typeface.BOLD);
                            String[] titleNet = new String[]{"GRAND TOTAL :" +fourDecimalPoint(Double.parseDouble(invoiceHeaderDetails.get(0).getNetTotal()))};
                            int[] colsWidthNet = new int[]{5, 5};
                            int[] colsAlignNet = new int[]{2, 2};
                            int[] colsSizeNet = new int[]{20, 23};
                            PrinterHelper.getInstance().printColumnsString(titleNet, colsWidthNet, colsAlignNet, colsSizeNet, null);

                        }else{
                            PrinterHelper.getInstance().printAndFeedPaper(15);
                            PrinterHelper.getInstance().setTextBitmapStyle(Typeface.BOLD);
                            String[] titleNet = new String[]{"GRAND TOTAL :" +twoDecimalPoint(Double.parseDouble(invoiceHeaderDetails.get(0).getNetTotal()))};
                            int[] colsWidthNet = new int[]{5, 5};
                            int[] colsAlignNet = new int[]{2, 2};
                            int[] colsSizeNet = new int[]{20, 23};
                            PrinterHelper.getInstance().printColumnsString(titleNet, colsWidthNet, colsAlignNet, colsSizeNet, null);

                            String[] titleGst = new String[]{"GST(" + invoiceHeaderDetails.get(0).getTaxType() + ":" + (int) Double.parseDouble(invoiceHeaderDetails.get(0).getTaxValue()) + " % ):$ "
                                    ,  twoDecimalPoint(Double.parseDouble(invoiceHeaderDetails.get(0).getNetTax()))};
                            int[] colsWidthGst = new int[]{5, 5};
                            int[] colsAlignGst = new int[]{2, 2};
                            int[] colsSizeGst = new int[]{20, 20};
                            PrinterHelper.getInstance().printColumnsString(titleGst, colsWidthGst, colsAlignGst, colsSizeGst, null);

                        }
                    }
                }

                PrinterHelper.getInstance().printAndFeedPaper(10);
                if (printLineSmall != null) {
                    //  PrinterHelper.getInstance().setBitmapWidth(AlignmentUtils.getLineWidth());
                    PrinterHelper.getInstance().printBitmapWithAlign(printLineSmall, 1, null);
                } else {
                    PrinterHelper.getInstance().printTextBitmap("----------------------------------", null);
                }

                    if (invoiceHeaderDetails.get(0).getOutStandingAmount() != null &&
                            !invoiceHeaderDetails.get(0).getOutStandingAmount().isEmpty() &&
                            !invoiceHeaderDetails.get(0).getOutStandingAmount().equals("null")) {

                        PrinterHelper.getInstance().printAndFeedPaper(10);
                        PrinterHelper.getInstance().setTextBitmapStyle(Typeface.BOLD);
                        String[] titleout = new String[]{"TOTAL OUTSTANDING : $ "  , twoDecimalPoint(Double.parseDouble(invoiceHeaderDetails.get(0).getOutStandingAmount()))};
                        int[] colsWidthout = new int[]{10, 5};
                        int[] colsAlignout = new int[]{2, 2};
                        int[] colsSizeout = new int[]{21, 23};
                        PrinterHelper.getInstance().printColumnsString(titleout, colsWidthout, colsAlignout, colsSizeout, null);

                        PrinterHelper.getInstance().printAndFeedPaper(10);
                        if (printLineSmall != null) {
                            //  PrinterHelper.getInstance().setBitmapWidth(AlignmentUtils.getLineWidth());
                            PrinterHelper.getInstance().printBitmapWithAlign(printLineSmall, 1, null);
                        } else {
                            PrinterHelper.getInstance().printTextBitmap("----------------------------------", null);
                        }
                }
                if (latLongLoc.equals("true")) {
                    if (invoiceHeaderDetails.get(0).getCurrentAddress() != null &&
                            !invoiceHeaderDetails.get(0).getCurrentAddress().isEmpty()) {
                        PrinterHelper.getInstance().printAndFeedPaper(10);
                        PrinterHelper.getInstance().setTextBitmapSize(12);
                        PrinterHelper.getInstance().setCodeAlignment(1);
                        PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
                        PrinterHelper.getInstance().printText("Current Addr : " + invoiceHeaderDetails.get(0).getCurrentAddress() + "\n", null);
                    }
                    }
                if (payNow != null && !payNow.isEmpty()) {
                    PrinterHelper.getInstance().printAndFeedPaper(5);

                    PrinterHelper.getInstance().setTextBitmapSize(12);
                    PrinterHelper.getInstance().setCodeAlignment(1);
                    PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
                    PrinterHelper.getInstance().printText("Pay Now : " + payNow , null);

                }
                if (bankCode != null && !bankCode.isEmpty()) {
                    PrinterHelper.getInstance().printAndFeedPaper(5);

                    PrinterHelper.getInstance().setTextBitmapSize(12);
                    PrinterHelper.getInstance().setCodeAlignment(1);
                    PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
                    PrinterHelper.getInstance().printText("Transfer Bank : " + bankCode, null);
                }
                if (cheque != null && !cheque.isEmpty()) {
                    PrinterHelper.getInstance().printAndFeedPaper(5);

                    PrinterHelper.getInstance().setTextBitmapSize(12);
                    PrinterHelper.getInstance().setCodeAlignment(1);
                    PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
                    PrinterHelper.getInstance().printText("Account : " + cheque , null);
                }


                if (salesManName != null && !salesManName.isEmpty()) {
                    PrinterHelper.getInstance().printAndFeedPaper(5);

                    PrinterHelper.getInstance().setTextBitmapSize(12);
                    PrinterHelper.getInstance().setCodeAlignment(1);
                    PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
                    PrinterHelper.getInstance().printText("Contact : " + salesManName +" "+userMiddleName+ "/" + salesManPhone , null);
                }
                if (salesManMail != null && !salesManMail.isEmpty()) {
                    PrinterHelper.getInstance().printAndFeedPaper(5);

                    PrinterHelper.getInstance().setTextBitmapSize(12);
                    PrinterHelper.getInstance().setCodeAlignment(1);
                    PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
                    PrinterHelper.getInstance().printText("Email : " + salesManMail , null);
                }
                if (company_phone != null && !company_phone.isEmpty()) {
                    PrinterHelper.getInstance().printAndFeedPaper(5);

                    PrinterHelper.getInstance().setTextBitmapSize(12);
                    PrinterHelper.getInstance().setCodeAlignment(1);
                    PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
                    PrinterHelper.getInstance().printText("Sales Office  : " + company_phone , null);
                }

                PrinterHelper.getInstance().printAndFeedPaper(5);

                    if (showSignature.equals("true")) {
                        PrinterHelper.getInstance().setTextBitmapSize(17);
                        PrinterHelper.getInstance().setCodeAlignment(0);
                        PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
                        PrinterHelper.getInstance().printText("Customer Signature & Company Stamp", null);
                        PrinterHelper.getInstance().printAndFeedPaper(5);

                        if (Utils.getSignature() != null && !Utils.getSignature().isEmpty() && Utils.getSignature().contains("base64")) {

                            Bitmap signature = getSignature(Utils.getSignature());
                            PrinterHelper.getInstance().printBitmapWithAlign(signature, 1, null);
                        } else {
                            PrinterHelper.getInstance().printAndFeedPaper(150);
                        }

                        if (printLineSmall != null) {
                            //  PrinterHelper.getInstance().setBitmapWidth(AlignmentUtils.getLineWidth());
                            PrinterHelper.getInstance().printBitmapWithAlign(printLineSmall, 1, null);
                        } else {
                            PrinterHelper.getInstance().printTextBitmap("----------------------------------", null);
                        }
                        PrinterHelper.getInstance().printAndFeedPaper(5);
//                            String filePath = Constants.getSignatureFolderPath(context);
//                            String fileName = "Signature.jpg";
//                        //    File mFile = new File(filePath, fileName);
//                            Bitmap bitmapSign = BitmapFactory.decodeFile(filePath);

                         }

            }

            if (showQrCode.equals("true")) {
                String filePath = Constants.getSignatureFolderPath(context);
                String fileName = "PayNow.jpg";
                File mFile = new File(filePath, fileName);
                if (mFile.exists()) {
                    PrinterHelper.getInstance().printBarCodeWithAlign(mFile.getAbsolutePath(), 4, 0, null);
                }
            }
                if (printLineSmall != null) {
                    //  PrinterHelper.getInstance().setBitmapWidth(AlignmentUtils.getLineWidth());
                    PrinterHelper.getInstance().printBitmapWithAlign(printLineSmall, 1, null);
                } else {
                    PrinterHelper.getInstance().printTextBitmap("----------------------------------", null);
                }
                PrinterHelper.getInstance().printAndFeedPaper(5);


                if (showStamp.equals("true")) {

                    double balance = Double.parseDouble(invoiceHeaderDetails.get(0).getBalanceAmount());
                    if (balance > 0.00) {
//                        Bitmap bitmap1 = Glide.with(context).asBitmap()
//                                .load(R.drawable.unpaid_new)
//                                .submit(100, 100).get();
//                        PrinterHelper.getInstance().printBitmapWithAlign(bitmap1, 1, null);
                        Bitmap unpaidStamp = Utils.getStamp(context, R.drawable.unpaid_new);
                        PrinterHelper.getInstance().printBitmapWithAlign(unpaidStamp, 1, null);
                    } else {
                        Bitmap paidStamp = Utils.getStamp(context, R.drawable.paid_new);
                        PrinterHelper.getInstance().printBitmapWithAlign(paidStamp, 1, null);
                    }
                    if (printLineSmall != null) {
                        //  PrinterHelper.getInstance().setBitmapWidth(AlignmentUtils.getLineWidth());
                        PrinterHelper.getInstance().printBitmap(printLineSmall, null);
                    } else {
                        PrinterHelper.getInstance().printTextBitmap("----------------------------------", null);
                    }
                }
                if (isDoPrint.equals("true")) {
                    printDeliveryOrder(copy, invoiceHeaderDetails, invoiceList);
                }

                PrinterHelper.getInstance().printAndFeedPaper(100);
//            PrinterHelper.getInstance().partialCut();
//            PrinterHelper.getInstance().printAndLineFeed();

                Log.w("PrintInvoiceSuccesss:", "Success");

        } catch (Exception exception) {
            Log.w("Error in Printing", exception.getMessage().toString());
        }
        //  }
//            }
//        }).start();
    }


    public void printDeliveryOrder(int copy, ArrayList<InvoicePrintPreviewModel> invoiceHeaderDetails,
                                   ArrayList<InvoicePrintPreviewModel.InvoiceList> invoiceList) {
        try {
            for (int i = 0; i < copy; i++) {
                Log.w("deliverOrderInv", "");
                PrinterHelper.getInstance().setTextBitmapSize(25);
                PrinterHelper.getInstance().setTextBitmapTypeface("Typeface.DEFAULT_BOLD");
                PrinterHelper.getInstance().printTextBitmapWithAli(company_name, 1, null);
                PrinterHelper.getInstance().printAndFeedPaper(10);

                printCompanyDetails();

                if (company_gst != null && !company_gst.isEmpty()) {
                    PrinterHelper.getInstance().setTextBitmapSize(20);
                    PrinterHelper.getInstance().setCodeAlignment(1);
                    PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
                    PrinterHelper.getInstance().printText(" GST REG NO  : " + company_gst + "\n", null);
                }
                PrinterHelper.getInstance().printAndFeedPaper(20);

                PrinterHelper.getInstance().setTextBitmapSize(25);
                PrinterHelper.getInstance().setTextBitmapTypeface("Typeface.DEFAULT");
                PrinterHelper.getInstance().printTextBitmapWithAli("Delivery Order", 1, null);

                PrinterHelper.getInstance().printAndFeedPaper(20);

                PrinterHelper.getInstance().setTextBitmapStyle(Typeface.BOLD);
                String[] bilDateString = new String[]{"DO No :", invoiceHeaderDetails.get(0).getInvoiceNumber()};
                int[] colsWidthArr3 = new int[]{5, 10};
                int[] colsAlign3 = new int[]{0, 0};
                int[] colsSize3 = new int[]{22, 20};
                PrinterHelper.getInstance().printColumnsString(bilDateString, colsWidthArr3, colsAlign3, colsSize3, null);

                PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
                String[] bilDateTime = new String[]{"Date :", invoiceHeaderDetails.get(0).getInvoiceDate()};
                int[] colsWidthArr3DateTime = new int[]{5, 10};
                int[] colsAlign3DateTime = new int[]{0, 0};
                int[] colsSize3DateTime = new int[]{22, 20};
                PrinterHelper.getInstance().printColumnsString(bilDateTime, colsWidthArr3DateTime, colsAlign3DateTime, colsSize3DateTime, null);

                String[] tableId = new String[]{"Cus Code :", invoiceHeaderDetails.get(0).getCustomerCode()};
                int[] colsWidth = new int[]{5, 10};
                int[] colsAlign = new int[]{0, 0};
                int[] colsSize = new int[]{22, 20};
                PrinterHelper.getInstance().printColumnsString(tableId, colsWidth, colsAlign, colsSize, null);

                String[] tableIdString = new String[]{"Cus Name :", invoiceHeaderDetails.get(0).getCustomerName()};
                int[] colsWidthTable = new int[]{5, 10};
                int[] colsAlignTable = new int[]{0, 0};
                int[] colsSizeTable = new int[]{22, 20};
                PrinterHelper.getInstance().printColumnsString(tableIdString, colsWidthTable, colsAlignTable,
                        colsSizeTable, null);

                if (invoiceHeaderDetails.get(0).getAddress() != null &&
                        !invoiceHeaderDetails.get(0).getAddress().isEmpty()) {
                    String[] tableIdString1 = new String[]{"ADDR :", invoiceHeaderDetails.get(0).getAddress()};
                    int[] colsWidthTable1 = new int[]{5, 10};
                    int[] colsAlignTable1 = new int[]{0, 0};
                    int[] colsSizeTable1 = new int[]{22, 20};
                    PrinterHelper.getInstance().printColumnsString(tableIdString1, colsWidthTable1, colsAlignTable1,
                            colsSizeTable1, null);
                }

                String[] tableIdString5 = new String[]{"User :", userName};
                int[] colsWidthTable5 = new int[]{5, 10};
                int[] colsAlignTable5 = new int[]{0, 0};
                int[] colsSizeTable5 = new int[]{22, 20};
                PrinterHelper.getInstance().printColumnsString(tableIdString5, colsWidthTable5, colsAlignTable5,
                        colsSizeTable5, null);
                PrinterHelper.getInstance().printAndFeedPaper(10);

                if (printLineSmall != null) {
                    PrinterHelper.getInstance().printBitmapWithAlign(printLineSmall, 1, null);
                } else {
                    PrinterHelper.getInstance().printTextBitmap("----------------------------------", null);
                }
                PrinterHelper.getInstance().printAndFeedPaper(5);

                PrinterHelper.getInstance().setTextBitmapStyle(Typeface.BOLD);
                String[] titleString = new String[]{"Sn", "Desc", "Qty", "Price", "Total"};
                int[] colsWidthTitle = new int[]{2, 7, 4, 4, 4};
                int[] colsAlignTitle = new int[]{0, 0, 1, 1, 1};
                int[] colsSizeTitle = new int[]{22, 22, 22, 22, 22};
                PrinterHelper.getInstance().printColumnsString(titleString, colsWidthTitle,
                        colsAlignTitle, colsSizeTitle, null);

                PrinterHelper.getInstance().printAndFeedPaper(5);
                if (printLineSmall != null) {
                    PrinterHelper.getInstance().printBitmapWithAlign(printLineSmall, 1, null);
                } else {
                    PrinterHelper.getInstance().printTextBitmap("----------------------------------", null);
                }

                PrinterHelper.getInstance().printAndFeedPaper(5);
                PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
                int index = 1;
                double exQty = 0.0;
                double cqty = 0.0;
                double overallItemDiscount = 0.0;

                for (InvoicePrintPreviewModel.InvoiceList model : invoiceList) {
                    // Toast.makeText(context, "invoiceListPrint1 " + invoiceList.size(), Toast.LENGTH_SHORT).show();

                    String productName = "";
                    String priceValue = "0.00";
                    String uomCode = "";

                    if (model.getSaleType().equals("Cqty") || model.getSaleType().equals("Lqty")) {
                        productName = model.getDescription();
                    } else if (model.getSaleType().equals("Return")) {
                        productName = model.getDescription() + " (as Ret)";
                    } else if (model.getSaleType().equals("FOC")) {
                        productName = model.getDescription() + " (as FOC)";
                    }

                    if (model.getUomCode() != null && !model.getUomCode().equals("null") && !model.getUomCode().isEmpty()) {
                        uomCode = "(" + model.getUomCode() + ")";
                    }

                    String[] itemString = new String[]{String.valueOf(index), productName + uomCode,
                            model.getNetQty(), model.getPricevalue(),
                            twoDecimalPoint(Double.parseDouble(model.getTotal())) + "\n"};
                    int[] colsWidthItem = new int[]{2, 7, 4, 4, 4};
                    int[] colsAlignItem = new int[]{0, 0, 1, 1, 1};
                    int[] colsSizeItem = new int[]{20, 17, 20, 20, 20};
                    PrinterHelper.getInstance().printColumnsString(itemString, colsWidthItem, colsAlignItem,
                            colsSizeItem, null);

                    if (model.getCqty() != null && !model.getCqty().isEmpty() && !model.getCqty().equals("0.0") && !model.getCqty().equals("0.00")) {
                        if (Double.parseDouble(model.getCqty()) > 0.0) {
                            cqty += Double.parseDouble(model.getCqty());
                        }
                    }
                    qtyVal += (Double.parseDouble(model.getNetQty()));

                    index++;
                }
                PrinterHelper.getInstance().printAndFeedPaper(10);

                if (printLineSmall != null) {
                    PrinterHelper.getInstance().printBitmapWithAlign(printLineSmall, 1, null);
                } else {
                    PrinterHelper.getInstance().printTextBitmap("----------------------------------", null);
                }
                PrinterHelper.getInstance().printAndFeedPaper(10);

                PrinterHelper.getInstance().setTextBitmapStyle(Typeface.BOLD);
                String[] tableIdString6 = new String[]{"Total Qty :", twoDecimalPoint(qtyVal)};
                int[] colsWidthTable6 = new int[]{5, 10};
                int[] colsAlignTable6 = new int[]{2, 2};
                int[] colsSizeTable6 = new int[]{22, 22};
                PrinterHelper.getInstance().printColumnsString(tableIdString6, colsWidthTable6, colsAlignTable6,
                        colsSizeTable6, null);

                if (showSignature.equals("true")) {
//                    try {
//                        if (Utils.getSignature() != null && !Utils.getSignature().isEmpty()) {
//
//                            String filePath = Constants.getSignatureFolderPath(context);
//                            String fileName = "Signature.jpg";
//                            File mFile = new File(filePath, fileName);
//                            if (mFile.exists()) {
////
//                                PrinterHelper.getInstance().printBarCodeWithAlign(mFile.getAbsolutePath(),
//                                        4, 0, null);
//                            }
//                            Log.d("cg_signature_data", Utils.getSignature());
////                            TscDll.sendbitmap_resize(0,y, ImageUtil.convertBase64toBitmap(Utils.getSignature()),
////                                    400,80);
//                         } else {
//                    PrinterHelper.getInstance().printAndFeedPaper(150);
//                }
//                    } catch (Exception e) {
//                        Log.d("cg_signature_err", e.getLocalizedMessage());
//                    }
                    }

                if (printLineSmall != null) {
                    //  PrinterHelper.getInstance().setBitmapWidth(AlignmentUtils.getLineWidth());
                    PrinterHelper.getInstance().printBitmapWithAlign(printLineSmall, 1, null);
                } else {
                    PrinterHelper.getInstance().printTextBitmap("----------------------------------", null);
                }

                PrinterHelper.getInstance().printAndFeedPaper(100);

//            }
//        }).start();
            }
        } catch (Exception e) {
            Toast.makeText(context, "DOprint_error" + e.toString(), Toast.LENGTH_SHORT).show();

            Log.w("ErrorInPrinter::", e.toString());
        }
    }

    public void printSalesOrder(int copy, ArrayList<SalesOrderPrintPreviewModel> salesOrderHeaderDetails,
                                ArrayList<SalesOrderPrintPreviewModel.SalesList> salesOrderList) {
        try {
            for (int i = 0; i < copy; i++) {
                Toast.makeText(context, "SOimin1 " + salesOrderList.size(), Toast.LENGTH_SHORT).show();

                Log.w("salesOrder", "");
                PrinterHelper.getInstance().setTextBitmapSize(25);
                PrinterHelper.getInstance().setTextBitmapTypeface("Typeface.DEFAULT_BOLD");
                PrinterHelper.getInstance().printTextBitmapWithAli(company_name, 1, null);
                PrinterHelper.getInstance().printAndFeedPaper(10);

                printCompanyDetails();

                PrinterHelper.getInstance().setTextBitmapSize(18);
                PrinterHelper.getInstance().setCodeAlignment(1);
                PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
                PrinterHelper.getInstance().printText("GST REG NO  : " + company_gst + "\n", null);

                PrinterHelper.getInstance().printAndFeedPaper(20);

                if (company_code.equals("SUPERSTAR TRADERS PTE LTD")) {
                    if (Double.parseDouble(salesOrderHeaderDetails.get(0).getNetTax()) > 0) {
                        PrinterHelper.getInstance().setTextBitmapSize(25);
                        PrinterHelper.getInstance().setTextBitmapTypeface("Typeface.DEFAULT");
                        PrinterHelper.getInstance().printTextBitmapWithAli("Tax Invoice", 1, null);
                    } else {
                        PrinterHelper.getInstance().setTextBitmapSize(25);
                        PrinterHelper.getInstance().setTextBitmapTypeface("Typeface.DEFAULT");
                        PrinterHelper.getInstance().printTextBitmapWithAli("Invoice", 1, null);
                    }
                } else {
                    PrinterHelper.getInstance().setTextBitmapSize(25);
                    PrinterHelper.getInstance().setTextBitmapTypeface("Typeface.DEFAULT");
                    PrinterHelper.getInstance().printTextBitmapWithAli("Sales Order", 1, null);
                }

                PrinterHelper.getInstance().printAndFeedPaper(20);

                PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
                String[] bilDateString = new String[]{"SO No :", salesOrderHeaderDetails.get(0).getSoNumber()};
                int[] colsWidthArr3 = new int[]{5, 10};
                int[] colsAlign3 = new int[]{0, 0};
                int[] colsSize3 = new int[]{22, 20};
                PrinterHelper.getInstance().printColumnsString(bilDateString, colsWidthArr3, colsAlign3, colsSize3, null);

                String[] bilDateTime = new String[]{"SO Date :", salesOrderHeaderDetails.get(0).getSoDate()};
                int[] colsWidthArr3DateTime = new int[]{5, 10};
                int[] colsAlign3DateTime = new int[]{0, 0};
                int[] colsSize3DateTime = new int[]{22, 20};
                PrinterHelper.getInstance().printColumnsString(bilDateTime, colsWidthArr3DateTime, colsAlign3DateTime, colsSize3DateTime, null);

                String[] tableId = new String[]{"Cus Code :", salesOrderHeaderDetails.get(0).getCustomerCode()};
                int[] colsWidth = new int[]{5, 10};
                int[] colsAlign = new int[]{0, 0};
                int[] colsSize = new int[]{22, 20};
                PrinterHelper.getInstance().printColumnsString(tableId, colsWidth, colsAlign, colsSize, null);

                String[] tableIdString = new String[]{"Cus Name :", salesOrderHeaderDetails.get(0).getCustomerName()};
                int[] colsWidthTable = new int[]{5, 10};
                int[] colsAlignTable = new int[]{0, 0};
                int[] colsSizeTable = new int[]{22, 20};
                PrinterHelper.getInstance().printColumnsString(tableIdString, colsWidthTable, colsAlignTable,
                        colsSizeTable, null);

                if (salesOrderHeaderDetails.get(0).getAddress() != null &&
                        !salesOrderHeaderDetails.get(0).getAddress().isEmpty()) {
                    String[] tableIdString1 = new String[]{"ADDR :", salesOrderHeaderDetails.get(0).getAddress()};
                    int[] colsWidthTable1 = new int[]{5, 10};
                    int[] colsAlignTable1 = new int[]{0, 0};
                    int[] colsSizeTable1 = new int[]{22, 20};
                    PrinterHelper.getInstance().printColumnsString(tableIdString1, colsWidthTable1, colsAlignTable1,
                            colsSizeTable1, null);
                }

                if (salesOrderHeaderDetails.get(0).getAddress1() != null &&
                        !salesOrderHeaderDetails.get(0).getAddress1().isEmpty()) {
                    String[] tableIdString1 = new String[]{"", salesOrderHeaderDetails.get(0).getAddress1()};
                    int[] colsWidthTable1 = new int[]{5, 10};
                    int[] colsAlignTable1 = new int[]{0, 0};
                    int[] colsSizeTable1 = new int[]{22, 20};
                    PrinterHelper.getInstance().printColumnsString(tableIdString1, colsWidthTable1, colsAlignTable1,
                            colsSizeTable1, null);
                }

                if (salesOrderHeaderDetails.get(0).getAddress2() != null &&
                        !salesOrderHeaderDetails.get(0).getAddress2().isEmpty()) {
                    String[] tableIdString1 = new String[]{"", salesOrderHeaderDetails.get(0).getAddress2()};
                    int[] colsWidthTable1 = new int[]{5, 10};
                    int[] colsAlignTable1 = new int[]{0, 0};
                    int[] colsSizeTable1 = new int[]{22, 20};
                    PrinterHelper.getInstance().printColumnsString(tableIdString1, colsWidthTable1, colsAlignTable1,
                            colsSizeTable1, null);
                }

                if (salesOrderHeaderDetails.get(0).getAddress3() != null &&
                        !salesOrderHeaderDetails.get(0).getAddress3().isEmpty()) {
                    String[] tableIdString1 = new String[]{"", salesOrderHeaderDetails.get(0).getAddress3()};
                    int[] colsWidthTable1 = new int[]{5, 10};
                    int[] colsAlignTable1 = new int[]{0, 0};
                    int[] colsSizeTable1 = new int[]{22, 20};
                    PrinterHelper.getInstance().printColumnsString(tableIdString1, colsWidthTable1, colsAlignTable1,
                            colsSizeTable1, null);
                }
                if (salesOrderHeaderDetails.get(0).getAddressstate() != null &&
                        !salesOrderHeaderDetails.get(0).getAddressstate().isEmpty()) {
                    String[] tableIdString1 = new String[]{"", salesOrderHeaderDetails.get(0).getAddressstate()};
                    int[] colsWidthTable1 = new int[]{5, 10};
                    int[] colsAlignTable1 = new int[]{0, 0};
                    int[] colsSizeTable1 = new int[]{22, 20};
                    PrinterHelper.getInstance().printColumnsString(tableIdString1, colsWidthTable1, colsAlignTable1,
                            colsSizeTable1, null);
                }
                if (salesOrderHeaderDetails.get(0).getAddresssZipcode() != null &&
                        !salesOrderHeaderDetails.get(0).getAddresssZipcode().isEmpty()) {
                    String[] tableIdString1 = new String[]{"", salesOrderHeaderDetails.get(0).getAddresssZipcode()};
                    int[] colsWidthTable1 = new int[]{5, 10};
                    int[] colsAlignTable1 = new int[]{0, 0};
                    int[] colsSizeTable1 = new int[]{22, 20};
                    PrinterHelper.getInstance().printColumnsString(tableIdString1, colsWidthTable1, colsAlignTable1,
                            colsSizeTable1, null);
                }

                PrinterHelper.getInstance().printAndFeedPaper(5);
                PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);

                String[] tableIdString5 = new String[]{"User :", userName};
                int[] colsWidthTable5 = new int[]{5, 10};
                int[] colsAlignTable5 = new int[]{0, 0};
                int[] colsSizeTable5 = new int[]{22, 20};
                PrinterHelper.getInstance().printColumnsString(tableIdString5, colsWidthTable5, colsAlignTable5,
                        colsSizeTable5, null);
                PrinterHelper.getInstance().printAndFeedPaper(10);

                if (printLineSmall != null) {
                    PrinterHelper.getInstance().printBitmapWithAlign(printLineSmall, 1, null);
                } else {
                    PrinterHelper.getInstance().printTextBitmap("----------------------------------", null);
                }
                PrinterHelper.getInstance().printAndFeedPaper(5);

                PrinterHelper.getInstance().setTextBitmapStyle(Typeface.BOLD);
                String[] titleString = new String[]{"Sn", "Desc", "Qty", "Price", "Total"};
                int[] colsWidthTitle = new int[]{2, 7, 4, 4, 4};
                int[] colsAlignTitle = new int[]{0, 0, 1, 1, 1};
                int[] colsSizeTitle = new int[]{22, 22, 22, 22, 22};
                PrinterHelper.getInstance().printColumnsString(titleString, colsWidthTitle,
                        colsAlignTitle, colsSizeTitle, null);

                PrinterHelper.getInstance().printAndFeedPaper(5);
                if (printLineSmall != null) {
                    PrinterHelper.getInstance().printBitmapWithAlign(printLineSmall, 1, null);
                } else {
                    PrinterHelper.getInstance().printTextBitmap("----------------------------------", null);
                }

                PrinterHelper.getInstance().printAndFeedPaper(5);
                PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
                int index = 1;
                double exQty = 0.0;
                double cqty = 0.0;
                double overallItemDiscount = 0.0;

                for (SalesOrderPrintPreviewModel.SalesList salesOrder : salesOrderList) {
                    // Toast.makeText(context, "invoiceListPrint1 " + invoiceList.size(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "SOimin2 " + salesOrderList.size(), Toast.LENGTH_SHORT).show();

                    String productName = "";
                    String priceValue = "0.00";
                    String totalValue = "0.00";
                    String uomCode = "";

                    if (Double.parseDouble(twoDecimalPoint(Double.parseDouble(salesOrder.getTotal()))) == 0.00) {
                        productName = salesOrder.getDescription() + "-(as FOC)";
                    } else if (Double.parseDouble(twoDecimalPoint(Double.parseDouble(salesOrder.getTotal()))) > 0.00) {
                        productName = salesOrder.getDescription();
                    } else {
                        productName = salesOrder.getDescription() + "-(as RTN)";
                    }

                    if (shortCodeStr.equalsIgnoreCase("FUXIN")) {
                        priceValue = fourDecimalPoint(Double.parseDouble(salesOrder.getPricevalue()));
                        totalValue = fourDecimalPoint(Double.parseDouble(salesOrder.getTotal()));
                    } else {
                        if (shortCodeStr.equalsIgnoreCase("SUPERSTAR")) {
                            priceValue = fourDecimalPoint(Double.parseDouble(salesOrder.getPricevalue()));
                            totalValue = twoDecimalPoint(Double.parseDouble(salesOrder.getTotal()));
                        } else {
                            priceValue = twoDecimalPoint(Double.parseDouble(salesOrder.getPricevalue()));
                            totalValue = twoDecimalPoint(Double.parseDouble(salesOrder.getTotal()));
                        }
                    }

                    if (salesOrder.getUomCode() != null && !salesOrder.getUomCode().equals("null") && !salesOrder.getUomCode().isEmpty()) {
                        uomCode = "(" + salesOrder.getUomCode() + ")";
                    }

                    String[] itemString = new String[]{String.valueOf(index), productName + uomCode,
                            salesOrder.getNetQty(), priceValue, totalValue + "\n"};
                    int[] colsWidthItem = new int[]{2, 7, 4, 4, 4};
                    int[] colsAlignItem = new int[]{0, 0, 1, 1, 1};
                    int[] colsSizeItem = new int[]{20, 17, 20, 20, 20};
                    PrinterHelper.getInstance().printColumnsString(itemString, colsWidthItem, colsAlignItem,
                            colsSizeItem, null);

                    index++;
                }
                PrinterHelper.getInstance().printAndFeedPaper(20);

                if (printLineSmall != null) {
                    //  PrinterHelper.getInstance().setBitmapWidth(AlignmentUtils.getLineWidth());
                    PrinterHelper.getInstance().printBitmapWithAlign(printLineSmall, 1, null);
                } else {
                    PrinterHelper.getInstance().printTextBitmap("----------------------------------", null);
                }
                if (shortCodeStr.equalsIgnoreCase("FUXIN")) {
                    PrinterHelper.getInstance().printAndFeedPaper(10);
                    PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);

                    String[] titleTicket = new String[]{"Sub Total :$ ", fourDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getSubTotal()))};
                    int[] colsWidthTicket = new int[]{5, 5};
                    int[] colsAlignTicket = new int[]{2, 2};
                    int[] colsSizeTicket = new int[]{20, 20};
                    PrinterHelper.getInstance().printColumnsString(titleTicket, colsWidthTicket, colsAlignTicket, colsSizeTicket, null);
                    PrinterHelper.getInstance().printAndFeedPaper(5);


                    String[] titleGst = new String[]{"GST(" + salesOrderHeaderDetails.get(0).getTaxType() + ":" +
                            (int) Double.parseDouble(salesOrderHeaderDetails.get(0).getTaxValue()) + " % ):$ "
                            , fourDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getNetTax()))};
                    int[] colsWidthGst = new int[]{5, 5};
                    int[] colsAlignGst = new int[]{2, 2};
                    int[] colsSizeGst = new int[]{20, 20};
                    PrinterHelper.getInstance().printColumnsString(titleGst, colsWidthGst, colsAlignGst, colsSizeGst, null);

                    PrinterHelper.getInstance().printAndFeedPaper(15);
                    PrinterHelper.getInstance().setTextBitmapStyle(Typeface.BOLD);
                    String[] titleNet = new String[]{"GRAND TOTAL :" + fourDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getNetTotal()))};
                    int[] colsWidthNet = new int[]{5, 5};
                    int[] colsAlignNet = new int[]{2, 2};
                    int[] colsSizeNet = new int[]{20, 23};
                    PrinterHelper.getInstance().printColumnsString(titleNet, colsWidthNet, colsAlignNet, colsSizeNet, null);
                } else {
                    PrinterHelper.getInstance().printAndFeedPaper(10);
                    PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);

                    String[] titleTicket = new String[]{"Sub Total :$ ", twoDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getSubTotal()))};
                    int[] colsWidthTicket = new int[]{5, 5};
                    int[] colsAlignTicket = new int[]{2, 2};
                    int[] colsSizeTicket = new int[]{20, 20};
                    PrinterHelper.getInstance().printColumnsString(titleTicket, colsWidthTicket, colsAlignTicket, colsSizeTicket, null);
                    PrinterHelper.getInstance().printAndFeedPaper(5);

                    String[] titleGst = new String[]{"GST(" + salesOrderHeaderDetails.get(0).getTaxType() + ":" + (int) Double.parseDouble(salesOrderHeaderDetails.get(0).getTaxValue()) + " % ):$ "
                            , salesOrderHeaderDetails.get(0).getNetTax()};
                    int[] colsWidthGst = new int[]{5, 5};
                    int[] colsAlignGst = new int[]{2, 2};
                    int[] colsSizeGst = new int[]{20, 20};
                    PrinterHelper.getInstance().printColumnsString(titleGst, colsWidthGst, colsAlignGst, colsSizeGst, null);

                    PrinterHelper.getInstance().printAndFeedPaper(15);
                    PrinterHelper.getInstance().setTextBitmapStyle(Typeface.BOLD);
                    String[] titleNet = new String[]{"GRAND TOTAL :" + twoDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getNetTotal()))};
                    int[] colsWidthNet = new int[]{5, 5};
                    int[] colsAlignNet = new int[]{2, 2};
                    int[] colsSizeNet = new int[]{20, 23};
                    PrinterHelper.getInstance().printColumnsString(titleNet, colsWidthNet, colsAlignNet, colsSizeNet, null);

                }

                PrinterHelper.getInstance().printAndFeedPaper(10);
                if (printLineSmall != null) {
                    //  PrinterHelper.getInstance().setBitmapWidth(AlignmentUtils.getLineWidth());
                    PrinterHelper.getInstance().printBitmapWithAlign(printLineSmall, 1, null);
                } else {
                    PrinterHelper.getInstance().printTextBitmap("----------------------------------", null);
                }

                if (salesOrderHeaderDetails.get(0).getOutStandingAmount() != null &&
                        !salesOrderHeaderDetails.get(0).getOutStandingAmount().isEmpty() &&
                        !salesOrderHeaderDetails.get(0).getOutStandingAmount().equals("null")) {

                    PrinterHelper.getInstance().printAndFeedPaper(10);
                    PrinterHelper.getInstance().setTextBitmapStyle(Typeface.BOLD);
                    String[] titleout = new String[]{"TOTAL OUTSTANDING : $ ", twoDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getOutStandingAmount()))};
                    int[] colsWidthout = new int[]{10, 5};
                    int[] colsAlignout = new int[]{2, 2};
                    int[] colsSizeout = new int[]{21, 23};
                    PrinterHelper.getInstance().printColumnsString(titleout, colsWidthout, colsAlignout, colsSizeout, null);

                    PrinterHelper.getInstance().printAndFeedPaper(10);
                    if (printLineSmall != null) {
                        //  PrinterHelper.getInstance().setBitmapWidth(AlignmentUtils.getLineWidth());
                        PrinterHelper.getInstance().printBitmapWithAlign(printLineSmall, 1, null);
                    } else {
                        PrinterHelper.getInstance().printTextBitmap("----------------------------------", null);
                    }
                }

                PrinterHelper.getInstance().printAndFeedPaper(10);
                if (showSignature.equals("true")) {
                    PrinterHelper.getInstance().setTextBitmapSize(17);
                    PrinterHelper.getInstance().setCodeAlignment(0);
                    PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
                    PrinterHelper.getInstance().printText("Customer Signature & Company Stamp", null);
                    PrinterHelper.getInstance().printAndFeedPaper(5);

                    if (Utils.getSignature() != null && !Utils.getSignature().isEmpty() && Utils.getSignature().contains("base64")) {

                        Bitmap signature = getSignature(Utils.getSignature());
                        PrinterHelper.getInstance().printBitmapWithAlign(signature, 1, null);
                    } else {
                        PrinterHelper.getInstance().printAndFeedPaper(150);
                    }

                    if (printLineSmall != null) {
                        //  PrinterHelper.getInstance().setBitmapWidth(AlignmentUtils.getLineWidth());
                        PrinterHelper.getInstance().printBitmapWithAlign(printLineSmall, 1, null);
                    } else {
                        PrinterHelper.getInstance().printTextBitmap("----------------------------------", null);
                    }
                    PrinterHelper.getInstance().printAndFeedPaper(5);
//                            String filePath = Constants.getSignatureFolderPath(context);
//                            String fileName = "Signature.jpg";
//                        //    File mFile = new File(filePath, fileName);
//                            Bitmap bitmapSign = BitmapFactory.decodeFile(filePath);

                }

                if (printLineSmall != null) {
                    //  PrinterHelper.getInstance().setBitmapWidth(AlignmentUtils.getLineWidth());
                    PrinterHelper.getInstance().printBitmap(printLineSmall, null);
                } else {
                    PrinterHelper.getInstance().printTextBitmap("----------------------------------", null);
                }
            }
            PrinterHelper.getInstance().printAndFeedPaper(100);
//            PrinterHelper.getInstance().partialCut();
//            PrinterHelper.getInstance().printAndLineFeed();

                Log.w("PrintSuccesss:", "Success");

        } catch (Exception exception) {
            Log.w("Error in Printing", exception.getMessage().toString());
        }
        //  }
//            }
//        }).start();
    }
    public void printReceipts(int copy, ArrayList<ReceiptPrintPreviewModel> receiptHeaderDetails,
                              ArrayList<ReceiptPrintPreviewModel.ReceiptsDetails> receiptList) {
        try {
            for (int i = 0; i < copy; i++) {
                Log.w("receipt", "");
                PrinterHelper.getInstance().setTextBitmapSize(25);
                PrinterHelper.getInstance().setTextBitmapTypeface("Typeface.DEFAULT_BOLD");
                PrinterHelper.getInstance().printTextBitmapWithAli(company_name, 1, null);
                PrinterHelper.getInstance().printAndFeedPaper(10);

                printCompanyDetails();

                PrinterHelper.getInstance().setTextBitmapSize(20);
                PrinterHelper.getInstance().setCodeAlignment(1);
                PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
                PrinterHelper.getInstance().printText("CO REG NO: " + company_gst + "\n", null);

                PrinterHelper.getInstance().printAndFeedPaper(20);

                PrinterHelper.getInstance().setTextBitmapSize(25);
                PrinterHelper.getInstance().setTextBitmapTypeface("Typeface.DEFAULT");
                PrinterHelper.getInstance().printTextBitmapWithAli("Receipts", 1, null);

                PrinterHelper.getInstance().printAndFeedPaper(20);

                PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
                String[] bilDateString = new String[]{"Receipt No: ", receiptHeaderDetails.get(0).getReceiptNumber()};
                int[] colsWidthArr3 = new int[]{5, 10};
                int[] colsAlign3 = new int[]{0, 0};
                int[] colsSize3 = new int[]{22, 20};
                PrinterHelper.getInstance().printColumnsString(bilDateString, colsWidthArr3, colsAlign3, colsSize3, null);


                PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
                String[] bilDateTime = new String[]{"Date :", receiptHeaderDetails.get(0).getReceiptDate()};
                int[] colsWidthArr3DateTime = new int[]{5, 10};
                int[] colsAlign3DateTime = new int[]{0, 0};
                int[] colsSize3DateTime = new int[]{22, 20};
                PrinterHelper.getInstance().printColumnsString(bilDateTime, colsWidthArr3DateTime, colsAlign3DateTime, colsSize3DateTime, null);

                PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
                String[] tableId = new String[]{"Cus Code :", receiptHeaderDetails.get(0).getCustomerCode()};
                int[] colsWidth = new int[]{5, 10};
                int[] colsAlign = new int[]{0, 0};
                int[] colsSize = new int[]{22, 20};
                PrinterHelper.getInstance().printColumnsString(tableId, colsWidth, colsAlign, colsSize, null);

                String[] tableIdString = new String[]{"Cus Name :", receiptHeaderDetails.get(0).getCustomerName()};
                int[] colsWidthTable = new int[]{5, 10};
                int[] colsAlignTable = new int[]{0, 0};
                int[] colsSizeTable = new int[]{22, 20};
                PrinterHelper.getInstance().printColumnsString(tableIdString, colsWidthTable, colsAlignTable,
                        colsSizeTable, null);

                PrinterHelper.getInstance().printAndFeedPaper(10);
                PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
                String[] tableIdString5 = new String[]{"User :", userName};
                int[] colsWidthTable5 = new int[]{5, 10};
                int[] colsAlignTable5 = new int[]{0, 0};
                int[] colsSizeTable5 = new int[]{22, 20};
                PrinterHelper.getInstance().printColumnsString(tableIdString5, colsWidthTable5, colsAlignTable5,
                        colsSizeTable5, null);

                PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
                String[] tableIdString2 = new String[]{"Pay Mode :", receiptHeaderDetails.get(0).getPayMode()};
                int[] colsWidthTable2 = new int[]{5, 10};
                int[] colsAlignTable2 = new int[]{0, 0};
                int[] colsSizeTable2 = new int[]{22, 20};
                PrinterHelper.getInstance().printColumnsString(tableIdString2, colsWidthTable2, colsAlignTable2,
                        colsSizeTable2, null);

                PrinterHelper.getInstance().printAndFeedPaper(10);

                if (receiptHeaderDetails.get(0).getAddress1() != null &&
                        !receiptHeaderDetails.get(0).getAddress1().isEmpty()) {
                    String[] tableIdString1 = new String[]{"", receiptHeaderDetails.get(0).getAddress1()};
                    int[] colsWidthTable1 = new int[]{5, 10};
                    int[] colsAlignTable1 = new int[]{0, 0};
                    int[] colsSizeTable1 = new int[]{22, 20};
                    PrinterHelper.getInstance().printColumnsString(tableIdString1, colsWidthTable1, colsAlignTable1,
                            colsSizeTable1, null);
                }

                if (receiptHeaderDetails.get(0).getAddress2() != null &&
                        !receiptHeaderDetails.get(0).getAddress2().isEmpty()) {
                    String[] tableIdString1 = new String[]{"", receiptHeaderDetails.get(0).getAddress2()};
                    int[] colsWidthTable1 = new int[]{5, 10};
                    int[] colsAlignTable1 = new int[]{0, 0};
                    int[] colsSizeTable1 = new int[]{22, 20};
                    PrinterHelper.getInstance().printColumnsString(tableIdString1, colsWidthTable1, colsAlignTable1,
                            colsSizeTable1, null);
                }

                if (receiptHeaderDetails.get(0).getAddress3() != null &&
                        !receiptHeaderDetails.get(0).getAddress3().isEmpty()) {
                    String[] tableIdString1 = new String[]{"", receiptHeaderDetails.get(0).getAddress3()};
                    int[] colsWidthTable1 = new int[]{5, 10};
                    int[] colsAlignTable1 = new int[]{0, 0};
                    int[] colsSizeTable1 = new int[]{22, 20};
                    PrinterHelper.getInstance().printColumnsString(tableIdString1, colsWidthTable1, colsAlignTable1,
                            colsSizeTable1, null);
                }
                if (receiptHeaderDetails.get(0).getAddressstate() != null &&
                        !receiptHeaderDetails.get(0).getAddressstate().isEmpty()) {
                    String[] tableIdString1 = new String[]{"", receiptHeaderDetails.get(0).getAddressstate()};
                    int[] colsWidthTable1 = new int[]{5, 10};
                    int[] colsAlignTable1 = new int[]{0, 0};
                    int[] colsSizeTable1 = new int[]{22, 20};
                    PrinterHelper.getInstance().printColumnsString(tableIdString1, colsWidthTable1, colsAlignTable1,
                            colsSizeTable1, null);
                }
                if (receiptHeaderDetails.get(0).getAddresssZipcode() != null &&
                        !receiptHeaderDetails.get(0).getAddresssZipcode().isEmpty()) {
                    String[] tableIdString1 = new String[]{"", receiptHeaderDetails.get(0).getAddresssZipcode()};
                    int[] colsWidthTable1 = new int[]{5, 10};
                    int[] colsAlignTable1 = new int[]{0, 0};
                    int[] colsSizeTable1 = new int[]{22, 20};
                    PrinterHelper.getInstance().printColumnsString(tableIdString1, colsWidthTable1, colsAlignTable1,
                            colsSizeTable1, null);
                }

                if (printLineSmall != null) {
                    PrinterHelper.getInstance().printBitmapWithAlign(printLineSmall, 1, null);
                } else {
                    PrinterHelper.getInstance().printTextBitmap("----------------------------------", null);
                }
                PrinterHelper.getInstance().printAndFeedPaper(5);

                String[] titleString = new String[]{"Sno", "Inv-No", "Inv-Date", "Amount"};
                int[] colsWidthTitle = new int[]{2, 7, 5, 4};
                int[] colsAlignTitle = new int[]{0, 0, 1, 1};
                int[] colsSizeTitle = new int[]{20, 20, 20, 20};
                PrinterHelper.getInstance().printColumnsString(titleString, colsWidthTitle, colsAlignTitle, colsSizeTitle, null);

                PrinterHelper.getInstance().printAndFeedPaper(5);
                if (printLineSmall != null) {
                    PrinterHelper.getInstance().printBitmapWithAlign(printLineSmall, 1, null);
                } else {
                    PrinterHelper.getInstance().printTextBitmap("----------------------------------", null);
                }

                PrinterHelper.getInstance().printAndFeedPaper(5);
                PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);

                int index = 1;
                double sum_paidamt = 0.0;
                double sum_balanceamt = 0.0;
                double sum_SRpaid = 0.00;

                for (ReceiptPrintPreviewModel.ReceiptsDetails receiptsDetails : receiptList) {

                    String[] itemString = new String[]{String.valueOf(index), receiptsDetails.getInvoiceNumber(),
                            receiptsDetails.getInvoiceDate(), twoDecimalPoint(Double.parseDouble(receiptsDetails.getAmount())) + "\n"};
                    int[] colsWidthItem = new int[]{2, 7, 5, 4};
                    int[] colsAlignItem = new int[]{0, 0, 1, 1};
                    int[] colsSizeItem = new int[]{20, 18, 20, 20};
                    PrinterHelper.getInstance().printColumnsString(itemString, colsWidthItem, colsAlignItem,
                            colsSizeItem, null);

                    PrinterHelper.getInstance().setTextBitmapSize(15);
                    PrinterHelper.getInstance().setCodeAlignment(1);
                    PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
                    PrinterHelper.getInstance().printText("Disc : " + twoDecimalPoint(Double.parseDouble(receiptsDetails.getDiscountAmount())) , null);

                    PrinterHelper.getInstance().printAndFeedPaper(5);
                    String[] itemString1 = new String[]{"Paid Amt : " + twoDecimalPoint(Double.parseDouble(receiptsDetails.getAmount())) ,
                            "Bal Amt: " + twoDecimalPoint(Double.parseDouble(receiptsDetails.getBalanceAmount()))+ "\n"};
                    int[] colsWidthItem1 = new int[]{8, 8};
                    int[] colsAlignItem1 = new int[]{0, 0};
                    int[] colsSizeItem1 = new int[]{20, 20};
                    PrinterHelper.getInstance().printColumnsString(itemString1, colsWidthItem1, colsAlignItem1,
                            colsSizeItem1, null);

//                    PrinterHelper.getInstance().printAndFeedPaper(5);
//                    PrinterHelper.getInstance().printAndFeedPaper(10);
//                    PrinterHelper.getInstance().setTextBitmapSize(10);
//                    PrinterHelper.getInstance().setCodeAlignment(0);
//                    PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
//                    PrinterHelper.getInstance().printText("Paid Amt : " + twoDecimalPoint(Double.parseDouble(receiptsDetails.getAmount())), null);
//
//                    PrinterHelper.getInstance().printAndFeedPaper(5);
//                    PrinterHelper.getInstance().setTextBitmapSize(10);
//                    PrinterHelper.getInstance().setCodeAlignment(0);
//                    PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
//                    PrinterHelper.getInstance().printText("Bal Amt: " + twoDecimalPoint(Double.parseDouble(receiptsDetails.getBalanceAmount())), null);

                    PrinterHelper.getInstance().printAndFeedPaper(5);
                    PrinterHelper.getInstance().setTextBitmapSize(10);
                    PrinterHelper.getInstance().setCodeAlignment(0);
                    PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
                    PrinterHelper.getInstance().printText("Credit Amt : " + twoDecimalPoint(Double.parseDouble(receiptsDetails.getDiscountAmount())), null);

                    sum_paidamt += Double.parseDouble(receiptsDetails.getAmount());
                    sum_balanceamt += Double.parseDouble(receiptsDetails.getBalanceAmount());

                    index++;
                }

                PrinterHelper.getInstance().printAndFeedPaper(10);
                if (printLineSmall != null) {
                    PrinterHelper.getInstance().printBitmapWithAlign(printLineSmall, 1, null);
                } else {
                    PrinterHelper.getInstance().printTextBitmap("----------------------------------", null);
                }

                if (receiptHeaderDetails.get(0).getPayMode().equalsIgnoreCase("Cheque") ||
                        receiptHeaderDetails.get(0).getPayMode().equalsIgnoreCase("Transfer")
                        || receiptHeaderDetails.get(0).getPayMode().equalsIgnoreCase("GIRO")) {

                    PrinterHelper.getInstance().printAndFeedPaper(10);
                    PrinterHelper.getInstance().setTextBitmapSize(20);
                    PrinterHelper.getInstance().setCodeAlignment(0);
                    PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
                    PrinterHelper.getInstance().printText("Bank Code : " + receiptHeaderDetails.get(0).getBankCode(), null);

                    PrinterHelper.getInstance().setTextBitmapSize(20);
                    PrinterHelper.getInstance().setCodeAlignment(0);
                    PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
                    PrinterHelper.getInstance().printText("Cheque No : " + receiptHeaderDetails.get(0).getChequeNo(), null);

                    PrinterHelper.getInstance().setTextBitmapSize(20);
                    PrinterHelper.getInstance().setCodeAlignment(0);
                    PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
                    PrinterHelper.getInstance().printText("Cheque Date : " + receiptHeaderDetails.get(0).getChequeDate(), null);

                    if (printLineSmall != null) {
                        PrinterHelper.getInstance().printBitmapWithAlign(printLineSmall, 1, null);
                    } else {
                        PrinterHelper.getInstance().printTextBitmap("----------------------------------", null);
                    }
                }

                PrinterHelper.getInstance().printAndFeedPaper(10);
                PrinterHelper.getInstance().setTextBitmapStyle(Typeface.BOLD);
                String[] titleNet = new String[]{"NET PAID AMT : $ " , twoDecimalPoint(sum_paidamt)};
                int[] colsWidthNet = new int[]{10, 5};
                int[] colsAlignNet = new int[]{2, 2};
                int[] colsSizeNet = new int[]{20, 20};
                PrinterHelper.getInstance().printColumnsString(titleNet, colsWidthNet, colsAlignNet, colsSizeNet, null);

                PrinterHelper.getInstance().printAndFeedPaper(5);
                PrinterHelper.getInstance().setTextBitmapStyle(Typeface.BOLD);
                String[] titleNet1 = new String[]{"NET BALANCE : $ " , twoDecimalPoint(sum_balanceamt)};
                int[] colsWidthNet1 = new int[]{10, 5};
                int[] colsAlignNet1 = new int[]{2, 2};
                int[] colsSizeNet1 = new int[]{20, 20};
                PrinterHelper.getInstance().printColumnsString(titleNet1, colsWidthNet1, colsAlignNet1, colsSizeNet1, null);

                PrinterHelper.getInstance().printAndFeedPaper(10);
                if (printLineSmall != null) {
                    PrinterHelper.getInstance().printBitmapWithAlign(printLineSmall, 1, null);
                } else {
                    PrinterHelper.getInstance().printTextBitmap("----------------------------------", null);
                }
                PrinterHelper.getInstance().printAndFeedPaper(5);

                PrinterHelper.getInstance().setTextBitmapSize(10);
                PrinterHelper.getInstance().setCodeAlignment(0);
                PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
                PrinterHelper.getInstance().printText("Customer Signature & Company Stamp", null);

                PrinterHelper.getInstance().printAndFeedPaper(120);

                if (printLineSmall != null) {
                    //  PrinterHelper.getInstance().setBitmapWidth(AlignmentUtils.getLineWidth());
                    PrinterHelper.getInstance().printBitmapWithAlign(printLineSmall, 1, null);
                } else {
                    PrinterHelper.getInstance().printTextBitmap("----------------------------------", null);
                }
                PrinterHelper.getInstance().printAndFeedPaper(5);

                PrinterHelper.getInstance().setTextBitmapSize(10);
                PrinterHelper.getInstance().setCodeAlignment(0);
                PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
                PrinterHelper.getInstance().printText("RECEIVED ABOVE GOODS IN GOOD ORDER AND CONDITION", null);
                PrinterHelper.getInstance().printAndFeedPaper(5);

                if (printLineSmall != null) {
                    //  PrinterHelper.getInstance().setBitmapWidth(AlignmentUtils.getLineWidth());
                    PrinterHelper.getInstance().printBitmapWithAlign(printLineSmall, 1, null);
                } else {
                    PrinterHelper.getInstance().printTextBitmap("----------------------------------", null);
                }
                PrinterHelper.getInstance().printAndFeedPaper(100);

            }
        } catch (Exception e) {
            Log.w("ErrorInPrinter::", e.toString());
        }

    }
    public void printSalesReturn(int copy, ArrayList<SalesReturnPrintPreviewModel> salesReturnHeader,
                                 ArrayList<SalesReturnPrintPreviewModel.SalesReturnDetails> salesReturnList) {
        try {
            for (int i = 0; i < copy; i++) {

                PrinterHelper.getInstance().setTextBitmapSize(25);
                PrinterHelper.getInstance().setTextBitmapTypeface("Typeface.DEFAULT_BOLD");
                PrinterHelper.getInstance().printTextBitmapWithAli(company_name, 1, null);
                PrinterHelper.getInstance().printAndFeedPaper(10);

                printCompanyDetails();

                if (company_gst != null && !company_gst.isEmpty()) {
                    PrinterHelper.getInstance().setTextBitmapSize(20);
                    PrinterHelper.getInstance().setCodeAlignment(1);
                    PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
                    PrinterHelper.getInstance().printText("CO REG NO: " + company_gst + "\n", null);
                }
                PrinterHelper.getInstance().printAndFeedPaper(20);

                PrinterHelper.getInstance().setTextBitmapSize(25);
                PrinterHelper.getInstance().setTextBitmapTypeface("Typeface.DEFAULT");
                PrinterHelper.getInstance().printTextBitmapWithAli("Sales Return", 1, null);

                PrinterHelper.getInstance().printAndFeedPaper(20);

                PrinterHelper.getInstance().setTextBitmapStyle(Typeface.BOLD);
                String[] bilDateString = new String[]{"SR No :", salesReturnHeader.get(0).getSrNo()};
                int[] colsWidthArr3 = new int[]{5, 10};
                int[] colsAlign3 = new int[]{0, 0};
                int[] colsSize3 = new int[]{22, 20};
                PrinterHelper.getInstance().printColumnsString(bilDateString, colsWidthArr3, colsAlign3, colsSize3, null);

                PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
                String[] bilDateTime = new String[]{"Date :", salesReturnHeader.get(0).getSrDate()};
                int[] colsWidthArr3DateTime = new int[]{5, 10};
                int[] colsAlign3DateTime = new int[]{0, 0};
                int[] colsSize3DateTime = new int[]{22, 20};
                PrinterHelper.getInstance().printColumnsString(bilDateTime, colsWidthArr3DateTime, colsAlign3DateTime, colsSize3DateTime, null);

                String[] tableId = new String[]{"Cus Code :", salesReturnHeader.get(0).getCustomerCode()};
                int[] colsWidth = new int[]{5, 10};
                int[] colsAlign = new int[]{0, 0};
                int[] colsSize = new int[]{22, 20};
                PrinterHelper.getInstance().printColumnsString(tableId, colsWidth, colsAlign, colsSize, null);

                String[] tableIdString = new String[]{"Cus Name :", salesReturnHeader.get(0).getCustomerName()};
                int[] colsWidthTable = new int[]{5, 10};
                int[] colsAlignTable = new int[]{0, 0};
                int[] colsSizeTable = new int[]{22, 20};
                PrinterHelper.getInstance().printColumnsString(tableIdString, colsWidthTable, colsAlignTable,
                        colsSizeTable, null);

                String[] tableIdString5 = new String[]{"User :", userName};
                int[] colsWidthTable5 = new int[]{5, 10};
                int[] colsAlignTable5 = new int[]{0, 0};
                int[] colsSizeTable5 = new int[]{22, 20};
                PrinterHelper.getInstance().printColumnsString(tableIdString5, colsWidthTable5, colsAlignTable5,
                        colsSizeTable5, null);
                PrinterHelper.getInstance().printAndFeedPaper(10);

                if (salesReturnHeader.get(0).getAddress1() != null &&
                        !salesReturnHeader.get(0).getAddress1().isEmpty()) {
                    String[] tableIdString1 = new String[]{"", salesReturnHeader.get(0).getAddress1()};
                    int[] colsWidthTable1 = new int[]{5, 10};
                    int[] colsAlignTable1 = new int[]{0, 0};
                    int[] colsSizeTable1 = new int[]{22, 20};
                    PrinterHelper.getInstance().printColumnsString(tableIdString1, colsWidthTable1, colsAlignTable1,
                            colsSizeTable1, null);
                }

                if (salesReturnHeader.get(0).getAddress2() != null &&
                        !salesReturnHeader.get(0).getAddress2().isEmpty()) {
                    String[] tableIdString1 = new String[]{"", salesReturnHeader.get(0).getAddress2()};
                    int[] colsWidthTable1 = new int[]{5, 10};
                    int[] colsAlignTable1 = new int[]{0, 0};
                    int[] colsSizeTable1 = new int[]{22, 20};
                    PrinterHelper.getInstance().printColumnsString(tableIdString1, colsWidthTable1, colsAlignTable1,
                            colsSizeTable1, null);
                }

                if (salesReturnHeader.get(0).getAddress3() != null &&
                        !salesReturnHeader.get(0).getAddress3().isEmpty()) {
                    String[] tableIdString1 = new String[]{"", salesReturnHeader.get(0).getAddress3()};
                    int[] colsWidthTable1 = new int[]{5, 10};
                    int[] colsAlignTable1 = new int[]{0, 0};
                    int[] colsSizeTable1 = new int[]{22, 20};
                    PrinterHelper.getInstance().printColumnsString(tableIdString1, colsWidthTable1, colsAlignTable1,
                            colsSizeTable1, null);
                }
                if (salesReturnHeader.get(0).getAddressstate() != null &&
                        !salesReturnHeader.get(0).getAddressstate().isEmpty()) {
                    String[] tableIdString1 = new String[]{"", salesReturnHeader.get(0).getAddressstate()};
                    int[] colsWidthTable1 = new int[]{5, 10};
                    int[] colsAlignTable1 = new int[]{0, 0};
                    int[] colsSizeTable1 = new int[]{22, 20};
                    PrinterHelper.getInstance().printColumnsString(tableIdString1, colsWidthTable1, colsAlignTable1,
                            colsSizeTable1, null);
                }
                if (salesReturnHeader.get(0).getAddresssZipcode() != null &&
                        !salesReturnHeader.get(0).getAddresssZipcode().isEmpty()) {
                    String[] tableIdString1 = new String[]{"", salesReturnHeader.get(0).getAddresssZipcode()};
                    int[] colsWidthTable1 = new int[]{5, 10};
                    int[] colsAlignTable1 = new int[]{0, 0};
                    int[] colsSizeTable1 = new int[]{22, 20};
                    PrinterHelper.getInstance().printColumnsString(tableIdString1, colsWidthTable1, colsAlignTable1,
                            colsSizeTable1, null);
                }

                PrinterHelper.getInstance().printAndFeedPaper(10);

                if (printLineSmall != null) {
                    PrinterHelper.getInstance().printBitmapWithAlign(printLineSmall, 1, null);
                } else {
                    PrinterHelper.getInstance().printTextBitmap("----------------------------------", null);
                }
                PrinterHelper.getInstance().printAndFeedPaper(5);

                PrinterHelper.getInstance().setTextBitmapStyle(Typeface.BOLD);
                String[] titleString = new String[]{"Sn", "Desc", "Qty", "Price", "Total"};
                int[] colsWidthTitle = new int[]{2, 7, 4, 4, 4};
                int[] colsAlignTitle = new int[]{0, 0, 1, 1, 1};
                int[] colsSizeTitle = new int[]{22, 22, 22, 22, 22};
                PrinterHelper.getInstance().printColumnsString(titleString, colsWidthTitle,
                        colsAlignTitle, colsSizeTitle, null);

                PrinterHelper.getInstance().printAndFeedPaper(5);
                if (printLineSmall != null) {
                    PrinterHelper.getInstance().printBitmapWithAlign(printLineSmall, 1, null);
                } else {
                    PrinterHelper.getInstance().printTextBitmap("----------------------------------", null);
                }
                PrinterHelper.getInstance().printAndFeedPaper(5);

                PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
                int index = 1;
                double net_qty = 0.0;
                int sum = 0;

                PrinterHelper.getInstance().printAndFeedPaper(10);
                PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);

                for (SalesReturnPrintPreviewModel.SalesReturnDetails salesReturnDetails : salesReturnList) {
                    String uomCode = "";

                    if (salesReturnDetails.getUomCode() != null && !salesReturnDetails.getUomCode().equals("null") &&
                            !salesReturnDetails.getUomCode().isEmpty()) {
                        uomCode = "(" + salesReturnDetails.getUomCode() + ")";
                    }

                    String[] itemString = new String[]{String.valueOf(index), salesReturnDetails.getDescription() + uomCode,
                            salesReturnDetails.getNetqty(),
                            twoDecimalPoint(Double.parseDouble(salesReturnDetails.getPrice())),
                            twoDecimalPoint(Double.parseDouble(salesReturnDetails.getTotal())) + "\n"};
                    int[] colsWidthItem = new int[]{2, 7, 4, 4, 4};
                    int[] colsAlignItem = new int[]{0, 0, 1, 1, 1};
                    int[] colsSizeItem = new int[]{20, 17, 20, 20, 20};
                    PrinterHelper.getInstance().printColumnsString(itemString, colsWidthItem, colsAlignItem,
                            colsSizeItem, null);

                    index++;
                }
                PrinterHelper.getInstance().printAndFeedPaper(10);

                if (printLineSmall != null) {
                    PrinterHelper.getInstance().printBitmapWithAlign(printLineSmall, 1, null);
                } else {
                    PrinterHelper.getInstance().printTextBitmap("----------------------------------", null);
                }
                PrinterHelper.getInstance().printAndFeedPaper(10);

                String[] titleTicket = new String[]{"Sub Total $ :" ,
                        twoDecimalPoint(Double.parseDouble(salesReturnHeader.get(0).getSubTotal()))};
                int[] colsWidthTicket = new int[]{5, 5};
                int[] colsAlignTicket = new int[]{2, 2};
                int[] colsSizeTicket = new int[]{20, 20};
                PrinterHelper.getInstance().printColumnsString(titleTicket, colsWidthTicket, colsAlignTicket, colsSizeTicket, null);

                String[] titleGst = new String[]{"GST(" + salesReturnHeader.get(0).getTaxType() + ":" + salesReturnHeader.get(0).getTaxValue() + " %) :",
                        salesReturnHeader.get(0).getTax()};
                int[] colsWidthGst = new int[]{5, 5};
                int[] colsAlignGst = new int[]{2, 2};
                int[] colsSizeGst = new int[]{20, 20};
                PrinterHelper.getInstance().printColumnsString(titleGst, colsWidthGst, colsAlignGst, colsSizeGst, null);

                PrinterHelper.getInstance().printAndFeedPaper(15);
                PrinterHelper.getInstance().setTextBitmapStyle(Typeface.BOLD);
                String[] titleNet = new String[]{"GRAND TOTAL $ :" ,
                        twoDecimalPoint(Double.parseDouble(salesReturnHeader.get(0).getNetTotal()))};
                int[] colsWidthNet = new int[]{5, 5};
                int[] colsAlignNet = new int[]{2, 2};
                int[] colsSizeNet = new int[]{20, 23};
                PrinterHelper.getInstance().printColumnsString(titleNet, colsWidthNet, colsAlignNet, colsSizeNet, null);

                PrinterHelper.getInstance().printAndFeedPaper(10);
                if (printLineSmall != null) {
                    PrinterHelper.getInstance().printBitmapWithAlign(printLineSmall, 1, null);
                } else {
                    PrinterHelper.getInstance().printTextBitmap("----------------------------------", null);
                }
                PrinterHelper.getInstance().printAndFeedPaper(5);

                PrinterHelper.getInstance().setTextBitmapSize(17);
                PrinterHelper.getInstance().setCodeAlignment(0);
                PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
                PrinterHelper.getInstance().printText("Customer Signature & Company Stamp", null);

                PrinterHelper.getInstance().printAndFeedPaper(150);

                if (printLineSmall != null) {
                    //  PrinterHelper.getInstance().setBitmapWidth(AlignmentUtils.getLineWidth());
                    PrinterHelper.getInstance().printBitmapWithAlign(printLineSmall, 1, null);
                } else {
                    PrinterHelper.getInstance().printTextBitmap("----------------------------------", null);
                }
                PrinterHelper.getInstance().printAndFeedPaper(5);

                PrinterHelper.getInstance().setTextBitmapSize(16);
                PrinterHelper.getInstance().setCodeAlignment(0);
                PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
                PrinterHelper.getInstance().printText("RECEIVED ABOVE GOODS IN GOOD ORDER AND CONDITION", null);

                PrinterHelper.getInstance().printAndFeedPaper(5);

                if (printLineSmall != null) {
                    //  PrinterHelper.getInstance().setBitmapWidth(AlignmentUtils.getLineWidth());
                    PrinterHelper.getInstance().printBitmapWithAlign(printLineSmall, 1, null);
                } else {
                    PrinterHelper.getInstance().printTextBitmap("----------------------------------", null);
                }

                PrinterHelper.getInstance().printAndFeedPaper(100);
            }
        } catch (Exception e) {
            Log.w("ErrorInPrinter::", e.toString());
        }
    }

    public void printCompanyDetails() {
        if (company_address1 != null && !company_address1.isEmpty()) {
            PrinterHelper.getInstance().setTextBitmapSize(13);
            PrinterHelper.getInstance().setCodeAlignment(1);
            PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
            PrinterHelper.getInstance().printText(company_address1+"\n", null);
        }
        if (company_address2 != null && !company_address2.isEmpty()) {
            PrinterHelper.getInstance().setTextBitmapSize(13);
            PrinterHelper.getInstance().setCodeAlignment(1);
            PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
            PrinterHelper.getInstance().printText(company_address2+"\n", null);
        }
        if (company_address3 != null && !company_address3.isEmpty()) {
            PrinterHelper.getInstance().setTextBitmapSize(13);
            PrinterHelper.getInstance().setCodeAlignment(1);
            PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
            PrinterHelper.getInstance().printText(company_address3+"\n", null);
        }
        if (company_phone != null && !company_phone.isEmpty()) {
            PrinterHelper.getInstance().setTextBitmapSize(13);
            PrinterHelper.getInstance().setCodeAlignment(1);
            PrinterHelper.getInstance().setTextBitmapStyle(Typeface.NORMAL);
            PrinterHelper.getInstance().printText(" TEL : " + company_phone+"\n", null);
        }
    }

    public Bitmap getSignature(String encodedImage) {
        String base64Image = encodedImage.split(",")[1];
        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
