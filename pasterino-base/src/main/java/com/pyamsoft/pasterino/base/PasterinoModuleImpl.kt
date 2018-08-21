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

import android.app.Application
import android.content.Context
import com.pyamsoft.pasterino.api.ClearPreferences
import com.pyamsoft.pasterino.api.PastePreferences
import com.pyamsoft.pasterino.api.PasterinoModule
import com.pyamsoft.pasterino.model.ServiceEvent
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.core.bus.RxBus
import com.pyamsoft.pydroid.core.cache.Cache
import com.pyamsoft.pydroid.loader.ImageLoader

class PasterinoModuleImpl(
  private val application: Application,
  private val imageLoader: ImageLoader,
  private val imageCache: Cache
) : PasterinoModule {

  private val preferences = PasterinoPreferencesImpl(application)
  private val pasteBus = RxBus.create<ServiceEvent>()

  override fun provideApplication(): Application = application

  override fun provideContext(): Context = provideApplication()

  override fun provideImageLoader(): ImageLoader = imageLoader

  override fun provideImageLoaderCache(): Cache = imageCache

  override fun providePasteBus(): EventBus<ServiceEvent> = pasteBus

  override fun providePreferences(): PastePreferences = preferences

  override fun provideClearPreferences(): ClearPreferences = preferences
}
