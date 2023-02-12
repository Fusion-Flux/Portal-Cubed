import glob
import os

for f in glob.glob('*.wav'):
    print('Converting', f)
    os.system(f'ffmpeg -i {f} -ac 1 {f[:-4]}.ogg')
    os.remove(f)
    print()
