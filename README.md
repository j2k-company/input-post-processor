# Input Post-Processor (hereinafter IPP)

This is a winapi-based utility This is a WinApi-based utility for input interpolation in real-time.

## Using

### Configuration

For start work with IPP place a file ```substitutions.json``` next to the executable file
with the filled in according to the following example:
```json
{
  "hot_keys" : {
    "ALT+WIN+S" : "SHOW_WINDOW",
    "CTRL+ALT+H" : "HIDE_WINDOW",
    "ALT+SHIFT+=" : "SHUTDOWN",
    "hotKey" : "action",
    ...
  },
  "substitutions" : {
    "nick": "Jaka2005",
    "mail": "jakalogin1808@gmail.com",
    "key": "value",
    ...
  }
}
```

#### HotKeys
IPP supports customizable hotkeys to control him.
Hotkeys are configured as follows:  
As a field key entered keys combination like a `MODIFIER_KEY+KEY_CHAR`.
The combination can contain any number of modifier keys and **only one** key of the key char.
It may also not contain modifier key. ***But char key is required and must be a printable character.
(if it is a letter is it desirable that it be written in uppercase)***

Here's the list of modifier keys: `WIN`, `ALT`, `SHIFT`, `CTRL`.
***They should be written only in this form.***

Keys separated by `+` symbol and written without spaces.


As a value entered the action to be performed when pressing the hotkey.
The following actions are currently supported:
 * `SHUTDOWN` - shut down the IPP
 * `SHOW_WINDWOW` - show the console window of the IPP
 * `HIDE_WINDWOW` - hide the console window of the IPP

if you don't want to fill `hot_key` field just leave it blank.
As example: `"hot_key" : {}` 

You can create any number of hotkeys for the action.

#### Substitutions
***Keys are case-sensitive and should consist only of printable characters***
IPP searches for keys and replaces them with the values you specified in the configuration.

### Start
Now that you have filled out the config, you can just launch the application)

### Using the application
#### Substitution
Now that the app is running, try typing in some input field ```%key%``` (where ```key``` is one of the keys from your config) and the application will automatically replace it with the value.

#### HotKeys
Just a try press the hotkey ¯\_(ツ)_/¯
