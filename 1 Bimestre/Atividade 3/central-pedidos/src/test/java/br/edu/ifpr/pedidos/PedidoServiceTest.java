package br.edu.ifpr.pedidos;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PedidoServiceTest {
    @Test
    void deveFecharPedidoDeClienteComumComFreteDoParanaEPagamentoAprovado() {
        // 1. Preparar: cliente comum, uma compra anterior e item disponível de R$ 100,00.
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        // Simula o pagamento e registra as cobranças, sem banco ou serviço externo.
        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            return true;
        });

        // 2. Executar: percorrer um caminho completo do fechamento.
        ResultadoPedido resultado = service.fechar(pedido, cliente);

        // 3. Verificar: sem desconto; frete de R$ 12,00; total de R$ 112,00.
        assertAll(
            () -> assertEquals("PAGO", resultado.status()),
            () -> assertEquals(10_000L, resultado.subtotalCentavos()),
            () -> assertEquals(0L, resultado.descontoCentavos()),
            () -> assertEquals(1_200L, resultado.freteCentavos()),
            () -> assertEquals(11_200L, resultado.totalCentavos()),
            // A lista comprova uma única cobrança, com o valor correto.
            () -> assertEquals(List.of(11_200L), cobrancas)
        );
    }

    @Test
    void deveLancarExcecaoQuandoProcessadorForNulo() {
        assertThrows(NullPointerException.class, () -> new PedidoService(null));
    }

    @Test
    void deveLancarExcecaoQuandoPedidoForNulo() {
        PedidoService service = new PedidoService(total -> true);
        Cliente cliente = new Cliente(false, false, 1);

        assertThrows(NullPointerException.class, () -> service.fechar(null, cliente));
    }

    @Test
    void deveLancarExcecaoQuandoClienteForNulo() {
        PedidoService service = new PedidoService(total -> true);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        assertThrows(NullPointerException.class, () -> service.fechar(pedido, null));
    }

    @Test
    void deveRetornarBloqueadoSemCobrarClienteBloqueado() {
        Cliente cliente = new Cliente(false, true, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            return true;
        });

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertAll(
                () -> assertEquals("BLOQUEADO", resultado.status()),
                () -> assertEquals(0L, resultado.subtotalCentavos()),
                () -> assertEquals(0L, resultado.descontoCentavos()),
                () -> assertEquals(0L, resultado.freteCentavos()),
                () -> assertEquals(0L, resultado.totalCentavos()),
                () -> assertTrue(cobrancas.isEmpty())
        );
    }

    @Test
    void deveRetornarBloqueadoAntesDeAvaliarItensECupom() {
        Cliente cliente = new Cliente(false, true, 1);
        Pedido pedido = new Pedido(List.of(), "PR", false, "PROMO");

        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            return true;
        });

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertAll(
                () -> assertEquals("BLOQUEADO", resultado.status()),
                () -> assertEquals(0L, resultado.totalCentavos()),
                () -> assertTrue(cobrancas.isEmpty())
        );
    }

    @Test
    void deveLancarExcecaoQuandoPedidoNaoTiverLinhas() {
        Cliente cliente = new Cliente(false, false, 1);
        Pedido pedido = new Pedido(List.of(), "PR", false, null);

        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            return true;
        });

        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> service.fechar(pedido, cliente)),
                () -> assertTrue(cobrancas.isEmpty())
        );
    }

    @Test
    void deveLancarExcecaoQuandoTodasAsLinhasEstiveremInativas() {
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido inativo = new ItemPedido("LIVRO-JAVA", 10_000, 0, 0, 1_000, false);
        Pedido pedido = new Pedido(List.of(inativo), "PR", false, null);

        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            return true;
        });

        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> service.fechar(pedido, cliente)),
                () -> assertTrue(cobrancas.isEmpty())
        );
    }

    @Test
    void deveRetornarSemEstoqueSemCobrarQuandoQuantidadeExcederEstoque() {
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 5, 4, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            return true;
        });

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertAll(
                () -> assertEquals("SEM_ESTOQUE", resultado.status()),
                () -> assertEquals(0L, resultado.subtotalCentavos()),
                () -> assertEquals(0L, resultado.descontoCentavos()),
                () -> assertEquals(0L, resultado.freteCentavos()),
                () -> assertEquals(0L, resultado.totalCentavos()),
                () -> assertTrue(cobrancas.isEmpty())
        );
    }

    @Test
    void deveRetornarSemEstoqueQuandoUltimaLinhaNaoTemEstoque() {
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido disponivel = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        ItemPedido semEstoque = new ItemPedido("CANETA", 1_000, 6, 5, 20, false);
        Pedido pedido = new Pedido(List.of(disponivel, semEstoque), "PR", false, null);

        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            return true;
        });

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertAll(
                () -> assertEquals("SEM_ESTOQUE", resultado.status()),
                () -> assertEquals(0L, resultado.totalCentavos()),
                () -> assertTrue(cobrancas.isEmpty())
        );
    }

    @Test
    void deveRetornarSemEstoqueAntesDeValidarCupom() {
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 5, 4, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, "PROMO");

        PedidoService service = new PedidoService(total -> true);

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertEquals("SEM_ESTOQUE", resultado.status());
    }

    @Test
    void deveLancarExcecaoQuandoCupomForDesconhecido() {
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, "PROMO");

        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            return true;
        });

        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> service.fechar(pedido, cliente)),
                () -> assertTrue(cobrancas.isEmpty())
        );
    }

    @Test
    void deveFecharPedidoDeClienteVipComDescontoEMetadeDoFrete() {
        Cliente cliente = new Cliente(true, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            return true;
        });

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertAll(
                () -> assertEquals("PAGO", resultado.status()),
                () -> assertEquals(10_000L, resultado.subtotalCentavos()),
                () -> assertEquals(1_000L, resultado.descontoCentavos()),
                () -> assertEquals(600L, resultado.freteCentavos()),
                () -> assertEquals(9_600L, resultado.totalCentavos()),
                () -> assertEquals(List.of(9_600L), cobrancas)
        );
    }

    @Test
    void deveTruncarDescontoDeClienteVipAoFecharPedido() {
        Cliente cliente = new Cliente(true, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_009, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            return true;
        });

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertAll(
                () -> assertEquals("PAGO", resultado.status()),
                () -> assertEquals(10_009L, resultado.subtotalCentavos()),
                () -> assertEquals(1_000L, resultado.descontoCentavos()),
                () -> assertEquals(600L, resultado.freteCentavos()),
                () -> assertEquals(9_609L, resultado.totalCentavos()),
                () -> assertEquals(List.of(9_609L), cobrancas)
        );
    }

    @Test
    void deveFecharPedidoDeClienteComumComDescontoEFreteGratuito() {
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("NOTEBOOK", 50_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            return true;
        });

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertAll(
                () -> assertEquals("PAGO", resultado.status()),
                () -> assertEquals(50_000L, resultado.subtotalCentavos()),
                () -> assertEquals(2_500L, resultado.descontoCentavos()),
                () -> assertEquals(0L, resultado.freteCentavos()),
                () -> assertEquals(47_500L, resultado.totalCentavos()),
                () -> assertEquals(List.of(47_500L), cobrancas)
        );
    }

    @Test
    void deveAplicarCupomExtra10NormalizadoAoFecharPedido() {
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 20_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "SP", false, " extra10 ");

        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            return true;
        });

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertAll(
                () -> assertEquals("PAGO", resultado.status()),
                () -> assertEquals(20_000L, resultado.subtotalCentavos()),
                () -> assertEquals(2_000L, resultado.descontoCentavos()),
                () -> assertEquals(2_000L, resultado.freteCentavos()),
                () -> assertEquals(20_000L, resultado.totalCentavos()),
                () -> assertEquals(List.of(20_000L), cobrancas)
        );
    }

    @Test
    void deveAplicarCupomBemvindoParaClienteSemCompras() {
        Cliente cliente = new Cliente(false, false, 0);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, "BEMVINDO");

        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            return true;
        });

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertAll(
                () -> assertEquals("PAGO", resultado.status()),
                () -> assertEquals(10_000L, resultado.subtotalCentavos()),
                () -> assertEquals(2_000L, resultado.descontoCentavos()),
                () -> assertEquals(1_200L, resultado.freteCentavos()),
                () -> assertEquals(9_200L, resultado.totalCentavos()),
                () -> assertEquals(List.of(9_200L), cobrancas)
        );
    }

    @Test
    void deveSomarFreteExpressoEFragilidadeAoFecharPedido() {
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("VASO", 10_000, 1, 5, 1_000, true);
        Pedido pedido = new Pedido(List.of(item), "PR", true, null);

        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            return true;
        });

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertAll(
                () -> assertEquals("PAGO", resultado.status()),
                () -> assertEquals(10_000L, resultado.subtotalCentavos()),
                () -> assertEquals(0L, resultado.descontoCentavos()),
                () -> assertEquals(3_200L, resultado.freteCentavos()),
                () -> assertEquals(13_200L, resultado.totalCentavos()),
                () -> assertEquals(List.of(13_200L), cobrancas)
        );
    }

    @Test
    void deveAvaliarEstoquePorLinhaAoFecharPedidoComSkuRepetido() {
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 3, 5, 500, false);
        Pedido pedido = new Pedido(List.of(item, item), "PR", false, null);

        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            return true;
        });

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertAll(
                () -> assertEquals("PAGO", resultado.status()),
                () -> assertEquals(60_000L, resultado.subtotalCentavos()),
                () -> assertEquals(3_000L, resultado.descontoCentavos()),
                () -> assertEquals(0L, resultado.freteCentavos()),
                () -> assertEquals(57_000L, resultado.totalCentavos()),
                () -> assertEquals(List.of(57_000L), cobrancas)
        );
    }

    @Test
    void deveEnviarParaRevisaoClienteSemComprasComEntregaExpressaSemCobrar() {
        Cliente cliente = new Cliente(false, false, 0);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", true, null);

        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            return true;
        });

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertAll(
                () -> assertEquals("REVISAO", resultado.status()),
                () -> assertEquals(10_000L, resultado.subtotalCentavos()),
                () -> assertEquals(0L, resultado.descontoCentavos()),
                () -> assertEquals(2_700L, resultado.freteCentavos()),
                () -> assertEquals(12_700L, resultado.totalCentavos()),
                () -> assertTrue(cobrancas.isEmpty())
        );
    }

    @Test
    void deveEnviarParaRevisaoClienteSemComprasComTotalAcimaDeMilReaisSemCobrar() {
        Cliente cliente = new Cliente(false, false, 0);
        ItemPedido item = new ItemPedido("NOTEBOOK", 200_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            return true;
        });

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertAll(
                () -> assertEquals("REVISAO", resultado.status()),
                () -> assertEquals(200_000L, resultado.subtotalCentavos()),
                () -> assertEquals(10_000L, resultado.descontoCentavos()),
                () -> assertEquals(0L, resultado.freteCentavos()),
                () -> assertEquals(190_000L, resultado.totalCentavos()),
                () -> assertTrue(cobrancas.isEmpty())
        );
    }

    @Test
    void deveEnviarParaRevisaoClienteComumComComprasComTotalAcimaDeCincoMilReaisSemCobrar() {
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("NOTEBOOK", 600_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            return true;
        });

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertAll(
                () -> assertEquals("REVISAO", resultado.status()),
                () -> assertEquals(600_000L, resultado.subtotalCentavos()),
                () -> assertEquals(30_000L, resultado.descontoCentavos()),
                () -> assertEquals(0L, resultado.freteCentavos()),
                () -> assertEquals(570_000L, resultado.totalCentavos()),
                () -> assertTrue(cobrancas.isEmpty())
        );
    }

    @Test
    void deveCobrarClienteVipComComprasComTotalAcimaDeCincoMilReais() {
        Cliente cliente = new Cliente(true, false, 1);
        ItemPedido item = new ItemPedido("NOTEBOOK", 600_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            return true;
        });

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertAll(
                () -> assertEquals("PAGO", resultado.status()),
                () -> assertEquals(600_000L, resultado.subtotalCentavos()),
                () -> assertEquals(60_000L, resultado.descontoCentavos()),
                () -> assertEquals(0L, resultado.freteCentavos()),
                () -> assertEquals(540_000L, resultado.totalCentavos()),
                () -> assertEquals(List.of(540_000L), cobrancas)
        );
    }

    @Test
    void deveRetornarPagamentoRecusadoComValoresCalculadosSemRepetirCobranca() {
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            return false;
        });

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertAll(
                () -> assertEquals("PAGAMENTO_RECUSADO", resultado.status()),
                () -> assertEquals(10_000L, resultado.subtotalCentavos()),
                () -> assertEquals(0L, resultado.descontoCentavos()),
                () -> assertEquals(1_200L, resultado.freteCentavos()),
                () -> assertEquals(11_200L, resultado.totalCentavos()),
                () -> assertEquals(List.of(11_200L), cobrancas)
        );
    }

    @Test
    void devePagarAposDuasIndisponibilidadesDoProcessador() {
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            if (cobrancas.size() < 3) throw new IllegalStateException("Indisponível");
            return true;
        });

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertAll(
                () -> assertEquals("PAGO", resultado.status()),
                () -> assertEquals(11_200L, resultado.totalCentavos()),
                () -> assertEquals(List.of(11_200L, 11_200L, 11_200L), cobrancas)
        );
    }

    @Test
    void deveRetornarPagamentoRecusadoQuandoProcessadorEsgotarTresTentativas() {
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            throw new IllegalStateException("Indisponível");
        });

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertAll(
                () -> assertEquals("PAGAMENTO_RECUSADO", resultado.status()),
                () -> assertEquals(10_000L, resultado.subtotalCentavos()),
                () -> assertEquals(0L, resultado.descontoCentavos()),
                () -> assertEquals(1_200L, resultado.freteCentavos()),
                () -> assertEquals(11_200L, resultado.totalCentavos()),
                () -> assertEquals(3, cobrancas.size())
        );
    }

    @Test
    void devePropagarExcecaoInesperadaDoProcessadorDePagamento() {
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            throw new UnsupportedOperationException("Falha inesperada");
        });

        assertAll(
                () -> assertThrows(UnsupportedOperationException.class, () -> service.fechar(pedido, cliente)),
                () -> assertEquals(1, cobrancas.size())
        );
    }
}
