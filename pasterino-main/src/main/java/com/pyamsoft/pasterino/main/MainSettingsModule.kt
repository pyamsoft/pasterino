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

package com.pyamsoft.pasterino.main

import android.support.annotation.CheckResult
import com.pyamsoft.pasterino.base.PasterinoModule
import com.pyamsoft.pasterino.model.ConfirmEvent
import com.pyamsoft.pydroid.bus.EventBus
import io.reactivex.Scheduler

class MainSettingsModule(module: PasterinoModule) {

  private val interactor: MainSettingsPreferenceInteractor
  private val mainBus: EventBus<ConfirmEvent> = MainBus()
  private val computationScheduler: Scheduler = module.provideComputationScheduler()
  private val ioScheduler: Scheduler = module.provideIoScheduler()
  private val mainScheduler: Scheduler = module.provideMainScheduler()

  init {
    interactor = MainSettingsPreferenceInteractorImpl(module.provideClearPreferences())
  }

  @CheckResult
  fun getSettingsPreferencePresenter(): MainSettingsPreferencePresenter = MainSettingsPreferencePresenter(
      interactor, mainBus, computationScheduler, ioScheduler,
      mainScheduler)

  @CheckResult
  fun getSettingsPreferencePublisher(): MainSettingsPreferencePublisher =
      MainSettingsPreferencePublisher(mainBus)
}
