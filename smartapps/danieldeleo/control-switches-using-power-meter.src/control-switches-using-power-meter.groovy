/**
 *  Control Switches using Power Meter
 *
 *  Author: Daniel De Leo
 *
 *  Uses power meter value to control switch
 *
 *  Date: 2017-07-06
 */

definition (
	name: "Control Switches Using Power Meter",
	namespace: "danieldeleo",
	author: "Daniel De Leo",
	description: "Turns on switches when power passes a specified wattage threshold and turns switches back off if power dips below threshold",
	category: "Convenience",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Allstate/power_allowance.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Allstate/power_allowance@2x.png"
)

preferences {
	section("Monitor this power meter"){
		input "meter", "capability.powerMeter", title: "When This Power Meter...", required: true, multiple: false
        input "powerThreshold", "number", title: "Rises above this wattage...", required: false, defaultValue: 50
	}
	section("Turn on these switches...") {
		input "switches", "capability.switch", multiple: true, title: "Which switches?"
	}
    section("Do not turn on between these times...") {
		input name: "fromTime", title: "From this time", type: "time", required: false
        input name: "toTime", title: "To this time", type: "time", required: false
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
}

def meterHandler(evt) {
    def meterValue = evt.value as double
    def threshold = powerThreshold as double
    def inTimeFrameToIgnore = false
    if(fromTime != null && toTime != null) {
    	inTimeFrameToIgnore = timeOfDayIsBetween(fromTime, toTime, new Date(), location.timeZone)	
    }
    log.debug "meterValue: $meterValue, powerThreshold: $threshold, inTimeFrameToIgnore: $inTimeFrameToIgnore"
	if(meterValue > threshold && !inTimeFrameToIgnore) {
		switches?.on()
	}
    else {
    	switches?.off()
    }
}