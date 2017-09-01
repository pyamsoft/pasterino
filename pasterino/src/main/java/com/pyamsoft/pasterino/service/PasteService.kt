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

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Build
import android.support.annotation.CheckResult
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.pyamsoft.pasterino.Injector
import com.pyamsoft.pasterino.service.PasteServicePresenter.Callback
import com.pyamsoft.pydroid.ui.helper.Toasty
import timber.log.Timber

class PasteService : AccessibilityService(), Callback {

  internal lateinit var presenter: PasteServicePresenter

  override fun onAccessibilityEvent(event: AccessibilityEvent) {
    Timber.d("onAccessibilityEvent")
  }

  override fun onInterrupt() {
    Timber.e("onInterrupt")
  }

  override fun onCreate() {
    super.onCreate()
    Injector.with(this) {
      it.inject(this)
    }

    presenter.bind(this)
  }

  override fun onServiceConnected() {
    super.onServiceConnected()
    Timber.d("onServiceConnected")

    isRunning = true
    PasteServiceNotification.start(this)
  }

  override fun onPasteRequested() {
    val info = rootInActiveWindow.findFocus(AccessibilityNodeInfo.FOCUS_INPUT)
    if (info != null && info.isEditable) {
      Timber.d("Perform paste on target: %s", info.viewIdResourceName)
      info.performAction(AccessibilityNodeInfoCompat.ACTION_PASTE)
      Toasty.makeText(applicationContext, "Pasting text into current input focus.",
          Toasty.LENGTH_SHORT).show()
    } else {
      Timber.e("No editable target to paste into")
    }
  }

  override fun onServiceFinishRequested() {
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
    presenter.unbind()
  }

  companion object {

    @JvmStatic
    var isRunning = false
      @get:CheckResult get
      private set
  }
}
