package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PoliticaDescontoTest {

    @Test
    void deveLancarExcecaoQuandoSubtotalForNegativo() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(false, false, 1);

        assertThrows(IllegalArgumentException.class, () -> politica.calcular(cliente, -1, null));
    }

    @Test
    void deveRetornarZeroParaClienteComumAbaixoDeQuinhentosReais() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(false, false, 1);

        long desconto = politica.calcular(cliente, 49_999, null);

        assertEquals(0L, desconto);
    }

    @Test
    void deveAplicarCincoPorCentoParaClienteComumComSubtotalDeQuinhentosReais() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(false, false, 1);

        long desconto = politica.calcular(cliente, 50_000, null);

        assertEquals(2_500L, desconto);
    }

    @Test
    void deveTruncarDescontoDeClienteComumQuandoDivisaoNaoForExata() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(false, false, 1);

        long desconto = politica.calcular(cliente, 99_999, null);

        assertEquals(4_999L, desconto);
    }

    @Test
    void deveAplicarDezPorCentoParaClienteVipAbaixoDeQuinhentosReais() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(true, false, 1);

        long desconto = politica.calcular(cliente, 10_000, null);

        assertEquals(1_000L, desconto);
    }

    @Test
    void deveAplicarDezPorCentoParaClienteVipAcimaDeQuinhentosReais() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(true, false, 1);

        long desconto = politica.calcular(cliente, 100_000, null);

        assertEquals(10_000L, desconto);
    }

    @Test
    void deveTruncarDescontoDeClienteVipQuandoDivisaoNaoForExata() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(true, false, 1);

        long desconto = politica.calcular(cliente, 10_009, null);

        assertEquals(1_000L, desconto);
    }

    @Test
    void deveManterDescontoBaseQuandoCupomForNulo() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(true, false, 1);

        long desconto = politica.calcular(cliente, 10_000, null);

        assertEquals(1_000L, desconto);
    }

    @Test
    void deveManterDescontoBaseQuandoCupomForBranco() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(true, false, 1);

        assertAll(
                () -> assertEquals(1_000L, politica.calcular(cliente, 10_000, "")),
                () -> assertEquals(1_000L, politica.calcular(cliente, 10_000, "   "))
        );
    }

    @Test
    void deveLancarExcecaoQuandoCupomForDesconhecido() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(false, false, 1);

        assertThrows(IllegalArgumentException.class, () -> politica.calcular(cliente, 10_000, "PROMO"));
    }

    @Test
    void deveLancarExcecaoQuandoClienteVipUsarCupomDesconhecido() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(true, false, 1);

        assertThrows(IllegalArgumentException.class, () -> politica.calcular(cliente, 100_000, "PROMO"));
    }

    @Test
    void deveAplicarBemvindoParaClienteSemComprasComSubtotalDeCemReais() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(false, false, 0);

        long desconto = politica.calcular(cliente, 10_000, "BEMVINDO");

        assertEquals(2_000L, desconto);
    }

    @Test
    void deveNaoAplicarBemvindoAbaixoDeCemReais() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(false, false, 0);

        long desconto = politica.calcular(cliente, 9_999, "BEMVINDO");

        assertEquals(0L, desconto);
    }

    @Test
    void deveNaoAplicarBemvindoQuandoClienteJaTemCompras() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(false, false, 1);

        long desconto = politica.calcular(cliente, 10_000, "BEMVINDO");

        assertEquals(0L, desconto);
    }

    @Test
    void deveSomarBemvindoAoDescontoBase() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(false, false, 0);

        long desconto = politica.calcular(cliente, 50_000, "BEMVINDO");

        assertEquals(4_500L, desconto);
    }

    @Test
    void deveNormalizarCupomComEspacosEMinusculas() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(false, false, 0);

        long desconto = politica.calcular(cliente, 15_000, "  bemvindo ");

        assertEquals(2_000L, desconto);
    }

    @Test
    void deveAplicarExtra10ComSubtotalDeDuzentosReais() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(false, false, 1);

        long desconto = politica.calcular(cliente, 20_000, "EXTRA10");

        assertEquals(2_000L, desconto);
    }

    @Test
    void deveNaoAplicarExtra10AbaixoDeDuzentosReais() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(false, false, 1);

        long desconto = politica.calcular(cliente, 19_999, "EXTRA10");

        assertEquals(0L, desconto);
    }

    @Test
    void deveSomarExtra10AoDescontoBase() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(false, false, 1);

        long desconto = politica.calcular(cliente, 50_000, "EXTRA10");

        assertEquals(7_500L, desconto);
    }

    @Test
    void deveManterDescontoBaseQuandoExtra10NaoForElegivel() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(true, false, 1);

        long desconto = politica.calcular(cliente, 19_999, "EXTRA10");

        assertEquals(1_999L, desconto);
    }

    @Test
    void deveNormalizarExtra10ComEspacosEMinusculas() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(false, false, 1);

        long desconto = politica.calcular(cliente, 20_000, " Extra10 ");

        assertEquals(2_000L, desconto);
    }

    @Test
    void deveLimitarDescontoCombinadoAVintePorCento() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(true, false, 0);

        long desconto = politica.calcular(cliente, 10_000, "BEMVINDO");

        assertEquals(2_000L, desconto);
    }

    @Test
    void deveAceitarDescontoCombinadoIgualAoTeto() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(true, false, 1);

        long desconto = politica.calcular(cliente, 100_000, "EXTRA10");

        assertEquals(20_000L, desconto);
    }
}
