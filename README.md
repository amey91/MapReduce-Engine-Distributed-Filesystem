MapReduce-Engine-Distributed-Filesystem
=======================================

All rights reserved by Amey Ghadigaonkar

This project implements a Map-Reduce Facility similar to Hadoop, built on top of my own virtual file system. 
It has certain design constraints aimed at enabling it to work more efficiently on the Andrew FileSystem with smaller 
data sets. I have constructed a facility capable of dispatching parallel maps and reduces across multiple 
hosts, as well as recover from worker failure. 
