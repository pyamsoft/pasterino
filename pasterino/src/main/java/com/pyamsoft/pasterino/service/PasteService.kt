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
import com.pyamsoft.pasterino.Pasterino
import com.pyamsoft.pasterino.PasterinoComponent
import com.pyamsoft.pasterino.service.PasteViewModel.PasteState
import com.pyamsoft.pasterino.service.ServiceFinishViewModel.FinishState
import com.pyamsoft.pydroid.arch.renderOnChange
import com.pyamsoft.pydroid.ui.Injector
import timber.log.Timber
import javax.inject.Inject

class PasteService : AccessibilityService() {

  @field:Inject internal lateinit var pasteViewModel: PasteViewModel
  @field:Inject internal lateinit var finishViewModel: ServiceFinishViewModel
  @field:Inject internal lateinit var stateViewModel: ServiceStateViewModel

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

    pasteViewModel.bind { state, oldState ->
      renderPaste(state, oldState)
    }
    finishViewModel.bind { state, oldState ->
      renderFinish(state, oldState)
    }
  }

  private fun renderFinish(
    state: FinishState,
    oldState: FinishState?
  ) {
    state.renderOnChange(oldState, value = { it.isFinished }) { finished ->
      if (finished) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
          disableSelf()
        }
      }
    }
  }

  private fun renderPaste(
    state: PasteState,
    oldState: PasteState?
  ) {
    state.renderOnChange(oldState, value = { it.isDeepSearchEnabled }) { deepSearch ->
      if (deepSearch != null) {
        performPaste(deepSearch.isEnabled)
      }
    }
  }

  private fun performPaste(deepSearch: Boolean) {
    var node: AccessibilityNodeInfo? = rootInActiveWindow
    Timber.d("Start search for paste target at root: $node")

    if (node != null && isNodeFocusedAndEditable(node)) {
      Timber.d("rootInActiveWindow node is paste target")
      pasteIntoNode(node)
      return
    }

    Timber.w("rootInActiveWindow was not paste target")
    node = rootInActiveWindow?.findFocus(AccessibilityNodeInfo.FOCUS_INPUT)
    if (node != null && isNodeFocusedAndEditable(node)) {
      Timber.d("root.FOCUS_INPUT node is paste target")
      pasteIntoNode(node)
      return
    }

    Timber.w("root.FOCUS_INPUT was not paste target")
    node = rootInActiveWindow?.findFocus(AccessibilityNodeInfo.FOCUS_ACCESSIBILITY)
    if (node != null && isNodeFocusedAndEditable(node)) {
      Timber.d("root.FOCUS_ACCESSIBLE node is paste target")
      pasteIntoNode(node)
      return
    }

    Timber.w("root.FOCUS_ACCESSIBLE was not paste target")
    if (deepSearch) {
      node = findFocusedNode(rootInActiveWindow)
      if (node != null && isNodeFocusedAndEditable(node)) {
        Timber.d("recursive result node is paste target")
        pasteIntoNode(node)
        return
      }
      Timber.w("recursive result node was not paste target")
    }

    Timber.e("No editable target to paste into")
    Toast.makeText(applicationContext, "Nothing to paste into.", Toast.LENGTH_SHORT)
        .show()
  }

  override fun onServiceConnected() {
    super.onServiceConnected()
    stateViewModel.start()
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

  private fun pasteIntoNode(node: AccessibilityNodeInfo) {
    Timber.d("Perform paste on target: %s", node)
    node.performAction(AccessibilityNodeInfoCompat.ACTION_PASTE)
    Toast.makeText(applicationContext, "Pasting text into current input.", Toast.LENGTH_SHORT)
        .show()
  }

  override fun onUnbind(intent: Intent): Boolean {
    PasteServiceNotification.stop(this)
    stateViewModel.stop()
    return super.onUnbind(intent)
  }

  override fun onDestroy() {
    super.onDestroy()
    pasteViewModel.unbind()
    finishViewModel.unbind()

    Pasterino.getRefWatcher(this)
        .watch(this)
  }
}
