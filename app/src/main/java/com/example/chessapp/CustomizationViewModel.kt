package com.example.chessapp

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CustomizationViewModel : ViewModel() {

    val whitePawnSkins = listOf(
        PawnSkin(
            id = "white_pawn",
            name = "Classic White Pawn",
            imageRes = R.drawable.white_pawn
        ),

        PawnSkin(
            id = "white_pawn_mx",
            name = "MX White Pawn",
            imageRes = R.drawable.white_pawn_mx
        )
    )

    val blackPawnSkins = listOf(
        PawnSkin(
            id = "black_pawn",
            name = "Classic Black Pawn",
            imageRes = R.drawable.black_pawn

        ),

        PawnSkin(
            id = "white_pawn_mx",
            name = "MX Black Pawn",
            imageRes = R.drawable.black_pawn_mx
        )
    )

    private val _selectedWhitePawnSkin = MutableStateFlow(whitePawnSkins[0])
    val selectedWhitePawnSkin: StateFlow<PawnSkin> = _selectedWhitePawnSkin

    private val _selectedBlackPawnSkin = MutableStateFlow(blackPawnSkins[0])
    val selectedBlackPawnSkin: StateFlow<PawnSkin> = _selectedBlackPawnSkin

    fun selectWhitePawnSkin(pawnSkin: PawnSkin) {
        _selectedWhitePawnSkin.value = pawnSkin
    }

    fun selectBlackPawnSkin(pawnSkin: PawnSkin) {
        _selectedBlackPawnSkin.value = pawnSkin
    }
}
