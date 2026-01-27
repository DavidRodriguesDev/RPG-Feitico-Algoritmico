package org.example.entidades;

import javafx.scene.image.Image;

/**
 * Classe abstrata para inimigos
 */
public abstract class Inimigo extends Personagem {
    protected int danoBase;

    public Inimigo(double x, double y, Image sprite, String nome, int vidaMaxima, int danoBase) {
        super(x, y, sprite, nome, vidaMaxima);
        this.danoBase = danoBase;
    }

    public Inimigo(double x, double y, double largura, double altura, Image sprite, String nome, int vidaMaxima, int danoBase) {
        super(x, y, largura, altura, sprite, nome, vidaMaxima);
        this.danoBase = danoBase;
    }

    /**
     * Realiza um ataque
     */
    public abstract int atacar();

    // Getters e Setters
    public int getDanoBase() { return danoBase; }
    public void setDanoBase(int danoBase) { this.danoBase = danoBase; }
}
