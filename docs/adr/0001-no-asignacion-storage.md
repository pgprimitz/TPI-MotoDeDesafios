# El Motor no guarda la asignación

Roadmap/Cursos unen cada desafío con su curso (orden, fechas, obligatorio). Roadmap decide la elegibilidad completa; el Motor exige esa decisión antes de abrir un intento y replica su contexto en los eventos. No guarda copia ni vuelve a implementar las reglas de Roadmap.

Evaluamos guardar una copia para validar en local y la descartamos. Duplica la propiedad y se desfasa cuando Roadmap edita el roadmap. Suma sincronización por datos que el Motor nunca decide.
