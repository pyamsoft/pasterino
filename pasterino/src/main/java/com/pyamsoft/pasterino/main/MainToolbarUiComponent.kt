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

import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.core.bus.Listener
import com.pyamsoft.pydroid.ui.arch.UiComponent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

internal class MainToolbarUiComponent internal constructor(
  private val toolbarView: MainToolbarView,
  private val uiBus: Listener<MainViewEvent>,
  owner: LifecycleOwner
) : UiComponent<MainViewEvent>(owner) {

  override fun id(): Int {
    return toolbarView.id()
  }

  override fun create(savedInstanceState: Bundle?) {
    toolbarView.inflate(savedInstanceState)
    owner.runOnDestroy { toolbarView.teardown() }
  }

  override fun onUiEvent(): Observable<MainViewEvent> {
    return uiBus.listen()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
  }

  override fun saveState(outState: Bundle) {
    toolbarView.saveState(outState)
  }

}
