package com.example.stupidexpense

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.stupidexpense.data.TotalRepository
import com.example.stupidexpense.ui.TotalUiState
import com.example.stupidexpense.ui.TotalViewModel
import com.example.stupidexpense.ui.TotalViewModelFactory
import com.example.stupidexpense.ui.theme.StupidExpenseTheme
import java.text.DecimalFormat

class MainActivity : ComponentActivity() {

    private val repository by lazy { TotalRepository(applicationContext) }
    private val viewModel: TotalViewModel by viewModels { TotalViewModelFactory(repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isSmsPermissionGranted()) {
            requestSmsPermission()
        }
        setContent {
            StupidExpenseTheme {
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                MainScreen(
                    state = state,
                    onAmountChange = viewModel::onInputChange,
                    onAddAmount = viewModel::addAmount,
                    onOpenReset = { startActivity(Intent(this, ResetActivity::class.java)) }
                )
            }
        }
    }

    private fun isSmsPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECEIVE_SMS
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_SMS
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestSmsPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS),
            SMS_PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            ) {
                // Permissions granted
            } else {
                Toast.makeText(
                    this,
                    "SMS permissions are required to read messages.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    companion object {
        private const val SMS_PERMISSION_CODE = 101
    }
}

@Composable
private fun MainScreen(
    state: TotalUiState,
    onAmountChange: (String) -> Unit,
    onAddAmount: () -> Unit,
    onOpenReset: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.align(Alignment.End)) {
                IconButton(onClick = onOpenReset) {
                    Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Reset options")
                }
            }

            TotalHeader(total = state.total)

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = state.input,
                    onValueChange = onAmountChange,
                    placeholder = { Text(text = "Enter amount") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Button(onClick = onAddAmount, enabled = state.input.isNotBlank()) {
                    Text(text = "+")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = onOpenReset, modifier = Modifier.align(Alignment.End)) {
                Text(text = "Reset total")
            }
        }
    }
}

@Composable
private fun TotalHeader(total: Float) {
    val formatted = remember(total) { DecimalFormat("#,##0.##").format(total.toDouble()) }
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = "Total", style = MaterialTheme.typography.labelMedium)
        Text(
            text = "₹$formatted",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
    }
}
