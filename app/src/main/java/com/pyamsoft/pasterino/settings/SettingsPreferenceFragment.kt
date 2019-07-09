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

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.pyamsoft.pasterino.PasterinoComponent
import com.pyamsoft.pasterino.R
import com.pyamsoft.pasterino.settings.SettingsControllerEvent.ClearAll
import com.pyamsoft.pasterino.settings.SettingsControllerEvent.Explain
import com.pyamsoft.pasterino.widget.ToolbarView
import com.pyamsoft.pydroid.arch.createComponent
import com.pyamsoft.pydroid.ui.Injector
import com.pyamsoft.pydroid.ui.app.requireToolbarActivity
import com.pyamsoft.pydroid.ui.arch.factory
import com.pyamsoft.pydroid.ui.settings.AppSettingsPreferenceFragment
import com.pyamsoft.pydroid.ui.util.show
import timber.log.Timber
import javax.inject.Inject

class SettingsPreferenceFragment : AppSettingsPreferenceFragment() {

  @JvmField @Inject internal var factory: ViewModelProvider.Factory? = null
  @JvmField @Inject internal var settingsView: SettingsView? = null
  @JvmField @Inject internal var toolbarView: ToolbarView<SettingsViewState, SettingsViewEvent>? =
    null
  private val viewModel by factory<SettingsViewModel> { factory }

  override val preferenceXmlResId: Int = R.xml.preferences

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    Injector.obtain<PasterinoComponent>(requireContext().applicationContext)
        .plusSettingsComponent()
        .create(viewLifecycleOwner, requireToolbarActivity(), listView, preferenceScreen)
        .inject(this)

    createComponent(
        savedInstanceState, viewLifecycleOwner,
        viewModel,
        requireNotNull(settingsView),
        requireNotNull(toolbarView)
    ) {
      return@createComponent when (it) {
        is Explain -> showHowTo()
        is ClearAll -> killApplication()
      }
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    toolbarView?.saveState(outState)
    settingsView?.saveState(outState)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    settingsView = null
    toolbarView = null
    factory = null
  }

  private fun killApplication() {
    requireContext().also {
      Timber.d("Clear application data")
      val activityManager = it.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
      activityManager.clearApplicationUserData()
    }
  }

  private fun showHowTo() {
    HowToDialog().show(requireActivity(), "howto")
  }

  override fun onClearAllClicked() {
    super.onClearAllClicked()
    ConfirmationDialog()
        .show(requireActivity(), "confirm")
  }

  companion object {

    const val TAG = "SettingsPreferenceFragment"
  }
}

