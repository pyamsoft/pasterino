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

package com.pyamsoft.pasterino

import android.app.Application
import android.app.Service
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.theme.Theming
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher

class Pasterino : Application(), PYDroid.Instance {

  private var pyDroid: PYDroid? = null
  private lateinit var component: PasterinoComponent
  private lateinit var refWatcher: RefWatcher

  override fun onCreate() {
    super.onCreate()
    if (LeakCanary.isInAnalyzerProcess(this)) {
      return
    }

    if (BuildConfig.DEBUG) {
      refWatcher = LeakCanary.install(this)
    } else {
      refWatcher = RefWatcher.DISABLED
    }

    Theming.IS_DEFAULT_DARK_THEME = false
    PYDroid.init(
        this,
        this,
        getString(R.string.app_name),
        "https://github.com/pyamsoft/pasterino/issues",
        BuildConfig.VERSION_CODE,
        BuildConfig.DEBUG
    )
  }

  override fun getSystemService(name: String): Any {
    if (Injector.name == name) {
      return component
    } else {
      return super.getSystemService(name)
    }
  }

  override fun getPydroid(): PYDroid? = pyDroid

  override fun setPydroid(instance: PYDroid) {
    pyDroid = instance.also {
      component = PasterinoComponentImpl(this, it.modules())
    }
  }

  companion object {

    @JvmStatic
    @CheckResult
    fun getRefWatcher(service: Service): RefWatcher = getRefWatcherInternal(service.application)

    @JvmStatic
    @CheckResult
    private fun getRefWatcherInternal(application: Application): RefWatcher {
      if (application is Pasterino) {
        return application.refWatcher
      } else {
        throw IllegalStateException("Application is not Pasterino")
      }
    }
  }
}
