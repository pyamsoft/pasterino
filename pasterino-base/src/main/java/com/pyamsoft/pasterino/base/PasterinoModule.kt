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

package com.pyamsoft.pasterino.base

import android.support.annotation.CheckResult
import com.pyamsoft.pasterino.base.preference.ClearPreferences
import com.pyamsoft.pasterino.base.preference.PastePreferences
import com.pyamsoft.pasterino.model.ServiceEvent
import com.pyamsoft.pydroid.PYDroidModule
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.loader.LoaderModule
import io.reactivex.Scheduler

class PasterinoModule(private val pyDroidModule: PYDroidModule,
        private val loaderModule: LoaderModule) {

    private val preferences: PasterinoPreferencesImpl = PasterinoPreferencesImpl(
            pyDroidModule.provideContext())
    private val pasteBus = PasteBus()

    @CheckResult
    fun providePasteBus(): EventBus<ServiceEvent> = pasteBus

    @CheckResult
    fun providePreferences(): PastePreferences = preferences

    @CheckResult
    fun provideClearPreferences(): ClearPreferences = preferences

    @CheckResult
    fun provideMainScheduler(): Scheduler = pyDroidModule.provideMainThreadScheduler()

    @CheckResult
    fun provideIoScheduler(): Scheduler = pyDroidModule.provideIoScheduler()

    @CheckResult
    fun provideComputationScheduler(): Scheduler = pyDroidModule.provideComputationScheduler()

    @CheckResult
    fun provideImageLoader(): ImageLoader = loaderModule.provideImageLoader()
}
