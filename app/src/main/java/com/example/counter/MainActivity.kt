package com.example.counter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.counter.ui.theme.CounterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CounterTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CounterScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun CounterScreen(modifier: Modifier = Modifier) {
    // rememberSaveable -> the count survives rotation and other config changes.
    var count by rememberSaveable { mutableIntStateOf(0) }
    val haptics = LocalHapticFeedback.current

    // Intentional design choice: the counter is clamped at 0. The decrement and
    // reset controls disable themselves at the floor so it's obvious why.
    val atFloor = count == 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.counter_caption),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(24.dp))

        // Animated centerpiece: the number slides up on increment, down on
        // decrement, with a cross-fade. Direction is inferred from the value.
        AnimatedContent(
            targetState = count,
            transitionSpec = {
                val height = 220
                if (targetState > initialState) {
                    (slideInVertically(tween(320)) { height } + fadeIn(tween(220))) togetherWith
                        (slideOutVertically(tween(320)) { -height } + fadeOut(tween(180)))
                } else {
                    (slideInVertically(tween(320)) { -height } + fadeIn(tween(220))) togetherWith
                        (slideOutVertically(tween(320)) { height } + fadeOut(tween(180)))
                }
            },
            label = "counter"
        ) { value ->
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(48.dp))

        // Decrement / Increment — large circular buttons, well above the 48dp
        // minimum touch target, with built-in ripple and tonal elevation.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledTonalIconButton(
                onClick = {
                    if (!atFloor) {
                        count--
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                },
                enabled = !atFloor,
                shape = CircleShape,
                modifier = Modifier
                    .size(84.dp)
                    .semantics { contentDescription = "Decrement" }
            ) {
                GlyphLabel("−") // minus sign
            }

            Spacer(Modifier.width(32.dp))

            FilledIconButton(
                onClick = {
                    count++
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                },
                shape = CircleShape,
                modifier = Modifier
                    .size(84.dp)
                    .semantics { contentDescription = "Increment" }
            ) {
                GlyphLabel("+")
            }
        }

        Spacer(Modifier.height(28.dp))

        // Reset — pill-shaped, fades out when there is nothing to reset.
        OutlinedButton(
            onClick = {
                count = 0
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            },
            enabled = !atFloor,
            shape = CircleShape,
            modifier = Modifier
                .height(52.dp)
                .alpha(if (atFloor) 0.4f else 1f)
        ) {
            Icon(
                imageVector = Icons.Filled.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.action_reset),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun GlyphLabel(symbol: String) {
    Text(
        text = symbol,
        fontSize = 34.sp,
        fontWeight = FontWeight.Bold
    )
}

@Preview(showBackground = true)
@Composable
fun CounterScreenPreview() {
    CounterTheme {
        CounterScreen()
    }
}
