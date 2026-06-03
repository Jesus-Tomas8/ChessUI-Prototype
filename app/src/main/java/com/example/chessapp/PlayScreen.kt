package com.example.chessapp

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PlayScreen(
    colors: AppColors,
    playProfileViewModel: PlayProfileViewModel,
    onBackClick: () -> Unit
) {
    var playDestination by remember { mutableStateOf("menu") }

    when (playDestination) {
        "twoPlayer" -> {
            ChessBoardGameScreen(
                colors = colors,
                mode = ChessPlayMode.TWO_PLAYER,
                onBackClick = { playDestination = "menu" }
            )
            return
        }

        "sandbox" -> {
            ChessBoardGameScreen(
                colors = colors,
                mode = ChessPlayMode.SANDBOX,
                onBackClick = { playDestination = "menu" }
            )
            return
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 18.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 32.dp, bottom = 24.dp)
        ) {
            item {
                Text(
                    text = "Play",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.title,
                    textAlign = TextAlign.Center
                )
            }

            item {
                PlayModeCard(
                    colors = colors,
                    onTwoPlayerClick = { playDestination = "twoPlayer" },
                    onSandboxClick = { playDestination = "sandbox" }
                )
            }

            item {
                PlayerIdentityCard(
                    colors = colors,
                    profile = playProfileViewModel.playerProfile,
                    usernameInput = playProfileViewModel.usernameInput,
                    statusMessage = playProfileViewModel.statusMessage,
                    onUsernameChange = playProfileViewModel::updateUsername,
                    onSaveClick = playProfileViewModel::saveProfile
                )
            }

            item {
                MatchmakingCard(
                    colors = colors,
                    profile = playProfileViewModel.playerProfile,
                    isSearchingForMatch = playProfileViewModel.isSearchingForMatch,
                    onFindMatchClick = playProfileViewModel::startMatchmaking,
                    onCancelClick = playProfileViewModel::cancelMatchmaking
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
fun PlayModeCard(
    colors: AppColors,
    onTwoPlayerClick: () -> Unit,
    onSandboxClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Board Modes",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colors.title
            )

            Text(
                text = "Play a local chess match or build a board setup to test tactics.",
                fontSize = 15.sp,
                color = colors.secondaryText
            )

            Button(
                onClick = onTwoPlayerClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.button,
                    contentColor = colors.buttonText
                )
            ) {
                Text(
                    text = "Two Player",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }

            Button(
                onClick = onSandboxClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.button,
                    contentColor = colors.buttonText
                )
            ) {
                Text(
                    text = "Sandbox",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun PlayerIdentityCard(
    colors: AppColors,
    profile: PlayerProfile?,
    usernameInput: String,
    statusMessage: String,
    onUsernameChange: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Player Identity",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colors.title
            )

            Text(
                text = "Your ID is generated once and saved locally with your username.",
                fontSize = 15.sp,
                color = colors.secondaryText
            )

            OutlinedTextField(
                value = usernameInput,
                onValueChange = onUsernameChange,
                label = { Text("Username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    keyboardType = KeyboardType.Text
                )
            )

            PlayerIdRow(
                colors = colors,
                playerId = profile?.id ?: "Generated after save"
            )

            Text(
                text = statusMessage,
                fontSize = 14.sp,
                color = colors.secondaryText
            )

            Button(
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.button,
                    contentColor = colors.buttonText
                )
            ) {
                Text(
                    text = if (profile == null) "Create Profile" else "Update Profile",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun PlayerIdRow(
    colors: AppColors,
    playerId: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = colors.background,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Matchmaking ID",
            fontSize = 14.sp,
            color = colors.secondaryText,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = playerId,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = colors.text,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun MatchmakingCard(
    colors: AppColors,
    profile: PlayerProfile?,
    isSearchingForMatch: Boolean,
    onFindMatchClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Matchmaking",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colors.title
            )

            Text(
                text = if (profile == null) {
                    "Create a profile to enable online match search."
                } else {
                    "Ready as ${profile.username}"
                },
                fontSize = 16.sp,
                color = colors.text
            )

            Text(
                text = if (isSearchingForMatch) {
                    "Waiting for another player..."
                } else {
                    "This prototype prepares the local data that a future server can use to pair players."
                },
                fontSize = 14.sp,
                color = colors.secondaryText
            )

            if (isSearchingForMatch) {
                TextButton(
                    onClick = onCancelClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Cancel Search",
                        fontSize = 18.sp,
                        color = colors.title
                    )
                }
            } else {
                Button(
                    onClick = onFindMatchClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.button,
                        contentColor = colors.buttonText
                    )
                ) {
                    Text(
                        text = "Find Match",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}
