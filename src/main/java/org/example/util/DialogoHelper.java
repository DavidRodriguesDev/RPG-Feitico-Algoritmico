package org.example.util;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import java.util.ArrayList;
import java.util.List;

public class DialogoHelper {

    public static void renderizarCaixaDialogo(GraphicsContext gc,
                                              String texto,
                                              int indice,
                                              int total) {
        int margem = 20;
        int altura = 150;
        int larguraTela = Constantes.TELA_LARGURA;
        int alturaTela = Constantes.TELA_ALTURA;

        gc.setFill(Color.rgb(0, 0, 0, 0.85));
        gc.fillRect(margem, alturaTela - altura - margem,
                larguraTela - margem * 2, altura);

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeRect(margem, alturaTela - altura - margem,
                larguraTela - margem * 2, altura);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(18));

        String[] linhas = quebrarTexto(texto, 80);
        int y = alturaTela - altura + 40;

        for (String linha : linhas) {
            gc.fillText(linha, margem + 20, y);
            y += 30;
        }

        gc.setFont(Font.font(14));
        gc.setFill(Color.YELLOW);
        gc.fillText("Pressione E para continuar", margem + 20,
                alturaTela - margem - 20);

        gc.setFill(Color.GRAY);
        gc.fillText((indice + 1) + "/" + total,
                larguraTela - margem - 50, alturaTela - margem - 20);
    }

    public static String[] quebrarTexto(String texto, int maxCaracteres) {
        List<String> linhas = new ArrayList<>();
        String[] palavras = texto.split(" ");
        StringBuilder linhaAtual = new StringBuilder();

        for (String palavra : palavras) {
            if (linhaAtual.length() + palavra.length() + 1 > maxCaracteres) {
                linhas.add(linhaAtual.toString());
                linhaAtual = new StringBuilder();
            }
            if (linhaAtual.length() > 0) {
                linhaAtual.append(" ");
            }
            linhaAtual.append(palavra);
        }

        if (linhaAtual.length() > 0) {
            linhas.add(linhaAtual.toString());
        }

        return linhas.toArray(new String[0]);
    }
}