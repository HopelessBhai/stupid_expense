package com.example.stupidexpense

import android.content.Intent
import android.os.Bundle
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.stupidexpense.data.TotalRepository
import com.example.stupidexpense.ui.TotalUiState
import com.example.stupidexpense.ui.TotalViewModel
import com.example.stupidexpense.ui.TotalViewModelFactory
import com.example.stupidexpense.ui.theme.StupidExpenseTheme
import java.text.DecimalFormat

class MainActivity : ComponentActivity() {

    // Reuse one repository/ViewModel per activity so the stream always points at DataStore.
    private val repository by lazy { TotalRepository(applicationContext) }
    private val viewModel: TotalViewModel by viewModels { TotalViewModelFactory(repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                // Overflow control mimics a widget menu and leads to the reset screen.
                IconButton(onClick = onOpenReset) {
                    Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Reset options")
                }
            }

            TotalHeader(total = state.total)

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Input row keeps things compact so it can live like a widget.
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

            // Provide a textual entry to the reset screen for discoverability.
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
        // Tiny label sits above the headline, similar to how widgets caption data.
        Text(text = "Total", style = MaterialTheme.typography.labelMedium)
        Text(
            text = "₹$formatted",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
    }
}
