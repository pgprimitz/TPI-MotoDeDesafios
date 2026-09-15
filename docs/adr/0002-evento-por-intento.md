# Un evento por intento, sin evento de agotados

El Motor publica un evento por cada intento cerrado, con corrección, tiempo, puntualidad, score IA replicado y contexto del curso. Ese único evento lo consumen Roadmap, Banco y Notificaciones, que deriva "nota disponible" de ahí. No publica "reintentos agotados": Roadmap lleva la cuenta con los resultados y deduce el agotamiento. Los hechos de catálogo como desafío publicado viajan aparte porque son otro hecho de otro agregado: un hecho, un evento.

Descartamos que el Motor cuente reintentos y avise. Dos servicios contando lo mismo chocan en cuanto Roadmap edita reintentos o resetea la cuenta por recuperación de vida.
