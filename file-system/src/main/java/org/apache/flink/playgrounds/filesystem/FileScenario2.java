package org.apache.flink.playgrounds.filesystem;

import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.connector.file.src.reader.TextLineFormat;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.File;

public class FileScenario2 {

    public static void run(){

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRuntimeMode(RuntimeExecutionMode.BATCH);

        Path file = Path.fromLocalFile(new File("input/test.csv"));
        final FileSource<String> source =
                FileSource.forRecordStreamFormat(new TextLineFormat(), file)
                        .build();


        final DataStream<String> stream =
                env.fromSource(source, WatermarkStrategy.noWatermarks(), "file-source");

    }
}
