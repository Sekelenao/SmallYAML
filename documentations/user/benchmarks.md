<h1 align="center">Benchmarks</h1>

## How benchmarks are done

The Java Microbenchmark Harness (JMH) is leveraged to ensure the optimal performance of parsing mechanisms.

Benchmarks specifically focus on evaluating the efficiency of loading a large configuration file, located at
`src/test/resources/document/correct/huge_document/document.yaml`, into a `PermissiveDocument` object.

Each benchmark run is executed in a dedicated JVM instance, configured with an initial heap size of 1GB and a maximum 
heap size of 1GB (`-Xms1g`, `-Xmx1g`), to provide a controlled and consistent environment.

A rigorous testing methodology is used, beginning with five warmup iterations (each lasting 2 seconds) to allow the 
JVM to perform its optimizations, followed by `cnt` measurement iterations (also 2 seconds each).

Results are then reported as the average time taken per operation, expressed in milliseconds, providing clear and 
actionable performance metrics.

## Results ordered by the most recent version

#### Snapshot 1.0.4

| Mode | Cnt | Score   | Error   | Units |
|------|-----|---------|---------|-------|
| avgt | 20  | 169,295 | ± 3,091 | ms/op |

#### Snapshot 1.0.2

| Mode | Cnt | Score   | Error   | Units |
|------|-----|---------|---------|-------|
| avgt | 20  | 166,171 | ± 3,574 | ms/op |

#### Snapshot 1.0.1

| Mode | Cnt | Score   | Error   | Units |
|------|-----|---------|---------|-------|
| avgt | 20  | 167,980 | ± 2,320 | ms/op |

#### Snapshot 1.0.0

| Mode | Cnt | Score   | Error   | Units |
|------|-----|---------|---------|-------|
| avgt | 20  | 191,963 | ± 2,617 | ms/op |

