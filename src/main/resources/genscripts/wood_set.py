import sys
from os import system as run

def main():
	for arg in sys.argv:
		if not '.py' in arg:
			makeWood(arg)

def makeWood(type):
	run(f"py generic_block.py {type}_planks")
	run(f"py pillar.py {type}_log stripped_{type}_log")
	run(f"py wood_block.py {type} stripped_{type}")

main()