package com.winapp.sapNetco.model

data class StockAdjustSaveDetail(
    val BatchDetails: List<Any>,
    val ItemCode: String,
    val Price: String,
    val UomCode: String,
    val WarehouseCode: String,
    val qty: String
)