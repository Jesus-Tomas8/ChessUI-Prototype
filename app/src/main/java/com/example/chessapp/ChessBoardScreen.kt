package com.example.chessapp

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChessBoardGameScreen(
    colors: AppColors,
    mode: ChessPlayMode,
    onBackClick: () -> Unit
) {
    val gameState = remember(mode) { ChessGameState(mode) }
    val title = if (mode == ChessPlayMode.SANDBOX) "Sandbox Board" else "Two Player Board"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 12.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp)
        ) {
            item {
                Text(
                    text = title,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.title,
                    textAlign = TextAlign.Center
                )
            }

            item {
                GameStatusCard(colors = colors, gameState = gameState)
            }

            item {
                ChessBoard(
                    colors = colors,
                    gameState = gameState
                )
            }

            if (mode == ChessPlayMode.SANDBOX) {
                item {
                    SandboxControls(
                        colors = colors,
                        gameState = gameState
                    )
                }
            }

            item {
                CapturedPiecesCard(colors = colors, gameState = gameState)
            }

            item {
                MoveHistoryCard(colors = colors, gameState = gameState)
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = gameState::resetStandardGame,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.button,
                            contentColor = colors.buttonText
                        )
                    ) {
                        Text("Reset")
                    }

                    Button(
                        onClick = onBackClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.button,
                            contentColor = colors.buttonText
                        )
                    ) {
                        Text("Back")
                    }
                }
            }
        }
    }
}

@Composable
fun GameStatusCard(
    colors: AppColors,
    gameState: ChessGameState
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = gameState.statusText,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = colors.text
            )

            Text(
                text = if (gameState.sandboxSetupMode) {
                    "Setup mode is active."
                } else {
                    "Tap a piece, then tap a highlighted square to move."
                },
                fontSize = 13.sp,
                color = colors.secondaryText
            )
        }
    }
}

@Composable
fun ChessBoard(
    colors: AppColors,
    gameState: ChessGameState
) {
    val checkedKing = gameState.kingInCheckPosition()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .border(2.dp, colors.title, RoundedCornerShape(6.dp))
            .clip(RoundedCornerShape(6.dp))
    ) {
        for (row in 0..7) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                for (col in 0..7) {
                    val position = BoardPosition(row, col)
                    ChessSquare(
                        colors = colors,
                        position = position,
                        piece = gameState.board[position],
                        isSelected = gameState.selectedPosition == position,
                        isHighlighted = position in gameState.highlightedTargets,
                        isCaptureHighlight = position in gameState.highlightedTargets &&
                            gameState.board[position] != null,
                        isCheckedKing = checkedKing == position,
                        onClick = { gameState.onSquareTapped(position) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun ChessSquare(
    colors: AppColors,
    position: BoardPosition,
    piece: ChessPiece?,
    isSelected: Boolean,
    isHighlighted: Boolean,
    isCaptureHighlight: Boolean,
    isCheckedKing: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLightSquare = (position.row + position.col) % 2 == 0
    val squareColor = if (isLightSquare) {
        Color(0xFFE7D7B6)
    } else {
        Color(0xFF6F8E6D)
    }
    val borderColor = when {
        isCheckedKing -> Color(0xFFD64545)
        isSelected -> Color(0xFFFFD36E)
        isCaptureHighlight -> Color(0xFFE85D4F)
        else -> Color.Transparent
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(squareColor)
            .border(3.dp, borderColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        piece?.let {
            ChessPieceView(piece = it)
        }

        if (isHighlighted && !isCaptureHighlight) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(Color(0xAA2E7D32))
            )
        }

        if (isCaptureHighlight) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp)
                    .border(3.dp, Color(0xFFE85D4F), CircleShape)
            )
        }
    }
}

@Composable
fun ChessPieceView(piece: ChessPiece) {
    val context = LocalContext.current
    val drawableId = remember(piece.side, piece.type) {
        pieceDrawableResource(context, piece)
    }

    if (drawableId != 0) {
        Image(
            painter = painterResource(id = drawableId),
            contentDescription = "${piece.side.label} ${piece.type.label}",
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp),
            contentScale = ContentScale.Fit
        )
    } else {
        Text(
            text = piece.symbol,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = if (piece.side == ChessSide.WHITE) Color(0xFFFDF7EA) else Color(0xFF1C1A17),
            textAlign = TextAlign.Center
        )
    }
}

