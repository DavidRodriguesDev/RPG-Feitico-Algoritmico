package org.example;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

// NOVOS IMPORTS - Classes refatoradas
import org.example.entidades.Player;
import org.example.entidades.NPC;
import org.example.sistemas.combate.EstadoBatalha;
import org.example.sistemas.mundo.MapaTiled;
import org.example.sistemas.mundo.MundoAlternativo;
import org.example.util.Constantes;
import org.example.util.CreditosHelper;
import org.example.util.DialogoHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Classe principal do jogo FEITIÇO ALGORÍTMICO
 * REFATORADA: Agora usa sistema OOP com hierarquia de entidades
 */
public class Main extends Application {

    // ===== MUDANÇA 1: Player agora é um objeto =====
    private Player player;
    
    // ===== SISTEMA =====
    private Canvas canvas;
    private GraphicsContext gc;
    private MapaTiled mapa;
    private MundoAlternativo mundoAlternativo;

    // ===== TRANSICAO =====
    private boolean emTransicao = false;
    private double tempoTransicao = 0.0;
    private double alphaTransicao = 0.0;

    // ===== CRÉDITOS =====
    private boolean creditosAtivo = false;
    private List<String> linhasCreditos = new ArrayList<>();
    private double scrollCreditos = Constantes.TELA_ALTURA;
    private boolean esperandoReinicio = false;
    private double tempoEsperaReinicio = 0.0;

    // ===== ESTADO DE BATALHA =====
    private EstadoBatalha estadoBatalha;
    private boolean emBatalha = false;

    // ===== INPUT =====
    private boolean up, down, left, right, correr;

    // ===== DIÁLOGO =====
    private boolean emDialogo = false;
    private List<String> textosDialogo = new ArrayList<>();
    private int indiceDialogo = 0;
    private NPC npcAtual = null;
    private boolean tutorialIniciado = false;
    private boolean tutorialCompleto = false;

    // ===== FLUXO DE VITÓRIA =====
    private boolean venceuBug = false;
    private boolean aguardandoDialogoFinal = false;

    // ===== MUNDO ALTERNATIVO =====
    private boolean mundoAlternativoAtivo = false;
    private Random random = new Random();

    // ===== BATALHA COM BUG =====
    private boolean batalhaComBug = false;

    // ===== DETECÇÃO AUTOMÁTICA DO BUG =====
    private boolean detectouBug = false;

