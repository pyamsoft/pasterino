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

package com.pyamsoft.pasterino.service

import com.pyamsoft.pasterino.api.PasteServiceInteractor
import com.pyamsoft.pasterino.service.SinglePastePresenter.View
import com.pyamsoft.pydroid.ktext.clear
import com.pyamsoft.pydroid.presenter.SchedulerPresenter
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import timber.log.Timber

class SinglePastePresenter internal constructor(
  private val interactor: PasteServiceInteractor,
  computationScheduler: Scheduler,
  ioScheduler: Scheduler,
  mainScheduler: Scheduler
) : SchedulerPresenter<View>(
    computationScheduler, ioScheduler, mainScheduler
) {

  private var postDisposable: Disposable = Disposables.empty()

  override fun onDestroy() {
    super.onDestroy()
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
