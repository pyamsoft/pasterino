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

import androidx.lifecycle.viewModelScope
import com.pyamsoft.pasterino.service.ServiceFinishEvent
import com.pyamsoft.pasterino.settings.SettingsControllerEvent.ClearAll
import com.pyamsoft.pasterino.settings.SettingsViewEvent.SignificantScroll
import com.pyamsoft.pydroid.arch.EventBus
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.arch.UnitViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class SettingsViewModel @Inject internal constructor(
  private val scrollBus: EventBus<SignificantScrollEvent>,
  private val serviceFinishBus: EventBus<ServiceFinishEvent>,
  private val clearBus: EventBus<ClearAllEvent>
) : UiViewModel<UnitViewState, SettingsViewEvent, SettingsControllerEvent>(
    initialState = UnitViewState
) {

  override fun onInit() {
    viewModelScope.launch(context = Dispatchers.Default) {
      clearBus.onEvent { withContext(context = Dispatchers.Main) { killApplication() } }
    }
  }

  override fun handleViewEvent(event: SettingsViewEvent) {
    return when (event) {
      is SignificantScroll -> scrollBus.publish(SignificantScrollEvent(event.visible))
    }
  }

  private fun killApplication() {
    serviceFinishBus.publish(ServiceFinishEvent)
    publish(ClearAll)
  }
}

