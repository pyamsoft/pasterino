/*
 * Copyright 2017 Peter Kenji Yamanaka
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

import android.support.annotation.CheckResult

class Injector private constructor(private val component: PasterinoComponent) {

  @CheckResult fun provideComponent(): PasterinoComponent {
    return component
  }

  companion object {

    @Volatile private var instance: Injector? = null

    @JvmStatic
    internal fun set(component: PasterinoComponent) {
      synchronized(Injector::class.java) {
        instance = Injector(component)
      }
    }

    @JvmStatic
    @CheckResult fun get(): Injector {
      if (instance == null) {
        synchronized(Injector::class.java) {
          if (instance == null) {
            throw IllegalStateException("Injector component is NULL")
          }
        }
      }

      return instance!!
    }
  }
}
