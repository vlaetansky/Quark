import sys
from os import system as run

# TODO allow input from cmd
category = 'world'
flag = 'azalea_wood'

def main():
	for arg in sys.argv:
		if not '.py' in arg:
			makeWood(arg)

def makeWood(type):
	run(f"py generic_block.py {type}_planks")
	run(f"py pillar.py {type}_log stripped_{type}_log")
	run(f"py wood_block.py {type} stripped_{type}")
	run(f"py stairs_slabs.py category={category} flag={flag} {type}_planks")
	run(f"py post_modded.py flag={flag} {type} stripped_{type}")
	run(f"py bookshelves.py {type}")
	run(f"py chest.py {type}")
	run(f"py ladder.py {type}")
	run(f"py door.py {type}")
	run(f"py trapdoor.py {type}")
	run(f"py fence_gates.py {type}")
	run(f"py fences.py {type}")
	run(f"py sign.py {type}")
	run(f"py buttons.py texname={type}_planks {type}")
	run(f"py pressure_plates.py texname={type}_planks {type}")
	run(f"py generic_item.py {type}_boat")

main()