package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PagamentoServiceTest {

    @Test
    void deveLancarExcecaoQuandoProcessadorForNulo() {
        assertThrows(NullPointerException.class, () -> new PagamentoService(null));
    }

    @Test
    void deveLancarExcecaoQuandoTotalForZero() {
        List<Long> chamadas = new ArrayList<>();
        PagamentoService service = new PagamentoService(total -> {
            chamadas.add(total);
            return true;
        });

        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> service.pagar(0, 3)),
                () -> assertTrue(chamadas.isEmpty())
        );
    }

    @Test
    void deveLancarExcecaoQuandoTotalForNegativo() {
        List<Long> chamadas = new ArrayList<>();
        PagamentoService service = new PagamentoService(total -> {
            chamadas.add(total);
            return true;
        });

        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> service.pagar(-1, 3)),
                () -> assertTrue(chamadas.isEmpty())
        );
    }

    @Test
    void deveLancarExcecaoQuandoLimiteDeTentativasForInvalido() {
        List<Long> chamadas = new ArrayList<>();
        PagamentoService service = new PagamentoService(total -> {
            chamadas.add(total);
            return true;
        });

        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> service.pagar(5_000, 0)),
                () -> assertThrows(IllegalArgumentException.class, () -> service.pagar(5_000, -1)),
                () -> assertThrows(IllegalArgumentException.class, () -> service.pagar(5_000, 4)),
                () -> assertTrue(chamadas.isEmpty())
        );
    }

    @Test
    void deveAprovarPagamentoNaPrimeiraTentativa() {
        List<Long> chamadas = new ArrayList<>();
        PagamentoService service = new PagamentoService(total -> {
            chamadas.add(total);
            return true;
        });

        boolean resultado = service.pagar(5_000, 3);

        assertAll(
                () -> assertTrue(resultado),
                () -> assertEquals(List.of(5_000L), chamadas)
        );
    }

    @Test
    void deveAprovarPagamentoComLimiteDeUmaTentativa() {
        List<Long> chamadas = new ArrayList<>();
        PagamentoService service = new PagamentoService(total -> {
            chamadas.add(total);
            return true;
        });

        boolean resultado = service.pagar(1, 1);

        assertAll(
                () -> assertTrue(resultado),
                () -> assertEquals(List.of(1L), chamadas)
        );
    }

    @Test
    void deveRecusarPagamentoImediatamenteSemRepetir() {
        List<Long> chamadas = new ArrayList<>();
        PagamentoService service = new PagamentoService(total -> {
            chamadas.add(total);
            return false;
        });

        boolean resultado = service.pagar(5_000, 3);

        assertAll(
                () -> assertFalse(resultado),
                () -> assertEquals(List.of(5_000L), chamadas)
        );
    }

    @Test
    void deveAprovarNaSegundaTentativaAposIndisponibilidade() {
        List<Long> chamadas = new ArrayList<>();
        PagamentoService service = new PagamentoService(total -> {
            chamadas.add(total);
            if (chamadas.size() < 2) throw new IllegalStateException("Indisponível");
            return true;
        });

        boolean resultado = service.pagar(5_000, 3);

        assertAll(
                () -> assertTrue(resultado),
                () -> assertEquals(List.of(5_000L, 5_000L), chamadas)
        );
    }

    @Test
    void deveAprovarNaTerceiraTentativaAposDuasIndisponibilidades() {
        List<Long> chamadas = new ArrayList<>();
        PagamentoService service = new PagamentoService(total -> {
            chamadas.add(total);
            if (chamadas.size() < 3) throw new IllegalStateException("Indisponível");
            return true;
        });

        boolean resultado = service.pagar(5_000, 3);

        assertAll(
                () -> assertTrue(resultado),
                () -> assertEquals(List.of(5_000L, 5_000L, 5_000L), chamadas)
        );
    }

    @Test
    void deveRecusarAposEsgotarTresTentativasIndisponiveis() {
        List<Long> chamadas = new ArrayList<>();
        PagamentoService service = new PagamentoService(total -> {
            chamadas.add(total);
            throw new IllegalStateException("Indisponível");
        });

        boolean resultado = service.pagar(5_000, 3);

        assertAll(
                () -> assertFalse(resultado),
                () -> assertEquals(3, chamadas.size())
        );
    }

    @Test
    void deveRecusarAposEsgotarUmaTentativaIndisponivel() {
        List<Long> chamadas = new ArrayList<>();
        PagamentoService service = new PagamentoService(total -> {
            chamadas.add(total);
            throw new IllegalStateException("Indisponível");
        });

        boolean resultado = service.pagar(5_000, 1);

        assertAll(
                () -> assertFalse(resultado),
                () -> assertEquals(1, chamadas.size())
        );
    }

    @Test
    void deveRecusarAposEsgotarDuasTentativasIndisponiveis() {
        List<Long> chamadas = new ArrayList<>();
        PagamentoService service = new PagamentoService(total -> {
            chamadas.add(total);
            throw new IllegalStateException("Indisponível");
        });

        boolean resultado = service.pagar(5_000, 2);

        assertAll(
                () -> assertFalse(resultado),
                () -> assertEquals(2, chamadas.size())
        );
    }

    @Test
    void deveRecusarSemRepetirAposIndisponibilidadeSeguidaDeRecusa() {
        List<Long> chamadas = new ArrayList<>();
        PagamentoService service = new PagamentoService(total -> {
            chamadas.add(total);
            if (chamadas.size() < 2) throw new IllegalStateException("Indisponível");
            return false;
        });

        boolean resultado = service.pagar(5_000, 3);

        assertAll(
                () -> assertFalse(resultado),
                () -> assertEquals(2, chamadas.size())
        );
    }

    @Test
    void devePropagarExcecaoDiferenteDeIndisponibilidade() {
        List<Long> chamadas = new ArrayList<>();
        PagamentoService service = new PagamentoService(total -> {
            chamadas.add(total);
            throw new UnsupportedOperationException("Falha inesperada");
        });

        assertAll(
                () -> assertThrows(UnsupportedOperationException.class, () -> service.pagar(5_000, 3)),
                () -> assertEquals(1, chamadas.size())
        );
    }

    @Test
    void devePropagarExcecaoDiferenteDeIndisponibilidadeNaSegundaTentativa() {
        List<Long> chamadas = new ArrayList<>();
        PagamentoService service = new PagamentoService(total -> {
            chamadas.add(total);
            if (chamadas.size() < 2) throw new IllegalStateException("Indisponível");
            throw new UnsupportedOperationException("Falha inesperada");
        });

        assertAll(
                () -> assertThrows(UnsupportedOperationException.class, () -> service.pagar(5_000, 3)),
                () -> assertEquals(2, chamadas.size())
        );
    }
}
