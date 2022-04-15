package org.apache.flink.playgrounds.filesystem.scenario1;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.core.io.SimpleVersionedSerializer;
import org.apache.flink.streaming.api.functions.sink.filesystem.BucketAssigner;
import org.apache.flink.streaming.api.functions.sink.filesystem.bucketassigners.SimpleVersionedStringSerializer;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.OnCheckpointRollingPolicy;

public  final class KeyBucketAssigner
        implements BucketAssigner<String, String> {

    private static final long serialVersionUID = 987325769970523326L;

    @Override
    public String getBucketId(final String element, final Context context) {
        return String.valueOf(element.split("-")[1]);
    }

    @Override
    public SimpleVersionedSerializer<String> getSerializer() {
        return SimpleVersionedStringSerializer.INSTANCE;
    }
}