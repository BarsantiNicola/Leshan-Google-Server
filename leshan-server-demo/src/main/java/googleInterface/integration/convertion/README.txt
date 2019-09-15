Nella directory sono mantenuti i file necessari alla conversione dei client LwM2M nei
modelli Google Smart Home.

	-device.json: Definisce le relazioni tra TIPO di un dispositivo e le ACTIONS
compatibili di Google Smart Home.

	-traits.json: Definisce le relazioni tra una ACTION e le RISORSE LWM2M che un client
deve possedere per poterla implementare. Ogni action è associata ai comandi che può ricevere
nelle richieste EXECUTE e da un insieme di risorse obbligatorie. 
Ogni risorsa obbligatoria può possedere un campo
states che identifica il nome da fornirgli nelle richieste di QUERY. Per permettere
di gestire l'alta varietà di LwM2M ogni risorsa obbligatoria mantiene  
un insieme di alias, risorse alternative per implementare l'Action, sono quindi disponibili molte risorse
per ogni risorsa obbligatoria(associazione sempre UNO A UNO, UNA risorsa obbligatoria viene sostituita da UN alias).