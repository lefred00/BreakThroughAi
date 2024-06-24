import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Ai minimax = new Ai();
    private static final Board board = new Board();

    public static void main(String[] args) {
        boolean isWhitePlayer = chooseColor();
        boolean gameOver = false;

        while (!gameOver) {
            if (isWhitePlayer) {
                playerMove(true);
                gameOver = checkGameOver();
                if (gameOver) break;
                aiMove(false);
            } else {
                aiMove(true);
                gameOver = checkGameOver();
                if (gameOver) break;
                playerMove(false);
            }
            gameOver = checkGameOver();
        }
        System.out.println("Game Over!");
    }

    private static boolean chooseColor() {
        System.out.println("Choose your color (white/black): ");
        String color = scanner.nextLine().trim().toLowerCase();
        return color.equals("white");
    }

    private static void playerMove(boolean isWhite) {
        while (true) {
            System.out.println("Enter your move (e.g., e2 e3): ");
            String move = scanner.nextLine().trim();
            String[] parts = move.split(" ");
            if (parts.length != 2) {
                System.out.println("Invalid input format. Please use 'e2 e3' format.");
                continue;
            }

            Position from = parsePosition(parts[0]);
            Position to = parsePosition(parts[1]);

            if (from == null || to == null) {
                System.out.println("Invalid move format. Please use 'e2 e3' format.");
                continue;
            }

            if (isValidPlayerMove(from, to, isWhite)) {
                board.movePawn(from, to);
                break;
            } else {
                System.out.println("Invalid move. Try again.");
            }
        }
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

    private static void aiMove(boolean isWhite) {
        String bestMove = "";
        int maxEval = Integer.MIN_VALUE;

        for (Position pos : getAllPawnsPositions(isWhite)) {
            for (Position move : board.getAvailableMoves(pos)) {
                Board newBoard = new Board(board); // Assurez-vous de copier correctement le plateau
                newBoard.movePawn(pos, move);
                int eval = minimax.minimax(newBoard, 0, !isWhite, Integer.MIN_VALUE, Integer.MAX_VALUE);
                if (eval > maxEval) {
                    maxEval = eval;
                    bestMove = positionToString(pos) + " " + positionToString(move);
                }
            }
        }

        String[] parts = bestMove.split(" ");
        Position from = parsePosition(parts[0]);
        Position to = parsePosition(parts[1]);
        board.movePawn(from, to);

        System.out.println("AI move: " + bestMove);
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

    private static List<Position> getAllPawnsPositions(boolean isWhite) {
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
