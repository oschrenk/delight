# Delight

Download schedule, book and cancel classes for [Delight Yoga](https://delightyoga.com) via command line

## Build

```
sbt assembly
```

## Requirements

**Create** config

```
mkdir $HOME/.delight
touch $HOME/.delight/config
```

and fill it with your credentials

```
username="john@doe.com"
password="my-long-password"
```

## Usage

**Fetch schedule** for current week

```
$ ./delight schedule
...
68160 Tue 18:15 Vinyasa w/ Jane Doe @ Weteringschans
82563 Tue 20:00 Yin w/ John Doe  @ Weteringschans
70760 Tue 20:00 Yin w/ Various Teachers @ Prinseneiland
...
```

**Book** a class

```
$ ./delight book 86594
```

**Cancel** a class

```
$ ./delight cancel 86594
```

**Show upcoming** classes

```
$ ./delight upcoming
```

**Show previous** classes

```
$ ./delight previous
```

## Advanced Usage

### Filtering classes

In your `$HOME/.delight/config` config file you can add one ore more of these

```
filter.teacher = [ "John Doe" ]
filter.experience = [ "Experienced" ]
filter.name = [ "Prenatal" ]
filter.location = [ "Nieuwe Achtergracht" ]
```

to *optionally* filter out certain classes, teachers, experience levels or locations from the schedule.
The wording needs to be exact.

### Output format

The `schedule`, `upcoming`, and `previous` command also support a `-f, --format` which
controls the output format.

*Supported Formats*

- `--format pretty` (default) colored output of the classes.
- `--format khal` (default) prints a command to create [khal](https://github.com/pimutils/khal) entries.
- `--format nocolor` (default) un-colored output of the classes.

