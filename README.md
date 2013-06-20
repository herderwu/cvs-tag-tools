cvs-tag-tools
=============

cvs tag tools by java
---------------------
created by jerome tan, now I update and maintain

a GUI tools to tag multi files  
by give a txt file, or copy file path at text area.

require
------------------------
* JDK 1.7  
* org-netbeans-lib-cvsclient.jar


config.properties readme
------------------------
make a file with name of *config.properties* at root folder

1. local cvs folder  
LOCALROOT=c:\\client

2. cvs string  
CVSSTRING=:pserver:username:pwd@{cvs server}:/CVSRPT

3. code folder  
SOURCEFOLDER=sourcecode

4. pre define Modules  
MODULES=Investopedia

5. tag files folder  
RELEASE_FILE_FOLDER=doc\\release

6. tag file extend  
RELEASE_FILE_PREFIX=.txt