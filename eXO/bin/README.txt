===========================================

	eXO simulator 

===========================================

eXO : Decentralized Social Networking 
with Autonomy, Scalability, and Efficiency
-------------------------------------------

eXO is a completely decentralized, scalable,
social networking system which contains :

(i) techniques for content indexing, 

(ii) topk algorithms for answering user search-for-content queries,

(iii) appropriate similarity functions, which help identify content
relevant to queries,

(iv) algorithms for efficient distributed content retrieval,

(v) techniques to exploit tag information for enriching user query
experiences. 

We have implemented eXO over the FreePastry (FP) DHT.
Currently we have built a beta version bundled with FP 2.04.

This .tgz file includes the Sim.jar jar archive and a set of folders.
The lib/ folder contains all the necessary libraries which are used
by the FreePastry and the eXO system. The jarrs/freepastry/ folder
contains the parameter file of FP. The datasets/ folder contains some
tests constructed by the datasets we used in our experiments. The full
testing files are only sent after communication with us.

* Homepage: http://netcins.ceid.upatras.gr/software.php

Quick start
-------------------------------------------

In order to use the eXO's simulator :

1) Unzip eXO_sim : 

	tar -xzvf eXO_sim.tgz

2) Install sun java runtime environment and extract tool. 

In Ubuntu/Debian issue :

	sudo apt-get install sun-java6-jre extract

3) In the eXO_sim folder run (we silently supposed that 
				java command is in your PATH):

	java -cp Sim.jar ceid.netcins.simulator.SimMain -nodes 100

-nodes <number> : this switch controls the number of nodes in the pastry network.
-test <relative path> : the test we want to run.

Examples	
-------------------------------------------

Examples of simulator usage:

1) java -cp Sim.jar ceid.netcins.simulator.SimMain -nodes 500

The above command begins simulator in command line mode. Type
"help" in command prompt to see a full list with the supported
commands. Usually, the first command which must be issued is 
the "createnodes" command. "createnodes" causes the network
initialization. After this phase, some indexing must be done
in order to fill the network with some indexed data to be 
searched. The indexing is implemented with the commands 
"index*". When the indexing has been done we can search for
the indexed objects with "search*" commands.

In order to use some predefined scenarios for simulation,
the "importtest" command can be used. Some testing scenarios
are included in datasets/ folder.

IMPORTANT: The -nodes value should be the same with the amount of
indexing users (e.g. in a testing scenario). So for example if
a dataset with 500 indexed users is used then the network must have 
exactly 500 nodes!

2) java -cp Sim.jar ceid.netcins.simulator.SimMain -nodes 500 -test datasets/users_500_new.xml > test.log &
   tail -f test.log
	     
The above example runs a test included in the users_500_new.xml
file. The test is run in the background and redirects all the 
output in a file called "test.log". In order to watch the output
in realtime a "tail -f test.log" command can be issued.


Documentation
-------------------------------------------

Currently there is not a complete documentation this is on our TODOs list!


Communucation
-------------------------------------------

For questions or feedback feel free to 
communicate with me at : 
		
	loupasak __AT__ ceid.upatras.gr 

where __AT__ = @


PS : All Datasets are available only upon request through email
