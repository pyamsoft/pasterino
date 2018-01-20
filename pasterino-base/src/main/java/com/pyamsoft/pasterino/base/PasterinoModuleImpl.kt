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
    override fun provideComputationScheduler(): Scheduler = pyDroidModule.provideComputationScheduler()

    @CheckResult
    override fun provideImageLoader(): ImageLoader = loaderModule.provideImageLoader()
}
