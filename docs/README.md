# Informes de calidad (práctica VVS)

Reflexión exigida por la rúbrica. Los HTML/XML de las herramientas se regeneran en `target/` (no se versionan).

Capturas PNG solo donde el informe HTML es claro y útil: JaCoCo, PITest y Checkstyle (`capturas/`). SpotBugs, ArchUnit y JETM se documentan en texto (XML, reglas y clases de test); las issues de GitHub se enlazan desde el informe de SpotBugs.

| Informe | Criterio de la rúbrica | Regenerar |
|---------|------------------------|-----------|
| [cobertura.md](cobertura.md) | Análisis de cobertura | `mvn test` → `target/site/jacoco/index.html` |
| [mutacion.md](mutacion.md) | Calidad de las pruebas (mutación) | `mvn clean test-compile org.pitest:pitest-maven:mutationCoverage` → `target/pit-reports/index.html` |
| [spotbugs.md](spotbugs.md) | Unitarias estáticas funcionales | `mvn test` → `target/spotbugsXml.xml` |
| [checkstyle.md](checkstyle.md) | Unitarias estáticas no funcionales | `mvn test` → `target/site/checkstyle.html` |
| [arquitectura-archunit.md](arquitectura-archunit.md) | Integración estructurales (ArchUnit) | `mvn test` (`*ArchitectureTest`) |
| [rendimiento-jetm.md](rendimiento-jetm.md) | Sistema / no funcionales (JETM) | `mvn test` (`*PerformanceTest`) |

## Alcance

Unidades de dominio: **Author**, **Book**, **Category**, **Publisher**, **User**/**Role**, **Mapper**, sus `*ServiceImpl`, integración H2 y `CategoryController` como muestra de capa web.

## Comandos

```bash
mvn test
mvn clean test-compile org.pitest:pitest-maven:mutationCoverage
```
