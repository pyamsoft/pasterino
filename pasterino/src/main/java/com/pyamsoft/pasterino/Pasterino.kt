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

package com.pyamsoft.pasterino

import android.app.Application
import android.support.annotation.CheckResult
import android.support.v4.app.Fragment
import com.pyamsoft.pasterino.base.PasterinoModule
import com.pyamsoft.pasterino.uicore.CanaryDialog
import com.pyamsoft.pasterino.uicore.CanaryFragment
import com.pyamsoft.pydroid.about.Licenses
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.app.fragment.ActionBarSettingsPreferenceFragment
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher

class Pasterino : Application() {

  private lateinit var refWatcher: RefWatcher

  override fun onCreate() {
    super.onCreate()
    if (LeakCanary.isInAnalyzerProcess(this)) {
      return
    }

    PYDroid.initialize(this, BuildConfig.DEBUG)
    Licenses.create("Firebase", "https://firebase.google.com", "licenses/firebase")

    Injector.set(PasterinoComponent.withModule(PasterinoModule(this)))

    if (BuildConfig.DEBUG) {
      refWatcher = LeakCanary.install(this)
    } else {
      refWatcher = RefWatcher.DISABLED
    }
  }

  companion object {

    @JvmStatic
    @CheckResult fun getRefWatcher(fragment: ActionBarSettingsPreferenceFragment): RefWatcher {
      return getRefWatcherInternal(fragment)
    }

    @JvmStatic
    @CheckResult fun getRefWatcher(fragment: CanaryFragment): RefWatcher {
      return getRefWatcherInternal(fragment)
    }

    @JvmStatic
    @CheckResult fun getRefWatcher(dialog: CanaryDialog): RefWatcher {
      return getRefWatcherInternal(dialog)
    }

    @JvmStatic
    @CheckResult private fun getRefWatcherInternal(fragment: Fragment): RefWatcher {
      val application = fragment.activity.application
      if (application is Pasterino) {
        return application.refWatcher
      } else {
        throw IllegalStateException("Application is not Pasterino")
      }
    }
  }
}

