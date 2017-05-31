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

class PasteServicePresenter internal constructor(observeScheduler: Scheduler,
    subscribeScheduler: Scheduler) : SchedulerPresenter(observeScheduler, subscribeScheduler) {

  /**
   * public
   */
  fun registerOnBus(callback: ServiceCallback) {
    disposeOnStop(EventBus.get()
        .listen(ServiceEvent::class.java)
        .subscribeOn(subscribeScheduler)
        .observeOn(observeScheduler)
        .subscribe({ (type) ->
          when (type) {
            ServiceEvent.Type.FINISH -> callback.onServiceFinishRequested()
            ServiceEvent.Type.PASTE -> callback.onPasteRequested()
            else -> throw IllegalArgumentException(
                "Invalid ServiceEvent.Type: " + type)
          }
        }, { Timber.e(it, "onError event bus") }))
  }

  interface ServiceCallback {

    fun onServiceFinishRequested()

    fun onPasteRequested()
  }
}
