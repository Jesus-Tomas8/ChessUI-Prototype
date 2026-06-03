package com.example.chessapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.math.abs

enum class ChessSide(val label: String, val resourcePrefix: String) {
    WHITE("White", "white"),
    BLACK("Black", "black");

    fun opposite(): ChessSide {
        return if (this == WHITE) BLACK else WHITE
    }
}

enum class ChessPieceType(
    val label: String,
    val resourceName: String,
    val whiteSymbol: String,
    val blackSymbol: String
) {
    KING("King", "king", "♔", "♚"),
    QUEEN("Queen", "queen", "♕", "♛"),
    ROOK("Rook", "rook", "♖", "♜"),
    BISHOP("Bishop", "bishop", "♗", "♝"),
    KNIGHT("Knight", "knight", "♘", "♞"),
    PAWN("Pawn", "pawn", "♙", "♟")
}

data class BoardPosition(val row: Int, val col: Int) {
    val isInsideBoard: Boolean
        get() = row in 0..7 && col in 0..7
}

data class ChessPiece(
    val side: ChessSide,
    val type: ChessPieceType,
    val hasMoved: Boolean = false
) {
    val symbol: String
        get() = if (side == ChessSide.WHITE) type.whiteSymbol else type.blackSymbol
}

enum class ChessPlayMode {
    TWO_PLAYER,
    SANDBOX
}

data class ChessMoveAnimation(
    val id: Int,
    val piece: ChessPiece,
    val from: BoardPosition,
    val to: BoardPosition
)

class ChessGameState(private val playMode: ChessPlayMode) {
    var board by mutableStateOf(standardBoard())
        private set

    var turn by mutableStateOf(ChessSide.WHITE)
        private set

    var selectedPosition by mutableStateOf<BoardPosition?>(null)
        private set

    var highlightedTargets by mutableStateOf<Set<BoardPosition>>(emptySet())
        private set

    var statusText by mutableStateOf("White to move.")
        private set

    var sandboxSetupMode by mutableStateOf(playMode == ChessPlayMode.SANDBOX)
        private set

    var setupSide by mutableStateOf(ChessSide.WHITE)
        private set

    var setupPieceType by mutableStateOf(ChessPieceType.QUEEN)
        private set

    var setupEraseMode by mutableStateOf(false)
        private set

    var lastMoveAnimation by mutableStateOf<ChessMoveAnimation?>(null)
        private set

    private var moveAnimationId = 0

    val moveHistory = mutableStateListOf<String>()

    val capturedWhite: List<ChessPiece>
        get() = capturedPieces(ChessSide.WHITE)

    val capturedBlack: List<ChessPiece>
        get() = capturedPieces(ChessSide.BLACK)

    fun onSquareTapped(position: BoardPosition) {
        if (playMode == ChessPlayMode.SANDBOX && sandboxSetupMode) {
            editSandboxSquare(position)
            return
        }

        val selected = selectedPosition
        val tappedPiece = board[position]

        if (selected == null) {
            if (tappedPiece?.side == turn) {
                selectPosition(position)
            } else {
                clearSelection()
            }
            return
        }

        if (position in highlightedTargets) {
            movePiece(selected, position)
            return
        }

        if (tappedPiece?.side == turn) {
            selectPosition(position)
        } else {
            statusText = "${turn.label} cannot move there."
            clearSelection()
        }
    }

    fun resetStandardGame() {
        board = standardBoard()
        turn = ChessSide.WHITE
        moveHistory.clear()
        lastMoveAnimation = null
        sandboxSetupMode = playMode == ChessPlayMode.SANDBOX
        clearSelection()
        statusText = if (sandboxSetupMode) {
            "Sandbox setup ready."
        } else {
            "White to move."
        }
    }

    fun clearSandboxBoard() {
        if (playMode != ChessPlayMode.SANDBOX) return

        board = emptyMap()
        turn = ChessSide.WHITE
        moveHistory.clear()
        lastMoveAnimation = null
        clearSelection()
        statusText = "Board cleared. Place pieces to test a strategy."
    }

    fun toggleSandboxMode() {
        if (playMode != ChessPlayMode.SANDBOX) return

        sandboxSetupMode = !sandboxSetupMode
        clearSelection()
        statusText = if (sandboxSetupMode) {
            "Setup mode: tap a square to place or erase pieces."
        } else {
            "${turn.label} to move from this setup."
        }
    }

    fun chooseSetupSide(side: ChessSide) {
        setupSide = side
        setupEraseMode = false
    }

