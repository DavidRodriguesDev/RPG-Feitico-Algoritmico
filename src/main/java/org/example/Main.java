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
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {

    // ===== TELA =====
    private static final int TELA_LARGURA = 1024;
    private static final int TELA_ALTURA  = 768;
    private static final double ZOOM = 3.0;

    // ===== JOGADOR =====
    private double jogadorX = 100;
    private double jogadorY = 270;

    private static final double VELOCIDADE_ANDAR = 70;
    private static final double VELOCIDADE_CORRER = 100;

    private static final double HITBOX_LARGURA  = 12;
    private static final double HITBOX_ALTURA   = 14;
    private static final double HITBOX_OFFSET_X = 32.5;
    private static final double HITBOX_OFFSET_Y = 18;

    // ===== SISTEMA =====
    private Canvas canvas;
    private GraphicsContext gc;
    private MapaTiled mapa;

    // ===== SPRITE =====
    private Image spriteSheet;
    private Image[] frames = new Image[8];
    private int frameAtual = 0;
    private double tempoAnimacao = 0;
    private static final double FPS_ANIMACAO = 10;
    private boolean olhandoDireita = true;

    // ===== INPUT =====
    private boolean up, down, left, right, correr;

    // ===== DIÁLOGO =====
    private boolean emDialogo = false;
    private String textoDialogo = "";
    private NPC npcAtual = null;

    @Override
    public void start(Stage stage) {

        mapa = new MapaTiled("/map/mapa.tmx");

        canvas = new Canvas(TELA_LARGURA, TELA_ALTURA);
        gc = canvas.getGraphicsContext2D();
        gc.setImageSmoothing(false);

        Scene scene = new Scene(new StackPane(canvas));
        configurarInput(scene);

        stage.setScene(scene);
        stage.setTitle("RPG");
        stage.setResizable(false);
        stage.show();

        carregarSprites();

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

    // ======================================================
    // ===================== ATUALIZA =======================
    // ======================================================

    private void atualizar(double dt) {

        if (emDialogo) {
            atualizarAnimacao(dt, false);
            return;
        }

        double velocidade = correr ? VELOCIDADE_CORRER : VELOCIDADE_ANDAR;

        double dx = 0, dy = 0;
        boolean movendo = false;

        if (up)    { dy -= velocidade * dt; movendo = true; }
        if (down)  { dy += velocidade * dt; movendo = true; }
        if (left)  { dx -= velocidade * dt; olhandoDireita = false; movendo = true; }
        if (right) { dx += velocidade * dt; olhandoDireita = true;  movendo = true; }

        moverX(dx);
        moverY(dy);
        atualizarAnimacao(dt, movendo);
    }

    private void moverX(double dx) {
        double novoX = jogadorX + dx;
        double hx = novoX + HITBOX_OFFSET_X;
        double hy = jogadorY + HITBOX_OFFSET_Y;

        if (!colideMapa(hx, hy) && !colideNPC(hx, hy)) {
            jogadorX = novoX;
        }
    }

    private void moverY(double dy) {
        double novoY = jogadorY + dy;
        double hx = jogadorX + HITBOX_OFFSET_X;
        double hy = novoY + HITBOX_OFFSET_Y;

        if (!colideMapa(hx, hy) && !colideNPC(hx, hy)) {
            jogadorY = novoY;
        }
    }

    private boolean colideMapa(double hitX, double hitY) {
        int t = mapa.getTileSize();

        int l = (int) (hitX / t);
        int r = (int) ((hitX + HITBOX_LARGURA - 1) / t);
        int u = (int) (hitY / t);
        int d = (int) ((hitY + HITBOX_ALTURA - 1) / t);

        return mapa.isSolido(l,u) || mapa.isSolido(r,u) ||
                mapa.isSolido(l,d) || mapa.isSolido(r,d);
    }

    private boolean colideNPC(double hitX, double hitY) {
        for (NPC npc : mapa.getNPCs()) {
            if (npc.colidiu(hitX, hitY, HITBOX_LARGURA, HITBOX_ALTURA)) {
                return true;
            }
        }
        return false;
    }

    private void atualizarAnimacao(double dt, boolean movendo) {
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

    // ======================================================
    // ===================== DIÁLOGO ========================
    // ======================================================

    private void alternarDialogo() {

        if (emDialogo) {
            emDialogo = false;
            npcAtual = null;
            return;
        }

        double px = jogadorX + HITBOX_OFFSET_X;
        double py = jogadorY + HITBOX_OFFSET_Y;

        for (NPC npc : mapa.getNPCs()) {
            if (npc.jogadorPerto(px, py, HITBOX_LARGURA, HITBOX_ALTURA)) {
                emDialogo = true;
                npcAtual = npc;
                textoDialogo = "Olá, aventureiro!";
                return;
            }
        }
    }

    // ======================================================
    // ===================== RENDER =========================
    // ======================================================

    private void renderizar() {

        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, TELA_LARGURA, TELA_ALTURA);

        Image frame = frames[frameAtual];
        double w = frame.getWidth() * 0.8;
        double h = frame.getHeight() * 0.8;

        double camW = TELA_LARGURA / ZOOM;
        double camH = TELA_ALTURA  / ZOOM;

        double camX = jogadorX + w / 2 - camW / 2;
        double camY = jogadorY + h / 2 - camH / 2;

        camX = Math.max(0, Math.min(camX, mapa.getLarguraPixels() - camW));
        camY = Math.max(0, Math.min(camY, mapa.getAlturaPixels()  - camH));

        gc.save();
        gc.scale(ZOOM, ZOOM);
        gc.translate(-Math.floor(camX), -Math.floor(camY));

        mapa.desenharBase(gc);
        mapa.desenharNPCs(gc);

        if (olhandoDireita) {
            gc.drawImage(frame, jogadorX, jogadorY, w, h);
        } else {
            gc.drawImage(frame, jogadorX + w, jogadorY, -w, h);
        }

        mapa.desenharTopo(gc);
        gc.restore();

        if (emDialogo) {
            desenharCaixaDialogo();
        }
    }

    private void desenharCaixaDialogo() {

        double margem = 20;
        double altura = 120;

        gc.setFill(Color.rgb(0, 0, 0, 0.85));
        gc.fillRect(margem, TELA_ALTURA - altura - margem,
                TELA_LARGURA - margem * 2, altura);

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeRect(margem, TELA_ALTURA - altura - margem,
                TELA_LARGURA - margem * 2, altura);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(18));
        gc.fillText(textoDialogo, margem + 20, TELA_ALTURA - altura + 40);
    }

    // ======================================================
    // ===================== INPUT ==========================
    // ======================================================

    private void configurarInput(Scene scene) {

        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case W, UP -> up = true;
                case S, DOWN -> down = true;
                case A, LEFT -> left = true;
                case D, RIGHT -> right = true;
                case SHIFT -> correr = true;
                case E -> alternarDialogo();
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
    }

    // ======================================================
    // ===================== SETUP ==========================
    // ======================================================

    private void carregarSprites() {
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
    }

    public static void main(String[] args) {
        launch(args);
    }
}
