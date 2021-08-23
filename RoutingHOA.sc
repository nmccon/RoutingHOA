RoutingHOA {
	var server, <num, numChannels, <>group, <>masterBus, <>fxBus, <ambiBus, <sendBus, <sendSynth, <ambiGroup, <send, sendSynthDef;

	*new {|server, num, numChannels, group, masterBus, fxBus|
		^super.newCopyArgs(server, num, numChannels, group, masterBus, fxBus).init;
	}

	init {
		server.newBusAllocators;
		ambiBus = Array.newClear(num);
		sendBus = Array.newClear(num);
		sendSynth = Array.newClear(num);
		ambiGroup = Array.newClear(num);
		send = Array.newClear(num);

		sendSynthDef = SynthDef(\ambiThrow, {
			var sig = In.ar(\from.kr(0), numChannels);
			var fxsig = In.ar(\fromfx.kr(0), numChannels);
			Out.ar(\to.kr(0), sig);
			Out.ar(\tofx.kr(0), fxsig)
		}).add;

		^this.routing;
	}

	routing {
		num.do{|i|
			ambiBus[i] = Bus.audio(server,numChannels);
			sendBus[i] = Bus.audio(server,numChannels);
			ambiGroup[i] = Group.before(group);
			sendSynth[i] = Synth(\ambiThrow, [\from, ambiBus[i], \to, masterBus, \fromfx, sendBus[i], \tofx,  fxBus ], ambiGroup[i], \addToTail);
		}
	}

}