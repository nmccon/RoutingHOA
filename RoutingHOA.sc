RoutingHOA {
	var server, <num, numChannels, <>group, <>masterBus, <>fxBus, <ambiBus, <sendBus, <sendSynth, <ambiGroup, <send, sendSynthDef, <>controlArray;

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
		controlArray = Array.newClear(num);

		sendSynthDef = SynthDef(\ambiThrow, {
			var sig = In.ar(\from.kr(0), numChannels);
			var fxsig = In.ar(\fromfx.kr(0), numChannels);
			Out.ar(\to.kr(0), sig);
			Out.ar(\tofx.kr(0), fxsig)
		}).add;

		^this.routing;
	}

	routing {
		server.bind{
			num.do{|i|
				ambiBus[i] = Bus.audio(server,numChannels);
				sendBus[i] = Bus.audio(server,numChannels);
				ambiGroup[i] = Group.before(group);
				server.sync;
				sendSynth[i] = Synth(\ambiThrow, [\from, ambiBus[i], \to, masterBus, \fromfx, sendBus[i], \tofx,  fxBus ], ambiGroup[i], \addToTail);
			};
		};
	}

	freeControlArray {
		controlArray.do{|i|
			case
			{i.class == VSTPluginController} {i.synth.set(\gate, 0)}
			{i.class == Synth} {i.set(\gate, 0)}
		};
	}
}


