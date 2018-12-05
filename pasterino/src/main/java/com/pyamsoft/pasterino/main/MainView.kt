package com.pyamsoft.pasterino.main

import com.pyamsoft.pydroid.ui.app.BaseScreen

interface MainView : BaseScreen {

  fun onToolbarNavClicked(onClick: () -> Unit)

}