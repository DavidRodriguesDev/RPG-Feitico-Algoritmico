package org.example.sistemas.combate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BancoQuestoes {
    private List<Questao> questoes;
    private Random random;

    // Classe interna para representar uma questão
    public static class Questao {
        private String enunciado;
        private String codigoComLacuna;
        private String respostaCorreta;
        private String dica;

        public Questao(String enunciado, String codigoComLacuna, String respostaCorreta, String dica) {
            this.enunciado = enunciado;
            this.codigoComLacuna = codigoComLacuna;
            this.respostaCorreta = respostaCorreta;
            this.dica = dica;
        }

        public Questao(String enunciado, String codigoComLacuna, String respostaCorreta) {
            this(enunciado, codigoComLacuna, respostaCorreta, "");
        }

        public String getEnunciado() { return enunciado; }
        public String getCodigoComLacuna() { return codigoComLacuna; }
        public String getRespostaCorreta() { return respostaCorreta; }
        public String getDica() { return dica; }
    }

    public BancoQuestoes() {
        questoes = new ArrayList<>();
        random = new Random();
        inicializarQuestoes();
    }

    private void inicializarQuestoes() {
        // Questão 1: MDC (Máximo Divisor Comum)
        questoes.add(new Questao(
                "Complete a função que calcula o MDC (Máximo Divisor Comum) entre dois números.",
                """
                public int mdc(int a, int b) {
                    while (b != 0) {
                        int temp = b;
                        b = a % b;
                        a = temp;
                    }
                    return _____;
                }
                """,
                "a",
                "MDC é o último valor não nulo de 'a'"
        ));

        // Questão 2: Contar vogais
        questoes.add(new Questao(
                "Complete a função que conta quantas vogais há em uma string.",
                """
                public int contarVogais(String texto) {
                    int count = 0;
                    String vogais = "aeiouAEIOU";
                    for (char c : texto.toCharArray()) {
                        if (vogais.indexOf(c) != -1) {
                            _____;
                        }
                    }
                    return count;
                }
                """,
                "count++",
                "Use o operador de incremento"
        ));

        // Questão 3: Soma de dois números
        questoes.add(new Questao(
                "Complete a função que retorna a soma de dois números.",
                """
                public int soma(int a, int b) {
                    return _____;
                }
                """,
                "a + b",
                "Some os dois parâmetros"
        ));

        // Questão 4: Maior de dois números
        questoes.add(new Questao(
                "Complete a função que retorna o maior de dois números.",
                """
                public int maior(int a, int b) {
                    return _____ ? a : b;
                }
                """,
                "a > b",
                "Compare se 'a' é maior que 'b'"
        ));

        // Questão 5: Fatorial
        questoes.add(new Questao(
                "Complete a função que calcula o fatorial de um número.",
                """
                public int fatorial(int n) {
                    return n <= 1 ? 1 : n * _____;
                }
                """,
                "fatorial(n - 1)",
                "Chamada recursiva diminuindo n"
        ));

        // Questão 6: Inverter string
        questoes.add(new Questao(
                "Complete a função que inverte uma string.",
                """
                public String inverter(String texto) {
                    return new StringBuilder(texto)._____().toString();
                }
                """,
                "reverse",
                "Método do StringBuilder que inverte"
        ));

        // Questão 7: Verificar número primo
        questoes.add(new Questao(
                "Complete a função que verifica se um número é primo.",
                """
                public boolean ehPrimo(int n) {
                    if (n <= 1) return false;
                    for (int i = 2; i * i <= n; i++) {
                        if (n % i == 0) {
                            return _____;
                        }
                    }
                    return true;
                }
                """,
                "false",
                "Se for divisível, não é primo"
        ));

        // Questão 8: Verificar número par
        questoes.add(new Questao(
                "Complete a função que verifica se um número é par.",
                """
                public boolean ehPar(int numero) {
                    return numero _____ 2 == 0;
                }
                """,
                "%",
                "Operador de resto da divisão"
        ));

        // Questão 9: Operador AND
        questoes.add(new Questao(
                "Complete a função com o operador lógico AND.",
                """
                public boolean ambosPositivos(int a, int b) {
                    return a > 0 _____ b > 0;
                }
                """,
                "&&",
                "Operador AND lógico"
        ));

        // Questão 10: Operador OR
        questoes.add(new Questao(
                "Complete a função com o operador lógico OR.",
                """
                public boolean peloMenosUmPositivo(int a, int b) {
                    return a > 0 _____ b > 0;
                }
                """,
                "||",
                "Operador OR lógico"
        ));

        // Questão 11: Operador de igualdade
        questoes.add(new Questao(
                "Complete a função com o operador de igualdade.",
                """
                public boolean saoIguais(int a, int b) {
                    return a _____ b;
                }
                """,
                "==",
                "Operador de igualdade"
        ));

        // Questão 12: Operador de diferença
        questoes.add(new Questao(
                "Complete a função com o operador de diferença.",
                """
                public boolean saoDiferentes(int a, int b) {
                    return a _____ b;
                }
                """,
                "!=",
                "Operador de diferença"
        ));

        // Questão 13: Média de três números
        questoes.add(new Questao(
                "Complete a função que calcula a média de três números.",
                """
                public double media(int a, int b, int c) {
                    return (a + b + c) _____ 3.0;
                }
                """,
                "/",
                "Operador de divisão"
        ));

        // Questão 14: Potência
        questoes.add(new Questao(
                "Complete a função que calcula a potência de um número.",
                """
                public double potencia(double base, int expoente) {
                    return Math.pow(base, _____);
                }
                """,
                "expoente",
                "Segundo parâmetro da função"
        ));

        // Questão 15: Verificar palíndromo
        questoes.add(new Questao(
                "Complete a função que verifica se uma string é um palíndromo.",
                """
                public boolean ehPalindromo(String texto) {
                    return texto.equals(new StringBuilder(texto)._____().toString());
                }
                """,
                "reverse",
                "Inverte a string para comparar"
        ));

        // Questão 16: Converter para maiúsculas
        questoes.add(new Questao(
                "Complete a função que converte uma string para maiúsculas.",
                """
                public String paraMaiusculas(String texto) {
                    return texto._____();
                }
                """,
                "toUpperCase",
                "Método da classe String"
        ));

        // Questão 17: Comprimento da string
        questoes.add(new Questao(
                "Complete a função que retorna o comprimento de uma string.",
                """
                public int comprimento(String texto) {
                    return texto._____();
                }
                """,
                "length",
                "Método da classe String"
        ));

        // Questão 18: Array de números pares
        questoes.add(new Questao(
                "Complete a função que cria um array com os primeiros N números pares.",
                """
                public int[] primeirosPares(int n) {
                    int[] pares = new int[n];
                    for (int i = 0; i < n; i++) {
                        pares[i] = _____ * 2;
                    }
                    return pares;
                }
                """,
                "i",
                "Use a variável do loop"
        ));

        // Questão 19: FizzBuzz
        questoes.add(new Questao(
                "Complete a função FizzBuzz para números divisíveis por 3.",
                """
                public String fizzBuzz(int n) {
                    if (n % 3 == 0 && n % 5 == 0) return "FizzBuzz";
                    if (n % 3 == 0) return "_____";
                    if (n % 5 == 0) return "Buzz";
                    return String.valueOf(n);
                }
                """,
                "Fizz",
                "Palavra para múltiplos de 3"
        ));

        // Questão 20: Encontrar mínimo em array
        questoes.add(new Questao(
                "Complete a função que encontra o menor valor em um array.",
                """
                public int encontrarMinimo(int[] numeros) {
                    int minimo = numeros[0];
                    for (int i = 1; i < numeros.length; i++) {
                        if (numeros[i] _____ minimo) {
                            minimo = numeros[i];
                        }
                    }
                    return minimo;
                }
                """,
                "<",
                "Operador de menor que"
        ));
    }

    public Questao getQuestaoAleatoria() {
        return questoes.get(random.nextInt(questoes.size()));
    }

    public int getTotalQuestoes() {
        return questoes.size();
    }

    public List<Questao> getTodasQuestoes() {
        return new ArrayList<>(questoes);
    }
}
