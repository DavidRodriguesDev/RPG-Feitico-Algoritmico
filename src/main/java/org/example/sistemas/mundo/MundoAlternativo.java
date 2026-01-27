package org.example.sistemas.mundo;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import java.util.Random;

public class MundoAlternativo {

    private MapaTiled mapaOriginal;
    private boolean ativo = false;
    private boolean espelhado = true;

    // Efeitos visuais
    private Color corFiltro = Color.rgb(128, 0, 255, 0.3);
    private Color corNevoa = Color.rgb(75, 0, 130, 0.1);

    // Partículas
    private Particula[] particulas;
    private Random random = new Random();

    // ===== BUG NO MUNDO ALTERNATIVO =====
    private Image spriteBugMundo;
    private double bugX, bugY;
    private double bugLargura = 50;
    private double bugAltura = 50;
    private boolean bugVisivel = true;

    // Classe interna para partículas
    private class Particula {
        double x, y;
        double velocidadeX, velocidadeY;
        double tamanho;
        Color cor;
        double opacidade;

        Particula(double larguraMapa, double alturaMapa) {
            reset(larguraMapa, alturaMapa);
        }

        void reset(double larguraMapa, double alturaMapa) {
            x = random.nextDouble() * larguraMapa;
            y = random.nextDouble() * alturaMapa;
            velocidadeX = (random.nextDouble() - 0.5) * 0.5;
            velocidadeY = (random.nextDouble() - 0.5) * 0.3;
            tamanho = 1 + random.nextDouble() * 3;
            cor = Color.rgb(
                    180 + random.nextInt(50),
                    0,
                    255,
                    0.3 + random.nextDouble() * 0.4
            );
            opacidade = 0.3 + random.nextDouble() * 0.7;
        }

        void atualizar(double dt, double larguraMapa, double alturaMapa) {
            x += velocidadeX * dt * 60;
            y += velocidadeY * dt * 60;

            // Wrap around
            if (x < 0) x = larguraMapa;
            if (x > larguraMapa) x = 0;
            if (y < 0) y = alturaMapa;
            if (y > alturaMapa) y = 0;

            // Pulsação suave
            opacidade = 0.3 + 0.4 * Math.sin(System.currentTimeMillis() * 0.001 + x + y);
        }

        void desenhar(GraphicsContext gc) {
            gc.setFill(cor.deriveColor(0, 1, 1, opacidade));
            gc.fillOval(x, y, tamanho, tamanho);
        }
    }

    public MundoAlternativo(MapaTiled mapaOriginal) {
        this.mapaOriginal = mapaOriginal;
        inicializarParticulas();
        inicializarBug();
    }

    private void inicializarParticulas() {
        int numParticulas = 50;
        particulas = new Particula[numParticulas];
        for (int i = 0; i < numParticulas; i++) {
            particulas[i] = new Particula(
                    mapaOriginal.getLarguraPixels(),
                    mapaOriginal.getAlturaPixels()
            );
        }
    }

    private void inicializarBug() {
        try {
            spriteBugMundo = new Image(getClass().getResourceAsStream("/sprites/bugjogo.png"));
            bugX = mapaOriginal.getLarguraPixels() / 2 - bugLargura / 2;
            bugY = mapaOriginal.getAlturaPixels() / 3;
        } catch (Exception e) {
            System.err.println("Erro ao carregar sprite do bug: " + e.getMessage());
            spriteBugMundo = null;
        }
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
        if (ativo) {
            bugVisivel = true;
        }
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setEspelhado(boolean espelhado) {
        this.espelhado = espelhado;
    }

    public void atualizar(double dt) {
        if (!ativo) return;

        for (Particula p : particulas) {
            if (p != null) {
                p.atualizar(dt,
                        mapaOriginal.getLarguraPixels(),
                        mapaOriginal.getAlturaPixels()
                );
            }
        }
    }

    public void desenharBase(GraphicsContext gc) {
        if (!ativo) {
            mapaOriginal.desenharBase(gc);
            return;
        }

        if (espelhado) {
            gc.save();
            gc.scale(-1, 1);
            gc.translate(-mapaOriginal.getLarguraPixels(), 0);
        }

        mapaOriginal.desenharBase(gc);

        if (espelhado) {
            gc.restore();
        }

        gc.setFill(corFiltro);
        gc.fillRect(0, 0,
                mapaOriginal.getLarguraPixels(),
                mapaOriginal.getAlturaPixels()
        );

        gc.setFill(corNevoa);
        gc.fillRect(0, 0,
                mapaOriginal.getLarguraPixels(),
                mapaOriginal.getAlturaPixels()
        );

        for (Particula p : particulas) {
            if (p != null) {
                p.desenhar(gc);
            }
        }
    }

    public void desenharTopo(GraphicsContext gc) {
        if (!ativo) {
            mapaOriginal.desenharTopo(gc);
            return;
        }

        if (espelhado) {
            gc.save();
            gc.scale(-1, 1);
            gc.translate(-mapaOriginal.getLarguraPixels(), 0);
        }

        mapaOriginal.desenharTopo(gc);

        if (bugVisivel && ativo) {
            desenharBug(gc);
        }

        if (espelhado) {
            gc.restore();
        }
    }

    private void desenharBug(GraphicsContext gc) {
        if (spriteBugMundo != null) {
            gc.drawImage(spriteBugMundo, bugX, bugY, bugLargura, bugAltura);
        } else {
            // Fallback
            gc.setFill(Color.rgb(255, 0, 255, 0.8));
            gc.fillRect(bugX, bugY, bugLargura, bugAltura);

            gc.setStroke(Color.WHITE);
            gc.setLineWidth(2);
            gc.strokeRect(bugX, bugY, bugLargura, bugAltura);

            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font("Consolas", 10));
            gc.fillText("BUG", bugX + 10, bugY + 25);
        }
    }

    public void desenharNPCs(GraphicsContext gc) {
        if (!ativo) {
            mapaOriginal.desenharNPCs(gc);
        }
    }

    public boolean isSolido(int tileX, int tileY) {
        return mapaOriginal.isSolido(tileX, tileY);
    }

    public int getLarguraPixels() {
        return mapaOriginal.getLarguraPixels();
    }

    public int getAlturaPixels() {
        return mapaOriginal.getAlturaPixels();
    }

    public int getTileSize() {
        return mapaOriginal.getTileSize();
    }

    public boolean jogadorPertoBug(double px, double py, double pw, double ph, double margemPersonalizada) {
        if (!ativo || !bugVisivel) return false;

        return px < bugX + bugLargura + margemPersonalizada &&
                px + pw > bugX - margemPersonalizada &&
                py < bugY + bugAltura + margemPersonalizada &&
                py + ph > bugY - margemPersonalizada;
    }

    public void iniciarBatalhaComBug() {
        bugVisivel = false;
    }

    public void resetarBug() {
        bugVisivel = true;
    }

    public void setPosicaoBug(double x, double y) {
        this.bugX = x;
        this.bugY = y;
    }
}
