import csv
import subprocess
import sys
import time
import psutil
import os

def findMaxCells(jarFile, jvmOptions, maxTime, timeOut, threshold, seed, lower, upper):
    # Save Start lower, upper
    s_lower = lower
    s_upper = upper

    # Documentation File
    xmx = jvmOptions.replace('-', '')
    jarName = jarFile.replace('.jar', '')
    dokFile = open(f'maxCells_Results/maxCells_{jarName}_{xmx}_dok.txt', 'a', newline='')
    csvFile = open(f'maxCells_Results/maxCells_{jarName}_{xmx}.csv', 'a', newline='')

    # Setup File-Writer
    csvWriter = csv.writer(csvFile)

    # Description Header
    t = time.localtime()
    current_time = time.strftime("%H:%M:%S", t)
    dokFile.write('-----------------------------------------------------------------------------\n')
    dokFile.write('-----------------------------------------------------------------------------\n')
    dokFile.write('|'+ current_time + f'| Begin Test for {jarFile} with {jvmOptions}\n')
    dokFile.write(f'maxTime = {maxTime}ms , timeOut = {timeOut}sec\n')
    dokFile.write(f'lower = {lower} , upper = {upper}\n')
    dokFile.write('-----------------------------------------------------------------------------\n')
    dokFile.write('-----------------------------------------------------------------------------\n')
    print('-----------------------------------------------------------------------------')
    print('-----------------------------------------------------------------------------')
    print('|'+ current_time + f'| Begin Test for {jarFile} with {jvmOptions}')
    print(f'maxTime = {maxTime}ms , timeOut = {timeOut}sec')
    print(f'lower = {lower} , upper = {upper}')
    print('-----------------------------------------------------------------------------')
    print('-----------------------------------------------------------------------------')

    while (True):
        # Algorithm Logic
        mid = int((upper - lower) / 2) + lower
        if (mid <= lower):
            break

        # Documentation Start Messages
        t = time.localtime()
        current_time = time.strftime("%H:%M:%S", t)
        dokFile.write(f'lower: {lower}   upper: {upper}   mid: {mid}\n')
        dokFile.write('|'+ current_time + f'| Starting: java {jvmOptions} -jar {jarFile} {mid} {mid} {maxTime} {threshold} {seed}\n')
        print(f'lower: {lower}   upper: {upper}   mid: {mid}')
        print('|'+ current_time + f'| Starting: java {jvmOptions} -jar {jarFile} {mid} {mid} {maxTime} {threshold} {seed}')

        # Run Java Code
        try:
            p1 = subprocess.run(f'java {jvmOptions} -jar {jarFile} {mid} {mid} {maxTime} {threshold} {seed}',
                                           text=True,
                                           check=True,
                                           timeout=timeOut,
                                           capture_output=False)

        except subprocess.TimeoutExpired:
            # Force Terminate on java.exe
            for process in psutil.process_iter():
                if process.name() == 'java.exe':
                    os.system('taskkill /F /im java.exe')
            # Documentation
            t = time.localtime()
            current_time = time.strftime("%H:%M:%S", t)
            dokFile.write('|'+ current_time + '| Failure: Timed-out\n')
            dokFile.write(f'set upper to {mid}.\n')
            dokFile.write('------------------------------------------------------------------------\n')
            print('|'+ current_time + '| Failure: Timed-out')
            print(f'set upper to {mid}.')
            print('------------------------------------------------------------------------')

            # Algoritm Logic for timed out Run
            upper = mid
            continue

        except subprocess.CalledProcessError:
            # Documentation
            t = time.localtime()
            current_time = time.strftime("%H:%M:%S", t)
            dokFile.write('|'+ current_time + '| Failure: Error\n')
            dokFile.write(f'set upper to {mid}.\n')
            dokFile.write('------------------------------------------------------------------------\n')
            print('|'+ current_time + '| Failure: Error')
            print(f'set upper to {mid}.')
            print('------------------------------------------------------------------------')

            # Algorithm Logic for failed Run
            upper = mid
            continue

        # Re-Run to validate Output
        t = time.localtime()
        current_time = time.strftime("%H:%M:%S", t)
        dokFile.write('|' + current_time + f'| Re-Run to gather Output.\n')
        print('\n|' + current_time + f'| Re-Run to gather Output.')
        try:
            p2 = subprocess.run(f'java {jvmOptions} -jar {jarFile} {mid} {mid} {maxTime} {threshold} {seed}',
                                text=True,
                                check=True,
                                capture_output=True)

        except subprocess.CalledProcessError:
            # Documentation
            t = time.localtime()
            current_time = time.strftime("%H:%M:%S", t)
            dokFile.write('|'+ current_time + '| Re-Run Failure: Error\n')
            dokFile.write(f'set upper to {mid}.\n')
            dokFile.write('------------------------------------------------------------------------\n')
            print('|'+ current_time + '| Re-Run Failure: Error')
            print(f'set upper to {mid}.')
            print('------------------------------------------------------------------------')

            # Algorithm Logic for failed Run
            upper = mid
            continue

        # Refine Output
        outputStr = p2.stdout
        outputSplit = outputStr.split(',')

        # Check Output: Empty?
        if (outputStr == ''):
            # Documentation Messages
            t = time.localtime()
            current_time = time.strftime("%H:%M:%S", t)
            dokFile.write('|' + current_time + f'| Data is empty. Invalid Run !\n')
            dokFile.write(f'set upper to {mid}.\n')
            dokFile.write('------------------------------------------------------------------------\n')
            print('|' + current_time + f'| Data is empty. Invalid Run !')
            print(f'set upper to {mid}.')
            print('------------------------------------------------------------------------')

            # Algorithm Logic for Succesful Fail... Empty Output
            upper = mid
            continue

        # Algorithm Logic for Succesful Run
        lower = mid

        # Save Output in csv File
        csvWriter.writerow(outputSplit)

        # Documentation Messages
        t = time.localtime()
        current_time = time.strftime("%H:%M:%S", t)
        dokFile.write('|'+ current_time + f'| Data written: {outputStr}\n')
        dokFile.write(f'set lower to {mid}.\n')
        dokFile.write('------------------------------------------------------------------------\n')
        print('|'+ current_time + f'| Data written: {outputStr}')
        print(f'set lower to {mid}.')
        print('------------------------------------------------------------------------')
    # END While

    t = time.localtime()
    current_time = time.strftime("%H:%M:%S", t)
    dokFile.write(f'End-Values | lower: {lower}   upper: {upper}   mid: {mid}\n')
    dokFile.write('-----------------------------------------------------------------------------\n')
    dokFile.write(f'Number of possible Rows and Columns: {mid}\n')
    dokFile.write('-----------------------------------------------------------------------------\n')
    dokFile.write('|'+ current_time + f'| Completed Test for {jarFile} with {jvmOptions}\n')
    dokFile.write(f'maxTime = {maxTime}ms , timeOut = {timeOut}sec\n')
    dokFile.write(f'lower: {s_lower}   upper: {s_upper}\n')
    dokFile.write('-----------------------------------------------------------------------------\n')
    dokFile.write('-----------------------------------------------------------------------------\n')
    print(f'End-Values | lower: {lower}   upper: {upper}   mid: {mid}')
    print('-----------------------------------------------------------------------------')
    print(f'Number of possible Rows and Columns: {mid}')
    print('-----------------------------------------------------------------------------')
    print('|'+ current_time + f'| Completed Test for {jarFile} with {jvmOptions}')
    print(f'maxTime = {maxTime}ms , timeOut = {timeOut}sec')
    print(f'lower: {s_lower}   upper: {s_upper}')
    print('-----------------------------------------------------------------------------')
    print('-----------------------------------------------------------------------------')
    # Close Files
    dokFile.close()
    csvFile.close()

