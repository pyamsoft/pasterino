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

package com.pyamsoft.pasterino

import android.app.Application
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibraries
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.util.isDebugMode

class Pasterino : Application() {

    private val component by lazy {
        val url = "https://github.com/pyamsoft/pasterino"

        val provider = PYDroid.init(
            this,
            PYDroid.Parameters(
                viewSourceUrl = url,
                bugReportUrl = "$url/issues",
                version = BuildConfig.VERSION_CODE,
                termsConditionsUrl = TERMS_CONDITIONS_URL,
                privacyPolicyUrl = PRIVACY_POLICY_URL
            )
        )
        // Using pydroid-notify
        OssLibraries.usingNotify = true

        return@lazy DaggerPasterinoComponent.factory().create(
            isDebugMode(),
            this,
            provider.theming(),
            provider.imageLoader()
        )
    }

    override fun getSystemService(name: String): Any? {
        return PYDroid.getSystemService(name) ?: fallbackGetSystemService(name)
    }

    private fun fallbackGetSystemService(name: String): Any? {
        return if (PasterinoComponent::class.java.name == name) component else {
            super.getSystemService(name)
        }
    }

    companion object {

        const val PRIVACY_POLICY_URL =
            "https://pyamsoft.blogspot.com/p/pasterino-privacy-policy.html"
        const val TERMS_CONDITIONS_URL =
            "https://pyamsoft.blogspot.com/p/pasterino-terms-and-conditions.html"
    }
}
