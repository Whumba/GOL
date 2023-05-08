import pandas as pd
import matplotlib.pyplot as plt

# Steigung
def steigung(x1,y1,x2,y2):
    m = (y2 - y1) / (x2 - x1)
    return m

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

lower = 16000000
upper = 81000000

olower = o.loc[o['cells'] == lower]
oupper = o.loc[o['cells'] == upper]

dlower = d.loc[d['cells'] == lower]
dupper = d.loc[d['cells'] == upper]

print()

omem = round(steigung(lower, olower.iloc[0]['maxMemory'], upper, oupper.iloc[0]['maxMemory']),3)
dmem = round(steigung(lower, dlower.iloc[0]['maxMemory'], upper, dupper.iloc[0]['maxMemory']),3)
memfaktor = round((omem / dmem), 3)

print(f"x-Achse: cells     y-Achse: maxMemory")
print()
print(f"OOPf lower maxMemory: {olower.iloc[0]['maxMemory']}")
print(f"OOPf upper maxMemory: {oupper.iloc[0]['maxMemory']}")
print(f"Steigung OOP_Flyweight maxMemory: {omem}")
print()
print(f"DOP lower maxMemory : {dlower.iloc[0]['maxMemory']}")
print(f"DOP upper maxMemory : {dupper.iloc[0]['maxMemory']}")
print(f"Steigung DOP           maxMemory: {dmem}")
print()
print(f"Faktor OOP_Flyweight / DOP: {memfaktor}")

print('---------------------------------------------------------------------------')
print('---------------------------------------------------------------------------')

oinit = (steigung(lower, olower.iloc[0]['initDuration'], upper, oupper.iloc[0]['initDuration']))
dinit = (steigung(lower, dlower.iloc[0]['initDuration'], upper, dupper.iloc[0]['initDuration']))
initfaktor = round((oinit / dinit), 3)

print(f"x-Achse: cells     y-Achse: initDuration")
print()
print(f"OOPf lower initDuration: {olower.iloc[0]['initDuration']}")
print(f"OOPf upper initDuration: {oupper.iloc[0]['initDuration']}")
print(f"Steigung OOP_Flyweight initDuration: {round(oinit,8)}")
print()
print(f"DOP lower initDuration : {dlower.iloc[0]['initDuration']}")
print(f"DOP upper initDuration : {dupper.iloc[0]['initDuration']}")
print(f"Steigung DOP           initDuration: {round(dinit,8)}")
print()
print(f"Faktor OOP_Flyweight / DOP: {initfaktor}")

print('---------------------------------------------------------------------------')
print('---------------------------------------------------------------------------')

ogen = (steigung(lower, olower.iloc[0]['run/gens'], upper, oupper.iloc[0]['run/gens']))
dgen = (steigung(lower, dlower.iloc[0]['run/gens'], upper, dupper.iloc[0]['run/gens']))
genfaktor = round((ogen / dgen), 3)

print(f"x-Achse: cells     y-Achse: runDuration / generations")
print()
print(f"OOPf lower runDuration / generations: {round(olower.iloc[0]['run/gens'],3)}")
print(f"OOPf upper runDuration / generations: {round(oupper.iloc[0]['run/gens'],3)}")
print(f"Steigung OOP_Flyweight runDuration / generations: {round(ogen,9)}")
print()
print(f"DOP lower runDuration / generations : {round(dlower.iloc[0]['run/gens'],3)}")
print(f"DOP upper runDuration / generations : {round(dupper.iloc[0]['run/gens'],3)}")
print(f"Steigung DOP           runDuration / generations: {round(dgen,9)}")
print(f"Faktor OOP_Flyweight / DOP: {genfaktor}")