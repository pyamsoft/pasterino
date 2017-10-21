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

package com.pyamsoft.pasterino.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.pyamsoft.pasterino.Injector
import com.pyamsoft.pasterino.PasterinoComponent
import com.pyamsoft.pasterino.model.ServiceEvent
import com.pyamsoft.pasterino.service.SinglePastePresenter.View
import timber.log.Timber

class SinglePasteService : Service(), View {

  private val handler: Handler = Handler(Looper.getMainLooper())
  internal lateinit var presenter: SinglePastePresenter
  internal lateinit var publisher: PasteServicePublisher

  override fun onCreate() {
    super.onCreate()
    Injector.obtain<PasterinoComponent>(applicationContext).inject(this)
    presenter.bind(this)
  }

  override fun onDestroy() {
    super.onDestroy()
    handler.removeCallbacksAndMessages(null)
    presenter.unbind()
  }

  override fun onBind(intent: Intent): IBinder? = null

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    Timber.d("Attempt single paste")
    presenter.postDelayedEvent()
    return Service.START_NOT_STICKY
  }

  override fun onPost(delay: Long) {
    handler.removeCallbacksAndMessages(null)
    handler.postDelayed({
      publisher.publish(ServiceEvent(ServiceEvent.Type.PASTE))
      stopSelf()
    }, delay)
  }

  companion object {

    @JvmStatic
    fun stop(context: Context) {
      val service = Intent(context.applicationContext, SinglePasteService::class.java)
      context.applicationContext.stopService(service)
    }
  }
}
