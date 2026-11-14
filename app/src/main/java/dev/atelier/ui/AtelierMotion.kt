package dev.atelier.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import dev.atelier.data.MotionMode

/**
 * ATELIER / Tactile surface.
 *
 * A deliberate 4.5% press compression. Interruptible, non-bouncy spring
 * releases to a stable resting pose. No glow, ripple, infinite animation,
 * layout remeasure, or state-dependent ornamental movement.
 *
 * Logical hit target comes from the caller's size; the visual scale never
 * shrinks the interactive region. Honor both the user switch and Android's
 * system animation scale (Compose's animation clock does the latter).
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Modifier.atelierPressable(
    reduceMotion: Boolean,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    onLongClickLabel: String? = null,
    mode: MotionMode = MotionMode.SIGNATURE,
    hapticsEnabled: Boolean = true
): Modifier {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val haptics = LocalHapticFeedback.current
    val scale by animateFloatAsState(
        targetValue = when {
            reduceMotion || mode == MotionMode.STILL || !pressed -> 1f
            mode == MotionMode.EXPRESSIVE -> .94f
            else -> .955f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "atelier-press"
    )
    val base = this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
    return if (onLongClick == null) {
        base.clickable(
            interactionSource = interaction,
            indication = null,
            onClick = onClick
        )
    } else {
        base.combinedClickable(
            interactionSource = interaction,
            indication = null,
            onClickLabel = null,
            onLongClickLabel = onLongClickLabel,
            onLongClick = {
                if(hapticsEnabled)haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onLongClick()
            },
            onClick = onClick
        )
    }
}
