# 🎮 FEITIÇO ALGORÍTMICO - Guia Rápido

> RPG educacional para aprender Java enquanto joga!

---

## 🎯 O que é?

Um jogo onde você derrota bugs (literalmente!) completando código Java. Feito para ensinar Programação Orientada a Objetos de forma divertida.

---

## 🚀 Como Rodar (Rápido!)

### 1. Você vai precisar:
- Java 17 ou superior
- IntelliJ IDEA (ou outra IDE)
- Maven

### 2. Passos:

```bash
# Clone o projeto
git clone [URL-DO-REPOSITORIO]

# Abra no IntelliJ
# File → Open → [pasta do projeto]

# No terminal do IntelliJ:
mvn clean install
mvn javafx:run
```

### ⚠️ ATENÇÃO!
**NÃO rode com `java Main`**
**USE SEMPRE: `mvn javafx:run`**

O JavaFX precisa do Maven para funcionar!

---

## 🎮 Como Jogar

### Controles:
- **WASD** ou **Setas**: Andar
- **SHIFT**: Correr
- **E**: Conversar/Avançar diálogo
- **Números**: Responder questões
- **Enter**: Confirmar resposta

### Objetivo:
1. Fale com o Mago (centro do mapa)
2. Vença a batalha do tutorial
3. Entre no portal que aparece
4. Encontre e derrote o Bug gigante
5. Assista os créditos!

---

## 📁 Estrutura do Projeto

```
org/example/
├── Main.java              → Jogo principal
├── entidades/             → Player, Inimigos, NPCs
├── sistemas/combate/      → Batalhas e questões
├── sistemas/mundo/        → Mapas
└── util/                  → Constantes
```

---

## 🏗️ Conceitos POO Usados

### Hierarquia de Classes:
```
Entidade
  ├─ Personagem
  │   ├─ Player (você)
  │   └─ Inimigo
  │       ├─ Mago
  │       └─ Bug
  └─ NPC
```

### Os 4 Pilares:
✅ **Encapsulamento**: Dados privados
✅ **Herança**: Classes filhas herdam dos pais
✅ **Polimorfismo**: Mesmo código, comportamentos diferentes
✅ **Abstração**: Classes abstratas definem contratos

---

## 🐛 Problemas Comuns

### "Module not found: javafx"
👉 Use `mvn javafx:run`, não `java Main`

### Tela preta
👉 `mvn clean install` e tente de novo

### Erro ao carregar mapa
👉 Verifique se tem `mapa.tmx` em `src/main/resources/map/`

---

## 👥 Quem Fez

**Desenvolvimento:**
- David Rodrigues
- Guilherme Rufino
- Arthur Vieira
- João Gomes
- Fernando
- Arthur

**Obrigado por jogar!** ❤️

---

## 🎓 Use para Estudar!

Este projeto é perfeito para:
- Aprender POO na prática
- Ver hierarquia de classes funcionando
- Entender polimorfismo
- Trabalhar com JavaFX

---

## 📊 Números do Projeto

- **13 classes** organizadas
- **3 níveis** de herança
- **~2400 linhas** de código
- **15+ questões** de Java
- **100% POO** aplicado

---

## 💡 Dica Final

Quanto mais rápido você responder as questões, mais dano vai causar nos inimigos! ⚡

**Boa sorte e bom código!** 🚀

---

### Feito com ❤️ e Java

