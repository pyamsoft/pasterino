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

package com.pyamsoft.pasterino.service

import com.pyamsoft.pasterino.api.PasteServiceInteractor
import com.pyamsoft.pasterino.service.PasteViewModel.PasteState
import com.pyamsoft.pasterino.service.PasteViewModel.PasteState.DeepSearch
import com.pyamsoft.pydroid.arch.UiState
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.core.singleDisposable
import com.pyamsoft.pydroid.core.threads.Enforcer
import com.pyamsoft.pydroid.core.tryDispose
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit.MILLISECONDS
import javax.inject.Inject

internal class PasteViewModel @Inject internal constructor(
  private val enforcer: Enforcer,
  private val interactor: PasteServiceInteractor,
  private val bus: EventBus<PasteRequestEvent>
) : UiViewModel<PasteState>(
    initialState = PasteState(isDeepSearchEnabled = null)
) {

  private var pasteDisposable by singleDisposable()

  override fun onBind() {
    bus.listen()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { handlePaste(it.deepSearchEnabled) }
        .destroy()
  }

  private fun handlePaste(deepSearchEnabled: Boolean) {
    val newValue = DeepSearch(deepSearchEnabled)
    setUniqueState(newValue, old = { it.isDeepSearchEnabled }) { state, value ->
      state.copy(isDeepSearchEnabled = value)
    }
  }

  override fun onUnbind() {
    pasteDisposable.tryDispose()
  }

  fun paste() {
    pasteDisposable = interactor.getPasteDelayTime()
        .observeOn(Schedulers.io())
        .flatMap {
          enforcer.assertNotOnMainThread()
          return@flatMap Single.just(0)
              .subscribeOn(Schedulers.io())
              .observeOn(Schedulers.io())
              .delay(it, MILLISECONDS)
        }
        .flatMap {
          enforcer.assertNotOnMainThread()
          return@flatMap interactor.isDeepSearchEnabled()
              .subscribeOn(Schedulers.io())
              .observeOn(Schedulers.io())
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(Consumer { bus.publish(PasteRequestEvent(it)) })
  }

  data class PasteState(val isDeepSearchEnabled: DeepSearch?) : UiState {
    data class DeepSearch(val isEnabled: Boolean)
  }

}