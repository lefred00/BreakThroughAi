public class Pawn {
    private Position position;
    private boolean isWhite;
    private boolean hasMoved = false;

    public Pawn(Position position, boolean isWhite) {
        this.position = position;
        this.isWhite = isWhite;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
        hasMoved = true;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public boolean isHasMoved() {
        return hasMoved;
    }
}
