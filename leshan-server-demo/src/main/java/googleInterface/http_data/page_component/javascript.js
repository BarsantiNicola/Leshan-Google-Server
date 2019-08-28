function getUrlParameters (){

				query = window.location.search.substr(1);

				keyValues = query.split('&');
				parameterMap = new Map();
				for ( i in keyValues) {

					param = keyValues[i];
					splitParam = param.split('=');
					parameterMap.set(splitParam[0], splitParam[1]);
				}
				return parameterMap;
			}
			
			function formSubmit(){
			
				params = getUrlParameters();
				if( params.get("error") ) document.getElementById("error").innerHTML='Autenticazione fallita';
			    document.getElementById('Redirect_uri').value = params.get('redirect_uri');;
				document.getElementById('Client_id').value = params.get('client_id');
				document.getElementById('Redirect').value = params.get('redirect');
				document.getElementById('State').value = params.get('state');

			}
			