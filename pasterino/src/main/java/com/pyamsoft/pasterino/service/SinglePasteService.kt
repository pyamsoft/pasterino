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

package com.pyamsoft.pasterino.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.pyamsoft.pasterino.Injector
import com.pyamsoft.pasterino.service.SinglePastePresenter.SinglePasteCallback
import timber.log.Timber

class SinglePasteService : Service() {

  internal val handler: Handler = Handler(Looper.getMainLooper())
  internal lateinit var presenter: SinglePastePresenter

  override fun onCreate() {
    super.onCreate()
    Injector.get().provideComponent().plusPasteComponent().inject(this)
  }

  override fun onDestroy() {
    super.onDestroy()
    handler.removeCallbacksAndMessages(null)
    presenter.stop()
    presenter.destroy()
  }

  override fun onBind(intent: Intent): IBinder? {
    return null
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    Timber.d("Attempt single paste")
    presenter.postDelayedEvent(object : SinglePasteCallback {
      override fun onPost(delay: Long) {
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
          PasteService.pasteIntoCurrentFocus()
          stopSelf()
        }, delay)
      }
    })
    return Service.START_NOT_STICKY
  }

  companion object {

    @JvmStatic
    fun stop(context: Context) {
      val service = Intent(context.applicationContext, SinglePasteService::class.java)
      context.applicationContext.stopService(service)
    }
  }
}
