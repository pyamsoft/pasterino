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

import com.pyamsoft.pasterino.api.MainSettingsPreferenceInteractor
import com.pyamsoft.pasterino.main.MainSettingsPreferencePresenter.View
import com.pyamsoft.pasterino.model.ConfirmEvent
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.presenter.SchedulerPresenter
import io.reactivex.Scheduler
import timber.log.Timber

class MainSettingsPreferencePresenter internal constructor(
        private val interactor: MainSettingsPreferenceInteractor,
        private val bus: EventBus<ConfirmEvent>,
        computationScheduler: Scheduler, ioScheduler: Scheduler,
        mainScheduler: Scheduler) : SchedulerPresenter<View>(
        computationScheduler, ioScheduler, mainScheduler) {

    override fun onCreate() {
        super.onCreate()
        registerOnEventBus()
    }

    private fun registerOnEventBus() {
        dispose {
            bus.listen()
                    .flatMapSingle { interactor.clearAll() }
                    .subscribeOn(ioScheduler)
                    .observeOn(mainThreadScheduler)
                    .subscribe({ view?.onClearAll() }, { Timber.e(it, "OnError EventBus") })
        }
    }

    interface View : ClearCallback

    interface ClearCallback {

        fun onClearAll()
    }
}
