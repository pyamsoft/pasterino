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

package com.pyamsoft.pasterino.main

import androidx.annotation.CheckResult
import androidx.recyclerview.widget.RecyclerView
import com.pyamsoft.pasterino.model.FabScrollListenerRequestEvent
import com.pyamsoft.pydroid.core.bus.EventBus
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MainFragmentViewModel internal constructor(
  private val fabScrollRequestBus: EventBus<FabScrollListenerRequestEvent>
) {

  @CheckResult
  fun onFabScrollListenerCreateRequest(func: (tag: String) -> Unit): Disposable {
    return fabScrollRequestBus.listen()
        // Do not listen to create results, only requests for new creations
        .filter { it.listenerResult == null }
        // Just need the tag
        .map { it.requestTag }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(func)
  }

  fun publishScrollListener(
    tag: String,
    listener: RecyclerView.OnScrollListener
  ) {
    fabScrollRequestBus.publish(
        FabScrollListenerRequestEvent(tag, listener)
    )
  }
}
