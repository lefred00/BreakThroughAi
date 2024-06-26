import java.util.ArrayList;
import java.util.List;

public class Ai {
    private static final int MAX_DEPTH = 5;
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
                // Vérifier si un joueur a une pièce inarrêtable
//                if (isUnstoppable(board, pos)) {
//                    return WIN_SCORE;
//                }
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
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            outerLoop:
            for (Position pos : getAllPawnsPositions(board, false)) {
                // Vérifier si un joueur a une pièce inarrêtable
//                if (isUnstoppable(board, pos)) {
//                    return LOSE_SCORE;
//                }
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
            return minEval;
        }
    }

    private int evaluate(Board board, boolean isMaximizingPlayer) {
        int scoreMax = 0;
        int scoreMin =0;
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
                // Avancement des pions
                scoreMax += pawn.isWhite() ? pos.getRow()*3 : (7 - pos.getRow());

                // Protection des pions
//                if (isProtectedSquare(board, pos, true)) {
//                    scoreMax += 1;
//                }

                // Contrôle des cases centrales
//                if (pos.getCol() == 0 || pos.getCol() == 7 || pos.getCol() == 3 || pos.getCol() == 4) {
//                    scoreMax += 2;
//                }

            }
        }
        for (Position pos : getAllPawnsPositions(board, false)) {
            Pawn pawn = board.getPawnAt(pos);
            if (pawn != null) {
                // Avancement des pions
                scoreMin += pawn.isWhite() ? pos.getRow() : (7 - pos.getRow())*3;

                // Protection des pions
//                if (isProtectedSquare(board, pos, false)) {
//                    scoreMin += 1;
//                }

//                if (pos.getCol() == 0 || pos.getCol() == 7 || pos.getCol() == 3 || pos.getCol() == 4) {
//                    scoreMax += 2;
//                }
            }
        }
        return scoreMax - scoreMin;
    }

    private boolean isUnstoppable(Board board, Position position) {
        int row = position.getRow();
        Pawn pawn = board.getPawnAt(position);
        if (pawn == null) return false;

        boolean isWhite = pawn.isWhite();
        int direction = isWhite ? 1 : -1;

        // Check if the pawn is in the opponent's half
        if ((isWhite && row > 4) || (!isWhite && row < 3)) {
            return false;
        }

        return isUnstoppablePath(board, position, direction);
    }

    private boolean isUnstoppablePath(Board board, Position position, int direction) {
        int row = position.getRow();
        int col = position.getCol();

        // If the pawn has reached the last row
        if ((direction == -1 && row == 0) || (direction == 1 && row == 7)) {
            return true;
        }

        while (true) {
            boolean allProtected = true;

            // Check the three squares in front of the current position
            for (int i = -1; i <= 1; i++) {
                int newRow = row + direction;
                int newCol = col + i;
                if (isValidPosition(newRow, newCol)) {
                    if (!isProtectedSquare(board, new Position(newRow, newCol), !board.getPawnAt(position).isWhite())) {
                        // Found an unprotected square, move the position and continue the check
                        row = newRow;
                        col = newCol;
                        allProtected = false;
                        break;
                    }
                }
            }

            // If all three squares are protected, the path is blocked
            if (allProtected) {
                return false;
            }

            // If the pawn has reached the last row
            if ((direction == -1 && row == 0) || (direction == 1 && row == 7)) {
                return true;
            }
        }
    }

    private boolean isProtectedSquare(Board board, Position position, boolean isWhite) {
        int row = position.getRow();
        int col = position.getCol();

        int direction = isWhite ? -1 : 1;

        for (int i = -1; i <= 1; i++) {
            if(i!=0) {
                int newRow = row + direction;
                int newCol = col + i;
                if (isValidPosition(newRow, newCol)) {
                    Pawn pawn = board.getPawnAt(new Position(newRow, newCol));
                    if (pawn != null && pawn.isWhite() == isWhite) {
                        return true;
                    }
                }
            }
        }

        return false;
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
}
