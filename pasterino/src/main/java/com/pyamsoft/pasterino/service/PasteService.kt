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
import com.pyamsoft.pydroid.core.singleDisposable
import com.pyamsoft.pydroid.core.tryDispose
import timber.log.Timber

class PasteService : AccessibilityService() {

  internal lateinit var pasteWorker: PasteServiceWorker
  internal lateinit var finishWorker: ServiceFinishWorker
  internal lateinit var serviceStateWorker: ServiceStateWorker

  private var finishDisposable by singleDisposable()
  private var pasteDisposable by singleDisposable()

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

    finishDisposable = finishWorker.onFinishEvent { onServiceFinishRequested() }
    pasteDisposable = pasteWorker.onPasteEvent { onPasteRequested(it.deepSearchEnabled) }
  }

  override fun onServiceConnected() {
    super.onServiceConnected()
    Timber.d("onServiceConnected")

    serviceStateWorker.setServiceState(true)
    PasteServiceNotification.start(this)
  }

  @CheckResult
  private fun isNodeFocusedAndEditable(node: AccessibilityNodeInfo): Boolean {
    return (node.isFocused || node.isAccessibilityFocused) && node.isEditable
  }

  @CheckResult
  private fun findFocusedNode(node: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
    if (node == null) {
      return null
    }

    if (isNodeFocusedAndEditable(node)) {
      return node
    }

    for (i in 0 until node.childCount) {
      val childNode = node.getChild(i)
      val focusedChildNode = findFocusedNode(childNode)
      if (focusedChildNode != null) {
        return focusedChildNode
      }
    }

    return null
  }

  private fun onPasteRequested(isDeepSearchEnabled: Boolean) {
    var node: AccessibilityNodeInfo? = rootInActiveWindow

    if (node != null && isNodeFocusedAndEditable(node)) {
      Timber.d("rootInActiveWindow node is paste target")
      pasteIntoNode(node)
      return
    }

    node = rootInActiveWindow.findFocus(AccessibilityNodeInfo.FOCUS_INPUT)
    if (node != null && isNodeFocusedAndEditable(node)) {
      Timber.d("root.FOCUS_INPUT node is paste target")
      pasteIntoNode(node)
      return
    }

    node = rootInActiveWindow.findFocus(AccessibilityNodeInfo.FOCUS_ACCESSIBILITY)
    if (node != null && isNodeFocusedAndEditable(node)) {
      Timber.d("root.FOCUS_ACCESSIBLE node is paste target")
      pasteIntoNode(node)
      return
    }

    if (isDeepSearchEnabled) {
      node = findFocusedNode(rootInActiveWindow)
      if (node != null && isNodeFocusedAndEditable(node)) {
        Timber.d("recursive result node is paste target")
        pasteIntoNode(node)
        return
      }
    }

    Timber.e("No editable target to paste into")
    Toast.makeText(applicationContext, "Nothing to paste into.", Toast.LENGTH_SHORT)
        .show()
  }

  private fun pasteIntoNode(node: AccessibilityNodeInfo) {
    Timber.d("Perform paste on target: %s", node)
    node.performAction(AccessibilityNodeInfoCompat.ACTION_PASTE)
    Toast.makeText(applicationContext, "Pasting text into current input.", Toast.LENGTH_SHORT)
        .show()
  }

  private fun onServiceFinishRequested() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      disableSelf()
    }
  }

  override fun onUnbind(intent: Intent): Boolean {
    Timber.d("onUnbind")

    PasteServiceNotification.stop(this)
    serviceStateWorker.setServiceState(false)
    return super.onUnbind(intent)
  }

  override fun onDestroy() {
    super.onDestroy()

    finishDisposable.tryDispose()
    pasteDisposable.tryDispose()

    Pasterino.getRefWatcher(this)
        .watch(this)
  }
}
