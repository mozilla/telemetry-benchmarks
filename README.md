# Benchmarks

Benchmarks allow for reproducible measurements of how well different processes
run within Mozilla Telemetry. They are used to test performance of Telemetry
tools. The goal is to use benchmarks to help determine improvements in the
future.

## Expectations for Experiments

We want to display outcomes uniformly. Each Benchmark folder should contain a
README that describes the `Background`, `Setup`, `Results`, and `Discussion`
for the process being analyzed. Each folder should also include files that can
allow users to replicate the benchmarks.

## Contribute:

To include benchmark tests:
1. Create a separate folder for each benchmark being tested.
2. Include a README describing the tests, analysis, and sample information about expected outcomes of the tests. 
3. Upload the tests.

Benchmark Tests should be formatted so that they can easily be run. Preferably, one command or script should be able to execute them. Make sure all dependencies are included.
