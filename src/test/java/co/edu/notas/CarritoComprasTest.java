package co.edu.notas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/*
 * ENTREGA DEL TALLER
 * Nombre completo: Sebastián Castro Vásquez
 * 1. Bug encontrado (método y línea de CarritoCompras.java): método agregarProducto, validación de cantidad: usaba "cantidad < 0" en vez de "cantidad <= 0", por eso dejaba pasar cantidad 0 (viola R1).
 * 2. Prueba que lo detectó (nombre del método): agregarProducto_cantidadCero_lanzaExcepcion
 * 3. Sugerencia de la IA que rechacé o corregí, y por qué
 *    (si todas eran correctas: cómo verifiqué una de ellas con las reglas): verifiqué calcularTotal_conEnvioPagado_incluyeIvaYEnvio con la regla R6: 100.000 × 1,19 = 119.000, que es menor a 150.000, entonces se suma el envío de 12.000 y da 131.000.
 */
class CarritoComprasTest {

    private CarritoCompras carrito;

    @BeforeEach
    void setUp() {
        carrito = new CarritoCompras();
    }

    // ---------- R1: validación de productos ----------
    @Test
    @DisplayName("Nombre vacío lanza excepción")
    void agregarProducto_nombreVacio_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto("", 1_000, 1));
    }

    @Test
    @DisplayName("Nombre nulo lanza excepción")
    void agregarProducto_nombreNulo_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto(null, 1_000, 1));
    }

    @Test
    @DisplayName("Precio cero lanza excepción")
    void agregarProducto_precioCero_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto("Lápiz", 0, 1));
    }

    @Test
    @DisplayName("Precio negativo lanza excepción")
    void agregarProducto_precioNegativo_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto("Lápiz", -500, 1));
    }

    @Test
    @DisplayName("Cantidad cero lanza excepción")
    void agregarProducto_cantidadCero_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto("Lápiz", 1_000, 0));
    }

    @Test
    @DisplayName("Cantidad negativa lanza excepción")
    void agregarProducto_cantidadNegativa_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto("Lápiz", 1_000, -2));
    }

    @Test
    @DisplayName("Producto válido se agrega al carrito")
    void agregarProducto_datosValidos_aumentaCantidadDeItems() {
        carrito.agregarProducto("Cuaderno", 8_000, 1);
        assertEquals(1, carrito.cantidadDeItems());
    }

    // ---------- R2: subtotal ----------
    @Test
    @DisplayName("Carrito vacío: subtotal 0 y total 0")
    void carritoVacio_subtotalYTotal_sonCero() {
        assertEquals(0, carrito.calcularSubtotal(), 0.001);
        assertEquals(0, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("Subtotal = precio × cantidad de cada producto")
    void calcularSubtotal_variosProductos_sumaCorrecta() {
        carrito.agregarProducto("Cuaderno", 8_000, 3);
        carrito.agregarProducto("Esfero", 2_000, 5);
        assertEquals(34_000, carrito.calcularSubtotal(), 0.001);
    }

    // ---------- R3, R4, R5: cupones ----------
    @Test
    @DisplayName("SENA10 descuenta 10 % del subtotal")
    void calcularDescuento_sena10_diezPorCiento() {
        carrito.agregarProducto("Libro", 100_000, 1);
        carrito.aplicarCupon("SENA10");
        assertEquals(10_000, carrito.calcularDescuento(), 0.001);
    }

    @Test
    @DisplayName("BIENVENIDO con subtotal 50.000 descuenta 5.000")
    void calcularDescuento_bienvenidoEnElLimite_cincoMil() {
        carrito.agregarProducto("Mochila", 50_000, 1);
        carrito.aplicarCupon("BIENVENIDO");
        assertEquals(5_000, carrito.calcularDescuento(), 0.001);
    }

    @Test
    @DisplayName("BIENVENIDO con subtotal 49.999 no descuenta")
    void calcularDescuento_bienvenidoBajoElLimite_cero() {
        carrito.agregarProducto("Mochila", 49_999, 1);
        carrito.aplicarCupon("BIENVENIDO");
        assertEquals(0, carrito.calcularDescuento(), 0.001);
    }

    @Test
    @DisplayName("Sin cupón no hay descuento")
    void calcularDescuento_sinCupon_cero() {
        carrito.agregarProducto("Libro", 100_000, 1);
        assertEquals(0, carrito.calcularDescuento(), 0.001);
    }

    @Test
    @DisplayName("Cupón inválido lanza excepción")
    void aplicarCupon_codigoInvalido_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.aplicarCupon("FALSO123"));
    }

    @Test
    @DisplayName("Cupón nulo lanza excepción")
    void aplicarCupon_codigoNulo_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.aplicarCupon(null));
    }

    // ---------- R6: total ----------
    @Test
    @DisplayName("Total con envío pagado: 100.000 → 131.000")
    void calcularTotal_conEnvioPagado_incluyeIvaYEnvio() {
        carrito.agregarProducto("Libro", 100_000, 1);
        assertEquals(131_000, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("Total con envío gratis y SENA10: 500.000 → 535.500")
    void calcularTotal_envioGratisConSena10_535500() {
        carrito.agregarProducto("Monitor", 500_000, 1);
        carrito.aplicarCupon("SENA10");
        assertEquals(535_500, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("Total con BIENVENIDO: (60.000 − 5.000) × 1,19 + 12.000 = 77.450")
    void calcularTotal_conBienvenido_77450() {
        carrito.agregarProducto("Kit escolar", 60_000, 1);
        carrito.aplicarCupon("BIENVENIDO");
        assertEquals(77_450, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("Envío gratis sin cupón: 200.000 → 238.000")
    void calcularTotal_sinCuponEnvioGratis_238000() {
        carrito.agregarProducto("Calculadora", 200_000, 1);
        assertEquals(238_000, carrito.calcularTotal(), 0.001);
    }
}