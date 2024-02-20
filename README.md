# ChatUniTest IntelliJ IDEA plugin

Many people have tried using ChatGPT to help them with various programming tasks and have achieved good results.
However, there are some issues with using ChatGPT directly. Firstly, the generated code often fails to execute
correctly, leading to the famous saying "five minutes to code, two hours to debug". Secondly, it is inconvenient to
integrate with existing projects as it requires manual interaction with ChatGPT and switching between different
platforms. To address these problems, we have proposed the "Generate-Validate-Repair" framework and implemented a
prototype. Additionally, to make it easier for everyone to use, we have developed an IntelliJ IDEA plugin that can be 
seamlessly integrated into existing development workflows.

The plugin is compatible with **IntelliJ IDEA 2022.1-2023.3** and can be built from sources.

## Use ChatUniTest
We have released the Chatunitest plugin in the IntelliJ IDEA plugin Marketplace,you can search and install ChatUniTest 
in Marketplace or visit plugin page [Chatunitest:IntelliJ IDEA Plugin](https://plugins.jetbrains.com/plugin/22522-chatunitest)
to know more about our plugin.

### Plugin Configuration
You can configure `ChatUniTest Configuration` for unit test generation
in the config panel, which can be found in IntelliJ idea panel `Tools-> ChatUniTest-> Configuration`

### Unit test generation
#### 1. Add the following dependency to `pom.xml` in your target project. 
Maven project
```xml
<dependency>
    <groupId>io.github.ZJU-ACES-ISE</groupId>
    <artifactId>chatunitest-starter</artifactId>
    <version>1.4.0</version>
    <type>pom</type>
</dependency>
``` 
Gradle project
```kts
dependencies {
    implementation 'io.github.ZJU-ACES-ISE:chatunitest-starter:1.4.0'
}
```

#### 2.ChatUniTest can generate unit tests in a variety of ways, depending on your needs:generate unit tests for your **Maven project**, **Java class**, and **Java method**.
1. You can place your mouse over the project root directory and click the right mouse button ,click `ChatUniTest generate tests`. The plugin will generate unit tests for your **entire project**
2. You can place your mouse over the module and click the right mouse button ,click `ChatUniTest generate tests`. The plugin will generate unit tests for the selected **module**
3. You can place your mouse over the `.java` file in `/src/main/java` directory and click the right mouse button ,click `ChatUniTest generate tests`. The plugin will generate unit tests for your **selected Java class** (Excluded getter, setter, constructor, and private methods)
4. You can place your mouse over the `method` in your `Java class` and click the right mouse button ,click `ChatUniTest generate tests`. The plugin will generate unit tests for your **selected Java method**

## Source Code
### Installation

At first, clone this repository and open the root folder.

**Build the plugin from sources and go:**

- Run `./gradlew :plugin:buildPlugin`
- Check out `./plugin/build/distributions/plugin-*.zip`
- Install the plugin in your **IntelliJ IDEA** via `File` - `Settings` - `Plugins`
    - `Install Plugin from Disk...`

**Quick IDE launch for evaluation:**
- Run `./gradlew :plugin:runIde`



