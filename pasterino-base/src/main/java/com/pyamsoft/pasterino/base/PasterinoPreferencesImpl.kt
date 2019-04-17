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

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.pyamsoft.pasterino.api.ClearPreferences
import com.pyamsoft.pasterino.api.PastePreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class PasterinoPreferencesImpl @Inject internal constructor(
  context: Context
) : PastePreferences, ClearPreferences {

  private val preferences: SharedPreferences

  private val delayTime: String
  private val delayTimeDefault: String

  private val deepSearch: String
  private val deepSearchDefault: Boolean

  init {
    val appContext = context.applicationContext
    preferences = PreferenceManager.getDefaultSharedPreferences(appContext)

    appContext.resources.also {
      delayTime = it.getString(R.string.delay_time_key_v2)
      delayTimeDefault = it.getString(R.string.delay_time_default_v2)

      deepSearch = it.getString(R.string.deep_search_key_v1)
      deepSearchDefault = it.getBoolean(R.bool.deep_search_default_v1)
    }
  }

  override val pasteDelayTime: Long
    get() = preferences.getString(delayTime, delayTimeDefault).orEmpty().toLong()

  override val isDeepSearchEnabled: Boolean
    get() = preferences.getBoolean(deepSearch, deepSearchDefault)

  @SuppressLint("ApplySharedPref")
  override fun clearAll() {
    // Make sure we commit so that they are cleared
    preferences.edit()
        .clear()
        .commit()
  }
}
