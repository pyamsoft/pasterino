/*
 * Copyright 2020 Peter Kenji Yamanaka
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
 */

package com.pyamsoft.pasterino.settings

import android.os.Bundle
import android.view.View
import com.pyamsoft.pasterino.PasterinoComponent
import com.pyamsoft.pasterino.PasterinoViewModelFactory
import com.pyamsoft.pasterino.R
import com.pyamsoft.pasterino.widget.ToolbarView
import com.pyamsoft.pydroid.arch.StateSaver
import com.pyamsoft.pydroid.arch.UiController
import com.pyamsoft.pydroid.arch.UnitControllerEvent
import com.pyamsoft.pydroid.arch.UnitViewState
import com.pyamsoft.pydroid.arch.createComponent
import com.pyamsoft.pydroid.ui.Injector
import com.pyamsoft.pydroid.ui.app.requireToolbarActivity
import com.pyamsoft.pydroid.ui.arch.fromViewModelFactory
import com.pyamsoft.pydroid.ui.settings.AppSettingsPreferenceFragment
import javax.inject.Inject

class SettingsPreferenceFragment :
    AppSettingsPreferenceFragment(), UiController<UnitControllerEvent> {

  @JvmField @Inject internal var settingsView: SettingsView? = null

  @JvmField @Inject internal var spacer: SettingsSpacer? = null

  @JvmField @Inject internal var toolbarView: ToolbarView<UnitViewState, SettingsViewEvent>? = null

  @JvmField @Inject internal var factory: PasterinoViewModelFactory? = null
  private val viewModel by fromViewModelFactory<SettingsViewModel> { factory?.create(this) }

  private var stateSaver: StateSaver? = null

  override val preferenceXmlResId: Int = R.xml.preferences

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    Injector.obtainFromApplication<PasterinoComponent>(view.context)
        .plusSettingsComponent()
        .create(requireToolbarActivity(), listView, preferenceScreen)
        .inject(this)

    stateSaver =
        createComponent(
            savedInstanceState,
            viewLifecycleOwner,
            viewModel,
            this,
            requireNotNull(settingsView),
            requireNotNull(toolbarView),
            requireNotNull(spacer)) {
          return@createComponent when (it) {
            is SettingsViewEvent.SignificantScroll -> viewModel.handleSendScroll(it.visible)
          }
        }
  }

  override fun onControllerEvent(event: UnitControllerEvent) {}

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    stateSaver?.saveState(outState)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    settingsView = null
    toolbarView = null
    factory = null
    stateSaver = null
  }

  companion object {

    const val TAG = "SettingsPreferenceFragment"
  }
}
