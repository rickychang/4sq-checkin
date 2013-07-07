4sq-checkin
==============

Simple Scala command-line app for checking in to venues on foursquare.  Currently, it requires you to look up a venue id and have generated an OAuth token for the app manually.

Building
---------
`sbt assembly`

Running
---------
`java -jar target/scala-2.10/4sq-checkin-assembly-0.1-SNAPSHOT.jar <venue_id> <oauth_token> <lat> <long>`


