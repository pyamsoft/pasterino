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

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.ViewPropertyAnimatorCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pyamsoft.pasterino.R
import com.pyamsoft.pasterino.main.MainViewEvent.ActionClick
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.loader.Loaded
import com.pyamsoft.pydroid.ui.util.popHide
import com.pyamsoft.pydroid.ui.util.popShow
import com.pyamsoft.pydroid.ui.util.setOnDebouncedClickListener
import javax.inject.Inject

internal class MainActionView @Inject internal constructor(
    private val imageLoader: ImageLoader,
    parent: ViewGroup
) : BaseUiView<MainViewState, MainViewEvent>(parent) {

    private val fab by boundView<FloatingActionButton>(R.id.fab)

    private var actionIconLoaded: Loaded? = null

    override val layout: Int = R.layout.floating_action_button

    override val layoutRoot by boundView<FrameLayout>(R.id.fab_container)

    private var animator: ViewPropertyAnimatorCompat? = null

    init {
        doOnTeardown {
            fab.setOnDebouncedClickListener(null)
            actionIconLoaded?.dispose()
        }

        doOnTeardown {
            cancelAnimator()
        }
    }

    private fun cancelAnimator() {
        animator?.cancel()
        animator = null
    }

    override fun onRender(state: MainViewState) {
        toggleVisibility(state.isVisible)
        setFabState(state.isServiceRunning)
    }

    private fun setFabState(running: Boolean) {
        fab.setOnDebouncedClickListener {
            publish(ActionClick(running))
        }

        val icon = if (running) {
            R.drawable.ic_help_24dp
        } else {
            R.drawable.ic_service_start_24dp
        }

        actionIconLoaded?.dispose()
        actionIconLoaded = imageLoader.load(icon)
            .into(fab)
    }

    private fun toggleVisibility(visible: Boolean) {
        if (animator == null) {
            val a = if (visible) fab.popShow() else fab.popHide()
            a.withEndAction { cancelAnimator() }
            animator = a
        }
    }
}
