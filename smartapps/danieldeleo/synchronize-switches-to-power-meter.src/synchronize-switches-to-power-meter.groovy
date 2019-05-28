/**
 *  Synchronize Switches to Power Meter
 *
 *  Copyright 2016 Daniel De Leo
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
    name: "Synchronize Switches to Power Meter",
    namespace: "danieldeleo",
    author: "Daniel De Leo",
    description: "Synchronize switches to a power meter",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Monitor this power meter"){
		input(name: "meter", type: "capability.switch", title: "Which power meter?", required: true, multiple: false)
	}
	section("and synchronize with these switches") {
		input "switches", "capability.switch", required: false, multiple: true, title: "Which switches?"
	}
}

def installed() {
	initialize()
}

def updated() {
	unsubscribe()
	initialize()
}

def initialize() {
	subscribe(meter, "power", meterHandler)
    state.energy = 0
}

def meterHandler(evt) {
    def meterValue = evt.value as double
	if(meterValue <= state.energy) {
        if (switches) {  
            switches.off()
        }
    }
    else if(meterValue > state.energy) {
		if (switches) {  
            switches.on()
        }
    }
    state.energy = meterValue
}