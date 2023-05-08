import pandas as pd
import matplotlib.pyplot as plt

o = pd.read_csv('GOL_OOP_Flyweight_Xmx8G.csv')
d = pd.read_csv('GOL_DOP_Xmx8G.csv')

o['cells'] = o['nRows'] * o['nRows']
o['KiB'] = o['maxMemory'] / 1024
o['MiB'] = o['KiB'] / 1024
o['gens/run'] = o['generations'] / o['runDuration']
o['run/gens'] = o['runDuration'] / o['generations']

d['cells'] = d['nRows'] * d['nRows']
d['KiB'] = d['maxMemory'] / 1024
d['MiB'] = d['KiB'] / 1024
d['gens/run'] = d['generations'] / d['runDuration']
d['run/gens'] = d['runDuration'] / d['generations']

mem = o.plot(x='cells', y='MiB', grid='True')
d.plot(x='cells', y='MiB', grid='True', ax=mem)
mem.legend(['OOP_Flyweight','DOP'])
mem.ticklabel_format(style='plain')
mem.set_ylabel('maxMemory in MiB')

init = o.plot(x='cells', y='initDuration', grid='True')
d.plot(x='cells', y='initDuration', grid='True', ax=init)
init.legend(['OOP_Flyweight','DOP'])
init.ticklabel_format(style='plain')
init.set_ylabel('initDuration in ms')

runPERgens = o.plot(x='cells', y='run/gens', grid='True')
d.plot(x='cells', y='run/gens', grid='True', ax=runPERgens)
runPERgens.legend(['OOP_Flyweight','DOP'])
runPERgens.ticklabel_format(style='plain')
runPERgens.set_ylabel('runDuration / generations in ms')

plt.show()