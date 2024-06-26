import java.util.ArrayList;
import java.util.List;

public class Ai {
    private static final int MAX_DEPTH = 4;
    private static final int WIN_SCORE = 10000;
    private static final int LOSE_SCORE = -10000;

    public int minimax(Board board, int depth, boolean isMaximizingPlayer, int alpha, int beta) {
        if (depth == MAX_DEPTH || isGameOver(board)) {
            return evaluate(board, isMaximizingPlayer);
        }

        if (isMaximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            outerLoop:
            for (Position pos : getAllPawnsPositions(board, true)) {
                for (Position move : board.getAvailableMoves(pos)) {
                    Board newBoard = new Board(board);
                    newBoard.movePawn(pos, move);
                    int eval = minimax(newBoard, depth + 1, false, alpha, beta);
                    maxEval = Math.max(maxEval, eval);
                    alpha = Math.max(alpha, eval);
                    if (beta <= alpha) {
                        break  outerLoop; // Beta cut-off
                    }
                }
            }
            return maxEval-1;
        } else {
            int minEval = Integer.MAX_VALUE;
            outerLoop:
            for (Position pos : getAllPawnsPositions(board, false)) {
                for (Position move : board.getAvailableMoves(pos)) {
                    Board newBoard = new Board(board);
                    newBoard.movePawn(pos, move);
                    int eval = minimax(newBoard, depth + 1, true, alpha, beta);
                    minEval = Math.min(minEval, eval);
                    beta = Math.min(beta, eval);
                    if (beta <= alpha) {
                        break  outerLoop; // Alpha cut-off
                    }
                }
            }
            return minEval+1;
        }
    }

    private int evaluate(Board board, boolean isMaximizingPlayer) {
        int scoreMax = 0;
        int scoreMin = 0;
        int nbBlack = 0;
        int nbWhite = 0;
        boolean whiteCanWin = false;

        if (isGameOver(board)) {
            for (int col = 0; col < 8; col++) {
                if (board.getPawnAt(new Position(0, col)) != null && !board.getPawnAt(new Position(0, col)).isWhite()) {
                    return isMaximizingPlayer ? LOSE_SCORE : WIN_SCORE;
                }
                if (board.getPawnAt(new Position(7, col)) != null && board.getPawnAt(new Position(7, col)).isWhite()) {
                    return isMaximizingPlayer ? LOSE_SCORE : WIN_SCORE;
                }
            }
        }

        for (Position pos : getAllPawnsPositions(board, true)) {
            Pawn pawn = board.getPawnAt(pos);
            if (pawn != null) {
                nbWhite++;
                // Vérifier si le pion blanc va gagner dans le prochain coup
                if (pos.getRow() == 6) {
                    if (isMaximizingPlayer) {
                        return WIN_SCORE; // C'est le tour des blancs et ils gagnent
                    } else if (!canBeCaptured(board, pos, false)) {
                        whiteCanWin = true; // Ce n'est pas le tour des blancs, mais ils ne peuvent pas être capturés
                    }
                }

                // Avancement des pions
                scoreMax += pos.getRow();

                // Protection des pions
                scoreMax += isProtectedSquare(board, pos, true);
            }
        }

        for (Position pos : getAllPawnsPositions(board, false)) {
            Pawn pawn = board.getPawnAt(pos);
            if (pawn != null) {
                nbBlack++;
                // Vérifier si le pion noir va gagner dans le prochain coup
                if (pos.getRow() == 1) {
                    if (!isMaximizingPlayer) {
                        return LOSE_SCORE; // C'est le tour des noirs et ils gagnent
                    }
                    else if (!canBeCaptured(board, pos, true)) {
                        return LOSE_SCORE; // Ce n'est pas le tour des noirs, mais ils ne peuvent pas être capturés
                    }
                }

                // Avancement des pions
                scoreMin += (7 - pos.getRow());

                // Protection des pions
                scoreMin += isProtectedSquare(board, pos, false);
            }
        }
        if(whiteCanWin)
            return WIN_SCORE;


        return (scoreMax - scoreMin) + (nbWhite - nbBlack)*5 + checkBoard(board);
    }

    private boolean canBeCaptured(Board board, Position position, boolean byWhite) {
        int row = position.getRow();
        int col = position.getCol();
        int direction = byWhite ? -1 : 1;

        // Vérifier les positions diagonales pour une capture potentielle
        if (isValidPosition(row + direction, col - 1) && board.getPawnAt(new Position(row + direction, col - 1)) != null && board.getPawnAt(new Position(row + direction, col - 1)).isWhite() == byWhite) {
            return true;
        }
        if (isValidPosition(row + direction, col + 1) && board.getPawnAt(new Position(row + direction, col + 1)) != null && board.getPawnAt(new Position(row + direction, col + 1)).isWhite() == byWhite) {
            return true;
        }

        return false;
    }

    private int isProtectedSquare(Board board, Position position, boolean isWhite) {
        int row = position.getRow();
        int col = position.getCol();
        int nbProtecters = 0;

        int direction = isWhite ? -1 : 1;

        for (int i = -1; i <= 1; i++) {
            if(i!=0) {
                int newRow = row + direction;
                int newCol = col + i;
                if (isValidPosition(newRow, newCol)) {
                    Pawn pawn = board.getPawnAt(new Position(newRow, newCol));
                    if (pawn != null && pawn.isWhite() == isWhite) {
                        nbProtecters++;
                    }
                }
            }
        }

        return nbProtecters;
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    private boolean isGameOver(Board board) {
        for (int col = 0; col < 8; col++) {
            if (board.getPawnAt(new Position(0, col)) != null && !board.getPawnAt(new Position(0, col)).isWhite()) {
                return true;
            }
            if (board.getPawnAt(new Position(7, col)) != null && board.getPawnAt(new Position(7, col)).isWhite()) {
                return true;
            }
        }
        return false;
    }


    private List<Position> getAllPawnsPositions(Board board, boolean isWhite) {
        List<Position> positions = new ArrayList<>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Pawn pawn = board.getPawnAt(new Position(row, col));
                if (pawn != null && pawn.isWhite() == isWhite) {
                    positions.add(new Position(row, col));
                }
            }
        }
        return positions;
    }

    private int checkBoard(Board board) {
        int score = 0;
        for (int col = 0; col < 8; col++) {
            for (int row = 0; row < 8; row++) {
                Pawn pawn = board.getPawnAt(new Position(row, col));
                if (pawn != null && pawn.isWhite()) {
                    if(!pawn.isHasMoved())
                        score ++;
                    score++;
                }
                else if (pawn != null && !pawn.isWhite()) {
                    if(!pawn.isHasMoved())
                        score --;
                    score --;
                }
            }
        }
        return score;
    }
}
