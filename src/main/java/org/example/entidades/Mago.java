package org.example.entidades;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Classe que representa o Mago (inimigo do tutorial)
 */
public class Mago extends Inimigo {
    
    public Mago() {
        super(0, 0, carregarSprite(), "Mago", 100, 33);
        this.largura = 200;
        this.altura = 200;
    }

    private static Image carregarSprite() {
        try {
            return new Image(Mago.class.getResourceAsStream("/sprites/magonpc.png"));
        } catch (Exception e) {
            System.err.println("Erro ao carregar sprite do mago: " + e.getMessage());
            return null;
        }
    }

    @Override
    public int atacar() {
        return danoBase; // Mago causa 33 de dano
    }

    @Override
    public void desenhar(GraphicsContext gc) {
        if (sprite != null) {
            gc.drawImage(sprite, 640, 100, largura, altura);
        } else {
            // Fallback caso sprite não carregue
            gc.setFill(javafx.scene.paint.Color.rgb(100, 0, 200));
            gc.fillOval(640, 100, largura, altura);
        }
    }
}
