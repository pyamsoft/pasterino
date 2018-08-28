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

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import androidx.annotation.CheckResult
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.pyamsoft.pasterino.Injector
import com.pyamsoft.pasterino.Pasterino
import com.pyamsoft.pasterino.PasterinoComponent
import com.pyamsoft.pydroid.core.addTo
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class PasteService : AccessibilityService() {

  internal lateinit var viewModel: PasteViewModel
  private val compositeDisposable = CompositeDisposable()

  override fun onAccessibilityEvent(event: AccessibilityEvent) {
    Timber.d("onAccessibilityEvent")
  }

  override fun onInterrupt() {
    Timber.e("onInterrupt")
  }

  override fun onCreate() {
    super.onCreate()
    Injector.obtain<PasterinoComponent>(applicationContext)
        .inject(this)
    viewModel.onFinishEvent { onServiceFinishRequested() }
        .addTo(compositeDisposable)
    viewModel.onPasteEvent { onPasteRequested() }
        .addTo(compositeDisposable)
  }

  override fun onServiceConnected() {
    super.onServiceConnected()
    Timber.d("onServiceConnected")

    isRunning = true
    PasteServiceNotification.start(this)
  }

  private fun onPasteRequested() {
    val info = rootInActiveWindow.findFocus(AccessibilityNodeInfo.FOCUS_INPUT)
    if (info != null) {
      Timber.d("Perform paste on target: %s", info)
      info.performAction(AccessibilityNodeInfoCompat.ACTION_PASTE)
      Toast.makeText(
          applicationContext, "Pasting text into current input focus.",
          Toast.LENGTH_SHORT
      )
          .show()
    } else {
      Timber.e("No editable target to paste into")
    }
  }

  private fun onServiceFinishRequested() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      disableSelf()
    }
  }

  override fun onUnbind(intent: Intent): Boolean {
    Timber.d("onUnbind")
    PasteServiceNotification.stop(this)
    isRunning = false
    return super.onUnbind(intent)
  }

  override fun onDestroy() {
    super.onDestroy()
    Pasterino.getRefWatcher(this)
        .watch(this)
    compositeDisposable.clear()
  }

  companion object {

    @JvmStatic
    var isRunning = false
      @get:CheckResult get
      private set
  }
}
