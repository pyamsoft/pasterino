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
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.pyamsoft.pasterino.BuildConfig
import com.pyamsoft.pasterino.Injector
import com.pyamsoft.pasterino.PasterinoComponent
import com.pyamsoft.pasterino.R
import com.pyamsoft.pydroid.ui.about.AboutFragment
import com.pyamsoft.pydroid.ui.navigation.FailedNavigationPresenter
import com.pyamsoft.pydroid.ui.rating.ChangeLogBuilder
import com.pyamsoft.pydroid.ui.rating.RatingActivity
import com.pyamsoft.pydroid.ui.rating.buildChangeLog
import com.pyamsoft.pydroid.ui.theme.ThemeInjector
import com.pyamsoft.pydroid.ui.util.commit
import com.pyamsoft.pydroid.ui.widget.shadow.DropshadowView
import kotlin.LazyThreadSafetyMode.NONE

class MainActivity : RatingActivity(), MainUiComponent.Callback {

  internal lateinit var component: MainUiComponent

  private val layoutRoot by lazy(NONE) {
    findViewById<ConstraintLayout>(R.id.layout_constraint)
  }

  override val versionName: String = BuildConfig.VERSION_NAME

  override val applicationIcon: Int = R.mipmap.ic_launcher

  override val snackbarRoot: View
    get() = layoutRoot

  override val fragmentContainerId: Int
    get() = layoutRoot.id

  override val changeLogLines: ChangeLogBuilder = buildChangeLog {
    change("New icon style")
    change("Better open source license viewing experience")
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    if (ThemeInjector.obtain(applicationContext).isDarkTheme()) {
      setTheme(R.style.Theme_Pasterino_Dark)
    } else {
      setTheme(R.style.Theme_Pasterino_Light)
    }
    super.onCreate(savedInstanceState)
    setContentView(R.layout.layout_constraint)

    Injector.obtain<PasterinoComponent>(applicationContext)
        .plusMainComponent(layoutRoot)
        .inject(this)

    component.bind(this, savedInstanceState, this)

    showMainFragment()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    component.saveState(outState)
  }

  private fun showMainFragment() {
    val fm = supportFragmentManager
    if (fm.findFragmentByTag(MainFragment.TAG) == null && !AboutFragment.isPresent(this)) {
      fm.beginTransaction()
          .add(fragmentContainerId, MainFragment(), MainFragment.TAG)
          .commit(this)
    }
  }
}

