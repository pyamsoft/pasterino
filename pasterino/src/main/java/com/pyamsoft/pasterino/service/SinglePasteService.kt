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
import com.pyamsoft.pasterino.Injector
import com.pyamsoft.pasterino.Pasterino
import com.pyamsoft.pasterino.PasterinoComponent
import com.pyamsoft.pasterino.model.ServiceEvent
import com.pyamsoft.pydroid.core.addTo
import com.pyamsoft.pydroid.core.bus.Publisher
import com.pyamsoft.pydroid.core.disposable
import com.pyamsoft.pydroid.core.tryDispose
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class SinglePasteService : Service() {

  internal lateinit var viewModel: PasteViewModel
  internal lateinit var publisher: Publisher<ServiceEvent>

  private val compositeDisposable = CompositeDisposable()
  private var postDisposable by disposable()

  override fun onCreate() {
    super.onCreate()
    Injector.obtain<PasterinoComponent>(applicationContext)
        .inject(this)

    viewModel.onPostEvent { onPost() }
        .addTo(compositeDisposable)
  }

  override fun onDestroy() {
    super.onDestroy()
    Pasterino.getRefWatcher(this)
        .watch(this)

    compositeDisposable.clear()
    postDisposable.tryDispose()
  }

  override fun onBind(intent: Intent): IBinder? = null

  override fun onStartCommand(
    intent: Intent?,
    flags: Int,
    startId: Int
  ): Int {
    Timber.d("Attempt single paste")
    postDisposable = viewModel.post()
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
