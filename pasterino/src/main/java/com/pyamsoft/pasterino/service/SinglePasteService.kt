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
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.os.postDelayed
import com.pyamsoft.pasterino.Injector
import com.pyamsoft.pasterino.Pasterino
import com.pyamsoft.pasterino.PasterinoComponent
import com.pyamsoft.pasterino.lifecycle.fakeBind
import com.pyamsoft.pasterino.lifecycle.fakeRelease
import com.pyamsoft.pasterino.model.ServiceEvent
import timber.log.Timber

class SinglePasteService : Service(), SinglePastePresenter.View, LifecycleOwner {

  private val lifecycle = LifecycleRegistry(this)
  private val handler: Handler = Handler(Looper.getMainLooper())
  internal lateinit var presenter: SinglePastePresenter
  internal lateinit var publisher: PasteServicePublisher

  override fun getLifecycle(): Lifecycle {
    return lifecycle
  }

  override fun onCreate() {
    super.onCreate()
    Injector.obtain<PasterinoComponent>(applicationContext)
        .inject(this)
    presenter.bind(this, this)
    lifecycle.fakeBind()
  }

  override fun onDestroy() {
    super.onDestroy()
    handler.removeCallbacksAndMessages(null)
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
    presenter.postDelayedEvent()
    return Service.START_NOT_STICKY
  }

  override fun onPost(delay: Long) {
    handler.removeCallbacksAndMessages(null)
    handler.postDelayed(delay) {
      publisher.publish(ServiceEvent(ServiceEvent.Type.PASTE))
      stopSelf()
    }
  }

  companion object {

    @JvmStatic
    fun stop(context: Context) {
      val service = Intent(context.applicationContext, SinglePasteService::class.java)
      context.applicationContext.stopService(service)
    }
  }
}
