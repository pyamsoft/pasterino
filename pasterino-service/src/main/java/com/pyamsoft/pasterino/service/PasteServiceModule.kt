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

package com.pyamsoft.pasterino.service

import android.support.annotation.CheckResult
import com.pyamsoft.pasterino.base.PasterinoModule
import io.reactivex.Scheduler

class PasteServiceModule(pasterinoModule: PasterinoModule) {

  private val interactor: PasteServiceInteractor
  private val pasteBus = pasterinoModule.providePasteBus()
  private val computationScheduler: Scheduler = pasterinoModule.provideComputationScheduler()
  private val ioScheduler: Scheduler = pasterinoModule.provideIoScheduler()
  private val mainScheduler: Scheduler = pasterinoModule.provideMainScheduler()

  init {
    interactor = PasteServiceInteractorImpl(pasterinoModule.providePreferences())
  }

  @CheckResult
  fun getSinglePresenter(): SinglePastePresenter =
      SinglePastePresenter(interactor, computationScheduler, ioScheduler, mainScheduler)

  @CheckResult
  fun getPasteServicePresenter(): PasteServicePresenter =
      PasteServicePresenter(pasteBus, computationScheduler, ioScheduler, mainScheduler)

  @CheckResult
  fun getPasteServicePublisher(): PasteServicePublisher =
      PasteServicePublisher(pasteBus)
}
