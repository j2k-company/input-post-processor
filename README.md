# Input Post-Processor (hereinafter IPP)

This is a winapi-based utility This is a WinApi-based utility for input interpolation in real-time.

## Using

### Configuration and launch
#### Configuration
For start work with IPP place a file ```substitutions.json``` next to the executable file with the filled in according to the following example:
```json
{
  "nick" : "Jaka2005",
  "mail" : "jakalogin1808@gmail.com",
  "key" : "value",
  ...
}
```
***WARNING: keys are case-sensitive and should consist only of printable characters***

#### Start
Now that you have filled out the config, you can just launch the application)

### Using the application
#### Substitution
Тow that the app is running, try typing in some input field ```%key%``` (where ```key``` is one of the keys from your config) and the application will automatically replace it with the value.

*NOTE: the application can't work yet without being open explicitly*
