/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.pasterino.service

import com.pyamsoft.pasterino.model.ServiceEvent
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.presenter.SchedulerPresenter
import io.reactivex.Scheduler
import timber.log.Timber

class PasteServicePresenter internal constructor(
    private val bus: EventBus<ServiceEvent>, computationScheduler: Scheduler,
    ioScheduler: Scheduler,
    mainScheduler: Scheduler) : SchedulerPresenter<PasteServicePresenter.Callback, Unit>(
    computationScheduler, ioScheduler, mainScheduler) {

  override fun onCreate(bound: Callback) {
    super.onCreate(bound)
    registerOnBus(bound::onPasteRequested, bound::onServiceFinishRequested)
  }

  private fun registerOnBus(onPasteRequested: () -> Unit, onServiceFinishRequested: () -> Unit) {
    disposeOnDestroy {
      bus.listen()
          .subscribeOn(ioScheduler)
          .observeOn(mainThreadScheduler)
          .subscribe({ (type) ->
            when (type) {
              ServiceEvent.Type.FINISH -> onServiceFinishRequested()
              ServiceEvent.Type.PASTE -> onPasteRequested()
              else -> throw IllegalArgumentException("Invalid ServiceEvent.Type: $type")
            }
          }, { Timber.e(it, "onError event bus") })
    }
  }

  interface Callback {

    fun onPasteRequested()

    fun onServiceFinishRequested()
  }
}
