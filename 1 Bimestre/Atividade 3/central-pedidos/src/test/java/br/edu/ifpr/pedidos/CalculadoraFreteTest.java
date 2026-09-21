package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CalculadoraFreteTest {

    @Test
    void deveLancarExcecaoQuandoValorLiquidoForNegativo() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        assertThrows(IllegalArgumentException.class, () -> calculadora.calcular(pedido, cliente, -1));
    }

    @Test
    void deveCobrarTarifaDoParana() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        long frete = calculadora.calcular(pedido, cliente, 10_000);

        assertEquals(1_200L, frete);
    }

    @Test
    void deveCobrarTarifaDeSaoPaulo() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "SP", false, null);

        long frete = calculadora.calcular(pedido, cliente, 10_000);

        assertEquals(2_000L, frete);
    }

    @Test
    void deveCobrarTarifaDoRioDeJaneiro() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "RJ", false, null);

        long frete = calculadora.calcular(pedido, cliente, 10_000);

        assertEquals(2_000L, frete);
    }

    @Test
    void deveCobrarTarifaPadraoParaDemaisUfs() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "MG", false, null);

        long frete = calculadora.calcular(pedido, cliente, 10_000);

        assertEquals(3_000L, frete);
    }

    @Test
    void deveAceitarValorLiquidoZero() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        long frete = calculadora.calcular(pedido, cliente, 0);

        assertEquals(1_200L, frete);
    }

    @Test
    void deveNaoCobrarAdicionalDePesoNoLimiteDeDoisQuilos() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 2_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        long frete = calculadora.calcular(pedido, cliente, 10_000);

        assertEquals(1_200L, frete);
    }

    @Test
    void deveNaoCobrarAdicionalDePesoAbaixoDoLimiteDeDoisQuilos() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_999, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        long frete = calculadora.calcular(pedido, cliente, 10_000);

        assertEquals(1_200L, frete);
    }

    @Test
    void deveCobrarUmAdicionalDePesoAcimaDoLimiteDeDoisQuilos() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 2_001, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        long frete = calculadora.calcular(pedido, cliente, 10_000);

        assertEquals(1_500L, frete);
    }

    @Test
    void deveCobrarUmAdicionalDePesoParaUmQuiloExatoAcimaDoLimite() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 3_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        long frete = calculadora.calcular(pedido, cliente, 10_000);

        assertEquals(1_500L, frete);
    }

    @Test
    void deveCobrarDoisAdicionaisDePesoQuandoHouverFracaoDeQuilo() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 3_001, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        long frete = calculadora.calcular(pedido, cliente, 10_000);

        assertEquals(1_800L, frete);
    }

    @Test
    void deveCobrarVariosAdicionaisDePeso() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 5_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        long frete = calculadora.calcular(pedido, cliente, 10_000);

        assertEquals(2_100L, frete);
    }

    @Test
    void deveCobrarAdicionalDePesoNoLimiteMaximoDoDominio() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("BLOCO", 10_000, 1, 5, 100_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        long frete = calculadora.calcular(pedido, cliente, 10_000);

        assertEquals(30_600L, frete);
    }

    @Test
    void deveConsiderarQuantidadeDasLinhasNoPesoExcedente() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 1_000, 3, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        long frete = calculadora.calcular(pedido, cliente, 10_000);

        assertEquals(1_500L, frete);
    }

    @Test
    void deveZerarFreteQuandoValorLiquidoForTrezentosReais() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        long frete = calculadora.calcular(pedido, cliente, 30_000);

        assertEquals(0L, frete);
    }

    @Test
    void deveCobrarFreteQuandoValorLiquidoForAbaixoDeTrezentosReais() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        long frete = calculadora.calcular(pedido, cliente, 29_999);

        assertEquals(1_200L, frete);
    }

    @Test
    void deveZerarAdicionalDePesoQuandoFreteForGratuito() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 5_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        long frete = calculadora.calcular(pedido, cliente, 30_000);

        assertEquals(0L, frete);
    }

    @Test
    void deveNaoZerarFreteExpressoMesmoComValorLiquidoElegivel() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", true, null);

        long frete = calculadora.calcular(pedido, cliente, 30_000);

        assertEquals(2_700L, frete);
    }

    @Test
    void deveCobrarMetadeDoFreteParaClienteVip() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Cliente cliente = new Cliente(true, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        long frete = calculadora.calcular(pedido, cliente, 10_000);

        assertEquals(600L, frete);
    }

    @Test
    void deveCobrarMetadeDaTarifaPadraoParaClienteVip() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Cliente cliente = new Cliente(true, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "MG", false, null);

        long frete = calculadora.calcular(pedido, cliente, 10_000);

        assertEquals(1_500L, frete);
    }

    @Test
    void deveCobrarMetadeDoFreteComAdicionalDePesoParaClienteVip() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Cliente cliente = new Cliente(true, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 3_001, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        long frete = calculadora.calcular(pedido, cliente, 10_000);

        assertEquals(900L, frete);
    }

    @Test
    void deveManterFreteZeroParaClienteVipComFreteGratuito() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Cliente cliente = new Cliente(true, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        long frete = calculadora.calcular(pedido, cliente, 30_000);

        assertEquals(0L, frete);
    }

    @Test
    void deveAcrescentarTaxaExpressaAoFrete() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", true, null);

        long frete = calculadora.calcular(pedido, cliente, 10_000);

        assertEquals(2_700L, frete);
    }

    @Test
    void deveAcrescentarTaxaExpressaSemDividirPelaMetadeParaClienteVip() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Cliente cliente = new Cliente(true, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", true, null);

        long frete = calculadora.calcular(pedido, cliente, 10_000);

        assertEquals(2_100L, frete);
    }

    @Test
    void deveAcrescentarTaxaDeFragilidade() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("VASO", 10_000, 1, 5, 1_000, true);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        long frete = calculadora.calcular(pedido, cliente, 10_000);

        assertEquals(1_700L, frete);
    }

    @Test
    void deveIgnorarTaxaDeFragilidadeParaItemFragilInativo() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido fragilInativo = new ItemPedido("VASO", 10_000, 0, 5, 1_000, true);
        ItemPedido comum = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(fragilInativo, comum), "PR", false, null);

        long frete = calculadora.calcular(pedido, cliente, 10_000);

        assertEquals(1_200L, frete);
    }

    @Test
    void deveCobrarTaxaDeFragilidadeUmaUnicaVez() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido primeiro = new ItemPedido("VASO", 10_000, 1, 5, 500, true);
        ItemPedido segundo = new ItemPedido("PRATO", 10_000, 1, 5, 500, true);
        Pedido pedido = new Pedido(List.of(primeiro, segundo), "PR", false, null);

        long frete = calculadora.calcular(pedido, cliente, 10_000);

        assertEquals(1_700L, frete);
    }

    @Test
    void deveCobrarTaxaDeFragilidadeMesmoComFreteGratuito() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("VASO", 10_000, 1, 5, 1_000, true);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        long frete = calculadora.calcular(pedido, cliente, 30_000);

        assertEquals(500L, frete);
    }

    @Test
    void deveSomarTaxaExpressaETaxaDeFragilidade() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("VASO", 10_000, 1, 5, 1_000, true);
        Pedido pedido = new Pedido(List.of(item), "PR", true, null);

        long frete = calculadora.calcular(pedido, cliente, 10_000);

        assertEquals(3_200L, frete);
    }

    @Test
    void deveCombinarPesoVipExpressoEFragilidade() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Cliente cliente = new Cliente(true, false, 1);
        ItemPedido item = new ItemPedido("VASO", 10_000, 1, 5, 3_001, true);
        Pedido pedido = new Pedido(List.of(item), "PR", true, null);

        long frete = calculadora.calcular(pedido, cliente, 10_000);

        assertEquals(2_900L, frete);
    }
}
