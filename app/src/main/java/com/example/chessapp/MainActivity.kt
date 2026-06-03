package com.example.chessapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChessMenuApp()
        }
    }
}

data class AppColors(
    val background: Color,
    val card: Color,
    val title: Color,
    val text: Color,
    val secondaryText: Color,
    val button: Color,
    val buttonText: Color
)

fun getAppColors(isDarkMode: Boolean): AppColors {
    return if (isDarkMode) {
        AppColors(
            background = Color(0xFF17111F),
            card = Color(0xFF2B2038),
            title = Color(0xFFFFD36E),
            text = Color.White,
            secondaryText = Color.LightGray,
            button = Color(0xFF5A3E78),
            buttonText = Color.White
        )
    } else {
        AppColors(
            background = Color(0xFFF5F1FA),
            card = Color.White,
            title = Color(0xFF3B255A),
            text = Color.Black,
            secondaryText = Color.DarkGray,
            button = Color(0xFF6B4C8C),
            buttonText = Color.White
        )
    }
}

@Composable
fun ChessMenuApp() {
    var currentScreen by remember { mutableStateOf("main") }

    val customizationViewModel: CustomizationViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()
    val playProfileViewModel: PlayProfileViewModel = viewModel()

    val colors = getAppColors(settingsViewModel.isDarkMode)

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = colors.background
        ) {
            when (currentScreen) {
                "main" -> MainMenuScreen(
                    colors = colors,
                    onPlayClick = { currentScreen = "play" },
                    onTrainingClick = { currentScreen = "training" },
                    onCustomizeClick = { currentScreen = "customize" },
                    onOptionsClick = { currentScreen = "options" }
                )

                "play" -> PlayScreen(
                    colors = colors,
                    playProfileViewModel = playProfileViewModel,
                    customizationViewModel = customizationViewModel,
                    pieceSlideAnimationEnabled = settingsViewModel.isPieceSlideAnimationOn,
                    onBackClick = { currentScreen = "main" }
                )

                "training" -> SkillTrainingScreen(
                    colors = colors,
                    onPiecesClick = { currentScreen = "pieces" },
                    onBackClick = { currentScreen = "main" }
                )

                "pieces" -> PiecesScreen(
                    colors = colors,
                    onBackClick = { currentScreen = "training" }
                )

                "customize" -> CustomizeScreen(
                    colors = colors,
                    customizationViewModel = customizationViewModel,
                    onBackClick = { currentScreen = "main" }
                )

                "options" -> OptionsScreen(
                    colors = colors,
                    settingsViewModel = settingsViewModel,
                    onBackClick = { currentScreen = "main" }
                )
            }
        }
    }
}

@Composable
fun MainMenuScreen(
    colors: AppColors,
    onPlayClick: () -> Unit,
    onTrainingClick: () -> Unit,
    onCustomizeClick: () -> Unit,
    onOptionsClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Pawn & Blade",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = colors.title,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Chess Strategy Game",
                fontSize = 18.sp,
                color = colors.text,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            MainMenuButton(
                text = "Play",
                colors = colors,
                onClick = onPlayClick
            )

            MainMenuButton(
                text = "Skill Training",
                colors = colors,
                onClick = onTrainingClick
            )

            MainMenuButton(
                text = "Customize",
                colors = colors,
                onClick = onCustomizeClick
            )

            MainMenuButton(
                text = "Options",
                colors = colors,
                onClick = onOptionsClick
            )
        }
    }
}

@Composable
fun SkillTrainingScreen(
    colors: AppColors,
    onPiecesClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Skill Training",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = colors.title,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Choose a training topic. More lessons can be added later as the app grows.",
                fontSize = 18.sp,
                color = colors.text,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            MainMenuButton(
                text = "Pieces",
                colors = colors,
                onClick = onPiecesClick
            )

            MainMenuButton(
                text = "Back to Main Menu",
                colors = colors,
                onClick = onBackClick
            )
        }
    }
}

@Composable
fun PiecesScreen(
    colors: AppColors,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Chess Pieces",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = colors.title,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Scroll through each piece card. Tap a card image to switch between the front and back.",
                fontSize = 16.sp,
                color = colors.text,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            PieceCard(
                colors = colors,
                pieceName = "Pawn",
                frontImageResource = R.drawable.pawn_card,
                backImageResource = R.drawable.pawn_card_back
            )

            PieceCard(
                colors = colors,
                pieceName = "Bishop",
                frontImageResource = R.drawable.bishop_card,
                backImageResource = R.drawable.bishop_card_back
            )

            PieceCard(
                colors = colors,
                pieceName = "Knight",
                frontImageResource = R.drawable.knight_card,
                backImageResource = R.drawable.knight_card_back
            )

            PieceCard(
                colors = colors,
                pieceName = "Rook",
                frontImageResource = R.drawable.rook_card,
                backImageResource = R.drawable.rook_card_back
            )

            PieceCard(
                colors = colors,
                pieceName = "Queen",
                frontImageResource = R.drawable.queen_card,
                backImageResource = R.drawable.queen_card_back
            )

            PieceCard(
                colors = colors,
                pieceName = "King",
                frontImageResource = R.drawable.king_card,
                backImageResource = R.drawable.king_card_back
            )

            MainMenuButton(
                text = "Back to Skill Training",
                colors = colors,
                onClick = onBackClick
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PieceCard(
    colors: AppColors,
    pieceName: String,
    frontImageResource: Int,
    backImageResource: Int
) {
    var showBackSide by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (showBackSide) 180f else 0f,
        animationSpec = tween(durationMillis = 900),
        label = "cardFlip"
    )

    val isShowingBack = rotation > 90f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.card
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(
                    id = if (isShowingBack) backImageResource else frontImageResource
                ),
                contentDescription = if (isShowingBack) {
                    "$pieceName card back image"
                } else {
                    "$pieceName card front image"
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(620.dp)
                    .graphicsLayer {
                        rotationY = if (isShowingBack) {
                            rotation - 180f
                        } else {
                            rotation
                        }
                        cameraDistance = 12f * density
                    }
                    .clickable { showBackSide = !showBackSide },
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun MainMenuButton(
    text: String,
    colors: AppColors,
    onClick: () -> Unit
) {
    PrimaryActionButton(
        text = text,
        colors = colors,
        fontSize = 20.sp,
        verticalPadding = 8.dp,
        onClick = onClick
    )
}

@Composable
fun PrimaryActionButton(
    text: String,
    colors: AppColors,
    modifier: Modifier = Modifier.fillMaxWidth(),
    fontSize: TextUnit = 18.sp,
    verticalPadding: Dp = 6.dp,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.button,
            contentColor = colors.buttonText
        )
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            modifier = Modifier.padding(vertical = verticalPadding)
        )
    }
}

@Composable
fun PrototypeCard(
    colors: AppColors,
    modifier: Modifier = Modifier.fillMaxWidth(),
    cornerRadius: Dp = 18.dp,
    elevation: Dp = 6.dp,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        content()
    }
}
