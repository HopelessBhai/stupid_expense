package com.example.stupidexpense

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.stupidexpense.data.TotalRepository
import com.example.stupidexpense.ui.TotalViewModel
import com.example.stupidexpense.ui.TotalViewModelFactory
import com.example.stupidexpense.ui.theme.StupidExpenseTheme
import java.text.DecimalFormat

class ResetActivity : ComponentActivity() {

    // Share the same repository contract so resetting here instantly reflects in MainActivity.
    private val repository by lazy { TotalRepository(applicationContext) }
    private val viewModel: TotalViewModel by viewModels { TotalViewModelFactory(repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StupidExpenseTheme {
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                ResetScreen(
                    total = state.total,
                    onReset = {
                        viewModel.resetTotal()
                        finish()
                    },
                    onBack = { finish() }
                )
            }
        }
    }
}

@Composable
private fun ResetScreen(
    total: Float,
    onReset: () -> Unit,
    onBack: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            val formatted = remember(total) { DecimalFormat("#,##0.##").format(total.toDouble()) }
            Text(
                text = "Reset Total",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(text = "Current saved total: ₹$formatted")
            // Reset immediately clears DataStore then finishes the screen.
            Button(onClick = onReset) {
                Text(text = "Reset now")
            }
            TextButton(onClick = onBack) {
                Text(text = "Back")
            }
        }
    }
}
