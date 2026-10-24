# Pruebas unitarias en Java con JUnit 5

- **Nombre:** Sebastián Castro Vásquez
- **Ficha:** 3292107
- **Cobertura de líneas en CarritoCompras:** 100 %

Bugs encontrados y corregidos:
- `CalculadoraNotas.estaAprobado`: usaba `>` en vez de `>=` (el 3.0 reprobaba).
- `CarritoCompras.agregarProducto`: usaba `cantidad < 0` en vez de `cantidad <= 0`.
Entrega final
