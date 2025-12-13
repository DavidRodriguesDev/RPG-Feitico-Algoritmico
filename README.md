RPG 2D EM JAVAFX
================

Projeto de RPG 2D desenvolvido em Java utilizando JavaFX e mapas criados no
Tiled Map Editor.

O objetivo do projeto é estudar e implementar:
- Renderização de mapas TMX
- Múltiplas camadas de mapa
- Câmera com zoom e limites
- Personagem animado
- Hitbox separada do sprite
- Movimento com corrida
- Organização correta de projeto Java + Maven


------------------------------------------------------------
TECNOLOGIAS UTILIZADAS
------------------------------------------------------------

- Java JDK 17 ou superior
- JavaFX
- Maven
- Tiled Map Editor
- IntelliJ IDEA (recomendado)


------------------------------------------------------------
ESTRUTURA DE PASTAS
------------------------------------------------------------

RPG
|
|-- .idea
|   |-- compiler.xml
|   |-- encodings.xml
|   |-- jarRepositories.xml
|   |-- misc.xml
|   |-- workspace.xml
|
|-- .mvn
|
|-- src
|   |
|   |-- main
|   |   |
|   |   |-- java
|   |   |   |
|   |   |   |-- org
|   |   |       |
|   |   |       |-- example
|   |   |           |
|   |   |           |-- Main.java
|   |   |           |-- MapaTiled.java
|   |   |
|   |   |-- resources
|   |       |
|   |       |-- map
|   |       |   |
|   |       |   |-- mapa.tmx
|   |       |   |-- tiles.tsx
|   |       |
|   |       |-- sprites
|   |       |   |
|   |       |   |-- player_walk.png
|   |       |
|   |       |-- tiles
|   |           |
|   |           |-- tileset.png
|
|-- test
|   |
|   |-- java
|
|-- target
|
|-- .gitignore
|
|-- pom.xml


OBS:
As pastas .idea/ e target/ NÃO devem ser enviadas para o GitHub.


------------------------------------------------------------
REQUISITOS PARA RODAR O PROJETO
------------------------------------------------------------

1) JAVA
Instale o JDK 17 ou superior:

https://adoptium.net/

Verifique no terminal:
java -version


2) JAVAFX
O JavaFX NÃO vem mais junto com o Java.

Baixe o JavaFX compatível com seu sistema operacional:
https://openjfx.io/

Extraia o JavaFX em algum diretório do seu computador.


------------------------------------------------------------
COMO RODAR NO INTELLIJ IDEA (RECOMENDADO)
------------------------------------------------------------

1) Abra o IntelliJ IDEA
2) Abra o projeto (pasta RPG)
3) Vá em:
   Run > Edit Configurations
4) Em "VM Options", adicione:

--module-path "CAMINHO_DO_JAVAFX/lib" --add-modules javafx.controls,javafx.fxml


EXEMPLOS:

Linux:
--module-path "/home/usuario/javafx/lib" --add-modules javafx.controls,javafx.fxml

Windows:
--module-path "C:\javafx\lib" --add-modules javafx.controls,javafx.fxml


5) Execute a classe:
org.example.Main


------------------------------------------------------------
CONTROLES DO JOGO
------------------------------------------------------------

W ou SETA PARA CIMA    -> Move para cima
S ou SETA PARA BAIXO   -> Move para baixo
A ou SETA PARA ESQUERDA-> Move para esquerda
D ou SETA PARA DIREITA -> Move para direita
SHIFT                 -> Correr


------------------------------------------------------------
MAPAS (TILED)
------------------------------------------------------------

- Mapas criados no Tiled Map Editor
- Formato TMX
- Tiles de tamanho fixo
- Múltiplas camadas renderizadas
- Assets carregados via src/main/resources


------------------------------------------------------------
FUNCIONALIDADES ATUAIS
------------------------------------------------------------

- Renderização completa do mapa
- Múltiplas camadas
- Câmera centralizada no jogador
- Zoom configurável
- Limite da câmera nas bordas do mapa
- Jogador limitado ao tamanho do mapa
- Sprite animado
- Hitbox separada do sprite
- Corrida ao segurar SHIFT


------------------------------------------------------------
PRÓXIMOS PASSOS (IDEIAS)
------------------------------------------------------------

- Colisão por tiles
- NPCs
- Sistema de diálogo
- Combate
- Inventário
- Save / Load
- Sons e músicas
- Exportar como JAR executável


------------------------------------------------------------
LICENÇA
------------------------------------------------------------

Projeto open-source para fins educacionais.
Sinta-se livre para estudar, modificar e expandir.


------------------------------------------------------------
AUTOR
------------------------------------------------------------

Projeto desenvolvido como estudo de JavaFX e jogos 2D.
