#!/usr/bin/env python3
"""
Sample Python script to test RaspberryJuice modern Minecraft (1.13+) support.

This script demonstrates and verifies placing modern Minecraft blocks
(Copper, Deepslate, Pale Oak, Cherry Planks) directly in front of the player.

Usage:
  1. Make sure your Spigot/Paper server is running with the RaspberryJuice plugin loaded.
  2. Log into the Minecraft server so a player is online.
  3. Run this script:
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

    mc.postToChat("RaspberryJuice modern block placement test starting...")
    print("Connected successfully!")

    # Get player tile position and facing direction vector
    try:
        player_pos = mc.player.getTilePos()
        direction = mc.player.getDirection()
        print(f"Player tile position: {player_pos}")
        print(f"Player facing direction vector: {direction}")
    except Exception as e:
        print(f"Could not fetch player position or direction: {e}")
        print("Make sure a player is logged in on the server.")
        return

    # Determine step direction in front of the player
    # Uses vector components to step 2 blocks in front
    dx = 1 if direction.x > 0.3 else (-1 if direction.x < -0.3 else 0)
    dz = 1 if direction.z > 0.3 else (-1 if direction.z < -0.3 else 0)

    # Default to +X if standing still/looking straight down or up
    if dx == 0 and dz == 0:
        dx = 2

    # Place blocks starting 2 blocks directly in front of the player
    start_x = player_pos.x + (dx * 2)
    start_y = player_pos.y
    start_z = player_pos.z + (dz * 2)

    # Calculate line direction vector (perpendicular to facing or stepping forward)
    step_x = 1 if dx == 0 else 0
    step_z = 1 if dz == 0 else 0
    if step_x == 0 and step_z == 0:
        step_z = 1

    blocks_to_place = [
        ("COPPER_BLOCK", "Copper Block"),
        ("DEEPSLATE", "Deepslate"),
        ("PALE_OAK_PLANKS", "Pale Oak Planks"),
        ("CHERRY_PLANKS", "Cherry Planks"),
    ]

    print("\n--- Placing Modern Blocks In Front of Player ---")
    for i, (material_name, label) in enumerate(blocks_to_place):
        bx = start_x + (i * step_x)
        by = start_y
        bz = start_z + (i * step_z)

        print(f"Placing {label} ({material_name}) at ({bx}, {by}, {bz})...")
        mc.setBlock(bx, by, bz, material_name)

        # Query block to confirm placement
        fetched = mc.getBlock(bx, by, bz)
        print(f"  Query result (world.getBlock): {fetched}")

    # Also test state-based block (e.g. waxed cut copper stair)
    bx_stair = start_x + (len(blocks_to_place) * step_x)
    bz_stair = start_z + (len(blocks_to_place) * step_z)
    stair_state = "cut_copper_stairs[facing=north]"
    print(f"\nPlacing Copper Stair State ({stair_state}) at ({bx_stair}, {start_y}, {bz_stair})...")
    mc.setBlock(bx_stair, start_y, bz_stair, stair_state)
    fetched_stair = mc.getBlock(bx_stair, start_y, bz_stair)
    print(f"  Query result (world.getBlock): {fetched_stair}")

    mc.postToChat("Placed Copper, Deepslate, Pale Oak, and Cherry Planks in front of you!")
    print("\nTest completed successfully!")


if __name__ == "__main__":
    main()
