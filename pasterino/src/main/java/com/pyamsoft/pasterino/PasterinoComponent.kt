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

package com.pyamsoft.pasterino

import android.support.annotation.CheckResult
import com.pyamsoft.pasterino.base.PasterinoModule
import com.pyamsoft.pasterino.main.ConfirmationDialog
import com.pyamsoft.pasterino.main.MainSettingsFragment
import com.pyamsoft.pasterino.main.MainSettingsModule
import com.pyamsoft.pasterino.main.MainSettingsPreferenceFragment
import com.pyamsoft.pasterino.service.PasteService
import com.pyamsoft.pasterino.service.PasteServiceModule
import com.pyamsoft.pasterino.service.SinglePasteService

class PasterinoComponent private constructor(module: PasterinoModule) {

  private val mainSettingsModule: MainSettingsModule = MainSettingsModule(module)
  private val pasteServiceModule: PasteServiceModule = PasteServiceModule(module)


  fun inject(service: SinglePasteService) {
    service.presenter = pasteServiceModule.getSinglePresenter()
    service.publisher = pasteServiceModule.getPasteServicePublisher()
  }

  fun inject(service: PasteService) {
    service.presenter = pasteServiceModule.getPasteServicePresenter()
  }

  fun inject(fragment: MainSettingsFragment) {
    fragment.presenter = mainSettingsModule.getPresenter()
  }

  fun inject(fragment: MainSettingsPreferenceFragment) {
    fragment.presenter = mainSettingsModule.getSettingsPreferencePresenter()
    fragment.publisher = pasteServiceModule.getPasteServicePublisher()
  }

  fun inject(dialog: ConfirmationDialog) {
    dialog.publisher = mainSettingsModule.getSettingsPreferencePublisher()
  }

  companion object {

    @JvmStatic
    @CheckResult internal fun withModule(module: PasterinoModule): PasterinoComponent {
      return PasterinoComponent(module)
    }
  }
}
