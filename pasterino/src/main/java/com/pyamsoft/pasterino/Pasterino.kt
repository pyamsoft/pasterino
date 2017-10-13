/*
 *     Copyright (C) 2017 Peter Kenji Yamanaka
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
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
  private var component: PasterinoComponent? = null

  override fun onCreate() {
    super.onCreate()
    if (LeakCanary.isInAnalyzerProcess(this)) {
      return
    }

    PYDroid.initialize(this, BuildConfig.DEBUG)
    Licenses.create("Firebase", "https://firebase.google.com", "licenses/firebase")
    component = PasterinoComponentImpl(PasterinoModule(applicationContext))

    refWatcher = if (BuildConfig.DEBUG) {
      LeakCanary.install(this)
    } else {
      RefWatcher.DISABLED
    }
  }

  override fun getSystemService(name: String?): Any {
    return if (Injector.name == name) {
      // Return
      component ?: throw IllegalStateException("PadLock component is NULL")
    } else {

      // Return
      super.getSystemService(name)
    }
  }

  companion object {

    @JvmStatic
    @CheckResult
    fun getRefWatcher(fragment: ActionBarSettingsPreferenceFragment): RefWatcher =
        getRefWatcherInternal(fragment)

    @JvmStatic
    @CheckResult
    fun getRefWatcher(fragment: CanaryFragment): RefWatcher =
        getRefWatcherInternal(fragment)

    @JvmStatic
    @CheckResult
    fun getRefWatcher(dialog: CanaryDialog): RefWatcher = getRefWatcherInternal(dialog)

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

