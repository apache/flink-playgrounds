package org.apache.flink.playgrounds.filesystem.scenario2;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import lombok.Data;
import org.apache.flink.api.common.serialization.Encoder;
import org.apache.flink.api.common.state.*;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.typeutils.base.IntSerializer;
import org.apache.flink.api.common.typeutils.base.StringSerializer;
import org.apache.flink.connector.file.sink.FileSink;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.connector.file.src.reader.TextLineFormat;
import org.apache.flink.core.fs.Path;
import org.apache.flink.core.io.SimpleVersionedSerializer;
import org.apache.flink.playgrounds.filesystem.scenario1.KeyBucketAssigner;
import org.apache.flink.playgrounds.filesystem.util.GiteeHelper;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.BucketAssigner;
import org.apache.flink.streaming.api.functions.sink.filesystem.bucketassigners.BasePathBucketAssigner;
import org.apache.flink.streaming.api.functions.sink.filesystem.bucketassigners.SimpleVersionedStringSerializer;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.OnCheckpointRollingPolicy;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class FileScenario2 {

    private  static final String outputPath = "./output_directory_tmp2";
    public static void run() throws Exception {

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(4);
        env.enableCheckpointing(5000L);
        env.setRestartStrategy(
                RestartStrategies.fixedDelayRestart(
                        Integer.MAX_VALUE, Time.of(10L, TimeUnit.SECONDS)));

        // generate data, shuffle, sink
        DataStream<LineData> source = env.addSource(new Generator(10, 600));

        FileSink<LineData> sink =
                FileSink.forRowFormat(
                                new Path(outputPath),
                                (Encoder<LineData>)
                                        (element, stream) -> {
                                            PrintStream out = new PrintStream(stream);
                                            out.println(element.getText());
                                        })
                        .withBucketAssigner(new KeyBucketAssigner2())
                        .withRollingPolicy(OnCheckpointRollingPolicy.build())
                        .build();
        source.sinkTo(sink);

        env.execute("StreamingFileSinkProgram_scenario2");

    }

    /**
     * data generator
     */
    private static final class Generator
            implements SourceFunction<LineData>, CheckpointedFunction {

        private static final long serialVersionUID = -2819385275681175792L;

        private final int batchRows = 10;
        private final int idlenessMs;
        private final int recordsToEmit;

        private volatile int numRecordsEmitted = 1;
        private volatile boolean canceled = false;

        private ListState<Integer> state = null;

        Generator(final int idlenessMs, final int recordsToEmit) {
            this.idlenessMs = idlenessMs;

            this.recordsToEmit = recordsToEmit;
        }

        @Override
        public void run(final SourceContext<LineData> ctx) throws Exception {

            String gitee_access_token = System.getenv("gitee_access_token");
            String plainText = GiteeHelper.getCSVContent(gitee_access_token);
            try (CSVReader reader = new CSVReader(new StringReader(plainText))) {
                String[] lineInArray;

                // the first two lines of input file
                // are not data related
                reader.skip(2);

                // skip the header line of the csv file
                int startFrom = numRecordsEmitted;
                reader.skip(startFrom);
                while (numRecordsEmitted < recordsToEmit) {
                    synchronized (ctx.getCheckpointLock()) {
                        startFrom = numRecordsEmitted;
                        for (int rows = startFrom; rows< startFrom + batchRows; rows++ ){
                            lineInArray = reader.readNext();
                            FileScenario2.LineData line = new FileScenario2.LineData();
                            line.setContents(lineInArray);
                            ctx.collect(line);
                            numRecordsEmitted++;
                        }
                    }
                    Thread.sleep(idlenessMs);
                }
            }



            while (!canceled) {
                Thread.sleep(50);
            }
        }

        @Override
        public void cancel() {
            canceled = true;
        }

        @Override
        public void initializeState(FunctionInitializationContext context) throws Exception {

            state =
                    context.getOperatorStateStore()
                            .getListState(
                                    new ListStateDescriptor<Integer>(
                                            "state", IntSerializer.INSTANCE));

            for (Integer i : state.get()) {
                numRecordsEmitted += i;
            }

        }

        @Override
        public void snapshotState(FunctionSnapshotContext context) throws Exception {
            state.clear();
            state.add(numRecordsEmitted);
        }
    }

    private   final static class KeyBucketAssigner2
            extends BasePathBucketAssigner<LineData> {

        private static final long serialVersionUID = 987325769970523326L;

        @Override
        public String getBucketId(final LineData element, final Context context) {
            String[] fields = element.getContents();
            return fields[0];
        }


    }


    @Data
    public static final class LineData {

        private String[] contents;

        public String getText() {
            String line = "";
            int len = contents.length;
            for (int i = 0; i < len; i++) {
                if(i < len -1) {
                    line += "\"" + contents[i] + "\",";
                }else {
                    line += "\"" + contents[i] + "\"";
                }
            }
            return  line;
        }

    }
}

