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

package com.pyamsoft.pasterino.main

import androidx.lifecycle.viewModelScope
import com.pyamsoft.pasterino.api.PasteServiceInteractor
import com.pyamsoft.pasterino.settings.SignificantScrollEvent
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.arch.UnitControllerEvent
import com.pyamsoft.pydroid.bus.EventBus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class MainViewModel @Inject internal constructor(
    interactor: PasteServiceInteractor,
    visibilityBus: EventBus<SignificantScrollEvent>
) : UiViewModel<MainViewState, UnitControllerEvent>(
    MainViewState(
        isVisible = true,
        isServiceRunning = false
    )
) {

    init {
        viewModelScope.launch(context = Dispatchers.Default) {
            interactor.observeServiceState { setState { copy(isServiceRunning = it) } }
        }

        viewModelScope.launch(context = Dispatchers.Default) {
            visibilityBus.onEvent { setState { copy(isVisible = it.visible) } }
        }
    }
}
