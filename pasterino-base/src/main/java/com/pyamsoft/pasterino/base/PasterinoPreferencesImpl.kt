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

package com.pyamsoft.pasterino.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.support.v7.preference.PreferenceManager
import com.pyamsoft.pasterino.api.ClearPreferences
import com.pyamsoft.pasterino.api.PastePreferences

internal class PasterinoPreferencesImpl internal constructor(context: Context) : PastePreferences,
        ClearPreferences {

    private val delayTime: String
    private val delayTimeDefault: String
    private val preferences: SharedPreferences

    init {
        val appContext = context.applicationContext
        preferences = PreferenceManager.getDefaultSharedPreferences(appContext)
        delayTime = appContext.getString(R.string.delay_time_key)
        delayTimeDefault = appContext.getString(R.string.delay_time_default)
    }

    override val pasteDelayTime: Long
        get() = preferences.getString(delayTime, delayTimeDefault).toLong()

    @SuppressLint("ApplySharedPref")
    override fun clearAll() {
        // Make sure we commit so that they are cleared
        preferences.edit().clear().commit()
    }
}
