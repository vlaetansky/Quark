import sys
from os import system as run

def main():
	for arg in sys.argv:
		if not '.py' in arg:
			makeWood(arg)

def makeWood(type):
	run(f"py generic_block.py {type}_planks")

main()