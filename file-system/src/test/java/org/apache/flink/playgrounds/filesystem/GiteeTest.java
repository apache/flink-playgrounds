package org.apache.flink.playgrounds.filesystem;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import org.apache.flink.playgrounds.filesystem.scenario2.FileScenario2;
import org.apache.flink.playgrounds.filesystem.util.GiteeHelper;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Test;

import java.io.StringReader;
import java.net.URL;
import java.util.List;

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
    public void getCSVLines_Test() {
        // get PATH environment variable
        String gitee_access_token = System.getenv("gitee_access_token");

        try{
            String plainText = GiteeHelper.getCSVContent(gitee_access_token);
            try (CSVReader reader = new CSVReader(new StringReader(plainText))) {
                String[] lineInArray;
                while ((lineInArray = reader.readNext()) != null) {
                    FileScenario2.LineData line = new FileScenario2.LineData();
                    line.setContents(lineInArray);
                    System.out.println(line.getText());
                }
            }
        }catch (Exception ex) {

        }
    }
}
