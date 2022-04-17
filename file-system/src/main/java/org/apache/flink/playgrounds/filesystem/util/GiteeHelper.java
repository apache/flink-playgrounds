package org.apache.flink.playgrounds.filesystem.util;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.net.URL;
import java.util.Base64;

public class GiteeHelper {

    public static String sendHttpGetClient(String url) throws Exception {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        try {
            // Define a HttpGet request;
            // You can choose between HttpPost, HttpDelete or HttpPut also.
            // Choice depends on type of method you will be invoking.
            HttpGet getRequest = new HttpGet(url);

            // Set the API media type in http accept header
            getRequest.addHeader("Accept", "application/json");

            // Send the request; It will immediately return the response in
            // HttpResponse object
            HttpResponse response = httpClient.execute(getRequest);

            HttpEntity httpEntity = response.getEntity();

            // String xxx = httpEntity.toString();
            String output = new String(EntityUtils.toByteArray(httpEntity), "utf-8");// 处理中文乱码
            JSONObject object = new JSONObject(output);
            return object.getString("content");

        } catch (Exception e) {
           throw e;
        } finally {
            // Important: Close the connect
            httpClient.close();
        }
    }

    public static String getCSVContent(String gitee_access_token)  throws Exception{

        String url = "https://gitee.com/api/v5/repos/jhinw/data/contents/azure/UsageDetail-TestData-outdated.csv";
        URIBuilder b = new URIBuilder(url);
        b.addParameter("ref", "feature/data");
        b.addParameter("access_token", gitee_access_token);

        URL finalUrl = b.build().toURL();

        String content = GiteeHelper.sendHttpGetClient(finalUrl.toString());

        Base64.Decoder dec = Base64.getDecoder();

        return new String(dec.decode(content));
    }


}
