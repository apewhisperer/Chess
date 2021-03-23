package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;

public class MainWindow extends JFrame implements MouseListener {

    static final int RESET = 15, MARGIN = 100, SIZE = 8, SQUARE_SIDE = 50, CHESSBOARD_SIDE = SIZE * SQUARE_SIDE;
    static int flip = 0, clickCount = 0, firstColumn = RESET, firstRow = RESET, secondColumn = RESET, secondRow = RESET, firstColumnClicked = RESET, firstRowClicked = RESET,
            currentColumn = RESET, currentRow = RESET, prevColumnBeg = RESET, prevRowBeg = RESET, prevColumnEnd = RESET, prevRowEnd = RESET;
    static boolean blackTurn, labelsOn = true, piecesOn = true;

    public MainWindow() {
        this.setTitle("Chess");
        this.setSize(617, 638);
        this.setLayout(null);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setIconImage(Chess.iconList[0]);
        getContentPane().addMouseListener(this);
    }

    public static boolean isGameOver() {

        //checking all possible moves and checkmate/stalemate


        ArrayList<Integer> piecesLeft = new ArrayList<>();

        int counter = 0;

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (Chess.chessboard[i][j] == 0) {
                    counter++;
                } else {
                    piecesLeft.add(Chess.chessboard[i][j]);
                }
            }
        }

        switch (counter) {
            case 60:
                int whiteColumn = SIZE;
                int whiteRow = SIZE;
                int blackColumn = SIZE;
                int blackRow = SIZE;

                if (piecesLeft.contains(3) && piecesLeft.contains(4)) {
                    for (int i = 0; i < SIZE; i++) {
                        for (int j = 0; j < SIZE; j++) {
                            if (Chess.chessboard[i][j] == 3) {
                                whiteColumn = i;
                                whiteRow = j;
                            }
                            if (Chess.chessboard[i][j] == 4) {
                                blackColumn = i;
                                blackRow = j;
                            }
                        }
                    }
                    int whiteTotal = whiteColumn + whiteRow;
                    int blackTotal = blackColumn + blackRow;

                    if ((whiteTotal % 2 != 0 && blackTotal % 2 == 0) || (blackTotal % 2 != 0 && whiteTotal % 2 == 0)) {
                        Chess.gameOver = 4;
                        System.out.println("draw due to insufficient material");
                        return true;
                    }
                }
                break;
            case 61:
                if (piecesLeft.contains(3) || piecesLeft.contains(4) || piecesLeft.contains(5) || piecesLeft.contains(6)) {
                    Chess.gameOver = 4;
                    System.out.println("draw due to insufficient material");
                    return true;
                }
                break;
            case 62:
                Chess.gameOver = 4;
                System.out.println("draw due to insufficient material");
                return true;
        }

        if (Chess.white75MoveRule == 75 || Chess.black75MoveRule == 75) {
            Chess.gameOver = 4;
            System.out.println("draw due to seventy-five-move-rule");
            return true;
        }

        int repetition = 0;

        for (int z = 0; z < Chess.moveCount - 1; z++) {
            if (Arrays.deepEquals(Chess.movesList[z], Chess.chessboard)) {
                Chess.gameOver = 4;
                repetition++;
            }
        }

        if (repetition == 5) {
            System.out.println("draw due to repetition");
            return true;
        }

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                for (int k = 0; k < SIZE; k++) {
                    for (int l = 0; l < SIZE; l++) {
                        if (Chess.isLegal(i, j, k, l)) {
                            Chess.promotionMenu.setVisible(false);
                            System.out.println("game can continue");
                            return false;
                        }
                    }
                }
            }
        }
        if (Chess.check) {
            System.out.println("checkmate");
            Chess.checkMate = true;
            if (blackTurn) {
                Chess.gameOver = 1;
            } else {
                Chess.gameOver = 2;
            }
        } else {
            System.out.println("stalemate");
            Chess.gameOver = 3;
        }
        return true;
    }

    public void paintEndScreen() {

        //painting endgame screen when the outcome is checkmate, stalemate or draw

        getContentPane().removeAll();

        JLayeredPane mainPanel = new JLayeredPane();
        mainPanel.setBounds(0, 0, 600, 600);
        mainPanel.setLayout(null);
        Chess.mainWindow.add(mainPanel);

        JPanel outerPanel = new JPanel();
        outerPanel.setBounds(0, 0, 600, 600);
        outerPanel.setBackground(new Color(30, 30, 30));
        outerPanel.setLayout(null);
        mainPanel.add(outerPanel, JLayeredPane.DEFAULT_LAYER);

        JPanel innerPanel = new JPanel();
        innerPanel.setBounds(100, 100, 400, 400);
        innerPanel.setBackground(new Color(225, 225, 225));
        innerPanel.setLayout(null);
        mainPanel.add(innerPanel, JLayeredPane.PALETTE_LAYER);

        JLabel winMessage = new JLabel("Game Over");
        winMessage.setBounds(120, 120, 500, 100);
        winMessage.setFont(Chess.thickFont.deriveFont(75f));
        mainPanel.add(winMessage, JLayeredPane.MODAL_LAYER);

        if (Chess.gameOver == 1) {

            JLabel whitePawnIcon = new JLabel();
            whitePawnIcon.setBounds(225, 215, 200, 200);
            whitePawnIcon.setIcon(new ImageIcon(Chess.iconList[23]));
            mainPanel.add(whitePawnIcon, JLayeredPane.MODAL_LAYER);

            JLabel whiteWinLabel = new JLabel("WHITE WINS !");
            whiteWinLabel.setBounds(175, 425, 300, 50);
            whiteWinLabel.setFont(Chess.pixelFont.deriveFont(30f));
            mainPanel.add(whiteWinLabel, JLayeredPane.MODAL_LAYER);

        } else if (Chess.gameOver == 2) {

            JLabel blackPawnIcon = new JLabel();
            blackPawnIcon.setBounds(225, 215, 200, 200);
            blackPawnIcon.setIcon(new ImageIcon(Chess.iconList[24]));
            mainPanel.add(blackPawnIcon, JLayeredPane.MODAL_LAYER);

            JLabel blackWinLabel = new JLabel("BLACK WINS !");
            blackWinLabel.setBounds(175, 425, 300, 50);
            blackWinLabel.setFont(Chess.pixelFont.deriveFont(30f));
            mainPanel.add(blackWinLabel, JLayeredPane.MODAL_LAYER);

        } else if (Chess.gameOver == 3) {

            JLabel whitePawnIcon = new JLabel();
            whitePawnIcon.setBounds(130, 215, 200, 200);
            whitePawnIcon.setIcon(new ImageIcon(Chess.iconList[23]));
            mainPanel.add(whitePawnIcon, JLayeredPane.MODAL_LAYER);

            JLabel blackPawnIcon = new JLabel();
            blackPawnIcon.setBounds(305, 215, 200, 200);
            blackPawnIcon.setIcon(new ImageIcon(Chess.iconList[24]));
            mainPanel.add(blackPawnIcon, JLayeredPane.MODAL_LAYER);

            JLabel stalemateLabel = new JLabel("STALEMATE");
            stalemateLabel.setBounds(189, 425, 300, 50);
            stalemateLabel.setFont(Chess.pixelFont.deriveFont(30f));
            mainPanel.add(stalemateLabel, JLayeredPane.MODAL_LAYER);

        } else if (Chess.gameOver == 4) {

            JLabel whitePawnIcon = new JLabel();
            whitePawnIcon.setBounds(130, 215, 200, 200);
            whitePawnIcon.setIcon(new ImageIcon(Chess.iconList[23]));
            mainPanel.add(whitePawnIcon, JLayeredPane.MODAL_LAYER);

            JLabel blackPawnIcon = new JLabel();
            blackPawnIcon.setBounds(305, 215, 200, 200);
            blackPawnIcon.setIcon(new ImageIcon(Chess.iconList[24]));
            mainPanel.add(blackPawnIcon, JLayeredPane.MODAL_LAYER);

            JLabel drawLabel = new JLabel("DRAW");
            drawLabel.setBounds(250, 425, 300, 50);
            drawLabel.setFont(Chess.pixelFont.deriveFont(30f));
            mainPanel.add(drawLabel, JLayeredPane.MODAL_LAYER);
        }
        repaint();
        setVisible(true);
    }

    public void paintChessboard() {

        //painting window with current chessboard using a 8x8 matrix

        getContentPane().removeAll();

        if (piecesOn) {
            capturedPieces();
        }

        JLayeredPane mainPanel = new JLayeredPane();
        mainPanel.setBounds(0, 0, 600, 600);
        mainPanel.setLayout(null);
        Chess.mainWindow.add(mainPanel);

        JPanel panel = new JPanel();
        panel.setBounds(0, 0, 600, 600);
        panel.setBackground(new Color(30, 30, 30));
        panel.setLayout(null);
        mainPanel.add(panel, JLayeredPane.DEFAULT_LAYER);

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {

                JPanel square = addSquare(i, j);
                mainPanel.add(square, JLayeredPane.PALETTE_LAYER);

                JLabel piece = addPiece(i, j);
                mainPanel.add(piece, JLayeredPane.MODAL_LAYER);

                if (i == currentColumn && j == currentRow) {
                    if ((i + j) % 2 == 0) {
                        square.setBackground(new Color(135, 206, 250));
                    } else {
                        square.setBackground(new Color(30, 144, 255));
                    }
                    continue;
                }

                if ((i == Math.abs(flip - prevColumnBeg) && j == Math.abs(flip - prevRowBeg)) || (i == Math.abs(flip - prevColumnEnd) && j == Math.abs(flip - prevRowEnd))) {
                    if ((i + j) % 2 == 0) {
                        square.setBackground(new Color(135, 206, 250));
                    } else {
                        square.setBackground(new Color(30, 144, 255));
                    }
                    continue;
                }

                if ((i + j) % 2 == 0) {
                    square.setBackground(Color.WHITE);
                } else {
                    square.setBackground(new Color(150, 180, 180));
                }
            }
        }

        if (labelsOn) {
            char[] charsList = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};

            for (int i = 0; i < SIZE; i++) {
                JLabel charLabel = addLabel();
                charLabel.setBounds(122 + Math.abs(flip - i) * 50, 510, 15, 15);
                charLabel.setText(String.valueOf(charsList[i]));
                charLabel.setHorizontalTextPosition(JLabel.CENTER);
                charLabel.setVerticalTextPosition(JLabel.CENTER);
                mainPanel.add(charLabel, JLayeredPane.PALETTE_LAYER);
            }

            for (int i = 0; i < SIZE; i++) {
                JLabel numberLabel = addLabel();
                numberLabel.setBounds(75, 467 - Math.abs(flip - i) * 50, 15, 15);
                numberLabel.setText(String.valueOf(i + 1));
                numberLabel.setHorizontalTextPosition(JLabel.CENTER);
                numberLabel.setVerticalTextPosition(JLabel.CENTER);
                mainPanel.add(numberLabel, JLayeredPane.PALETTE_LAYER);
            }
        }

        InputMap[] inputMaps = new InputMap[]{
                mainPanel.getInputMap(JComponent.WHEN_FOCUSED),
                mainPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT),
                mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW),
        };
        for (InputMap i : inputMaps) {
            i.put(KeyStroke.getKeyStroke("control F"), "flip");
            i.put(KeyStroke.getKeyStroke("control Z"), "undo");
            i.put(KeyStroke.getKeyStroke("control shift Z"), "redo");
            i.put(KeyStroke.getKeyStroke("control L"), "labels");
            i.put(KeyStroke.getKeyStroke("control P"), "pieces");
            i.put(KeyStroke.getKeyStroke("control A"), "ui");
            i.put(KeyStroke.getKeyStroke("control Q"), "quit");
        }
        mainPanel.getActionMap().put("flip", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("flip");
                if (flip == 0) {
                    flip = 7;
                } else {
                    flip = 0;
                }
                clickCount = 0;
                currentColumn = RESET;
                currentRow = RESET;
                paintChessboard();
            }
        });
        mainPanel.getActionMap().put("undo", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("undo");
                Chess.promotionMenu.setVisible(false);
                if (Chess.moveCount > 0) {
                    for (int k = 0; k < SIZE; k++) {
                        System.arraycopy(Chess.movesList[Chess.moveCount - 1][k], 0, Chess.chessboard[k], 0, SIZE);
                    }
                    blackTurn = !blackTurn;
                    Chess.playSound("/res/sound/piece_move.wav");
                    Chess.moveCount--;
                    if (Chess.moveCount == 0) {
                        Chess.moveCycles--;
                    }
                    clickCount = 0;
                    currentColumn = RESET;
                    currentRow = RESET;
                    prevColumnBeg = RESET;
                    prevRowBeg = RESET;
                    prevColumnEnd = RESET;
                    prevRowEnd = RESET;
                    paintChessboard();
                } else {
                    System.err.println("unable to perform keystroke");
                }
            }
        });
        mainPanel.getActionMap().put("redo", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("redo");
                if (Chess.moveCount + 1 <= Chess.moveMemory) {
                    for (int k = 0; k < SIZE; k++) {
                        System.arraycopy(Chess.movesList[Chess.moveCount + 1][k], 0, Chess.chessboard[k], 0, SIZE);
                    }
                    blackTurn = !blackTurn;
                    Chess.playSound("/res/sound/piece_move.wav");
                    Chess.moveCount++;
                    if (Chess.moveCount == 200) {
                        Chess.moveCycles++;
                    }
                    currentColumn = RESET;
                    currentRow = RESET;
                    paintChessboard();
                } else {
                    System.err.println("unable to perform keystroke");
                }
            }
        });
        mainPanel.getActionMap().put("labels", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("labels");
                labelsOn = !labelsOn;
                paintChessboard();
            }
        });
        mainPanel.getActionMap().put("pieces", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("pieces");
                piecesOn = !piecesOn;
                paintChessboard();
            }
        });
        mainPanel.getActionMap().put("ui", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("ui");
                labelsOn = !labelsOn;
                piecesOn = !piecesOn;
                paintChessboard();
            }
        });
        mainPanel.getActionMap().put("quit", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("quit");
                dispose();
            }
        });
        repaint();
        setVisible(true);
    }

    private void capturedPieces() {

        int whiteX = 107;
        int blackX = 107;

        int whiteY;
        int blackY;

        if (flip == 0) {
            whiteY = 525;
            blackY = 25;
        } else {
            whiteY = 25;
            blackY = 525;
        }
        int whitePawn = 8;
        int whiteBishop = 2;
        int whiteKnight = 2;
        int whiteRook = 2;
        int whiteQueen = 1;

        int blackPawn = 8;
        int blackBishop = 2;
        int blackKnight = 2;
        int blackRook = 2;
        int blackQueen = 1;

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                switch (Chess.chessboard[i][j]) {
                    case 1:
                        whitePawn--;
                        break;
                    case 2:
                        blackPawn--;
                        break;
                    case 3:
                        whiteBishop--;
                        if (whiteBishop < 0) {
                            whitePawn--;
                        }
                        break;
                    case 4:
                        blackBishop--;
                        if (blackBishop < 0) {
                            blackPawn--;
                        }
                        break;
                    case 5:
                        whiteKnight--;
                        if (whiteKnight < 0) {
                            whitePawn--;
                        }
                        break;
                    case 6:
                        blackKnight--;
                        if (blackKnight < 0) {
                            blackPawn--;
                        }
                        break;
                    case 7:
                    case 13:
                        whiteRook--;
                        if (whiteRook < 0) {
                            whitePawn--;
                        }
                        break;
                    case 8:
                    case 14:
                        blackRook--;
                        if (blackRook < 0) {
                            blackPawn--;
                        }
                        break;
                    case 9:
                        whiteQueen--;
                        if (whiteQueen < 0) {
                            whitePawn--;
                        }
                        break;
                    case 10:
                        blackQueen--;
                        if (blackQueen < 0) {
                            blackPawn--;
                        }
                }
            }
        }
        for (int i = 0; i < whitePawn; i++) {
            JLabel whitePawnLabel = new JLabel();
            whitePawnLabel.setIcon(new ImageIcon(Chess.iconList[13]));
            whitePawnLabel.setBounds(whiteX, whiteY, 50, 50);
            Chess.mainWindow.add(whitePawnLabel);

            whiteX += 10;
        }
        if (whitePawn > 0) {
            whiteX += 19;
        }
        for (int i = 0; i < whiteBishop; i++) {
            JLabel whiteBishopLabel = new JLabel();
            whiteBishopLabel.setIcon(new ImageIcon(Chess.iconList[15]));
            whiteBishopLabel.setBounds(whiteX, whiteY, 50, 50);
            Chess.mainWindow.add(whiteBishopLabel);

            whiteX += 10;
        }
        if (whiteBishop > 0) {
            whiteX += 25;
        }
        for (int i = 0; i < whiteKnight; i++) {
            JLabel whiteKnightLabel = new JLabel();
            whiteKnightLabel.setIcon(new ImageIcon(Chess.iconList[17]));
            whiteKnightLabel.setBounds(whiteX, whiteY, 50, 50);
            Chess.mainWindow.add(whiteKnightLabel);

            whiteX += 10;
        }
        if (whiteKnight > 0) {
            whiteX += 22;
        }
        for (int i = 0; i < whiteRook; i++) {
            JLabel whiteRookLabel = new JLabel();
            whiteRookLabel.setIcon(new ImageIcon(Chess.iconList[19]));
            whiteRookLabel.setBounds(whiteX, whiteY, 50, 50);
            Chess.mainWindow.add(whiteRookLabel);

            whiteX += 10;
        }
        if (whiteRook > 0) {
            whiteX += 22;
        }
        for (int i = 0; i < whiteQueen; i++) {
            JLabel whiteQueenLabel = new JLabel();
            whiteQueenLabel.setIcon(new ImageIcon(Chess.iconList[21]));
            whiteQueenLabel.setBounds(whiteX, whiteY, 50, 50);
            Chess.mainWindow.add(whiteQueenLabel);

            whiteX += 10;
        }
        if (whiteQueen > 0) {
            whiteX += 27;
        }
        whitePawn = Math.abs(whitePawn - 8);
        whiteBishop = Math.abs(whiteBishop - 2);
        whiteKnight = Math.abs(whiteKnight - 2);
        whiteRook = Math.abs(whiteRook - 2);
        whiteQueen = Math.abs(whiteQueen - 1);

        int whiteScore = whitePawn + whiteBishop * 3 + whiteKnight * 3 + whiteRook * 5 + whiteQueen * 9;

        for (int i = 0; i < blackPawn; i++) {
            JLabel blackPawnLabel = new JLabel();
            blackPawnLabel.setIcon(new ImageIcon(Chess.iconList[14]));
            blackPawnLabel.setBounds(blackX, blackY, 50, 50);
            Chess.mainWindow.add(blackPawnLabel);

            blackX += 10;
        }
        if (blackPawn > 0) {
            blackX += 19;
        }
        for (int i = 0; i < blackBishop; i++) {
            JLabel blackBishopLabel = new JLabel();
            blackBishopLabel.setIcon(new ImageIcon(Chess.iconList[16]));
            blackBishopLabel.setBounds(blackX, blackY, 50, 50);
            Chess.mainWindow.add(blackBishopLabel);

            blackX += 10;
        }
        if (blackBishop > 0) {
            blackX += 25;
        }
        for (int i = 0; i < blackKnight; i++) {
            JLabel blackKnightLabel = new JLabel();
            blackKnightLabel.setIcon(new ImageIcon(Chess.iconList[18]));
            blackKnightLabel.setBounds(blackX, blackY, 50, 50);
            Chess.mainWindow.add(blackKnightLabel);

            blackX += 10;
        }
        if (blackKnight > 0) {
            blackX += 22;
        }
        for (int i = 0; i < blackRook; i++) {
            JLabel blackRookLabel = new JLabel();
            blackRookLabel.setIcon(new ImageIcon(Chess.iconList[20]));
            blackRookLabel.setBounds(blackX, blackY, 50, 50);
            Chess.mainWindow.add(blackRookLabel);

            blackX += 10;
        }
        if (blackRook > 0) {
            blackX += 22;
        }
        for (int i = 0; i < blackQueen; i++) {
            JLabel blackQueenLabel = new JLabel();
            blackQueenLabel.setIcon(new ImageIcon(Chess.iconList[22]));
            blackQueenLabel.setBounds(blackX, blackY, 50, 50);
            Chess.mainWindow.add(blackQueenLabel);

            blackX += 10;
        }
        if (blackQueen > 0) {
            blackX += 27;
        }
        blackPawn = Math.abs(blackPawn - 8);
        blackBishop = Math.abs(blackBishop - 2);
        blackKnight = Math.abs(blackKnight - 2);
        blackRook = Math.abs(blackRook - 2);
        blackQueen = Math.abs(blackQueen - 1);

        int blackScore = blackPawn + blackBishop * 3 + blackKnight * 3 + blackRook * 5 + blackQueen * 9;

        if (whiteScore > blackScore) {
            JLabel score = new JLabel();
            score.setText("+" + (whiteScore - blackScore));
            score.setBounds(whiteX, whiteY, 50, 50);
            score.setFont(Chess.font.deriveFont(15f));
            score.setForeground(new Color(240, 240, 240));
            Chess.mainWindow.add(score);
        } else if (blackScore > whiteScore) {
            JLabel score = new JLabel();
            score.setText("+" + (blackScore - whiteScore));
            score.setBounds(blackX, blackY, 50, 50);
            score.setFont(Chess.font.deriveFont(15f));
            score.setForeground(Color.BLACK);
            Chess.mainWindow.add(score);
        }
    }

    private JLabel addPiece(int i, int j) {

        //adding piece tiles to the chessboard according to the matrix

        JLabel piece = new JLabel();
        piece.setBounds(100 + i * SQUARE_SIDE, 100 + j * SQUARE_SIDE, SQUARE_SIDE, SQUARE_SIDE);

        switch (Chess.chessboard[Math.abs(flip - i)][Math.abs(flip - j)]) {
            case 0:
                break;
            case 1:
                piece.setIcon(new ImageIcon(Chess.iconList[1]));
                break;
            case 2:
                piece.setIcon(new ImageIcon(Chess.iconList[2]));
                break;
            case 3:
                piece.setIcon(new ImageIcon(Chess.iconList[3]));
                break;
            case 4:
                piece.setIcon(new ImageIcon(Chess.iconList[4]));
                break;
            case 5:
                piece.setIcon(new ImageIcon(Chess.iconList[5]));
                break;
            case 6:
                piece.setIcon(new ImageIcon(Chess.iconList[6]));
                break;
            case 7:
            case 13:
                piece.setIcon(new ImageIcon(Chess.iconList[7]));
                break;
            case 8:
            case 14:
                piece.setIcon(new ImageIcon(Chess.iconList[8]));
                break;
            case 9:
                piece.setIcon(new ImageIcon(Chess.iconList[9]));
                break;
            case 10:
                piece.setIcon(new ImageIcon(Chess.iconList[10]));
                break;
            case 11:
            case 15:
                piece.setIcon(new ImageIcon(Chess.iconList[11]));
                break;
            case 12:
            case 16:
                piece.setIcon(new ImageIcon(Chess.iconList[12]));
        }
        return piece;
    }

    private JPanel addSquare(int i, int j) {

        //adding square tiles to the chessboard according to the matrix

        JPanel square = new JPanel();
        square.setBounds(100 + i * SQUARE_SIDE, 100 + j * SQUARE_SIDE, SQUARE_SIDE, SQUARE_SIDE);

        return square;
    }

    private JLabel addLabel() {

        //adding coordinate labels

        JLabel label = new JLabel();
        label.setFont(Chess.font.deriveFont(10f));
        label.setForeground(new Color(240, 240, 240));

        return label;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //not used
    }

    @Override
    public void mousePressed(MouseEvent e) {

        int x = e.getX() - MARGIN;
        int y = e.getY() - MARGIN;

        if (!(x < 0 || x > CHESSBOARD_SIDE || y < 0 || y > CHESSBOARD_SIDE)) {
            firstColumn = Math.abs(flip - x / SQUARE_SIDE);
            firstRow = Math.abs(flip - y / SQUARE_SIDE);
            currentColumn = Math.abs(flip - firstColumn);
            currentRow = Math.abs(flip - firstRow);
            paintChessboard();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        int x = e.getX() - MARGIN;
        int y = e.getY() - MARGIN;

        if (x < 0 || x > CHESSBOARD_SIDE || y < 0 || y > CHESSBOARD_SIDE) {
            return;
        } else {
            secondColumn = Math.abs(flip - x / SQUARE_SIDE);
            secondRow = Math.abs(flip - y / SQUARE_SIDE);

            if (Chess.isLegal(firstColumn, firstRow, secondColumn, secondRow)) {
                clickCount = 0;
                Chess.move(firstColumn, firstRow, secondColumn, secondRow);
                return;
            }
        }
        int secondColumnClicked = Math.abs(flip - x / SQUARE_SIDE);
        int secondRowClicked = Math.abs(flip - y / SQUARE_SIDE);

        clickCount++;

        if (clickCount == 2) {
            if (Chess.isLegal(firstColumnClicked, firstRowClicked, secondColumnClicked, secondRowClicked)) {

                Chess.move(firstColumnClicked, firstRowClicked, secondColumnClicked, secondRowClicked);
            }
            clickCount = 1;
        }
        firstColumnClicked = secondColumnClicked;
        firstRowClicked = secondRowClicked;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //not used
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //not used
    }
}
