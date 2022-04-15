package org.apache.flink.playgrounds.filesystem.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.StringWriter;
import java.nio.charset.Charset;

public class MyHttpClient {
    /**
     * CloseableHttpClient
     *
     * @return
     */
    private static CloseableHttpClient createCloseableHttpClient() {
        // UsernamePasswordCredentials credentials = new
        // UsernamePasswordCredentials("admin", "sysadmin");
        // BasicCredentialsProvider credentialsProvider = new
        // BasicCredentialsProvider();
        // credentialsProvider.setCredentials(AuthScope.ANY, credentials);
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        return httpClient;
    }

    private static void createHttpDeleteClient() throws Exception {
        CloseableHttpClient httpClient = createCloseableHttpClient();
        try {
            // Define a deleteRequest request
            HttpDelete deleteRequest = new HttpDelete("http://localhost:8080/v1/api/bookstore/book/1");

            // deleteRequest.addHeader("Accept", "application/xml");
            deleteRequest.addHeader("Accept", "application/json");

            // Send the request; It will immediately return the response in HttpResponse
            // object if any
            HttpResponse response = httpClient.execute(deleteRequest);

            // verify the valid error code first
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new RuntimeException("Failed with HTTP error code : " + statusCode);
            }
            // Now pull back the response object
            HttpEntity httpEntity = response.getEntity();
            String output = new String(EntityUtils.toByteArray(httpEntity), "utf-8");// 处理中文乱码
            // Lets see what we got from API
            System.out.println(output);
        } finally {
            // Important: Close the connect
            httpClient.close();
        }
    }

    private static void createHttpPutClient() throws Exception {
        CloseableHttpClient httpClient = createCloseableHttpClient();

        StringWriter writer = new StringWriter();

        try {
            // Define a putRequest request
            HttpPut putRequest = new HttpPut("http://localhost:8080/v1/api/bookstore/book");

            // Set the API media type in http content-type header
            putRequest.addHeader("content-type", "application/xml");
            // putRequest.addHeader("Accept", "application/xml");
            putRequest.addHeader("Accept", "application/json");

            // Set the request post body
            StringEntity userEntity = new StringEntity(writer.getBuffer().toString(), Charset.forName("utf-8"));// 设置发送的编码
            putRequest.setEntity(userEntity);

            // Send the request; It will immediately return the response in HttpResponse
            // object if any
            HttpResponse response = httpClient.execute(putRequest);

            // verify the valid error code first
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new RuntimeException("Failed with HTTP error code : " + statusCode);
            }
            // Now pull back the response object
            HttpEntity httpEntity = response.getEntity();
            String output = new String(EntityUtils.toByteArray(httpEntity), "utf-8");// 处理中文乱码
            // Lets see what we got from API
            System.out.println(output);
        } finally {
            // Important: Close the connect
            httpClient.close();
        }
    }

    private static void createHttpPostClient() throws Exception {
        CloseableHttpClient httpClient = createCloseableHttpClient();

        StringWriter writer = new StringWriter();

        try {
            // Define a postRequest request
            HttpPost postRequest = new HttpPost("http://localhost:8080/v1/api/bookstore/book");

            // Set the API media type in http content-type header
            postRequest.addHeader("content-type", "application/xml");
            // postRequest.addHeader("Accept", "application/xml");
            postRequest.addHeader("Accept", "application/json");

            // Set the request post body
            StringEntity userEntity = new StringEntity(writer.getBuffer().toString(), Charset.forName("utf-8"));// 设置发送的编码
            postRequest.setEntity(userEntity);

            // Send the request; It will immediately return the response in HttpResponse
            // object if any
            HttpResponse response = httpClient.execute(postRequest);

            // verify the valid error code first
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new RuntimeException("Failed with HTTP error code : " + statusCode);
            }
            // Now pull back the response object
            HttpEntity httpEntity = response.getEntity();
            String output = new String(EntityUtils.toByteArray(httpEntity), "utf-8");// 处理中文乱码
            // Lets see what we got from API
            System.out.println(output);
        } finally {
            // Important: Close the connect
            httpClient.close();
        }
    }

    public static int createHttpGetClient(String url, String header) throws Exception {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        try {
            // Define a HttpGet request;
            // You can choose between HttpPost, HttpDelete or HttpPut also.
            // Choice depends on type of method you will be invoking.
            HttpGet getRequest = new HttpGet(url);

            // Set the API media type in http accept header
            getRequest.addHeader("Accept", header);
            // getRequest.addHeader("Accept", "application/json");

            // Send the request; It will immediately return the response in
            // HttpResponse object
            HttpResponse response = httpClient.execute(getRequest);

            // verify the valid error code first
            int statusCode = response.getStatusLine().getStatusCode();
            return statusCode;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            // Important: Close the connect
            httpClient.close();
        }

    }


    public static String createHttpPostClientForChart(String input, String token) throws Exception {

        CloseableHttpClient httpClient = createCloseableHttpClient();

        try {
            // Define a postRequest request
            HttpPost postRequest = new HttpPost("https://bcservices.chinacloudsites.cn/api/generateImage");

            postRequest.addHeader("Authorization", "Bearer " + token);

            // Set the API media type in http content-type header
            postRequest.addHeader("content-type", "application/json");
            // postRequest.addHeader("Accept", "application/xml");
            postRequest.addHeader("Accept", "application/json");

            StringEntity userEntity = new StringEntity(input, Charset.forName("utf-8"));// 设置发送的编码
            postRequest.setEntity(userEntity);

            // Send the request; It will immediately return the response in HttpResponse
            // object if any
            HttpResponse response = httpClient.execute(postRequest);

            HttpEntity httpEntity = response.getEntity();
            // String xxx = httpEntity.toString();
            String output = new String(EntityUtils.toByteArray(httpEntity), "utf-8");// 处理中文乱码
            // Lets see what we got from API
            return output;
        } finally {
            // Important: Close the connect
            httpClient.close();
        }
    }
}
