package com.example.stupidexpense

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.example.stupidexpense.data.TotalRepository
import com.example.stupidexpense.widget.StupidExpenseWidgetProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext



class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("SmsReceiver", "SMS received - action: ${intent.action}")
        
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val pendingResult = goAsync()
            
            try {
                val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                for (message in messages) {
                    val msgBody = message.messageBody
                    val msgSender = message.originatingAddress
                    Log.d("SmsReceiver", "Message from $msgSender: $msgBody")
                    val parsed = BankSmsParser.parse(msgSender, msgBody)
                    Log.d("SmsReceiver", "Parsed: $parsed")
                    if (parsed != null && parsed.type == BankSmsParser.TransactionType.DEBIT) {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val repository = TotalRepository(context.applicationContext)
                                repository.addToTotal(parsed.amount.toFloat())
                                Log.i("SmsReceiver", "Debit added: ${parsed.amount}")
                                notifyWidgetUpdate(context)
                            } finally {
                                pendingResult.finish()
                            }
                        }
                        return
                    }

                }
                pendingResult.finish()
            } catch (e: Exception) {
                Log.e("SmsReceiver", "Error processing SMS", e)
                pendingResult.finish()
            }
        }
    }

    private suspend fun notifyWidgetUpdate(context: Context) {
        withContext(Dispatchers.IO) {
            val component = ComponentName(context, StupidExpenseWidgetProvider::class.java)
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(component)
            if (ids.isEmpty()) return@withContext

            val updateIntent = Intent(context, StupidExpenseWidgetProvider::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            }
            context.sendBroadcast(updateIntent)
        }
    }
}
