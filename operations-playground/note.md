## difference between docker compose run and docker compose exec
https://stackoverflow.com/questions/65100572/docker-compose-difference-run-exec-and-what-happens-to-the-layers#:~:text=docker-compose%20exec%20will%20run%20inside%20your%20existing%2C%20running,entrypoint%2C%20while%20exec%20does%20override%20the%20defined%20entrypoint.


### step1
 mkdir -p .\tmp\flink-checkpoints-directory
 mkdir -p  .\tmp\flink-savepoints-directory

 ### step2
 docker-compose exec kafka kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic input

 docker-compose exec kafka kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic output