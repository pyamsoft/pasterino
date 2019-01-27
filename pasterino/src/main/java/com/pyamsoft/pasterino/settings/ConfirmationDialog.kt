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

package com.pyamsoft.pasterino.settings

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.pyamsoft.pasterino.Injector
import com.pyamsoft.pasterino.PasterinoComponent
import com.pyamsoft.pydroid.ui.app.fragment.ToolbarDialog

class ConfirmationDialog : ToolbarDialog() {

  internal lateinit var worker: ClearAllWorker

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    Injector.obtain<PasterinoComponent>(requireContext().applicationContext)
        .inject(this)

    return AlertDialog.Builder(requireActivity())
        .setMessage(
            """
        |Really clear all application settings?
        |You will have to manually restart the Accessibility Service component of Pasterino""".trimMargin()
        )
        .setPositiveButton("Yes") { _, _ ->
          worker.clearAll()
          dismiss()
        }
        .setNegativeButton("No") { _, _ -> dismiss() }
        .create()
  }
}
