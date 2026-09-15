# Los ítems no tocan al Motor

Los bonus viven en Mercado con estados activo e inactivo por alumno. Cada consumidor (Roadmap, Banco) consulta si hay activo al aplicar y lo consume en la misma operación. El Motor no valida, no reserva, no guarda referencias ni replica nada.

Antes el intento guardaba referencias validadas al abrir. Se descartó porque metía a Mercado en el camino caliente y dejaba reservas colgadas cuando el intento vencía sin cerrar.
