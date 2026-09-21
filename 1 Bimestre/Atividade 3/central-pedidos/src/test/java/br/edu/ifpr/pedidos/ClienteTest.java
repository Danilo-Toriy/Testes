package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {

    @Test
    void deveCriarClienteComumSemHistorico() {
        Cliente cliente = new Cliente(false, false, 0);

        assertAll(
                () -> assertFalse(cliente.vip()),
                () -> assertFalse(cliente.bloqueado()),
                () -> assertEquals(0, cliente.comprasAnteriores())
        );
    }

    @Test
    void deveCriarClienteVipBloqueadoComHistorico() {
        Cliente cliente = new Cliente(true, true, 5);

        assertAll(
                () -> assertTrue(cliente.vip()),
                () -> assertTrue(cliente.bloqueado()),
                () -> assertEquals(5, cliente.comprasAnteriores())
        );
    }

    @Test
    void deveLancarExcecaoQuandoHistoricoForNegativo() {
        assertThrows(IllegalArgumentException.class, () -> new Cliente(false, false, -1));
    }
}
