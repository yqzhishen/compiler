import os


src = os.popen('for /r src %i in (*.java) do @echo %i')
dst = open('path.txt', 'w')
print('\n'.join('.' + filename[len(os.getcwd()):] for filename in src.read().replace('\\', '/').split()), file=dst)
