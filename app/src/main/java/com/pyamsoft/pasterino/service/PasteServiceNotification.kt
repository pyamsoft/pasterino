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

package com.pyamsoft.pasterino.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES
import androidx.annotation.CheckResult
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.pyamsoft.pasterino.R
import timber.log.Timber

object PasteServiceNotification {

    private const val CHANNEL_ID = "pasterino_foreground"
    private const val ID = 1005
    private const val RC = 1005

    @Volatile
    private var notificationManager: NotificationManager? = null

    @JvmStatic
    internal fun start(context: Context) {
        Timber.d("Start notification %d", ID)
        getNotificationManager(context).notify(ID, createNotification(context))
    }

    @JvmStatic
    internal fun stop(context: Context) {
        Timber.d("Stop notification %d", ID)
        getNotificationManager(context).cancel(ID)
    }

    @JvmStatic
    @CheckResult
    private fun getNotificationManager(context: Context): NotificationManager {
        if (notificationManager == null) {
            synchronized(this) {
                if (notificationManager == null) {
                    notificationManager =
                        requireNotNull(context.applicationContext.getSystemService())
                }
            }
        }

        return requireNotNull(notificationManager)
    }

    @JvmStatic
    @CheckResult
    private fun createNotification(context: Context): Notification {
        val appContext = context.applicationContext
        val singlePasteIntent = Intent(appContext, SinglePasteService::class.java)
        if (Build.VERSION.SDK_INT >= VERSION_CODES.O) {
            setupNotificationChannel(appContext)
        }
        return NotificationCompat.Builder(appContext, CHANNEL_ID)
            .apply {
                setSmallIcon(R.drawable.ic_paste_notification)
                setContentText("Pasterino Plzarino")
                setContentIntent(PendingIntent.getService(appContext, RC, singlePasteIntent, 0))
                setWhen(0)
                setOngoing(true)
                setAutoCancel(false)
                setNumber(0)
                priority = NotificationCompat.PRIORITY_MIN
                color = ContextCompat.getColor(appContext, R.color.green500)
            }
            .build()
    }

    @JvmStatic
    @RequiresApi(VERSION_CODES.O)
    private fun setupNotificationChannel(
        context: Context
    ) {
        val name = "Paste Service"
        val desc = "Notification related to the Pasterino service"
        val importance = NotificationManager.IMPORTANCE_MIN
        val notificationChannel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            description = desc
            enableLights(false)
            enableVibration(false)
            setSound(null, null)
        }

        Timber.d("Create notification channel with id: %s", CHANNEL_ID)
        getNotificationManager(context).createNotificationChannel(notificationChannel)
    }
}
