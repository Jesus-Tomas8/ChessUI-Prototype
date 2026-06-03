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
        skin("white_pawn", "Classic White Pawn", ChessSide.WHITE, ChessPieceType.PAWN, "Classic", R.drawable.white_pawn),
        skin("white_pawn_mx", "MX White Pawn", ChessSide.WHITE, ChessPieceType.PAWN, "Mexican", R.drawable.white_pawn_mx),
        skin("black_pawn", "Classic Black Pawn", ChessSide.BLACK, ChessPieceType.PAWN, "Classic", R.drawable.black_pawn),
        skin("black_pawn_mx", "MX Black Pawn", ChessSide.BLACK, ChessPieceType.PAWN, "Mexican", R.drawable.black_pawn_mx),
        skin("wh_king_classic", "Classic White King", ChessSide.WHITE, ChessPieceType.KING, "Classic", R.drawable.wh_king_classic),
        skin("bl_king_classic", "Classic Black King", ChessSide.BLACK, ChessPieceType.KING, "Classic", R.drawable.bl_king_classic),
        skin("wh_queen_classic", "Classic White Queen", ChessSide.WHITE, ChessPieceType.QUEEN, "Classic", R.drawable.wh_queen_classic),
        skin("bl_queen_classic", "Classic Black Queen", ChessSide.BLACK, ChessPieceType.QUEEN, "Classic", R.drawable.bl_queen_classic),
        skin("wh_rook_classic", "Classic White Rook", ChessSide.WHITE, ChessPieceType.ROOK, "Classic", R.drawable.wh_rook_classic),
        skin("bl_rook_classic", "Classic Black Rook", ChessSide.BLACK, ChessPieceType.ROOK, "Classic", R.drawable.bl_rook_classic),
        skin("wh_bishop_classic", "Classic White Bishop", ChessSide.WHITE, ChessPieceType.BISHOP, "Classic", R.drawable.wh_bishop_classic),
        skin("bl_bishop_classic", "Classic Black Bishop", ChessSide.BLACK, ChessPieceType.BISHOP, "Classic", R.drawable.bl_bishop_classic),
        skin("wh_knight_classic", "Classic White Knight", ChessSide.WHITE, ChessPieceType.KNIGHT, "Classic", R.drawable.wh_knight_classic),
        skin("bl_knight_classic", "Classic Black Knight", ChessSide.BLACK, ChessPieceType.KNIGHT, "Classic", R.drawable.bl_knight_classic)
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

    private fun skin(
        id: String,
        name: String,
        side: ChessSide,
        pieceType: ChessPieceType,
        collectionName: String,
        imageRes: Int
    ): PieceSkin {
        return PieceSkin(
            id = id,
            name = name,
            side = side,
            pieceType = pieceType,
            collectionName = collectionName,
            imageRes = imageRes
        )
    }
}
