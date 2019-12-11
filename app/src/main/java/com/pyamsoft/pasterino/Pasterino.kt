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

package com.pyamsoft.pasterino

import android.app.Application
import com.pyamsoft.pydroid.ui.PYDroid

class Pasterino : Application() {

    private var component: PasterinoComponent? = null

    override fun onCreate() {
        super.onCreate()

        PYDroid.init(
            this,
            getString(R.string.app_name),
            "https://github.com/pyamsoft/pasterino",
            "https://github.com/pyamsoft/pasterino/issues",
            PRIVACY_POLICY_URL,
            TERMS_CONDITIONS_URL,
            BuildConfig.VERSION_CODE,
            BuildConfig.DEBUG
        ) { provider ->
            component = DaggerPasterinoComponent.factory()
                .create(this, provider.theming(), provider.enforcer(), provider.imageLoader())
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
