# faker
[![version](https://img.shields.io/maven-metadata/v.svg?label=release&metadataUrl=https://repo1.maven.org/maven2/com/arakelian/faker/maven-metadata.xml)](https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.arakelian%22%20AND%20a%3A%22faker%22)
[![CI](https://github.com/arakelian/faker/actions/workflows/ci.yml/badge.svg)](https://github.com/arakelian/faker/actions/workflows/ci.yml)

Faker is library for generating high-quality fake data, such as names, addresses and phone numbers. 
Faker is still under development, and will evolved in the coming months.

Using Faker is very easy.

## Requirements

* Versions < 4.0.0 require Java 8+
* Version 4+ require Java 11+

## Generating People

Let's generate a random person.  

```
Person person = RandomPerson.get().next()
```

If we want a list of 20 random people, we would call:

```
List<Person> people = RandomPerson.get().listOf(20);
```

If you use Jackson to serialize a person, you'll see something like this:

```
{
  "firstName" : "PAM",
  "lastName" : "GALLINGER",
  "title" : "Carpet, Floor, and Tile Installer",
  "gender" : "FEMALE",
  "birthdate" : "1987-07-13T22:49:34.743000000Z",
  "age" : 30
}
``` 

## Generating Addresses

Let's generate a random address.  

```
Address address = RandomAddress.get().next()
```

If we want a list of 20 random addresses, we would call:

```
List<Address> addresses = RandomAddress.get().listOf(20);
```

If you use Jackson to serialize an address, you'll see something like this:

```
{
  "street" : "60 ENGLISH ST",
  "city" : "SAN FRANCISCO",
  "state" : "CA",
  "postalCode" : "94105"
}
``` 

## Installation

The library is available on [Maven Central](https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.arakelian%22%20AND%20a%3A%22faker%22).

### Maven

Add the following to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>central</id>
        <name>Central Repository</name>
        <url>http://repo.maven.apache.org/maven2</url>
        <releases>
            <enabled>true</enabled>
        </releases>
    </repository>
</repositories>

...

<dependency>
    <groupId>com.arakelian</groupId>
    <artifactId>faker</artifactId>
    <version>4.0.1</version>
    <scope>test</scope>
</dependency>
```

### Gradle

Add the following to your `build.gradle`:

```groovy
repositories {
  mavenCentral()
}

dependencies {
  testCompile 'com.arakelian:faker:4.0.1'
}
```

## Licence

Apache Version 2.0
