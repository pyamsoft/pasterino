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

import android.support.annotation.CheckResult
import com.pyamsoft.pasterino.base.PasterinoModule
import io.reactivex.Scheduler

class PasteServiceModule(pasterinoModule: PasterinoModule) {

  private val interactor: PasteServiceInteractor = PasteServiceInteractor(
      pasterinoModule.providePreferences())
  private val obsScheduler: Scheduler = pasterinoModule.provideObsScheduler()
  private val subScheduler: Scheduler = pasterinoModule.provideSubScheduler()

  @CheckResult internal fun getSinglePresenter(): SinglePastePresenter {
    return SinglePastePresenter(interactor, obsScheduler, subScheduler)
  }

  @CheckResult internal fun getPasteServicePresenter(): PasteServicePresenter {
    return PasteServicePresenter(obsScheduler, subScheduler)
  }
}
