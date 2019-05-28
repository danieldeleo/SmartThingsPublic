/**
 *  Control Panel 
 *
 *  Copyright 2018 SmartThings
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
 */
definition(
    name: "Control Panel",
    namespace: "danieldeleo",
    author: "Danny De Leo",
    description: "Control Panel Web App",
    category: "",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    oauth: [displayName: "Control Panel", displayLink: "http://localhost:4567"])

preferences {
    page(name: "configure")
}

def configure() {
    dynamicPage(name: "configure", title: "Configure Switch and Phrase", install: true, uninstall: true) {
        def actions = location.helloHome?.getPhrases()*.label
        if (actions) {
            actions.sort()
            section("Routines to Trigger") {
                input "arriveDayAction", "enum", title: "Action to execute for arrival before sunset", options: actions, required: false
                input "arriveNightAction", "enum", title: "Action to execute arrival after sunset", options: actions, required: false
                input "arriveAlwaysAction", "enum", title: "Action to execute for arrival at any time", options: actions, required: false
                input "departAction", "enum", title: "Action to execute for departure", options: actions, required: false
            }
        }
        section ("All Lights") {
            input "switches", "capability.switch", multiple: true, required: true
        }
        section ("Kitchen") {
            input "kitchenSwitches", "capability.switch", multiple: true, required: true
        }
        section ("Living Room") {
            input "livingRoomSwitches", "capability.switch", multiple: true, required: true
        }
        section ("Bedroom") {
            input "bedroomSwitches", "capability.switch", multiple: true, required: true
        }
        section ("Chromecast") {
            input "chromecast", "capability.switch", multiple: true, required: true
        }
        section ("FireTV") {
            input "fireTv", "capability.switch", multiple: true, required: true
        }
        section ("AC") {
            input "ac", "capability.switch", multiple: true, required: true
        }
        section ("Allow access to door") {
            input "door", "capability.switch", required: true
        }
        section("Change to this mode when...") {
            input "homeMode", "mode", title: "Arriving"
            input "awayMode", "mode", title: "Leaving"
        }
	}
}

mappings {
  path("/switches") {
    action: [
      GET: "listSwitches"
    ]
  }
  path("/switches/:command") {
    action: [
      PUT: "updateSwitches"
    ]
  }
}

// returns a list like
// [[name: "kitchen lamp", value: "off"], [name: "bathroom", value: "on"]]
def listSwitches() {

    def resp = []
    switches.each {
        resp << [name: it.displayName, value: it.currentValue("switch")]
    }
    return resp
}

def toggleSwitches(_switches){
	def isOn = false
    _switches.each {
    	def displayName = it.displayName
        def switchValue = it.currentValue("switch")
	    log.debug "switch value: $displayName $switchValue"
		if(switchValue == "on"){
        	isOn = true
        }
    }
    if(isOn){
    	_switches.off()
    } else {
    	_switches.on()
    }
}

void updateSwitches() {
    // use the built-in request object to get the command parameter
    def command = params.command

    // all switches have the comand
    // execute the command on all switches
    // (note we can do this on the array - the command will be invoked on every element
    switch(command) {
        case "arrived":
            arrived()
            break
        case "departed":
            departed()
            break
        case "on":
            switches.on()
            break
        case "off":
            switches.off()
            break
		case "kitchen":
        	toggleSwitches(kitchenSwitches)
        	break
        case "livingroom":
        	toggleSwitches(livingRoomSwitches)
        	break
        case "bedroom":
        	toggleSwitches(bedroomSwitches)
        	break
        case "chromecast":
        	toggleSwitches(chromecast)
        	break
        case "firetv":
        	toggleSwitches(fireTv)
        	break
		case "ac":
        	toggleSwitches(ac)
        	break            
        case "door":
        	door.on()
            arrived()
            break
        default:
            httpError(400, "$command is not a valid command for all switches specified")
    }

}

def arrived() {
	if(location.mode != homeMode) {
    	location.setMode(homeMode)
    }
    
	def now = new Date()
	def sunTime = getSunriseAndSunset(sunsetOffset: -30);
    
    
    if(now > sunTime.sunset || now < sunTime.sunrise) {// Arrive at Night
        if(arriveNightAction != null) {
            sendNotificationEvent("Executing: " + arriveNightAction)
            location.helloHome?.execute(arriveNightAction)
        }
    }
    else if(now > sunTime.sunrise && now < sunTime.sunset) {// Arrive at Day
        if(arriveDayAction != null) {
            sendNotificationEvent("Executing: " + arriveDayAction)
            location.helloHome?.execute(arriveDayAction)
        }
    }
    if(arriveAlwaysAction != null) {// Arrive anytime
        sendNotificationEvent("Executing: " + arriveAlwaysAction)
        location.helloHome?.execute(arriveAlwaysAction)
    }
}

def departed() {
    location.setMode(awayMode)
    if(departAction) {
        sendNotificationEvent("Executing: " + departAction)
        location.helloHome?.execute(departAction)
    }
}

def installed() {}

def updated() {}