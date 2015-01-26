# -*- coding: utf-8 -*-
"""
@author: akaur
"""

import csv
import os
import sys
import numpy as np
import datetime
import fnmatch
import timeit
from sklearn.cluster import DBSCAN
from decimal import Decimal

start_time = datetime.datetime.now()

#dbscan_epsi=Decimal(sys.argv[1])
#minSize=int(sys.argv[2])

dbscan_epsi=0.02
minSize=4

with open("metadata.txt") as metaINfile:
    dataFile=(metaINfile.readline()).split('\n', 1)[0]
    print(dataFile)

folder="clusters"
start_time = timeit.default_timer()
allfiles=fnmatch.filter(os.listdir(folder), '*[!~]')

for subspaceFile in allfiles:
    print(subspaceFile)    
    with open(folder+os.sep+subspaceFile) as csvINfile:
        inFile=csv.reader(csvINfile,quoting=csv.QUOTE_NONE,delimiter='-')
     
        #Prepare input data matrix for dbscan (for each maximal subspace found by SubscalePlus.java)
        with open(dataFile,"rU") as csvDATAfile:  
            datacsv=csv.reader(csvDATAfile,quoting=csv.QUOTE_NONE,delimiter=',')           
            datacsv=list(datacsv)
            del datacsv[0]
            Allclusters=[]        
            for row in inFile: #for each found subspace in maximaloutput.csv
                DBcluster=[]   
                SSdata=[]                
                dim=map(int,row[0][1:-1].split(","))                
                DBcluster.append(dim)       
                points=map(int,row[1][1:-1].split(","))       
                for count,p in enumerate(points):
                    SSdata.append([])                
                    for d in dim:
                        SSdata[count].append(datacsv[p][d])                     
                X = np.array(SSdata) 
                
                #Run Scikit-learn DBSCAN on each subspace
                db=DBSCAN(eps=dbscan_epsi, min_samples=minSize).fit(X)      
                labels = db.labels_               
                foundCluster=False
                for k in set(labels):                
                    if k == -1:                    
                        continue
                    else:
                        clusters = [points[index[0]] for index in np.argwhere(labels == k)]  
                        DBcluster.append(clusters) 
                        foundCluster=True
                if(foundCluster):
                    Allclusters.append(DBcluster)   
        if(Allclusters):
            outFile='DB'+subspaceFile        
            with open(folder+os.sep+outFile, 'w') as csvOUTfile:
                output=csv.writer(csvOUTfile)
                output.writerows(Allclusters) 


end_time = timeit.default_timer()
DBruntime=end_time-start_time 
with open('metadata.txt', 'a') as metaOUTfile:        
    metaOUTfile.write(str(DBruntime)+"\n")