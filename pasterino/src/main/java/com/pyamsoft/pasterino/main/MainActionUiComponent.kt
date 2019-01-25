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
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pasterino.main.SettingsStateEvent.SignificantScroll
import com.pyamsoft.pasterino.service.ServiceStateEvent.Start
import com.pyamsoft.pasterino.service.ServiceStateEvent.Stop
import com.pyamsoft.pasterino.service.ServiceStateWorker
import com.pyamsoft.pydroid.core.bus.Listener
import com.pyamsoft.pydroid.ui.arch.UiComponent
import com.pyamsoft.pydroid.ui.arch.destroy
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainActionUiComponent internal constructor(
  private val actionView: MainActionView,
  private val uiBus: Listener<ActionViewEvent>,
  private val controllerBus: Listener<SettingsStateEvent>,
  private val worker: ServiceStateWorker,
  owner: LifecycleOwner
) : UiComponent<ActionViewEvent>(owner) {

  override fun id(): Int {
    return actionView.id()
  }

  override fun create(savedInstanceState: Bundle?) {
    actionView.inflate(savedInstanceState)
    owner.run { actionView.teardown() }

    worker.onStateEvent {
      return@onStateEvent when (it) {
        Start -> actionView.setFabFromServiceState(true)
        Stop -> actionView.setFabFromServiceState(false)
      }
    }
        .destroy(owner)

    controllerBus.listen()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
          return@subscribe when (it) {
            is SignificantScroll -> actionView.toggleVisibility(it.visible)
          }
        }
        .destroy(owner)
  }

  override fun onUiEvent(): Observable<ActionViewEvent> {
    return uiBus.listen()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
  }

  override fun saveState(outState: Bundle) {
    actionView.saveState(outState)
  }

}