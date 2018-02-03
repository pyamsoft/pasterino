/*
 *     Copyright (C) 2017 Peter Kenji Yamanaka
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.pyamsoft.pasterino.api

import android.support.annotation.CheckResult
import com.pyamsoft.pasterino.model.ServiceEvent
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.loader.ImageLoader
import io.reactivex.Scheduler

interface PasterinoModule {

  @CheckResult
  fun providePasteBus(): EventBus<ServiceEvent>

  @CheckResult
  fun providePreferences(): PastePreferences

  @CheckResult
  fun provideClearPreferences(): ClearPreferences

  @CheckResult
  fun provideMainScheduler(): Scheduler

  @CheckResult
  fun provideIoScheduler(): Scheduler

  @CheckResult
  fun provideComputationScheduler(): Scheduler

  @CheckResult
  fun provideImageLoader(): ImageLoader
}
