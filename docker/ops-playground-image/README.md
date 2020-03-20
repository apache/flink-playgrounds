# Flink Operations Playground Image

The image defined by the `Dockerfile` in this folder is required by the Flink operations playground.

The image is based on the official Flink image and adds a demo Flink job (Click Event Count) and a corresponding data generator. The code of the application is located in the `./java/flink-ops-playground` folder.

----
## Python PyFlink StreamTable example

error 

```
>docker-compose exec client python ClickEventCount.py
Traceback (most recent call last):
  File "ClickEventCount.py", line 24, in <module>
    Kafka()
  File "/usr/local/lib/python3.7/dist-packages/pyflink/table/descriptors.py", line 705, in __init__
    self._j_kafka = gateway.jvm.Kafka()
TypeError: 'JavaPackage' object is not callable
```

From https://blog.csdn.net/ghostyusheng/article/details/102696867

如果你是按照上面教程，你会发现pyflink.table.descriptors import Kafka 中的 Kafka()实例化会失败,然而官方也没有给出解决方案，根据不断的尝试摸索，正确的做法是，flink/flink-connectors/flink-connector-kafka-base/target里面的flink-connector-kafka-base_2.11-1.9-SNAPSHOT.jar + original-flink-connector-kafka-base_2.11-1.9-SNAPSHOT.jar JAR包复制到flink/flink-python/dist/apache-flink-1.9.dev0/deps/lib目录，让后使用tar czvf命令重新打包成pyflink依赖包，把之前的卸载掉pip3 uninstall apache-flink && pipi3 install 新打包.tar.gz.
————————————————
版权声明：本文为CSDN博主「ghostyusheng」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/ghostyusheng/article/details/102696867

Logging into the `clint` container, we find python3.7 installation with dist at 
`/usr/local/lib/python3.7/dist-packages/pyflink/lib`, with `flink-dist_2.11-1.10.0.jar,flink-table_2.11-1.10.0.jar, slf4j-log4j12-1.7.15.jar, flink-table-blink_2.11-1.10.0.jar, log4j-1.2.17.jar`

Sounds likwe need to copy the `flink-connector-kafka-base*.jar` and `original-flink-connector-kafka-base_2.11-1.9-SNAPSHOT.jar` to here. 

According to the above post, we need to reinstall `pyflink` from the above source. But maybe adding the JAR files to the right place will do the trick?

### Fixing JAR dependencies

The following [Dockerfile_pylink_dep](Dockerfile_pylink_dep) fixed the above issue (https://github.com/garyfeng/flink-playgrounds/issues/1 as well as realted issues such as https://github.com/garyfeng/flink-playgrounds/issues/2) by copying in flink-connectors, flink-formats, and other missing JARs.

It requires a two-stage building strategy, where we first compile the flink system, including `flink-python`, then copy the JARs to the `flink-python`. The next stage starts with the official flink docker, install python, and the custom `pyflink` library from the previous step. 

### Giving up for now
This works, but then we run into additional java errors for creating a streaming table environment (https://github.com/garyfeng/flink-playgrounds/issues/3).

It looks like the product is just not ready. 

## Apache Beam

Analternative is to use `Apache Beam` to program the pipeline, and use `flink` as a runner. 

https://flink.apache.org/ecosystem/2020/02/22/apache-beam-how-beam-runs-on-top-of-flink.html

https://beam.apache.org/get-started/try-apache-beam/

https://beam.apache.org/documentation/runners/direct/

https://github.com/apache/beam/tree/master/sdks/python/apache_beam/examples


