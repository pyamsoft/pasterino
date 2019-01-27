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
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.preference.PreferenceScreen
import androidx.recyclerview.widget.RecyclerView
import com.pyamsoft.pasterino.base.PasterinoModuleImpl
import com.pyamsoft.pasterino.main.ActionViewEvent
import com.pyamsoft.pasterino.main.MainComponent
import com.pyamsoft.pasterino.main.MainComponentImpl
import com.pyamsoft.pasterino.main.MainFragmentComponent
import com.pyamsoft.pasterino.main.MainFragmentComponentImpl
import com.pyamsoft.pasterino.main.MainModule
import com.pyamsoft.pasterino.main.MainViewEvent
import com.pyamsoft.pasterino.service.PasteRequestEvent
import com.pyamsoft.pasterino.service.PasteService
import com.pyamsoft.pasterino.service.PasteServiceWorker
import com.pyamsoft.pasterino.service.ServiceFinishEvent
import com.pyamsoft.pasterino.service.ServiceFinishWorker
import com.pyamsoft.pasterino.service.ServiceModule
import com.pyamsoft.pasterino.service.ServiceStateWorker
import com.pyamsoft.pasterino.service.SinglePasteService
import com.pyamsoft.pasterino.service.SinglePasteWorker
import com.pyamsoft.pasterino.settings.ClearAllWorker
import com.pyamsoft.pasterino.settings.ConfirmationDialog
import com.pyamsoft.pasterino.settings.SettingsComponent
import com.pyamsoft.pasterino.settings.SettingsComponentImpl
import com.pyamsoft.pasterino.settings.SettingsStateEvent
import com.pyamsoft.pasterino.settings.SettingsViewEvent
import com.pyamsoft.pydroid.core.bus.RxBus
import com.pyamsoft.pydroid.ui.ModuleProvider

internal class PasterinoComponentImpl internal constructor(
  application: Application,
  moduleProvider: ModuleProvider
) : PasterinoComponent {

  private val enforcer = moduleProvider.enforcer()
  private val loaderModule = moduleProvider.loaderModule()
  private val mainModule: MainModule
  private val serviceModule: ServiceModule

  private val actionViewBus = RxBus.create<ActionViewEvent>()

  private val settingsStateBus = RxBus.create<SettingsStateEvent>()
  private val settingsViewBus = RxBus.create<SettingsViewEvent>()

  private val serviceFinishBus = RxBus.create<ServiceFinishEvent>()

  private val mainViewBus = RxBus.create<MainViewEvent>()

  private val pasteRequestBus = RxBus.create<PasteRequestEvent>()

  init {
    val pasterinoModule = PasterinoModuleImpl(application, loaderModule)
    mainModule = MainModule(pasterinoModule, moduleProvider.enforcer())
    serviceModule = ServiceModule(pasterinoModule, moduleProvider.enforcer())
  }

  override fun inject(dialog: ConfirmationDialog) {
    dialog.worker = ClearAllWorker(mainModule.interactor, settingsStateBus)
  }

  override fun inject(service: PasteService) {
    service.finishWorker = ServiceFinishWorker(serviceFinishBus)
    service.pasteWorker = PasteServiceWorker(pasteRequestBus)
    service.serviceStateWorker = ServiceStateWorker(serviceModule.interactor)
  }

  override fun inject(service: SinglePasteService) {
    service.serviceWorker = SinglePasteWorker(enforcer, serviceModule.interactor, pasteRequestBus)
  }

  override fun plusMainComponent(
    parent: ViewGroup,
    owner: LifecycleOwner
  ): MainComponent = MainComponentImpl(parent, owner, mainViewBus)

  override fun plusSettingsComponent(
    owner: LifecycleOwner,
    recyclerView: RecyclerView,
    preferenceScreen: PreferenceScreen
  ): SettingsComponent = SettingsComponentImpl(
      preferenceScreen, recyclerView, owner, mainModule,
      settingsViewBus, settingsStateBus, serviceFinishBus
  )

  override fun plusMainFragmentComponent(
    parent: ViewGroup,
    owner: LifecycleOwner
  ): MainFragmentComponent = MainFragmentComponentImpl(
      parent, owner, loaderModule, serviceModule, actionViewBus, settingsStateBus
  )
}
