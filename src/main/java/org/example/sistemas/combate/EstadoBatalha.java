package org.example.sistemas.combate;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.image.Image;
import org.example.Main;
import org.example.entidades.Inimigo;
import org.example.entidades.Mago;
import org.example.entidades.Bug;
import org.example.util.Constantes;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EstadoBatalha {
    // Estados da batalha
    private enum Estado {
        FADE_IN,
        CONTAGEM,
        BATALHA,
        FADE_OUT,
        GAME_OVER,
        VITORIA,
        CENA_FINAL
    }

    private Estado estadoAtual = Estado.FADE_IN;
    private double alpha = 0.0;
    private double tempoContagem = 3.0;
    private int contagemAtual = 3;
    private double tempoEstado = 0.0;
    private double tempoResultado = 0.0;

    // ===== MUDANÇA PRINCIPAL: Usar Inimigo em vez de variáveis separadas =====
    private Inimigo inimigoAtual;

    // Tempo
    private double tempoLimite = 60.0;
    private double tempoAtual = 60.0;
    private double porcentagemDano = 100.0;

    // Questão atual
    private BancoQuestoes.Questao questaoAtual;
    private List<String> linhasCodigo = new ArrayList<>();
    private int linhaComLacuna = -1;
    private int posicaoLacuna = -1;
    private String textoAntesLacuna = "";
    private String textoDepoisLacuna = "";

    // Input do jogador
    private StringBuilder entradaJogador = new StringBuilder();

    // Jogador
    private String nomeJogador = "PLAYER";
    private int vidaMaximaJogador = Constantes.VIDA_MAXIMA_JOGADOR;
    private int vidaAtualJogador = Constantes.VIDA_MAXIMA_JOGADOR;

    // Sistema de questões
    private BancoQuestoes bancoQuestoes;

    // Estado do cursor
    private boolean mostrarCursor = true;
    private double tempoCursor = 0;

    // Feedback visual
    private String feedbackMensagem = "";
    private double feedbackTempo = 0;
    private Color feedbackCor = Color.WHITE;

    // Dica da questão
    private boolean mostrarDica = false;
    private double tempoDica = 0;

    // Modo tutorial
    private boolean modoTutorial = false;
    private List<String> dicasTutorial = new ArrayList<>();
    private int indiceDicaTutorial = 0;
    private double tempoDicaTutorial = 0;
    private boolean mostrarDicaTutorial = false;

    // Referência ao main
    private Main main;

    // Flag para controle de game over
    private boolean resultadoGameOver = false;
    private boolean vitoriaTutorial = false;

    // Sprite do player em batalha
    private Image spritePlayerBatalha;

    // Parâmetros de tamanho do sprite do player
    private double playerPosX = 720;
    private double playerPosY = 420;
    private double playerLargura = 120;
    private double playerAltura = 150;

    // Efeitos visuais de dano
    private double tempoDanoPlayer = 0;
    private double tempoDanoInimigo = 0;

    // Random para efeitos
    private Random random = new Random();

    // Variáveis para cena final
    private List<String> dialogoFinal = new ArrayList<>();
    private int indiceDialogoFinal = 0;
    private boolean dialogoAtivo = false;
    private double alphaFadeOutCena = 0.0;
    private boolean iniciandoCreditos = false;

    public EstadoBatalha(Main main) {
        this.main = main;
        this.bancoQuestoes = new BancoQuestoes();

        try {
            spritePlayerBatalha = new Image(getClass().getResourceAsStream("/sprites/playerbatalha.png"));
        } catch (Exception e) {
            System.err.println("Erro ao carregar sprite do player: " + e.getMessage());
            e.printStackTrace();
        }

        inicializarDicasTutorial();
        inicializarDialogoFinal();
    }

    private void inicializarDicasTutorial() {
        dicasTutorial.add("TUTORIAL: Você tem 60 segundos para resolver cada questão.");
        dicasTutorial.add("TUTORIAL: Responda nos primeiros 15 segundos para causar 100% de dano!");
        dicasTutorial.add("TUTORIAL: O dano diminui entre 15-20 segundos, e zera após 20s.");
        dicasTutorial.add("TUTORIAL: Cada erro ou tempo esgotado tira 33 de sua vida.");
        dicasTutorial.add("TUTORIAL: Complete a lacuna no código para atacar o Mago!");
        dicasTutorial.add("TUTORIAL: Pressione H para ver dicas específicas da questão.");
        dicasTutorial.add("TUTORIAL: Derrote o Mago para completar o tutorial!");
    }

    private void inicializarDialogoFinal() {
        dialogoFinal.clear();
        dialogoFinal.add("Parabéns, jovem programador!");
        dialogoFinal.add("Você conseguiu! Você derrotou o BUG mais perigoso!");
        dialogoFinal.add("Com essa vitória, o equilíbrio foi restaurado.");
        dialogoFinal.add("A anomalia que você causou foi corrigida.");
        dialogoFinal.add("Você provou ser digno de voltar ao seu mundo.");
        dialogoFinal.add("Sua jornada aqui chegou ao fim.");
        dialogoFinal.add("Leve consigo as lições aprendidas:");
        dialogoFinal.add("Um bom código é como uma boa magia -");
        dialogoFinal.add("requer precisão, clareza e cuidado.");
        dialogoFinal.add("Agora, é hora de voltar para casa.");
        dialogoFinal.add("Farewell, aventureiro!");
    }

    public boolean isAtivo() {
        return estadoAtual != Estado.FADE_OUT;
    }

    public boolean isGameOver() {
        return resultadoGameOver;
    }

    public void iniciarBatalha(boolean tutorial) {
        this.modoTutorial = tutorial;
        estadoAtual = Estado.FADE_IN;
        resultadoGameOver = false;
        vitoriaTutorial = false;
        alpha = 0.0;
        tempoContagem = 3.0;
        contagemAtual = 3;
        tempoEstado = 0.0;
        tempoResultado = 0.0;

        indiceDicaTutorial = 0;
        tempoDicaTutorial = 0;
        mostrarDicaTutorial = true;

        tempoDanoPlayer = 0;
        tempoDanoInimigo = 0;

        // ===== MUDANÇA: Criar o inimigo apropriado =====
        if (modoTutorial) {
            inimigoAtual = new Mago();
        } else {
            inimigoAtual = new Bug();
        }

        vidaAtualJogador = vidaMaximaJogador;
        tempoAtual = tempoLimite;
        porcentagemDano = modoTutorial ? Constantes.DANO_MAXIMO_TUTORIAL : Constantes.DANO_MAXIMO_BUG;

        selecionarNovaQuestao();

        if (modoTutorial) {
            mostrarFeedback("TUTORIAL INICIADO! Derrote o Mago para continuar.", Color.YELLOW);
        } else {
            mostrarFeedback("Batalha Iniciada! Dano máximo: 50%", Color.YELLOW);
        }
    }

    public void iniciarBatalhaComBug() {
        this.modoTutorial = false;
        estadoAtual = Estado.FADE_IN;
        resultadoGameOver = false;
        vitoriaTutorial = false;
        alpha = 0.0;
        tempoContagem = 3.0;
        contagemAtual = 3;
        tempoEstado = 0.0;
        tempoResultado = 0.0;

        mostrarDicaTutorial = false;

        tempoDanoPlayer = 0;
        tempoDanoInimigo = 0;

        // ===== MUDANÇA: Criar Bug =====
        inimigoAtual = new Bug();

        vidaAtualJogador = vidaMaximaJogador;
        tempoAtual = tempoLimite;
        porcentagemDano = Constantes.DANO_MAXIMO_BUG;

        selecionarNovaQuestao();

        mostrarFeedback("BUG ENCONTRADO! Dano máximo: 50%", Color.RED);
    }

    private void selecionarNovaQuestao() {
        questaoAtual = bancoQuestoes.getQuestaoAleatoria();

        linhasCodigo.clear();
        linhaComLacuna = -1;
        posicaoLacuna = -1;
        textoAntesLacuna = "";
        textoDepoisLacuna = "";

        String[] linhas = questaoAtual.getCodigoComLacuna().split("\n");
        for (int i = 0; i < linhas.length; i++) {
            String linha = linhas[i];
            linhasCodigo.add(linha);

            if (linha.contains("_____")) {
                linhaComLacuna = i;
                posicaoLacuna = linha.indexOf("_____");
                textoAntesLacuna = linha.substring(0, posicaoLacuna);
                textoDepoisLacuna = linha.substring(posicaoLacuna + 5);
            }
        }

        entradaJogador = new StringBuilder();
        mostrarDica = false;
        tempoDica = 0;

        tempoAtual = tempoLimite;
    }

    public void atualizar(double dt) {
        tempoEstado += dt;

        if (tempoDanoPlayer > 0) {
            tempoDanoPlayer -= dt;
            if (tempoDanoPlayer < 0) tempoDanoPlayer = 0;
        }

        if (tempoDanoInimigo > 0) {
            tempoDanoInimigo -= dt;
            if (tempoDanoInimigo < 0) tempoDanoInimigo = 0;
        }

        switch (estadoAtual) {
            case FADE_IN:
                alpha += dt * 2;
                if (alpha >= 1.0) {
                    alpha = 1.0;
                    estadoAtual = Estado.CONTAGEM;
                    tempoEstado = 0.0;
                }
                break;

            case CONTAGEM:
                tempoContagem -= dt;
                if (tempoContagem <= 0) {
                    estadoAtual = Estado.BATALHA;
                    tempoEstado = 0.0;
                } else {
                    int novaContagem = (int)Math.ceil(tempoContagem);
                    if (novaContagem != contagemAtual) {
                        contagemAtual = novaContagem;
                    }
                }
                break;

            case BATALHA:
                atualizarBatalha(dt);
                break;

            case GAME_OVER:
                tempoResultado += dt;
                if (tempoResultado >= 3.0) {
                    estadoAtual = Estado.FADE_OUT;
                    alpha = 1.0;
                    tempoEstado = 0.0;
                }
                break;

            case VITORIA:
                tempoResultado += dt;
                if (tempoResultado >= 3.0) {
                    estadoAtual = Estado.FADE_OUT;
                    alpha = 1.0;
                    tempoEstado = 0.0;
                }
                break;

            case CENA_FINAL:
                atualizarCenaFinal(dt);
                break;

            case FADE_OUT:
                alpha -= dt * 2;
                if (alpha <= 0.0) {
                    alpha = 0.0;
                    main.finalizarBatalha(resultadoGameOver, vitoriaTutorial);
                }
                break;
        }
    }

    private void atualizarBatalha(double dt) {
        tempoAtual -= dt;
        calcularPorcentagemDano();

        if (modoTutorial && mostrarDicaTutorial) {
            tempoDicaTutorial += dt;
            if (tempoDicaTutorial >= 8.0) {
                tempoDicaTutorial = 0;
                indiceDicaTutorial = (indiceDicaTutorial + 1) % dicasTutorial.size();
            }
        }

        if (mostrarDica) {
            tempoDica -= dt;
            if (tempoDica <= 0) {
                mostrarDica = false;
            }
        }

        if (!mostrarDica && tempoAtual < 30 && !questaoAtual.getDica().isEmpty()) {
            mostrarDica = true;
            tempoDica = 5.0;
        }

        if (tempoAtual <= 0) {
            tempoAtual = 0;
            aplicarDanoPlayer();

            if (vidaAtualJogador <= 0) {
                vidaAtualJogador = 0;
                resultadoGameOver = true;
                estadoAtual = Estado.GAME_OVER;
                tempoResultado = 0.0;
            } else {
                selecionarNovaQuestao();
            }
        }

        tempoCursor += dt;
        if (tempoCursor >= 0.5) {
            tempoCursor = 0;
            mostrarCursor = !mostrarCursor;
        }

        if (feedbackTempo > 0) {
            feedbackTempo -= dt;
        }
    }

    private void atualizarCenaFinal(double dt) {
        if (dialogoAtivo) {
            // Espera input
        } else {
            alphaFadeOutCena += dt * 0.5;
            if (alphaFadeOutCena >= 1.0) {
                alphaFadeOutCena = 1.0;
                if (!iniciandoCreditos) {
                    iniciandoCreditos = true;
                    main.iniciarCreditosAposBatalha();
                }
            }
        }
    }

    private void aplicarDanoPlayer() {
        vidaAtualJogador -= Constantes.DANO_ERRO;
        tempoDanoPlayer = Constantes.DURACAO_DANO_VISUAL;
        mostrarFeedback("Tempo esgotado! -33 HP", Color.RED);
    }

    private void calcularPorcentagemDano() {
        double tempoRestante = tempoAtual;
        double danoMaximo = modoTutorial ? Constantes.DANO_MAXIMO_TUTORIAL : Constantes.DANO_MAXIMO_BUG;

        if (tempoRestante > 45) {
            porcentagemDano = danoMaximo;
        } else if (tempoRestante > 20) {
            double progresso = (45 - tempoRestante) / 25.0;
            porcentagemDano = danoMaximo * (1.0 - progresso);
        } else {
            porcentagemDano = 0.0;
        }

        if (porcentagemDano < 0) porcentagemDano = 0;
        if (porcentagemDano > danoMaximo) porcentagemDano = danoMaximo;
    }

    private void mostrarFeedback(String mensagem, Color cor) {
        if (!modoTutorial && mensagem.contains("Correto!")) {
            mensagem += " (Dano máximo: 50%)";
        }
        feedbackMensagem = mensagem;
        feedbackTempo = 2.0;
        feedbackCor = cor;
    }

    public void renderizar(GraphicsContext gc) {
        if (estadoAtual != Estado.GAME_OVER && estadoAtual != Estado.VITORIA && estadoAtual != Estado.CENA_FINAL) {
            renderizarBatalha(gc);
        }

        switch (estadoAtual) {
            case FADE_IN:
            case FADE_OUT:
                renderizarFade(gc);
                break;

            case CONTAGEM:
                renderizarContagem(gc);
                break;

            case GAME_OVER:
                renderizarGameOver(gc);
                break;

            case VITORIA:
                renderizarVitoria(gc);
                break;

            case CENA_FINAL:
                renderizarCenaFinal(gc);
                break;
        }

        if (estadoAtual == Estado.FADE_IN || estadoAtual == Estado.FADE_OUT) {
            renderizarFade(gc);
        }
    }

    private void renderizarBatalha(GraphicsContext gc) {
        // Fundo
        if (modoTutorial) {
            gc.setFill(Color.rgb(30, 30, 70));
        } else {
            gc.setFill(Color.rgb(40, 0, 60));
        }
        gc.fillRect(0, 0, Constantes.TELA_LARGURA, Constantes.TELA_ALTURA);

        // Painel esquerdo
        gc.setFill(Color.rgb(30, 30, 45));
        gc.fillRect(20, 20, 600, 728);
        gc.setStroke(Color.rgb(80, 80, 100));
        gc.setLineWidth(2);
        gc.strokeRect(20, 20, 600, 728);

        // ===== MUDANÇA: Usar métodos do Inimigo =====
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 24));
        gc.fillText(inimigoAtual.getNome(), 40, 60);

        // Barra de vida do inimigo
        double percentualVidaInimigo = (double) inimigoAtual.getVida() / inimigoAtual.getVidaMaxima();
        gc.setFill(Color.rgb(200, 50, 50));
        gc.fillRect(40, 70, 200 * percentualVidaInimigo, 20);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
        gc.strokeRect(40, 70, 200, 20);

        gc.setFill(inimigoAtual.getVida() < 50 ? Color.WHITE : Color.BLACK);
        gc.setFont(Font.font("Consolas", 12));
        gc.fillText(inimigoAtual.getVida() + "/" + inimigoAtual.getVidaMaxima(), 45, 85);

        // Tempo e porcentagem de dano
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 18));
        gc.setFill(Color.YELLOW);
        gc.fillText(String.format("TEMPO: %.1fs", tempoAtual), 40, 120);
        gc.setFill(Color.CYAN);
        gc.fillText(String.format("DANO: %.0f%%", porcentagemDano), 200, 120);

        gc.setStroke(Color.rgb(80, 80, 100));
        gc.strokeLine(40, 140, 580, 140);

        // Questão
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 16));
        gc.fillText("QUESTÃO:", 40, 170);
        gc.setFont(Font.font("Consolas", 14));
        gc.setTextAlign(TextAlignment.LEFT);

        String[] palavrasQuestao = questaoAtual.getEnunciado().split(" ");
        StringBuilder linha = new StringBuilder();
        int y = 200;
        for (String palavra : palavrasQuestao) {
            if (linha.length() + palavra.length() + 1 > 70) {
                gc.fillText(linha.toString(), 40, y);
                y += 25;
                linha = new StringBuilder();
            }
            linha.append(palavra).append(" ");
        }
        if (linha.length() > 0) {
            gc.fillText(linha.toString(), 40, y);
        }

        gc.setStroke(Color.rgb(80, 80, 100));
        gc.strokeLine(40, y + 20, 580, y + 20);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 16));
        gc.fillText("CÓDIGO:", 40, y + 50);

        gc.setFont(Font.font("Consolas", 14));
        int inicioCodigoY = y + 80;

        for (int i = 0; i < linhasCodigo.size(); i++) {
            if (i == linhaComLacuna) {
                gc.setFill(Color.rgb(180, 180, 180));
                gc.fillText(textoAntesLacuna, 40, inicioCodigoY + i * 25);

                gc.setFill(Color.RED);
                gc.fillText("_____", 40 + calcularLarguraTexto(textoAntesLacuna, gc), inicioCodigoY + i * 25);

                gc.setFill(Color.rgb(180, 180, 180));
                gc.fillText(textoDepoisLacuna,
                        40 + calcularLarguraTexto(textoAntesLacuna + "_____", gc),
                        inicioCodigoY + i * 25);
            } else {
                gc.setFill(Color.rgb(180, 180, 180));
                gc.fillText(linhasCodigo.get(i), 40, inicioCodigoY + i * 25);
            }
        }

        int yAposCodigo = inicioCodigoY + linhasCodigo.size() * 25 + 10;
        gc.setStroke(Color.rgb(80, 80, 100));
        gc.strokeLine(40, yAposCodigo, 580, yAposCodigo);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 16));
        gc.fillText("COMPLETE A LACUNA:", 40, yAposCodigo + 40);

        gc.setFill(Color.rgb(50, 50, 70));
        gc.fillRect(40, yAposCodigo + 70, 400, 50);
        gc.setStroke(Color.CYAN);
        gc.setLineWidth(2);
        gc.strokeRect(40, yAposCodigo + 70, 400, 50);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 20));
        String textoExibido = entradaJogador.toString();

        if (mostrarCursor && entradaJogador.length() < Constantes.MAX_CARACTERES_INPUT) {
            textoExibido += "_";
        }

        gc.fillText(textoExibido, 50, yAposCodigo + 105);

        gc.setFont(Font.font("Consolas", 12));
        gc.setFill(entradaJogador.length() >= Constantes.MAX_CARACTERES_INPUT ? Color.RED : Color.GRAY);
        gc.fillText(String.format("%d/%d caracteres", entradaJogador.length(), Constantes.MAX_CARACTERES_INPUT),
                450, yAposCodigo + 105);

        gc.setFill(Color.YELLOW);
        gc.setFont(Font.font("Consolas", 12));
        gc.fillText("Digite o código que completa a lacuna vermelha", 40, yAposCodigo + 140);

        if (mostrarDica && !questaoAtual.getDica().isEmpty()) {
            gc.setFill(Color.rgb(255, 255, 0, 0.9));
            gc.setFont(Font.font("Consolas", FontWeight.BOLD, 14));
            gc.fillText("💡 Dica: " + questaoAtual.getDica(), 40, yAposCodigo + 170);
        }

        if (modoTutorial && mostrarDicaTutorial && indiceDicaTutorial < dicasTutorial.size()) {
            gc.setFill(Color.rgb(0, 255, 255, 0.9));
            gc.setFont(Font.font("Consolas", FontWeight.BOLD, 14));
            gc.fillText("📚 " + dicasTutorial.get(indiceDicaTutorial), 40, yAposCodigo + 200);

            gc.setFill(Color.GRAY);
            gc.setFont(Font.font("Consolas", 10));
            gc.fillText(String.format("Dica %d/%d", indiceDicaTutorial + 1, dicasTutorial.size()),
                    40, yAposCodigo + 225);
        }

        // ===== MUDANÇA: Desenhar inimigo usando polimorfismo =====
        inimigoAtual.desenhar(gc);

        // Efeito visual de dano no inimigo
        if (tempoDanoInimigo > 0) {
            double intensidade = tempoDanoInimigo / Constantes.DURACAO_DANO_VISUAL;
            gc.setFill(Color.rgb(255, 0, 0, 0.5 * intensidade));
            gc.fillRect(640, 100, 200, 200);
        }

        // Desenhar Player
        if (spritePlayerBatalha != null) {
            gc.drawImage(spritePlayerBatalha,
                    playerPosX, playerPosY,
                    playerLargura, playerAltura
            );
        } else {
            gc.setFill(Color.rgb(70, 130, 180));
            gc.fillRect(playerPosX, playerPosY, playerLargura, playerAltura);
        }

        // Efeito visual de dano no player
        if (tempoDanoPlayer > 0) {
            double intensidade = tempoDanoPlayer / Constantes.DURACAO_DANO_VISUAL;
            gc.setFill(Color.rgb(255, 0, 0, 0.5 * intensidade));
            gc.fillRect(playerPosX, playerPosY, playerLargura, playerAltura);
        }

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 20));
        gc.fillText(nomeJogador, 640, 595);

        double percentualVidaJogador = (double) vidaAtualJogador / vidaMaximaJogador;
        gc.setFill(Color.rgb(50, 200, 50));
        gc.fillRect(640, 600, 300 * percentualVidaJogador, 25);

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
        gc.strokeRect(640, 600, 300, 25);

        gc.setFont(Font.font("Consolas", 14));
        gc.fillText(vidaAtualJogador + "/" + vidaMaximaJogador, 640, 635);

        gc.setFill(Color.YELLOW);
        gc.setFont(Font.font("Consolas", 12));
        gc.fillText("ENTER: Submeter resposta", 640, 660);
        gc.fillText("ESC: Fugir da batalha", 640, 680);
        gc.fillText("H: Mostrar dica (se disponível)", 640, 700);
        gc.fillText("BACKSPACE: Apagar caractere", 640, 720);

        if (feedbackTempo > 0) {
            gc.setFill(feedbackCor);
            gc.setFont(Font.font("Consolas", FontWeight.BOLD, 16));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText(feedbackMensagem, Constantes.TELA_LARGURA / 2, 50);
            gc.setTextAlign(TextAlignment.LEFT);
        }

        if (tempoAtual > 45) {
            gc.setFill(Color.rgb(0, 255, 0, 0.3));
            gc.fillRect(40, 70, 200, 20);
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Consolas", 10));
            gc.fillText("DANO MÁXIMO (" + (modoTutorial ? "100" : "50") + "%)", 45, 85);
        } else if (tempoAtual > 20) {
            gc.setFill(Color.rgb(255, 255, 0, 0.3));
            gc.fillRect(40, 70, 200, 20);
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Consolas", 10));
            gc.fillText(String.format("DANO: %.0f%%", porcentagemDano), 45, 85);
        } else {
            gc.setFill(Color.rgb(255, 0, 0, 0.3));
            gc.fillRect(40, 70, 200, 20);
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Consolas", 10));
            gc.fillText("SEM DANO (0%)", 45, 85);
        }

        if (!modoTutorial) {
            if (random.nextDouble() < 0.1) {
                gc.setFill(Color.rgb(255, 0, 255, 0.1));
                gc.fillRect(20, 20, 600, 728);
            }
        }
    }

    private void renderizarCenaFinal(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, Constantes.TELA_LARGURA, Constantes.TELA_ALTURA);

        double centerX = Constantes.TELA_LARGURA / 2;

        // ===== MUDANÇA: Usar sprite do inimigo (Mago para cena final) =====
        Mago magoFinal = new Mago();
        gc.drawImage(magoFinal.getSprite(), centerX - 300, Constantes.TELA_ALTURA/2 - 100, 200, 200);

        if (spritePlayerBatalha != null) {
            gc.drawImage(spritePlayerBatalha, centerX + 100, Constantes.TELA_ALTURA/2 - 100, 150, 150);
        } else {
            gc.setFill(Color.rgb(70, 130, 180));
            gc.fillRect(centerX + 100, Constantes.TELA_ALTURA/2 - 100, 150, 150);
        }

        if (dialogoAtivo && indiceDialogoFinal < dialogoFinal.size()) {
            desenharCaixaDialogoFinal(gc);
        }

        if (!dialogoAtivo) {
            gc.setFill(Color.rgb(0, 0, 0, alphaFadeOutCena));
            gc.fillRect(0, 0, Constantes.TELA_LARGURA, Constantes.TELA_ALTURA);
        }
    }

    private void desenharCaixaDialogoFinal(GraphicsContext gc) {
        int margem = 20;
        int altura = 150;

        gc.setFill(Color.rgb(0, 0, 0, 0.85));
        gc.fillRect(margem, Constantes.TELA_ALTURA - altura - margem,
                Constantes.TELA_LARGURA - margem * 2, altura);

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeRect(margem, Constantes.TELA_ALTURA - altura - margem,
                Constantes.TELA_LARGURA - margem * 2, altura);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Consolas", 18));

        String textoAtual = dialogoFinal.get(indiceDialogoFinal);
        String[] linhas = quebrarTexto(textoAtual, 80);
        int y = Constantes.TELA_ALTURA - altura + 40;

        for (String linhaTxt : linhas) {
            gc.fillText(linhaTxt, margem + 20, y);
            y += 30;
        }

        gc.setFont(Font.font("Consolas", 14));
        gc.setFill(Color.YELLOW);
        gc.fillText("Pressione E para continuar", margem + 20, Constantes.TELA_ALTURA - margem - 20);

        gc.setFill(Color.GRAY);
        gc.fillText((indiceDialogoFinal + 1) + "/" + dialogoFinal.size(),
                Constantes.TELA_LARGURA - margem - 50, Constantes.TELA_ALTURA - margem - 20);
    }

    private void renderizarFade(GraphicsContext gc) {
        gc.setFill(Color.rgb(0, 0, 0, 1.0 - alpha));
        gc.fillRect(0, 0, Constantes.TELA_LARGURA, Constantes.TELA_ALTURA);
    }

    private void renderizarContagem(GraphicsContext gc) {
        gc.setFill(Color.rgb(0, 0, 0, 0.5));
        gc.fillRect(0, 0, Constantes.TELA_LARGURA, Constantes.TELA_ALTURA);

        String texto;
        Color cor;

        if (contagemAtual > 0) {
            texto = String.valueOf(contagemAtual);
            cor = Color.YELLOW;
        } else {
            texto = "GO!";
            cor = Color.LIME;
        }

        double escala = 1.0 + (3.0 - tempoContagem) * 0.5;

        gc.setFill(cor);
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 120 * escala));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(texto, Constantes.TELA_LARGURA / 2, Constantes.TELA_ALTURA / 2);
        gc.setTextAlign(TextAlignment.LEFT);
    }

    private void renderizarGameOver(GraphicsContext gc) {
        gc.setFill(Color.rgb(0, 0, 0, 0.9));
        gc.fillRect(0, 0, Constantes.TELA_LARGURA, Constantes.TELA_ALTURA);

        gc.setFill(Color.rgb(60, 0, 0));
        gc.fillRect(Constantes.TELA_LARGURA/2 - 250, Constantes.TELA_ALTURA/2 - 120, 500, 240);
        gc.setStroke(Color.RED);
        gc.setLineWidth(4);
        gc.strokeRect(Constantes.TELA_LARGURA/2 - 250, Constantes.TELA_ALTURA/2 - 120, 500, 240);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 48));
        gc.setTextAlign(TextAlignment.CENTER);

        if (modoTutorial) {
            gc.fillText("TUTORIAL FALHOU", Constantes.TELA_LARGURA/2, Constantes.TELA_ALTURA/2 - 50);
            gc.setFont(Font.font("Consolas", 20));
            gc.fillText("O Mago foi mais forte que você!", Constantes.TELA_LARGURA/2, Constantes.TELA_ALTURA/2);
        } else {
            gc.fillText("GAME OVER", Constantes.TELA_LARGURA/2, Constantes.TELA_ALTURA/2 - 50);
            gc.setFont(Font.font("Consolas", 20));
            gc.fillText("O bug foi mais rápido que você!", Constantes.TELA_LARGURA/2, Constantes.TELA_ALTURA/2);
        }

        gc.setFont(Font.font("Consolas", 16));
        gc.fillText(String.format("Reiniciando em %.0f segundos...", 3.0 - tempoResultado),
                Constantes.TELA_LARGURA/2, Constantes.TELA_ALTURA/2 + 40);

        gc.setTextAlign(TextAlignment.LEFT);
    }

    private void renderizarVitoria(GraphicsContext gc) {
        gc.setFill(Color.rgb(0, 0, 0, 0.9));
        gc.fillRect(0, 0, Constantes.TELA_LARGURA, Constantes.TELA_ALTURA);

        gc.setFill(Color.rgb(0, 60, 0));
        gc.fillRect(Constantes.TELA_LARGURA/2 - 250, Constantes.TELA_ALTURA/2 - 120, 500, 240);
        gc.setStroke(Color.GREEN);
        gc.setLineWidth(4);
        gc.strokeRect(Constantes.TELA_LARGURA/2 - 250, Constantes.TELA_ALTURA/2 - 120, 500, 240);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 48));
        gc.setTextAlign(TextAlignment.CENTER);

        if (modoTutorial) {
            gc.fillText("VITÓRIA!", Constantes.TELA_LARGURA/2, Constantes.TELA_ALTURA/2 - 50);
            gc.setFont(Font.font("Consolas", 20));
            gc.fillText("Parabéns! Você derrotou o Mago!", Constantes.TELA_LARGURA/2, Constantes.TELA_ALTURA/2);
        } else {
            gc.fillText("VITÓRIA!", Constantes.TELA_LARGURA/2, Constantes.TELA_ALTURA/2 - 50);
            gc.setFont(Font.font("Consolas", 20));
            gc.fillText("Você derrotou o bug!", Constantes.TELA_LARGURA/2, Constantes.TELA_ALTURA/2);
        }

        gc.setFont(Font.font("Consolas", 16));
        gc.fillText(String.format("Continuando em %.0f segundos...", 3.0 - tempoResultado),
                Constantes.TELA_LARGURA/2, Constantes.TELA_ALTURA/2 + 40);

        gc.setTextAlign(TextAlignment.LEFT);
    }

    private double calcularLarguraTexto(String texto, GraphicsContext gc) {
        return texto.length() * gc.getFont().getSize() * 0.6;
    }

    public void tratarTeclaPressionada(String tecla) {
        switch (estadoAtual) {
            case BATALHA:
                tratarTeclaBatalha(tecla);
                break;

            case CENA_FINAL:
                tratarTeclaCenaFinal(tecla);
                break;
        }
    }

    private void tratarTeclaBatalha(String tecla) {
        switch (tecla) {
            case "ENTER":
                verificarResposta();
                break;
            case "BACK_SPACE":
                if (entradaJogador.length() > 0) {
                    entradaJogador.deleteCharAt(entradaJogador.length() - 1);
                }
                break;
            case "H":
                if (!questaoAtual.getDica().isEmpty()) {
                    mostrarDica = true;
                    tempoDica = 5.0;
                    mostrarFeedback("Dica ativada!", Color.YELLOW);
                }
                break;
            case "ESCAPE":
                estadoAtual = Estado.FADE_OUT;
                alpha = 1.0;
                mostrarFeedback("Fugiu da batalha!", Color.YELLOW);
                break;
        }
    }

    private void tratarTeclaCenaFinal(String tecla) {
        if (dialogoAtivo && tecla.equals("E")) {
            indiceDialogoFinal++;
            if (indiceDialogoFinal >= dialogoFinal.size()) {
                dialogoAtivo = false;
            }
        }
    }

    public void tratarTeclaDigitada(String caractere) {
        if (estadoAtual != Estado.BATALHA) return;

        if (caractere.length() == 1 &&
                entradaJogador.length() < Constantes.MAX_CARACTERES_INPUT) {

            char c = caractere.charAt(0);
            if (Character.isLetterOrDigit(c) ||
                    c == ' ' || c == '+' || c == '-' || c == '*' || c == '/' ||
                    c == '%' || c == '=' || c == '<' || c == '>' || c == '!' ||
                    c == '&' || c == '|' || c == '(' || c == ')' || c == '[' ||
                    c == ']' || c == '{' || c == '}' || c == '.' || c == ',' ||
                    c == ';' || c == ':' || c == '?' || c == '"' || c == '\'' ||
                    c == '_') {

                entradaJogador.append(caractere);
            }
        }
    }

    private void verificarResposta() {
        if (entradaJogador.length() == 0) {
            mostrarFeedback("Digite uma resposta primeiro!", Color.YELLOW);
            return;
        }

        String respostaJogador = entradaJogador.toString().trim();
        String respostaEsperada = questaoAtual.getRespostaCorreta().trim();

        if (respostaJogador.equalsIgnoreCase(respostaEsperada) ||
                respostaJogador.replaceAll("\\s+", "").equalsIgnoreCase(respostaEsperada.replaceAll("\\s+", ""))) {

            // ===== MUDANÇA: Aplicar dano usando método do Inimigo =====
            int dano = (int)(inimigoAtual.getVidaMaxima() * (porcentagemDano / 100.0));
            inimigoAtual.receberDano(dano);

            tempoDanoInimigo = Constantes.DURACAO_DANO_VISUAL;

            mostrarFeedback(String.format("✓ Correto! Causou %d de dano! (%d%%)", dano, (int)porcentagemDano), Color.GREEN);

            // ===== MUDANÇA: Verificar vida usando método do Inimigo =====
            if (!inimigoAtual.estaVivo()) {
                if (modoTutorial) {
                    vitoriaTutorial = true;
                    estadoAtual = Estado.VITORIA;
                    tempoResultado = 0.0;
                } else {
                    iniciarCenaFinal();
                }
                return;
            }

            selecionarNovaQuestao();

        } else {
            vidaAtualJogador -= Constantes.DANO_ERRO;

            tempoDanoPlayer = Constantes.DURACAO_DANO_VISUAL;

            mostrarFeedback(String.format("✗ Errado! Era '%s'. -33 HP", respostaEsperada), Color.RED);

            if (vidaAtualJogador <= 0) {
                vidaAtualJogador = 0;
                resultadoGameOver = true;
                estadoAtual = Estado.GAME_OVER;
                tempoResultado = 0.0;
            } else {
                selecionarNovaQuestao();
            }
        }
    }

    private void iniciarCenaFinal() {
        estadoAtual = Estado.CENA_FINAL;
        dialogoAtivo = true;
        indiceDialogoFinal = 0;
        alphaFadeOutCena = 0.0;
        iniciandoCreditos = false;
    }

    private String[] quebrarTexto(String texto, int maxCaracteres) {
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
