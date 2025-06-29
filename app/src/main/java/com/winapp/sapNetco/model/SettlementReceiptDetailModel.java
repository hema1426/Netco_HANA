package com.winapp.sapNetco.model;

import java.util.ArrayList;

public class SettlementReceiptDetailModel {
    // Define the variables for the Settlement
    private String receiptNo;
    private String receiptDate;
    private String customerName;
    private String customerCode;
    private String paidAmount;
    private String creditAmount;
    private String finalPaidAmount;
    private String paymode;
    private String bankCode;
    private String chequeNo;
    private String chequeDate;
    private ArrayList<invoiceDetailSettlement> invoiceDetailSettlementList;

    public ArrayList<invoiceDetailSettlement> getInvoiceDetailSettlementList() {
        return invoiceDetailSettlementList;
    }

    public void setInvoiceDetailSettlementList(ArrayList<invoiceDetailSettlement> invoiceDetailSettlementList) {
        this.invoiceDetailSettlementList = invoiceDetailSettlementList;
    }

    public String getReceiptNo() {
        return receiptNo;
    }

    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

    public String getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(String receiptDate) {
        this.receiptDate = receiptDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(String paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(String creditAmount) {
        this.creditAmount = creditAmount;
    }

    public String getFinalPaidAmount() {
        return finalPaidAmount;
    }

    public void setFinalPaidAmount(String finalPaidAmount) {
        this.finalPaidAmount = finalPaidAmount;
    }

    public String getPaymode() {
        return paymode;
    }

    public void setPaymode(String paymode) {
        this.paymode = paymode;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getChequeNo() {
        return chequeNo;
    }

    public void setChequeNo(String chequeNo) {
        this.chequeNo = chequeNo;
    }

    public String getChequeDate() {
        return chequeDate;
    }

    public void setChequeDate(String chequeDate) {
        this.chequeDate = chequeDate;
    }

    public static class invoiceDetailSettlement {

        // Define the variables for the Currency
        private String invoiceNo;
        private String invoiceDate;
        private String total;
        private String paidAmt;
        private String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getInvoiceNo() {
            return invoiceNo;
        }

        public void setInvoiceNo(String invoiceNo) {
            this.invoiceNo = invoiceNo;
        }

        public String getInvoiceDate() {
            return invoiceDate;
        }

        public void setInvoiceDate(String invoiceDate) {
            this.invoiceDate = invoiceDate;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public String getPaidAmt() {
            return paidAmt;
        }

        public void setPaidAmt(String paidAmt) {
            this.paidAmt = paidAmt;
        }
    }

}
