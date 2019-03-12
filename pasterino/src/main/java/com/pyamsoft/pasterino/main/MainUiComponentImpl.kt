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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pasterino.main.MainUiComponent.Callback
import com.pyamsoft.pydroid.arch.BaseUiComponent
import com.pyamsoft.pydroid.ui.widget.shadow.DropshadowView

internal class MainUiComponentImpl internal constructor(
  private val layoutRoot: ConstraintLayout,
  private val toolbar: MainToolbarView,
  private val frameView: MainFrameView,
  private val dropshadow: DropshadowView,
  private val presenter: MainPresenter
) : BaseUiComponent<MainUiComponent.Callback>(),
    MainUiComponent,
    MainPresenter.Callback {

  override fun onBind(
    owner: LifecycleOwner,
    savedInstanceState: Bundle?,
    callback: Callback
  ) {
    createComponents(savedInstanceState)
    layoutComponents(layoutRoot)

    presenter.bind(this)
  }

  override fun saveState(outState: Bundle) {
    toolbar.saveState(outState)
    frameView.saveState(outState)
    dropshadow.saveState(outState)
  }

  private fun createComponents(savedInstanceState: Bundle?) {
    toolbar.inflate(savedInstanceState)
    frameView.inflate(savedInstanceState)
    dropshadow.inflate(savedInstanceState)
  }

  private fun layoutComponents(layoutRoot: ConstraintLayout) {
    ConstraintSet().apply {
      clone(layoutRoot)

      toolbar.also {
        connect(it.id(), ConstraintSet.TOP, layoutRoot.id, ConstraintSet.TOP)
        connect(it.id(), ConstraintSet.START, layoutRoot.id, ConstraintSet.START)
        connect(it.id(), ConstraintSet.END, layoutRoot.id, ConstraintSet.END)
        constrainWidth(it.id(), ConstraintSet.MATCH_CONSTRAINT)
      }

      frameView.also {
        connect(it.id(), ConstraintSet.TOP, toolbar.id(), ConstraintSet.BOTTOM)
        connect(it.id(), ConstraintSet.BOTTOM, layoutRoot.id, ConstraintSet.BOTTOM)
        connect(it.id(), ConstraintSet.START, layoutRoot.id, ConstraintSet.START)
        connect(it.id(), ConstraintSet.END, layoutRoot.id, ConstraintSet.END)
        constrainHeight(it.id(), ConstraintSet.MATCH_CONSTRAINT)
        constrainWidth(it.id(), ConstraintSet.MATCH_CONSTRAINT)
      }

      dropshadow.also {
        connect(it.id(), ConstraintSet.TOP, toolbar.id(), ConstraintSet.BOTTOM)
        connect(it.id(), ConstraintSet.START, layoutRoot.id, ConstraintSet.START)
        connect(it.id(), ConstraintSet.END, layoutRoot.id, ConstraintSet.END)
        constrainWidth(it.id(), ConstraintSet.MATCH_CONSTRAINT)
      }

      applyTo(layoutRoot)
    }
  }

}