import java.util.ArrayList;
import java.util.List;

public class Board {
    private static final int SIZE = 8;
    private Pawn[][] board;

    public Board() {
        board = new Pawn[SIZE][SIZE];
        setupInitialPosition();
    }

    private void setupInitialPosition() {
        // Placer les pions blancs
        for (int col = 0; col < SIZE; col++) {
            board[0][col] = new Pawn(new Position(0, col), true);
            board[1][col] = new Pawn(new Position(1, col), true);
        }

        // Placer les pions noirs
        for (int col = 0; col < SIZE; col++) {
            board[6][col] = new Pawn(new Position(6, col), false);
            board[7][col] = new Pawn(new Position(7, col), false);
        }
    }

    public Pawn getPawnAt(Position position) {
        return board[position.getRow()][position.getCol()];
    }

    public void movePawn(Position from, Position to) {
        Pawn pawn = getPawnAt(from);
        board[to.getRow()][to.getCol()] = pawn;
        board[from.getRow()][from.getCol()] = null;
        pawn.setPosition(to);
    }

    public List<Position> getAvailableMoves(Position position) {
        List<Position> moves = new ArrayList<>();
        Pawn pawn = getPawnAt(position);
        if (pawn == null) return moves;

        int direction = pawn.isWhite() ? 1 : -1;
        int row = position.getRow();
        int col = position.getCol();

        // Move forward
        if (isValidMove(row + direction, col) && board[row + direction][col] == null) {
            moves.add(new Position(row + direction, col));
        }

        // Move diagonally
        if (isValidMove(row + direction, col - 1) && (board[row + direction][col - 1] == null || isOpponentPiece(row + direction, col - 1, pawn.isWhite()))) {
            moves.add(new Position(row + direction, col - 1));
        }
        if (isValidMove(row + direction, col + 1) && (board[row + direction][col + 1] == null || isOpponentPiece(row + direction, col + 1, pawn.isWhite()))) {
            moves.add(new Position(row + direction, col + 1));
        }

        return moves;
    }

    private boolean isValidMove(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    private boolean isOpponentPiece(int row, int col, boolean isWhite) {
        Pawn pawn = board[row][col];
        return pawn != null && pawn.isWhite() != isWhite;
    }

    // Constructor for deep copy
    public Board(Board other) {
        this.board = new Pawn[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Pawn pawn = other.board[row][col];
                if (pawn != null) {
                    this.board[row][col] = new Pawn(new Position(pawn.getPosition().getRow(), pawn.getPosition().getCol()), pawn.isWhite());
                }
            }
        }
    }
}
