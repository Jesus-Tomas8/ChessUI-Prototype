package com.example.chessapp

import androidx.annotation.DrawableRes

data class PieceSkinKey(
    val side: ChessSide,
    val pieceType: ChessPieceType
) {
    val preferenceKey: String
        get() = "${side.name}_${pieceType.name}"
}

data class PieceSkin(
    val id: String,
    val name: String,
    val side: ChessSide,
    val pieceType: ChessPieceType,
    val collectionName: String,
    @param:DrawableRes val imageRes: Int
)
