package com.pyamsoft.pasterino.main

import androidx.recyclerview.widget.RecyclerView
import com.pyamsoft.pydroid.ui.app.BaseView

interface SettingsView : BaseView {

  fun onExplainClicked(onClick: () -> Unit)

  fun addScrollListener(
    listView: RecyclerView,
    listener: RecyclerView.OnScrollListener
  )

}