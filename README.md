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

and *optionally* with some teachers or experience levels you want filtered out
from the schedule. The wording needs to be exact.

```
filter.teacher = [ "John Doe" ]
filter.experience = [ "Experienced" ]
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

**Book** a class

```
$ java -jar target/scala-2.12/delight-assembly-*.jar book 86594
```

**Cancel** a class

```
$ java -jar target/scala-2.12/delight-assembly-*.jar cancel 86594
```

**Show upcoming** classes

```
$ java -jar target/scala-2.12/delight-assembly-*.jar upcoming
```

