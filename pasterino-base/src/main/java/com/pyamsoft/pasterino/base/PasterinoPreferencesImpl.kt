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

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.support.v7.preference.PreferenceManager
import com.pyamsoft.pasterino.base.preference.ClearPreferences
import com.pyamsoft.pasterino.base.preference.PastePreferences

internal class PasterinoPreferencesImpl(context: Context) : PastePreferences, ClearPreferences {

  private val delayTime: String
  private val delayTimeDefault: String
  private val preferences: SharedPreferences

  init {
    val appContext = context.applicationContext
    preferences = PreferenceManager.getDefaultSharedPreferences(appContext)
    delayTime = appContext.getString(R.string.delay_time_key)
    delayTimeDefault = appContext.getString(R.string.delay_time_default)
  }

  override val pasteDelayTime: Long
    get() = preferences.getString(delayTime, delayTimeDefault).toLong()

  @SuppressLint("CommitPrefEdits")
  override fun clearAll() {
    // Make sure we commit so that they are cleared
    preferences.edit().clear().commit()
  }
}
