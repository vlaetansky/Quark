from jsongen import *

copy([
	('block_model_door_top.json', 'assets/{modid}/models/block/{name}_door_top.json'),
	('block_model_door_bottom.json', 'assets/{modid}/models/block/{name}_door_bottom.json'),
	('block_model_door_top_hinge.json', 'assets/{modid}/models/block/{name}_door_top_hinge.json'),
	('block_model_door_bottom_hinge.json', 'assets/{modid}/models/block/{name}_door_bottom_hinge.json'),

	('block_item_door.json', 'assets/{modid}/models/item/{name}_door.json'),
	('blockstate_door.json', 'assets/{modid}/blockstates/{name}_door.json')
])

localize((
	lambda name, modid: 'block.{modid}.{name}_door'.format(name = name, modid = modid),
	lambda name, modid: re.sub(r's$', '', localize_name(name, modid)) + ' Door'
))

import update_drop_tables