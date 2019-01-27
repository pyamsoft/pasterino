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

package com.pyamsoft.pasterino.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceScreen
import androidx.recyclerview.widget.RecyclerView
import com.pyamsoft.pasterino.R
import com.pyamsoft.pasterino.settings.SettingsViewEvent.ExplainClicked
import com.pyamsoft.pasterino.settings.SettingsViewEvent.SignificantScroll
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.ui.arch.PrefUiView
import com.pyamsoft.pydroid.ui.widget.scroll.HideOnScrollListener

internal class SettingsView internal constructor(
  private val recyclerView: RecyclerView,
  parent: PreferenceScreen,
  bus: EventBus<SettingsViewEvent>
) : PrefUiView<SettingsViewEvent>(parent, bus) {

  private val zaptorchExplain by lazyPref<Preference>(R.string.explain_key)

  private var scrollListener: RecyclerView.OnScrollListener? = null

  override fun inflate(savedInstanceState: Bundle?) {
    zaptorchExplain.setOnPreferenceClickListener {
      publish(ExplainClicked)
      return@setOnPreferenceClickListener true
    }

    val listener = HideOnScrollListener.create(true) {
      publish(SignificantScroll(it))
    }
    recyclerView.addOnScrollListener(listener)
    scrollListener = listener
  }

  override fun teardown() {
    zaptorchExplain.onPreferenceClickListener = null

    scrollListener?.also { recyclerView.removeOnScrollListener(it) }
    scrollListener = null
  }

}

