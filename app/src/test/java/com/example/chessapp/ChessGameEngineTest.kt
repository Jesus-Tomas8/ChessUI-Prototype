package com.example.chessapp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ChessGameEngineTest {
    @Test
    fun whitePawnCanMoveOneOrTwoSquaresFromStart() {
        val gameState = ChessGameState(ChessPlayMode.TWO_PLAYER)

        gameState.onSquareTapped(BoardPosition(6, 4))

        assertTrue(BoardPosition(5, 4) in gameState.highlightedTargets)
        assertTrue(BoardPosition(4, 4) in gameState.highlightedTargets)

        gameState.onSquareTapped(BoardPosition(4, 4))

        assertEquals(ChessPieceType.PAWN, gameState.board[BoardPosition(4, 4)]?.type)
        assertEquals(ChessSide.WHITE, gameState.board[BoardPosition(4, 4)]?.side)
        assertEquals(ChessSide.BLACK, gameState.turn)
    }

    @Test
    fun rookStartsBlockedByOwnPawn() {
        val gameState = ChessGameState(ChessPlayMode.TWO_PLAYER)

        gameState.onSquareTapped(BoardPosition(7, 0))

        assertTrue(gameState.highlightedTargets.isEmpty())
        assertTrue(gameState.statusText.contains("no legal moves"))
    }

    @Test
    fun sandboxCanPlaceAndErasePieces() {
        val gameState = ChessGameState(ChessPlayMode.SANDBOX)

        gameState.clearSandboxBoard()
        gameState.chooseSetupSide(ChessSide.BLACK)
        gameState.chooseSetupPiece(ChessPieceType.KING)
        gameState.onSquareTapped(BoardPosition(0, 4))

        assertEquals(ChessPiece(ChessSide.BLACK, ChessPieceType.KING), gameState.board[BoardPosition(0, 4)])

        gameState.chooseEraser()
        gameState.onSquareTapped(BoardPosition(0, 4))

        assertFalse(gameState.board.containsKey(BoardPosition(0, 4)))
    }
}
