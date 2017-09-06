# TyBoard
Software for an ongoing smart mirror project. App combines information from [OpenWeatherMap API](https://openweathermap.org/) and [Google Directions API](https://developers.google.com/maps/documentation/directions/) to give a brief overview of the current weather and traffic situation. Updates every morning at 7.30AM local time.

## Inspiration
This app was created to answer some of my wifes most frequent questions in the morning ;) - I drew inspiration from Max Braun's post on [Medium](https://medium.com/@maxbraun/my-bathroom-mirror-is-smarter-than-yours-94b21c6671ba) and Becky Stern's take on an [Android based smart-mirror](http://lifehacker.com/build-your-own-smart-mirror-with-a-two-way-mirror-and-1739447316). 

## Application Layout
The app holds a very basic constraint layout. 

## Future work
- A graph to show past traffic values per date is in the making. The design is implemented already it's just a matter of populating it and storing the accumulated information persistently.
- Local Time Zone has to be changed manually for automatic updates
- News alerts
- Implementation of speech recognition and speech control API to change settings

## Requirements
- Needs OpenWeatherMap, Google Geo and Google Directions API Key
- Min SDK 15, Target SDK 25

## Screenshots
This is how the app looks currently.
<div>
  <img src="https://github.com/andirs/TyBoard/blob/master/doc/img/01main.png" alt="Main Page" width="200">
  <img src="https://github.com/andirs/TyBoard/blob/master/doc/img/02settings.png" alt="Settings" width="200">
  <img src="https://github.com/andirs/TyBoard/blob/master/doc/img/03settings_autofill.png" alt="Settings Page Autofill" width="200">
</div>
