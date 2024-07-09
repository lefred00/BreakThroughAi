import java.util.*;

public class Ai {
    private static final int MAX_DEPTH = 4;
    private static final int WIN_SCORE = 10000;
    private static final int LOSE_SCORE = -10000;
    public long startTime;
    public static final long TIME_LIMIT = 50000; // Limite de temps en millisecondes (1 seconde)

    public int minimax(Board board, int depth, boolean isMaximizingPlayer, int alpha, int beta, int correction) {
        if (System.currentTimeMillis() - startTime > TIME_LIMIT) {
            return evaluate(board, isMaximizingPlayer); // Retourner une évaluation approximative si le temps est écoulé
        }
        if (isGameOver(board)) {
            return isMaximizingPlayer ? LOSE_SCORE*2 : WIN_SCORE*2;
        }
//        if (isGameOverIn1(board, !isMaximizingPlayer)) {
//            return isMaximizingPlayer ? LOSE_SCORE + correction*10 : WIN_SCORE + correction*10;
//        }
//        if (isGameOverIn2(board, !isMaximizingPlayer)) {
//            return isMaximizingPlayer ? LOSE_SCORE + correction*20 : WIN_SCORE + correction*20;
//        }

        if (depth == MAX_DEPTH) {
            return evaluate(board, isMaximizingPlayer) + correction;
        }
        List<Position> allPawnsPositions = getAllPawnsPositions(board, isMaximizingPlayer);

        if (isMaximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            outerLoop:
            for (Position pos : allPawnsPositions) {
                List<Position> availableMoves = board.getAvailableMoves(pos);
                for (Position move : availableMoves) {
                    Board newBoard = new Board(board);
                    newBoard.movePawn(pos, move);

                    int eval = minimax(newBoard, depth + 1, false, alpha, beta, correction);
                    maxEval = Math.max(maxEval, eval);
                    alpha = Math.max(alpha, eval);
                    if (beta <= alpha) {
                        break outerLoop; // Beta cut-off
                    }
                }
            }
            return maxEval + correction;
        } else {
            int minEval = Integer.MAX_VALUE;
            outerLoop:
            for (Position pos : allPawnsPositions) {
                List<Position> availableMoves = board.getAvailableMoves(pos);
                for (Position move : availableMoves) {
                    Board newBoard = new Board(board);
                    newBoard.movePawn(pos, move);
                    int eval = minimax(newBoard, depth + 1, true, alpha, beta, correction);
                    minEval = Math.min(minEval, eval);
                    beta = Math.min(beta, eval);
                    if (beta <= alpha) {
                        break outerLoop; // Alpha cut-off
                    }
                }
            }

            return minEval + correction;
        }
    }

    private int evaluate(Board board, boolean isMaximizingPlayer) {
        int nbBlack = 0;
        int nbWhite = 0;
        int whiteScore = 0;
        int blackScore = 0;
        boolean whiteWin = false;

        if(isGameOverIn1(board, isMaximizingPlayer)) {
            return isMaximizingPlayer ? WIN_SCORE : LOSE_SCORE;
        }
        if(isGameOverIn1(board, !isMaximizingPlayer)) {
            return !isMaximizingPlayer ? WIN_SCORE : LOSE_SCORE;
        }
        if(isGameOverIn2(board, isMaximizingPlayer)) {
            return isMaximizingPlayer ? WIN_SCORE : LOSE_SCORE;
        }
        if(isGameOverIn2(board, !isMaximizingPlayer)) {
            return !isMaximizingPlayer ? WIN_SCORE : LOSE_SCORE;
        }


        for (Position pos : getAllPawnsPositions(board, true)) {
            Pawn pawn = board.getPawnAt(pos);
            if (pawn != null) {
//                if (pos.getRow() > 4 && isWinningPawn(board, pos, true)) {
//                    whiteWin = true;
//                    if (isMaximizingPlayer)
//                        return WIN_SCORE;
//                }
                nbWhite++;
                //whiteScore += pos.getRow(); // Plus un pion est proche de la fin, plus sa valeur est élevée
                whiteScore += ( pos.getRow()*isProtectedSquare(board, pos, true)) * 2; // Bonus pour les pions protégés
            }
        }

        for (Position pos : getAllPawnsPositions(board, false)) {
            Pawn pawn = board.getPawnAt(pos);
            if (pawn != null) {
//                if (pos.getRow() < 3 && isWinningPawn(board, pos, false)) {
//                    return LOSE_SCORE;
//                }
                nbBlack++;
                //blackScore += (7 - pos.getRow()); // Plus un pion est proche de la fin, plus sa valeur est élevée
                blackScore += ((7 - pos.getRow())*isProtectedSquare(board, pos, false)) * 2; // Bonus pour les pions protégés
            }
        }

//        if (whiteWin)
//            return WIN_SCORE;

        return (whiteScore - blackScore) + (nbWhite - nbBlack) * 100;
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
            if (i != 0) {
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

    private boolean isGameOverIn2(Board board, boolean forWhite) {
        for (Position pos : getAllPawnsPositions(board, forWhite)) {
            Pawn pawn = board.getPawnAt(pos);
            if (pawn != null) {
                if (forWhite) {
                    if (pos.getRow() == 5 && isWinningPawn(board, pos, true)) {
                        return true;
                    }
                } else {
                    if (pos.getRow() == 2 && isWinningPawn(board, pos, false)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isGameOverIn1(Board board, boolean forWhite) {
        for (Position pos : getAllPawnsPositions(board, forWhite)) {
            Pawn pawn = board.getPawnAt(pos);
            if (pawn != null) {
                if (forWhite) {
                    if (pos.getRow() == 6 && isWinningPawn(board, pos, true)) {
                        return true;
                    }
                } else {
                    if (pos.getRow() == 1 && isWinningPawn(board, pos, false)) {
                        return true;
                    }
                }
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
        // Collections.shuffle(positions);
        return positions;
    }

    private boolean isWinningPawn(Board board, Position position, boolean isWhite) {
        int direction = isWhite ? 1 : -1;
        if (isWhite && position.getRow() == 7 || !isWhite && position.getRow() == 0) {
            return true;
        }
        if (canBeCaptured(board, position, !isWhite)) {
            return false;
        }
        if (isWhite && position.getRow() == 6 || !isWhite && position.getRow() == 1) {
            return true;
        }
        Position f = position.getForwardPosition(direction);
        Position l = position.getForwardLeftPosition(direction);
        Position r = position.getForwardRightPosition(direction);
        board.getPawnAt(f);
        if (board.getPawnAt(f) == null) {
            return !(isProtectedSquare(board, f, !isWhite) >= isProtectedSquare(board, f, isWhite) && isProtectedSquare(board, l, !isWhite) >= isProtectedSquare(board, l, isWhite) && isProtectedSquare(board, r, !isWhite) >= isProtectedSquare(board, r, isWhite));
        } else {
            return !(isProtectedSquare(board, l, !isWhite) >= isProtectedSquare(board, l, isWhite) && isProtectedSquare(board, r, !isWhite) >= isProtectedSquare(board, r, isWhite));
        }
    }


}