    @Override
    public void start(Stage stage) {
        mapa = new MapaTiled("/map/mapa.tmx");
        mundoAlternativo = new MundoAlternativo(mapa);

        // ===== MUDANÇA 2: Inicializar o player como objeto =====
        player = new Player(100, 270);

        inicializarCreditos();

        canvas = new Canvas(Constantes.TELA_LARGURA, Constantes.TELA_ALTURA);
        gc = canvas.getGraphicsContext2D();
        gc.setImageSmoothing(false);

        estadoBatalha = new EstadoBatalha(this);

        Scene scene = new Scene(new StackPane(canvas));
        configurarInput(scene);

        stage.setScene(scene);
        stage.setTitle("FEITIÇO ALGORITMICO");
        stage.setResizable(false);
        stage.show();

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

    private void inicializarCreditos() {
        linhasCreditos.clear();
        linhasCreditos.add("CRÉDITOS");
        linhasCreditos.add("");
        linhasCreditos.add("FEITIÇO ALGORITMICO");
        linhasCreditos.add("");
        linhasCreditos.add("DESENVOLVIMENTO");
        linhasCreditos.add("Programação e Design");
        linhasCreditos.add("David Rodrigues");
        linhasCreditos.add("Guilherme Rufino");
        linhasCreditos.add("Arthur Vieira");
        linhasCreditos.add("João Gomes");
        linhasCreditos.add("Fernando");
        linhasCreditos.add("Arthur");
        linhasCreditos.add("");
        linhasCreditos.add("AGRADECIMENTOS ESPECIAIS");
        linhasCreditos.add("A todos que apoiaram este projeto");
        linhasCreditos.add("");
        linhasCreditos.add("OBRIGADO POR JOGAR!");
    }

    private void atualizar(double dt) {
        if (creditosAtivo) {
            atualizarCreditos(dt);
            return;
        }

        if (emBatalha) {
            estadoBatalha.atualizar(dt);
            return;
        }

        if (emTransicao) {
            tempoTransicao += dt;
            alphaTransicao = Math.sin((tempoTransicao / Constantes.DURACAO_TRANSICAO) * Math.PI);

            if (tempoTransicao >= Constantes.DURACAO_TRANSICAO) {
                emTransicao = false;
                tempoTransicao = 0.0;
                alphaTransicao = 0.0;

                if (venceuBug && aguardandoDialogoFinal) {
                    iniciarDialogoVitoriaFinal();
                    aguardandoDialogoFinal = false;
                } else if (!venceuBug) {
                    mundoAlternativoAtivo = !mundoAlternativoAtivo;
                    mundoAlternativo.setAtivo(mundoAlternativoAtivo);

                    if (mundoAlternativoAtivo) {
                        // ===== MUDANÇA 3: Usar métodos do Player =====
                        double larguraMapa = mapa.getLarguraPixels();
                        double novoX = larguraMapa - player.getX() - 50;
                        player.setX(novoX);

                        if (player.getX() < 50) player.setX(50);
                        if (player.getX() > larguraMapa - 100) player.setX(larguraMapa - 100);

                        detectouBug = false;
                        System.out.println("Mundo alternativo ativado!");
                    } else {
                        System.out.println("Mundo normal ativado!");
                    }
                }
            }
        }

        if (mundoAlternativoAtivo && !venceuBug) {
            mundoAlternativo.atualizar(dt);

            if (!emBatalha && !emDialogo && !emTransicao) {
                // ===== MUDANÇA 4: Usar getters da hitbox do Player =====
                double px = player.getHitboxX();
                double py = player.getHitboxY();

                if (mundoAlternativo.jogadorPertoBug(px, py, 
                        player.getHitboxLargura(), 
                        player.getHitboxAltura(), 
                        Constantes.BUG_INTERACAO_MARGEM)) {
                    if (!detectouBug) {
                        System.out.println("Bug detectado! Iniciando batalha automática...");
                        detectouBug = true;
                        iniciarBatalhaComBug();
                        return;
                    }
                } else {
                    detectouBug = false;
                }
            }
        }

        if (emDialogo) {
            // ===== MUDANÇA 5: Animação do player delegada =====
            player.atualizarAnimacao(dt, false);
            return;
        }

        // ===== MUDANÇA 6: Velocidade vem do Player =====
        double velocidade = player.getVelocidadeAtual();

        double dx = 0, dy = 0;
        boolean movendo = false;

        if (up)    { dy -= velocidade * dt; movendo = true; }
        if (down)  { dy += velocidade * dt; movendo = true; }
        if (left)  { 
            dx -= velocidade * dt; 
            player.setOlhandoDireita(false); 
            movendo = true; 
        }
        if (right) { 
            dx += velocidade * dt; 
            player.setOlhandoDireita(true);  
            movendo = true; 
        }

        moverX(dx);
        moverY(dy);
        
        // ===== MUDANÇA 7: Animação do player =====
        player.atualizarAnimacao(dt, movendo);
    }

    private void atualizarCreditos(double dt) {
        scrollCreditos -= dt * 40;

        if (scrollCreditos < -linhasCreditos.size() * 40 - 200) {
            esperandoReinicio = true;
            tempoEsperaReinicio += dt;
        }
    }

    private void moverX(double dx) {
        if (dx == 0) return;

        // ===== MUDANÇA 8: Usar Player para cálculos =====
        double novoX = player.getX() + dx;

        double hitLeft   = novoX + (player.getHitboxX() - player.getX());
        double hitRight  = hitLeft + player.getHitboxLargura();
        double hitTop    = player.getHitboxY();
        double hitBottom = hitTop + player.getHitboxAltura();

        if (!colideMapa(hitLeft, hitRight, hitTop, hitBottom) &&
                !colideNPC(hitLeft, hitTop)) {
            player.setX(novoX);
        }
    }

    private void moverY(double dy) {
        if (dy == 0) return;

        double novoY = player.getY() + dy;

        double hitLeft   = player.getHitboxX();
        double hitRight  = hitLeft + player.getHitboxLargura();
        double hitTop    = novoY + (player.getHitboxY() - player.getY());
        double hitBottom = hitTop + player.getHitboxAltura();

        if (!colideMapa(hitLeft, hitRight, hitTop, hitBottom) &&
                !colideNPC(hitLeft, hitTop)) {
            player.setY(novoY);
        }
    }

    private boolean colideMapa(double left, double right, double top, double bottom) {
        int t = mapa.getTileSize();

        int tileLeft   = (int) Math.floor(left / t);
        int tileRight  = (int) Math.floor((right - 1) / t);
        int tileTop    = (int) Math.floor(top / t);
        int tileBottom = (int) Math.floor((bottom - 1) / t);

        if (mundoAlternativoAtivo) {
            return mundoAlternativo.isSolido(tileLeft, tileTop) ||
                    mundoAlternativo.isSolido(tileRight, tileTop) ||
                    mundoAlternativo.isSolido(tileLeft, tileBottom) ||
                    mundoAlternativo.isSolido(tileRight, tileBottom);
        } else {
            return mapa.isSolido(tileLeft, tileTop) ||
                    mapa.isSolido(tileRight, tileTop) ||
                    mapa.isSolido(tileLeft, tileBottom) ||
                    mapa.isSolido(tileRight, tileBottom);
        }
    }

    private boolean colideNPC(double hitX, double hitY) {
        if (mundoAlternativoAtivo) return false;

        for (NPC npc : mapa.getNPCs()) {
            if (npc.colidiu(hitX, hitY, player.getHitboxLargura(), player.getHitboxAltura())) {
                return true;
            }
        }
        return false;
    }

    private void alternarDialogo() {
        if (emBatalha) return;

        if (emDialogo) {
            indiceDialogo++;

            if (indiceDialogo >= textosDialogo.size()) {
                emDialogo = false;
                npcAtual = null;
                textosDialogo.clear();
                indiceDialogo = 0;

                if (tutorialCompleto && !mundoAlternativoAtivo && !emTransicao && !venceuBug) {
                    transporteImediatoMundoAlternativo();
                } else if (!tutorialIniciado && !tutorialCompleto) {
                    tutorialIniciado = true;
                    iniciarBatalhaTutorial();
                } else if (venceuBug) {
                    iniciarCreditos();
                }
            }
            return;
        }

        // ===== MUDANÇA 9: Usar hitbox do Player =====
        double px = player.getHitboxX();
        double py = player.getHitboxY();

        if (!mundoAlternativoAtivo && !venceuBug) {
            for (NPC npc : mapa.getNPCs()) {
                if (npc.jogadorPerto(px, py, player.getHitboxLargura(), player.getHitboxAltura())) {
                    emDialogo = true;
                    npcAtual = npc;

                    if (!tutorialIniciado && !tutorialCompleto) {
                        textosDialogo.add("Olá aventureiro, você deve estar se perguntando oq está acontecendo.");
                        textosDialogo.add("Antes de tudo, eu sou o mago ancião, protetor desse lugar.");
                        textosDialogo.add("Você fez um código tão bizonho que veio parar aqui.");
                        textosDialogo.add("A sua gambiarra foi tão pesada que desestabilizou a ordem natural das coisas.");
                        textosDialogo.add("Agora o meu mundo está em perigo por sua causa.");
                        textosDialogo.add("Por causa da sua chegada aqui, monstros horrendos que se alimentam da frustração das pessoas chegaram aqui também.");
                        textosDialogo.add("Eu apelidei eles de BUGS, você deve estar familiarizado com esse nome.");
                        textosDialogo.add("Eu até poderia te mandar ao seu mundo agora, mas estou debilitado agora.");
                        textosDialogo.add("Para eu poder te mandar o seu mundo, você terá que exterminar os BUGS que você trouxe pra cá.");
                        textosDialogo.add("Como você vai fazer isso?, irei te ensinar agora jovem gafanhoto.");
                        textosDialogo.add("Vamos começar com um tutorial básico.");
                        textosDialogo.add("Na batalha, você precisará completar lacunas no código.");
                        textosDialogo.add("Tem 60 segundos por questão. Nos primeiros 15s, causa 100% de dano!");
                        textosDialogo.add("Entre 15-20s, o dano diminui. Após 20s, não causa dano.");
                        textosDialogo.add("Cada erro ou tempo esgotado tira 33 de sua vida (100 total).");
                        textosDialogo.add("Se conseguir acertar os códigos, você vence! Se sua vida acabar, game over.");
                        textosDialogo.add("Vamos começar?");
                    } else if (tutorialCompleto) {
                        textosDialogo.add("Parabéns, aventureiro! Você completou o tutorial!");
                        textosDialogo.add("Agora você está preparado para enfrentar os BUGS.");
                        textosDialogo.add("Há um BUG especial no mundo alternativo que precisa ser eliminado.");
                        textosDialogo.add("Prepare-se para uma jornada através do ESPELHO ROXO!");
                        textosDialogo.add("Boa sorte, você vai precisar!");
                    } else {
                        textosDialogo.add("Complete o tutorial primeiro, aventureiro!");
                        textosDialogo.add("Derrote o Mago para continuar sua jornada.");
                    }
                    return;
                }
            }
        }
    }

    private void transporteImediatoMundoAlternativo() {
        System.out.println("TRANSPORTE IMEDIATO PARA MUNDO ALTERNATIVO!");

        mundoAlternativoAtivo = true;
        mundoAlternativo.setAtivo(true);

        // ===== MUDANÇA 10: Player manipulado como objeto =====
        double larguraMapa = mapa.getLarguraPixels();
        double novoX = larguraMapa - player.getX() - 50;
        player.setX(novoX);

        if (player.getX() < 50) player.setX(50);
        if (player.getX() > larguraMapa - 100) player.setX(larguraMapa - 100);

        detectouBug = false;

        mostrarFeedbackInstantaneo("TRANSPORTADO PARA O MUNDO ALTERNATIVO!");
    }

    private void mostrarFeedbackInstantaneo(String mensagem) {
        gc.save();
        gc.setFill(Color.rgb(0, 0, 0, 0.5));
        gc.fillRect(0, Constantes.TELA_ALTURA - 100, Constantes.TELA_LARGURA, 100);

        gc.setFill(Color.CYAN);
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 20));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(mensagem, Constantes.TELA_LARGURA / 2, Constantes.TELA_ALTURA - 50);

