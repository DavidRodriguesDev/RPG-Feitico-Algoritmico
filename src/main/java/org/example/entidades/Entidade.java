package org.example.entidades;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Classe base abstrata para todas as entidades do jogo
 */
public abstract class Entidade {
    protected double x;
    protected double y;
    protected double largura;
    protected double altura;
    protected Image sprite;

    public Entidade(double x, double y, Image sprite) {
        this.x = x;
        this.y = y;
        this.sprite = sprite;
    }

    public Entidade(double x, double y, double largura, double altura, Image sprite) {
        this.x = x;
        this.y = y;
        this.largura = largura;
        this.altura = altura;
        this.sprite = sprite;
    }

    /**
     * Desenha a entidade na tela
     */
    public abstract void desenhar(GraphicsContext gc);

    /**
     * Atualiza a entidade
     */
    public void atualizar(double dt) {
        // Implementação padrão vazia - pode ser sobrescrita
    }

    // Getters e Setters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getLargura() { return largura; }
    public double getAltura() { return altura; }
    public Image getSprite() { return sprite; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setSprite(Image sprite) { this.sprite = sprite; }
}
