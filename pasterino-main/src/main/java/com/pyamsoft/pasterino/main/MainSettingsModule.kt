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
import com.pyamsoft.pasterino.api.MainSettingsPreferenceInteractor
import com.pyamsoft.pasterino.api.PasterinoModule
import com.pyamsoft.pasterino.model.ConfirmEvent
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.core.threads.Enforcer

class MainSettingsModule(
  module: PasterinoModule,
  enforcer: Enforcer
) {

  private val interactor: MainSettingsPreferenceInteractor
  private val mainBus: EventBus<ConfirmEvent> = MainBus()

  init {
    interactor = MainSettingsPreferenceInteractorImpl(module.provideClearPreferences(), enforcer)
  }

  @CheckResult
  fun getSettingsPreferencePresenter(): MainSettingsPreferencePresenter =
    MainSettingsPreferencePresenter(interactor, mainBus)

  @CheckResult
  fun getSettingsPreferencePublisher(): MainSettingsPreferencePublisher =
    MainSettingsPreferencePublisher(mainBus)
}
