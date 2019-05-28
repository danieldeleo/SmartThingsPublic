/**
 *  Copyright 2019 SmartThings
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
 *  Switch Triggers Google Assistant
 *
 *  Author: Danny De Leo
 *
 *  Date: 2019-05-25
 */
definition(
	name: "Switch Triggers Google Assistant",
	namespace: "danieldeleo",
	author: "Danny De Leo",
	description: "Send commands to Google when switch turns on/off",
	category: "Convenience",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet@2x.png"
)

preferences {
	section("When switch is turned on, send commands to Google") {
		input "onGoogle", "capability.switch", title: "Which swtich?", required: false
        input name: "onCommands", type: "text", title: "Which commands? (comma separated)", required: false
	}

    section("When switch is turned off, send commands to Google") {
		input "offGoogle", "capability.switch", title: "Which switch?", required: false
        input name: "offCommands", type: "text", title: "Which commands? (comma separated)", required: false
	}
    section("Google Assistant Endpoint") {
        input "assistantEndpoint", "text", title: "Cloud Function Endpoint"
    }
}

def installed()
{   
	subscribe(onGoogle, "switch.on", onHandler)
	subscribe(offGoogle, "switch.off", offHandler)
}

def updated()
{
	unsubscribe()
	subscribe(onGoogle, "switch.on", onHandler)
	subscribe(offGoogle, "switch.off", offHandler) 
}

def logHandler(evt) {
	log.debug evt.value
}

def onHandler(evt) {
	log.debug evt.value
	String[] commands = onCommands.split(',')
    send_to_google_assistant(commands)
}

def offHandler(evt) {
	log.debug evt.value
	String[] commands = offCommands.split(',')
    send_to_google_assistant(commands)
}

def send_to_google_assistant(String[] message_list){
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
