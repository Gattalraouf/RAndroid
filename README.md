# RAndroid
An Android Studio plugin for code smell refactoring. It is able to fix Android-specific code smells and/or recommends appropriate refactorings to resolve them.


<p align="center">
  <img width="460" height="300" alt="RAndroid logo" src="https://plugins.jetbrains.com/files/15058/97180/icon/pluginIcon.svg">
</p>

The plugin Supports the following code smells:

### Automatic refactoring:

* Init OnDraw (IOD)
* Heavy Start Service (HSS)
* Rigid AlarmManager (RAM)
* Inefficient Data Structure (IDS)

### Giving Recommendations:
* UI OverDraw (UIO)

RAndroid adapts the logic behind JDeodorant. It is the outcome of a research conducted as part of [Abderraouf Gattal](https://www.raouf.codes/) & [Abir Hammache](https://github.com/HammacheAbir)'s graduation project, in the "Laboratoire Méthodes de Conception de Systèmes (LMCS)" Lab 
@ "Ecole nationale Supérieure d’Informatique (ESI)" Algiers, Algeria.

**PS: Detection should be made manually by the developer using either [aDoctor](https://github.com/fpalomba/aDoctor) or [Paprika](https://github.com/GeoffreyHecht/paprika) & than the resulting files should be uploaded to the plugin.**


## Installation and execution
### Install from Jetbrains Plugin Repository
Follow these steps to install the plugin in Android Studio for production use:

* Open Android Studio
* Go into File>Settings...>Plugins>Marketplace
* Type "RAndroid"
* Install it
* Restart Android Studio
* Open the Android project you wish to analyze
* Go to Refactor>RAndroid 
* Choose the code smell you wish to handle
* Upload the detection file
* Click Refactor

The process will be handled automatically after that. You can find the infected classes open in the IDE opened window, so all you have to do is check the changes or the recommendations the plugin leaves in the shape of //TODO comments.

## Evaluation process

RAndroid were tested on over 56 open source application found in [F-Droid](https://www.f-droid.org/) & [GitHub](https://github.com/). The list of these Applications can be found [here](https://docs.google.com/document/d/1TQsHforI_P5tKcnO3R5ZsEMpgZHArVu_SHMzFEkfMaA/edit?usp=sharing).

## Credits
Thanks to Madame [Nabila Bousbia](https://www.linkedin.com/in/nabilabousbia/) and Mister [Adel Nassim Henniche](https://www.linkedin.com/in/adel-nassim-henniche-b775927b/) for their guidance through the entire process.

## Troubleshootings
RAndroid is still in development.

Found a bug? We'd love to know about it!

Please report all issues on the github issue tracker.
