/**
 *  Switch Changes Mode
 *
 *  Copyright 2015 Daniel De Leo
 *  Version 1.01 3/8/15
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
 *  Ties a mode to a switch's (virtual or real) on/off state. Perfect for use with IFTTT.
 *  Simple define a switch to be used, then tie the on/off state of the switch to a specific mode.
 *  Connect the switch to an IFTTT action, and the mode will fire with the switch state change.
 *
 *
 */
definition(
    name: "Switch Changes Mode",
    namespace: "danieldeleo",
    author: "Daniel De Leo",
    description: "Ties a mode to a switch's state. Perfect for use with IFTTT.",
    category: "Convenience",
    iconUrl: "https://raw.githubusercontent.com/MichaelStruck/SmartThings/master/IFTTT-SmartApps/App1.png",
    iconX2Url: "https://raw.githubusercontent.com/MichaelStruck/SmartThings/master/IFTTT-SmartApps/App1@2x.png",
    iconX3Url: "https://raw.githubusercontent.com/MichaelStruck/SmartThings/master/IFTTT-SmartApps/App1@2x.png")

preferences {
	page(name: "getPref", title: "Choose Switch and Modes", install:true, uninstall: true) {
    	section("Choose a switch to use...") {
			input "controlSwitch", "capability.switch", title: "Switch", multiple: false, required: true
   		}
		section("Change to this mode when switched on...") {
			input "onMode", "mode", title: "Switch is on", required: false
		}
        section("When the garage closes and I'm home, change to this mode...") {
			input "homeMode", "mode", title: "Home Mode?"
        }
        section("When the garage closes and I'm away, change to this mode...") {
            input "awayMode", "mode", title: "Away Mode?"
        }
        section("When changing modes, check for who's presence") {
            input "presence", "capability.presenceSensor", title: "Who?",multiple: true, required: true
        }
	}
}

def installed() {
	subscribe(controlSwitch, "switch", "switchHandler")
}

def updated() {
	unsubscribe()
	subscribe(controlSwitch, "switch", "switchHandler")
}

def switchHandler(evt) {
	if (evt.value == "on") {
		setLocationMode(onMode)
    } else {
		def nobodyHome = presence.find{it.currentPresence == "present"} == null
        if(nobodyHome) {
            location.mode = awayMode;
        }
        else {
            location.mode = homeMode;
        }
    }
}