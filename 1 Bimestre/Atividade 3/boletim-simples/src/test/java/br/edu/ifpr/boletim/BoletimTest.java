package br.edu.ifpr.boletim;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BoletimTest {

    @Test
    void deveAprovarAlunoComMediaOito() {
        // Preparar: criar o objeto que será testado.
        Boletim boletim = new Boletim();

        // Executar: chamar um único método com uma entrada conhecida.
        String resultado = boletim.verificarSituacao(8);

        // Verificar: comparar o resultado esperado com o resultado obtido.
        assertEquals("APROVADO", resultado);
    }

    @Test
    void deveRecuperarNotaAlunoComMediaQuatro() {
        Boletim boletim = new Boletim();

        // Executar: chamar um único método com uma entrada conhecida.
        String resultado = boletim.verificarSituacao(4);

        // Verificar: comparar o resultado esperado com o resultado obtido.
        assertEquals("RECUPERACAO", resultado);
    }

    @Test
    void deveReprovarAlunoComMediaDois() {
        Boletim boletim = new Boletim();

        // Executar: chamar um único método com uma entrada conhecida.
        String resultado = boletim.verificarSituacao(2);

        // Verificar: comparar o resultado esperado com o resultado obtido.
        assertEquals("REPROVADO", resultado);
    }

    @Test
    void deveCalcularMediaIgualCinco() {
        Boletim boletim = new Boletim();

        double resultado = boletim.calcularMedia(5,5);

        assertEquals(5,resultado);

    }

    @Test
    void deveAprovarAlunoComMediaSete() {
        Boletim boletim = new Boletim();

        String resultado = boletim.verificarSituacao(7);

        assertEquals("APROVADO", resultado);
    }

    @Test
    void deveAprovarAlunoComMediaDez() {
        Boletim boletim = new Boletim();

        String resultado = boletim.verificarSituacao(10);

        assertEquals("APROVADO", resultado);
    }

    @Test
    void deveRecuperarNotaAlunoComMediaSeisVirgulaNoventaENove() {
        Boletim boletim = new Boletim();

        String resultado = boletim.verificarSituacao(6.99);

        assertEquals("RECUPERACAO", resultado);
    }

    @Test
    void deveRecuperarNotaAlunoComMediaCinco() {
        Boletim boletim = new Boletim();

        String resultado = boletim.verificarSituacao(5);

        assertEquals("RECUPERACAO", resultado);
    }

    @Test
    void deveReprovarAlunoComMediaTresVirgulaNoventaENove() {
        Boletim boletim = new Boletim();

        String resultado = boletim.verificarSituacao(3.99);

        assertEquals("REPROVADO", resultado);
    }

    @Test
    void deveReprovarAlunoComMediaZero() {
        Boletim boletim = new Boletim();

        String resultado = boletim.verificarSituacao(0);

        assertEquals("REPROVADO", resultado);
    }

    @Test
    void deveCalcularMediaComParteDecimal() {
        Boletim boletim = new Boletim();

        double resultado = boletim.calcularMedia(7, 8);

        assertEquals(7.5, resultado, 0.0001);
    }

    @Test
    void deveCalcularMediaComNotasDiferentes() {
        Boletim boletim = new Boletim();

        double resultado = boletim.calcularMedia(10, 0);

        assertEquals(5, resultado, 0.0001);
    }

    @Test
    void deveCalcularMediaIgualZero() {
        Boletim boletim = new Boletim();

        double resultado = boletim.calcularMedia(0, 0);

        assertEquals(0, resultado, 0.0001);
    }

    @Test
    void deveCalcularMediaIgualDez() {
        Boletim boletim = new Boletim();

        double resultado = boletim.calcularMedia(10, 10);

        assertEquals(10, resultado, 0.0001);
    }

    @Test
    void deveCalcularMediaComDuasCasasDecimais() {
        Boletim boletim = new Boletim();

        double resultado = boletim.calcularMedia(6.5, 7.75);

        assertEquals(7.125, resultado, 0.0001);
    }

    @Test
    void deveContarZeroAprovadosEmArrayVazio() {
        Boletim boletim = new Boletim();

        int resultado = boletim.contarAprovados(new double[] {});

        assertEquals(0, resultado);
    }

    @Test
    void deveContarUmAprovadoEmArrayComUmElementoAprovado() {
        Boletim boletim = new Boletim();

        int resultado = boletim.contarAprovados(new double[] {8});

        assertEquals(1, resultado);
    }

    @Test
    void deveContarZeroAprovadosEmArrayComUmElementoNaoAprovado() {
        Boletim boletim = new Boletim();

        int resultado = boletim.contarAprovados(new double[] {5});

        assertEquals(0, resultado);
    }

    @Test
    void deveContarDoisAprovadosEmArrayComVariosElementos() {
        Boletim boletim = new Boletim();

        int resultado = boletim.contarAprovados(new double[] {8, 5, 7});

        assertEquals(2, resultado);
    }

    @Test
    void deveContarTodosAprovadosEmArrayComSomenteAprovados() {
        Boletim boletim = new Boletim();

        int resultado = boletim.contarAprovados(new double[] {7, 8.5, 10});

        assertEquals(3, resultado);
    }

    @Test
    void deveContarZeroAprovadosEmArrayComSomenteNaoAprovados() {
        Boletim boletim = new Boletim();

        int resultado = boletim.contarAprovados(new double[] {0, 3.99, 4, 6.99});

        assertEquals(0, resultado);
    }

    @Test
    void deveContarMediaSeteComoAprovadaNoArray() {
        Boletim boletim = new Boletim();

        int resultado = boletim.contarAprovados(new double[] {7});

        assertEquals(1, resultado);
    }

    @Test
    void deveNaoContarMediaSeisVirgulaNoventaENoveComoAprovadaNoArray() {
        Boletim boletim = new Boletim();

        int resultado = boletim.contarAprovados(new double[] {6.99});

        assertEquals(0, resultado);
    }
}
