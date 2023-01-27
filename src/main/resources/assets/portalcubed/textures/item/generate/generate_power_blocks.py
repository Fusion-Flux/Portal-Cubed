"""
This script is intended to be run from the `item` directory, *not* from the `generate` directory.
"""
from PIL import Image


def save(img: Image.Image, name: str) -> None:
    img.save(name)
    print('Wrote', name)


img_base = Image.open('power_block.png')
print('Read power_block.png')

for level in range(16):
    img = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    img.paste(img_base)
    img.paste(Image.open(f'generate/light_{level:02}.png').crop((0, 0, 8 if level >= 10 else 4, 5)))
    save(img, f'power_block_{level:02}.png')
