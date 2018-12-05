package com.pyamsoft.pasterino.model

import androidx.recyclerview.widget.RecyclerView

data class FabScrollListenerRequestEvent(
  val requestTag: String,
  val listenerResult: RecyclerView.OnScrollListener? = null
)