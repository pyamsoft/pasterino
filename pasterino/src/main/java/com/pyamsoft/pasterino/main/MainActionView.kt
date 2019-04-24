/*
 * Copyright 2019 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.pyamsoft.pasterino.main

import android.view.ViewGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pyamsoft.pasterino.R
import com.pyamsoft.pasterino.main.MainActionView.Callback
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.loader.Loaded
import com.pyamsoft.pydroid.ui.util.popHide
import com.pyamsoft.pydroid.ui.util.popShow
import com.pyamsoft.pydroid.ui.util.setOnDebouncedClickListener
import javax.inject.Inject

internal class MainActionView @Inject internal constructor(
  private val imageLoader: ImageLoader,
  parent: ViewGroup,
  callback: Callback
) : BaseUiView<Callback>(parent, callback) {

  private var actionIconLoaded: Loaded? = null

  override val layout: Int = R.layout.floating_action_button

  override val layoutRoot by boundView<FloatingActionButton>(R.id.main_settings_fab)

  override fun onTeardown() {
    layoutRoot.setOnDebouncedClickListener(null)
    actionIconLoaded?.dispose()
  }

  fun setFabFromServiceState(running: Boolean) {
    layoutRoot.setOnDebouncedClickListener {
      callback.onActionButtonClicked(running)
    }

    val icon: Int
    if (running) {
      icon = R.drawable.ic_help_24dp
    } else {
      icon = R.drawable.ic_service_start_24dp
    }
    actionIconLoaded?.dispose()
    actionIconLoaded = imageLoader.load(icon)
        .into(layoutRoot)
  }

  fun toggleVisibility(visible: Boolean) {
    if (visible) {
      layoutRoot.popShow()
    } else {
      layoutRoot.popHide()
    }
  }

  interface Callback {

    fun onActionButtonClicked(running: Boolean)

  }

}

