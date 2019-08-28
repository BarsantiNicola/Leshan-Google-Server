
CLASSI DA COMPILARE PER COMPLETARE IL BRIDGING.
Ogni classe viene associata a un azione, le associazioni vengono già realizzate dal software implementato.
Rimane solo da definire il loro comportamento e le risorse necessarie nel file conversion/traits.json

Le classi devono implementare i seguenti metodi:
 - putObservation: eseguita nel caso di ricezione di una notify dal software
 - queryInformation: deve fornire gli status dell'azione, viene richiamata durante le richieste QUERY
 - handleRequest:  eseguita alla ricezione di un comando per il dispositivo
 - startObservation:  eseguita alla creazione di un dispositivo, definisce le observe per l'azione
 - getAttribute:  richiamata durante la gestione della SYNC fornisce gli attributi per il trait

Esempi di utilizzo sono presenti per le classi OnOff, Brightless e ColorSetting