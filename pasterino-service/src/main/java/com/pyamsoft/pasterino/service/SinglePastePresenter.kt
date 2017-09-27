/*
 * Copyright 2017 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pasterino.service

import com.pyamsoft.pydroid.helper.clear
import com.pyamsoft.pydroid.presenter.SchedulerPresenter
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import timber.log.Timber

class SinglePastePresenter internal constructor(private val interactor: PasteServiceInteractor,
    computationScheduler: Scheduler, ioScheduler: Scheduler,
    mainScheduler: Scheduler) : SchedulerPresenter<Unit>(
    computationScheduler, ioScheduler, mainScheduler) {

  private var postDisposable: Disposable = null.clear()

  override fun onUnbind() {
    super.onUnbind()
    postDisposable = postDisposable.clear()
  }

  fun postDelayedEvent(onPost: (Long) -> Unit) {
    postDisposable = postDisposable.clear()
    postDisposable = interactor.getPasteDelayTime()
        .subscribeOn(ioScheduler)
        .observeOn(mainThreadScheduler)
        .subscribe({ onPost(it) },
            { Timber.e(it, "onError postDelayedEvent") })
  }
}
