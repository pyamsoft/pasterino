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

import android.content.Context
import androidx.annotation.CheckResult
import com.pyamsoft.pasterino.PasterinoComponent.PasterinoModule
import com.pyamsoft.pasterino.base.BaseModule
import com.pyamsoft.pasterino.main.MainComponent
import com.pyamsoft.pasterino.main.MainFragmentComponent
import com.pyamsoft.pasterino.service.PasteRequestEvent
import com.pyamsoft.pasterino.service.PasteService
import com.pyamsoft.pasterino.service.ServiceFinishEvent
import com.pyamsoft.pasterino.service.SinglePasteService
import com.pyamsoft.pasterino.settings.ClearAllEvent
import com.pyamsoft.pasterino.settings.ConfirmationDialog
import com.pyamsoft.pasterino.settings.SettingsComponent
import com.pyamsoft.pasterino.settings.SignificantScrollEvent
import com.pyamsoft.pydroid.arch.EventBus
import com.pyamsoft.pydroid.core.Enforcer
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.ui.theme.Theming
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Singleton
@Component(modules = [PasterinoModule::class, BaseModule::class])
interface PasterinoComponent {

  fun inject(dialog: ConfirmationDialog)

  fun inject(service: PasteService)

  fun inject(service: SinglePasteService)

  @CheckResult
  fun plusMainComponent(): MainComponent.Factory

  @CheckResult
  fun plusMainFragmentComponent(): MainFragmentComponent.Factory

  @CheckResult
  fun plusSettingsComponent(): SettingsComponent.Factory

  @Component.Factory
  interface Factory {

    @CheckResult
    fun create(
      @BindsInstance context: Context,
      @BindsInstance theming: Theming,
      @BindsInstance enforcer: Enforcer,
      @BindsInstance imageLoader: ImageLoader
    ): PasterinoComponent
  }

  @Module
  abstract class PasterinoModule {

    @Module
    companion object {

      @Provides
      @Singleton
      @JvmStatic
      @CheckResult
      internal fun provideClearBus(): EventBus<ClearAllEvent> {
        return EventBus.create()
      }

      @Provides
      @Singleton
      @JvmStatic
      @CheckResult
      internal fun providePasteBus(): EventBus<PasteRequestEvent> {
        return EventBus.create()
      }

      @Provides
      @Singleton
      @JvmStatic
      @CheckResult
      internal fun provideServiceBus(): EventBus<ServiceFinishEvent> {
        return EventBus.create()
      }

      @Provides
      @Singleton
      @JvmStatic
      @CheckResult
      internal fun provideScrollBus(): EventBus<SignificantScrollEvent> {
        return EventBus.create()
      }

    }
  }
}
