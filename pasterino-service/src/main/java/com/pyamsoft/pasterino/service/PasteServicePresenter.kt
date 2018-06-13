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

import com.pyamsoft.pasterino.model.ServiceEvent
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.core.presenter.Presenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class PasteServicePresenter internal constructor(
  private val bus: EventBus<ServiceEvent>
) : Presenter<PasteServicePresenter.View>() {

  override fun onCreate() {
    super.onCreate()
    registerOnBus()
  }

  private fun registerOnBus() {
    dispose {
      bus.listen()
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe({ (type) ->
            when (type) {
              ServiceEvent.Type.FINISH -> view?.onServiceFinishRequested()
              ServiceEvent.Type.PASTE -> view?.onPasteRequested()
              else -> throw IllegalArgumentException(
                  "Invalid ServiceEvent.Type: $type"
              )
            }
          }, { Timber.e(it, "onError event bus") })
    }
  }

  interface View : ServiceCallback

  interface ServiceCallback {

    fun onPasteRequested()

    fun onServiceFinishRequested()
  }
}
