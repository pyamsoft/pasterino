/*
 * Copyright 2019 Peter Kenji Yamanaka
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
 *
 */

package com.pyamsoft.pasterino.service

import androidx.annotation.CheckResult
import com.pyamsoft.pasterino.api.PasteServiceInteractor
import com.pyamsoft.pasterino.model.ServiceEvent
import com.pyamsoft.pasterino.model.ServiceEvent.Type.FINISH
import com.pyamsoft.pasterino.model.ServiceEvent.Type.PASTE
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.core.threads.Enforcer
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit.MILLISECONDS

class PasteViewModel internal constructor(
  private val enforcer: Enforcer,
  private val interactor: PasteServiceInteractor,
  private val bus: EventBus<ServiceEvent>
) {

  @CheckResult
  fun onFinishEvent(func: (ServiceEvent.Type) -> Unit): Disposable {
    return bus.listen()
        .map { it.type }
        .filter { it == FINISH }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(func)
  }

  @CheckResult
  fun onPasteEvent(func: (ServiceEvent.Type) -> Unit): Disposable {
    return bus.listen()
        .map { it.type }
        .filter { it == PASTE }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(func)
  }

  @CheckResult
  fun post(
    onPost: () -> Unit,
    onPostError: (error: Throwable) -> Unit
  ): Disposable {
    return interactor.getPasteDelayTime()
        .observeOn(Schedulers.io())
        .flatMap {
          enforcer.assertNotOnMainThread()
          return@flatMap Single.just(0)
              .subscribeOn(Schedulers.io())
              .observeOn(Schedulers.io())
              .delay(it, MILLISECONDS)
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ onPost() }, {
          Timber.e(it, "Error on post")
          onPostError(it)
        })
  }

  @CheckResult
  fun onServiceStateChanged(func: (Boolean) -> Unit): Disposable {
    return interactor.observeServiceState()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(func)
  }

  fun setServiceState(running: Boolean) {
    interactor.setServiceState(running)
  }

}