        gc.restore();

        new Thread(() -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void iniciarBatalhaTutorial() {
        emBatalha = true;
        batalhaComBug = false;
        estadoBatalha.iniciarBatalha(true);
    }

    private void iniciarBatalhaComBug() {
        emBatalha = true;
        batalhaComBug = true;
        estadoBatalha.iniciarBatalhaComBug();
        mundoAlternativo.iniciarBatalhaComBug();
    }

    private void iniciarDialogoVitoriaFinal() {
        emDialogo = true;
        textosDialogo.clear();
        textosDialogo.add("Parabéns, jovem programador!");
        textosDialogo.add("Você conseguiu! Você derrotou o BUG mais perigoso!");
        textosDialogo.add("Com essa vitória, o equilíbrio foi restaurado.");
        textosDialogo.add("A anomalia que você causou foi corrigida.");
        textosDialogo.add("Você provou ser digno de voltar ao seu mundo.");
        textosDialogo.add("Sua jornada aqui chegou ao fim.");
        textosDialogo.add("Leve consigo as lições aprendidas:");
        textosDialogo.add("Um bom código é como uma boa magia -");
        textosDialogo.add("requer precisão, clareza e cuidado.");
        textosDialogo.add("Agora, é hora de voltar para casa.");
        textosDialogo.add("Farewell, aventureiro!");
        indiceDialogo = 0;
    }

    public void iniciarCreditosAposBatalha() {
        emBatalha = false;
        batalhaComBug = false;
        venceuBug = true;
        creditosAtivo = true;
        scrollCreditos = Constantes.TELA_ALTURA;
        esperandoReinicio = false;
        tempoEsperaReinicio = 0.0;

        System.out.println("Cena final concluída. Iniciando créditos...");
    }

    private void iniciarCreditos() {
        creditosAtivo = true;
        scrollCreditos = Constantes.TELA_ALTURA;
        esperandoReinicio = false;
        tempoEsperaReinicio = 0.0;

        // ===== MUDANÇA 11: Resetar posição do Player =====
        player.setX(mapa.getLarguraPixels() / 2 - 25);
        player.setY(mapa.getAlturaPixels() / 2 - 25);
    }

    public void finalizarBatalha(boolean gameOver, boolean vitoriaTutorial) {
        emBatalha = false;

        if (batalhaComBug) {
            batalhaComBug = false;

            if (gameOver) {
                reiniciarJogoCompleto();
            } else {
                System.out.println("Vitória sobre o bug - cena final em andamento...");
            }
        } else if (gameOver) {
            reiniciarJogoCompleto();
        } else if (vitoriaTutorial) {
            tutorialCompleto = true;
            tutorialIniciado = false;
        }
    }

    private void reiniciarJogoCompleto() {
        // ===== MUDANÇA 12: Resetar Player =====
        player = new Player(100, 270);
        
        tutorialIniciado = false;
        tutorialCompleto = false;
        mundoAlternativoAtivo = false;
        mundoAlternativo.setAtivo(false);
        mundoAlternativo.resetarBug();
        emDialogo = false;
        textosDialogo.clear();
        indiceDialogo = 0;
        creditosAtivo = false;
        venceuBug = false;
        aguardandoDialogoFinal = false;
        up = false;
        down = false;
        left = false;
        right = false;
        correr = false;
        detectouBug = false;
        estadoBatalha = new EstadoBatalha(this);

        System.out.println("Jogo reiniciado completamente!");
    }

    private void renderizar() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, Constantes.TELA_LARGURA, Constantes.TELA_ALTURA);

