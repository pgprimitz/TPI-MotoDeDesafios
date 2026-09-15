# El Motor entrega el contenido al abrir

Al abrir un intento, el Motor consulta la elegibilidad en Roadmap y recupera desde T04 o T05 el contenido de la versión que va a congelar. Solo entonces crea el intento, inicia el reloj y devuelve al frontend el intento junto con el contenido. El Motor no persiste ni interpreta ese contenido.

Descartamos que el frontend consulte directamente al corrector porque obligaría a repetir allí la autorización del intento y podría exponer contenido bloqueado. También evita iniciar el reloj cuando el corrector no logra entregar el contenido.
