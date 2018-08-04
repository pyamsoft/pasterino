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

import com.pyamsoft.pasterino.api.PastePreferences
import com.pyamsoft.pasterino.api.PasteServiceInteractor
import com.pyamsoft.pydroid.core.threads.Enforcer
import io.reactivex.Single

internal class PasteServiceInteractorImpl internal constructor(
  private val enforcer: Enforcer,
  private val preferences: PastePreferences
) : PasteServiceInteractor {

  override fun getPasteDelayTime(): Single<Long> =
    Single.fromCallable {
      enforcer.assertNotOnMainThread()
      return@fromCallable preferences.pasteDelayTime
    }
}
