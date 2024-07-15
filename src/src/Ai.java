import java.util.*;

public class Ai {
    private static final int MAX_DEPTH = 5;
    private static final int WIN_SCORE = 100000;
    private static final int LOSE_SCORE = -100000;
    //public static long startTime;
    public static long TIME_LIMIT = 0; // Limite de temps en millisecondes (1 seconde)

    public int minimax(Board board, int depth, boolean isMaximizingPlayer, int alpha, int beta, int correction) {
        //long startTime = System.nanoTime();
        // long timeLimitNanos = timeLimit * 1_000_000; // Convertir la limite de temps en nanosecondes
        if (isGameOver(board)) {
            return isMaximizingPlayer ? LOSE_SCORE * 2 : WIN_SCORE * 2;
        }

        if (depth == MAX_DEPTH) {
            return evaluate(board, isMaximizingPlayer) + correction;
        }
        int nbBranches = Main.nbBranches(isMaximizingPlayer);
        List<Position> allPawnsPositions = Main.getAllPawnsPositions(board, isMaximizingPlayer);

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
        if (isGameOverIn1(board, isMaximizingPlayer, true)) {
            return isMaximizingPlayer ? WIN_SCORE : LOSE_SCORE;
        }
        if (isGameOverIn1(board, !isMaximizingPlayer, false)) {
            return !isMaximizingPlayer ? WIN_SCORE : LOSE_SCORE;
        }
        if (isGameOverIn2(board, isMaximizingPlayer, true)) {
            return isMaximizingPlayer ? WIN_SCORE : LOSE_SCORE;
        }
        if (isGameOverIn2(board, !isMaximizingPlayer, false)) {
            return !isMaximizingPlayer ? WIN_SCORE : LOSE_SCORE;
        }
        if (isGameOverIn3(board, isMaximizingPlayer, true)) {
            return isMaximizingPlayer ? WIN_SCORE : LOSE_SCORE;
        }
//        if (isGameOverIn3(board, !isMaximizingPlayer, false)) {
//            return isMaximizingPlayer ? WIN_SCORE : LOSE_SCORE;
//        }


        for (Position pos : Main.getAllPawnsPositions(board, true)) {
            Pawn pawn = board.getPawnAt(pos);
            if (pawn != null) {
//                if(unstopable(board,pos,true))
//                    whiteScore += 100 * pos.getRow();
                nbWhite++;
                if (isProtectedSquare(board, pos, true) < isProtectedSquare(board, pos, false)) {
                    if (!isMaximizingPlayer)
                        whiteScore -= 1000;
                }
                if (sidesProtected(board, pos, true))
                    whiteScore += 50;
                //whiteScore += pos.getRow(); // Plus un pion est proche de la fin, plus sa valeur est élevée
                whiteScore += (pos.getRow() * isProtectedSquare(board, pos, true)) * 5; // Bonus pour les pions protégés
            }
        }

        for (Position pos : Main.getAllPawnsPositions(board, false)) {
            Pawn pawn = board.getPawnAt(pos);
            if (pawn != null) {
//                if(unstopable(board,pos,false))
//                    blackScore += 100 * (7 - pos.getRow());
                nbBlack++;
                if (isProtectedSquare(board, pos, false) < isProtectedSquare(board, pos, true)) {
                    if (isMaximizingPlayer)
                        blackScore -= 1000;
                }
                if (sidesProtected(board, pos, false))
                    blackScore += 50;
                //blackScore += (7 - pos.getRow()); // Plus un pion est proche de la fin, plus sa valeur est élevée
                blackScore += ((7 - pos.getRow()) * isProtectedSquare(board, pos, false)) * 5; // Bonus pour les pions protégés
            }
        }
        return (whiteScore - blackScore) + (nbWhite - nbBlack) * 1000;
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
        return !Main.getAllPawnsInRow(board, true, 7).isEmpty() || !Main.getAllPawnsInRow(board, false, 0).isEmpty();
    }

