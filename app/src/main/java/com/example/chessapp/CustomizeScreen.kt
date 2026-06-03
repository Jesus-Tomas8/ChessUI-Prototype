package com.example.chessapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomizeScreen(
    colors: AppColors,
    customizationViewModel: CustomizationViewModel,
    onBackClick: () -> Unit
) {
    val selectedPieceSkins by customizationViewModel.selectedPieceSkins.collectAsState()
    var selectedPieceType by remember { mutableStateOf<ChessPieceType?>(null) }

    selectedPieceType?.let { pieceType ->
        PieceSkinDetailScreen(
            colors = colors,
            pieceType = pieceType,
            selectedPieceSkins = selectedPieceSkins,
            customizationViewModel = customizationViewModel,
            onBackToPiecesClick = { selectedPieceType = null },
            onBackClick = onBackClick
        )
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 32.dp, bottom = 24.dp)
        ) {
            item {
                Text(
                    text = "Customize Pieces",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.title,
                    textAlign = TextAlign.Center
                )
            }

            item {
                Text(
                    text = "Choose which piece art each side will use on the board.",
                    fontSize = 16.sp,
                    color = colors.text,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            customizationViewModel.customizablePieceTypes.forEach { pieceType ->
                item {
                    PieceSkinMenuCard(
                        colors = colors,
                        pieceType = pieceType,
                        selectedWhiteSkin = selectedPieceSkins[PieceSkinKey(ChessSide.WHITE, pieceType)],
                        selectedBlackSkin = selectedPieceSkins[PieceSkinKey(ChessSide.BLACK, pieceType)],
                        onClick = { selectedPieceType = pieceType }
                    )
                }
            }

            item {
                Text(
                    text = "More piece types can use this same skin system as new art is added.",
                    fontSize = 14.sp,
                    color = colors.secondaryText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            item {
                MainMenuButton(
                    text = "Back to Main Menu",
                    colors = colors,
                    onClick = onBackClick
                )
            }
        }
    }
}

@Composable
fun PieceSkinMenuCard(
    colors: AppColors,
    pieceType: ChessPieceType,
    selectedWhiteSkin: PieceSkin?,
    selectedBlackSkin: PieceSkin?,
    onClick: () -> Unit
) {
    PrototypeCard(
        colors = colors,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${pieceType.label} Skins",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.title
                )

                Text(
                    text = "White: ${selectedWhiteSkin?.collectionName ?: "None"}",
                    fontSize = 13.sp,
                    color = colors.secondaryText
                )

                Text(
                    text = "Black: ${selectedBlackSkin?.collectionName ?: "None"}",
                    fontSize = 13.sp,
                    color = colors.secondaryText
                )

                PrimaryActionButton(
                    text = "Open",
                    colors = colors,
                    fontSize = 14.sp,
                    verticalPadding = 2.dp,
                    onClick = onClick
                )
            }

            CurrentSkinPreview(
                colors = colors,
                label = "W",
                pieceSkin = selectedWhiteSkin,
                modifier = Modifier.weight(0.7f)
            )

            CurrentSkinPreview(
                colors = colors,
                label = "B",
                pieceSkin = selectedBlackSkin,
                modifier = Modifier.weight(0.7f)
            )
        }
    }
}

