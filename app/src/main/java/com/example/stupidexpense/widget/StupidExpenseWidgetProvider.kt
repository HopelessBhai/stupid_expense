package com.example.stupidexpense.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import com.example.stupidexpense.MainActivity
import com.example.stupidexpense.R
import com.example.stupidexpense.data.TotalRepository
import com.example.stupidexpense.widget.dialog.WidgetQuickAddActivity
import java.text.DecimalFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class StupidExpenseWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        refreshWidgets(context, appWidgetIds)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        scope.coroutineContext.cancelChildren()
    }

    private fun refreshWidgets(
        context: Context,
        appWidgetIds: IntArray? = null
    ) {
        scope.launch {
            val manager = AppWidgetManager.getInstance(context)
            val ids = appWidgetIds ?: manager.getAppWidgetIds(
                ComponentName(context, StupidExpenseWidgetProvider::class.java)
            )
            if (ids.isEmpty()) return@launch

            val repository = TotalRepository(context.applicationContext)
            val total = repository.totalStream.first()
            ids.forEach { id ->
                val views = buildRemoteViews(context, id, total)
                manager.updateAppWidget(id, views)
            }
        }
    }

    private fun buildRemoteViews(
        context: Context,
        appWidgetId: Int,
        total: Float
    ): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_stupid_expense)
        val formatter = DecimalFormat("#,##0.##")
        val formattedTotal = "₹${formatter.format(total.toDouble())}"
        views.setTextViewText(R.id.widget_total_value, formattedTotal)

        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            OPEN_APP_REQUEST_CODE,
            Intent(context, MainActivity::class.java),
            pendingIntentFlags(mutable = false)
        )

        val quickAddPendingIntent = buildQuickAddPendingIntent(context, appWidgetId)

        views.setOnClickPendingIntent(R.id.widget_container, openAppPendingIntent)
        views.setOnClickPendingIntent(R.id.widget_header, openAppPendingIntent)
        views.setOnClickPendingIntent(R.id.widget_quick_add_pill, quickAddPendingIntent)
        views.setOnClickPendingIntent(R.id.widget_add_button, quickAddPendingIntent)

        return views
    }

    private fun buildQuickAddPendingIntent(
        context: Context,
        appWidgetId: Int
    ): PendingIntent {
        val intent = Intent(context, WidgetQuickAddActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }

        val requestCode = QUICK_ADD_REQUEST_CODE_START + appWidgetId

        return PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            pendingIntentFlags(mutable = false)
        )
    }

    companion object {
        private const val OPEN_APP_REQUEST_CODE = 1001
        private const val QUICK_ADD_REQUEST_CODE_START = 2000
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        private fun pendingIntentFlags(mutable: Boolean): Int {
            val base = PendingIntent.FLAG_UPDATE_CURRENT
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (mutable) {
                    base or PendingIntent.FLAG_MUTABLE
                } else {
                    base or PendingIntent.FLAG_IMMUTABLE
                }
            } else {
                base
            }
        }
    }
}

