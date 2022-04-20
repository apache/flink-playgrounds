package org.apache.flink.playgrounds.filesystem;

import org.apache.flink.playgrounds.filesystem.scenario3.TableScenario;
import org.apache.flink.playgrounds.filesystem.util.GiteeHelper;
import org.junit.Test;

public class TableTest {



    @Test
    public void table_Test() {
        // get PATH environment variable

        try{
            TableScenario.run();
        }catch (Exception ex) {
            ex.printStackTrace();

        }
    }
}
