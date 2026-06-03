package com.example.chessapp

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CustomizationViewModel(application: Application) : AndroidViewModel(application) {
    private val preferences = application.getSharedPreferences(
        "piece_skin_preferences",
        Context.MODE_PRIVATE
    )

    val pieceSkins = listOf(
        PieceSkin(
            id = "white_pawn",
            name = "Classic White Pawn",
            side = ChessSide.WHITE,
            pieceType = ChessPieceType.PAWN,
            collectionName = "Classic",
            imageRes = R.drawable.white_pawn
        ),
        PieceSkin(
            id = "white_pawn_mx",
            name = "MX White Pawn",
            side = ChessSide.WHITE,
            pieceType = ChessPieceType.PAWN,
            collectionName = "Mexican",
            imageRes = R.drawable.white_pawn_mx
        ),
        PieceSkin(
            id = "black_pawn",
            name = "Classic Black Pawn",
            side = ChessSide.BLACK,
            pieceType = ChessPieceType.PAWN,
            collectionName = "Classic",
            imageRes = R.drawable.black_pawn
        ),
        PieceSkin(
            id = "black_pawn_mx",
            name = "MX Black Pawn",
            side = ChessSide.BLACK,
            pieceType = ChessPieceType.PAWN,
            collectionName = "Mexican",
            imageRes = R.drawable.black_pawn_mx
        ),
        PieceSkin(
            id = "wh_king_classic",
            name = "Classic White King",
            side = ChessSide.WHITE,
            pieceType = ChessPieceType.KING,
            collectionName = "Classic",
            imageRes = R.drawable.wh_king_classic
        ),
        PieceSkin(
            id = "bl_king_classic",
            name = "Classic Black King",
            side = ChessSide.BLACK,
            pieceType = ChessPieceType.KING,
            collectionName = "Classic",
            imageRes = R.drawable.bl_king_classic
        ),
        PieceSkin(
            id = "wh_queen_classic",
            name = "Classic White Queen",
            side = ChessSide.WHITE,
            pieceType = ChessPieceType.QUEEN,
            collectionName = "Classic",
            imageRes = R.drawable.wh_queen_classic
        ),
        PieceSkin(
            id = "bl_queen_classic",
            name = "Classic Black Queen",
            side = ChessSide.BLACK,
            pieceType = ChessPieceType.QUEEN,
            collectionName = "Classic",
            imageRes = R.drawable.bl_queen_classic
        ),
        PieceSkin(
            id = "wh_rook_classic",
            name = "Classic White Rook",
            side = ChessSide.WHITE,
            pieceType = ChessPieceType.ROOK,
            collectionName = "Classic",
            imageRes = R.drawable.wh_rook_classic
        ),
        PieceSkin(
            id = "bl_rook_classic",
            name = "Classic Black Rook",
            side = ChessSide.BLACK,
            pieceType = ChessPieceType.ROOK,
            collectionName = "Classic",
            imageRes = R.drawable.bl_rook_classic
        ),
        PieceSkin(
            id = "wh_bishop_classic",
            name = "Classic White Bishop",
            side = ChessSide.WHITE,
            pieceType = ChessPieceType.BISHOP,
            collectionName = "Classic",
            imageRes = R.drawable.wh_bishop_classic
        ),
        PieceSkin(
            id = "bl_bishop_classic",
            name = "Classic Black Bishop",
            side = ChessSide.BLACK,
            pieceType = ChessPieceType.BISHOP,
            collectionName = "Classic",
            imageRes = R.drawable.bl_bishop_classic
        ),
        PieceSkin(
            id = "wh_knight_classic",
            name = "Classic White Knight",
            side = ChessSide.WHITE,
            pieceType = ChessPieceType.KNIGHT,
            collectionName = "Classic",
            imageRes = R.drawable.wh_knight_classic
        ),
        PieceSkin(
            id = "bl_knight_classic",
            name = "Classic Black Knight",
            side = ChessSide.BLACK,
            pieceType = ChessPieceType.KNIGHT,
            collectionName = "Classic",
            imageRes = R.drawable.bl_knight_classic
        )
    )

    val customizablePieceTypes = listOf(
        ChessPieceType.PAWN,
        ChessPieceType.KING,
        ChessPieceType.QUEEN,
        ChessPieceType.ROOK,
        ChessPieceType.BISHOP,
        ChessPieceType.KNIGHT
    )

    private val skinsByKey = pieceSkins.groupBy { PieceSkinKey(it.side, it.pieceType) }

    private val _selectedPieceSkins = MutableStateFlow(loadSelectedPieceSkins())
    val selectedPieceSkins: StateFlow<Map<PieceSkinKey, PieceSkin>> = _selectedPieceSkins.asStateFlow()

    fun availableSkinsFor(side: ChessSide, pieceType: ChessPieceType): List<PieceSkin> {
        return skinsByKey[PieceSkinKey(side, pieceType)].orEmpty()
    }

    fun selectedSkinFor(side: ChessSide, pieceType: ChessPieceType): PieceSkin? {
        val key = PieceSkinKey(side, pieceType)
        return _selectedPieceSkins.value[key] ?: skinsByKey[key]?.firstOrNull()
    }

    fun selectPieceSkin(pieceSkin: PieceSkin) {
        val key = PieceSkinKey(pieceSkin.side, pieceSkin.pieceType)
        _selectedPieceSkins.value = _selectedPieceSkins.value + (key to pieceSkin)

        preferences.edit()
            .putString(key.preferenceKey, pieceSkin.id)
            .apply()
    }

    private fun loadSelectedPieceSkins(): Map<PieceSkinKey, PieceSkin> {
        return skinsByKey.mapValues { (key, skins) ->
            val savedSkinId = preferences.getString(key.preferenceKey, null)
            skins.firstOrNull { skin -> skin.id == savedSkinId } ?: skins.first()
        }
    }
}