fun pieceDrawableResource(context: Context, piece: ChessPiece): Int {
    val sidePrefix = if (piece.side == ChessSide.WHITE) "wh" else "bl"
    val candidateNames = listOf(
        "${sidePrefix}_${piece.type.resourceName}_classic",
        "${piece.side.resourcePrefix}_${piece.type.resourceName}"
    )

    return candidateNames
        .asSequence()
        .map { resourceName ->
            context.resources.getIdentifier(
                resourceName,
                "drawable",
                context.packageName
            )
        }
        .firstOrNull { drawableId -> drawableId != 0 }
        ?: 0
}

@Composable
fun SandboxControls(
    colors: AppColors,
    gameState: ChessGameState
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = gameState::toggleSandboxMode,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.button,
                        contentColor = colors.buttonText
                    )
                ) {
                    Text(if (gameState.sandboxSetupMode) "Test Moves" else "Edit Setup")
                }

                Button(
                    onClick = gameState::clearSandboxBoard,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.button,
                        contentColor = colors.buttonText
                    )
                ) {
                    Text("Clear")
                }
            }

            Text(
                text = "Side to move",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colors.text
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SideChip(
                    colors = colors,
                    label = "White",
                    selected = gameState.turn == ChessSide.WHITE,
                    onClick = { gameState.chooseTurn(ChessSide.WHITE) }
                )
                SideChip(
                    colors = colors,
                    label = "Black",
                    selected = gameState.turn == ChessSide.BLACK,
                    onClick = { gameState.chooseTurn(ChessSide.BLACK) }
                )
            }

            Text(
                text = "Place pieces",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colors.text
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SideChip(
                    colors = colors,
                    label = "White",
                    selected = gameState.setupSide == ChessSide.WHITE && !gameState.setupEraseMode,
                    onClick = { gameState.chooseSetupSide(ChessSide.WHITE) }
                )
                SideChip(
                    colors = colors,
                    label = "Black",
                    selected = gameState.setupSide == ChessSide.BLACK && !gameState.setupEraseMode,
                    onClick = { gameState.chooseSetupSide(ChessSide.BLACK) }
                )
                SideChip(
                    colors = colors,
                    label = "Erase",
                    selected = gameState.setupEraseMode,
                    onClick = gameState::chooseEraser
                )
            }

            PiecePalette(colors = colors, gameState = gameState)
        }
    }
}

@Composable
fun SideChip(
    colors: AppColors,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = colors.button,
            selectedLabelColor = colors.buttonText,
            labelColor = colors.text
        )
    )
}

@Composable
fun PiecePalette(
    colors: AppColors,
    gameState: ChessGameState
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ChessPieceType.values().take(3).forEach { pieceType ->
                PieceChip(
                    colors = colors,
                    pieceType = pieceType,
                    selected = gameState.setupPieceType == pieceType && !gameState.setupEraseMode,
                    onClick = { gameState.chooseSetupPiece(pieceType) }
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ChessPieceType.values().drop(3).forEach { pieceType ->
                PieceChip(
                    colors = colors,
                    pieceType = pieceType,
                    selected = gameState.setupPieceType == pieceType && !gameState.setupEraseMode,
                    onClick = { gameState.chooseSetupPiece(pieceType) }
                )
            }
        }
    }
}

@Composable
fun PieceChip(
    colors: AppColors,
    pieceType: ChessPieceType,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(pieceType.label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = colors.button,
            selectedLabelColor = colors.buttonText,
            labelColor = colors.text
        )
    )
}

@Composable
fun CapturedPiecesCard(
    colors: AppColors,
    gameState: ChessGameState
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Captured",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colors.title
            )

            CapturedRow(
                colors = colors,
                label = "White",
                pieces = gameState.capturedWhite
            )

            CapturedRow(
                colors = colors,
                label = "Black",
                pieces = gameState.capturedBlack
            )
        }
    }
}

@Composable
fun CapturedRow(
    colors: AppColors,
    label: String,
    pieces: List<ChessPiece>
) {
    Text(
        text = "$label: ${if (pieces.isEmpty()) "None" else pieces.joinToString("") { it.symbol }}",
        fontSize = 14.sp,
        color = colors.text
    )
}

@Composable
fun MoveHistoryCard(
    colors: AppColors,
    gameState: ChessGameState
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Move Log",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colors.title
            )

            if (gameState.moveHistory.isEmpty()) {
                Text(
                    text = "No moves yet.",
                    fontSize = 14.sp,
                    color = colors.secondaryText
                )
            } else {
                gameState.moveHistory.take(6).forEach { move ->
                    Text(
                        text = move,
                        fontSize = 14.sp,
                        color = colors.text
                    )
                }
            }
        }
    }
}
