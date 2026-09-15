# Motor de desafíos

El Motor guarda el catálogo de desafíos y el ciclo de vida de los intentos. No corrige contenido y no otorga recompensas.

## Lenguaje

### Catálogo

**Desafío**:
Plantilla reutilizable con título, descripción, dificultad, tipo, reintentos y duración máxima.
_Evitar_: Ejercicio

**Dificultad**:
Nivel de un desafío: BASIC, MEDIUM o ADVANCED.
_Evitar_: BASICO, MEDIO, AVANZADO

**Tipo de desafío**:
Dice si el desafío es PRACTICO o TEORICO. Define qué servicio lo corrige.
_Evitar_: Categoría, Modalidad

**Reintentos permitidos**:
Tope de intentos extra (0 a 3) que trae el desafío. Roadmap cuenta cuántos van y decide el efecto, el Motor solo abre y cierra intentos.
_Evitar_: Oportunidades

**Borrado lógico**:
Un desafío borrado se oculta del catálogo y no se puede asignar más. Los intentos abiertos y el historial se conservan.
_Evitar_: Borrado físico, Eliminar

**Catálogo propio**:
Desafíos de un profesor. Solo él los ve y edita.
_Evitar_: Mis desafíos

**Catálogo global**:
Todos los desafíos de la plataforma. Solo el admin lo gestiona.
_Evitar_: Catálogo general

**Desafío de recuperación**:
Desafío común con una marca que dice que sirve para recuperar vidas. El Motor guarda la marca y la replica, Roadmap arma la pool por curso y aplica las reglas.
_Evitar_: Desafío especial, Desafío de vida

**Estado del desafío**:
Un desafío está en borrador o publicado. Publicar exige contenido cargado en el corrector, y solo lo publicado se asigna.
_Evitar_: Activo, Visible

### Ejecución

**Intento**:
Apertura de un desafío por un alumno, con el contenido entregado y sus marcas de inicio y fin. Termina en una corrección y cada reintento es un intento nuevo.
_Evitar_: Entrega, Envío, Respuesta, Sesión

**Corrección**:
Veredicto del servicio Teórico o Práctico sobre un intento (aprobado/reprobado + puntaje). El otro servicio la decide, el Motor solo la guarda.
_Evitar_: Nota, Score, Calificación

**Resultado del intento**:
Intento cerrado con su corrección, el tiempo usado y el contexto del curso replicado. El Motor lo publica en eventos y no calcula XP ni monedas.
_Evitar_: Nota final

**Estado del intento**:
Un intento está abierto, en corrección o cerrado. El reloj corre en abierto y se frena al entregar.
_Evitar_: Pendiente, En proceso

**Puntualidad**:
Marca que dice si la entrega llegó a tiempo o tarde según las fechas de Roadmap. El Motor la publica, Roadmap decide la penalidad.
_Evitar_: Penalidad, Descuento

**Vencido**:
Intento que cerró el plazo sin entrega. El Motor lo marca y lo publica sin opinar sobre vidas.
_Evitar_: Fallado, Abandonado

**Versión del desafío**:
Foto del desafío al abrir el intento. Los intentos abiertos conservan la vieja, los nuevos usan la nueva.
_Evitar_: Copia, Snapshot

### Referenciado, no propio

**Asignación**:
Unión única entre un desafío y el roadmap de un curso (orden, fechas, obligatoriedad). Un mismo desafío aparece como máximo una vez en cada roadmap; Roadmap/Cursos define la asignación y el Motor nunca la guarda.
_Evitar_: Desafío de curso

**Elegibilidad**:
Decisión de Roadmap sobre si un alumno puede abrir un desafío asignado en un curso, acompañada por el motivo y el contexto de la asignación.
_Evitar_: Permiso del Motor, Desbloqueo

**Obligatorio**:
Atributo de la asignación que dice si el desafío es obligatorio en ese curso. Roadmap lo decide, el Motor solo lo replica en eventos.
_Evitar_: Requerido

**Contenido teórico**:
Preguntas y respuestas del desafío teórico. Lo define el servicio Teórico, el Motor nunca lo guarda.
_Evitar_: Ejercicio teórico

**Contenido práctico**:
Consigna de código y casos de prueba del desafío práctico. Lo define el servicio Práctico, el Motor nunca lo guarda.
_Evitar_: Ejercicio práctico

**Score de uso de IA**:
Puntaje del evaluador sobre cómo el alumno usó la IA en un intento. Vive atado al intento, el Motor lo recibe y lo replica sin calcularlo.
_Evitar_: Nota de IA, Bonus de IA

**Ítem activo**:
Bonificador de Mercado marcado en uso por un alumno, con estados activo e inactivo. Mercado lo posee, los consumidores lo consultan y consumen. El Motor no lo ve.
_Evitar_: Equipamiento, Bonus
