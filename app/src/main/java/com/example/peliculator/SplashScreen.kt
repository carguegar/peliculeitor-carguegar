package com.example.peliculator

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(navController: NavController) {
    val text = "peliculeitor"
    val letters = text.toCharArray().map { it.toString() }

    // Animaciones por letra (alpha 0f -> 1f)
    val letterAlphas = remember { letters.map { Animatable(0f) } }

    // Fade out global antes de navegar
    val globalAlpha = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        val stagger = 80L
        // Iniciar animaciones por letra en corutinas separadas (staggered)
        letterAlphas.forEachIndexed { index, anim ->
            launch {
                delay(index * stagger)
                anim.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 350)
                )
            }
        }

        // Esperar a que terminen (aprox): último inicio + duración + breve pausa
        val totalApprox = (letters.size - 1) * stagger + 350 + 500L
        delay(totalApprox)

        // Pausa final mostrando el logo
        delay(500)

        // Fade out global
        globalAlpha.animateTo(0f, animationSpec = tween(durationMillis = 450))

        // Navegar y eliminar splash del back stack
        navController.navigate("peliculas") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .graphicsLayer(alpha = globalAlpha.value),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            letters.forEachIndexed { i, ch ->
                val a = letterAlphas[i].value
                Text(
                    text = ch,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFE50914),
                    modifier = Modifier.graphicsLayer {
                        alpha = a
                        translationY = (1f - a) * 18f // efecto subida
                        scaleX = 0.95f + a * 0.1f
                        scaleY = 0.95f + a * 0.1f
                    }
                )
            }
        }
    }
}
