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
$ java -jar target/scala-2.12/delight-assembly-*.jar schedule
...
Class(78765,Time(2017-02-15T20:00,2017-02-15T21:30),Yoga Basics,De Clercqstraat,Hanna Paz,All levels)
Class(78329,Time(2017-02-15T20:15,2017-02-15T21:30),Astanga Mysore,De Clercqstraat,Lidewij Severins,Beginners)
Class(86594,Time(2017-02-15T20:15,2017-02-15T21:45),Astanga Led,Weteringschans,Marije Roede,All levels)
...
```

**Show upcoming** classes

```
$ java -jar target/scala-2.12/delight-assembly-*.jar upcoming
```

**Book** a class

```
$ java -jar target/scala-2.12/delight-assembly-*.jar book 86594
```

**Cancel** a class

```
$ java -jar target/scala-2.12/delight-assembly-*.jar cancel 86594
```

## Advanced Usage

### Filtering classes

In your `$HOME/.delight/config` config file you can add

```
filter.teacher = [ "John Doe" ]
filter.experience = [ "Experienced" ]
```

to *optionally* filter out certain teachers or experience levels from the schedule.
The wording needs to be exact.

### Output format

The `schedule` and `upcoming` command also support a `-f, --format` which
controls the output format.

Right now by default it prints a colored output of the classes, but if you
specify `--format khal` it prints the classes as a command to create [khal](https://github.com/pimutils/khal) entries.

```
$ java -jar target/scala-2.12/delight-assembly-*.jar upcoming
khal new 20.02. 18:15 19:45 Vinyasa w/ Inge Peters
...
```

