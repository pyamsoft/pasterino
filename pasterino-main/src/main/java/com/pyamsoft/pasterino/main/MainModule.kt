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

import androidx.annotation.CheckResult
import com.pyamsoft.pasterino.api.MainInteractor
import com.pyamsoft.pasterino.api.PasterinoModule
import com.pyamsoft.pasterino.main.ConfirmEvent
import com.pyamsoft.pydroid.core.bus.Publisher
import com.pyamsoft.pydroid.core.bus.RxBus
import com.pyamsoft.pydroid.core.threads.Enforcer

class MainModule(
  private val module: PasterinoModule,
  private val enforcer: Enforcer
) {

  private val interactor: MainInteractor
  private val mainBus = RxBus.create<com.pyamsoft.pasterino.main.ConfirmEvent>()

  init {
    interactor = MainInteractorImpl(module.provideClearPreferences(), enforcer)
  }

  @CheckResult
  fun getViewModel(tag: String) = com.pyamsoft.pasterino.main.MainViewModel(
      enforcer, interactor,
      module.provideFabScrollRequestBus(), mainBus, tag
  )

  @CheckResult
  fun getFragmentViewModel() = MainFragmentViewModel(module.provideFabScrollRequestBus())

  @CheckResult
  fun getPublisher(): Publisher<com.pyamsoft.pasterino.main.ConfirmEvent> = mainBus
}
