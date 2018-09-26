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

package com.pyamsoft.pasterino

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pasterino.base.PasterinoModuleImpl
import com.pyamsoft.pasterino.main.ConfirmationDialog
import com.pyamsoft.pasterino.main.MainComponent
import com.pyamsoft.pasterino.main.MainComponentImpl
import com.pyamsoft.pasterino.main.MainModule
import com.pyamsoft.pasterino.service.PasteServiceModule
import com.pyamsoft.pasterino.service.ServiceComponent
import com.pyamsoft.pasterino.service.ServiceComponentImpl
import com.pyamsoft.pydroid.ui.ModuleProvider

internal class PasterinoComponentImpl internal constructor(
  application: Application,
  private val moduleProvider: ModuleProvider
) : PasterinoComponent {

  private val pasterinoModule =
    PasterinoModuleImpl(application, moduleProvider.loaderModule().provideImageLoader())
  private val mainSettingsModule = MainModule(pasterinoModule, moduleProvider.enforcer())
  private val pasteServiceModule = PasteServiceModule(pasterinoModule, moduleProvider.enforcer())

  override fun inject(dialog: ConfirmationDialog) {
    dialog.publisher = mainSettingsModule.getPublisher()
  }

  override fun plusMainComponent(owner: LifecycleOwner): MainComponent {
    return MainComponentImpl(
        owner, moduleProvider.loaderModule(), mainSettingsModule, pasteServiceModule
    )
  }

  override fun plusServiceComponent(owner: LifecycleOwner): ServiceComponent {
    return ServiceComponentImpl(owner, pasteServiceModule)
  }
}
