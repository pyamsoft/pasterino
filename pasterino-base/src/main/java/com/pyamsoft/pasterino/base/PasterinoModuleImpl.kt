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

package com.pyamsoft.pasterino.base

import android.support.annotation.CheckResult
import com.pyamsoft.pasterino.api.ClearPreferences
import com.pyamsoft.pasterino.api.PastePreferences
import com.pyamsoft.pasterino.api.PasterinoModule
import com.pyamsoft.pasterino.model.ServiceEvent
import com.pyamsoft.pydroid.PYDroidModule
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.loader.LoaderModule
import io.reactivex.Scheduler

class PasterinoModuleImpl(
  private val pyDroidModule: PYDroidModule,
  private val loaderModule: LoaderModule
) : PasterinoModule {

  private val preferences: PasterinoPreferencesImpl = PasterinoPreferencesImpl(
      pyDroidModule.provideContext()
  )
  private val pasteBus = PasteBus()

  @CheckResult
  override fun providePasteBus(): EventBus<ServiceEvent> = pasteBus

  @CheckResult
  override fun providePreferences(): PastePreferences = preferences

  @CheckResult
  override fun provideClearPreferences(): ClearPreferences = preferences

  @CheckResult
  override fun provideMainScheduler(): Scheduler = pyDroidModule.provideMainThreadScheduler()

  @CheckResult
  override fun provideIoScheduler(): Scheduler = pyDroidModule.provideIoScheduler()

  @CheckResult
  override fun provideComputationScheduler(): Scheduler =
    pyDroidModule.provideComputationScheduler()

  @CheckResult
  override fun provideImageLoader(): ImageLoader = loaderModule.provideImageLoader()
}
