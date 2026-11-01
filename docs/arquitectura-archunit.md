# ArchUnit (integración estructural, caja blanca)

**Criterio rúbrica:** pruebas de integración dinámicas estructurales — ficheros específicos, escenarios diversos, un aspecto por regla.

## Herramienta

| Campo | Valor |
|-------|-------|
| Dependencia | `archunit-junit5` |
| Clases | `src/test/java/.../quality/*ArchitectureTest.java` |
| Ejecución | incluidas en `mvn test` |
| Evidencia | código de las reglas + resultado Surefire en verde (no hay informe HTML propio) |

## Clases de prueba

- `AuthorArchitectureTest`
- `BookArchitectureTest`
- `CategoryArchitectureTest`
- `PublisherArchitectureTest`
- `UserArchitectureTest`
- `MapperArchitectureTest`

## Aspectos comprobados (un `@ArchTest` = un aspecto)

- La entidad vive en `..entity..`
- El `*ServiceImpl` vive en `..service.impl..` e implementa la interfaz del servicio
- El service depende del repository correspondiente
- La entidad **no** depende de service / controller / repository
- El service **no** depende de controllers

La evidencia es el propio código de esas reglas y que `mvn test` / `mvn test -Dtest="*ArchitectureTest"` las ejecuta sin fallos.

## Cómo regenerar

```bash
mvn test -Dtest="*ArchitectureTest"
```

## Nota

El perfil Maven `architecture` del `pom.xml` apunta a `**/architecture/**`; estas clases viven en `quality/` y se ejecutan con el Surefire normal.
