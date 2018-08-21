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
import com.pyamsoft.pasterino.lifecycle.fakeBind
import com.pyamsoft.pasterino.lifecycle.fakeRelease
import com.pyamsoft.pasterino.model.ServiceEvent
import com.pyamsoft.pydroid.core.bus.Publisher
import timber.log.Timber

class SinglePasteService : Service(), LifecycleOwner {

  private val lifecycle = LifecycleRegistry(this)
  internal lateinit var viewModel: PasteViewModel
  internal lateinit var publisher: Publisher<ServiceEvent>

  override fun getLifecycle(): Lifecycle {
    return lifecycle
  }

  override fun onCreate() {
    super.onCreate()
    Injector.obtain<PasterinoComponent>(applicationContext)
        .inject(this)
    lifecycle.fakeBind()

    viewModel.onPostEvent(this) { onPost() }
  }

  override fun onDestroy() {
    super.onDestroy()
    lifecycle.fakeRelease()
    Pasterino.getRefWatcher(this)
        .watch(this)
  }

  override fun onBind(intent: Intent): IBinder? = null

  override fun onStartCommand(
    intent: Intent?,
    flags: Int,
    startId: Int
  ): Int {
    Timber.d("Attempt single paste")
    viewModel.post(this)
    return Service.START_NOT_STICKY
  }

  private fun onPost() {
    publisher.publish(ServiceEvent(ServiceEvent.Type.PASTE))
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