    private boolean isGameOverIn3(Board board, boolean forWhite, boolean isHisTurn) {
        for (Position pos : Main.getAllPawnsPositions(board, forWhite)) {
            Pawn pawn = board.getPawnAt(pos);
            if (pawn != null) {
                int direction = forWhite ? 1 : -1;
                if (forWhite && pos.getRow() == 4 || !forWhite && pos.getRow() == 3) {
                    Position f = pos.getForwardPosition(direction);
                    Position l = pos.getForwardLeftPosition(direction);
                    Position r = pos.getForwardRightPosition(direction);
                    boolean fPos = !(isProtectedSquare(board, f, !forWhite) >= isProtectedSquare(board, f, forWhite));
                    boolean lPos = !(isProtectedSquare(board, l, !forWhite) >= isProtectedSquare(board, l, forWhite));
                    boolean rPos = !(isProtectedSquare(board, r, !forWhite) >= isProtectedSquare(board, r, forWhite));

                    if (board.getPawnAt(f) == null) {
                        if (fPos || lPos || rPos) {
                            if (fPos && isValidPosition(f.getRow(), f.getCol()))
                                return isWinningPawn(board, f, forWhite, isHisTurn);
                            if (lPos && isValidPosition(l.getRow(), l.getCol()))
                                return isWinningPawn(board, l, forWhite, isHisTurn);
                            if (rPos && isValidPosition(r.getRow(), r.getCol()))
                                return isWinningPawn(board, r, forWhite, isHisTurn);
                        }

                    } else {
                        if (lPos || rPos) {
                            if (lPos && isValidPosition(l.getRow(), l.getCol()))
                                return isWinningPawn(board, l, forWhite, isHisTurn);
                            if (rPos && isValidPosition(r.getRow(), r.getCol()))
                                return isWinningPawn(board, r, forWhite, isHisTurn);
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isGameOverIn2(Board board, boolean forWhite, boolean isHisTurn) {

        if(forWhite) {
            for (Position pos : Main.getAllPawnsInRow(board, true, 5)) {
                if (isWinningPawn(board, pos, true, isHisTurn)) {
                    return true;
                }
            }
        }
        else{
            for (Position pos : Main.getAllPawnsInRow(board, false, 2)) {
                if (isWinningPawn(board, pos, false, isHisTurn)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isGameOverIn1(Board board, boolean forWhite, boolean isHisTurn) {
        if(forWhite) {
            for (Position pos : Main.getAllPawnsInRow(board, true, 6)) {
                if (isWinningPawn(board, pos, true, isHisTurn)) {
                    return true;
                }
            }
        }
        else{
            for (Position pos : Main.getAllPawnsInRow(board, false, 1)) {
                if (isWinningPawn(board, pos, false, isHisTurn)) {
                    return true;
                }
            }
        }


        return false;
    }


    private boolean isWinningPawn(Board board, Position position, boolean isWhite, boolean isHisTurn) {
        int direction = isWhite ? 1 : -1;
        if (isWhite && position.getRow() == 7 || !isWhite && position.getRow() == 0) {
            return true;
        }
        if (!isHisTurn && isProtectedSquare(board, position, !isWhite) > isProtectedSquare(board, position, isWhite)) {
            return false;
        }
        if (isWhite && position.getRow() == 6 || !isWhite && position.getRow() == 1) {
            return true;
        }
        Position f = position.getForwardPosition(direction);
        Position l = position.getForwardLeftPosition(direction);
        Position r = position.getForwardRightPosition(direction);
        boolean fPos = !(isProtectedSquare(board, f, !isWhite) >= isProtectedSquare(board, f, isWhite));
        boolean lPos = !(isProtectedSquare(board, l, !isWhite) >= isProtectedSquare(board, l, isWhite));
        boolean rPos = !(isProtectedSquare(board, r, !isWhite) >= isProtectedSquare(board, r, isWhite));
        if (!isHisTurn) {
            if (isProtectedSquare(board, f, isWhite) == 2) {
                int nbDeff = 2;
                Position deffenderLeft = deffender(board, f, true, isWhite);
                Position deffenderRight = deffender(board, f, false, isWhite);
                if (canBeCaptured(board, deffenderRight, !isWhite))
                    nbDeff--;
                if (canBeCaptured(board, deffenderLeft, !isWhite))
                    nbDeff--;
                fPos = nbDeff > isProtectedSquare(board, f, !isWhite);
            }
            if (isProtectedSquare(board, l, isWhite) == 2) {
                int nbDeff = 2;
                Position deffenderLeft = deffender(board, l, true, isWhite);
                Position deffenderRight = deffender(board, l, false, isWhite);
                if (canBeCaptured(board, deffenderRight, !isWhite))
                    nbDeff--;
                if (canBeCaptured(board, deffenderLeft, !isWhite))
                    nbDeff--;
                lPos = nbDeff > isProtectedSquare(board, l, !isWhite);
            }
            if (isProtectedSquare(board, r, isWhite) == 2) {
                int nbDeff = 2;
                Position deffenderLeft = deffender(board, r, true, isWhite);
                Position deffenderRight = deffender(board, r, false, isWhite);
                if (canBeCaptured(board, deffenderRight, !isWhite))
                    nbDeff--;
                if (canBeCaptured(board, deffenderLeft, !isWhite))
                    nbDeff--;
                rPos = nbDeff > isProtectedSquare(board, r, !isWhite);
            }

        }
        if (board.getPawnAt(f) == null) {
            return fPos || lPos || rPos;
        } else {
            return lPos || rPos;
        }
    }

    private Position deffender(Board board, Position posTodeffend, boolean leftDeffender, boolean isWhite) {
        int direction = isWhite ? -1 : 1;
        if (leftDeffender) {
            return new Position(posTodeffend.getRow() + direction, posTodeffend.getCol() - 1);
        } else {
            return new Position(posTodeffend.getRow() + direction, posTodeffend.getCol() + 1);
        }
    }

    private boolean unstopable(Board board, Position position, boolean isWhite) {
        int col = position.getCol();
        int direction = isWhite ? 1 : -1;
        int endRow = isWhite ? 7 : 0;
        int row = position.getRow() + direction * 2;
        while (row != endRow + direction) {
            if (isValidPosition(row, col - 1) && board.getPawnAt(new Position(row, col - 1)) != null && board.getPawnAt(new Position(row, col - 1)).isWhite() == !isWhite) {
                return false;
            }
            if (isValidPosition(row, col) && board.getPawnAt(new Position(row, col)) != null && board.getPawnAt(new Position(row, col)).isWhite() == !isWhite) {
                return false;
            }

            if (isValidPosition(row, col + 1) && board.getPawnAt(new Position(row, col + 1)) != null && board.getPawnAt(new Position(row, col + 1)).isWhite() == !isWhite) {
                return false;
            }
            row = row + direction;
        }
        return true;
    }

    private boolean sidesProtected(Board board, Position position, boolean isWhite) {
        int col = position.getCol();
        int row = position.getRow();

        return (isValidPosition(row, col - 1) && board.getPawnAt(new Position(row, col - 1)) == null && isProtectedSquare(board, new Position(row, col - 1), isWhite) > 0)
                &&
                (isValidPosition(row, col + 1) && board.getPawnAt(new Position(row, col + 1)) == null && isProtectedSquare(board, new Position(row, col + 1), isWhite) > 0);
    }


}

