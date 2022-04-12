package org.apache.flink.playgrounds.filesystem;

import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.serialization.Encoder;
import org.apache.flink.connector.file.sink.FileSink;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.OnCheckpointRollingPolicy;

import java.io.PrintStream;

public class FileScenario1 {
    private  static final String Line = "https://www.jianshu.com/p/4830c68ac921";

    private  static final String outputPath = "./output_directory_tmp";

    public static void run(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRuntimeMode(RuntimeExecutionMode.BATCH);

        DataStream<String> source= env.fromSequence(0, 10)
                .map( i -> Line + "-" + i.toString());

        FileSink<String> sink =
                FileSink.forRowFormat(
                                new Path(outputPath),
                                (Encoder<String>)
                                        (element, stream) -> {
                                            PrintStream out = new PrintStream(stream);

                                            out.println(element);
                                        })
                        .withBucketAssigner(new KeyBucketAssigner())
                        .withRollingPolicy(OnCheckpointRollingPolicy.build())
                        .build();
        source.sinkTo(sink);


        env.execute("StreamingFileSinkProgram");

    }
}
