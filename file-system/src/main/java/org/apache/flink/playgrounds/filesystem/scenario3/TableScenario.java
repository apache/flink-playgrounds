package org.apache.flink.playgrounds.filesystem.scenario3;

import com.opencsv.CSVReader;
import lombok.Data;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.common.typeutils.base.IntSerializer;
import org.apache.flink.playgrounds.filesystem.scenario2.FileScenario2;
import org.apache.flink.playgrounds.filesystem.util.GiteeHelper;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.table.api.*;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.connector.ChangelogMode;
import org.apache.flink.types.Row;

import java.io.StringReader;
import java.util.concurrent.TimeUnit;

public class TableScenario {

    private  static final String outputPath = "./output_directory_scenario3";
    public static void run() throws Exception {
        // create environments of both APIs
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(4);
        env.enableCheckpointing(5000L);
        env.setRestartStrategy(
                RestartStrategies.fixedDelayRestart(
                        Integer.MAX_VALUE, Time.of(10L, TimeUnit.SECONDS)));


        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
        // generate data, shuffle, sink
        /*
        * "账户所有者 Live Id (AccountOwnerId)","账户名称 (Account Name)",
        * "服务管理员 Live Id (ServiceAdministratorId)", "订阅 Id (SubscriptionId)",
        * "订阅 Guid (SubscriptionGuid)","订阅名称 (Subscription Name)",
        * "日期 (Date)","月 (Month)","日 (Day)","年 (Year)",
        * "产品 (Product)","资源 GUID (Meter ID)",
        * "服务 (Meter Category)","服务类型 (Meter Sub-Category)",
        * "服务区域 (Meter Region)","服务资源 (Meter Name)",
        * "已消耗资源数量 (Consumed Quantity)","资源费率 (ResourceRate)",
        * "扩展的成本 (ExtendedCost)","服务子区域 (Resource Location)",
        * "服务信息 (Consumed Service)","组件 (Instance ID)",
        * "服务信息 1 (ServiceInfo1)","服务信息 2 (ServiceInfo2)",
        * "附加信息 (AdditionalInfo)","(Tags)","(Store Service Identifier)",
        * "(Department Name)","(Cost Center)","(Unit of Measure)","(Resource Group)",
        * */
        DataStream<LineData> rawSource =
                env.addSource(new Generator(10, 600));

        DataStream<Row> rowSource = rawSource
                        .map(el ->{
                            String[] contents = el.getContents();
                            return  Row.of(
                                    contents[0],  contents[1],
                                    contents[2], contents[3]
                            );
                        })
                        .returns(Types.ROW_NAMED(
                                new String[] {
                                        "AccountOwnerId", "AccountName",
                                        "ServiceAdministratorId", "SubscriptionId"
                                },
                                Types.STRING, Types.STRING,
                                Types.STRING, Types.STRING));

        // add a printing sink and execute in DataStream API

        // try to find a better way to create temporary view
            //        Table tableSource =
            //                tableEnv.fromChangelogStream(source);
        tableEnv.createTemporaryView("InputTable", rowSource);

        tableEnv.createTable("azureDataSink", TableDescriptor.forConnector("filesystem")
                .schema(Schema.newBuilder()
                        .column("AccountOwnerId", DataTypes.STRING())
                        .column("AccountName", DataTypes.STRING())
                        .column("ServiceAdministratorId", DataTypes.STRING())
                        .column("SubscriptionId", DataTypes.STRING())
                        .build())
                .option("path", outputPath)
                .format(FormatDescriptor.forFormat("csv")
                        .option("field-delimiter", "|")
                        .build())
                .build());

        Table table1 = tableEnv.from("InputTable");
        table1.executeInsert("azureDataSink");

         rawSource.print();

        env.execute();
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
                            LineData line = new LineData();
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
