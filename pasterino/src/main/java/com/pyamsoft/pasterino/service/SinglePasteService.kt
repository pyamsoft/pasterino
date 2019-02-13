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

package com.pyamsoft.pasterino.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.pyamsoft.pasterino.Injector
import com.pyamsoft.pasterino.Pasterino
import com.pyamsoft.pasterino.PasterinoComponent
import com.pyamsoft.pydroid.util.fakeBind
import com.pyamsoft.pydroid.util.fakeUnbind
import timber.log.Timber

class SinglePasteService : Service(), LifecycleOwner, PastePresenter.Callback {

  internal lateinit var presenter: PastePresenter

  private val registry = LifecycleRegistry(this)

  override fun getLifecycle(): Lifecycle {
    return registry
  }

  override fun onCreate() {
    super.onCreate()
    Injector.obtain<PasterinoComponent>(applicationContext)
        .plusServiceComponent(this)
        .inject(this)

    presenter.bind(this)

    registry.fakeBind()
  }

  override fun onDestroy() {
    super.onDestroy()
    Pasterino.getRefWatcher(this)
        .watch(this)

    registry.fakeUnbind()
  }

  override fun onBind(intent: Intent): IBinder? = null

  override fun onStartCommand(
    intent: Intent?,
    flags: Int,
    startId: Int
  ): Int {
    Timber.d("Attempt single paste")
    presenter.paste()
    return Service.START_NOT_STICKY
  }

  override fun onPaste(deepSearchEnabled: Boolean) {
    stopSelf()
  }

  companion object {

    @JvmStatic
    fun stop(context: Context) {
      val service = Intent(context.applicationContext, SinglePasteService::class.java)
      context.applicationContext.stopService(service)
    }
  }
}
