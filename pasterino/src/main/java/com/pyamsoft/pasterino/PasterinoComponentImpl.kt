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

package com.pyamsoft.pasterino

import android.app.Application
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.preference.PreferenceScreen
import com.pyamsoft.pasterino.base.PasterinoModuleImpl
import com.pyamsoft.pasterino.main.ConfirmationDialog
import com.pyamsoft.pasterino.main.MainActivity
import com.pyamsoft.pasterino.main.MainComponent
import com.pyamsoft.pasterino.main.MainComponentImpl
import com.pyamsoft.pasterino.main.MainFragmentComponent
import com.pyamsoft.pasterino.main.MainFragmentComponentImpl
import com.pyamsoft.pasterino.main.MainModule
import com.pyamsoft.pasterino.service.PasteService
import com.pyamsoft.pasterino.service.PasteServiceModule
import com.pyamsoft.pasterino.service.ServiceComponent
import com.pyamsoft.pasterino.service.ServiceComponentImpl
import com.pyamsoft.pasterino.service.SinglePasteService
import com.pyamsoft.pydroid.ui.ModuleProvider

internal class PasterinoComponentImpl internal constructor(
  application: Application,
  moduleProvider: ModuleProvider
) : PasterinoComponent {

  private val theming = moduleProvider.theming()
  private val loaderModule = moduleProvider.loaderModule()
  private val pasterinoModule = PasterinoModuleImpl(application, loaderModule)
  private val mainSettingsModule = MainModule(pasterinoModule, moduleProvider.enforcer())
  private val pasteServiceModule = PasteServiceModule(pasterinoModule, moduleProvider.enforcer())

  override fun inject(activity: MainActivity) {
    activity.theming = theming
    activity.mainView = MainViewImpl(activity)
  }

  override fun inject(dialog: ConfirmationDialog) {
    dialog.publisher = mainSettingsModule.getPublisher()
  }

  override fun inject(service: PasteService) {
    service.viewModel = pasteServiceModule.getViewModel()
  }

  override fun inject(service: SinglePasteService) {
    service.viewModel = pasteServiceModule.getViewModel()
    service.publisher = pasteServiceModule.getPublisher()
  }

  override fun plusMainComponent(
    owner: LifecycleOwner,
    preferenceScreen: PreferenceScreen,
    tag: String
  ): MainComponent = MainComponentImpl(
      owner, preferenceScreen, tag,
      mainSettingsModule, pasteServiceModule
  )

  override fun plusMainFragmentComponent(
    owner: LifecycleOwner,
    inflater: LayoutInflater,
    container: ViewGroup?
  ): MainFragmentComponent = MainFragmentComponentImpl(
      owner, inflater, container,
      loaderModule, pasteServiceModule, mainSettingsModule
  )

  override fun plusServiceComponent(owner: LifecycleOwner): ServiceComponent =
    ServiceComponentImpl(owner, pasteServiceModule)
}
