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
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.util.isDebugMode

class Pasterino : Application() {

    private var component: PasterinoComponent? = null

    override fun onCreate() {
        super.onCreate()

        PYDroid.init(
            this,
            PYDroid.Parameters(
                viewSourceUrl = "https://github.com/pyamsoft/pasterino",
                bugReportUrl = "https://github.com/pyamsoft/pasterino/issues",
                version = BuildConfig.VERSION_CODE,
                termsConditionsUrl = TERMS_CONDITIONS_URL,
                privacyPolicyUrl = PRIVACY_POLICY_URL
            )
        ) { provider ->
            component = DaggerPasterinoComponent.factory()
                .create(
                    isDebugMode(),
                    this,
                    provider.theming(),
                    provider.imageLoader()
                )
        }
    }

    override fun getSystemService(name: String): Any? {
        val service = PYDroid.getSystemService(name)
        if (service != null) {
            return service
        }

        if (PasterinoComponent::class.java.name == name) {
            return requireNotNull(component)
        }

        return super.getSystemService(name)
    }

    companion object {

        const val PRIVACY_POLICY_URL =
            "https://pyamsoft.blogspot.com/p/pasterino-privacy-policy.html"
        const val TERMS_CONDITIONS_URL =
            "https://pyamsoft.blogspot.com/p/pasterino-terms-and-conditions.html"
    }
}
