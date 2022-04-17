package org.apache.flink.playgrounds.filesystem;

import org.apache.flink.playgrounds.filesystem.util.GiteeHelper;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Test;

import java.net.URL;

public class GiteeTest {


    @Test
    public void getRawFileFromGitee_Test() {
        // get PATH environment variable
        String gitee_access_token = System.getenv("gitee_access_token");

        try{
            String plainText = GiteeHelper.getCSVContent(gitee_access_token);

            System.out.println(plainText);
        }catch (Exception ex) {

        }
    }

    /*
        http://opencsv.sourceforge.net/
    * */
    @Test
    public void getCSVLine_Test() {
        // get PATH environment variable
        String gitee_access_token = System.getenv("gitee_access_token");

        try{
            String plainText = GiteeHelper.getCSVContent(gitee_access_token);

            System.out.println(plainText);
        }catch (Exception ex) {

        }
    }
}
