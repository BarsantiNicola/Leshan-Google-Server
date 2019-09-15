
Sottopackage googleinterface.integration.actions

Il package mantiene tutte le classi necessarie alla gestione delle Actions Google. Le classi sono definite tramite l'estensione della classe
astratta Trait. Tutte le classi necessarie a Google sono già state definite, googleinterface.integration.GoogleLeshanConverter tramite Trait in automatico
se la risorsa è presente associa la giusta classe Trait al Device.


Ogni classe estesa da Trait deve implementare i seguenti metodi:
 - startObservation:  eseguita alla istanziazione del Trait, definisce le observation delle risorse utilizzate dall'azione(per aggiornare in tempo reale gli status di Query)
 - putObservation: eseguita nel caso di ricezione di una notify dal software(con le informazioni ricevute bisogna aggiornare gli status per Query)
 - queryInformation: deve fornire gli status dell'azione, viene richiamata durante le richieste QUERY
 - handleRequest:  eseguita alla ricezione di un comando per il dispositivo
 - getAttribute:  richiamata durante la gestione della SYNC fornisce gli attributi per il trait
 - setVariables:  associa le risorse(che sono variabili ed eterogenee) alle variabili locali utilizzate nel codice della classe Trait(il codice statico così riesce a gestire la dinamicità delle risorse)

//  TODO

Per completare l'integrazione rimane solo da definire il comportamento delle classi Trait e le risorse utilizzate dal file convertion/traits.json.
Le azioni OnOff, Brightless e ColorSetting sono state implementate e possono essere usate come esempio di compilazione di classi Trait.