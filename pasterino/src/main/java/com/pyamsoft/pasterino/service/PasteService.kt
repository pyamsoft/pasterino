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

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Build
import android.support.annotation.CheckResult
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.pyamsoft.pasterino.Injector
import com.pyamsoft.pasterino.PasterinoComponent
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
    Injector.obtain<PasterinoComponent>(applicationContext).inject(this)
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
