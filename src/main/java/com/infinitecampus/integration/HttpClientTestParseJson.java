package com.infinitecampus.integration;


import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * This example demonstrates the use of the {@link ResponseHandler} to simplify
 * the process of processing the HTTP response and releasing associated resources.
 */
public class HttpClientTestParseJson {

    public final static void main(String[] args) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpget = new HttpGet("http://ci.infinitecampus.com/api/json?pretty=true&tree=jobs[name,url]");

            System.out.println("Executing request " + httpget.getRequestLine());

            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                @Override
                public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }

            };
            String responseBody = httpclient.execute(httpget, responseHandler);
            System.out.println("----------------------------------------");
            System.out.println(responseBody);
            JSONObject jsonObject = (JSONObject) JSONValue.parse(responseBody);
            @SuppressWarnings("unchecked")
			List<JSONObject> jobArray = (JSONArray) jsonObject.get("jobs");
            System.out.println(jsonObject);
            int jobCount = 1;
            for (JSONObject jenkinsJob : jobArray) {
            	System.out.format("%03d %s: %s\n", jobCount, jenkinsJob.get("name"),jenkinsJob.get("url"));
				jobCount++;	
			}
            
        } finally {
            httpclient.close();
        }
    }

}

//const char urlStart[] = "";
//
//const char urlEnd[] = "";
//
// 