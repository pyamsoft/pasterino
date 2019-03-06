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
import androidx.preference.PreferenceScreen
import androidx.recyclerview.widget.RecyclerView
import com.pyamsoft.pasterino.base.PasterinoModuleImpl
import com.pyamsoft.pasterino.main.MainComponent
import com.pyamsoft.pasterino.main.MainComponentImpl
import com.pyamsoft.pasterino.main.MainFragmentComponent
import com.pyamsoft.pasterino.main.MainFragmentComponentImpl
import com.pyamsoft.pasterino.main.MainModule
import com.pyamsoft.pasterino.service.PastePresenterImpl
import com.pyamsoft.pasterino.service.PasteRequestEvent
import com.pyamsoft.pasterino.service.PasteService
import com.pyamsoft.pasterino.service.ServiceFinishEvent
import com.pyamsoft.pasterino.service.ServiceFinishPresenterImpl
import com.pyamsoft.pasterino.service.ServiceModule
import com.pyamsoft.pasterino.service.ServiceStatePresenterImpl
import com.pyamsoft.pasterino.service.SinglePasteService
import com.pyamsoft.pasterino.settings.ClearAllEvent
import com.pyamsoft.pasterino.settings.ClearAllPresenterImpl
import com.pyamsoft.pasterino.settings.ConfirmationDialog
import com.pyamsoft.pasterino.settings.SettingsComponent
import com.pyamsoft.pasterino.settings.SettingsComponentImpl
import com.pyamsoft.pasterino.settings.SignificantScrollEvent
import com.pyamsoft.pydroid.core.bus.RxBus
import com.pyamsoft.pydroid.ui.ModuleProvider

internal class PasterinoComponentImpl internal constructor(
  application: Application,
  moduleProvider: ModuleProvider
) : PasterinoComponent {

  private val clearAllBus = RxBus.create<ClearAllEvent>()
  private val significantScrollBus = RxBus.create<SignificantScrollEvent>()

  private val pasteRequestBus = RxBus.create<PasteRequestEvent>()
  private val serviceFinishBus = RxBus.create<ServiceFinishEvent>()

  private val schedulerProvider = moduleProvider.schedulerProvider()
  private val navModule = moduleProvider.failedNavigationModule()
  private val enforcer = moduleProvider.enforcer()
  private val loaderModule = moduleProvider.loaderModule()
  private val mainModule: MainModule
  private val serviceModule: ServiceModule

  init {
    val pasterinoModule = PasterinoModuleImpl(application, loaderModule)
    mainModule = MainModule(pasterinoModule, moduleProvider.enforcer())
    serviceModule = ServiceModule(pasterinoModule, moduleProvider.enforcer())
  }

  override fun inject(dialog: ConfirmationDialog) {
    dialog.apply {
      this.presenter = ClearAllPresenterImpl(mainModule.interactor, clearAllBus)
    }
  }

  override fun inject(service: PasteService) {
    service.apply {
      this.presenter = PastePresenterImpl(enforcer, serviceModule.interactor, pasteRequestBus)
      this.finishPresenter = ServiceFinishPresenterImpl(serviceFinishBus)
      this.statePresenter = ServiceStatePresenterImpl(serviceModule.interactor)
    }
  }

  override fun inject(service: SinglePasteService) {
    service.apply {
      this.presenter = PastePresenterImpl(enforcer, serviceModule.interactor, pasteRequestBus)
    }
  }

  override fun plusMainComponent(parent: ViewGroup): MainComponent =
    MainComponentImpl(schedulerProvider, parent, navModule.bus)

  override fun plusSettingsComponent(
    recyclerView: RecyclerView,
    preferenceScreen: PreferenceScreen
  ): SettingsComponent = SettingsComponentImpl(
      recyclerView, preferenceScreen, mainModule.interactor,
      clearAllBus, significantScrollBus, serviceFinishBus
  )

  override fun plusMainFragmentComponent(parent: ViewGroup): MainFragmentComponent =
    MainFragmentComponentImpl(
        parent, loaderModule.provideImageLoader(),
        serviceModule.interactor, significantScrollBus
    )
}
