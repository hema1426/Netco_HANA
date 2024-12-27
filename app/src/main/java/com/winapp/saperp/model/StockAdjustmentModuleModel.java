package com.winapp.saperp.model;

import java.util.ArrayList;

public class StockAdjustmentModuleModel {

    private String number;
    private String code;
    private String date;
    private String netTotal;
    private String doStatus;
    private boolean isShow=false;
    private ArrayList<StockAdjustmentPreviewModel.StockAdjustList> stockAdjustList;



    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }


    public ArrayList<StockAdjustmentPreviewModel.StockAdjustList> getStockAdjustList() {
        return stockAdjustList;
    }

    public void setStockAdjustList(ArrayList<StockAdjustmentPreviewModel.StockAdjustList> stockAdjustList) {
        this.stockAdjustList = stockAdjustList;
    }
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDoStatus() {
        return doStatus;
    }

    public void setDoStatus(String doStatus) {
        this.doStatus = doStatus;
    }

    public String getNetTotal() {
        return netTotal;
    }

    public void setNetTotal(String netTotal) {
        this.netTotal = netTotal;
    }
}
