![Imgur](http://i.imgur.com/SGUP2UA.png)

### Pixavive Survival (Alpha)

===

**Abstract**

A real-time strategy game involving two sides (Green vs. Red), where the goal of this game is to try and defeat the enemy team.

===

**Gameplay:**

There are two teams, the Green Team and the Red Team, and the player is the Green Team. Each team will have a predetermined number of spawners randomly placed throughout the game area, known as the battlefield. Each spawner will spawn 1 unit that will chase down the enemy units or will aim to destroy nearby spawners. All spawners are set to spawn units at the same time, in the same interval.

When the units of respective teams have glowed to a bright hue of its respective color, they will start their journeys and will try to engage battles whenever it can and whenever the enemy is nearby. Each of the units will attack at the enemies when engaged in battle, and will disappear once the enemy unit has been defeated, and finally, continue to search and destroy.

If there aren't any units of an opposing team, the remaining units will then attack the spawners until all of the spawners have died. The game is finished when one of the teams have no more spawners out in the battlefield.

To place a spawner, click anywhere in the battlefield. The white pixel, also known as the cursor, will mark the position with a spawner that will start building immediately. Once placed, the cooldown starts counting down. You cannot place spawners while the cooldown is active.

===

**Behind the Scenes:**

The game utilizes the A* pathfinding algorithm, using the Manhattan distance heuristics to determine the distance between two nodes A and B, A node is where the unit is currently located, and B node is the node with a non-fixed position, meaning it moves around constantly.

Whenever a unit is spawned, the A* path creation starts by counting all of the nodes that connects two nodes, A and B, in the shortest distance. Once the path is completed, the unit will then keep a copy of the path, and will follow the path's nodes per tick.

===

| Known Issues | Information |
|:---:|:---:|
| Current implementation of the A* pathfinding algorithm is a prototype. Therefore, actual speed of the implementation is slower than average. | It may be possible to refactor and optimize the codes to make it create the path only when needed. |
| The game itself contains hardly any artwork, it may be unappealing to some players. | This is by design, therefore, this won't be fixed. |

===

**Plans:**

* Continue working on the active project, "Pokémon Walking").
* Optimize the A* pathfinding algorithm.

===

| Download Site | Download Link (Latest Stable) |
|:---:|:---:|
| The Helper Forums | http://www.thehelper.net/attachments/pixavive_alpha_v0-08-jar-zip.18582/ |

===

**Want to chat?**
You may head over to The Helper Forums, or Java-Gaming.org to post your feedback and/or comments. Much appreciated.

| Site Page | Discussion Thread |
|:---:|:---:|
| The Helper Forums | http://www.thehelper.net/threads/java-pixavive-alpha-derived-from-demo-concept-i-was-working-on.161041/page-3 |
