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

import android.os.Bundle
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pyamsoft.pasterino.R
import com.pyamsoft.pasterino.main.ActionViewEvent.ActionClicked
import com.pyamsoft.pydroid.core.bus.Publisher
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.ui.arch.UiView
import com.pyamsoft.pydroid.ui.util.popHide
import com.pyamsoft.pydroid.ui.util.popShow
import com.pyamsoft.pydroid.ui.util.setOnDebouncedClickListener

internal class MainActionView internal constructor(
  private val parent: ViewGroup,
  private val imageLoader: ImageLoader,
  private val owner: LifecycleOwner,
  publisher: Publisher<ActionViewEvent>
) : UiView<ActionViewEvent>(publisher) {

  private lateinit var actionButton: FloatingActionButton

  override fun id(): Int {
    return actionButton.id
  }

  override fun inflate(savedInstanceState: Bundle?) {
    parent.inflateAndAdd(R.layout.floating_action_button) {
      actionButton = findViewById(R.id.main_settings_fab)
    }
  }

  override fun saveState(outState: Bundle) {
  }

  override fun teardown() {
    actionButton.setOnDebouncedClickListener(null)
  }

  fun setFabFromServiceState(running: Boolean) {
    actionButton.setOnDebouncedClickListener {
      publish(ActionClicked(running))
    }

    val icon: Int
    if (running) {
      icon = R.drawable.ic_help_24dp
    } else {
      icon = R.drawable.ic_service_start_24dp
    }
    imageLoader.load(icon)
        .into(actionButton)
        .bind(owner)
  }

  fun toggleVisibility(visible: Boolean) {
    if (visible) {
      actionButton.popShow()
    } else {
      actionButton.popHide()
    }
  }

}