package main;

public enum Pieces {
    WHITE_PAWN(1),
    BLACK_PAWN(2),
    WHITE_BISHOP(3),
    BLACK_BISHOP(4),
    WHITE_KNIGHT(5),
    BLACK_KNIGHT(6),
    WHITE_ROOK(7),
    BLACK_ROOK(8),
    WHITE_QUEEN(9),
    BLACK_QUEEN(10),
    WHITE_KING(11),
    BLACK_KING(12);

    public final int value;

    Pieces(int value) {
        this.value = value;
    }
}