    fun chooseSetupPiece(pieceType: ChessPieceType) {
        setupPieceType = pieceType
        setupEraseMode = false
    }

    fun chooseEraser() {
        setupEraseMode = true
    }

    fun chooseTurn(side: ChessSide) {
        if (playMode != ChessPlayMode.SANDBOX) return

        turn = side
        clearSelection()
        statusText = "${turn.label} to move from this setup."
    }

    private fun selectPosition(position: BoardPosition) {
        val piece = board[position] ?: return

        selectedPosition = position
        highlightedTargets = legalMovesFrom(position, board)
        statusText = if (highlightedTargets.isEmpty()) {
            "${piece.side.label} ${piece.type.label.lowercase()} has no legal moves."
        } else {
            "${piece.side.label} ${piece.type.label.lowercase()} selected."
        }
    }

    private fun movePiece(from: BoardPosition, to: BoardPosition) {
        val movingPiece = board[from] ?: return
        val capturedPiece = board[to]
        val promotedPiece = if (
            movingPiece.type == ChessPieceType.PAWN &&
            (to.row == 0 || to.row == 7)
        ) {
            movingPiece.copy(type = ChessPieceType.QUEEN, hasMoved = true)
        } else {
            movingPiece.copy(hasMoved = true)
        }

        board = board
            .minus(from)
            .plus(to to promotedPiece)

        moveAnimationId += 1
        lastMoveAnimation = ChessMoveAnimation(
            id = moveAnimationId,
            piece = promotedPiece,
            from = from,
            to = to
        )

        val moveText = buildMoveText(movingPiece, from, to, capturedPiece, promotedPiece)
        moveHistory.add(0, moveText)
        turn = turn.opposite()
        clearSelection()
        updateGameStatusAfterMove()
    }

    private fun updateGameStatusAfterMove() {
        val isInCheck = isKingInCheck(board, turn)
        val hasMove = hasAnyLegalMove(turn, board)

        statusText = when {
            isInCheck && !hasMove -> "Checkmate. ${turn.opposite().label} wins."
            !isInCheck && !hasMove -> "Stalemate."
            isInCheck -> "${turn.label} king is in check."
            else -> "${turn.label} to move."
        }
    }

    private fun editSandboxSquare(position: BoardPosition) {
        board = if (setupEraseMode) {
            board.minus(position)
        } else {
            board.plus(position to ChessPiece(setupSide, setupPieceType))
        }

        lastMoveAnimation = null
        clearSelection()
        statusText = if (setupEraseMode) {
            "Removed piece from ${position.toAlgebraic()}."
        } else {
            "Placed ${setupSide.label} ${setupPieceType.label.lowercase()} on ${position.toAlgebraic()}."
        }
    }

    private fun clearSelection() {
        selectedPosition = null
        highlightedTargets = emptySet()
    }

    private fun legalMovesFrom(
        position: BoardPosition,
        currentBoard: Map<BoardPosition, ChessPiece>
    ): Set<BoardPosition> {
        val piece = currentBoard[position] ?: return emptySet()

        return pseudoLegalMoves(position, piece, currentBoard)
            .filter { target ->
                val movedPiece = if (
                    piece.type == ChessPieceType.PAWN &&
                    (target.row == 0 || target.row == 7)
                ) {
                    piece.copy(type = ChessPieceType.QUEEN, hasMoved = true)
                } else {
                    piece.copy(hasMoved = true)
                }

                val nextBoard = currentBoard
                    .minus(position)
                    .plus(target to movedPiece)

                !isKingInCheck(nextBoard, piece.side)
            }
            .toSet()
    }

