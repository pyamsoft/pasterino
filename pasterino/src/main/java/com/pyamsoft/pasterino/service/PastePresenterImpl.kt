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

import com.pyamsoft.pasterino.api.PasteServiceInteractor
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.core.singleDisposable
import com.pyamsoft.pydroid.core.threads.Enforcer
import com.pyamsoft.pydroid.core.tryDispose
import com.pyamsoft.pydroid.arch.BasePresenter
import com.pyamsoft.pydroid.arch.destroy
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit.MILLISECONDS

internal class PastePresenterImpl internal constructor(
  private val enforcer: Enforcer,
  private val interactor: PasteServiceInteractor,
  bus: EventBus<PasteRequestEvent>
) : BasePresenter<PasteRequestEvent, PastePresenter.Callback>(bus),
    PastePresenter {

  private var pasteDisposable by singleDisposable()

  override fun onBind() {
    listen()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { callback.onPaste(it.deepSearchEnabled) }
        .destroy(owner)
  }

  override fun onUnbind() {
    pasteDisposable.tryDispose()
  }

  override fun paste() {
    pasteDisposable = interactor.getPasteDelayTime()
        .observeOn(Schedulers.io())
        .flatMap {
          enforcer.assertNotOnMainThread()
          return@flatMap Single.just(0)
              .subscribeOn(Schedulers.io())
              .observeOn(Schedulers.io())
              .delay(it, MILLISECONDS)
        }
        .flatMap {
          enforcer.assertNotOnMainThread()
          return@flatMap interactor.isDeepSearchEnabled()
              .subscribeOn(Schedulers.io())
              .observeOn(Schedulers.io())
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(Consumer { publish(PasteRequestEvent(it)) })
  }

}