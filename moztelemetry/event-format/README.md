# Compare Compressed Size of Array Events vs. Object Events

Events in Glean are tuples: `[timestamp, category, event_name, extra]`. A question arose:
should we use an Object representation instead? e.g.
```
{
    "timestamp": <int>,
    "category": <string>,
    "event_name": <string>,
    "extra": <map<string, string>>
}
```

The big question is if we will be sending much more information over with that representation, after
compression.

## Results

These benchmarks show a slight increase in size (~5%) when including the object-representation
of the events. We can safely proceed with including them.