@Composable
fun PieceSkinDetailScreen(
    colors: AppColors,
    pieceType: ChessPieceType,
    selectedPieceSkins: Map<PieceSkinKey, PieceSkin>,
    customizationViewModel: CustomizationViewModel,
    onBackToPiecesClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 32.dp, bottom = 24.dp)
        ) {
            item {
                Text(
                    text = "${pieceType.label} Skins",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.title,
                    textAlign = TextAlign.Center
                )
            }

            item {
                CurrentPieceSkinCard(
                    colors = colors,
                    pieceType = pieceType,
                    selectedWhiteSkin = selectedPieceSkins[PieceSkinKey(ChessSide.WHITE, pieceType)],
                    selectedBlackSkin = selectedPieceSkins[PieceSkinKey(ChessSide.BLACK, pieceType)]
                )
            }

            ChessSide.values().forEach { side ->
                val availableSkins = customizationViewModel.availableSkinsFor(side, pieceType)
                val selectedSkin = selectedPieceSkins[PieceSkinKey(side, pieceType)]

                if (availableSkins.isNotEmpty() && selectedSkin != null) {
                    item {
                        PieceSkinOptionRow(
                            colors = colors,
                            title = "${side.label} ${pieceType.label}",
                            pieceSkins = availableSkins,
                            selectedPieceSkin = selectedSkin,
                            onPieceSkinClick = { pieceSkin ->
                                customizationViewModel.selectPieceSkin(pieceSkin)
                            }
                        )
                    }
                }
            }

            item {
                MainMenuButton(
                    text = "Back to Pieces",
                    colors = colors,
                    onClick = onBackToPiecesClick
                )
            }

            item {
                MainMenuButton(
                    text = "Back to Main Menu",
                    colors = colors,
                    onClick = onBackClick
                )
            }
        }
    }
}

@Composable
fun CurrentPieceSkinCard(
    colors: AppColors,
    pieceType: ChessPieceType,
    selectedWhiteSkin: PieceSkin?,
    selectedBlackSkin: PieceSkin?
) {
    PrototypeCard(colors = colors) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Current ${pieceType.label} Set",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colors.text
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CurrentSkinPreview(
                    colors = colors,
                    label = "White",
                    pieceSkin = selectedWhiteSkin,
                    modifier = Modifier.weight(1f)
                )

                CurrentSkinPreview(
                    colors = colors,
                    label = "Black",
                    pieceSkin = selectedBlackSkin,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun CurrentSkinPreview(
    colors: AppColors,
    label: String,
    pieceSkin: PieceSkin?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(colors.background, RoundedCornerShape(14.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (pieceSkin != null) {
            Image(
                painter = painterResource(id = pieceSkin.imageRes),
                contentDescription = pieceSkin.name,
                modifier = Modifier.size(82.dp),
                contentScale = ContentScale.Fit
            )
        }

        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = colors.text,
            textAlign = TextAlign.Center
        )

        Text(
            text = pieceSkin?.name ?: "No skin",
            fontSize = 12.sp,
            color = colors.secondaryText,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun PieceSkinOptionRow(
    colors: AppColors,
    title: String,
    pieceSkins: List<PieceSkin>,
    selectedPieceSkin: PieceSkin,
    onPieceSkinClick: (PieceSkin) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = colors.text
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(pieceSkins) { pieceSkin ->
                PieceSkinSelectionCard(
                    colors = colors,
                    pieceSkin = pieceSkin,
                    isSelected = pieceSkin.id == selectedPieceSkin.id,
                    onClick = {
                        onPieceSkinClick(pieceSkin)
                    }
                )
            }
        }
    }
}

@Composable
fun PieceSkinSelectionCard(
    colors: AppColors,
    pieceSkin: PieceSkin,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) colors.title else Color.Transparent
    val statusText = if (isSelected) "Selected" else pieceSkin.collectionName
    val statusColor = if (isSelected) colors.title else colors.secondaryText

    PrototypeCard(
        colors = colors,
        modifier = Modifier
            .size(width = 168.dp, height = 188.dp)
            .clickable { onClick() }
            .border(
                width = 3.dp,
                color = borderColor,
                shape = RoundedCornerShape(18.dp)
            ),
        elevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = pieceSkin.imageRes),
                contentDescription = pieceSkin.name,
                modifier = Modifier.size(92.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = pieceSkin.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = colors.text,
                textAlign = TextAlign.Center
            )

            Text(
                text = statusText,
                fontSize = 13.sp,
                color = statusColor,
                textAlign = TextAlign.Center
            )
        }
    }
}
