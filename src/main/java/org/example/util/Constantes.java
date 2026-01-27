package org.example.util;

/**
 * Classe com constantes do jogo
 */
public class Constantes {
    
    // ===== TELA =====
    public static final int TELA_LARGURA = 1024;
    public static final int TELA_ALTURA = 768;
    public static final double ZOOM = 3.0;
    
    // ===== PLAYER =====
    public static final double VELOCIDADE_ANDAR = 70;
    public static final double VELOCIDADE_CORRER = 100;
    public static final double HITBOX_LARGURA = 12;
    public static final double HITBOX_ALTURA = 14;
    public static final double HITBOX_OFFSET_X = 32.5;
    public static final double HITBOX_OFFSET_Y = 18;
    
    // ===== ANIMAÇÃO =====
    public static final double FPS_ANIMACAO = 10;
    
    // ===== TRANSIÇÃO =====
    public static final double DURACAO_TRANSICAO = 3.0;
    
    // ===== CRÉDITOS =====
    public static final double TEMPO_ESPERA_REINICIO = 3.0;
    
    // ===== BATALHA =====
    public static final double DANO_MAXIMO_TUTORIAL = 100.0;
    public static final double DANO_MAXIMO_BUG = 50.0;
    public static final double TEMPO_LIMITE_BATALHA = 60.0;
    public static final int VIDA_MAXIMA_JOGADOR = 100;
    public static final int VIDA_MAXIMA_INIMIGO = 100;
    public static final int DANO_ERRO = 33;
    public static final double DURACAO_DANO_VISUAL = 1.0;
    public static final int MAX_CARACTERES_INPUT = 30;
    
    // ===== DETECÇÃO =====
    public static final double BUG_INTERACAO_MARGEM = 30;
    public static final double INTERACAO_MARGEM_NPC = 18;
    
    // ===== NPC =====
    public static final double NPC_AJUSTE_X = -7;
    public static final double NPC_ESCALA = 0.3;
    public static final double NPC_HIT_W = 32;
    public static final double NPC_HIT_H = 35;
    public static final double NPC_HIT_OFF_X = 6;
    public static final double NPC_HIT_OFF_Y = 8;
    
    // ===== MUNDO ALTERNATIVO =====
    public static final int NUM_PARTICULAS = 50;
    public static final double BUG_MUNDO_LARGURA = 50;
    public static final double BUG_MUNDO_ALTURA = 50;
    
    private Constantes() {
        // Classe utilitária - não deve ser instanciada
    }
}
