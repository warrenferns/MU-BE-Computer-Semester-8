# Suzuki Kasami Algorithm #



This program uses Java scoket programming and threads to execute. This program will implement Suzuki Kasami algorithm over the network or on a local computer using different ports. The assumption here is that all ip's and ports are accessible to each node and no firewall or network partition issue occurs.

## How to give input? ##

1. The program requires a file called as **nodes.config** which contain the site number, its ip address and it's port number.
2. The file should be kept at the same level from which we are running the program (jar)
3. The assumption here is that all sites id's would be defined sequentially, in a continous manner starting from 1
4. Each line would contain only one site infomration

Format of config files

```<site_id> <Ip_of_site> <port>```

Eg:<br/>
1 127.0.0.1 3450<br/>
2 127.0.0.1 3451<br/>
3 127.0.0.1 3452<br/>
4 127.0.0.1 3453<br/>

You can add or subtract sites from the list but the sequence numbers should be continous and no gap in numbering of site id should be there.

## How to run the program? ##

This program is written in Java 1.8 (build 1.8.0_172-b11). So you would need the same JDK version as this is not tested against other JVM versions

To start the program, simply type [0] from the location where the jar and config file is placed. Both the jar and config file should be placed at the same level.

[0] ```java -jar suzuki_kasami.jar```

Once the program starts, it will ask the site number like[1]:

[1] ```Enter site number (1-4):```

Enter the site  number. Once entered. It will give the prompt [2]

[2] ```Press ENTER to enter CS:```

Before pressing ENTER please ensure all sites are initialized (by running jar on each computer or prompt (eg: 4 cmd prompts for 4 sites on the same computer)). This program assumes that all sites are up and running all the time. If you get any exception while initializing the program, simply quit the program and restart it. You will need to restart all nodes to reset LN.

Once all sites are up, press ENTER on any site and the algorthm will work. On initialization, site 1 will hold the token.

The program uses threads to handle incoming requests, synchronzing LN and sending token. So, on the terminal you would see all the logs printed like 

```

Press ENTER to enter CS:
request,2,1

Requesting token
Broadcasting request to 3 sites.
3450
3451
3453
Waiting for token..
request,4,1
ln,1,0
request,1,1
ln,2,1
token,4,1
Site has recieved token. Executing CS.
Exiting CS.
Sending token to site 4
Press ENTER to enter CS:
```

## Quiting the program: ##
To quit the program, simply press Ctrl+C


## Compiling the program: ##

**Step1:** Place suzuki_kasami.java and nodes.config in a folder at the same level

**Step2:** compile the code using: javac suzuki_kasami.java

**Step3:**** Run the code using: java suzuki_kasami



