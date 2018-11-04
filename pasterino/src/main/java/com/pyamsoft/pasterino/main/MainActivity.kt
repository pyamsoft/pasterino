/*
 * Copyright (C) 2018 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pasterino.main

import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.preference.PreferenceManager
import android.view.View
import com.pyamsoft.pasterino.BuildConfig
import com.pyamsoft.pasterino.R
import com.pyamsoft.pasterino.databinding.ActivityMainBinding
import com.pyamsoft.pydroid.ui.about.AboutLibrariesFragment
import com.pyamsoft.pydroid.ui.bugreport.BugreportDialog
import com.pyamsoft.pydroid.ui.rating.ChangeLogBuilder
import com.pyamsoft.pydroid.ui.rating.RatingActivity
import com.pyamsoft.pydroid.ui.rating.buildChangeLog
import com.pyamsoft.pydroid.ui.util.DebouncedOnClickListener
import com.pyamsoft.pydroid.ui.util.commit
import com.pyamsoft.pydroid.util.toDp

class MainActivity : RatingActivity() {

  private lateinit var binding: ActivityMainBinding

  override val versionName: String = BuildConfig.VERSION_NAME

  override val applicationIcon: Int = R.mipmap.ic_launcher

  override val currentApplicationVersion: Int = BuildConfig.VERSION_CODE

  override val applicationName: String
    get() = getString(R.string.app_name)

  override val rootView: View
    get() = binding.root

  override val changeLogLines: ChangeLogBuilder = buildChangeLog {
    change("New icon style")
    change("Better open source license viewing experience")
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    setTheme(R.style.Theme_Pasterino_Light)
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    PreferenceManager.setDefaultValues(this, R.xml.preferences, false)

    setupAppBar()
    showMainFragment()
  }

  private fun setupAppBar() {
    binding.mainToolbar.apply {
      setToolbar(this)
      setTitle(R.string.app_name)
      ViewCompat.setElevation(this, 4.toDp(context).toFloat())

      setNavigationOnClickListener(DebouncedOnClickListener.create {
        onBackPressed()
      })
    }

    BugreportDialog.attachToToolbar(this, applicationName, currentApplicationVersion)
  }

  private fun showMainFragment() {
    val fragmentManager = supportFragmentManager
    if (fragmentManager.findFragmentByTag(MainFragment.TAG) == null
        && !AboutLibrariesFragment.isPresent(this)
    ) {
      fragmentManager.beginTransaction()
          .add(R.id.main_container, MainFragment(), MainFragment.TAG)
          .commit(this)
    }
  }
}
