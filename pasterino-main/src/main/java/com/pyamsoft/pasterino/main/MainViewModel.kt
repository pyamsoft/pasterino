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

package com.pyamsoft.pasterino.main

import androidx.annotation.CheckResult
import androidx.recyclerview.widget.RecyclerView
import com.pyamsoft.pasterino.api.MainInteractor
import com.pyamsoft.pasterino.model.ConfirmEvent
import com.pyamsoft.pasterino.model.FabScrollListenerRequestEvent
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.core.bus.Listener
import com.pyamsoft.pydroid.core.threads.Enforcer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MainViewModel internal constructor(
  private val enforcer: Enforcer,
  private val interactor: MainInteractor,
  private val scrollListenerBus: EventBus<FabScrollListenerRequestEvent>,
  private val bus: Listener<ConfirmEvent>,
  private val tag: String
) {

  @CheckResult
  fun onClearAllEvent(func: () -> Unit): Disposable {
    return bus.listen()
        .observeOn(Schedulers.io())
        .flatMapSingle {
          enforcer.assertNotOnMainThread()
          return@flatMapSingle interactor.clearAll()
              .subscribeOn(Schedulers.io())
              .observeOn(Schedulers.io())
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { func() }
  }

  @CheckResult
  fun onScrollListenerCreated(func: (RecyclerView.OnScrollListener) -> Unit): Disposable {
    return scrollListenerBus.listen()
        .filter { it.listenerResult != null }
        .filter { it.requestTag == tag }
        .map { requireNotNull(it.listenerResult) }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(func)
  }

  fun publishScrollListenerCreateRequest() {
    scrollListenerBus.publish(FabScrollListenerRequestEvent(tag))
  }
}
