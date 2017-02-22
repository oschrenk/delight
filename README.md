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
Class(78765,Time(2017-02-15T20:00,2017-02-15T21:30),Yoga Basics,De Clercqstraat,Hanna Paz,All levels)
Class(78329,Time(2017-02-15T20:15,2017-02-15T21:30),Astanga Mysore,De Clercqstraat,Lidewij Severins,Beginners)
Class(86594,Time(2017-02-15T20:15,2017-02-15T21:45),Astanga Led,Weteringschans,Marije Roede,All levels)
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
**Show previous ** classes

```
$ ./delight upcoming
```

## Advanced Usage

### Filtering classes

In your `$HOME/.delight/config` config file you can add one ore more of these

```
filter.teacher = [ "John Doe" ]
filter.experience = [ "Experienced" ]
filter.name = [ "Prenatal" ]
```

to *optionally* filter out certain classes, teachers, experience levels from the schedule.
The wording needs to be exact.

### Output format

The `schedule`, `upcoming`, and `previous` command also support a `-f, --format` which
controls the output format.

*Supported Formats*

- `--format pretty` (default) colored output of the classes.
- `--format khal` (default) prints a command to create [khal](https://github.com/pimutils/khal) entries.
- `--format nocolor` (default) un-colored output of the classes.

