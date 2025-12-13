package org.example;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

    // ===== TELA =====
    private static final int TELA_LARGURA = 1024;
    private static final int TELA_ALTURA  = 768;

    // ===== CÂMERA =====
    private static final double ZOOM = 3.0;

    // ===== JOGADOR =====
    private static final double ESCALA_JOGADOR = 0.80;
    private double jogadorX = 300;
    private double jogadorY = 100;

    private static final double VELOCIDADE_ANDAR = 70;
    private static final double VELOCIDADE_CORRER = 100;

    // ===== HITBOX =====
    private static final double HITBOX_LARGURA = 12;
    private static final double HITBOX_ALTURA  = 14;
    private static final double HITBOX_OFFSET_X = 32.5;
    private static final double HITBOX_OFFSET_Y = 18;

    private Canvas canvas;
    private GraphicsContext gc;
    private MapaTiled mapa;

    // ===== SPRITE =====
    private Image spriteSheet;
    private Image[] frames = new Image[8];
    private int frameAtual = 0;
    private double tempoAnimacao = 0;
    private final double FPS_ANIMACAO = 10;
    private boolean olhandoDireita = true;

    private boolean up, down, left, right, correr;

    @Override
    public void start(Stage stage) {

        mapa = new MapaTiled("/map/mapa.tmx");

        canvas = new Canvas(TELA_LARGURA, TELA_ALTURA);
        gc = canvas.getGraphicsContext2D();
        gc.setImageSmoothing(false);

        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, TELA_LARGURA, TELA_ALTURA);

        stage.setTitle("RPG");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        // ===== SPRITE =====
        spriteSheet = new Image(
                getClass().getResourceAsStream("/sprites/player_walk.png")
        );

        double frameW = spriteSheet.getWidth() / 8;
        double frameH = spriteSheet.getHeight();

        for (int i = 0; i < 8; i++) {
            frames[i] = new WritableImage(
                    spriteSheet.getPixelReader(),
                    (int) (i * frameW), 0,
                    (int) frameW, (int) frameH
            );
        }

        // ===== INPUT =====
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case W, UP -> up = true;
                case S, DOWN -> down = true;
                case A, LEFT -> left = true;
                case D, RIGHT -> right = true;
                case SHIFT -> correr = true;
            }
        });

        scene.setOnKeyReleased(e -> {
            switch (e.getCode()) {
                case W, UP -> up = false;
                case S, DOWN -> down = false;
                case A, LEFT -> left = false;
                case D, RIGHT -> right = false;
                case SHIFT -> correr = false;
            }
        });

        // ===== LOOP =====
        new AnimationTimer() {
            long last = 0;

            @Override
            public void handle(long now) {
                if (last == 0) {
                    last = now;
                    return;
                }

                double dt = (now - last) / 1e9;
                last = now;

                atualizar(dt);
                renderizar();
            }
        }.start();
    }

    private void atualizar(double dt) {

        double velocidadeAtual = correr ? VELOCIDADE_CORRER : VELOCIDADE_ANDAR;

        double dx = 0;
        double dy = 0;
        boolean movendo = false;

        if (up)    { dy -= velocidadeAtual * dt; movendo = true; }
        if (down)  { dy += velocidadeAtual * dt; movendo = true; }
        if (left)  { dx -= velocidadeAtual * dt; olhandoDireita = false; movendo = true; }
        if (right) { dx += velocidadeAtual * dt; olhandoDireita = true; movendo = true; }

        double novoX = jogadorX + dx;
        double novoY = jogadorY + dy;

        // ===== HITBOX =====
        double hitX = novoX + HITBOX_OFFSET_X;
        double hitY = novoY + HITBOX_OFFSET_Y;

        double maxX = mapa.getLarguraPixels() - HITBOX_LARGURA;
        double maxY = mapa.getAlturaPixels() - HITBOX_ALTURA;

        if (hitX < 0) novoX = -HITBOX_OFFSET_X;
        if (hitY < 0) novoY = -HITBOX_OFFSET_Y;
        if (hitX > maxX) novoX = maxX - HITBOX_OFFSET_X;
        if (hitY > maxY) novoY = maxY - HITBOX_OFFSET_Y;

        jogadorX = novoX;
        jogadorY = novoY;

        // ===== ANIMAÇÃO =====
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

    private void renderizar() {

        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, TELA_LARGURA, TELA_ALTURA);

        Image frame = frames[frameAtual];
        double larguraJogador = frame.getWidth() * ESCALA_JOGADOR;
        double alturaJogador  = frame.getHeight() * ESCALA_JOGADOR;

        double cameraW = TELA_LARGURA / ZOOM;
        double cameraH = TELA_ALTURA  / ZOOM;

        double cameraX = jogadorX + larguraJogador / 2 - cameraW / 2;
        double cameraY = jogadorY + alturaJogador  / 2 - cameraH / 2;

        double maxCameraX = mapa.getLarguraPixels() - cameraW;
        double maxCameraY = mapa.getAlturaPixels() - cameraH;

        if (cameraX < 0) cameraX = 0;
        if (cameraY < 0) cameraY = 0;
        if (cameraX > maxCameraX) cameraX = maxCameraX;
        if (cameraY > maxCameraY) cameraY = maxCameraY;

        gc.save();
        gc.scale(ZOOM, ZOOM);
        gc.translate(-Math.floor(cameraX), -Math.floor(cameraY));

        // MAPA
        mapa.desenhar(gc);

        // JOGADOR
        if (olhandoDireita) {
            gc.drawImage(frame, jogadorX, jogadorY, larguraJogador, alturaJogador);
        } else {
            gc.drawImage(
                    frame,
                    jogadorX + larguraJogador,
                    jogadorY,
                    -larguraJogador,
                    alturaJogador
            );
        }

        gc.restore();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
