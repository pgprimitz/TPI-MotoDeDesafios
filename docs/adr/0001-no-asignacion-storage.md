# El Motor no guarda la asignación

Roadmap/Cursos unen cada desafío con su curso (orden, fechas, obligatorio). El Motor consulta elegibilidad y desbloqueo antes de abrir un intento y replica ese contexto en sus eventos. No guarda copia.

Evaluamos guardar una copia para validar en local y la descartamos. Duplica la propiedad y se desfasa cuando Roadmap edita el roadmap. Suma sincronización por datos que el Motor nunca decide.
