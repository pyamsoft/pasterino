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

import android.content.Context
import android.support.annotation.CheckResult
import com.pyamsoft.pasterino.base.preference.ClearPreferences
import com.pyamsoft.pasterino.base.preference.PastePreferences
import com.pyamsoft.pasterino.model.ServiceEvent
import com.pyamsoft.pydroid.bus.EventBus
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class PasterinoModule(context: Context) {

  private val preferences: PasterinoPreferencesImpl = PasterinoPreferencesImpl(
      context.applicationContext)
  private val pasteBus = PasteBus()

  @CheckResult fun providePasteBus(): EventBus<ServiceEvent> = pasteBus

  @CheckResult fun providePreferences(): PastePreferences = preferences

  @CheckResult fun provideClearPreferences(): ClearPreferences = preferences

  @CheckResult fun provideMainScheduler(): Scheduler = AndroidSchedulers.mainThread()

  @CheckResult fun provideIoScheduler(): Scheduler = Schedulers.io()

  @CheckResult fun provideComputationScheduler(): Scheduler = Schedulers.computation()
}
