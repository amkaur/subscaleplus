# -*- coding: utf-8 -*-
"""
@author: akaur
"""

import sys
import timeit
import os

dataFile=sys.argv[1]
EPSI=sys.argv[2]
TAU=sys.argv[3]
SPFACTOR=sys.argv[4]

with open('metadata.txt', 'w') as metaOUTfile:        
    metaOUTfile.write(dataFile+"\n")
    metaOUTfile.write(EPSI+"\n")
    metaOUTfile.write(TAU+"\n")
    metaOUTfile.write(SPFACTOR+"\n")
    
start_time = timeit.default_timer()

cmd='java subscaleplusjava.DenseNeighbours '+dataFile+' '+EPSI+' '+TAU+' '+SPFACTOR
os.system(cmd)

if not os.path.exists("clusters"):
    os.makedirs("clusters")
for SLICENUM in range(1,int(SPFACTOR)):
    cmd='java subscaleplusjava.SubscalePlus '+str(SLICENUM)+' '+TAU    
    os.system(cmd)

cmd='java subscaleplusjava.MergeDensePoints'
os.system(cmd)

end_time = timeit.default_timer()
totalruntime=end_time-start_time 

with open('metadata.txt', 'a') as metaOUTfile:        
    metaOUTfile.write(str(totalruntime)+"\n")   
