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

package com.pyamsoft.pasterino.base

import com.pyamsoft.pasterino.api.PastePreferences
import com.pyamsoft.pasterino.api.PasteServiceInteractor
import com.pyamsoft.pydroid.core.bus.RxBus
import com.pyamsoft.pydroid.core.threads.Enforcer
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class PasteServiceInteractorImpl @Inject internal constructor(
  private val enforcer: Enforcer,
  private val preferences: PastePreferences
) : PasteServiceInteractor {

  private var running = false

  private val runningStateBus = RxBus.create<Boolean>()

  override fun setServiceState(start: Boolean) {
    running = start
    runningStateBus.publish(start)
  }

  override fun observeServiceState(): Observable<Boolean> {
    return runningStateBus.listen()
        .startWith(running)
  }

  override fun getPasteDelayTime(): Single<Long> {
    return Single.fromCallable {
      enforcer.assertNotOnMainThread()
      return@fromCallable preferences.pasteDelayTime
    }
  }

  override fun isDeepSearchEnabled(): Single<Boolean> {
    return Single.fromCallable {
      enforcer.assertNotOnMainThread()
      return@fromCallable preferences.isDeepSearchEnabled
    }
  }
}