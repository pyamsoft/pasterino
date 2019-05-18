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
import com.pyamsoft.pasterino.service.ServiceControllerEvent.Finish
import com.pyamsoft.pasterino.service.ServiceControllerEvent.PasteEvent
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.core.singleDisposable
import com.pyamsoft.pydroid.core.threads.Enforcer
import com.pyamsoft.pydroid.core.tryDispose
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit.MILLISECONDS
import javax.inject.Inject

internal class PasteViewModel @Inject internal constructor(
  private val finishBus: EventBus<ServiceFinishEvent>,
  private val pasteRequestBus: EventBus<PasteRequestEvent>,
  private val interactor: PasteServiceInteractor,
  private val enforcer: Enforcer
) {

  private var pasteDisposable by singleDisposable()
  private var pasteRequestDisposable by singleDisposable()
  private var finishDisposable by singleDisposable()

  @CheckResult
  fun bind(onEvent: (event: ServiceControllerEvent) -> Unit): Disposable {
    finishDisposable = finishBus.listen()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { onEvent(Finish) }

    pasteRequestDisposable = pasteRequestBus.listen()
        .subscribeOn(Schedulers.computation())
        .observeOn(Schedulers.computation())
        .subscribe { paste(onEvent) }

    return object : Disposable {

      override fun isDisposed(): Boolean {
        return finishDisposable.isDisposed && pasteRequestDisposable.isDisposed
      }

      override fun dispose() {
        pasteRequestDisposable.tryDispose()
        finishDisposable.tryDispose()
      }

    }
  }

  private inline fun paste(crossinline onEvent: (event: ServiceControllerEvent) -> Unit) {
    pasteDisposable = interactor.getPasteDelayTime()
        .flatMap {
          enforcer.assertNotOnMainThread()
          return@flatMap Single.just(Unit)
              .delay(it, MILLISECONDS)
        }
        .flatMap {
          enforcer.assertNotOnMainThread()
          return@flatMap interactor.isDeepSearchEnabled()
        }
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .doAfterTerminate { pasteDisposable.tryDispose() }
        .subscribe(Consumer { onEvent(PasteEvent(it)) })
  }

  fun start() {
    interactor.setServiceState(true)
  }

  fun stop() {
    interactor.setServiceState(false)
  }

}
