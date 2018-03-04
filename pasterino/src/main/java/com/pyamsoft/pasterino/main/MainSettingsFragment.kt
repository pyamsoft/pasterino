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

import com.pyamsoft.pasterino.Pasterino
import com.pyamsoft.pydroid.ui.app.fragment.AppSettingsFragment
import com.pyamsoft.pydroid.ui.app.fragment.SettingsPreferenceFragment

class MainSettingsFragment : AppSettingsFragment() {

  override fun provideSettingsFragment(): SettingsPreferenceFragment =
    MainSettingsPreferenceFragment()

  override fun provideSettingsTag(): String = MainSettingsPreferenceFragment.TAG

  override fun onDestroy() {
    super.onDestroy()
    Pasterino.getRefWatcher(this)
        .watch(this)
  }

  companion object {
    const val TAG = "MainSettingsFragment"
  }
}
