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

### Plaintext

```
username="john@doe.com"
password="my-long-password"
```

### Keychain support

Or if you have a mac, you can use your keychain to store the password for you.

First store your password in the keychain:

```
security add-generic-password -a "john@doe.com" -D "Delight" -s "delightyoga.com" -w
```

it will ask for the password

```
username="john@doe.com"
password.keychain = true
```

The first time you use the application a dialog pop's up asking for permission.

## Usage

**Fetch schedule** for current week

```
$ ./delight schedule
...
68160 Tue 18:15 Vinyasa w/ Jane Doe @ Weteringschans
82563 Tue 20:00 Yin w/ John Doe @ Weteringschans
70760 Tue 20:00 Yin w/ Various Teachers @ Prinseneiland
...
```

**Book** one ore more classes

```
$ ./delight book 86594
$ ./delight book 86594 12345
```

**Cancel** one or more classes

```
$ ./delight cancel 86594
$ ./delight cancel 86594 12345
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

### Favorites

In the `schedule` view you can highlight teachers in bold by marking them as your favourite

```
favorite.teacher = [ "John Doe" ]
```

The wording needs to be exact.

### Preferred time

If you only want to show classes after a certain time, you can do so by adding

```
preferred.time = "18-" ]
```

and then calling the ui with the flag `--preferred`


### Output format

The `schedule`, `upcoming`, and `previous` command also support a `-f, --format` which
controls the output format.

*Supported Formats*

- `--format pretty` (default) colored output of the classes.
- `--format khal` (default) prints a command to create [khal](https://github.com/pimutils/khal) entries.
- `--format nocolor` (default) un-colored output of the classes.

