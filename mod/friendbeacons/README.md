# Friend Beacons — Minecraft 26.2

Live Xaero's Minimap pins of friends who opt in. **Paper, Fabric, and NeoForge share one packet format**, so a Paper server can drive Fabric and NeoForge clients (and a Fabric/NeoForge server can do the same).

Minecraft **26.2**. Java **25**. Client optional. Pins only appear if Xaero's Minimap is installed.

## Who needs what

| Piece | Where it goes | Needed to |
| --- | --- | --- |
| `FriendBeacons-paper-26.2.jar` | Paper server `plugins/` | Run commands + broadcast pins |
| `FriendBeacons-fabric-26.2.jar` | Fabric server `mods/` **or** Fabric client `mods/` | Server: same as Paper. Client: see pins |
| NeoForge jar from `./gradlew :neoforge:build` | NeoForge server/client `mods/` | Same as Fabric |
| Xaero's Minimap 26.2 | Viewing **client** only | Draw the waypoint |

A Paper server + Fabric clients is the usual mix. Put the Paper jar on the server. Friends who want pins install the Fabric (or NeoForge) jar **and** Xaero's on their game. Players without the client jar still join and can still run `/fwp`.

Do not mix Minecraft 1.21.1 with 26.2 — they are different protocol versions.

## Commands

| Command | Effect |
| --- | --- |
| `/fwp share <player>` | That player may see your live pin |
| `/fwp unshare <player>` | Revoke |
| `/fwp everyone` | Anyone online with the client + Xaero's can see you |
| `/fwp nobody` | Hide (default) |
| `/fwp list` / `/fwp incoming` | Who can see you / who is sharing with you |
| `/fwp color <color>` | Pin color |
| `/fwp` | Status |

Sharing is one-way. Alias: `/friendbeacons`.

## Protocol (so the loaders interoperate)

Custom payload channels:

- `friendbeacons:update`
- `friendbeacons:remove`

`update` body (Minecraft FriendlyByteBuf): UUID, UTF name (max 32), UTF dimension (max 128), VarInt x y z, byte color 0–15.

`remove` body: UUID.

Paper sends these as plugin messages. Fabric and NeoForge register the same payload ids.

## Build

Java 25.

```
./gradlew :paper:build :fabric:build :neoforge:build
```

Jars land in each subproject `build/libs/`.
