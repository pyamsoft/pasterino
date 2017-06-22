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

package com.pyamsoft.pasterino.main

import com.pyamsoft.pydroid.presenter.SchedulerPreferencePresenter
import io.reactivex.Scheduler
import timber.log.Timber

class MainSettingsPreferencePresenter internal constructor(
    private val interactor: MainSettingsPreferenceInteractor,
    private val bus: MainBus,
    observeScheduler: Scheduler, subscribeScheduler: Scheduler) : SchedulerPreferencePresenter(
    observeScheduler, subscribeScheduler) {

  /**
   * public
   */
  fun registerOnEventBus(onClearAll: () -> Unit) {
    disposeOnStop {
      bus.listen()
          .flatMapSingle { interactor.clearAll() }
          .subscribeOn(backgroundScheduler)
          .observeOn(foregroundScheduler)
          .subscribe({ onClearAll() }, { Timber.e(it, "OnError EventBus") })
    }
  }
}
