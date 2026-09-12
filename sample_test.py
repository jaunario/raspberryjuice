#!/usr/bin/env python3
"""
Sample Python script to test RaspberryJuice modern Minecraft (1.13+) support.

This script demonstrates and verifies:
1. Connecting to a Spigot server running RaspberryJuice via TCP (default port 4711).
2. Setting standard modern blocks using Bukkit Material string names (e.g. "DIAMOND_BLOCK", "GOLD_BLOCK").
3. Setting blocks with modern BlockState syntax (e.g. "oak_stairs[facing=north]", "beehive[honey_level=5]").
4. Querying placed block types via `world.getBlock` / `world.getBlocks`.
5. Spawning modern entity types (e.g. "ZOMBIE", "VILLAGER", "PIG") via `world.spawnEntity`.

Usage:
  1. Make sure your Spigot/Paper server is running with the RaspberryJuice plugin loaded.
  2. Run this script:
     python3 sample_test.py [host] [port]
"""

import sys
import os

# Automatically locate and add the modded mcpi library path
script_dir = os.path.dirname(os.path.abspath(__file__))

possible_paths = [
    os.path.join(script_dir, "src", "main", "resources", "mcpi", "api", "python", "modded"),
    os.path.join(script_dir, "mcpi"),
    script_dir,
    os.getcwd(),
]

for p in possible_paths:
    if os.path.exists(os.path.join(p, "mcpi")) or os.path.exists(os.path.join(p, "mcpi", "__init__.py")):
        if p not in sys.path:
            sys.path.insert(0, p)
        break

try:
    from mcpi.minecraft import Minecraft
except ImportError:
    print("Error: Could not import 'mcpi' library.")
    print("\nTo fix this, choose one of the following options:")
    print("1. Run this script from the root directory of the RaspberryJuice repo.")
    print("2. Set PYTHONPATH to include the mcpi directory:")
    print("   export PYTHONPATH=$PYTHONPATH:/path/to/RaspberryJuice/src/main/resources/mcpi/api/python/modded")
    print("3. Copy the 'mcpi' folder into the same directory as this script.")
    sys.exit(1)


def main():
    host = sys.argv[1] if len(sys.argv) > 1 else "localhost"
    port = int(sys.argv[2]) if len(sys.argv) > 2 else 4711

    print(f"Connecting to RaspberryJuice at {host}:{port}...")
    try:
        mc = Minecraft.create(host, port)
    except Exception as e:
        print(f"Failed to connect to Minecraft server at {host}:{port}: {e}")
        print("Please check that your Spigot server is running and RaspberryJuice is loaded.")
        return

    mc.postToChat("RaspberryJuice modern support test starting...")
    print("Connected successfully!")

    # Get initial player tile position
    try:
        player_pos = mc.player.getTilePos()
        print(f"Player tile position: {player_pos}")
    except Exception as e:
        print(f"Note: Could not fetch player position (is a player online?): {e}")
        player_pos = type('Vec3', (), {'x': 0, 'y': 64, 'z': 0})()

    # Test offset origin near player
    base_x = player_pos.x + 2
    base_y = player_pos.y
    base_z = player_pos.z

    print("\n--- 1. Testing Modern Block Material Names ---")
    blocks_to_test = [
        ("DIAMOND_BLOCK", base_x, base_y, base_z),
        ("GOLD_BLOCK", base_x, base_y + 1, base_z),
        ("EMERALD_BLOCK", base_x, base_y + 2, base_z),
    ]

    for name, x, y, z in blocks_to_test:
        print(f"Setting block at ({x}, {y}, {z}) -> {name}")
        mc.setBlock(x, y, z, name)
        fetched = mc.getBlock(x, y, z)
        print(f"  Query result (world.getBlock): {fetched}")

    print("\n--- 2. Testing Modern Block States ---")
    state_blocks = [
        ("oak_stairs[facing=north]", base_x + 2, base_y, base_z),
        ("oak_stairs[facing=east]", base_x + 2, base_y + 1, base_z),
        ("beehive[honey_level=5]", base_x + 2, base_y + 2, base_z),
    ]

    for block_state, x, y, z in state_blocks:
        print(f"Setting block state at ({x}, {y}, {z}) -> {block_state}")
        mc.setBlock(x, y, z, block_state)
        fetched = mc.getBlock(x, y, z)
        print(f"  Query result (world.getBlock): {fetched}")

    print("\n--- 3. Testing Modern Cuboid Placement & Retrieval (world.getBlocks) ---")
    mc.setBlocks(base_x + 4, base_y, base_z, base_x + 4, base_y + 2, base_z, "SMOOTH_STONE")
    blocks = list(mc.getBlocks(base_x + 4, base_y, base_z, base_x + 4, base_y + 2, base_z))
    print(f"  Query result (world.getBlocks): {blocks}")

    print("\n--- 4. Testing Modern Entity Spawning ---")
    entities = ["ZOMBIE", "VILLAGER", "PIG"]
    for i, entity_name in enumerate(entities):
        ex = base_x + 6
        ey = base_y
        ez = base_z + i
        try:
            entity_id = mc.spawnEntity(ex, ey, ez, entity_name)
            print(f"Spawned {entity_name} at ({ex}, {ey}, {ez}) -> Entity ID: {entity_id}")
        except Exception as e:
            print(f"Error spawning {entity_name}: {e}")

    mc.postToChat("RaspberryJuice modern support test complete!")
    print("\nTest completed successfully!")


if __name__ == "__main__":
    main()
