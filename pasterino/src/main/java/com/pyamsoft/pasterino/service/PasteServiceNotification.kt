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

package com.pyamsoft.pasterino.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES
import android.support.annotation.CheckResult
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import timber.log.Timber

object PasteServiceNotification {

  private const val ID = 1005
  private const val RC = 1005

  @JvmStatic
  internal fun start(context: Context) {
    if (PasteService.isRunning) {
      Timber.d("Start notification %d", ID)
      getNotificationManager(context).notify(ID, createNotification(context))
    }
  }

  @JvmStatic
  internal fun stop(context: Context) {
    Timber.d("Stop notification %d", ID)
    getNotificationManager(context).cancel(ID)
  }

  @JvmStatic
  @CheckResult
  private fun getNotificationManager(context: Context): NotificationManager {
    val appContext = context.applicationContext
    return appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
  }

  @JvmStatic
  @CheckResult
  private fun createNotification(context: Context): Notification {
    val appContext = context.applicationContext
    val singlePasteIntent = Intent(appContext, SinglePasteService::class.java)
    val notificationChannelId = "pasterino_foreground"
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      setupNotificationChannel(appContext, notificationChannelId)
    }
    return NotificationCompat.Builder(appContext, notificationChannelId)
        .setContentTitle(
            appContext.getString(R.string.app_name)
        )
        .setSmallIcon(R.drawable.ic_paste_notification)
        .setContentText("Pasterino Plzarino")
        .setContentIntent(PendingIntent.getService(appContext, RC, singlePasteIntent, 0))
        .setPriority(NotificationCompat.PRIORITY_MIN)
        .setWhen(0)
        .setOngoing(true)
        .setAutoCancel(false)
        .setColor(ContextCompat.getColor(appContext, R.color.green500))
        .setNumber(0)
        .build()
  }

  @JvmStatic
  @RequiresApi(VERSION_CODES.O)
  private fun setupNotificationChannel(
    context: Context,
    notificationChannelId: String
  ) {
    val name = "Paste Service"
    val description = "Notification related to the Pasterino service"
    val importance = NotificationManager.IMPORTANCE_MIN
    val notificationChannel = NotificationChannel(notificationChannelId, name, importance)
    notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
    notificationChannel.description = description
    notificationChannel.enableLights(false)
    notificationChannel.enableVibration(false)

    Timber.d("Create notification channel with id: %s", notificationChannelId)
    val notificationManager: NotificationManager = context.applicationContext.getSystemService(
        Context.NOTIFICATION_SERVICE
    ) as NotificationManager
    notificationManager.createNotificationChannel(notificationChannel)
  }
}