# JAR-File names
jarOOPc = 'GOL_OOP_Classic.jar'
jarOOPf = 'GOL_OOP_Flyweight.jar'
jarDOP = 'GOL_DOP.jar'

# JVM Options
x25M = '-Xmx25M'
x50M = '-Xmx50M'
x100M = '-Xmx100M'
x200M = '-Xmx200M'
x300M = '-Xmx300M'
x400M = '-Xmx400M'
x500M = '-Xmx500M'
x1G = '-Xmx1G'
x2G = '-Xmx2G'
x3G = '-Xmx3G'
x4G = '-Xmx4G'
x5G = '-Xmx5G'
x6G = '-Xmx6G'
x7G = '-Xmx7G'
x8G = '-Xmx8G'

# OOP_Classic
#findMaxCells(jarOOPc, x50M, 300000, 600, 0, 1, 3, 500)

# OOP_Flyweight
#findMaxCells(jarOOPf, x25M, 1000, 5, 0, 1, 3, 5000)
#findMaxCells(jarOOPf, x50M, 2000, 10, 0, 1, 3, 5000)
#findMaxCells(jarOOPf, x1G, 10000, 20, 0, 1, 3, 10000) #3571 3388
#findMaxCells(jarOOPf, x2G, 15000, 30, 0, 1, 3571, 10000) #5052 7582
#findMaxCells(jarOOPf, x3G, 25000, 50, 0, 1, 5052, 10000) #6188 12128
#findMaxCells(jarOOPf, x4G, 35000, 70, 0, 1, 6188, 10000) #7147 14355
#findMaxCells(jarOOPf, x5G, 45000, 90, 0, 1, 7147, 10000) #7990 16154
#findMaxCells(jarOOPf, x6G, 55000, 110, 0, 1, 7990, 10000) #8753 22745
#findMaxCells(jarOOPf, x7G, 70000, 140, 0, 1, 8753, 11000) #9454 27663
#findMaxCells(jarOOPf, x8G, 85000, 170, 0, 1, 9454, 12000) #10107 29821

# DOP
#findMaxCells(jarDOP, x25M, 1000, 5, 0, 1, 3, 5000) #2937
#findMaxCells(jarDOP, x50M, 1000, 5, 0, 1, 3, 5000) #4097 193
#findMaxCells(jarDOP, x1G, 5000, 10, 0, 1, 3, 20000) #18741 3491
#findMaxCells(jarDOP, x2G, 10000, 20, 0, 1, 18741, 40000) #26599 6982
#findMaxCells(jarDOP, x3G, 20000, 40, 0, 1, 26599, 50000) #32735 10534
#findMaxCells(jarDOP, x4G, 25000, 50, 0, 1, 32735, 50000) #37519 13807
#findMaxCells(jarDOP, x5G, 30000, 60, 0, 1, 37519, 50000) #42198 17481
#findMaxCells(jarDOP, x6G, 35000, 70, 0, 1, 42198, 60000) #46072 20195
#findMaxCells(jarDOP, x7G, 40000, 80, 0, 1, 46072, 60000) #49912 23768
#findMaxCells(jarDOP, x8G, 45000, 90, 0, 1, 49912, 60000) #53214 27122