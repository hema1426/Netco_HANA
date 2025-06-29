package com.winapp.sapNetco.model;

public class OrderHeader {
    public String orderId;
    public String invoiceNumber;
    public String mode;
    public String invoiceDate;
    public String customerCode;
    public String customerName;
    public String haveTax;
    public String taxType;
    public String taxPerc;
    public String taxCode;
    public String currencyRate;
    public String currencyName;
    public String customerReferenceNo;
    public String taxTotal;
    public String subTotal;
    public String total;
    public String netTotal;
    public String itemDiscount;
    public String billDiscount;
    public String currentDateTime;
    public String totalDiscount;
    public String deliveryCode;
    public String CurrencyRate;
    public String status;
    public String createUser;
    public String modifyUser;
    public String companyName;
    public String stockUpdated;
    public String invoiceType;
    public String companyCode;
    public String locationCode;
    public String latitude;
    public String longitude;
    public String CurrentAddress;
    public String signature;
    private boolean isShow=false;
    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getHaveTax() {
        return haveTax;
    }

    public void setHaveTax(String haveTax) {
        this.haveTax = haveTax;
    }

    public String getTaxType() {
        return taxType;
    }

    public void setTaxType(String taxType) {
        this.taxType = taxType;
    }

    public String getTaxPerc() {
        return taxPerc;
    }

    public void setTaxPerc(String taxPerc) {
        this.taxPerc = taxPerc;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public String getCurrencyRate() {
        return currencyRate;
    }

    public void setCurrencyRate(String currencyRate) {
        this.currencyRate = currencyRate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getStockUpdated() {
        return stockUpdated;
    }

    public void setStockUpdated(String stockUpdated) {
        this.stockUpdated = stockUpdated;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCurrentAddress() {
        return CurrentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        CurrentAddress = currentAddress;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getCustomerReferenceNo() {
        return customerReferenceNo;
    }

    public void setCustomerReferenceNo(String customerReferenceNo) {
        this.customerReferenceNo = customerReferenceNo;
    }

    public String getTaxTotal() {
        return taxTotal;
    }

    public void setTaxTotal(String taxTotal) {
        this.taxTotal = taxTotal;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getNetTotal() {
        return netTotal;
    }

    public void setNetTotal(String netTotal) {
        this.netTotal = netTotal;
    }

    public String getItemDiscount() {
        return itemDiscount;
    }

    public void setItemDiscount(String itemDiscount) {
        this.itemDiscount = itemDiscount;
    }

    public String getBillDiscount() {
        return billDiscount;
    }

    public void setBillDiscount(String billDiscount) {
        this.billDiscount = billDiscount;
    }

    public String getCurrentDateTime() {
        return currentDateTime;
    }

    public void setCurrentDateTime(String currentDateTime) {
        this.currentDateTime = currentDateTime;
    }

    public String getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(String totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public String getDeliveryCode() {
        return deliveryCode;
    }

    public void setDeliveryCode(String deliveryCode) {
        this.deliveryCode = deliveryCode;
    }
}
