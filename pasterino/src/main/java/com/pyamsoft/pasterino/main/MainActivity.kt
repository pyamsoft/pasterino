/*
 *     Copyright (C) 2017 Peter Kenji Yamanaka
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.pyamsoft.pasterino.main

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.preference.PreferenceManager
import android.view.MenuItem
import com.pyamsoft.pasterino.BuildConfig
import com.pyamsoft.pasterino.R
import com.pyamsoft.pasterino.databinding.ActivityMainBinding
import com.pyamsoft.pydroid.ui.about.AboutLibrariesFragment
import com.pyamsoft.pydroid.ui.sec.TamperActivity
import com.pyamsoft.pydroid.util.AppUtil

class MainActivity : TamperActivity() {

    private lateinit var binding: ActivityMainBinding

    override val changeLogLines: Array<String> = arrayOf(
            "BUGFIX: Better support for small screen devices"
    )

    override val safePackageName: String = "com.pyamsoft.pasterino"

    override val versionName: String = BuildConfig.VERSION_NAME

    override val applicationIcon: Int = R.mipmap.ic_launcher

    override val currentApplicationVersion: Int = BuildConfig.VERSION_CODE

    override val applicationName: String
        get() = getString(R.string.app_name)

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Pasterino_Light)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)

        setupAppBar()
        showMainFragment()
    }

    override fun onBackPressed() {
        val fragmentManager = supportFragmentManager
        val backStackCount = fragmentManager.backStackEntryCount
        if (backStackCount > 0) {
            fragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        val handled: Boolean = when (itemId) {
            android.R.id.home -> {
                onBackPressed()

                // Assign
                true
            }
            else -> false
        }
        return handled || super.onOptionsItemSelected(item)
    }

    private fun setupAppBar() {
        setSupportActionBar(binding.mainToolbar)
        binding.mainToolbar.setTitle(R.string.app_name)
        ViewCompat.setElevation(binding.mainToolbar, AppUtil.convertToDP(this, 4f))
    }

    private fun showMainFragment() {
        val fragmentManager = supportFragmentManager
        if (fragmentManager.findFragmentByTag(
                MainSettingsPreferenceFragment.TAG) == null && fragmentManager.findFragmentByTag(
                AboutLibrariesFragment.TAG) == null) {
            fragmentManager.beginTransaction().replace(R.id.main_container, MainFragment(),
                    MainFragment.TAG).commit()
        }
    }

}
