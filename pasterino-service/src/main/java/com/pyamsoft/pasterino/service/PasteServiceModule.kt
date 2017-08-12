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

  private val interactor: PasteServiceInteractor
  private val pasteBus = pasterinoModule.providePasteBus()
  private val computationScheduler: Scheduler = pasterinoModule.provideComputationScheduler()
  private val ioScheduler: Scheduler = pasterinoModule.provideIoScheduler()
  private val mainScheduler: Scheduler = pasterinoModule.provideMainScheduler()

  init {
    interactor = PasteServiceInteractorImpl(pasterinoModule.providePreferences())
  }

  @CheckResult fun getSinglePresenter(): SinglePastePresenter {
    return SinglePastePresenter(interactor, computationScheduler, ioScheduler, mainScheduler)
  }

  @CheckResult fun getPasteServicePresenter(): PasteServicePresenter {
    return PasteServicePresenter(pasteBus, computationScheduler, ioScheduler, mainScheduler)
  }

  @CheckResult fun getPasteServicePublisher(): PasteServicePublisher {
    return PasteServicePublisher(pasteBus)
  }
}
