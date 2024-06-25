import java.util.ArrayList;
import java.util.List;

public class Ai {
    private static final int MAX_DEPTH = 3;
    private static final int WIN_SCORE = 10000;
    private static final int LOSE_SCORE = -10000;

    public int minimax(Board board, int depth, boolean isMaximizingPlayer, int alpha, int beta) {
        // Vérifier si un joueur a une pièce inarrêtable
//        for (Position pos : getAllPawnsPositions(board, isMaximizingPlayer)) {
//            if (isUnstoppable(board, pos)) {
//                return isMaximizingPlayer ? WIN_SCORE : LOSE_SCORE;
//            }
//        }

        if (depth == MAX_DEPTH || isGameOver(board)) {
            return evaluate(board, isMaximizingPlayer);
        }

        if (isMaximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Position pos : getAllPawnsPositions(board, true)) {
                for (Position move : board.getAvailableMoves(pos)) {
                    Board newBoard = new Board(board);
                    newBoard.movePawn(pos, move);
                    int eval = minimax(newBoard, depth + 1, false, alpha, beta);
                    maxEval = Math.max(maxEval, eval);
                    alpha = Math.max(alpha, eval);
                    if (beta <= alpha) {
                        break; // Beta cut-off
                    }
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Position pos : getAllPawnsPositions(board, false)) {
                for (Position move : board.getAvailableMoves(pos)) {
                    Board newBoard = new Board(board);
                    newBoard.movePawn(pos, move);
                    int eval = minimax(newBoard, depth + 1, true, alpha, beta);
                    minEval = Math.min(minEval, eval);
                    beta = Math.min(beta, eval);
                    if (beta <= alpha) {
                        break; // Alpha cut-off
                    }
                }
            }
            return minEval;
        }
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

    private int evaluate(Board board, boolean isMaximizingPlayer) {
        int score = 0;
        boolean isWhitePlayer = isMaximizingPlayer;

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

        for (Position pos : getAllPawnsPositions(board, isWhitePlayer)) {
            Pawn pawn = board.getPawnAt(pos);
            if (pawn != null) {
                // Avancement des pions
                score += pawn.isWhite() ? pos.getRow() : (7 - pos.getRow());

                // Protection des pions
                if (isProtected(board, pos)) {
                    score += 5;
                }

                // Contrôle des cases centrales
                if (pos.getCol() == 3 || pos.getCol() == 4) {
                    score += 2;
                }

                // Mobilité
                //score += board.getAvailableMoves(pos).size();
            }
        }
        return score;
    }

    private boolean isUnstoppable(Board board, Position position) {
        int row = position.getRow();
        int col = position.getCol();
        Pawn pawn = board.getPawnAt(position);
        if (pawn == null) return false;

        boolean isWhite = pawn.isWhite();
        int direction = isWhite ? -1 : 1;

        // Check if the pawn is in the opponent's half
        if ((isWhite && row < 4) || (!isWhite && row > 3)) {
            return false;
        }

        // Check if there is an unstoppable path
        for (int i = row + direction; isValidPosition(i, col); i += direction) {
            boolean pathBlocked = false;
            for (int j = col - 1; j <= col + 1; j++) {
                if (isValidPosition(i, j) && board.getPawnAt(new Position(i, j)) != null && board.getPawnAt(new Position(i, j)).isWhite() != isWhite) {
                    pathBlocked = true;
                    break;
                }
            }
            if (!pathBlocked) {
                return true;
            }
        }

        return false;
    }

    private boolean isProtected(Board board, Position position) {
        int row = position.getRow();
        int col = position.getCol();
        Pawn pawn = board.getPawnAt(position);
        if (pawn == null) return false;

        boolean isWhite = pawn.isWhite();
        int direction = isWhite ? -1 : 1;

        return (isValidPosition(row + direction, col - 1) && board.getPawnAt(new Position(row + direction, col - 1)) != null && board.getPawnAt(new Position(row + direction, col - 1)).isWhite() == isWhite) ||
                (isValidPosition(row + direction, col + 1) && board.getPawnAt(new Position(row + direction, col + 1)) != null && board.getPawnAt(new Position(row + direction, col + 1)).isWhite() == isWhite);
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
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
