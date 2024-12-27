package com.winapp.saperp.model

data class StockAdjustSaveModel(
    val DocDate: String,
    val GoodReceiveDetails: List<StockAdjustSaveDetail>
)