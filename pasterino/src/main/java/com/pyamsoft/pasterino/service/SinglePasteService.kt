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
import com.pyamsoft.pasterino.Pasterino
import com.pyamsoft.pasterino.PasterinoComponent
import com.pyamsoft.pasterino.service.PasteViewModel.PasteState
import com.pyamsoft.pydroid.arch.renderOnChange
import com.pyamsoft.pydroid.ui.Injector
import timber.log.Timber
import javax.inject.Inject

class SinglePasteService : Service() {

  @JvmField @Inject internal var viewModel: PasteViewModel? = null

  override fun onCreate() {
    super.onCreate()
    Injector.obtain<PasterinoComponent>(applicationContext)
        .inject(this)

    requireNotNull(viewModel).bind { state, oldState ->
      renderPaste(state, oldState)
    }
  }

  private fun renderPaste(
    state: PasteState,
    oldState: PasteState?
  ) {
    state.renderOnChange(oldState, value = { it.isDeepSearchEnabled }) { deepSearch ->
      if (deepSearch != null) {
        stopSelf()
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    viewModel?.unbind()

    viewModel = null

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
    requireNotNull(viewModel).paste()
    return START_NOT_STICKY
  }

  companion object {

    @JvmStatic
    fun stop(context: Context) {
      val service = Intent(context.applicationContext, SinglePasteService::class.java)
      context.applicationContext.stopService(service)
    }
  }
}
