package de.hhn.shogi.frontend;

import de.hhn.shogi.gamelogic.Piece;
import de.hhn.shogi.gamelogic.util.Vec2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import static de.hhn.shogi.frontend.Window.BOARD_SIZE;
import static de.hhn.shogi.gamelogic.Game.ACTIVE_GAME;

public class FieldButton extends JButton {
    private final Vec2 pos;
    private final Color legalMoveColor = new Color(145, 100, 145, 200);
    private final int legalMoveBorderWidth = 5;
    Window window;
    private Piece piece;
    private int translatedX, translatedY;
    private boolean hovering = false, mousePressed = false;
    private boolean legalMoveIcon = false;

    public FieldButton(Vec2 pos, Piece piece, Window w) {
        super();
        this.pos = pos;
        this.piece = piece;
        this.window = w;

        //init button
        this.translatedX = this.limit(pos.getX() * 75 + this.window.getWidth() / 2 - BOARD_SIZE / 2 + 37, 0, this.window.getWidth());
        this.translatedY = this.limit((8 - pos.getY()) * 75 + this.window.getHeight() / 2 - BOARD_SIZE / 2 + 37, 0, this.window.getHeight());
        this.setContentAreaFilled(false);
        this.setBounds(this.translatedX, this.translatedY, 75, 75);
        this.setBorderPainted(false);
        this.window.add(this, 0);

        //paint border if hovered over
        this.addMouseListener(this.mouseListener());
    }

    //rotate a buffered image 90°
    public static BufferedImage rotate(BufferedImage img) {
        BufferedImage rotated = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        Graphics2D graphic = rotated.createGraphics();
        graphic.rotate(Math.PI, img.getWidth() >> 1, img.getHeight() >> 1);
        graphic.drawRenderedImage(img, null);
        graphic.dispose();
        return rotated;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        // calculate position on the screen from shogi coordinates
        this.translatedX = this.limit(this.pos.getX() * 75 + this.window.getWidth() / 2 - BOARD_SIZE / 2 + 37, 0, this.window.getWidth());
        this.translatedY = this.limit((8 - this.pos.getY()) * 75 + this.window.getHeight() / 2 - BOARD_SIZE / 2 + 37, 0, this.window.getHeight());
        this.setBounds(this.translatedX, this.translatedY, 75, 75);

        //paint piece if Piece is not null
        if (this.piece != null) {
            this.setIcon(this.getImg(this.getPathName()));
        } else {
            this.setIcon(this.getEmptyEffect());
        }
    }

    //returns the background of an empty field
    private ImageIcon getEmptyEffect() {
        BufferedImage img = new BufferedImage(75, 75, BufferedImage.TYPE_INT_ARGB);
        Graphics2D imgGraphics = img.createGraphics();
        if (this.mousePressed) {
            imgGraphics.setColor(new Color(0, 0, 0, 60));
            imgGraphics.fillRect(0, 0, 75, 75);
        } else if (this.hovering) {
            imgGraphics.setColor(new Color(0, 0, 0, 30));
            imgGraphics.fillRect(0, 0, 75, 75);
        }
        if (this.legalMoveIcon) {
            imgGraphics.setColor(this.legalMoveColor);
            imgGraphics.fillOval(25, 25, 25, 25);
        }
        return new ImageIcon(img);
    }

    //returns formatted image from path name
    private ImageIcon getImg(String name) {
        ImageIcon pieceImg = new ImageIcon("src/de/hhn/shogi/frontend/images/" + name);
        ImageIcon plusImg = new ImageIcon("src/de/hhn/shogi/frontend/images/plus.png");
        BufferedImage img = new BufferedImage(75, 75, BufferedImage.TYPE_INT_ARGB);
        //create graphics
        Graphics2D imgGraphics = img.createGraphics();
        //draw hover/click effect
        imgGraphics.drawImage(this.getEmptyEffect().getImage(), 0, 0, null);
        //draw piece
        imgGraphics.drawImage(pieceImg.getImage(), 0, 0, null);
        //add plus icon
        if (this.piece.isPromoted()) {
            imgGraphics.drawImage(plusImg.getImage(), 0, 0, null);
        }
        //rotate
        BufferedImage rotated = this.piece.getSide() == this.window.bottom ? img : FieldButton.rotate(img);
        //add legalMoveIcon
        if (this.legalMoveIcon) {
            Graphics2D rotatedGraphics = rotated.createGraphics();
            rotatedGraphics.setColor(this.legalMoveColor);
            rotatedGraphics.fillRect(2, 2, 72, this.legalMoveBorderWidth);
            rotatedGraphics.fillRect(2, 2, this.legalMoveBorderWidth, 72);
            rotatedGraphics.fillRect(74 - this.legalMoveBorderWidth, 2, this.legalMoveBorderWidth, 72);
            rotatedGraphics.fillRect(2, 74 - this.legalMoveBorderWidth, 72, this.legalMoveBorderWidth);
        }
        return new ImageIcon(rotated);
    }

    //select image based on PieceType
    public String getPathName() {
        return switch (this.piece.getBaseType()) {
            case LANCE -> "lance.png";
            case KNIGHT -> "knight.png";
            case BISHOP, HORSE -> "bishop.png";
            case KING -> "king.png";
            case ROOK, DRAGON -> "rook.png";
            case PAWN -> "pawn.png";
            case GOLD_GENERAL -> "gold_general.png";
            case SILVER_GENERAL -> "silver_general.png";
            default -> "blank.png";
        };
    }

    private MouseListener mouseListener() {
        return new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                FieldButton.this.hovering = true;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                FieldButton.this.hovering = false;
                FieldButton.this.mousePressed = false;
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == 1 && FieldButton.this.hovering) {
                    FieldButton.this.mousePressed = true;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == 1) {
                    if (FieldButton.this.mousePressed && FieldButton.this.hovering) {
                        System.out.println(FieldButton.this.pos);
                        ACTIVE_GAME.getState().fieldClick(FieldButton.this.pos);
                    }
                    FieldButton.this.mousePressed = false;
                }
            }
        };
    }

    private int limit(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public void legalMove(boolean addOrRemove) {
        this.legalMoveIcon = addOrRemove;
    }
}
