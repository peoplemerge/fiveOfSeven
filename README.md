Five Of Seven Application
=========================

Wouldn't it be dreamy if you could tell how engaged your users are on your site / app / system?

Well now you can.  Simply drop the pixel at http://service/px on your site, which will drop a cookie
then will push via asynchronous SCALA / Play / Akka to Streaming Spark.

Then the simple calculation (to be implemented) from the time series will work as follows:

* Derive a RRD per User with their activity
* Report the Users' Activity following the Five of Seven rule.  Track the % of users who have any activity in 5 of the last 7 days
