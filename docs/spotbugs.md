# SpotBugs (estático funcional, caja blanca)

**Criterio rúbrica:** unitarias estáticas funcionales — aplicar la técnica y derivar pruebas o issues.

## Herramienta

| Campo | Valor |
|-------|-------|
| Plugin | `spotbugs-maven-plugin` (fase `test`) |
| Filtro | `spotbugs-include.xml` |
| Informe | `target/spotbugsXml.xml` (también `target/spotbugs.xml`) |

SpotBugs en este proyecto **no genera un HTML de resumen legible** como JaCoCo o Checkstyle: la salida útil es el XML. La evidencia queda documentada aquí (tabla de hallazgos) y en las issues de GitHub.

## Cómo regenerar

```bash
mvn test
```

## Resultados: 15 hallazgos

Resumen del XML (`FindBugsSummary total_bugs='15'`, todos `priority_2`, categoría `MALICIOUS_CODE`). Tipos `EI_EXPOSE_REP` (devolver la colección interna) y `EI_EXPOSE_REP2` (guardar la referencia externa).

| Clase | Métodos | Tipo |
|-------|---------|------|
| Book | getAuthors, getCategories, getPublishers | EI_EXPOSE_REP |
| Book | setAuthors, setCategories, setPublishers | EI_EXPOSE_REP2 |
| Author | getBooks / setBooks | EI_EXPOSE_REP / REP2 |
| Category | getBooks / setBooks | EI_EXPOSE_REP / REP2 |
| Publisher | getBooks / setBooks | EI_EXPOSE_REP / REP2 |
| User | getRoles / setRoles / constructor | EI_EXPOSE_REP / REP2 |

Ejemplos de mensajes del informe: *“May expose internal representation by returning reference to mutable object”* / *“…by storing an externally mutable object”*.

## Pruebas derivadas

En `BookTest` (clases anidadas `ExposeRep` / `ExposeRep2`) se caracteriza el fallo: mutar el set del getter o el pasado al setter altera el estado de `Book`. Author, Publisher y User tienen asserts equivalentes (`assertSame` / mutación del set).

## Issues derivadas

| Issue | Hallazgo |
|-------|----------|
| [#12](https://github.com/pablojhdcoder/vvs-udc/issues/12) | `Book#getAuthors` |
| [#13](https://github.com/pablojhdcoder/vvs-udc/issues/13) | getters categories / publishers |
| [#14](https://github.com/pablojhdcoder/vvs-udc/issues/14) | setters Book |
| [#15](https://github.com/pablojhdcoder/vvs-udc/issues/15) | `Author#getBooks` / `setBooks` |

Pendientes (resto del equipo): misma familia en **Category**, **Publisher** y **User**.