        if (creditosAtivo) {
            renderizarCreditos();
            return;
        }

        if (emBatalha) {
            estadoBatalha.renderizar(gc);
            return;
        }

        if (emTransicao) {
            renderizarEfeitoTransicao();
        }

        // ===== MUDANÇA 13: Câmera baseada no Player =====
        double camW = Constantes.TELA_LARGURA / Constantes.ZOOM;
        double camH = Constantes.TELA_ALTURA / Constantes.ZOOM;

        double camX = player.getX() + player.getFrames()[0].getWidth() * 0.4 - camW / 2;
        double camY = player.getY() + player.getFrames()[0].getHeight() * 0.4 - camH / 2;

        int larguraMapa = mundoAlternativoAtivo ?
                mundoAlternativo.getLarguraPixels() : mapa.getLarguraPixels();
        int alturaMapa = mundoAlternativoAtivo ?
                mundoAlternativo.getAlturaPixels() : mapa.getAlturaPixels();

        camX = Math.max(0, Math.min(camX, larguraMapa - camW));
        camY = Math.max(0, Math.min(camY, alturaMapa - camH));

        gc.save();
        gc.scale(Constantes.ZOOM, Constantes.ZOOM);
        gc.translate(-Math.floor(camX), -Math.floor(camY));

        if (mundoAlternativoAtivo && !venceuBug) {
            mundoAlternativo.desenharBase(gc);
            mundoAlternativo.desenharNPCs(gc);
            mundoAlternativo.desenharTopo(gc);
            
            // ===== MUDANÇA 14: Desenhar Player invertido =====
            player.desenharInvertido(gc);

        } else {
            mapa.desenharBase(gc);
            mapa.desenharNPCs(gc);

            // ===== MUDANÇA 15: Desenhar Player normal =====
            player.desenhar(gc);

            mapa.desenharTopo(gc);
        }

