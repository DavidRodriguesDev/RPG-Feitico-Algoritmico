package org.example.entidades;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Classe que representa o Bug (inimigo final)
 */
public class Bug extends Inimigo {
    
    public Bug() {
        super(0, 0, carregarSpriteBatalha(), "BUG", 100, 33);
        this.largura = 200;
        this.altura = 200;
    }

    private static Image carregarSpriteBatalha() {
        try {
            return new Image(Bug.class.getResourceAsStream("/sprites/bugbatalha.png"));
        } catch (Exception e) {
            System.err.println("Erro ao carregar sprite do bug batalha: " + e.getMessage());
            return null;
        }
    }

    @Override
    public int atacar() {
        return danoBase; // Bug causa 33 de dano
    }

    @Override
    public void desenhar(GraphicsContext gc) {
        if (sprite != null) {
            gc.drawImage(sprite, 640, 100, largura, altura);
        } else {
            // Fallback caso sprite não carregue
            gc.setFill(javafx.scene.paint.Color.rgb(255, 0, 128));
            gc.fillRect(640, 100, largura, altura);

            gc.setFill(javafx.scene.paint.Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font("Consolas", javafx.scene.text.FontWeight.BOLD, 48));
            gc.fillText("BUG", 690, 200);
        }
    }
}
