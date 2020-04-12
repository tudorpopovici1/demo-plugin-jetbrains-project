# Plugins for Intellij

## Running the plugin for the first time

Make sure the Gradle plugin is installed in your IDE, go to `File -> Open`, select the `build.gradle` file
and choose `Open as Project`. 

If you already imported the project when it was not based on Gradle, then choose the option to delete the existing 
project and reimport it.

Once the IDE is done downloading dependencies and refreshing the project, you can use the `Gradle` tool window
and use the following `Tasks`:
* `build > assemble` to build the project
* `intellij > runIde` to run the plugin in a sandboxed instance<br/><br/>
![Image of runIde](images/image5.png)

## Methods Plugin
Go to tools menu
Click on `Markdown Files Report`<br/><br/>
![Image of Tools Menu](images/image1.png)<br/>
- In run tool window click on `MD Files`, this shows the all markdown files in the project. Under each markdown file you can find the **URL links** and **References** that it has<br/><br/>
![Image of MD Files](images/image4.png)
- Click on `MD Statistics`, this shows the statistics for markdown files<br/><br/>
![Image of MD Statistics](images/image3.png)


## Markdown Files Plugin
Go to tools menu
Click on `Summary Report`<br/><br/>
![Image of Tools Menu](images/image1.png)<br/>

- In run tool window click on `Statistics`, this shows the statistics for the current open java file<br/><br/>
![Image of Statistics](images/image2.png)
