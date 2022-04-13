package org.apache.flink.playgrounds.filesystem;

import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeutils.base.IntSerializer;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.connector.file.src.reader.TextLineFormat;
import org.apache.flink.core.fs.Path;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class FileScenario2 {

    public static void run(){

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(4);
        env.enableCheckpointing(5000L);
        env.setRestartStrategy(
                RestartStrategies.fixedDelayRestart(
                        Integer.MAX_VALUE, Time.of(10L, TimeUnit.SECONDS)));

        // generate data, shuffle, sink
        DataStream<String> source = env.addSource(new Generator(10, 10, 60));



    }

    /**
     * data generator
     */
    private static final class Generator
            implements SourceFunction<String>, CheckpointedFunction {

        private static final long serialVersionUID = -2819385275681175792L;

        private final int numKeys;
        private final int idlenessMs;
        private final int recordsToEmit;

        private volatile int numRecordsEmitted = 0;
        private volatile boolean canceled = false;

        private ListState<Integer> state = null;

        Generator(final int numKeys, final int idlenessMs, final int durationSeconds) {
            this.numKeys = numKeys;
            this.idlenessMs = idlenessMs;

            this.recordsToEmit = ((durationSeconds * 1000) / idlenessMs) * numKeys;
        }

        @Override
        public void run(final SourceContext<String> ctx) throws Exception {
            while (numRecordsEmitted < recordsToEmit) {
                synchronized (ctx.getCheckpointLock()) {
                    for (int i = 0; i < numKeys; i++) {
                        ctx.collect(String.valueOf(i));
                        numRecordsEmitted++;
                    }
                }
                Thread.sleep(idlenessMs);
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


    public static final class Line {
        private String text;
    }


    public static final class Key {
        private String enrollNumber;

        private String department;

        private String account;

        private String subscriptionId;
    }

}
