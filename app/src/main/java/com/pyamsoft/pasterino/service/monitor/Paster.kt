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

import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.CheckResult
import timber.log.Timber

object Paster {

  @CheckResult
  fun performPaste(
      deepSearch: Boolean,
      rootNode: AccessibilityNodeInfo?,
      inputFocus: AccessibilityNodeInfo?,
      accessibilityFocus: AccessibilityNodeInfo?
  ): AccessibilityNodeInfo? {
    var node = rootNode
    Timber.d("Start search for paste target at root: $node")

    if (node != null && isNodeFocusedAndEditable(node)) {
      Timber.d("rootInActiveWindow node is paste target")
      return node
    }

    Timber.w("rootInActiveWindow was not paste target")
    node = inputFocus
    if (node != null && isNodeFocusedAndEditable(node)) {
      Timber.d("root.FOCUS_INPUT node is paste target")
      return node
    }

    Timber.w("root.FOCUS_INPUT was not paste target")
    node = accessibilityFocus
    if (node != null && isNodeFocusedAndEditable(node)) {
      Timber.d("root.FOCUS_ACCESSIBLE node is paste target")
      return node
    }

    Timber.w("root.FOCUS_ACCESSIBLE was not paste target")
    if (deepSearch) {
      node = findFocusedNode(rootNode)
      if (node != null && isNodeFocusedAndEditable(node)) {
        Timber.d("recursive result node is paste target")
        return node
      }
      Timber.w("recursive result node was not paste target")
    }

    Timber.e("No editable target to paste into")
    return null
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
}
