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

import com.pyamsoft.pasterino.api.MainSettingsPreferenceInteractor
import com.pyamsoft.pasterino.model.ConfirmEvent
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.core.presenter.Presenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class MainSettingsPreferencePresenter internal constructor(
  private val interactor: MainSettingsPreferenceInteractor,
  private val bus: EventBus<ConfirmEvent>
) : Presenter<MainSettingsPreferencePresenter.View>() {

  override fun onCreate() {
    super.onCreate()
    registerOnEventBus()
  }

  private fun registerOnEventBus() {
    dispose {
      bus.listen()
          .flatMapSingle { interactor.clearAll() }
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe({ view?.onClearAll() }, { Timber.e(it, "OnError EventBus") })
    }
  }

  interface View : ClearCallback

  interface ClearCallback {

    fun onClearAll()
  }
}
