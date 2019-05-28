/**
 *  Copyright 2015 SmartThings
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  Lights Off, When Closed
 *
 *  Author: Daniel De Leo
 */
definition(
    name: "Pleasing Poo",
    namespace: "danieldeleo",
    author: "Daniel De Leo",
    description: "Listen to nature sounds while you poo",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet@2x.png"
)

preferences {
	section ("Play nature sounds when the bathroom door closes...") {
		input "contact1", "capability.contactSensor", title: "Which door?"
	}
    section("Google Assistant Endpoint") {
        input "assistantEndpoint", "text", title: "Cloud Function Endpoint"
    }
}

def installed()
{
	subscribe(contact1, "contact.open", contactOpenHandler)
	subscribe(contact1, "contact.closed", contactClosedHandler)
}

def updated()
{
	unsubscribe()
    subscribe(contact1, "contact.open", contactOpenHandler)
	subscribe(contact1, "contact.closed", contactClosedHandler)
}

def contactClosedHandler(evt) {
	send_to_google_assistant([
    	"set bathroom volume to 50%",
        "shuffle the album relax nature collection on Google play music on bathroom"
    ])
}

def contactOpenHandler(evt) {
	send_to_google_assistant([
    	"stop bathroom",
        "set bathroom volume to 70%"
    ])
}

def send_to_google_assistant(message_list){
	if(assistantEndpoint == null) return 
    def params = [
        uri: assistantEndpoint,
        body: [
            messages: message_list
        ]
    ]

    try {
        httpPostJson(params) { resp ->
            resp.headers.each {
                log.debug "${it.name} : ${it.value}"
            }
            log.debug "response contentType: ${resp.    contentType}"
        }
    } catch (e) {
        log.debug "something went wrong: $e"
    }
}