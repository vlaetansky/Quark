import sys, os, json
from os import system as run

# TODO allow input from cmd
modid = 'quark'
category = 'world'
flag = 'azalea_wood'

def main():
	for arg in sys.argv:
		if not '.py' in arg:
			makeWood(arg)

def makeWood(type):
	#run(f"py generic_block.py {type}_planks")
	#run(f"py pillar.py {type}_log stripped_{type}_log")
	#run(f"py wood_block.py {type} stripped_{type}")
	#run(f"py stairs_slabs.py category={category} flag={flag} {type}_planks")
	#run(f"py post_modded.py flag={flag} {type} stripped_{type}")
	#run(f"py bookshelves.py {type}")
	#run(f"py chest.py {type}")
	#run(f"py ladder.py {type}")
	#run(f"py door.py {type}")
	#run(f"py trapdoor.py {type}")
	#run(f"py fence_gates.py {type}")
	#run(f"py fences.py {type}")
	#run(f"py sign.py {type}")
	#run(f"py buttons.py texname={type}_planks {type}")
	#run(f"py pressure_plates.py texname={type}_planks {type}")
	#run(f"py generic_item.py {type}_boat")

	appendTags(type)

def appendTags(type):
	print('')
	print('Appending tags for', type)

	addToTag('logs', type, ["%_log", "stripped_%_log", "%_wood", "stripped_%_wood"])

def addToTag(tag, type, items, is_block=True, mirror=True):
	if mirror:
		addToTag(tag, type, items, not is_block, False)

	if ':' in tag:
		resloc = tag.split(':')
	else:
		resloc = ['minecraft', tag]

	tag_type = 'blocks' if is_block else 'items'
	path = f"../data/{resloc[0]}/tags/{tag_type}/{resloc[1]}.json"

	if not os.path.exists(path):
		with open(path, 'w') as f:
			f.write('{ "replace": false, "values": [] }')

	with open(path, 'r') as f:
		data = json.load(f)
		values = data['values']

		changed = False
		for raw_item in items:
			item = f"{modid}:{raw_item}"
			item = item.replace('%', type)

			if not item in values:
				values.append(item)
				changed = True
		
		if changed:
			with open(path, 'w') as fw:
				json.dump(data, fw, indent=4)
				print('Updating', path)

main()