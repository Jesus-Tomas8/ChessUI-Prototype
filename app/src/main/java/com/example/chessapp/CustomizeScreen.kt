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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    val selectedWhitePawnSkin by customizationViewModel.selectedWhitePawnSkin.collectAsState()
    val selectedBlackPawnSkin by customizationViewModel.selectedBlackPawnSkin.collectAsState()

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
                    text = "Choose which pawn design each side will use on the board.",
                    fontSize = 16.sp,
                    color = colors.text,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            item {
                PawnSkinSection(
                    colors = colors,
                    title = "White Pawn",
                    pawnSkins = customizationViewModel.whitePawnSkins,
                    selectedPawnSkin = selectedWhitePawnSkin,
                    onPawnSkinClick = { pawnSkin ->
                        customizationViewModel.selectWhitePawnSkin(pawnSkin)
                    }
                )
            }

            item {
                PawnSkinSection(
                    colors = colors,
                    title = "Black Pawn",
                    pawnSkins = customizationViewModel.blackPawnSkins,
                    selectedPawnSkin = selectedBlackPawnSkin,
                    onPawnSkinClick = { pawnSkin ->
                        customizationViewModel.selectBlackPawnSkin(pawnSkin)
                    }
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
fun PawnSkinSection(
    colors: AppColors,
    title: String,
    pawnSkins: List<PawnSkin>,
    selectedPawnSkin: PawnSkin,
    onPawnSkinClick: (PawnSkin) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = colors.title,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        pawnSkins.forEach { pawnSkin ->
            PawnSkinSelectionCard(
                colors = colors,
                pawnSkin = pawnSkin,
                isSelected = pawnSkin.id == selectedPawnSkin.id,
                onClick = {
                    onPawnSkinClick(pawnSkin)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun PawnSkinSelectionCard(
    colors: AppColors,
    pawnSkin: PawnSkin,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) colors.title else Color.Transparent
    val statusText = if (isSelected) "Selected" else "Tap to Select"
    val statusColor = if (isSelected) colors.title else colors.text

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                width = 3.dp,
                color = borderColor,
                shape = RoundedCornerShape(18.dp)
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.card
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = pawnSkin.imageRes),
                contentDescription = pawnSkin.name,
                modifier = Modifier.size(120.dp),
                contentScale = ContentScale.Fit
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 18.dp)
            ) {
                Text(
                    text = pawnSkin.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.text
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = statusText,
                    fontSize = 16.sp,
                    color = statusColor
                )
            }
        }
    }
}