        gc.restore();

        if (emDialogo && indiceDialogo < textosDialogo.size()) {
            desenharCaixaDialogo();
        }

        if (venceuBug && !emTransicao && !emDialogo && !creditosAtivo) {
            gc.setFill(Color.rgb(0, 255, 0, 0.7));
            gc.fillRect(Constantes.TELA_LARGURA/2 - 200, Constantes.TELA_ALTURA/2 - 50, 400, 100);

            gc.setStroke(Color.WHITE);
            gc.setLineWidth(3);
            gc.strokeRect(Constantes.TELA_LARGURA/2 - 200, Constantes.TELA_ALTURA/2 - 50, 400, 100);

            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Consolas", FontWeight.BOLD, 24));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("VITÓRIA SOBRE O BUG!", Constantes.TELA_LARGURA/2, Constantes.TELA_ALTURA/2);
            gc.fillText("Retornando ao mundo normal...", Constantes.TELA_LARGURA/2, Constantes.TELA_ALTURA/2 + 30);
            gc.setTextAlign(TextAlignment.LEFT);
        }
    }

    private void renderizarCreditos() {
        CreditosHelper.renderizar(gc, linhasCreditos, scrollCreditos,
                esperandoReinicio, tempoEsperaReinicio);
    }

    private void renderizarEfeitoTransicao() {
        gc.setFill(Color.rgb(128, 0, 255, alphaTransicao * 0.7));
        gc.fillRect(0, 0, Constantes.TELA_LARGURA, Constantes.TELA_ALTURA);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 32));
        gc.setTextAlign(TextAlignment.CENTER);

        String texto;
        if (venceuBug) {
            texto = "RETORNANDO AO MUNDO NORMAL...";
        } else if (mundoAlternativoAtivo) {
            texto = "VOLTANDO AO MUNDO NORMAL...";
        } else {
            texto = "VIAJANDO PARA O MUNDO ALTERNATIVO...";
        }

        gc.fillText(texto, Constantes.TELA_LARGURA / 2, Constantes.TELA_ALTURA / 2);

        for (int i = 0; i < 100; i++) {
            double x = random.nextDouble() * Constantes.TELA_LARGURA;
            double y = random.nextDouble() * Constantes.TELA_ALTURA;
            double size = 1 + random.nextDouble() * 5;

            gc.setFill(Color.rgb(
                    128 + random.nextInt(128),
                    0,
                    255,
                    alphaTransicao * 0.5
            ));
            gc.fillOval(x, y, size, size);
        }

        gc.setTextAlign(TextAlignment.LEFT);
    }

    private void desenharCaixaDialogo() {
        if (indiceDialogo < textosDialogo.size()) {
            DialogoHelper.renderizarCaixaDialogo(gc,
                    textosDialogo.get(indiceDialogo),
                    indiceDialogo,
                    textosDialogo.size()
            );
        }
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

    private void configurarInput(Scene scene) {
        scene.setOnKeyPressed(e -> {
            if (creditosAtivo) {
                if (e.getCode().toString().equals("SPACE") && esperandoReinicio) {
                    reiniciarJogoCompleto();
                }
                return;
            }

            if (emBatalha) {
                estadoBatalha.tratarTeclaPressionada(e.getCode().toString());
                return;
            }

            switch (e.getCode()) {
                case W, UP -> up = true;
                case S, DOWN -> down = true;
                case A, LEFT -> left = true;
                case D, RIGHT -> right = true;
                case SHIFT -> {
                    correr = true;
                    player.setCorrendo(true);
                }
                case E -> alternarDialogo();
            }
        });

        scene.setOnKeyTyped(e -> {
            if (emBatalha && !e.getCharacter().isEmpty()) {
                estadoBatalha.tratarTeclaDigitada(e.getCharacter());
            }
        });

        scene.setOnKeyReleased(e -> {
            if (emBatalha) return;

            switch (e.getCode()) {
                case W, UP -> up = false;
                case S, DOWN -> down = false;
                case A, LEFT -> left = false;
                case D, RIGHT -> right = false;
                case SHIFT -> {
                    correr = false;
                    player.setCorrendo(false);
                }
            }
        });
    }

    public boolean isMundoAlternativo() {
        return mundoAlternativoAtivo && !venceuBug;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
