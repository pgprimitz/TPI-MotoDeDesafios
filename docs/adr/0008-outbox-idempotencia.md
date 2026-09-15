# Outbox para publicar, idempotencia por intentoId

El cierre del intento y el guardado del evento van en la misma transacción en EVENTO_OUTBOX; un relay publica a Kafka y marca enviado. Si el bus cae, nada se pierde: sale al recuperarse.

Publicamos al menos una vez, así que puede haber duplicados: los consumidores aplican por intentoId y vale lo último por timestamp. Del otro lado, cerrar es idempotente por estados y abrir usa clave del llamante.

Descartamos publicar directo tras el commit porque un corte entre medio pierde el evento sin dejar rastro, y descartamos XA porque acopla la base al bus.
