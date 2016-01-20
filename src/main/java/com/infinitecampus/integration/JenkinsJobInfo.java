package com.infinitecampus.integration;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class JenkinsJobInfo {
	private List<String> jenkinsJobs;
	private String jenkinsJobUrlTemplate = "http://ci.infinitecampus.com/job/%s/lastCompletedBuild/api/json?tree=result";
	
	public JenkinsJobInfo(List<String> jenkinsJobs) {
		super();
		this.jenkinsJobs = jenkinsJobs;
	}

	public JenkinsJobInfo(List<String> jenkinsJobs, String jenkinsJobUrlTemplate) {
		super();
		this.jenkinsJobs = jenkinsJobs;
		this.jenkinsJobUrlTemplate = jenkinsJobUrlTemplate;
	}

	int getFailedJobsCount(){
		int failedJobs = 0;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		ResponseHandler<String> httpResponseHandler = new HttpResponseHandler();
		HttpGet httpGet = new HttpGet();

		for (String jenkinsJob : jenkinsJobs) {
			String formattedJenkinsUrl = String.format(jenkinsJobUrlTemplate, jenkinsJob);
			String responseBody;
			try {
				httpGet.setURI(new URI(formattedJenkinsUrl));
				responseBody = httpClient.execute(httpGet, httpResponseHandler);
				System.out.println("----------------------------------------");
				System.out.println(formattedJenkinsUrl);
				System.out.println(responseBody);
				JSONObject jsonObject = (JSONObject) JSONValue.parse(responseBody);
				String jobResult = (String) jsonObject.get("result");
				System.out.println(jobResult);
				if(jobResult.equalsIgnoreCase("FAILURE")){
					failedJobs++;
				}
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
				failedJobs = -1;
				break;
			}

		}
		try {
			httpClient.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return failedJobs;
	}
}
