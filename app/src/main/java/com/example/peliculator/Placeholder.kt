package com.example.peliculator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PlaceholderItem() {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .width(120.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.LightGray)
    )
}
