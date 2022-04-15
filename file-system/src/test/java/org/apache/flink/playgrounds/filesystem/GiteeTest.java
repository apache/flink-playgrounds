package org.apache.flink.playgrounds.filesystem;

import org.apache.flink.playgrounds.filesystem.scenario1.FileScenario1;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Test;

import java.net.URL;

public class GiteeTest {


    @Test
    public void getRawFileFromGitee_Test() {
        String  url = "https://gitee.com/api/v5/repos/jhinw/data/contents/azure%2FUsageDetail-TestData-outdated.csv";
        try{
            URIBuilder b = new URIBuilder(url);
            b.addParameter("ref", "feature/data");
            b.addParameter("access_token", "{from env}");

            URL finalUrl = b.build().toURL();

        }catch (Exception ex) {

        }
    }
}
