package com.winapp.sapNetco.model

data class StockAdjustSaveModel(
    val DocDate: String,
    val GoodReceiveDetails: List<StockAdjustSaveDetail>
)