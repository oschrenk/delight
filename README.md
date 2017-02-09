# Delight

Download schedule and book classes for https://delightyoga.com

## Build

```
sbt assembly
```

## Requirements

**Create** credentials

```
touch $HOME/.delight
```

and fill it with your details

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

**Book** a class

```
$ java -jar target/scala-2.12/delight-assembly-*.jar book 86594
```

