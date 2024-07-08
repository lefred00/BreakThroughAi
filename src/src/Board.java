import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Board {
    private static final int SIZE = 8;
    private Pawn[][] board;
    private static long[][][] zobristTable = new long[SIZE][SIZE][2]; // 2 for white and black pawns
    private long zobristHash;

    public Board() {
        board = new Pawn[SIZE][SIZE];

        setupInitialPosition();
        //setupPosition();

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

    private void setupPosition() {

        board[0][2] = new Pawn(new Position(0, 2), true);
        board[0][3] = new Pawn(new Position(0, 3), true);
        board[1][1] = new Pawn(new Position(1, 1), true);
        board[1][4] = new Pawn(new Position(1, 4), true);
        board[1][5] = new Pawn(new Position(1, 5), true);
        board[2][6] = new Pawn(new Position(2, 6), true);
        board[2][1] = new Pawn(new Position(2, 1), true);
        board[2][3] = new Pawn(new Position(2, 3), true);
        board[2][5] = new Pawn(new Position(2, 5), true);
        board[2][7] = new Pawn(new Position(2, 7), true);
        board[3][6] = new Pawn(new Position(3, 6), true);
        board[4][3] = new Pawn(new Position(4, 3), true);

        // Placer les pions noirs

        board[7][1] = new Pawn(new Position(7, 1), false);
        board[6][0] = new Pawn(new Position(6, 0), false);
        board[6][1] = new Pawn(new Position(6, 1), false);
        board[6][4] = new Pawn(new Position(6, 4), false);
        board[5][6] = new Pawn(new Position(5, 6), false);
        board[5][0] = new Pawn(new Position(5, 0), false);
        board[5][1] = new Pawn(new Position(5, 1), false);
        board[5][3] = new Pawn(new Position(5, 3), false);
        board[4][7] = new Pawn(new Position(4, 7), false);
        board[4][0] = new Pawn(new Position(4, 0), false);
        board[2][4] = new Pawn(new Position(2, 4), false);
    }


    public Pawn getPawnAt(Position position) {
        return board[position.getRow()][position.getCol()];
    }

    public void movePawn(Position from, Position to) {
        Pawn pawn = getPawnAt(from);
        if (pawn != null) {
            board[to.getRow()][to.getCol()] = pawn;
            board[from.getRow()][from.getCol()] = null;
            pawn.setPosition(to);
        }
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
        //Collections.shuffle(moves);

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
        this.zobristHash = other.zobristHash;
    }

    // Method to print the board
    public void printBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Pawn pawn = board[row][col];
                if (pawn == null) {
                    System.out.print(" ");
                } else if (pawn.isWhite()) {
                    System.out.print("w");
                } else {
                    System.out.print("b");
                }
                System.out.print(" ");
            }
            System.out.println();
        }
    }
}
