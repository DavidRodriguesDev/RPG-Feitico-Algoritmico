package org.example;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class NPC {

    private static final double AJUSTE_X = -7;
    private static final double ESCALA = 0.3;

    private double x;
    private double y;
    private Image sprite;

    // ===== HITBOX SÓLIDA =====
    private static final double HIT_W = 32;
    private static final double HIT_H = 35;
    private static final double HIT_OFF_X = 6;
    private static final double HIT_OFF_Y = 8;

    // ===== ÁREA DE INTERAÇÃO (MAIOR) =====
    private static final double INTERACAO_MARGEM = 18;

    public static boolean DEBUG = false;

    public NPC(double x, double y, Image sprite) {
        this.x = x;
        this.y = y;
        this.sprite = sprite;
    }

    public void desenhar(GraphicsContext gc) {
        double w = sprite.getWidth() * ESCALA;
        double h = sprite.getHeight() * ESCALA;

        double drawX = x + AJUSTE_X;
        double drawY = y - h;

        gc.drawImage(sprite, drawX, drawY, w, h);

        if (DEBUG) {
            // hitbox sólida
            gc.setStroke(Color.RED);
            gc.strokeRect(getHitX(), getHitY(), HIT_W, HIT_H);

            // área de interação
            gc.setStroke(Color.YELLOW);
            gc.strokeRect(
                    getHitX() - INTERACAO_MARGEM,
                    getHitY() - INTERACAO_MARGEM,
                    HIT_W + INTERACAO_MARGEM * 2,
                    HIT_H + INTERACAO_MARGEM * 2
            );
        }
    }

    // ===== HITBOX =====
    public double getHitX() {
        return x + AJUSTE_X + HIT_OFF_X;
    }

    public double getHitY() {
        return y - sprite.getHeight() * ESCALA + HIT_OFF_Y;
    }

    // ===== COLISÃO SÓLIDA =====
    public boolean colidiu(double px, double py, double pw, double ph) {
        return px < getHitX() + HIT_W &&
                px + pw > getHitX() &&
                py < getHitY() + HIT_H &&
                py + ph > getHitY();
    }

    // ===== INTERAÇÃO =====
    public boolean jogadorPerto(double px, double py, double pw, double ph) {
        return px < getHitX() + HIT_W + INTERACAO_MARGEM &&
                px + pw > getHitX() - INTERACAO_MARGEM &&
                py < getHitY() + HIT_H + INTERACAO_MARGEM &&
                py + ph > getHitY() - INTERACAO_MARGEM;
    }
}
