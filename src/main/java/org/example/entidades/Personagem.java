package org.example.entidades;

import javafx.scene.image.Image;

/**
 * Classe abstrata para personagens que possuem vida
 */
public abstract class Personagem extends Entidade {
    protected String nome;
    protected int vida;
    protected int vidaMaxima;

    public Personagem(double x, double y, Image sprite, String nome, int vidaMaxima) {
        super(x, y, sprite);
        this.nome = nome;
        this.vidaMaxima = vidaMaxima;
        this.vida = vidaMaxima;
    }

    public Personagem(double x, double y, double largura, double altura, Image sprite, String nome, int vidaMaxima) {
        super(x, y, largura, altura, sprite);
        this.nome = nome;
        this.vidaMaxima = vidaMaxima;
        this.vida = vidaMaxima;
    }

    /**
     * Aplica dano ao personagem
     */
    public void receberDano(int dano) {
        vida -= dano;
        if (vida < 0) vida = 0;
    }

    /**
     * Cura o personagem
     */
    public void curar(int quantidade) {
        vida += quantidade;
        if (vida > vidaMaxima) vida = vidaMaxima;
    }

    /**
     * Verifica se o personagem está vivo
     */
    public boolean estaVivo() {
        return vida > 0;
    }

    /**
     * Reseta a vida ao máximo
     */
    public void resetarVida() {
        vida = vidaMaxima;
    }

    // Getters e Setters
    public String getNome() { return nome; }
    public int getVida() { return vida; }
    public int getVidaMaxima() { return vidaMaxima; }
    
    public void setVida(int vida) { 
        this.vida = vida;
        if (this.vida < 0) this.vida = 0;
        if (this.vida > vidaMaxima) this.vida = vidaMaxima;
    }
    
    public void setVidaMaxima(int vidaMaxima) { 
        this.vidaMaxima = vidaMaxima; 
    }
}
