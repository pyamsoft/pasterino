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

import com.pyamsoft.pasterino.api.PasteServiceInteractor
import com.pyamsoft.pasterino.main.MainControllerEvent.ServiceAction
import com.pyamsoft.pasterino.main.MainViewEvent.ActionClick
import com.pyamsoft.pasterino.settings.SignificantScrollEvent
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.core.singleDisposable
import com.pyamsoft.pydroid.core.tryDispose
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

internal class MainViewModel @Inject internal constructor(
  serviceInteractor: PasteServiceInteractor,
  visibilityBus: EventBus<SignificantScrollEvent>
) : UiViewModel<MainViewState, MainViewEvent, MainControllerEvent>(
    initialState = MainViewState(isVisible = true, isServiceRunning = false)
) {

  private var serviceDisposable by singleDisposable()
  private var visibilityDisposable by singleDisposable()

  init {
    serviceDisposable = serviceInteractor.observeServiceState()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { setState { copy(isServiceRunning = it) } }

    visibilityDisposable = visibilityBus.listen()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { setState { copy(isVisible = it.visible) } }
  }

  override fun handleViewEvent(event: MainViewEvent) {
    return when (event) {
      is ActionClick -> publish(ServiceAction(event.isServiceRunning))
    }
  }

  override fun onCleared() {
    serviceDisposable.tryDispose()
    visibilityDisposable.tryDispose()
  }

}

