/*
 * Copyright 2020 Peter Kenji Yamanaka
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

package com.pyamsoft.pasterino.service.monitor

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pasterino.PasterinoComponent
import com.pyamsoft.pasterino.service.monitor.ServiceEvent.PasteEvent
import com.pyamsoft.pasterino.service.monitor.notification.NotificationHandler
import com.pyamsoft.pydroid.ui.Injector
import com.pyamsoft.pydroid.ui.util.Toaster
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE
import timber.log.Timber

class PasteService : AccessibilityService(), LifecycleOwner {

  @JvmField @Inject internal var binder: PasteBinder? = null

  @JvmField @Inject internal var notificationHandler: NotificationHandler? = null

  private val registry by lazy(NONE) { LifecycleRegistry(this) }

  override fun getLifecycle(): Lifecycle {
    return registry
  }

  override fun onAccessibilityEvent(event: AccessibilityEvent) {
    Timber.d("onAccessibilityEvent")
  }

  override fun onInterrupt() {
    Timber.e("onInterrupt")
  }

  override fun onCreate() {
    super.onCreate()
    Injector.obtainFromApplication<PasterinoComponent>(this).inject(this)

    requireNotNull(binder).bind(lifecycleScope) {
      return@bind when (it) {
        is PasteEvent -> performPaste(it.isDeepSearchEnabled)
      }
    }

    registry.currentState = Lifecycle.State.RESUMED
  }

  private fun performPaste(deepSearch: Boolean) {
    requireNotNull(binder).handlePasteInput(lifecycleScope, deepSearch, rootInActiveWindow) { result
      ->
      if (result) {
        Toaster.bindTo(this).short(applicationContext, "Pasting text into currently focused input")
      } else {
        Toaster.bindTo(this).short(applicationContext, "Nothing to paste into")
      }
    }
  }

  override fun onServiceConnected() {
    super.onServiceConnected()
    requireNotNull(binder).handleStart(lifecycleScope)
    requireNotNull(notificationHandler).start()
  }

  override fun onUnbind(intent: Intent): Boolean {
    notificationHandler?.stop()
    binder?.handleStop(lifecycleScope)
    return super.onUnbind(intent)
  }

  override fun onDestroy() {
    super.onDestroy()

    binder?.unbind()
    binder = null

    registry.currentState = Lifecycle.State.DESTROYED
  }
}
