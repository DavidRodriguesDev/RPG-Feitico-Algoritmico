package org.example.entidades;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

/**
 * Classe que representa o jogador
 */
public class Player extends Personagem {
    // Velocidades
    private static final double VELOCIDADE_ANDAR = 70;
    private static final double VELOCIDADE_CORRER = 100;

    // Hitbox
    private static final double HITBOX_LARGURA = 12;
    private static final double HITBOX_ALTURA = 14;
    private static final double HITBOX_OFFSET_X = 32.5;
    private static final double HITBOX_OFFSET_Y = 18;

    // Animação
    private Image spriteSheet;
    private Image[] frames;
    private int frameAtual = 0;
    private double tempoAnimacao = 0;
    private static final double FPS_ANIMACAO = 10;
    private boolean olhandoDireita = true;

    // Estados
    private boolean correndo = false;

    public Player(double x, double y) {
        super(x, y, null, "PLAYER", 100);
        this.frames = new Image[8];
        carregarSprites();
    }

    private void carregarSprites() {
        try {
            spriteSheet = new Image(getClass().getResourceAsStream("/sprites/player_walk.png"));
            double fw = spriteSheet.getWidth() / 8;
            double fh = spriteSheet.getHeight();

            for (int i = 0; i < 8; i++) {
                frames[i] = new WritableImage(
                        spriteSheet.getPixelReader(),
                        (int)(i * fw), 0,
                        (int)fw, (int)fh
                );
            }
            this.sprite = frames[0];
        } catch (Exception e) {
            System.err.println("Erro ao carregar sprites do player: " + e.getMessage());
        }
    }

    public void atualizarAnimacao(double dt, boolean movendo) {
        if (movendo) {
            tempoAnimacao += dt;
            if (tempoAnimacao >= 1.0 / FPS_ANIMACAO) {
                tempoAnimacao = 0;
                frameAtual = (frameAtual + 1) % frames.length;
            }
        } else {
            frameAtual = 0;
        }
    }

    @Override
    public void desenhar(GraphicsContext gc) {
        Image frame = frames[frameAtual];
        double w = frame.getWidth() * 0.8;
        double h = frame.getHeight() * 0.8;

        if (olhandoDireita) {
            gc.drawImage(frame, x, y, w, h);
        } else {
            gc.drawImage(frame, x + w, y, -w, h);
        }
    }

    public void desenharInvertido(GraphicsContext gc) {
        Image frame = frames[frameAtual];
        double w = frame.getWidth() * 0.8;
        double h = frame.getHeight() * 0.8;

        // Inverte a direção no mundo alternativo
        boolean direcaoRender = !olhandoDireita;

        if (direcaoRender) {
            gc.drawImage(frame, x, y, w, h);
        } else {
            gc.drawImage(frame, x + w, y, -w, h);
        }
    }

    // Métodos de movimentação
    public void moverX(double dx) {
        if (dx == 0) return;
        x += dx;
    }

    public void moverY(double dy) {
        if (dy == 0) return;
        y += dy;
    }

    // Getters da hitbox
    public double getHitboxX() {
        return x + HITBOX_OFFSET_X;
    }

    public double getHitboxY() {
        return y + HITBOX_OFFSET_Y;
    }

    public double getHitboxLargura() {
        return HITBOX_LARGURA;
    }

    public double getHitboxAltura() {
        return HITBOX_ALTURA;
    }

    // Getters de velocidade
    public double getVelocidadeAtual() {
        return correndo ? VELOCIDADE_CORRER : VELOCIDADE_ANDAR;
    }

    // Getters e Setters
    public boolean isOlhandoDireita() { return olhandoDireita; }
    public void setOlhandoDireita(boolean olhandoDireita) { this.olhandoDireita = olhandoDireita; }

    public boolean isCorrendo() { return correndo; }
    public void setCorrendo(boolean correndo) { this.correndo = correndo; }

    public int getFrameAtual() { return frameAtual; }
    public void setFrameAtual(int frameAtual) { this.frameAtual = frameAtual; }

    public double getTempoAnimacao() { return tempoAnimacao; }
    public void setTempoAnimacao(double tempoAnimacao) { this.tempoAnimacao = tempoAnimacao; }

    public Image[] getFrames() { return frames; }

    public void resetarAnimacao() {
        frameAtual = 0;
        tempoAnimacao = 0;
    }
}
