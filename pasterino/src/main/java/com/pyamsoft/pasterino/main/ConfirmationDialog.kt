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

package com.pyamsoft.pasterino.main

import android.app.Dialog
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.pyamsoft.pasterino.model.ConfirmEvent
import com.pyamsoft.pasterino.uicore.CanaryDialog
import com.pyamsoft.pydroid.bus.EventBus

class ConfirmationDialog : CanaryDialog() {

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return AlertDialog.Builder(activity).setMessage(
        """
        |Really clear all application settings?
        |You will have to manually restart the Accessibility Service component of Pasterino""".trimMargin())
        .setPositiveButton("Yes") { _, _ ->
          dismiss()
          EventBus.get().publish(ConfirmEvent)
        }
        .setNegativeButton("No") { _, _ -> dismiss() }
        .create()
  }
}
