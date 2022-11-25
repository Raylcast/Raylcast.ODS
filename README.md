# Raylcast.ODS
A plugin that allows you to manage systemd services using ingame commands.
The intended use case is to run this on your lobby server and configure your other servers using ODS. 
Users can choose which one to play on, only that one will be running.

## Usage
- Build plugin & put `.jar` into your `plugins` folder
- Use ingame command `/ods`, tab-completion will do the rest ^^

## Permissions
There are permission nodes for each command available. No argument based permissions as of now!

### `raylcast.ods.command.ods`
Access to /ods, but no sub commands

### `raylcast.ods.command.ods.start`
Access to `/ods start`
### `raylcast.ods.command.ods.stop`
Access to `/ods stop`
### `raylcast.ods.command.status`
Access to `/ods status`

