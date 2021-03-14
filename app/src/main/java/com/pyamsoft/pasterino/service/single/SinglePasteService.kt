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

package com.pyamsoft.pasterino.service.single

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pasterino.PasterinoComponent
import com.pyamsoft.pydroid.ui.Injector
import timber.log.Timber
import javax.inject.Inject

class SinglePasteService : JobIntentService(), LifecycleOwner {

    @JvmField
    @Inject
    internal var binder: SingleBinder? = null

    private val registry by lazy(LazyThreadSafetyMode.NONE) { LifecycleRegistry(this) }

    override fun getLifecycle(): Lifecycle {
        return registry
    }

    override fun onCreate() {
        super.onCreate()
        Injector.obtainFromApplication<PasterinoComponent>(this)
            .inject(this)

        requireNotNull(binder).bind(lifecycleScope) { }

        registry.currentState = Lifecycle.State.RESUMED
    }

    override fun onDestroy() {
        super.onDestroy()
        binder?.unbind()
        binder = null

        registry.currentState = Lifecycle.State.DESTROYED
    }

    override fun onHandleWork(intent: Intent) {
        try {
            requireNotNull(binder).paste(lifecycleScope)
        } catch (e: IllegalStateException) {
            Timber.e(e, "Error pasting")
        }
    }

    companion object {

        private const val JOB_ID = 42069

        @JvmStatic
        fun enqueue(context: Context) {
            val intent = Intent(context.applicationContext, SinglePasteService::class.java)
            enqueueWork(
                context,
                SinglePasteService::class.java,
                JOB_ID,
                intent
            )
        }
    }
}
