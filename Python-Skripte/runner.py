import csv
import subprocess
import sys
import time

def runTest(jarFile, jvmOptions, rows, cols, maxTime, threshold, seed):
    # Documentation File
    xmx = jvmOptions.replace('-', '')
    jarName = jarFile.replace('.jar', '')
    csvFile = open(f'{jarName}_{xmx}.csv', 'a', newline='')
    # Setup File-Writer
    csvWriter = csv.writer(csvFile)

    # Start Message
    t = time.localtime()
    current_time = time.strftime("%H:%M:%S", t)
    print(f'|{current_time}| Starting: ' + f'java {jvmOptions} -jar {jarFile} {rows} {cols} {maxTime} {threshold} {seed}')

    try:
        processResult = subprocess.run(f'java {jvmOptions} -jar {jarFile} {rows} {cols} {maxTime} {threshold} {seed}',
                                       text=True,
                                       check=True,
                                       capture_output=True)
    except subprocess.CalledProcessError:
        t = time.localtime()
        current_time = time.strftime("%H:%M:%S", t)
        print(f'|{current_time}| Finished. Error!')

    t = time.localtime()
    current_time = time.strftime("%H:%M:%S", t)
    print(f'|{current_time}| Finished.')

    # Refine Output
    # outputByte = processResult
    outputStr = processResult.stdout
    outputSplit = outputStr.split(',')

    # Save Output
    csvWriter.writerow(outputSplit)
    print('Data written: ' + outputStr)
    print('------------------------------------------------------------------------')

    csvFile.close()
# END def runTest()

# JAR-File name
OOPc = 'GOL_OOP_Classic.jar'
OOPf = 'GOL_OOP_Flyweight.jar'
DOP = 'GOL_DOP.jar'

x8G = '-Xmx8G'

################################################################################
for i in range(50, 10101, 50):
    runTest(DOP, x8G, i, i, 60000, 500, 1)
    runTest(OOPf, x8G, i, i, 60000, 500, 1)