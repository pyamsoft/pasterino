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

import com.pyamsoft.pasterino.api.ClearPreferences
import com.pyamsoft.pasterino.api.MainSettingsPreferenceInteractor
import io.reactivex.Single

internal class MainSettingsPreferenceInteractorImpl internal constructor(
        private val preferences: ClearPreferences) : MainSettingsPreferenceInteractor {

    // This must be a Single or the stream will not continue
    override fun clearAll(): Single<Unit> = Single.fromCallable(preferences::clearAll)
}
