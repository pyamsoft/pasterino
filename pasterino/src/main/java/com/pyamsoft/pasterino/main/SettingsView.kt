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
import android.view.View
import androidx.annotation.CheckResult
import androidx.annotation.StringRes
import androidx.preference.Preference
import androidx.preference.PreferenceScreen
import androidx.recyclerview.widget.RecyclerView
import com.pyamsoft.pasterino.R
import com.pyamsoft.pasterino.main.SettingsViewEvent.ExplainClicked
import com.pyamsoft.pasterino.main.SettingsViewEvent.SignificantScroll
import com.pyamsoft.pydroid.core.bus.Publisher
import com.pyamsoft.pydroid.ui.arch.InvalidUiComponentIdException
import com.pyamsoft.pydroid.ui.arch.UiView
import com.pyamsoft.pydroid.ui.widget.scroll.HideOnScrollListener

internal class SettingsView internal constructor(
    // TODO: Remove view req from scroll listener
  private val view: View,
  private val preferenceScreen: PreferenceScreen,
  private val recyclerView: RecyclerView,
  bus: Publisher<SettingsViewEvent>
) : UiView<SettingsViewEvent>(bus) {

  private val context = preferenceScreen.context

  private lateinit var zaptorchExplain: Preference

  private var scrollListener: RecyclerView.OnScrollListener? = null

  override fun id(): Int {
    throw InvalidUiComponentIdException
  }

  override fun inflate(savedInstanceState: Bundle?) {
    zaptorchExplain = findPreference(R.string.explain_key)

    zaptorchExplain.setOnPreferenceClickListener {
      publish(ExplainClicked)
      return@setOnPreferenceClickListener true
    }

    val listener = HideOnScrollListener.withView(view) {
      publish(SignificantScroll(it))
    }
    recyclerView.addOnScrollListener(listener)
    scrollListener = listener
  }

  override fun saveState(outState: Bundle) {
  }

  override fun teardown() {
    zaptorchExplain.onPreferenceClickListener = null

    scrollListener?.also { recyclerView.removeOnScrollListener(it) }
    scrollListener = null
  }

  @CheckResult
  private fun findPreference(@StringRes id: Int): Preference {
    return preferenceScreen.findPreference(context.getString(id))
  }

}

