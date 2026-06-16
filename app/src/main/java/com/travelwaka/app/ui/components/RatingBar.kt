package com.travelwaka.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.travelwaka.app.ui.theme.StarColor
import com.travelwaka.app.ui.theme.TextSecondary

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.scale

@Composable
fun RatingBar(
    rating: Int,
    maxRating: Int = 5,
    starSize: Dp = 32.dp,
    onRatingChanged: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        for (i in 1..maxRating) {
            val isSelected = i <= rating
            val scaleAnim = remember { Animatable(1f) }
            
            LaunchedEffect(rating) {
                if (isSelected) {
                    scaleAnim.snapTo(1.3f)
                    scaleAnim.animateTo(
                        targetValue = 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                }
            }

            Icon(
                imageVector = if (isSelected) Icons.Filled.Star else Icons.Outlined.StarOutline,
                contentDescription = "Star $i",
                tint = if (isSelected) StarColor else TextSecondary,
                modifier = Modifier
                    .size(starSize)
                    .scale(scaleAnim.value)
                    .then(
                        if (onRatingChanged != null) {
                            Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onRatingChanged(i) }
                        } else Modifier
                    )
            )
        }
    }
}

@Composable
fun DisplayRatingBar(
    rating: Float,
    maxRating: Int = 5,
    starSize: Dp = 16.dp,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        for (i in 1..maxRating) {
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                contentDescription = null,
                tint = if (i <= rating) StarColor else TextSecondary,
                modifier = Modifier.size(starSize)
            )
        }
    }
}
