#!/bin/sh

java -server -Djava.net.preferIPv4Stack=true -cp ../lib/hazelcast-all-3.4.1.jar com.hazelcast.console.ConsoleApp

