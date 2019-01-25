## The problem
Have you ever run out of bikes while you arrived into a sharing bike system station? Or even worse, not having a site to park it when you arrive at your destination? That is a pretty common situation and some people who use this kind of service are getting upset.

Nowadays, bike systems applications are just informative, they don't take advantage of the information provided by the public API's where the developers could get so much information to satisfy the real needs of the users.


## What it does

**Select an Station as your starting point or destination**

The first step is to know where do you want to go. When using the service eventually, you should pick one station in the map to know if it is going to be the starting point (you want to know if there will be bikes available) or the destination (you want to track if there will be a free slot to park the bike).

**Receive alerts if you run out of bikes or free slots in your way to the station**

You don't have to worry about checking constantly the status of the station, we will be tracking it and notify you if you run out of bikes or slots, depending on your choice.

The notification can be handled by Android smartphones and smartwatches (android wearables and pebble)

**Be notified with the closest station that best suits your needs**

Receive in real time an alternative station to go where you will be able to park or pick a bike.

**Create recurrent alarms if you are a daily user of the service**

If you use the service everyday at one determined hour, that is the best alternative for you. Program live notification of any station the time you need it.

## How we built it

There is an android application that is used as a middleware between a server and a posible smartwatch. All the data the application needs to work is provided by our own server. When the status of a station changes, if it is necessary it is notified by push notifications as well as the users that are tracking this station.


## Why Smartwatch?

When you are on a bike, you can't really use a big smartphone screen, so we though that smartwatches are the best option as they can be attached to a bike's handlebar.

## Accomplishments that we're proud of

We are a team of three and we developed the whole stack in 20 hours! 

## What we learned
- Google Maps on Android Wear
- Push notifications
- Pebble programming
