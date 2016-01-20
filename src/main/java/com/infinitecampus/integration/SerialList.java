package com.infinitecampus.integration;

import com.fazecast.jSerialComm.SerialPort;

public class SerialList {

	public static void main(String[] args) {
		SerialPort[] serialPorts = SerialPort.getCommPorts();
		for (SerialPort serialPort : serialPorts) {
			System.out.format("%s\t%s\n", serialPort.getDescriptivePortName(), serialPort.getSystemPortName());
		}
		

	}

}
