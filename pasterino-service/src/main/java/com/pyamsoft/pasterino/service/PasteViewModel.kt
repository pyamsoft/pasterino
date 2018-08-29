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

import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pasterino.api.PasteServiceInteractor
import com.pyamsoft.pasterino.model.ServiceEvent
import com.pyamsoft.pasterino.model.ServiceEvent.Type.FINISH
import com.pyamsoft.pasterino.model.ServiceEvent.Type.PASTE
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.core.bus.RxBus
import com.pyamsoft.pydroid.core.threads.Enforcer
import com.pyamsoft.pydroid.core.viewmodel.BaseViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit.MILLISECONDS

class PasteViewModel internal constructor(
  owner: LifecycleOwner,
  private val enforcer: Enforcer,
  private val interactor: PasteServiceInteractor,
  private val bus: EventBus<ServiceEvent>
) : BaseViewModel(owner) {

  private val pasteBus = RxBus.create<Unit>()

  fun onFinishEvent(func: (ServiceEvent.Type) -> Unit) {
    dispose {
      bus.listen()
          .map { it.type }
          .filter { it == FINISH }
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(func)
    }
  }

  fun onPasteEvent(func: (ServiceEvent.Type) -> Unit) {
    dispose {
      bus.listen()
          .map { it.type }
          .filter { it == PASTE }
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(func)
    }
  }

  fun onPostEvent(func: () -> Unit) {
    dispose {
      pasteBus.listen()
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe { func() }
    }
  }

  fun post() {
    dispose {
      interactor.getPasteDelayTime()
          .flatMap {
            enforcer.assertNotOnMainThread()
            return@flatMap Single.just(0)
                .delay(it, MILLISECONDS)
          }
          .subscribeOn(Schedulers.io())
          .observeOn(Schedulers.io())
          .subscribe({ pasteBus.publish(Unit) }, { Timber.e(it, "Error on post") })
    }
  }

}
