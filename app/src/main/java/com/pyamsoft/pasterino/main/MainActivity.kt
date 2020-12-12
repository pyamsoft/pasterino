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

package com.pyamsoft.pasterino.main

import android.os.Bundle
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModelProvider
import com.pyamsoft.pasterino.BuildConfig
import com.pyamsoft.pasterino.PasterinoComponent
import com.pyamsoft.pasterino.R
import com.pyamsoft.pydroid.arch.StateSaver
import com.pyamsoft.pydroid.arch.createComponent
import com.pyamsoft.pydroid.ui.Injector
import com.pyamsoft.pydroid.ui.arch.viewModelFactory
import com.pyamsoft.pydroid.ui.changelog.ChangeLogActivity
import com.pyamsoft.pydroid.ui.changelog.buildChangeLog
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.util.commit
import com.pyamsoft.pydroid.ui.util.layout
import com.pyamsoft.pydroid.ui.widget.shadow.DropshadowView
import com.pyamsoft.pydroid.util.stableLayoutHideNavigation
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

class MainActivity : ChangeLogActivity() {

    @JvmField
    @Inject
    internal var toolbar: ActivityToolbarView? = null

    @JvmField
    @Inject
    internal var activityView: ActivityFrameView? = null

    @JvmField
    @Inject
    internal var theming: Theming? = null

    @JvmField
    @Inject
    internal var factory: ViewModelProvider.Factory? = null
    private val viewModel by viewModelFactory<ActivityViewModel> { factory }

    private var stateSaver: StateSaver? = null

    override val versionName: String = BuildConfig.VERSION_NAME

    override val applicationIcon: Int = R.mipmap.ic_launcher

    override val snackbarRoot: ViewGroup by lazy(NONE) {
        findViewById<CoordinatorLayout>(R.id.snackbar_root)
    }

    override val fragmentContainerId: Int
        get() = requireNotNull(activityView).id()

    override val changelog = buildChangeLog {
        feature("Support for full screen content and gesture navigation")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Pasterino)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.snackbar_screen)

        val layoutRoot = findViewById<ConstraintLayout>(R.id.content_root)
        Injector.obtain<PasterinoComponent>(applicationContext)
            .plusMainComponent()
            .create(
                layoutRoot,
                this
            ) { requireNotNull(theming).isDarkTheme(this) }
            .inject(this)

        val activityView = requireNotNull(activityView)
        val toolbar = requireNotNull(toolbar)
        val dropshadow =
            DropshadowView.createTyped<ActivityViewState, ActivityViewEvent>(layoutRoot)

        stableLayoutHideNavigation()

        stateSaver = createComponent(
            savedInstanceState, this,
            viewModel,
            activityView,
            toolbar,
            dropshadow
        ) {}

        layoutRoot.layout {
            toolbar.also {
                connect(it.id(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                connect(it.id(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
                connect(it.id(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
                constrainWidth(it.id(), ConstraintSet.MATCH_CONSTRAINT)
            }

            dropshadow.also {
                connect(it.id(), ConstraintSet.TOP, toolbar.id(), ConstraintSet.BOTTOM)
                connect(it.id(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
                connect(it.id(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
                constrainWidth(it.id(), ConstraintSet.MATCH_CONSTRAINT)
            }

            activityView.also {
                connect(it.id(), ConstraintSet.TOP, toolbar.id(), ConstraintSet.BOTTOM)
                connect(
                    it.id(),
                    ConstraintSet.BOTTOM,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.BOTTOM
                )
                connect(it.id(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
                connect(it.id(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
                constrainHeight(it.id(), ConstraintSet.MATCH_CONSTRAINT)
                constrainWidth(it.id(), ConstraintSet.MATCH_CONSTRAINT)
            }
        }

        showMainFragment()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        stateSaver?.saveState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()

        stateSaver = null
        factory = null
        activityView = null
        toolbar = null
        theming = null
    }

    private fun showMainFragment() {
        val fm = supportFragmentManager
        if (fm.findFragmentByTag(MainFragment.TAG) == null) {
            fm.commit(this) {
                add(fragmentContainerId, MainFragment(), MainFragment.TAG)
            }
        }
    }
}
