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
 *  Aeon Double Switch Momentary Routine
 *
 *  Author: Daniel De Leo
 *
 *  Date: 2013-05-01
 */
definition(
	name: "Aeon Double Switch Momentary Routine",
	namespace: "danieldeleo",
	author: "Daniel De Leo",
	description: "Aeon Double Switch Momentary Routine",
	category: "Convenience",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet@2x.png"
)

preferences {
	section("When this switch is turned on, off or dimmed") {
		input "master", "capability.switch", title: "Where?"
	}
	section("Turn on or off all of these switches as well") {
		input "secondarySwitch", "capability.switch", required: true
	}
}

def installed()
{   
	subscribe(master, "switch.on", onHandler)
}

def updated()
{
	unsubscribe()
	subscribe(master, "switch.on", onHandler)
}

def logHandler(evt) {
	log.debug evt.value
}

def onHandler(evt) {
	secondarySwitch?.on1()
    master?.off()
}