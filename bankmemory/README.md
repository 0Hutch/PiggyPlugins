# Bank Memory [![Active Installs](http://img.shields.io/endpoint?url=https://i.pluginhub.info/shields/installs/plugin/bank-memory)](https://runelite.net/plugin-hub/LazyFaith)

A plugin for [RuneLite](https://github.com/runelite/runelite) which remembers the contents of your bank and displays that in a searchable interface.


## Features
- Remembers the bank contents of all your accounts
    - **Current bank saves** - linked to an account and automatically update as your bank contents changes
    - **Snapshot bank saves** - a snapshot of what your bank was at a specific time
- View any of your bank saves at any time
- Quickly search the contents of your banks
- Compare two banks saves to see the difference in items and value
- Easily export item data

**NB**: You must log in to an account and open the bank for the plugin to be able to actually get the data.


## Version history

- v1.3.0
  - Added item tooltip in inventory stating how many of that item you have stored in your bank (thank you, Fiffers)
  - Updated for new Runelite APIs (thank you, YvesW)
  - Bug fix - Now works if you log in with a Jagex account
- v1.2.0
  - Add ability to export bank item data in TSV format (Hint: right click to open context menu)
  - Filter out null items from item lists
  - Filter out bank filler from item lists
- v1.1.2
  - Updated to work with new RuneLite APIs (thank you, Septem151) 
- v1.1.1
  - Bug fix - Stop current bank panel sometimes being reset and left blank
- v1.1
  - Separate current bank saves for regular worlds, League worlds, Tournament (Unrestricted) worlds, and Deadman Mode worlds.
  - Display total Grand Exchange value and High Alch value of bank saves (and the difference when comparing 2 banks saves)
- v1.0
  - Initial release