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

package com.pyamsoft.pasterino.main

import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pasterino.main.MainToolbarUiComponent.Callback
import com.pyamsoft.pydroid.arch.BaseUiComponent
import com.pyamsoft.pydroid.arch.doOnDestroy
import com.pyamsoft.pydroid.ui.widget.shadow.DropshadowView
import javax.inject.Inject

internal class MainToolbarUiComponentImpl @Inject internal constructor(
  private val toolbarView: MainToolbarView,
  private val dropshadowView: DropshadowView
) : BaseUiComponent<Callback>(),
    MainToolbarUiComponent {

  override fun id(): Int {
    return toolbarView.id()
  }

  override fun onBind(
    owner: LifecycleOwner,
    savedInstanceState: Bundle?,
    callback: Callback
  ) {
    owner.doOnDestroy {
      toolbarView.teardown()
      dropshadowView.teardown()
    }

    toolbarView.inflate(savedInstanceState)
    dropshadowView.inflate(savedInstanceState)
  }

  override fun onSaveState(outState: Bundle) {
    toolbarView.saveState(outState)
    dropshadowView.saveState(outState)
  }

  override fun onLayout(set: ConstraintSet) {
    dropshadowView.also {
      set.connect(it.id(), ConstraintSet.TOP, toolbarView.id(), ConstraintSet.BOTTOM)
      set.connect(it.id(), ConstraintSet.START, toolbarView.id(), ConstraintSet.START)
      set.connect(it.id(), ConstraintSet.END, toolbarView.id(), ConstraintSet.END)
      set.constrainWidth(it.id(), ConstraintSet.MATCH_CONSTRAINT)
    }
  }

}