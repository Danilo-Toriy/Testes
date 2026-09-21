package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PedidoTest {

    @Test
    void deveCriarPedidoValido() {
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);

        Pedido pedido = new Pedido(List.of(item), "PR", true, "EXTRA10");

        assertAll(
                () -> assertEquals(List.of(item), pedido.itens()),
                () -> assertEquals("PR", pedido.uf()),
                () -> assertTrue(pedido.expresso()),
                () -> assertEquals("EXTRA10", pedido.cupom())
        );
    }

    @Test
    void deveAceitarUfDiferenteDeParanaSaoPauloERioDeJaneiro() {
        Pedido pedido = new Pedido(List.of(), "MG", false, null);

        assertEquals("MG", pedido.uf());
    }

    @Test
    void deveAceitarCupomNuloOuBranco() {
        Pedido nulo = new Pedido(List.of(), "PR", false, null);
        Pedido branco = new Pedido(List.of(), "PR", false, "   ");

        assertAll(
                () -> assertNull(nulo.cupom()),
                () -> assertEquals("   ", branco.cupom())
        );
    }

    @Test
    void deveLancarExcecaoQuandoListaDeItensForNula() {
        assertThrows(IllegalArgumentException.class, () -> new Pedido(null, "PR", false, null));
    }

    @Test
    void deveAceitarListaComCemLinhas() {
        ItemPedido item = new ItemPedido("CANETA", 100, 1, 1, 10, false);

        Pedido pedido = new Pedido(Collections.nCopies(100, item), "PR", false, null);

        assertEquals(100, pedido.itens().size());
    }

    @Test
    void deveLancarExcecaoQuandoListaTiverMaisDeCemLinhas() {
        ItemPedido item = new ItemPedido("CANETA", 100, 1, 1, 10, false);

        assertThrows(IllegalArgumentException.class,
                () -> new Pedido(Collections.nCopies(101, item), "PR", false, null));
    }

    @Test
    void deveLancarExcecaoQuandoListaTiverElementoNulo() {
        ItemPedido item = new ItemPedido("CANETA", 100, 1, 1, 10, false);

        assertThrows(NullPointerException.class,
                () -> new Pedido(Arrays.asList(item, null), "PR", false, null));
    }

    @Test
    void deveLancarExcecaoQuandoUfForInvalida() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new Pedido(List.of(), null, false, null)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Pedido(List.of(), "", false, null)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Pedido(List.of(), "P", false, null)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Pedido(List.of(), "PRR", false, null)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Pedido(List.of(), "pr", false, null)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Pedido(List.of(), "P1", false, null)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Pedido(List.of(), "\u00C1B", false, null))
        );
    }

    @Test
    void deveCopiarListaDeItensDefensivamente() {
        ItemPedido item = new ItemPedido("CANETA", 100, 1, 1, 10, false);
        List<ItemPedido> itens = new ArrayList<>();
        itens.add(item);
        Pedido pedido = new Pedido(itens, "PR", false, null);

        itens.add(item);

        assertEquals(1, pedido.itens().size());
    }

    @Test
    void deveExporListaDeItensImutavel() {
        ItemPedido item = new ItemPedido("CANETA", 100, 1, 1, 10, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        assertThrows(UnsupportedOperationException.class, () -> pedido.itens().add(item));
    }

    @Test
    void deveCalcularSubtotalDeUmaLinha() {
        ItemPedido item = new ItemPedido("CANETA", 2_500, 4, 10, 20, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        assertEquals(10_000L, pedido.subtotalCentavos());
    }

    @Test
    void deveCalcularSubtotalDeVariasLinhas() {
        ItemPedido primeiro = new ItemPedido("CANETA", 1_000, 2, 10, 20, false);
        ItemPedido segundo = new ItemPedido("CADERNO", 3_000, 1, 10, 300, false);
        Pedido pedido = new Pedido(List.of(primeiro, segundo), "PR", false, null);

        assertEquals(5_000L, pedido.subtotalCentavos());
    }

    @Test
    void deveIgnorarLinhaInativaNoSubtotal() {
        ItemPedido inativo = new ItemPedido("CANETA", 9_000, 0, 10, 20, false);
        ItemPedido ativo = new ItemPedido("CADERNO", 1_000, 1, 10, 300, false);
        Pedido pedido = new Pedido(List.of(inativo, ativo), "PR", false, null);

        assertEquals(1_000L, pedido.subtotalCentavos());
    }

    @Test
    void deveCalcularSubtotalZeroSomenteComLinhasInativas() {
        ItemPedido inativo = new ItemPedido("CANETA", 9_000, 0, 10, 20, false);
        Pedido pedido = new Pedido(List.of(inativo, inativo), "PR", false, null);

        assertEquals(0L, pedido.subtotalCentavos());
    }

    @Test
    void deveSomarLinhasComSkuRepetidoNoSubtotal() {
        ItemPedido item = new ItemPedido("CANETA", 1_000, 2, 10, 20, false);
        Pedido pedido = new Pedido(List.of(item, item), "PR", false, null);

        assertEquals(4_000L, pedido.subtotalCentavos());
    }

    @Test
    void deveCalcularSubtotalZeroParaListaVazia() {
        Pedido pedido = new Pedido(List.of(), "PR", false, null);

        assertEquals(0L, pedido.subtotalCentavos());
    }

    @Test
    void deveCalcularPesoMultiplicandoPesoUnitarioPelaQuantidade() {
        ItemPedido item = new ItemPedido("CANETA", 1_000, 3, 10, 500, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        assertEquals(1_500, pedido.pesoGramas());
    }

    @Test
    void deveCalcularPesoDeVariasLinhas() {
        ItemPedido primeiro = new ItemPedido("CANETA", 1_000, 2, 10, 500, false);
        ItemPedido segundo = new ItemPedido("CADERNO", 3_000, 1, 10, 1_000, false);
        Pedido pedido = new Pedido(List.of(primeiro, segundo), "PR", false, null);

        assertEquals(2_000, pedido.pesoGramas());
    }

    @Test
    void deveIgnorarLinhaInativaNoPeso() {
        ItemPedido inativo = new ItemPedido("CANETA", 1_000, 0, 10, 5_000, false);
        ItemPedido ativo = new ItemPedido("CADERNO", 3_000, 1, 10, 1_000, false);
        Pedido pedido = new Pedido(List.of(inativo, ativo), "PR", false, null);

        assertEquals(1_000, pedido.pesoGramas());
    }

    @Test
    void deveCalcularPesoZeroParaListaVazia() {
        Pedido pedido = new Pedido(List.of(), "PR", false, null);

        assertEquals(0, pedido.pesoGramas());
    }

    @Test
    void deveIdentificarItemFragilAtivo() {
        ItemPedido comum = new ItemPedido("CANETA", 1_000, 1, 10, 20, false);
        ItemPedido fragil = new ItemPedido("VASO", 3_000, 1, 10, 800, true);
        Pedido pedido = new Pedido(List.of(comum, fragil), "PR", false, null);

        assertTrue(pedido.temFragil());
    }

    @Test
    void deveIdentificarItemFragilNoInicioDaLista() {
        ItemPedido fragil = new ItemPedido("VASO", 3_000, 1, 10, 800, true);
        ItemPedido comum = new ItemPedido("CANETA", 1_000, 1, 10, 20, false);
        Pedido pedido = new Pedido(List.of(fragil, comum), "PR", false, null);

        assertTrue(pedido.temFragil());
    }

    @Test
    void deveIgnorarItemFragilInativo() {
        ItemPedido fragilInativo = new ItemPedido("VASO", 3_000, 0, 10, 800, true);
        ItemPedido comum = new ItemPedido("CANETA", 1_000, 1, 10, 20, false);
        Pedido pedido = new Pedido(List.of(fragilInativo, comum), "PR", false, null);

        assertFalse(pedido.temFragil());
    }

    @Test
    void deveRetornarFalsoQuandoNaoHouverItemFragil() {
        ItemPedido comum = new ItemPedido("CANETA", 1_000, 1, 10, 20, false);
        Pedido pedido = new Pedido(List.of(comum, comum), "PR", false, null);

        assertFalse(pedido.temFragil());
    }

    @Test
    void deveRetornarFalsoParaFragilidadeEmListaVazia() {
        Pedido pedido = new Pedido(List.of(), "PR", false, null);

        assertFalse(pedido.temFragil());
    }

    @Test
    void deveTerEstoqueSuficienteQuandoTodasAsLinhasEstaoDisponiveis() {
        ItemPedido primeiro = new ItemPedido("CANETA", 1_000, 2, 5, 20, false);
        ItemPedido segundo = new ItemPedido("CADERNO", 3_000, 1, 1, 300, false);
        Pedido pedido = new Pedido(List.of(primeiro, segundo), "PR", false, null);

        assertTrue(pedido.estoqueSuficiente());
    }

    @Test
    void deveTerEstoqueInsuficienteQuandoPrimeiraLinhaNaoTemEstoque() {
        ItemPedido semEstoque = new ItemPedido("CANETA", 1_000, 6, 5, 20, false);
        ItemPedido disponivel = new ItemPedido("CADERNO", 3_000, 1, 5, 300, false);
        Pedido pedido = new Pedido(List.of(semEstoque, disponivel), "PR", false, null);

        assertFalse(pedido.estoqueSuficiente());
    }

    @Test
    void deveTerEstoqueInsuficienteQuandoUltimaLinhaNaoTemEstoque() {
        ItemPedido disponivel = new ItemPedido("CADERNO", 3_000, 1, 5, 300, false);
        ItemPedido semEstoque = new ItemPedido("CANETA", 1_000, 6, 5, 20, false);
        Pedido pedido = new Pedido(List.of(disponivel, semEstoque), "PR", false, null);

        assertFalse(pedido.estoqueSuficiente());
    }

    @Test
    void deveTerEstoqueInsuficienteQuandoLinhaDoMeioNaoTemEstoque() {
        ItemPedido primeiro = new ItemPedido("CADERNO", 3_000, 1, 5, 300, false);
        ItemPedido semEstoque = new ItemPedido("CANETA", 1_000, 6, 5, 20, false);
        ItemPedido ultimo = new ItemPedido("BORRACHA", 500, 1, 5, 10, false);
        Pedido pedido = new Pedido(List.of(primeiro, semEstoque, ultimo), "PR", false, null);

        assertFalse(pedido.estoqueSuficiente());
    }

    @Test
    void deveAvaliarEstoquePorLinhaComSkuRepetido() {
        ItemPedido item = new ItemPedido("CANETA", 1_000, 3, 5, 20, false);
        Pedido pedido = new Pedido(List.of(item, item), "PR", false, null);

        assertTrue(pedido.estoqueSuficiente());
    }

    @Test
    void deveTerEstoqueSuficienteParaLinhaInativaSemEstoque() {
        ItemPedido inativo = new ItemPedido("CANETA", 1_000, 0, 0, 20, false);
        Pedido pedido = new Pedido(List.of(inativo), "PR", false, null);

        assertTrue(pedido.estoqueSuficiente());
    }

    @Test
    void deveTerEstoqueSuficienteParaListaVazia() {
        Pedido pedido = new Pedido(List.of(), "PR", false, null);

        assertTrue(pedido.estoqueSuficiente());
    }
}
