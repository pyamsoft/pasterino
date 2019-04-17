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
import com.pyamsoft.pasterino.PasterinoComponent
import com.pyamsoft.pasterino.R
import com.pyamsoft.pasterino.service.PasteServiceNotification
import com.pyamsoft.pasterino.service.SinglePasteService
import com.pyamsoft.pasterino.widget.ToolbarView
import com.pyamsoft.pydroid.ui.Injector
import com.pyamsoft.pydroid.ui.app.requireToolbarActivity
import com.pyamsoft.pydroid.ui.settings.AppSettingsPreferenceFragment
import com.pyamsoft.pydroid.ui.util.show
import timber.log.Timber
import javax.inject.Inject

class SettingsPreferenceFragment : AppSettingsPreferenceFragment(),
    SettingsUiComponent.Callback {

  @field:Inject internal lateinit var toolbar: ToolbarView
  @field:Inject internal lateinit var component: SettingsUiComponent

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

    toolbar.inflate(savedInstanceState)
    component.bind(viewLifecycleOwner, savedInstanceState, this)
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    toolbar.saveState(outState)
    component.saveState(outState)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    toolbar.teardown()
  }

  override fun showHowTo() {
    HowToDialog().show(requireActivity(), "howto")
  }

  override fun onKillApplication() {
    requireContext().also {
      PasteServiceNotification.stop(it)
      SinglePasteService.stop(it)

      Timber.d("Clear application data")
      val activityManager = it.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
      activityManager.clearApplicationUserData()
    }
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