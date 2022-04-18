package org.apache.flink.playgrounds.filesystem;

import org.apache.flink.playgrounds.filesystem.scenario1.FileScenario1;
import org.apache.flink.playgrounds.filesystem.scenario2.FileScenario2;
import org.junit.Test;

public class Scenario2Test {

    @Test
    public void test() {
        try{
            FileScenario2.run();
        }catch (Exception ex) {

        }

    }
}
