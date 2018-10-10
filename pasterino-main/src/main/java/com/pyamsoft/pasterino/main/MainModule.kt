/*
 * Copyright (C) 2018 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pasterino.main

import androidx.annotation.CheckResult
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pasterino.api.MainInteractor
import com.pyamsoft.pasterino.api.PasterinoModule
import com.pyamsoft.pasterino.model.ConfirmEvent
import com.pyamsoft.pydroid.core.bus.Publisher
import com.pyamsoft.pydroid.core.bus.RxBus
import com.pyamsoft.pydroid.core.threads.Enforcer

class MainModule(
  module: PasterinoModule,
  private val enforcer: Enforcer
) {

  private val interactor: MainInteractor
  private val mainBus = RxBus.create<ConfirmEvent>()

  init {
    interactor = MainInteractorImpl(module.provideClearPreferences(), enforcer)
  }

  @CheckResult
  fun getViewModel(owner: LifecycleOwner) = MainViewModel(owner, enforcer, interactor, mainBus)

  @CheckResult
  fun getPublisher(): Publisher<ConfirmEvent> = mainBus
}