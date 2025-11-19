package com.example.stupidexpense.widget.dialog

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.stupidexpense.R
import com.example.stupidexpense.data.TotalRepository
import com.example.stupidexpense.widget.StupidExpenseWidgetProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Lightweight activity that behaves like a dialog so users can input numbers without
 * relying on inline widget typing (unsupported before Android 15).
 */
class WidgetQuickAddActivity : ComponentActivity() {

    private lateinit var amountInput: TextInputEditText
    private lateinit var addButton: MaterialButton
    private lateinit var cancelButton: MaterialButton

    private val repository by lazy { TotalRepository(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFinishOnTouchOutside(true)
        setContentView(R.layout.dialog_widget_quick_add)

        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        amountInput = findViewById(R.id.input_amount)
        addButton = findViewById(R.id.button_add)
        cancelButton = findViewById(R.id.button_cancel)

        cancelButton.setOnClickListener { finish() }
        addButton.setOnClickListener { handleAddClicked() }
        amountInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handleAddClicked()
                true
            } else {
                false
            }
        }
    }

    private fun handleAddClicked() {
        val input = amountInput.text?.toString()?.trim().orEmpty()
        val amount = input.toFloatOrNull()
        if (amount == null) {
            amountInput.error = getString(R.string.widget_dialog_input_error)
            return
        }

        amountInput.error = null
        toggleInputs(false)

        lifecycleScope.launch {
            repository.addToTotal(amount)
            notifyWidgetUpdate()
            finish()
        }
    }

    private suspend fun notifyWidgetUpdate() {
        withContext(Dispatchers.IO) {
            val context = applicationContext
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

    private fun toggleInputs(enabled: Boolean) {
        amountInput.isEnabled = enabled
        addButton.isEnabled = enabled
        cancelButton.isEnabled = enabled
    }
}

