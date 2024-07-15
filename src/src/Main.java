import java.sql.SQLOutput;
import java.util.*;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Ai minimax = new Ai();
    private static final Board board = new Board();
    public static long TIME_LIMIT = 15000; // Limite de temps en millisecondes (1 seconde)


    public static void main(String[] args) {
        boolean isWhitePlayer = chooseColor();
        boolean gameOver = false;

//        while (!gameOver) {
//            if (isWhitePlayer) {
//                playerMove(true);
//                gameOver = checkGameOver();
//                if (gameOver) break;
//                aiMove(false);
//            } else {
//                aiMove(true);
//                gameOver = checkGameOver();
//                if (gameOver) break;
//                playerMove(false);
//            }
//            gameOver = checkGameOver();
//        }
        System.out.println("Game Over!");
    }

    private static boolean chooseColor() {
        System.out.println("Choose your color (white/black): ");
        String color = scanner.nextLine().trim().toLowerCase();
        return color.equals("white");
    }

    public static void playerMove(boolean isWhite, String move) {
//        while (true) {
//            System.out.println("Enter your move (e.g., e2 e3): ");
//            String move = scanner.nextLine().trim();
//            String[] parts = move.split(" ");
//            if (parts.length != 2) {
//                System.out.println("Invalid input format. Please use 'e2 e3' format.");
//                continue;
//            }
//
//            Position from = parsePosition(parts[0]);
//            Position to = parsePosition(parts[1]);
//
//            if (from == null || to == null) {
//                System.out.println("Invalid move format. Please use 'e2 e3' format.");
//                continue;
//            }
//
//            if (isValidPlayerMove(from, to, isWhite)) {
//                board.movePawn(from, to);
//                break;
//            } else {
//                System.out.println("Invalid move. Try again.");
//            }
//        }
        String[] parts = move.split(" ");
        Position from = parsePosition(parts[0]);
        Position to = parsePosition(parts[1]);
        board.movePawn(from, to);
    }

    private static Position parsePosition(String pos) {
        if (pos.length() != 2) return null;
        int col = pos.charAt(0) - 'a';
        int row = pos.charAt(1) - '1';
        if (col < 0 || col >= 8 || row < 0 || row >= 8) return null;
        return new Position(row, col);
    }

    private static boolean isValidPlayerMove(Position from, Position to, boolean isWhite) {
        Pawn pawn = board.getPawnAt(from);
        if (pawn == null || pawn.isWhite() != isWhite) return false;
        return board.getAvailableMoves(from).contains(to);
    }

    public static String aiMove(boolean isWhite) {
        //long startTime = System.nanoTime();
        int correction = isWhite ? -1 : 1;
        String bestMove = "";
        int bestEval = isWhite ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        List<Position> allPawnsPositions = getAllPawnsPositions(board, isWhite);

        outerLoop:
        for (Position pos : allPawnsPositions) {
            List<Position> availableMoves = board.getAvailableMoves(pos);

            for (Position move : availableMoves) {
                Board newBoard = new Board(board); // Assurez-vous de copier correctement le plateau
                newBoard.movePawn(pos, move);
                int eval = minimax.minimax(newBoard, 0, !isWhite, Integer.MIN_VALUE, Integer.MAX_VALUE, correction);
                if (isWhite) {
                    if (eval > bestEval) {
                        bestEval = eval;
                        bestMove = positionToString(pos) + " " + positionToString(move);
                    }
                    if (eval >= 10000 * 2) break outerLoop;
                } else {
                    if (eval < bestEval) {
                        bestEval = eval;
                        bestMove = positionToString(pos) + " " + positionToString(move);
                    }
                    if (eval <= -10000 * 2) break outerLoop;
                }
//                if (System.nanoTime() - startTime > TIME_LIMIT*1_000_000) {
//                    break outerLoop;
//                }
            }
        }
        System.out.println(bestEval);
        //minimax.setTranspositionTable(new HashMap<>());
        // Effectuer le mouvement sur le plateau principal
        playerMove(isWhite, bestMove);
        System.out.println("AI move: " + bestMove);
        return bestMove.toUpperCase();
    }

    public static int nbBranches(boolean isWhite) {
        int nbBranches = 0;
        List<Position> allPawnsPositions = getAllPawnsPositions(board, isWhite);
        for (Position pos : allPawnsPositions) {
            List<Position> availableMoves = board.getAvailableMoves(pos);
            for (Position move : availableMoves) {
                nbBranches++;
            }
        }
        return nbBranches;
    }


    private static String positionToString(Position pos) {
        char col = (char) ('a' + pos.getCol());
        char row = (char) ('1' + pos.getRow());
        return "" + col + row;
    }

    private static boolean checkGameOver() {
        for (int col = 0; col < 8; col++) {
            if (board.getPawnAt(new Position(0, col)) != null && !board.getPawnAt(new Position(0, col)).isWhite()) {
                System.out.println("Black wins!");
                return true;
            }
            if (board.getPawnAt(new Position(7, col)) != null && board.getPawnAt(new Position(7, col)).isWhite()) {
                System.out.println("White wins!");
                return true;
            }
        }
        return false;
    }

    public static List<Position> getAllPawnsPositions(Board board, boolean isWhite) {
        List<Position> positions = new ArrayList<>();

        if (!isWhite) {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    Pawn pawn = board.getPawnAt(new Position(row, col));
                    if (pawn != null && pawn.isWhite() == isWhite) {
                        positions.add(new Position(row, col));
                    }
                }
            }
        } else {
            for (int row = 7; row > -1; row--) {
                for (int col = 7; col > -1; col--) {
                    Pawn pawn = board.getPawnAt(new Position(row, col));
                    if (pawn != null && pawn.isWhite() == isWhite) {
                        positions.add(new Position(row, col));
                    }
                }
            }
        }
        return positions;
    }

    public static List<Position> getAllPawnsInRow(Board board, boolean isWhite, int row) {
        List<Position> positions = new ArrayList<>();

        for (int col = 0; col < 8; col++) {
            Pawn pawn = board.getPawnAt(new Position(row, col));
            if (pawn != null && pawn.isWhite() == isWhite) {
                positions.add(new Position(row, col));
            }
        }

        return positions;
    }
}
