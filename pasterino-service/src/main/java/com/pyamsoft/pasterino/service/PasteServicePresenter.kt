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

import com.pyamsoft.pasterino.model.ServiceEvent
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.presenter.SchedulerPresenter
import io.reactivex.Scheduler
import timber.log.Timber

class PasteServicePresenter internal constructor(
    private val bus: EventBus<ServiceEvent>, computationScheduler: Scheduler,
    ioScheduler: Scheduler,
    mainScheduler: Scheduler) : SchedulerPresenter<PasteServicePresenter.Callback>(
    computationScheduler, ioScheduler, mainScheduler) {

  override fun onBind(v: Callback) {
    super.onBind(v)
    registerOnBus(v::onPasteRequested, v::onServiceFinishRequested)
  }

  private fun registerOnBus(onPasteRequested: () -> Unit, onServiceFinishRequested: () -> Unit) {
    dispose {
      bus.listen()
          .subscribeOn(ioScheduler)
          .observeOn(mainThreadScheduler)
          .subscribe({ (type) ->
            when (type) {
              ServiceEvent.Type.FINISH -> onServiceFinishRequested()
              ServiceEvent.Type.PASTE -> onPasteRequested()
              else -> throw IllegalArgumentException("Invalid ServiceEvent.Type: $type")
            }
          }, { Timber.e(it, "onError event bus") })
    }
  }

  interface Callback {

    fun onPasteRequested()

    fun onServiceFinishRequested()
  }
}
