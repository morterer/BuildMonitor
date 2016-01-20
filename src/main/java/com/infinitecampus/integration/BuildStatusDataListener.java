package com.infinitecampus.integration;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

public class BuildStatusDataListener implements SerialPortDataListener {
	private SerialPort serialPort;
	private JenkinsJobInfo jenkinsJobInfo;
	private int currentFailCount = 0;
	private int previousFailCount = -1;
	private int sendRedraw = 1;

	public BuildStatusDataListener(SerialPort serialPort, JenkinsJobInfo jenkinsJobInfo) {
		this.serialPort = serialPort;
		this.jenkinsJobInfo = jenkinsJobInfo;
	}

	@Override
	public int getListeningEvents() {
		return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
			try {
				byte[] newData = new byte[serialPort.bytesAvailable()];
				int numRead = serialPort.readBytes(newData, newData.length);
	
				String serialCommand = new String(newData);
	
				System.out.format("Read %d bytes. ", numRead);
				System.out.format("Command [%s]\n", serialCommand);
	
				switch (serialCommand) {
	
				case "RST":
					// Arduino was powered up or reset so send the redraw option otherwise
					// the ring won't be redrawn until the next currentFailCount != previousFailCount
					sendRedraw = 1;
					break;
				case "GET":
					currentFailCount = jenkinsJobInfo.getFailedJobsCount();

					// if the number of failed builds changed, send redraw true
					if (currentFailCount != previousFailCount) {
						sendRedraw = 1;
					}
					previousFailCount = currentFailCount;
	
					StringBuffer response = new StringBuffer();
					response.append(currentFailCount);
					response.append(':');
					response.append(sendRedraw);
					response.append(':');
	
					System.out.format("Sending [%s]\n", response);
					byte[] responseBytes = response.toString().getBytes();
					serialPort.writeBytes(responseBytes, responseBytes.length);
	
					// set sendRedraw to false/0 once it's been sent so the ring
					// isn't redrawn every time
					if (sendRedraw == 1) {
						sendRedraw = 0;
					}
					break;
	
				default:
					System.out.format("Unknown command [%s]\n", serialCommand);
				}
	
			} catch (NegativeArraySizeException e){
				System.err.println("Serial port not open!");
				if(serialPort.openPort()){
					System.out.println("Serial port was reopened");
				}
			}
		}
	}

}
