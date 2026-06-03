package com.example.chessapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OptionsScreen(
    colors: AppColors,
    settingsViewModel: SettingsViewModel,
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Options",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = colors.title,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Adjust your game settings.",
                fontSize = 18.sp,
                color = colors.text,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            OptionItem(
                colors = colors,
                title = "Dark Mode",
                description = "Switch between light and dark mode.",
                checked = settingsViewModel.isDarkMode,
                onCheckedChange = { settingsViewModel.updateDarkMode(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OptionItem(
                colors = colors,
                title = "Volume",
                description = "Placeholder sound setting for future game audio.",
                checked = settingsViewModel.isVolumeOn,
                onCheckedChange = { settingsViewModel.updateVolume(it) }
            )

            Spacer(modifier = Modifier.height(28.dp))

            MainMenuButton(
                text = "Back to Main Menu",
                colors = colors,
                onClick = onBackClick
            )
        }
    }
}

@Composable
fun OptionItem(
    colors: AppColors,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.card
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.text
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = colors.secondaryText
                )
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}