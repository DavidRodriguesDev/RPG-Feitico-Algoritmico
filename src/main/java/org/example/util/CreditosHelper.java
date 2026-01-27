package org.example.util;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import java.util.List;

public class CreditosHelper {

    public static void renderizar(GraphicsContext gc,
                                  List<String> linhas,
                                  double scroll,
                                  boolean esperandoReinicio,
                                  double tempoEspera) {

        int largura = Constantes.TELA_LARGURA;
        int altura = Constantes.TELA_ALTURA;

        // Fundo
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, largura, altura);

        // Estrelas
        for (int i = 0; i < 100; i++) {
            double x = (System.currentTimeMillis() * 0.1 + i * 100) % largura;
            double y = (i * 20 + scroll * 0.5) % altura;
            double size = 1 + Math.sin(System.currentTimeMillis() * 0.001 + i) * 0.5;

            gc.setFill(Color.rgb(255, 255, 255,
                    0.5 + Math.sin(System.currentTimeMillis() * 0.002 + i) * 0.3));
            gc.fillOval(x, y, size, size);
        }

        // Título
        gc.setFill(Color.YELLOW);
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 48));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("FIM DA JORNADA", largura/2, scroll);

        // Créditos
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Consolas", 24));

        for (int i = 0; i < linhas.size(); i++) {
            double y = scroll + 100 + i * 40;

            if (linhas.get(i).contains("CRÉDITOS") ||
                    linhas.get(i).contains("DESENVOLVIMENTO") ||
                    linhas.get(i).contains("AGRADECIMENTOS") ||
                    linhas.get(i).contains("OBRIGADO")) {

                gc.setFill(Color.CYAN);
                gc.setFont(Font.font("Consolas", FontWeight.BOLD, 28));
            } else {
                gc.setFill(Color.WHITE);
                gc.setFont(Font.font("Consolas", 24));
            }

            gc.fillText(linhas.get(i), largura/2, y);
        }

        // Instrução
        if (esperandoReinicio) {
            double alpha = Math.sin(tempoEspera * 2) * 0.5 + 0.5;
            gc.setFill(Color.rgb(255, 255, 0, alpha));
            gc.setFont(Font.font("Consolas", FontWeight.BOLD, 32));
            gc.fillText("Pressione ESPAÇO para reiniciar", largura/2, altura - 100);
        }

        gc.setTextAlign(TextAlignment.LEFT);
    }
}