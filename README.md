# Java Format Binding

The **Java Format Binding** provides an API that allows the mapping between text messages with fixed length fields and Plain Old Java Objects (POJOs).

The **Java Format Binding** aims to facilitate the conversion of any type of text message with fixed length fields into a Java object or vice versa. Below in this document we will see through some examples how to carry out this type of conversion.

## Getting started

At the moment the API is not yet available for download on the [Maven Central Repository](https://mvnrepository.com/). So to see the **Java Format Binding** in action you can just clone this repository and make a build in your local environment.

### Prerequisite

To build the **Java Format Binding** project, you need to install the following software:

 - **OpenJDK 17 64-bit** or latest
 - (Optional) **Gradle**: It is not required to install Gradle since the project contains a Gradle Wrapper, which is a batch/shell script that bootstraps/downloads Gradle in the required version by itself. As long as internet access is available, a call to `./gradlew` or `gradle.bat` will start this bootstrap.

### First build

 1. Open GitBash and go to the directory where you want to clone this repository
 2. Clone this repository with the following command:
```git
$ git clone git@github.com:jformatb/jformatb.git
```

 3. After cloning this repository the file system will look like this:

```md
jformatb
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── jformatb-api
│   ├── src
│   │   └── main
│   │       └── java
│   └── build.gradle
├── jformatb-bom
│   └── build.gradle
├── jformatb-ri
│   ├── src
│   │   ├── main
│   │   │   └── java
│   │   ├── resources
│   │   │   └── META-INF
│   │   │       └── services
│   │   │           ├── format.bind.converter.spi.FieldConverterProvider
│   │   │           └── format.bind.spi.FormatProcessorFactory
│   │   ├── test
│   │   │   └── java
│   │   └── resources
│   └── build.gradle
├── .editorconfig
├── .gitattributes
├── .gitignore
├── build.gradle
├── gradlew
├── gradlew.bat
├── LICENSE.txt
└── settings.gradle
```

 4. In GitBash go to the root folder `jformatb` and perform a full build by executing the following command:

```bash
$ ./gradlew build
```

 5. After a successful build you can publish the artifacts to your Local Maven Repository by executing the following command:

```bash
$ ./gradlew publishToMavenLocal
```

Now the artifacts have been published to your Local Maven Repository usually located at `~/.m2/repository`. This allows you to use the generated artificats as any other Maven dependencies in your local environment.

### Usage

In your Java project add the following dependencies:

For Maven project:

```xml
<dependency>
  <groupId>io.github.jformatb</groupId>
  <artifactId>jformatb-api</artifactId>
  <version>0.1-SNAPSHOT</version>
</dependency>
<dependency>
  <groupId>io.github.jformatb</groupId>
  <artifactId>jformatb-ri</artifactId>
  <version>0.1-SNAPSHOT</version>
</dependency>
```

For Gradle project:

```groovy
dependencies {
    implementation "io.github.jformatb:jformatb-api:0.1-SNAPSHOT"
    implementation "io.github.jformatb:jformatb-ri:0.1-SNAPSHOT"
}
```

### Example

Now we are ready to use the Java Format Binding API. In this example let suppose that the message to convert consist of a monetary value formed by the [ISO 4217](https://en.wikipedia.org/wiki/ISO_4217) currency code and the value. An example value should be:

```md
USD000000100000
```
In this example the message just represents `$1,000.00` and can be converted in the following Java type:

```java
@Data
public class Amount {

    @FormatField
    private Currency currency;

    @FormatField
    private long value;

}
```

One of the key feature of the **Java Format Binding** is to define the way to bind text message fields to Java object properties. In this example the pattern to use could be:

```java
"${currency:3}${value:12}"
```
 
 Now we can invoke the entry point of the **Java Format Binding API** to convert the text message into a Java object of type `Amount` defined above with the following instruction:

```java
    Amount amount = Formatter.of(Amount.class)
            .withPattern("${currency:3}${value:12}")
            .parse("USD000000100000");
```

Et voilà ! We have made our first ever conversion of a simple text message into a Plain Old Java Object (POJO).

The official documentation will be available asap to explain in more details the API. In the mean time just browse the repository content and have a look to our JUnit tests.
 
## License

This code is licensed under the [Apache-2.0 license](https://www.apache.org/licenses/LICENSE-2.0). See `LICENSE.txt` for more information.
