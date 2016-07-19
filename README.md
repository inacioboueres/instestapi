Instestapi
================================

Requirements
------------
* [Java Platform (JDK) 7](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Apache Maven 3.x](http://maven.apache.org/)

Quick start
-----------
1. 'mvn clean package'
2. 'mvn spring-boot:run' 

Description
-----------
This Application will download all the media from a Instagram user and keep it on memory (Data Base was not used because I decided to keep it simple) for future request.
It starts loading Justin Bieber user informations but you can request any user you want.
I decided to use the API to retrieve the information but as it change, the unregistered users are unable to get public information about other users besides himself, and to get this kind of permission you application must feet on a very restrict kind of applications type that mine dosen't fits, so I decided to use "https://www.instagram.com/user/media" that somehow return the user information plus media as a Json, but it only returns 20 medias per request, so was implemented a task to keep searching for more results until the end and TimeTask is running ever 30s to check for new updates following the same rules of the download.
It was implemented a small UI on Angular that will start or stop a task, show information about a selected user, and check every 15 seconds if the data changed.     
