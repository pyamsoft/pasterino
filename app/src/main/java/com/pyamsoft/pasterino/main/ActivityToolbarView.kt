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
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import com.pyamsoft.pasterino.Pasterino
import com.pyamsoft.pasterino.R
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.ui.app.ToolbarActivityProvider
import com.pyamsoft.pydroid.ui.privacy.addPrivacy
import com.pyamsoft.pydroid.ui.privacy.removePrivacy
import com.pyamsoft.pydroid.ui.theme.ThemeProvider
import com.pyamsoft.pydroid.util.toDp
import javax.inject.Inject

internal class ActivityToolbarView @Inject internal constructor(
    theming: ThemeProvider,
    toolbarActivityProvider: ToolbarActivityProvider,
    parent: ViewGroup
) : BaseUiView<ActivityViewState, ActivityViewEvent>(parent) {

    override val layoutRoot by boundView<Toolbar>(R.id.toolbar)

    override val layout: Int = R.layout.toolbar

    init {
        doOnInflate {
            setupToolbar(toolbarActivityProvider, theming)
        }

        doOnTeardown {
            toolbarActivityProvider.setToolbar(null)
            layoutRoot.removePrivacy()
        }
    }

    private fun setupToolbar(
        toolbarActivityProvider: ToolbarActivityProvider,
        theming: ThemeProvider
    ) {
        val theme = if (theming.isDarkTheme()) {
            R.style.ThemeOverlay_MaterialComponents
        } else {
            R.style.ThemeOverlay_MaterialComponents_Light
        }

        layoutRoot.apply {
            popupTheme = theme
            toolbarActivityProvider.setToolbar(this)
            setTitle(R.string.app_name)
            ViewCompat.setElevation(this, 4F.toDp(context).toFloat())
            addPrivacy(Pasterino.PRIVACY_POLICY_URL, Pasterino.TERMS_CONDITIONS_URL)
        }
    }

    override fun onRender(state: ActivityViewState) {
    }
}
