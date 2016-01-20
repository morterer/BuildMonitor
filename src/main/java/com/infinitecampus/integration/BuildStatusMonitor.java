package com.infinitecampus.integration;

import java.util.Arrays;
import java.util.List;



import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;

public class BuildStatusMonitor {
	static List<String> jenkinsJobs = Arrays.asList(
			"TrunkCI", "Dictionary-Trunk", 

			"Integ-Trunk-Flow", "Integ-Trunk-Midday",
			"CI-TrunkCurrentRelease", "Release-E-Flow", 
			
			"CI-TrunkPreviousRelease", "Rx-E-Deploy-RXE", "Rx-E-Deploy-RXEBIE", "Rx-E-Deploy-RXEQA",
			
			"FailBuild"
			);
	static String jenkinsJobUrl = "http://ci.infinitecampus.com/job/%s/lastCompletedBuild/api/json?tree=result";
//	static String jenkinsJobUrl = "http://localhost:8080/JenkinsSim/job/%s/lastCompletedBuild/api/json?tree=result";

	public static void main(String[] args) {
		
		
		//ttyACM0
		SerialPort serialPort = SerialPort.getCommPorts()[2];
		serialPort.setBaudRate(115200);
		serialPort.openPort();
		
		JenkinsJobInfo jenkinsJobInfo = new JenkinsJobInfo(jenkinsJobs, jenkinsJobUrl);
		
		SerialPortDataListener dataListener = new BuildStatusDataListener(serialPort, jenkinsJobInfo);
		serialPort.addDataListener(dataListener);

	}

}
