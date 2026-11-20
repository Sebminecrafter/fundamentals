# Miscellaneous Settings

## Time formats

Fundamentals uses Java time patterns in commands and log output.

```yaml
log-time-format: "MM/dd/yyyy HH:mm:ss"
time-format: "EEEE, LLLL d, yyyy, h:mm a (zz)"
```

| Key               | Purpose                                                           |
|-------------------|-------------------------------------------------------------------|
| `log-time-format` | Timestamp format used by the console log formatter                |
| `time-format`     | Human-readable date format used in commands and time placeholders |

The values follow Java `SimpleDateFormat` patterns.
For example, `MM` is the month, `dd` is the day, and `yyyy` is the four-digit year.

## Config version

```yaml
version: 1
```

The `version` key is used for plugin update tracking.
It is highly advised not to change this.

The [configuration index](../) contains links to all dedicated settings pages.
