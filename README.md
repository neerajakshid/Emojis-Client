# Emojis-Client
Emojis Client is an android app that allows a user to view all the Emojis displayes, enables users to pick an emoji and search for an emoji. The app utilizes Github API (https://api.github.com/emojis).

<b>User Stories:</b>

The following functionality are implemented in the app:

 * User can view all the emojis from the Github API in a GridView
 * User can select an emoji from the GridView and the corresponding emoji is selected, the name and image of the selected emoji    are displayed (by default first emoji is displayed)
 * The emojis are stored in SQLite Database (through ActiveAndroid ORM) for persistance. In case of network failure, emojis are    displayed from SQLite Database
 * User can search for an emoji entering the name of the emoji
 * User can infinitely paginate through the list of emojis by scrolling to the bottom of the grid
 
<b>Open-source libraries used</b>

* Volley Plus
* Picasso
* Active Android ORM 
