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

import androidx.preference.PreferenceScreen
import androidx.recyclerview.widget.RecyclerView
import com.pyamsoft.pasterino.api.MainInteractor
import com.pyamsoft.pasterino.service.ServiceFinishEvent
import com.pyamsoft.pasterino.service.ServiceFinishPresenterImpl
import com.pyamsoft.pasterino.widget.ToolbarView
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.ui.app.requireToolbarActivity

internal class SettingsComponentImpl internal constructor(
  private val recyclerView: RecyclerView,
  private val preferenceScreen: PreferenceScreen,
  private val interactor: MainInteractor,
  private val clearAllBus: EventBus<ClearAllEvent>,
  private val significantScrollBus: EventBus<SignificantScrollEvent>,
  private val serviceFinishBus: EventBus<ServiceFinishEvent>
) : SettingsComponent {

  override fun inject(fragment: MainSettingsPreferenceFragment) {
    val settingsPresenter = SettingsPresenterImpl(significantScrollBus)
    val view = SettingsView(
        fragment.viewLifecycleOwner, recyclerView, preferenceScreen, settingsPresenter
    )

    fragment.apply {
      this.clearPresenter = ClearAllPresenterImpl(interactor, clearAllBus)
      this.serviceFinishPresenter = ServiceFinishPresenterImpl(serviceFinishBus)
      this.toolbarView = ToolbarView(requireToolbarActivity())
      this.presenter = settingsPresenter
      this.settingsView = view
    }
  }

}

