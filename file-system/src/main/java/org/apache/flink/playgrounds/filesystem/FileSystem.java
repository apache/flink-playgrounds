/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.playgrounds.filesystem;

import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.serialization.Encoder;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.file.sink.FileSink;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;


import java.io.PrintStream;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.OnCheckpointRollingPolicy;

/*
    how to write and customize source
    https://www.jianshu.com/p/4830c68ac921
*/
public class FileSystem {

     private  static final String Line = "https://www.jianshu.com/p/4830c68ac921";

     private  static final String outputPath = "./fink_directory";

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRuntimeMode(RuntimeExecutionMode.BATCH);

        DataStream<String>  source= env.fromSequence(0, 10)
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
