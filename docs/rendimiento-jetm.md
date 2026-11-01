# Rendimiento JETM (no funcionales, caja blanca)

**Criterio rúbrica:** pruebas de sistema dinámicas no funcionales — a nivel de **unidad** e **integración**, nombre representativo y localización adecuada.

## Herramienta

| Campo | Valor |
|-------|-------|
| Dependencia | `jetm` |
| API | `EtmMonitor` / `EtmPoint` / `SimpleTextRenderer` |
| Ejecución | incluidas en `mvn test` |
| Evidencia | clases `*PerformanceTest` + salida de texto en consola al pasar los tests (JETM no genera HTML de informe en este proyecto) |

## Pruebas

| Nivel | Clases |
|-------|--------|
| Unidad | `quality/AuthorServiceImplPerformanceTest`, `BookServiceImplPerformanceTest`, `PublisherServiceImplPerformanceTest`, `UserServiceImplPerformanceTest` |
| Integración | `integration/AuthorServiceIntegrationPerformanceTest`, `BookServiceIntegrationPerformanceTest`, `PublisherServiceIntegrationPerformanceTest`, `UserServiceIntegrationPerformanceTest` |

## Qué miden

Operaciones representativas (`findPaginated`, `findAll` + mapper, `loadUserByUsername`, etc.) con catálogos de distinto tamaño. El monitor JETM registra puntos de medición; el assert comprueba que el informe de texto contiene esos puntos (observabilidad del coste temporal a dos niveles: mock vs H2).

Category no tiene `*PerformanceTest` propio: su servicio no expone paginación equivalente; el patrón queda cubierto en Author/Book/Publisher/User.

## Cómo regenerar

```bash
mvn test -Dtest="*PerformanceTest"
```