    private fun pseudoLegalMoves(
        position: BoardPosition,
        piece: ChessPiece,
        currentBoard: Map<BoardPosition, ChessPiece>
    ): Set<BoardPosition> {
        return when (piece.type) {
            ChessPieceType.PAWN -> pawnMoves(position, piece, currentBoard)
            ChessPieceType.KNIGHT -> jumpMoves(
                position,
                piece,
                currentBoard,
                listOf(-2 to -1, -2 to 1, -1 to -2, -1 to 2, 1 to -2, 1 to 2, 2 to -1, 2 to 1)
            )
            ChessPieceType.BISHOP -> slidingMoves(
                position,
                piece,
                currentBoard,
                listOf(-1 to -1, -1 to 1, 1 to -1, 1 to 1)
            )
            ChessPieceType.ROOK -> slidingMoves(
                position,
                piece,
                currentBoard,
                listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)
            )
            ChessPieceType.QUEEN -> slidingMoves(
                position,
                piece,
                currentBoard,
                listOf(-1 to -1, -1 to 1, 1 to -1, 1 to 1, -1 to 0, 1 to 0, 0 to -1, 0 to 1)
            )
            ChessPieceType.KING -> jumpMoves(
                position,
                piece,
                currentBoard,
                listOf(-1 to -1, -1 to 0, -1 to 1, 0 to -1, 0 to 1, 1 to -1, 1 to 0, 1 to 1)
            )
        }
    }

    private fun pawnMoves(
        position: BoardPosition,
        piece: ChessPiece,
        currentBoard: Map<BoardPosition, ChessPiece>
    ): Set<BoardPosition> {
        val moves = mutableSetOf<BoardPosition>()
        val direction = if (piece.side == ChessSide.WHITE) -1 else 1
        val startRow = if (piece.side == ChessSide.WHITE) 6 else 1
        val oneStep = BoardPosition(position.row + direction, position.col)

        if (oneStep.isInsideBoard && currentBoard[oneStep] == null) {
            moves.add(oneStep)

            val twoStep = BoardPosition(position.row + direction * 2, position.col)
            if (
                position.row == startRow &&
                !piece.hasMoved &&
                twoStep.isInsideBoard &&
                currentBoard[twoStep] == null
            ) {
                moves.add(twoStep)
            }
        }

        listOf(-1, 1).forEach { colOffset ->
            val capture = BoardPosition(position.row + direction, position.col + colOffset)
            val target = currentBoard[capture]
            if (capture.isInsideBoard && target != null && target.side != piece.side) {
                moves.add(capture)
            }
        }

        return moves
    }

    private fun jumpMoves(
        position: BoardPosition,
        piece: ChessPiece,
        currentBoard: Map<BoardPosition, ChessPiece>,
        offsets: List<Pair<Int, Int>>
    ): Set<BoardPosition> {
        return offsets
            .map { (rowOffset, colOffset) ->
                BoardPosition(position.row + rowOffset, position.col + colOffset)
            }
            .filter { target ->
                target.isInsideBoard && currentBoard[target]?.side != piece.side
            }
            .toSet()
    }

    private fun slidingMoves(
        position: BoardPosition,
        piece: ChessPiece,
        currentBoard: Map<BoardPosition, ChessPiece>,
        directions: List<Pair<Int, Int>>
    ): Set<BoardPosition> {
        val moves = mutableSetOf<BoardPosition>()

        directions.forEach { (rowDirection, colDirection) ->
            var next = BoardPosition(position.row + rowDirection, position.col + colDirection)

            while (next.isInsideBoard) {
                val targetPiece = currentBoard[next]

                if (targetPiece == null) {
                    moves.add(next)
                } else {
                    if (targetPiece.side != piece.side) {
                        moves.add(next)
                    }
                    break
                }

                next = BoardPosition(next.row + rowDirection, next.col + colDirection)
            }
        }

        return moves
    }

    private fun isKingInCheck(
        currentBoard: Map<BoardPosition, ChessPiece>,
        side: ChessSide
    ): Boolean {
        val kingPosition = currentBoard.entries
            .firstOrNull { (_, piece) -> piece.side == side && piece.type == ChessPieceType.KING }
            ?.key
            ?: return false

        return isSquareAttacked(kingPosition, side.opposite(), currentBoard)
    }

    private fun isSquareAttacked(
        position: BoardPosition,
        attackingSide: ChessSide,
        currentBoard: Map<BoardPosition, ChessPiece>
    ): Boolean {
        return currentBoard.any { (attackerPosition, piece) ->
            piece.side == attackingSide && attacksSquare(attackerPosition, piece, position, currentBoard)
        }
    }

    private fun attacksSquare(
        attackerPosition: BoardPosition,
        piece: ChessPiece,
        target: BoardPosition,
        currentBoard: Map<BoardPosition, ChessPiece>
    ): Boolean {
        val rowDelta = target.row - attackerPosition.row
        val colDelta = target.col - attackerPosition.col

        return when (piece.type) {
            ChessPieceType.PAWN -> {
                val direction = if (piece.side == ChessSide.WHITE) -1 else 1
                rowDelta == direction && abs(colDelta) == 1
            }
            ChessPieceType.KNIGHT -> {
                (abs(rowDelta) == 2 && abs(colDelta) == 1) ||
                    (abs(rowDelta) == 1 && abs(colDelta) == 2)
            }
            ChessPieceType.KING -> abs(rowDelta) <= 1 && abs(colDelta) <= 1
            ChessPieceType.BISHOP -> canSlideTo(
                attackerPosition,
                target,
                currentBoard,
                listOf(-1 to -1, -1 to 1, 1 to -1, 1 to 1)
            )
            ChessPieceType.ROOK -> canSlideTo(
                attackerPosition,
                target,
                currentBoard,
                listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)
            )
            ChessPieceType.QUEEN -> canSlideTo(
                attackerPosition,
                target,
                currentBoard,
                listOf(-1 to -1, -1 to 1, 1 to -1, 1 to 1, -1 to 0, 1 to 0, 0 to -1, 0 to 1)
            )
        }
    }

    private fun canSlideTo(
        from: BoardPosition,
        target: BoardPosition,
        currentBoard: Map<BoardPosition, ChessPiece>,
        directions: List<Pair<Int, Int>>
    ): Boolean {
        directions.forEach { (rowDirection, colDirection) ->
            var next = BoardPosition(from.row + rowDirection, from.col + colDirection)

            while (next.isInsideBoard) {
                if (next == target) return true
                if (currentBoard[next] != null) break
                next = BoardPosition(next.row + rowDirection, next.col + colDirection)
            }
        }

        return false
    }

    private fun hasAnyLegalMove(
        side: ChessSide,
        currentBoard: Map<BoardPosition, ChessPiece>
    ): Boolean {
        return currentBoard.any { (position, piece) ->
            piece.side == side && legalMovesFrom(position, currentBoard).isNotEmpty()
        }
    }

    private fun capturedPieces(side: ChessSide): List<ChessPiece> {
        val currentCount = board.values
            .filter { it.side == side }
            .groupingBy { it.type }
            .eachCount()

        return startingPieceCounts.flatMap { (pieceType, startingCount) ->
            val missingCount = startingCount - (currentCount[pieceType] ?: 0)
            List(missingCount.coerceAtLeast(0)) { ChessPiece(side, pieceType) }
        }
    }

    private fun buildMoveText(
        movingPiece: ChessPiece,
        from: BoardPosition,
        to: BoardPosition,
        capturedPiece: ChessPiece?,
        promotedPiece: ChessPiece
    ): String {
        val captureText = if (capturedPiece == null) "-" else "x"
        val promotionText = if (movingPiece.type != promotedPiece.type) {
            "=Q"
        } else {
            ""
        }

        return "${movingPiece.side.label} ${movingPiece.type.label}: " +
            "${from.toAlgebraic()}$captureText${to.toAlgebraic()}$promotionText"
    }

    fun kingInCheckPosition(): BoardPosition? {
        val sideInCheck = ChessSide.values().firstOrNull { side -> isKingInCheck(board, side) }

        return sideInCheck?.let { checkedSide ->
            board.entries
                .firstOrNull { (_, piece) ->
                    piece.side == checkedSide && piece.type == ChessPieceType.KING
                }
                ?.key
        }
    }

    companion object {
        private val startingPieceCounts = mapOf(
            ChessPieceType.KING to 1,
            ChessPieceType.QUEEN to 1,
            ChessPieceType.ROOK to 2,
            ChessPieceType.BISHOP to 2,
            ChessPieceType.KNIGHT to 2,
            ChessPieceType.PAWN to 8
        )

        fun standardBoard(): Map<BoardPosition, ChessPiece> {
            val pieces = mutableMapOf<BoardPosition, ChessPiece>()
            val backRank = listOf(
                ChessPieceType.ROOK,
                ChessPieceType.KNIGHT,
                ChessPieceType.BISHOP,
                ChessPieceType.QUEEN,
                ChessPieceType.KING,
                ChessPieceType.BISHOP,
                ChessPieceType.KNIGHT,
                ChessPieceType.ROOK
            )

            backRank.forEachIndexed { col, pieceType ->
                pieces[BoardPosition(0, col)] = ChessPiece(ChessSide.BLACK, pieceType)
                pieces[BoardPosition(7, col)] = ChessPiece(ChessSide.WHITE, pieceType)
            }

            for (col in 0..7) {
                pieces[BoardPosition(1, col)] = ChessPiece(ChessSide.BLACK, ChessPieceType.PAWN)
                pieces[BoardPosition(6, col)] = ChessPiece(ChessSide.WHITE, ChessPieceType.PAWN)
            }

            return pieces
        }
    }
}

fun BoardPosition.toAlgebraic(): String {
    val file = ('a'.code + col).toChar()
    val rank = 8 - row
    return "$file$rank"
}
