# presto-hyperloglog

Algebird's HyperLogLog support for Facebook Presto (prestodb.io).

## Functions

There are two types of functions, aggregation and scalar.

### Aggregation

`merge(<hll: HLL>)` -> HLL

### Scalar

`create_hll(<element:VARCHAR>, <bits: BIGINT>)` -> HLL

`cardinality(<hll: HLL>)` -> BIGINT

## Building

Running `mvn clean package` will generate a `jar` file as well as a `zip` file
containing this plugin along with all of its dependencies.

## Deployment on AWS EMR

```bash
cd /usr/lib/presto/plugin
curl -sLO https://github.com/robotblake/presto-hyperloglog/releases/download/$PRESTO_VERSION/presto-hyperloglog-$PRESTO_VERSION.zip
unzip presto-hyperloglog-$PRESTO_VERSION.zip
rm presto-hyperloglog-$PRESTO_VERSION.zip
```
