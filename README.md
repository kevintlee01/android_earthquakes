# android_earthquakes
Android Application which pulls web API data of earthquakes around the world and list them on a display. Each earthquake can be accessed and is plotted onto maps displaying the location of the earthquake.

2020-12-26
Developed with Android Studio 4.1.1
JSON API URL: http://api.geonames.org/earthquakesJSON?formatted=true&north=44.1&south=-9.9&east=-22.4&west=55.2&username=mkoppelman
Settings adjust following parameters in URL:
Username: <Replaces mkoppelman with another name>
North: <Takes Value of Double Format>
South: <Takes Value of Double Format>
East: <Takes Value of Double Format>
West: <Takes Value of Double Format>

Additional fields in settings:
Highlight: <Takes Value of Double Format> (Input of an earthquake magnitude; any magnitude equal or great will be highlighted in red when displayed in table format)

Notes:
- Each earthquake in list is tappable
- Tapping the earthquake will oepn a new screen showing a map with a pinned location of the earthquake. 
- Magnitude is visually reprented by size of circles.
