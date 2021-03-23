package main;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static main.Pieces.*;

public class Chess {

    static final Dimension DIMENSION = new Dimension(50, 50);
    static float VOLUME = 0.01f;
    static int[][] chessboard = new int[8][8];
    static int[][][] movesList = new int[200][8][8];
    static int[][] enPassantList = new int[200][3];
    static Image[] iconList = new Image[27];
    static boolean check, checkMate, promotionCondition;
    static int promotion = 0, moveMemory = 0, moveCount = 0, moveCycles = 0, white75MoveRule = 0, black75MoveRule = 0, gameOver = 0;
    static Font font, boldFont, pixelFont, thickFont;
    static JPopupMenu promotionMenu = new JPopupMenu();
    static MainWindow mainWindow;

    public static void main(String[] args) {

        loadResources();

        chessboard = prepareChessboard();
        mainWindow = new MainWindow();
        mainWindow.paintChessboard();
    }

    public static boolean isLegal(int firstColumn, int firstRow, int secondColumn, int secondRow) {

        //checking whether the piece is able to move on the chosen square or not

        if (isDiscoveringSelfCheck(firstColumn, firstRow, secondColumn, secondRow)) {
            return false;
        }
        int moveSource = chessboard[firstColumn][firstRow];
        int moveTarget = chessboard[secondColumn][secondRow];
        int firstCoordinate = Math.abs(firstColumn - secondColumn);
        int secondCoordinate = Math.abs(firstRow - secondRow);

        if (moveSource != 0) { //if 1st pick isnt blank space
            if (moveSource == moveTarget) { //if 1st pick = 2nd
                return false;
            } else if (moveTarget != 0 && ((MainWindow.blackTurn && moveTarget % 2 == 0) || (!MainWindow.blackTurn && moveTarget % 2 != 0))) { //if 2nd isnt blank and if 1st and 2nd arent same color
                return false;
            } else if (!MainWindow.blackTurn) {

                switch (moveSource) {

                    case 1: // white pawn
                        if (firstColumn == secondColumn) {
                            if (firstRow == secondRow + 1 && moveTarget == 0) { //single move
                                if (secondRow == 0) { //promotion

                                    promotionMenu.removeAll();

                                    FlowLayout layout = new FlowLayout(FlowLayout.CENTER, 3, 3);
                                    promotionMenu.setLayout(layout);
                                    promotionMenu.setBackground(Color.BLACK);
                                    promotionMenu.setBorderPainted(false);

                                    JButton bishop = new JButton();
                                    bishop.setIcon(new ImageIcon(iconList[15]));
                                    bishop.setPreferredSize(DIMENSION);
                                    bishop.setBackground(Color.WHITE);
                                    bishop.setBorderPainted(false);
                                    bishop.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            promotionCondition = true;
                                            promotion = 3;
                                            move(firstColumn, firstRow, secondColumn, secondRow);
                                            promotionMenu.setVisible(false);
                                        }
                                    });
                                    promotionMenu.add(bishop);

                                    JButton knight = new JButton();
                                    knight.setIcon(new ImageIcon(iconList[17]));
                                    knight.setPreferredSize(DIMENSION);
                                    knight.setBackground(Color.WHITE);
                                    knight.setBorderPainted(false);
                                    knight.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            promotionCondition = true;
                                            promotion = 5;
                                            move(firstColumn, firstRow, secondColumn, secondRow);
                                            promotionMenu.setVisible(false);
                                        }
                                    });
                                    promotionMenu.add(knight);

                                    JButton rook = new JButton();
                                    rook.setIcon(new ImageIcon(iconList[19]));
                                    rook.setPreferredSize(DIMENSION);
                                    rook.setBackground(Color.WHITE);
                                    rook.setBorderPainted(false);
                                    rook.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            promotionCondition = true;
                                            promotion = 13;
                                            move(firstColumn, firstRow, secondColumn, secondRow);
                                            promotionMenu.setVisible(false);
                                        }
                                    });
                                    promotionMenu.add(rook);

                                    JButton queen = new JButton();
                                    queen.setIcon(new ImageIcon(iconList[21]));
                                    queen.setPreferredSize(DIMENSION);
                                    queen.setBackground(Color.WHITE);
                                    queen.setBorderPainted(false);
                                    queen.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            promotionCondition = true;
                                            promotion = 9;
                                            move(firstColumn, firstRow, secondColumn, secondRow);
                                            promotionMenu.setVisible(false);
                                        }
                                    });
                                    promotionMenu.add(queen);

                                    JButton cancel = new JButton();
                                    cancel.setIcon(new ImageIcon(iconList[26]));
                                    cancel.setPreferredSize(new Dimension(25, 50));
                                    cancel.setBackground(Color.WHITE);
                                    cancel.setBorderPainted(false);
                                    cancel.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            promotionMenu.setVisible(false);
                                        }
                                    });
                                    promotionMenu.add(cancel);

                                    promotionMenu.show(mainWindow, 107 + 50 * secondColumn, 69);
                                    return false;
                                }
                                return true;
                            } else if (firstRow == 6 && firstRow == secondRow + 2 && chessboard[secondColumn][secondRow + 1] == 0 && moveTarget == 0) { //double move
                                return true;
                            }
                        } else if (((firstColumn == secondColumn - 1 || firstColumn == secondColumn + 1) && firstRow == secondRow + 1) && moveTarget != 0) { //takes
                            if (secondRow == 0) { //promotion

                                promotionMenu.removeAll();

                                FlowLayout layout = new FlowLayout(FlowLayout.CENTER, 3, 3);
                                promotionMenu.setLayout(layout);
                                promotionMenu.setBackground(Color.BLACK);
                                promotionMenu.setBorderPainted(false);

                                JButton bishop = new JButton();
                                bishop.setIcon(new ImageIcon(iconList[13]));
                                bishop.setPreferredSize(DIMENSION);
                                bishop.setBackground(Color.WHITE);
                                bishop.setBorderPainted(false);
                                bishop.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        promotionCondition = true;
                                        promotion = 3;
                                        move(firstColumn, firstRow, secondColumn, secondRow);
                                        promotionMenu.setVisible(false);
                                    }
                                });
                                promotionMenu.add(bishop);

                                JButton knight = new JButton();
                                knight.setIcon(new ImageIcon(iconList[15]));
                                knight.setPreferredSize(DIMENSION);
                                knight.setBackground(Color.WHITE);
                                knight.setBorderPainted(false);
                                knight.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        promotionCondition = true;
                                        promotion = 5;
                                        move(firstColumn, firstRow, secondColumn, secondRow);
                                        promotionMenu.setVisible(false);
                                    }
                                });
                                promotionMenu.add(knight);

                                JButton rook = new JButton();
                                rook.setIcon(new ImageIcon(iconList[17]));
                                rook.setPreferredSize(DIMENSION);
                                rook.setBackground(Color.WHITE);
                                rook.setBorderPainted(false);
                                rook.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        promotionCondition = true;
                                        promotion = 13;
                                        move(firstColumn, firstRow, secondColumn, secondRow);
                                        promotionMenu.setVisible(false);
                                    }
                                });
                                promotionMenu.add(rook);

                                JButton queen = new JButton();
                                queen.setIcon(new ImageIcon(iconList[19]));
                                queen.setPreferredSize(DIMENSION);
                                queen.setBackground(Color.WHITE);
                                queen.setBorderPainted(false);
                                queen.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        promotionCondition = true;
                                        promotion = 9;
                                        move(firstColumn, firstRow, secondColumn, secondRow);
                                        promotionMenu.setVisible(false);
                                    }
                                });
                                promotionMenu.add(queen);

                                JButton cancel = new JButton();
                                cancel.setIcon(new ImageIcon(iconList[26]));
                                cancel.setPreferredSize(new Dimension(25, 50));
                                cancel.setBackground(Color.WHITE);
                                cancel.setBorderPainted(false);
                                cancel.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        promotionMenu.setVisible(false);
                                    }
                                });
                                promotionMenu.add(cancel);

                                promotionMenu.show(mainWindow, 107 + 50 * secondColumn, 69);
                                return false;
                            }
                            return true;
                        } else if ((((firstColumn == secondColumn - 1 && firstRow == secondRow + 1) && moveTarget == 0) || ((firstColumn == secondColumn + 1 && firstRow == secondRow + 1) && moveTarget == 0)) && enPassantList[moveCount][0] == 1 && secondColumn == enPassantList[moveCount][1] && secondRow == enPassantList[moveCount][2]) { //en passant
                            return true;
                        }
                        break;
                    case 3: //white bishop
                        if (firstCoordinate == secondCoordinate) { //if move is diagonal
                            if (secondColumn < firstColumn && secondRow < firstRow) { //first direction
                                for (int k = firstCoordinate - 1; k > 0; k--) { //range
                                    if (Chess.chessboard[firstColumn - k][firstRow - k] != 0) { //if squares in range are unoccupied
                                        return false;
                                    }
                                }
                            } else if (secondColumn > firstColumn && secondRow < firstRow) {
                                for (int k = firstCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn + k][firstRow - k] != 0) {
                                        return false;
                                    }
                                }
                            } else if (secondColumn < firstColumn && secondRow > firstRow) {
                                for (int k = firstCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn - k][firstRow + k] != 0) {
                                        return false;
                                    }
                                }
                            } else if (secondColumn > firstColumn && secondRow > firstRow) {
                                for (int k = firstCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn + k][firstRow + k] != 0) {
                                        return false;
                                    }
                                }
                            }
                            return true;
                        }
                        break;
                    case 5: //white knight
                        if ((firstCoordinate == 2 && secondCoordinate == 1) || (firstCoordinate == 1 && secondCoordinate == 2)) { //checking the "L" pattern
                            return true;
                        }
                        break;
                    case 7: //white rook
                        if (firstColumn == secondColumn || firstRow == secondRow) { //checking if the desired position is the same firstColumn/firstRow
                            if (secondRow < firstRow) { //first direction
                                for (int k = secondCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn][firstRow - k] != 0) {
                                        return false;
                                    }
                                }
                            } else if (secondColumn > firstColumn) {
                                for (int k = firstCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn + k][firstRow] != 0) {
                                        return false;
                                    }
                                }
                            } else if (secondRow > firstRow) {
                                for (int k = secondCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn][firstRow + k] != 0) {
                                        return false;
                                    }
                                }
                            } else if (secondColumn < firstColumn) {
                                for (int k = firstCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn - k][firstRow] != 0) {
                                        return false;
                                    }
                                }
                            }
                            return true;
                        }
                        break;
                    case 9: //white queen
                        if (firstCoordinate == secondCoordinate) { //diagonal
                            if (secondColumn < firstColumn && secondRow < firstRow) {
                                for (int k = firstCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn - k][firstRow - k] != 0) {
                                        return false;
                                    }
                                }
                            } else if (secondColumn > firstColumn && secondRow < firstRow) {
                                for (int k = firstCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn + k][firstRow - k] != 0) {
                                        return false;
                                    }
                                }
                            } else if (secondColumn < firstColumn && secondRow > firstRow) {
                                for (int k = firstCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn - k][firstRow + k] != 0) {
                                        return false;
                                    }
                                }
                            } else if (secondColumn > firstColumn && secondRow > firstRow) {
                                for (int k = firstCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn + k][firstRow + k] != 0) {
                                        return false;
                                    }
                                }
                            }
                            return true;
                        }
                        if (firstColumn == secondColumn || firstRow == secondRow) { //horizontal/vertical
                            if (secondRow < firstRow) {
                                for (int k = secondCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn][firstRow - k] != 0) {
                                        return false;
                                    }
                                }
                            } else if (secondColumn > firstColumn) {
                                for (int k = firstCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn + k][firstRow] != 0) {
                                        return false;
                                    }
                                }
                            } else if (secondRow > firstRow) {
                                for (int k = secondCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn][firstRow + k] != 0) {
                                        return false;
                                    }
                                }
                            } else if (secondColumn < firstColumn) {
                                for (int k = firstCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn - k][firstRow] != 0) {
                                        return false;
                                    }
                                }
                            }
                            return true;
                        }
                        break;
                    case 11: //white king
                        if (firstCoordinate == 1 || secondCoordinate == 1) { //checking range of 1 square
                            //horizontal/vertical
                            if (firstCoordinate == secondCoordinate) { //diagonal
                                return true;
                            } else return firstColumn == secondColumn || firstRow == secondRow;
                        } else { //castling
                            if (chessboard[7][7] == 7 && firstColumn == 4 && firstRow == 7 && secondColumn == 6 && secondRow == 7 && chessboard[5][7] == 0 && chessboard[6][7] == 0 && !isDiscoveringSelfCheck(4, 7, 5, 7) && !isDiscoveringSelfCheck(4, 7, 6, 7) && !isSelfCheck()) { //castle kingside
                                return true;
                            } else if (chessboard[0][7] == 7 && firstColumn == 4 && firstRow == 7 && secondColumn == 2 && secondRow == 7 && chessboard[1][7] == 0 && chessboard[2][7] == 0 && chessboard[3][7] == 0 && !isDiscoveringSelfCheck(4, 7, 1, 7) && !isDiscoveringSelfCheck(4, 7, 2, 7) && !isDiscoveringSelfCheck(4, 7, 3, 7) && !isSelfCheck()) { //castle queenside
                                return true;
                            }
                        }
                        break;
                    case 13: //white rook after it moved for the first time
                        if (firstColumn == secondColumn || firstRow == secondRow) { //checking if the desired position is the same firstColumn/firstRow
                            if (secondRow < firstRow) { //first direction
                                for (int k = secondCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn][firstRow - k] != 0) {
                                        return false;
                                    }
                                }
                            } else if (secondColumn > firstColumn) {
                                for (int k = firstCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn + k][firstRow] != 0) {
                                        return false;
                                    }
                                }
                            } else if (secondRow > firstRow) {
                                for (int k = secondCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn][firstRow + k] != 0) {
                                        return false;
                                    }
                                }
                            } else if (secondColumn < firstColumn) {
                                for (int k = firstCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn - k][firstRow] != 0) {
                                        return false;
                                    }
                                }
                            }
                            return true;
                        }
                        break;
                    case 15: //white king after it moved for the first time
                        if (firstCoordinate == 1 || secondCoordinate == 1) { //checking range of 1 square
                            //horizontal/vertical
                            if (firstCoordinate == secondCoordinate) { //diagonal
                                return true;
                            } else return firstColumn == secondColumn || firstRow == secondRow;
                        }
                }
            } else {
                switch (moveSource) {

                    case 2: //black pawn
                        if (firstColumn == secondColumn) {
                            if (firstRow == secondRow - 1 && moveTarget == 0) { //single move
                                if (secondRow == 7) { // promotion

                                    promotionMenu.removeAll();

                                    FlowLayout layout = new FlowLayout(FlowLayout.CENTER, 3, 3);
                                    promotionMenu.setLayout(layout);
                                    promotionMenu.setBackground(Color.BLACK);
                                    promotionMenu.setBorderPainted(false);

                                    JButton bishop = new JButton();
                                    bishop.setIcon(new ImageIcon(iconList[14]));
                                    bishop.setPreferredSize(DIMENSION);
                                    bishop.setBackground(Color.WHITE);
                                    bishop.setBorderPainted(false);
                                    bishop.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            promotionCondition = true;
                                            promotion = 4;
                                            move(firstColumn, firstRow, secondColumn, secondRow);
                                            promotionMenu.setVisible(false);
                                        }
                                    });
                                    promotionMenu.add(bishop);

                                    JButton knight = new JButton();
                                    knight.setIcon(new ImageIcon(iconList[16]));
                                    knight.setPreferredSize(DIMENSION);
                                    knight.setBackground(Color.WHITE);
                                    knight.setBorderPainted(false);
                                    knight.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            promotionCondition = true;
                                            promotion = 6;
                                            move(firstColumn, firstRow, secondColumn, secondRow);
                                            promotionMenu.setVisible(false);
                                        }
                                    });
                                    promotionMenu.add(knight);

                                    JButton rook = new JButton();
                                    rook.setIcon(new ImageIcon(iconList[18]));
                                    rook.setPreferredSize(DIMENSION);
                                    rook.setBackground(Color.WHITE);
                                    rook.setBorderPainted(false);
                                    rook.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            promotionCondition = true;
                                            promotion = 14;
                                            move(firstColumn, firstRow, secondColumn, secondRow);
                                            promotionMenu.setVisible(false);
                                        }
                                    });
                                    promotionMenu.add(rook);

                                    JButton queen = new JButton();
                                    queen.setIcon(new ImageIcon(iconList[20]));
                                    queen.setPreferredSize(DIMENSION);
                                    queen.setBackground(Color.WHITE);
                                    queen.setBorderPainted(false);
                                    queen.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            promotionCondition = true;
                                            promotion = 10;
                                            move(firstColumn, firstRow, secondColumn, secondRow);
                                            promotionMenu.setVisible(false);
                                        }
                                    });
                                    promotionMenu.add(queen);

                                    JButton cancel = new JButton();
                                    cancel.setIcon(new ImageIcon(iconList[26]));
                                    cancel.setPreferredSize(new Dimension(25, 50));
                                    cancel.setBackground(Color.WHITE);
                                    cancel.setBorderPainted(false);
                                    cancel.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            promotionMenu.setVisible(false);
                                        }
                                    });
                                    promotionMenu.add(cancel);

                                    promotionMenu.show(mainWindow, 107 + 50 * secondColumn, 531);
                                    return false;
                                }
                                return true;
                            } else if (firstRow == 1 && firstRow == secondRow - 2 && chessboard[secondColumn][secondRow - 1] == 0 && moveTarget == 0) { //double move
                                return true;
                            }
                        } else if (((firstColumn == secondColumn - 1 || firstColumn == secondColumn + 1) && firstRow == secondRow - 1) && moveTarget != 0) { //takes
                            if (secondRow == 7) { // promotion
                                promotionMenu.removeAll();

                                FlowLayout layout = new FlowLayout(FlowLayout.CENTER, 3, 3);
                                promotionMenu.setLayout(layout);
                                promotionMenu.setBackground(Color.BLACK);
                                promotionMenu.setBorderPainted(false);

                                JButton bishop = new JButton();
                                bishop.setIcon(new ImageIcon(iconList[14]));
                                bishop.setPreferredSize(DIMENSION);
                                bishop.setBackground(Color.WHITE);
                                bishop.setBorderPainted(false);
                                bishop.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        promotionCondition = true;
                                        promotion = 4;
                                        move(firstColumn, firstRow, secondColumn, secondRow);
                                        promotionMenu.setVisible(false);
                                    }
                                });
                                promotionMenu.add(bishop);

                                JButton knight = new JButton();
                                knight.setIcon(new ImageIcon(iconList[16]));
                                knight.setPreferredSize(DIMENSION);
                                knight.setBackground(Color.WHITE);
                                knight.setBorderPainted(false);
                                knight.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        promotionCondition = true;
                                        promotion = 6;
                                        move(firstColumn, firstRow, secondColumn, secondRow);
                                        promotionMenu.setVisible(false);
                                    }
                                });
                                promotionMenu.add(knight);

                                JButton rook = new JButton();
                                rook.setIcon(new ImageIcon(iconList[18]));
                                rook.setPreferredSize(DIMENSION);
                                rook.setBackground(Color.WHITE);
                                rook.setBorderPainted(false);
                                rook.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        promotionCondition = true;
                                        promotion = 14;
                                        move(firstColumn, firstRow, secondColumn, secondRow);
                                        promotionMenu.setVisible(false);
                                    }
                                });
                                promotionMenu.add(rook);

                                JButton queen = new JButton();
                                queen.setIcon(new ImageIcon(iconList[20]));
                                queen.setPreferredSize(DIMENSION);
                                queen.setBackground(Color.WHITE);
                                queen.setBorderPainted(false);
                                queen.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        promotionCondition = true;
                                        promotion = 10;
                                        move(firstColumn, firstRow, secondColumn, secondRow);
                                        promotionMenu.setVisible(false);
                                    }
                                });
                                promotionMenu.add(queen);

                                JButton cancel = new JButton();
                                cancel.setIcon(new ImageIcon(iconList[26]));
                                cancel.setPreferredSize(new Dimension(25, 50));
                                cancel.setBackground(Color.WHITE);
                                cancel.setBorderPainted(false);
                                cancel.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        promotionMenu.setVisible(false);
                                    }
                                });
                                promotionMenu.add(cancel);

                                promotionMenu.show(mainWindow, 107 + 50 * secondColumn, 531);
                                return false;
                            }
                            return true;
                        } else if ((((firstColumn == secondColumn - 1 && firstRow == secondRow - 1) && moveTarget == 0) || ((firstColumn == secondColumn + 1 && firstRow == secondRow - 1) && moveTarget == 0)) && enPassantList[moveCount][0] == 2 && secondColumn == enPassantList[moveCount][1] && secondRow == enPassantList[moveCount][2]) { //en passant
                            return true;
                        }
                        break;
                    case 4: //black bishop
                        if (firstCoordinate == secondCoordinate) {
                            if (secondColumn < firstColumn && secondRow < firstRow) {
                                for (int k = firstCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn - k][firstRow - k] != 0) {
                                        return false;
                                    }
                                }
                            } else if (secondColumn > firstColumn && secondRow < firstRow) {
                                for (int k = firstCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn + k][firstRow - k] != 0) {
                                        return false;
                                    }
                                }
                            } else if (secondColumn < firstColumn && secondRow > firstRow) {
                                for (int k = firstCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn - k][firstRow + k] != 0) {
                                        return false;
                                    }
                                }
                            } else if (secondColumn > firstColumn && secondRow > firstRow) {
                                for (int k = firstCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn + k][firstRow + k] != 0) {
                                        return false;
                                    }
                                }
                            }
                            return true;
                        }
                        break;
                    case 6: //black knight
                        if ((firstCoordinate == 2 && secondCoordinate == 1) || (firstCoordinate == 1 && secondCoordinate == 2)) {
                            return true;
                        }
                        break;
                    case 8: //black rook
                        if (firstColumn == secondColumn || firstRow == secondRow) {
                            if (secondRow < firstRow) {
                                for (int k = secondCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn][firstRow - k] != 0) {
                                        return false;
                                    }
                                }
                            } else if (secondColumn > firstColumn) {
                                for (int k = firstCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn + k][firstRow] != 0) {
                                        return false;
                                    }
                                }
                            } else if (secondRow > firstRow) {
                                for (int k = secondCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn][firstRow + k] != 0) {
                                        return false;
                                    }
                                }
                            } else if (secondColumn < firstColumn) {
                                for (int k = firstCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn - k][firstRow] != 0) {
                                        return false;
                                    }
                                }
                            }
                            return true;
                        }
                        break;
                    case 10: //black queen
                        if (firstCoordinate == secondCoordinate) {
                            if (secondColumn < firstColumn && secondRow < firstRow) {
                                for (int k = firstCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn - k][firstRow - k] != 0) {
                                        return false;
                                    }
                                }
                            } else if (secondColumn > firstColumn && secondRow < firstRow) {
                                for (int k = firstCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn + k][firstRow - k] != 0) {
                                        return false;
                                    }
                                }
                            } else if (secondColumn < firstColumn && secondRow > firstRow) {
                                for (int k = firstCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn - k][firstRow + k] != 0) {
                                        return false;
                                    }
                                }
                            } else if (secondColumn > firstColumn && secondRow > firstRow) {
                                for (int k = firstCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn + k][firstRow + k] != 0) {
                                        return false;
                                    }
                                }
                            }
                            return true;
                        }
                        if (firstColumn == secondColumn || firstRow == secondRow) {
                            if (secondRow < firstRow) {
                                for (int k = secondCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn][firstRow - k] != 0) {
                                        return false;
                                    }
                                }
                            } else if (secondColumn > firstColumn) {
                                for (int k = firstCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn + k][firstRow] != 0) {
                                        return false;
                                    }
                                }
                            } else if (secondRow > firstRow) {
                                for (int k = secondCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn][firstRow + k] != 0) {
                                        return false;
                                    }
                                }
                            } else if (secondColumn < firstColumn) {
                                for (int k = firstCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn - k][firstRow] != 0) {
                                        return false;
                                    }
                                }
                            }
                            return true;
                        }
                        break;
                    case 12: //black king
                        if (firstCoordinate == 1 || secondCoordinate == 1) {
                            if (firstCoordinate == secondCoordinate) {
                                return true;
                            } else return firstColumn == secondColumn || firstRow == secondRow;
                        } else {
                            if (chessboard[7][0] == 8 && firstColumn == 4 && firstRow == 0 && secondColumn == 6 && secondRow == 0 && chessboard[5][0] == 0 && chessboard[6][0] == 0 && !isDiscoveringSelfCheck(4, 0, 5, 0) && !isDiscoveringSelfCheck(4, 0, 6, 0) && !isSelfCheck()) { //castle kingside
                                //check if squares along are attacked
                                return true;

                            } else if (chessboard[0][0] == 8 && firstColumn == 4 && firstRow == 0 && secondColumn == 2 && secondRow == 0 && chessboard[1][0] == 0 && chessboard[2][0] == 0 && chessboard[3][0] == 0 && !isDiscoveringSelfCheck(4, 0, 1, 0) && !isDiscoveringSelfCheck(4, 0, 2, 0) && !isDiscoveringSelfCheck(4, 0, 3, 0) && !isSelfCheck()) { //castle queenside
                                //check if squares along are attacked
                                return true;
                            }
                        }
                        break;
                    case 14: //black rook after it moved for the first time
                        if (firstColumn == secondColumn || firstRow == secondRow) {
                            if (secondRow < firstRow) {
                                for (int k = secondCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn][firstRow - k] != 0) {
                                        return false;
                                    }
                                }
                            } else if (secondColumn > firstColumn) {
                                for (int k = firstCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn + k][firstRow] != 0) {
                                        return false;
                                    }
                                }
                            } else if (secondRow > firstRow) {
                                for (int k = secondCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn][firstRow + k] != 0) {
                                        return false;
                                    }
                                }
                            } else if (secondColumn < firstColumn) {
                                for (int k = firstCoordinate - 1; k > 0; k--) {
                                    if (Chess.chessboard[firstColumn - k][firstRow] != 0) {
                                        return false;
                                    }
                                }
                            }
                            return true;
                        }
                        break;
                    case 16: //black king after it moved for the first time
                        if (firstCoordinate == 1 || secondCoordinate == 1) {
                            if (firstCoordinate == secondCoordinate) {
                                return true;
                            } else return firstColumn == secondColumn || firstRow == secondRow;
                        }
                }
            }
        }
        return false;
    }

    public static void playSound(String path) {

        //try to load a sound and play it

        try {
            URL url = Chess.class.getResource(path);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            if (VOLUME < 0f || VOLUME > 1f)
                throw new IllegalArgumentException("volume not valid");
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(20f * (float) Math.log10(VOLUME));
            clip.start();
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            System.err.println("sound load error");
        }
    }

    public static void move(int firstColumn, int firstRow, int secondColumn, int secondRow) {

        //finally make the move and test for check

        for (int k = 0; k < 8; k++) { //save current chessboard to memory before the move
            System.arraycopy(chessboard[k], 0, movesList[moveCount][k], 0, 8);
        }
        int moveSource = chessboard[firstColumn][firstRow];
        MainWindow.prevColumnBeg = firstColumn;
        MainWindow.prevRowBeg = firstRow;
        MainWindow.prevColumnEnd = secondColumn;
        MainWindow.prevRowEnd = secondRow;

        Chess.playSound("/res/sound/piece_move.wav");

        if (chessboard[firstColumn][firstRow] == 1 || chessboard[firstColumn][firstRow] == 2) { //if the moving piece is a pawn
            if (!MainWindow.blackTurn) {
                if ((((firstColumn == secondColumn - 1 && firstRow == secondRow + 1) && chessboard[secondColumn][secondRow] == 0) || ((firstColumn == secondColumn + 1 && firstRow == secondRow + 1) && chessboard[secondColumn][secondRow] == 0)) && enPassantList[moveCount][0] == 1 && secondColumn == enPassantList[moveCount][1] && secondRow == enPassantList[moveCount][2]) { //en passant
                    chessboard[secondColumn][firstRow] = 0; //piece which caused en passant is removed
                    take();
                }
            } else {
                if ((((firstColumn == secondColumn - 1 && firstRow == secondRow - 1) && chessboard[secondColumn][secondRow] == 0) || ((firstColumn == secondColumn + 1 && firstRow == secondRow - 1) && chessboard[secondColumn][secondRow] == 0)) && enPassantList[moveCount][0] == 2 && secondColumn == enPassantList[moveCount][1] && secondRow == enPassantList[moveCount][2]) { //en passant
                    white75MoveRule = 0;
                    chessboard[secondColumn][firstRow] = 0;
                    take();
                }
                black75MoveRule = 0;
            }
        }
        if (chessboard[secondColumn][secondRow] != 0) { //seventy-five moves without take condition reset
            if (!MainWindow.blackTurn) {
                white75MoveRule = 0;
            } else {
                black75MoveRule = 0;
            }
            take();
        }

        moveCount++;

        if (moveCount == 200) { //set move's number in memory
            moveCount = 0;
            moveCycles++;
        }
        if (!MainWindow.blackTurn) { //count moves to the 75-moves-rule
            white75MoveRule++;
        } else {
            black75MoveRule++;
        }
        if (moveSource == 1 && firstRow == 6 && firstRow == secondRow + 2 && chessboard[secondColumn][secondRow + 1] == 0 && chessboard[secondColumn][secondRow] == 0) { //double move
            enPassantList[moveCount][0] = 2;
            enPassantList[moveCount][1] = secondColumn;
            enPassantList[moveCount][2] = secondRow + 1;
        } else if (moveSource == 2 && firstRow == 1 && firstRow == secondRow - 2 && chessboard[secondColumn][secondRow - 1] == 0 && chessboard[secondColumn][secondRow] == 0) { //double move
            enPassantList[moveCount][0] = 1;
            enPassantList[moveCount][1] = secondColumn;
            enPassantList[moveCount][2] = secondRow - 1;
        } else {
            enPassantList[moveCount][0] = 0;
        }
        if (MainWindow.blackTurn) { //perform castle
            if (moveSource == 12 && chessboard[7][0] == 8 && firstColumn == 4 && firstRow == 0 && secondColumn == 6 && secondRow == 0) {
                chessboard[4][0] = 16;
                chessboard[5][0] = 14;
                chessboard[7][0] = 0;
            } else if (moveSource == 12 && chessboard[0][0] == 8 && firstColumn == 4 && firstRow == 0 && secondColumn == 2 && secondRow == 0) {
                chessboard[4][0] = 16;
                chessboard[3][0] = 14;
                chessboard[0][0] = 0;
            }
        } else {
            if (moveSource == 11 && chessboard[7][7] == 7 && firstColumn == 4 && firstRow == 7 && secondColumn == 6 && secondRow == 7) {
                chessboard[4][7] = 15;
                chessboard[5][7] = 13;
                chessboard[7][7] = 0;
            } else if (moveSource == 11 && chessboard[0][7] == 7 && firstColumn == 4 && firstRow == 7 && secondColumn == 2 && secondRow == 7) {
                chessboard[4][7] = 15;
                chessboard[3][7] = 13;
                chessboard[0][7] = 0;
            }
        }
        switch (chessboard[firstColumn][firstRow]) { //mark the pieces involved in castling when they're moved for the first time
            case 7:
                if (firstColumn == 0 && firstRow == 7) {
                    chessboard[0][7] = 13;
                } else if (firstColumn == 7 && firstRow == 7) {
                    chessboard[7][7] = 13;
                }
                break;
            case 8:
                if (firstColumn == 0 && firstRow == 0) {
                    chessboard[0][0] = 14;
                } else if (firstColumn == 7 && firstRow == 0) {
                    chessboard[7][0] = 14;
                }
                break;
            case 11:
                chessboard[4][7] = 15;
                break;
            case 12:
                chessboard[4][0] = 16;
        }
        if (promotionCondition) { //promotion
            if (chessboard[firstColumn][firstRow] == 1) {
                chessboard[firstColumn][firstRow] = promotion;
                promotionCondition = false;
                promotion = 0;
            } else if (chessboard[firstColumn][firstRow] == 2) {
                chessboard[firstColumn][firstRow] = promotion;
                promotionCondition = false;
                promotion = 0;
            }
        }
        chessboard[secondColumn][secondRow] = chessboard[firstColumn][firstRow];
        chessboard[firstColumn][firstRow] = 0;

        if (isCheck()) { //is the king in check after this move
            check = true;
            playSound("/res/sound/check.wav");
            System.out.println("check");
        } else {
            check = false;
        }
        for (int k = 0; k < 8; k++) { //save current chessboard to memory after the move
            for (int l = 0; l < 8; l++) {
                movesList[moveCount][k][l] = chessboard[k][l];
                moveMemory = moveCount;
            }
        }
        MainWindow.blackTurn = !MainWindow.blackTurn; //pass turn
        mainWindow.paintChessboard();

        if (MainWindow.isGameOver()) { //check if the game can be continued

            String message;
            JLabel messageLabel;

            switch (gameOver) {

                case 1:
                    message = "<html><body><div width='100px' align='center'>Checkmate by white!</div></body></html>";
                    messageLabel = new JLabel(message);
                    JOptionPane.showMessageDialog(mainWindow, messageLabel, "Game Over", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(iconList[23]));
                    break;
                case 2:
                    message = "<html><body><div width='100px' align='center'>Checkmate by black!</div></body></html>";
                    messageLabel = new JLabel(message);
                    JOptionPane.showMessageDialog(mainWindow, messageLabel, "Game Over", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(iconList[24]));
                    break;
                case 3:
                    message = "<html><body><div width='110px' align='center'>Stalemate!</div></body></html>";
                    messageLabel = new JLabel(message);
                    JOptionPane.showMessageDialog(mainWindow, messageLabel, "Game Over", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(iconList[25]));
                    break;
                case 4:
                    message = "<html><body><div width='113px' align='center'>Draw!</div></body></html>";
                    messageLabel = new JLabel(message);
                    JOptionPane.showMessageDialog(mainWindow, messageLabel, "Game Over", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(iconList[25]));
            }
//            mainWindow.paintEndScreen();
        }
    }

    private static void take() {

        //play take sound

        playSound("/res/sound/piece_take.wav");
    }

    private static boolean isDiscoveringSelfCheck(int firstColumn, int firstRow, int secondColumn, int secondRow) {

        //forcing a move and then checking if it will cause self check/checkmate

        boolean discoverSelfCheck = false;

        if (chessboard[secondColumn][secondRow] != 11 && chessboard[secondColumn][secondRow] != 15 && chessboard[secondColumn][secondRow] != 12 && chessboard[secondColumn][secondRow] != 16) { //filtering the kings
            int temp = chessboard[secondColumn][secondRow];
            chessboard[secondColumn][secondRow] = chessboard[firstColumn][firstRow]; // forcing the move
            chessboard[firstColumn][firstRow] = 0;
            if (isSelfCheck()) {
                discoverSelfCheck = true;
            }
            chessboard[firstColumn][firstRow] = chessboard[secondColumn][secondRow]; //swap back positions
            chessboard[secondColumn][secondRow] = temp;
        }
        return discoverSelfCheck;
    }

    private static boolean isSelfCheck() {

        //picking the king's square and testing if it's still going to be in check

        boolean isCheck = true;
        int secondColumn = 8;
        int secondRow = 8;

        for (int firstColumn = 0; firstColumn < 8; firstColumn++) { //iterate through every square
            for (int firstRow = 0; firstRow < 8; firstRow++) {

                if (!MainWindow.blackTurn) {
                    for (int k = 0; k < 8; k++) {
                        for (int l = 0; l < 8; l++) {
                            if (chessboard[k][l] == 11 || chessboard[k][l] == 15) { //find white king's square
                                secondColumn = k;
                                secondRow = l;
                            }
                        }
                    }
                    int moveSource = chessboard[firstColumn][firstRow];
                    int moveTarget = chessboard[secondColumn][secondRow];
                    int firstCoordinate = Math.abs(firstColumn - secondColumn);
                    int secondCoordinate = Math.abs(firstRow - secondRow);

                    if (chessboard[firstColumn][firstRow] == 0 || chessboard[firstColumn][firstRow] % 2 != 0 || chessboard[firstColumn][firstRow] == 12 || chessboard[firstColumn][firstRow] == 16) { //if move source is empty or is a white piece as well or is a black king
                        isCheck = false;
                    }
                    switch (moveSource) {

                        case 2:
                            if (((firstColumn == secondColumn - 1 || firstColumn == secondColumn + 1) && firstRow == secondRow - 1) && moveTarget != 0) {
                                return true;
                            } else {
                                isCheck = false;
                            }
                            break;
                        case 4:
                            if (firstCoordinate == secondCoordinate) {
                                if (firstCoordinate == 1) {
                                    isCheck = true;
                                } else if (secondColumn < firstColumn && secondRow < firstRow) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[firstColumn - k][firstRow - k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else {
                                            isCheck = true;
                                        }
                                    }
                                } else if (secondColumn > firstColumn && secondRow < firstRow) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[firstColumn + k][firstRow - k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else {
                                            isCheck = true;
                                        }
                                    }
                                } else if (secondColumn < firstColumn && secondRow > firstRow) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[firstColumn - k][firstRow + k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else {
                                            isCheck = true;
                                        }
                                    }
                                } else if (secondColumn > firstColumn && secondRow > firstRow) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[firstColumn + k][firstRow + k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else {
                                            isCheck = true;
                                        }
                                    }
                                }
                                if (isCheck) {
                                    return true;
                                }
                            }
                            break;
                        case 6:
                            if ((firstCoordinate == 2 && secondCoordinate == 1) || (firstCoordinate == 1 && secondCoordinate == 2)) {
                                return true;
                            } else {
                                isCheck = false;
                            }
                            break;
                        case 8:
                        case 14:
                            if (firstColumn == secondColumn || firstRow == secondRow) {
                                if (firstCoordinate == 1 || secondCoordinate == 1) {
                                    isCheck = true;
                                } else if (secondRow < firstRow) {
                                    for (int k = secondCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[firstColumn][firstRow - k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else {
                                            isCheck = true;
                                        }
                                    }
                                } else if (secondColumn > firstColumn) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[firstColumn + k][firstRow] != 0) {
                                            isCheck = false;
                                            break;
                                        } else {
                                            isCheck = true;
                                        }
                                    }
                                } else if (secondRow > firstRow) {
                                    for (int k = secondCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[firstColumn][firstRow + k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else {
                                            isCheck = true;
                                        }
                                    }
                                } else if (secondColumn < firstColumn) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[firstColumn - k][firstRow] != 0) {
                                            isCheck = false;
                                            break;
                                        } else {
                                            isCheck = true;
                                        }
                                    }
                                }
                                if (isCheck) {
                                    return true;
                                }
                            }
                            break;
                        case 10:
                            if (firstCoordinate == secondCoordinate) {
                                if (firstCoordinate == 1) {
                                    isCheck = true;
                                } else if (secondColumn < firstColumn && secondRow < firstRow) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[firstColumn - k][firstRow - k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else {
                                            isCheck = true;
                                        }
                                    }
                                } else if (secondColumn > firstColumn && secondRow < firstRow) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[firstColumn + k][firstRow - k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else {
                                            isCheck = true;
                                        }
                                    }
                                } else if (secondColumn < firstColumn && secondRow > firstRow) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[firstColumn - k][firstRow + k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else {
                                            isCheck = true;
                                        }
                                    }
                                } else if (secondColumn > firstColumn && secondRow > firstRow) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[firstColumn + k][firstRow + k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else {
                                            isCheck = true;
                                        }
                                    }
                                }
                                if (isCheck) {
                                    return true;
                                }
                            }
                            if (firstColumn == secondColumn || firstRow == secondRow) {
                                if (firstCoordinate == 1 || secondCoordinate == 1) {
                                    isCheck = true;
                                } else if (secondRow < firstRow) {
                                    for (int k = secondCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[firstColumn][firstRow - k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else {
                                            isCheck = true;
                                        }
                                    }
                                } else if (secondColumn > firstColumn) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[firstColumn + k][firstRow] != 0) {
                                            isCheck = false;
                                            break;
                                        } else {
                                            isCheck = true;
                                        }
                                    }
                                } else if (secondRow > firstRow) {
                                    for (int k = secondCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[firstColumn][firstRow + k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else {
                                            isCheck = true;
                                        }
                                    }
                                } else if (secondColumn < firstColumn) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[firstColumn - k][firstRow] != 0) {
                                            isCheck = false;
                                            break;
                                        } else {
                                            isCheck = true;
                                        }
                                    }
                                }
                                if (isCheck) {
                                    return true;
                                }
                            }
                            break;
                        case 12:
                        case 16:
                            if (firstCoordinate == secondCoordinate) {
                                isCheck = firstCoordinate == 1;
                            }
                            if (firstColumn == secondColumn || firstRow == secondRow) {
                                isCheck = firstCoordinate == 1 || secondCoordinate == 1;
                            }
                            if (isCheck) {
                                return true;
                            }
                    }
                } else {

                    for (int k = 0; k < 8; k++) {
                        for (int l = 0; l < 8; l++) {
                            if (chessboard[k][l] == 12 || chessboard[k][l] == 16) { //find black king's square
                                secondColumn = k;
                                secondRow = l;
                            }
                        }
                    }
                    int moveSource = chessboard[firstColumn][firstRow];
                    int moveTarget = chessboard[secondColumn][secondRow];
                    int firstCoordinate = Math.abs(firstColumn - secondColumn);
                    int secondCoordinate = Math.abs(firstRow - secondRow);

                    if (chessboard[firstColumn][firstRow] == 0 || chessboard[firstColumn][firstRow] % 2 == 0 || chessboard[firstColumn][firstRow] == 11 || chessboard[firstColumn][firstRow] == 15) { //if move source is empty or is a black piece as well or is a white king
                        isCheck = false;
                    }
                    switch (moveSource) {

                        case 1:
                            if (((firstColumn == secondColumn - 1 || firstColumn == secondColumn + 1) && firstRow == secondRow + 1) && moveTarget != 0) {
                                return true;
                            } else {
                                isCheck = false;
                            }
                            break;
                        case 3:
                            if (firstCoordinate == secondCoordinate) {
                                if (firstCoordinate == 1) {
                                    isCheck = true;
                                } else if (secondColumn < firstColumn && secondRow < firstRow) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[firstColumn - k][firstRow - k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else {
                                            isCheck = true;
                                        }
                                    }
                                } else if (secondColumn > firstColumn && secondRow < firstRow) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[firstColumn + k][firstRow - k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else {
                                            isCheck = true;
                                        }
                                    }
                                } else if (secondColumn < firstColumn && secondRow > firstRow) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[firstColumn - k][firstRow + k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else {
                                            isCheck = true;
                                        }
                                    }
                                } else if (secondColumn > firstColumn && secondRow > firstRow) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[firstColumn + k][firstRow + k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else {
                                            isCheck = true;
                                        }
                                    }
                                }
                                if (isCheck) {
                                    return true;
                                }
                            }
                            break;
                        case 5:
                            if ((firstCoordinate == 2 && secondCoordinate == 1) || (firstCoordinate == 1 && secondCoordinate == 2)) {
                                return true;
                            } else {
                                isCheck = false;
                            }
                            break;
                        case 7:
                        case 13:
                            if (firstColumn == secondColumn || firstRow == secondRow) {
                                if (firstCoordinate == 1 || secondCoordinate == 1) {
                                    isCheck = true;
                                } else if (secondRow < firstRow) {
                                    for (int k = secondCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[firstColumn][firstRow - k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else {
                                            isCheck = true;
                                        }
                                    }
                                } else if (secondColumn > firstColumn) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[firstColumn + k][firstRow] != 0) {
                                            isCheck = false;
                                            break;
                                        } else {
                                            isCheck = true;
                                        }
                                    }
                                } else if (secondRow > firstRow) {
                                    for (int k = secondCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[firstColumn][firstRow + k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else {
                                            isCheck = true;
                                        }
                                    }
                                } else if (secondColumn < firstColumn) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[firstColumn - k][firstRow] != 0) {
                                            isCheck = false;
                                            break;
                                        } else {
                                            isCheck = true;
                                        }
                                    }
                                }
                                if (isCheck) {
                                    return true;
                                }
                            }
                            break;
                        case 9:
                            if (firstCoordinate == secondCoordinate) {
                                if (firstCoordinate == 1) {
                                    isCheck = true;
                                } else if (secondColumn < firstColumn && secondRow < firstRow) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[firstColumn - k][firstRow - k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else {
                                            isCheck = true;
                                        }
                                    }
                                } else if (secondColumn > firstColumn && secondRow < firstRow) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[firstColumn + k][firstRow - k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else {
                                            isCheck = true;
                                        }
                                    }
                                } else if (secondColumn < firstColumn && secondRow > firstRow) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[firstColumn - k][firstRow + k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else {
                                            isCheck = true;
                                        }
                                    }
                                } else if (secondColumn > firstColumn && secondRow > firstRow) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[firstColumn + k][firstRow + k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else {
                                            isCheck = true;
                                        }
                                    }
                                }
                                if (isCheck) {
                                    return true;
                                }
                            }
                            if (firstColumn == secondColumn || firstRow == secondRow) {
                                if (firstCoordinate == 1 || secondCoordinate == 1) {
                                    isCheck = true;
                                } else if (secondRow < firstRow) {
                                    for (int k = secondCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[firstColumn][firstRow - k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else {
                                            isCheck = true;
                                        }
                                    }
                                } else if (secondColumn > firstColumn) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[firstColumn + k][firstRow] != 0) {
                                            isCheck = false;
                                            break;
                                        } else {
                                            isCheck = true;
                                        }
                                    }
                                } else if (secondRow > firstRow) {
                                    for (int k = secondCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[firstColumn][firstRow + k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else {
                                            isCheck = true;
                                        }
                                    }
                                } else if (secondColumn < firstColumn) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[firstColumn - k][firstRow] != 0) {
                                            isCheck = false;
                                            break;
                                        } else {
                                            isCheck = true;
                                        }
                                    }
                                }
                                if (isCheck) {
                                    return true;
                                }
                            }
                            break;
                        case 11:
                        case 15:
                            if (firstCoordinate == secondCoordinate) {
                                isCheck = firstCoordinate == 1;
                            }
                            if (firstColumn == secondColumn || firstRow == secondRow) {
                                isCheck = firstCoordinate == 1 || secondCoordinate == 1;
                            }
                            if (isCheck) {
                                return true;
                            }
                    }
                }
            }
        }
        return isCheck;
    }

    private static boolean isCheck() {

        //finding out if the opposing king's in danger

        boolean isCheck = true;
        int secondColumn = 8;
        int secondRow = 8;

        for (int column = 0; column < 8; column++) {
            for (int row = 0; row < 8; row++) {

                if (MainWindow.blackTurn) {
                    for (int k = 0; k < 8; k++) {
                        for (int l = 0; l < 8; l++) {
                            if (chessboard[k][l] == 11 || chessboard[k][l] == 15) {
                                secondColumn = k;
                                secondRow = l;
                            }
                        }
                    }
                    int moveSource = chessboard[column][row];
                    int moveTarget = chessboard[secondColumn][secondRow];
                    int firstCoordinate = Math.abs(column - secondColumn);
                    int secondCoordinate = Math.abs(row - secondRow);

                    if (chessboard[column][row] == 0 || chessboard[column][row] % 2 == 0 || chessboard[column][row] == 12) {
                        isCheck = false;
                    }
                    switch (moveSource) {

                        case 2:
                            if (((column == secondColumn - 1 || column == secondColumn + 1) && row == secondRow - 1) && moveTarget != 0) {
                                return true;
                            } else {
                                isCheck = false;
                            }
                            break;
                        case 4:
                            if (firstCoordinate == secondCoordinate) {
                                if (firstCoordinate == 1) {
                                    isCheck = true;
                                } else if (secondColumn < column && secondRow < row) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[column - k][row - k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else isCheck = true;
                                    }
                                } else if (secondColumn > column && secondRow < row) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[column + k][row - k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else isCheck = true;
                                    }
                                } else if (secondColumn < column && secondRow > row) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[column - k][row + k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else isCheck = true;
                                    }
                                } else if (secondColumn > column && secondRow > row) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[column + k][row + k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else isCheck = true;
                                    }
                                }
                                if (isCheck) {
                                    return true;
                                }
                            }
                            break;
                        case 6:
                            if ((firstCoordinate == 2 && secondCoordinate == 1) || (firstCoordinate == 1 && secondCoordinate == 2)) {
                                return true;
                            } else {
                                isCheck = false;
                            }
                            break;
                        case 8:
                        case 14:
                            if (column == secondColumn || row == secondRow) {
                                if (firstCoordinate == 1 || secondCoordinate == 1) {
                                    isCheck = true;
                                } else if (secondRow < row) {
                                    for (int k = secondCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[column][row - k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else isCheck = true;
                                    }
                                } else if (secondColumn > column) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[column + k][row] != 0) {
                                            isCheck = false;
                                            break;
                                        } else isCheck = true;
                                    }
                                } else if (secondRow > row) {
                                    for (int k = secondCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[column][row + k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else isCheck = true;
                                    }
                                } else if (secondColumn < column) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[column - k][row] != 0) {
                                            isCheck = false;
                                            break;
                                        } else isCheck = true;
                                    }
                                }
                                if (isCheck) {
                                    return true;
                                }
                            }
                            break;
                        case 10:
                            if (firstCoordinate == secondCoordinate) {
                                if (firstCoordinate == 1) {
                                    isCheck = true;
                                } else if (secondColumn < column && secondRow < row) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[column - k][row - k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else isCheck = true;
                                    }
                                } else if (secondColumn > column && secondRow < row) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[column + k][row - k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else isCheck = true;
                                    }
                                } else if (secondColumn < column && secondRow > row) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[column - k][row + k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else isCheck = true;
                                    }
                                } else if (secondColumn > column && secondRow > row) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[column + k][row + k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else isCheck = true;
                                    }
                                }
                                if (isCheck) {
                                    return true;
                                }
                            }
                            if (column == secondColumn || row == secondRow) {
                                if (firstCoordinate == 1 || secondCoordinate == 1) {
                                    isCheck = true;
                                } else if (secondRow < row) {
                                    for (int k = secondCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[column][row - k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else isCheck = true;
                                    }
                                } else if (secondColumn > column) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[column + k][row] != 0) {
                                            isCheck = false;
                                            break;
                                        } else isCheck = true;
                                    }
                                } else if (secondRow > row) {
                                    for (int k = secondCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[column][row + k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else isCheck = true;
                                    }
                                } else if (secondColumn < column) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[column - k][row] != 0) {
                                            isCheck = false;
                                            break;
                                        } else isCheck = true;
                                    }
                                }
                                if (isCheck) {
                                    return true;
                                }
                            }
                    }
                } else {

                    for (int k = 0; k < 8; k++) {
                        for (int l = 0; l < 8; l++) {
                            if (chessboard[k][l] == 12 || chessboard[k][l] == 16) {
                                secondColumn = k;
                                secondRow = l;
                            }
                        }
                    }
                    int moveSource = chessboard[column][row];
                    int moveTarget = chessboard[secondColumn][secondRow];
                    int firstCoordinate = Math.abs(column - secondColumn);
                    int secondCoordinate = Math.abs(row - secondRow);

                    if (chessboard[column][row] == 0 || chessboard[column][row] % 2 != 0 || chessboard[column][row] == 11) {
                        isCheck = false;
                    }
                    switch (moveSource) {

                        case 1:
                            if (((column == secondColumn - 1 || column == secondColumn + 1) && row == secondRow + 1) && moveTarget != 0) {
                                return true;
                            } else {
                                isCheck = false;
                            }
                            break;
                        case 3:
                            if (firstCoordinate == secondCoordinate) {
                                if (firstCoordinate == 1) {
                                    isCheck = true;
                                } else if (secondColumn < column && secondRow < row) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[column - k][row - k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else isCheck = true;
                                    }
                                } else if (secondColumn > column && secondRow < row) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[column + k][row - k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else isCheck = true;
                                    }
                                } else if (secondColumn < column && secondRow > row) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[column - k][row + k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else isCheck = true;
                                    }
                                } else if (secondColumn > column && secondRow > row) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[column + k][row + k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else isCheck = true;
                                    }
                                }
                                if (isCheck) {
                                    return true;
                                }
                            }
                            break;
                        case 5:
                            if ((firstCoordinate == 2 && secondCoordinate == 1) || (firstCoordinate == 1 && secondCoordinate == 2)) {
                                return true;
                            } else {
                                isCheck = false;
                            }
                            break;
                        case 7:
                        case 13:
                            if (column == secondColumn || row == secondRow) {
                                if (firstCoordinate == 1 || secondCoordinate == 1) {
                                    isCheck = true;
                                } else if (secondRow < row) {
                                    for (int k = secondCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[column][row - k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else isCheck = true;
                                    }
                                } else if (secondColumn > column) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[column + k][row] != 0) {
                                            isCheck = false;
                                            break;
                                        } else isCheck = true;
                                    }
                                } else if (secondRow > row) {
                                    for (int k = secondCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[column][row + k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else isCheck = true;
                                    }
                                } else if (secondColumn < column) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[column - k][row] != 0) {
                                            isCheck = false;
                                            break;
                                        } else isCheck = true;
                                    }
                                }
                                if (isCheck) {
                                    return true;
                                }
                            }
                            break;
                        case 9:
                            if (firstCoordinate == secondCoordinate) {
                                if (firstCoordinate == 1) {
                                    isCheck = true;
                                } else if (secondColumn < column && secondRow < row) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[column - k][row - k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else isCheck = true;
                                    }
                                } else if (secondColumn > column && secondRow < row) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[column + k][row - k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else isCheck = true;
                                    }
                                } else if (secondColumn < column && secondRow > row) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[column - k][row + k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else isCheck = true;
                                    }
                                } else if (secondColumn > column && secondRow > row) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[column + k][row + k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else isCheck = true;
                                    }
                                }
                                if (isCheck) {
                                    return true;
                                }
                            }
                            if (column == secondColumn || row == secondRow) {
                                if (firstCoordinate == 1 || secondCoordinate == 1) {
                                    isCheck = true;
                                } else if (secondRow < row) {
                                    for (int k = secondCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[column][row - k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else isCheck = true;
                                    }
                                } else if (secondColumn > column) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[column + k][row] != 0) {
                                            isCheck = false;
                                            break;
                                        } else isCheck = true;
                                    }
                                } else if (secondRow > row) {
                                    for (int k = secondCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[column][row + k] != 0) {
                                            isCheck = false;
                                            break;
                                        } else isCheck = true;
                                    }
                                } else if (secondColumn < column) {
                                    for (int k = firstCoordinate - 1; k > 0; k--) {
                                        if (Chess.chessboard[column - k][row] != 0) {
                                            isCheck = false;
                                            break;
                                        } else isCheck = true;
                                    }
                                }
                                if (isCheck) {
                                    return true;
                                }
                            }
                    }
                }
            }
        }
        return isCheck;
    }

    private static int[][] prepareChessboard() {

        //make and set a 8x8 chessboard matrix

        final int SIZE = 8;
        int[][] preparedBoard = new int[8][8];

        for (int i = 0; i < SIZE; i++) {
            preparedBoard[i][6] = WHITE_PAWN.value; //white pawn
        }
        for (int i = 0; i < SIZE; i++) {
            preparedBoard[i][1] = BLACK_PAWN.value; //black pawn
        }
        preparedBoard[0][0] = BLACK_ROOK.value; //black rook
        preparedBoard[1][0] = BLACK_KNIGHT.value; //black knight
        preparedBoard[2][0] = BLACK_BISHOP.value; //black bishop
        preparedBoard[3][0] = BLACK_QUEEN.value; //black queen
        preparedBoard[4][0] = BLACK_KING.value; //black king
        preparedBoard[5][0] = BLACK_BISHOP.value;
        preparedBoard[6][0] = BLACK_KNIGHT.value;
        preparedBoard[7][0] = BLACK_ROOK.value;
        preparedBoard[0][7] = WHITE_ROOK.value; //white rook
        preparedBoard[1][7] = WHITE_KNIGHT.value; //white knight
        preparedBoard[2][7] = WHITE_BISHOP.value; //white bishop
        preparedBoard[3][7] = WHITE_QUEEN.value; //white queen
        preparedBoard[4][7] = WHITE_KING.value; //white king
        preparedBoard[5][7] = WHITE_BISHOP.value;
        preparedBoard[6][7] = WHITE_KNIGHT.value;
        preparedBoard[7][7] = WHITE_ROOK.value;

        return preparedBoard;
    }

    private static void loadResources() {

        //try and load piece images and fonts

        try {
            loadFonts();
        } catch (IOException | FontFormatException e) {
            System.err.println("font load error");
        }
        try {
            loadImages();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("image load error");
        }
    }

    private static void loadImages() throws IOException {

        int accuracy = 16;

        for (int i = 0; i < iconList.length; i++) {
            iconList[i] = ImageIO.read(Chess.class.getResource("/res/img/" + i + ".png"));
            if (i == 0) {
                scaleImage(i, 256, 256, accuracy);
            }
            if (i < 13) {
                scaleImage(i, 50, 50, accuracy);
            } else if (i == 13 || i == 14) {
                scaleImage(i, 24, 30, accuracy);
            } else if (i == 15 || i == 16) {
                scaleImage(i, 30, 30, accuracy);
            } else if (i < 21) {
                scaleImage(i, 27, 30, accuracy);
            } else if (i < 23) {
                scaleImage(i, 32, 30, accuracy);
            } else if (i == 23 || i == 24) {
                scaleImage(i, 40, 40, accuracy);
            } else if (i == 25) {
                scaleImage(i, 33, 40, accuracy);
            } else if (i == 26) {
                scaleImage(i, 25, 25, accuracy);
            }
        }
    }

    private static void scaleImage(int i, int width, int height, int accuracy) {

        iconList[i] = iconList[i].getScaledInstance(width, height, accuracy);
    }

    private static void loadFonts() throws IOException, FontFormatException {

        for (int i = 1; i <= 4; i++) {

            InputStream input = Chess.class.getResourceAsStream("/res/fonts/" + i + ".ttf");

            switch (i) {
                case 1:
                    font = Font.createFont(Font.TRUETYPE_FONT, input);
                    break;
                case 2:
                    boldFont = Font.createFont(Font.TRUETYPE_FONT, input);
                    break;
                case 3:
                    pixelFont = Font.createFont(Font.TRUETYPE_FONT, input);
                    break;
                case 4:
                    thickFont = Font.createFont(Font.TRUETYPE_FONT, input);
            }
        }
    }
}
