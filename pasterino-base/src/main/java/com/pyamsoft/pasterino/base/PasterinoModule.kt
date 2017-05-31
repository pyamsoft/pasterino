/*
 * Copyright 2017 Peter Kenji Yamanaka
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
 */

package com.pyamsoft.pasterino.base

import android.content.Context
import android.support.annotation.CheckResult
import com.pyamsoft.pasterino.base.preference.ClearPreferences
import com.pyamsoft.pasterino.base.preference.PastePreferences
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class PasterinoModule(context: Context) {

  private val preferences: PasterinoPreferencesImpl = PasterinoPreferencesImpl(
      context.applicationContext)

  @CheckResult fun providePreferences(): PastePreferences {
    return preferences
  }

  @CheckResult fun provideClearPreferences(): ClearPreferences {
    return preferences
  }

  @CheckResult fun provideObsScheduler(): Scheduler {
    return AndroidSchedulers.mainThread()
  }

  @CheckResult fun provideSubScheduler(): Scheduler {
    return Schedulers.io()
  }
}
