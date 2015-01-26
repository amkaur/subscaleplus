					======
					README
					======
			Programs for the Scalable version of SUBSCALE Algorithm

==================================================


1. Using the code
------------------


		----------- Step 1 -----------

The folder 'subscaleplus' contains relevant java code to run first part of the SUBSCALE algorithm. 

The python script file 'subscaleplus.py' will run the necessary java code. Also, appropriate size of RAM can be allocated for the code execution using -Xmx option. This script takes four parameters as input: 
1. datafile (csv format)
2. epsilon (double)
3. tau (integer)
4. split factor (integer)

The program outputs different files according to the dimensionality of the maximal subspaces in it. All the result files are stored in 'clusters' folder.
Another text file called 'metadata.txt' contains the parametrs used alongwith the execution time '[subspace]-[Dense points]'.

		
Example: python subscaleplus.py datafile.csv 0.02 3 2

		
		----------- Step 2 -----------

The python script 'refine.py' outputs the final maximal subspace clusters from the maximal subspace dense points generated and distributed across files in 'clusters' folder. DBSCAN from scikit-learn is applied on each found subspace and it takes two parameters:
1. epsilon (double) - The script can be changed to adapt epsilon values according to the dimensionality of the subspaces. 
2. minSize (integer) which is the minimum allowed size for a cluster. 

The final results are again distributed to the corresponding files in the 'cluster' folder.

Example: python refine.py 0.02 4



2. Data
--------

The program uses normalised data between 0 and 1 in csv file format.

