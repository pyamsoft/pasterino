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

import com.pyamsoft.pasterino.service.SinglePastePresenter.View
import com.pyamsoft.pydroid.helper.clear
import com.pyamsoft.pydroid.presenter.SchedulerPresenter
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import timber.log.Timber

class SinglePastePresenter internal constructor(private val interactor: PasteServiceInteractor,
    computationScheduler: Scheduler, ioScheduler: Scheduler,
    mainScheduler: Scheduler) : SchedulerPresenter<View>(
    computationScheduler, ioScheduler, mainScheduler) {

  private var postDisposable: Disposable = null.clear()

  override fun onUnbind() {
    super.onUnbind()
    postDisposable = postDisposable.clear()
  }

  fun postDelayedEvent() {
    postDisposable = postDisposable.clear()
    postDisposable = interactor.getPasteDelayTime()
        .subscribeOn(ioScheduler)
        .observeOn(mainThreadScheduler)
        .subscribe({ view?.onPost(it) },
            { Timber.e(it, "onError postDelayedEvent") })
  }

  interface View : PostCallback

  interface PostCallback {

    fun onPost(delay: Long)
  }
}
