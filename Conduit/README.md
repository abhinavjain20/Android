<h1 align="center">
	<img
		width="600"
		src="https://cloud.githubusercontent.com/assets/556934/25672246/9a20e960-2fe7-11e7-99d3-23652878a2c2.png?raw=true">
</h1>

### Demonstrate a Full Stack Android Application built with Kotlin including CRUD operations, authentication and more.<br/>
### A Medium.com clone (called Conduit) is built using Kotlin to connect to other backends from [ https://realworld.io/]<br/>
### For More Information on how this works go to [RealWorld](https://github.com/gothinkster/realworld) repo.<br/>
## Development 
The Project is developed in [Android Studio](https://developer.android.com/studio).<br/>
## Concepts 
The Conduit App tries to show the following Android Concepts. 
* MVVM (Model View- View Model) architecture
* Live Data
* Coroutines
* Jetpack Navigation Components<br/>
## Architecture 
The Project follows general MVVM architecture without any specifics. 
There are two modules in the project 
* app - The UI of the app. The main project that forms the APK
* api - The REST API consumption library. Pure JVM library not Android-specific<br/>
## Testing 
The Project manually test against.
* Emulator
  * Pixel 4 API 30 
* Devices 
  * Moto G5 S plus Android 8.1.0<br/>
## Automated tests
The project contains an example e2e test to illustrate an end-to-end test case.<br/>
## License and Credits
Credits have to go out to Thinkster with their awesome RealWorld<br/>
This project is licensed under the MIT license.