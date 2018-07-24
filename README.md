# Benchmarks

Benchmarks allow for reproducible measurements of how well different processes run within Mozilla Telemetry. They are used to test performance of Telemetry tools with experimental approaches to help determine improvements in the future. The goal is to be able to place tests for different processes here in order to compare them to related results, to prove that improvements occur as we improve on tools for data science. The repository contains tests in both Scala and Python. Results of running the tests are provided with each group of tests in order to demonstrate expected outcomes. Instructions are also provided in order to replicate the results.

## Contribute:

To include benchmark tests:
1. Create a separate folder for each benchmark being tested.
2. Include a README describing the tests, analysis, and sample information about expected outcomes of the tests. 
3. Upload the tests.

Tests should be formatted so that they can easily be run. Preferably, one command or script should be able to execute them. Make sure all dependencies are included.
