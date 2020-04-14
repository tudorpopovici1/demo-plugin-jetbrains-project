# Plugins for Intellij IDEA

A toy plugin for IntelliJ IDEA created by group of TU Delft students.

## Table of Contents
* [Running the plugin for the first time](#running-the-plugin-for-the-first-time)
* [Markdown Files Plugin](#markdown-files-plugin)
* [Methods Plugin](#methods-plugin)
* [Contributors](#contributors)

## Running the plugin for the first time

* Use following steps to run the plugin: 

  * Make sure the Gradle is installed in your computer
  * Then, install plugin repository to your computer
  * Open your 'Terminal' and go to the directory you installed the plugin
  * Then, run **"gradle runIde"**
  * Plugin is ready to use!

## Markdown Files Plugin
In order to activate markdown files plugin go to tools menu
click on `Markdown Files Report` or optionally use `Ctrl+Alt+F` shortcut <br/><br/>
![Image of Tools Menu](images/image1.png)<br/>
- In run tool window when you click on `MD Files`, it shows the all markdown files in the project. Under each markdown file you can find the **URL links** and **References** that it has.<br/><br/>
![Image of MD Files](images/image4.png)
- In run tool window when you click on `MD Statistics - metrics/project`, it shows the statistics for markdown files<br/><br/>
![Image of MD Statistics](images/image3.png)
- When you change the content of a markdown file, delete a markdown file or add a new markdown file, all statistics are dynamically updating. So you always see up to date statistics.

## Methods Plugin
In order to activate methods plugin go to tools menu
click on `Summary Report` or optionally use `Ctrl+Alt+G` shortcut <br/><br/>
![Image of Tools Menu](images/image1.png)<br/>

- In run tool window when you click on `Statistics`, it shows the statistics for the current open java file. 
- If you click to another Java file or change the content of the file, it refreshes the statistics in the table. It starts to display the renewed statistics of the current file.<br/><br/>
![Image of Statistics](images/image2.png)

## Contributors
* [Tudor Popovici](https://github.com/tudorpopovici1)
* [Tommaso Brandirali](https://github.com/TommasoBrandirali)
* [Ceren Ugurlu](https://github.com/cugurlu)
* [Irem Ugurlu](https://github.com/iremugurlu)
