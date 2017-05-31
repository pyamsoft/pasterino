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
import com.pyamsoft.pasterino.main.MainComponent
import com.pyamsoft.pasterino.main.MainSettingsModule
import com.pyamsoft.pasterino.service.PasteComponent
import com.pyamsoft.pasterino.service.PasteServiceModule

class PasterinoComponent private constructor(module: PasterinoModule) {

  private val mainComponent: MainComponent
  private val pasteComponent: PasteComponent

  init {
    val mainSettingsModule = MainSettingsModule(module)
    val pasteServiceModule = PasteServiceModule(module)
    mainComponent = MainComponent(mainSettingsModule)
    pasteComponent = PasteComponent(pasteServiceModule)
  }

  @CheckResult fun plusMainComponent(): MainComponent {
    return mainComponent
  }

  @CheckResult fun plusPasteComponent(): PasteComponent {
    return pasteComponent
  }

  companion object {

    @JvmStatic
    @CheckResult internal fun withModule(module: PasterinoModule): PasterinoComponent {
      return PasterinoComponent(module)
    }
  }
}
