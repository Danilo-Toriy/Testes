package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AnaliseRiscoTest {

    @Test
    void deveLancarExcecaoQuandoTotalForNegativo() {
        AnaliseRisco risco = new AnaliseRisco();
        Cliente cliente = new Cliente(false, false, 1);

        assertThrows(IllegalArgumentException.class, () -> risco.avaliar(cliente, -1, false));
    }

    @Test
    void deveLancarExcecaoParaTotalNegativoAntesDeVerificarBloqueio() {
        AnaliseRisco risco = new AnaliseRisco();
        Cliente cliente = new Cliente(false, true, 1);

        assertThrows(IllegalArgumentException.class, () -> risco.avaliar(cliente, -1, false));
    }

    @Test
    void deveRecusarClienteBloqueado() {
        AnaliseRisco risco = new AnaliseRisco();
        Cliente cliente = new Cliente(false, true, 1);

        String resultado = risco.avaliar(cliente, 0, false);

        assertEquals("RECUSADO", resultado);
    }

    @Test
    void deveRecusarClienteBloqueadoMesmoComTotalAltoEEntregaExpressa() {
        AnaliseRisco risco = new AnaliseRisco();
        Cliente cliente = new Cliente(false, true, 0);

        String resultado = risco.avaliar(cliente, 600_000, true);

        assertEquals("RECUSADO", resultado);
    }

    @Test
    void deveAprovarClienteSemComprasComTotalZero() {
        AnaliseRisco risco = new AnaliseRisco();
        Cliente cliente = new Cliente(false, false, 0);

        String resultado = risco.avaliar(cliente, 0, false);

        assertEquals("APROVADO", resultado);
    }

    @Test
    void deveAprovarClienteSemComprasComTotalDeMilReais() {
        AnaliseRisco risco = new AnaliseRisco();
        Cliente cliente = new Cliente(false, false, 0);

        String resultado = risco.avaliar(cliente, 100_000, false);

        assertEquals("APROVADO", resultado);
    }

    @Test
    void deveEnviarParaRevisaoClienteSemComprasComTotalAcimaDeMilReais() {
        AnaliseRisco risco = new AnaliseRisco();
        Cliente cliente = new Cliente(false, false, 0);

        String resultado = risco.avaliar(cliente, 100_001, false);

        assertEquals("REVISAO", resultado);
    }

    @Test
    void deveEnviarParaRevisaoClienteSemComprasComEntregaExpressa() {
        AnaliseRisco risco = new AnaliseRisco();
        Cliente cliente = new Cliente(false, false, 0);

        String resultado = risco.avaliar(cliente, 1_000, true);

        assertEquals("REVISAO", resultado);
    }

    @Test
    void deveEnviarParaRevisaoClienteVipSemComprasComTotalAcimaDeMilReais() {
        AnaliseRisco risco = new AnaliseRisco();
        Cliente cliente = new Cliente(true, false, 0);

        String resultado = risco.avaliar(cliente, 100_001, false);

        assertEquals("REVISAO", resultado);
    }

    @Test
    void deveEnviarParaRevisaoClienteVipSemComprasComEntregaExpressa() {
        AnaliseRisco risco = new AnaliseRisco();
        Cliente cliente = new Cliente(true, false, 0);

        String resultado = risco.avaliar(cliente, 1_000, true);

        assertEquals("REVISAO", resultado);
    }

    @Test
    void deveAprovarClienteComComprasComTotalDeCincoMilReais() {
        AnaliseRisco risco = new AnaliseRisco();
        Cliente cliente = new Cliente(false, false, 1);

        String resultado = risco.avaliar(cliente, 500_000, false);

        assertEquals("APROVADO", resultado);
    }

    @Test
    void deveEnviarParaRevisaoClienteComumComComprasComTotalAcimaDeCincoMilReais() {
        AnaliseRisco risco = new AnaliseRisco();
        Cliente cliente = new Cliente(false, false, 1);

        String resultado = risco.avaliar(cliente, 500_001, false);

        assertEquals("REVISAO", resultado);
    }

    @Test
    void deveAprovarClienteVipComComprasComTotalAcimaDeCincoMilReais() {
        AnaliseRisco risco = new AnaliseRisco();
        Cliente cliente = new Cliente(true, false, 1);

        String resultado = risco.avaliar(cliente, 500_001, false);

        assertEquals("APROVADO", resultado);
    }

    @Test
    void deveAprovarClienteComComprasComTotalAcimaDeMilReaisAbaixoDoLimite() {
        AnaliseRisco risco = new AnaliseRisco();
        Cliente cliente = new Cliente(false, false, 1);

        String resultado = risco.avaliar(cliente, 100_001, false);

        assertEquals("APROVADO", resultado);
    }

    @Test
    void deveIgnorarEntregaExpressaParaClienteComCompras() {
        AnaliseRisco risco = new AnaliseRisco();
        Cliente cliente = new Cliente(false, false, 1);

        String resultado = risco.avaliar(cliente, 1_000, true);

        assertEquals("APROVADO", resultado);
    }

    @Test
    void deveAprovarClienteVipComComprasComEntregaExpressaETotalAlto() {
        AnaliseRisco risco = new AnaliseRisco();
        Cliente cliente = new Cliente(true, false, 3);

        String resultado = risco.avaliar(cliente, 500_001, true);

        assertEquals("APROVADO", resultado);
    }
}
