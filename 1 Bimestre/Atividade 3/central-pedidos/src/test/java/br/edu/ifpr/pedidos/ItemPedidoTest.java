package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ItemPedidoTest {

    @Test
    void deveCriarItemValido() {
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 2, 5, 1_000, true);

        assertAll(
                () -> assertEquals("LIVRO-JAVA", item.sku()),
                () -> assertEquals(10_000L, item.precoCentavos()),
                () -> assertEquals(2, item.quantidade()),
                () -> assertEquals(5, item.estoque()),
                () -> assertEquals(1_000, item.pesoGramas()),
                () -> assertTrue(item.fragil())
        );
    }

    @Test
    void deveCalcularTotalMultiplicandoPrecoPelaQuantidade() {
        ItemPedido item = new ItemPedido("CANETA", 2_500, 4, 10, 20, false);

        assertEquals(10_000L, item.totalCentavos());
    }

    @Test
    void deveCalcularTotalZeroParaLinhaInativa() {
        ItemPedido item = new ItemPedido("CANETA", 2_500, 0, 10, 20, false);

        assertEquals(0L, item.totalCentavos());
    }

    @Test
    void deveCalcularTotalNoLimiteMaximoDoDominio() {
        ItemPedido item = new ItemPedido("CANETA", 1_000_000, 100, 100, 20, false);

        assertEquals(100_000_000L, item.totalCentavos());
    }

    @Test
    void deveEstarDisponivelQuandoQuantidadeForMenorQueEstoque() {
        ItemPedido item = new ItemPedido("CANETA", 2_500, 4, 5, 20, false);

        assertTrue(item.disponivel());
    }

    @Test
    void deveEstarDisponivelQuandoQuantidadeForIgualAoEstoque() {
        ItemPedido item = new ItemPedido("CANETA", 2_500, 5, 5, 20, false);

        assertTrue(item.disponivel());
    }

    @Test
    void deveEstarIndisponivelQuandoQuantidadeForMaiorQueEstoque() {
        ItemPedido item = new ItemPedido("CANETA", 2_500, 6, 5, 20, false);

        assertFalse(item.disponivel());
    }

    @Test
    void deveEstarDisponivelQuandoLinhaInativaNaoTemEstoque() {
        ItemPedido item = new ItemPedido("CANETA", 2_500, 0, 0, 20, false);

        assertTrue(item.disponivel());
    }

    @Test
    void deveLancarExcecaoQuandoSkuForInvalido() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new ItemPedido(null, 1_000, 1, 1, 100, false)),
                () -> assertThrows(IllegalArgumentException.class, () -> new ItemPedido("", 1_000, 1, 1, 100, false)),
                () -> assertThrows(IllegalArgumentException.class, () -> new ItemPedido("   ", 1_000, 1, 1, 100, false))
        );
    }

    @Test
    void deveLancarExcecaoQuandoPrecoForInvalido() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new ItemPedido("SKU-1", 0, 1, 1, 100, false)),
                () -> assertThrows(IllegalArgumentException.class, () -> new ItemPedido("SKU-1", -1, 1, 1, 100, false)),
                () -> assertThrows(IllegalArgumentException.class, () -> new ItemPedido("SKU-1", 1_000_001, 1, 1, 100, false))
        );
    }

    @Test
    void deveAceitarPrecoNosLimites() {
        ItemPedido minimo = new ItemPedido("SKU-1", 1, 1, 1, 100, false);
        ItemPedido maximo = new ItemPedido("SKU-1", 1_000_000, 1, 1, 100, false);

        assertAll(
                () -> assertEquals(1L, minimo.precoCentavos()),
                () -> assertEquals(1_000_000L, maximo.precoCentavos())
        );
    }

    @Test
    void deveLancarExcecaoQuandoQuantidadeForInvalida() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new ItemPedido("SKU-1", 1_000, -1, 1, 100, false)),
                () -> assertThrows(IllegalArgumentException.class, () -> new ItemPedido("SKU-1", 1_000, 101, 1, 100, false))
        );
    }

    @Test
    void deveAceitarQuantidadeNosLimites() {
        ItemPedido minimo = new ItemPedido("SKU-1", 1_000, 0, 0, 100, false);
        ItemPedido maximo = new ItemPedido("SKU-1", 1_000, 100, 100, 100, false);

        assertAll(
                () -> assertEquals(0, minimo.quantidade()),
                () -> assertEquals(100, maximo.quantidade())
        );
    }

    @Test
    void deveLancarExcecaoQuandoEstoqueForNegativo() {
        assertThrows(IllegalArgumentException.class, () -> new ItemPedido("SKU-1", 1_000, 1, -1, 100, false));
    }

    @Test
    void deveAceitarEstoqueZero() {
        ItemPedido item = new ItemPedido("SKU-1", 1_000, 0, 0, 100, false);

        assertEquals(0, item.estoque());
    }

    @Test
    void deveLancarExcecaoQuandoPesoForInvalido() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new ItemPedido("SKU-1", 1_000, 1, 1, 0, false)),
                () -> assertThrows(IllegalArgumentException.class, () -> new ItemPedido("SKU-1", 1_000, 1, 1, -1, false)),
                () -> assertThrows(IllegalArgumentException.class, () -> new ItemPedido("SKU-1", 1_000, 1, 1, 100_001, false))
        );
    }

    @Test
    void deveAceitarPesoNosLimites() {
        ItemPedido minimo = new ItemPedido("SKU-1", 1_000, 1, 1, 1, false);
        ItemPedido maximo = new ItemPedido("SKU-1", 1_000, 1, 1, 100_000, false);

        assertAll(
                () -> assertEquals(1, minimo.pesoGramas()),
                () -> assertEquals(100_000, maximo.pesoGramas())
        );
    }
}
