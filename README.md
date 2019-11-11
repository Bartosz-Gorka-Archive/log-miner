# Activity Miner based on Log file
We want to analyze the log from a single business process.
Inside log, each line means separated, independent business case.
Each activity is specified by a single character which is the name of the activity.
The activity can be used more than once in a single case.

## Example log
```
abcd
acbd
aed
```

Inside log, we have five unique activities: `[a, b, c, d, e]` and three business cases (each line).

## Relations between activities
Each pair of activities `a` and `b` can be described as **one** relation:
* **Causality**: `a > b` and `not b > a`
* **Reversed Causality**: `b > a` and `not a > b`
* **Parallel**: `a > b` and `b > a`
* **Choice**: `not a > b` and not `b > a`

Operator `>` mean direct succession (activities in the log are next to each other)

## What we want
We expect to find maximum count pairs of activities which:
* On input reference to log file
* Output formatted as `({left}, {right})` with one group in single line
* In `left group` activities are in `Choice` relation between each other
* In `right group` activities are in `Choice` relation between each other
* Between `left` and `right` group `Causality` relation

## How to run
```java
java -jar pathToJAR.jar logFilePath
```
