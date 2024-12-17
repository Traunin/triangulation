# Triangulation

This is the library used for polygon triangulation. Can be used for polygon rasterization by splitting them into triangles. If a polygon is in 3d space, it has to be rotated parallel to one of coordinate planes.

## Installation

### Using Maven Central Repository

https://central.sonatype.com/artifact/io.github.traunin/triangulation

Maven example for `pom.xml`:

```xml
<dependency>
  <groupId>io.github.traunin</groupId>
  <artifactId>triangulation</artifactId>
  <version>1.1.0</version>
</dependency>
```

Gradle (Kotlin) example for `build.gradle.kts`:

```kts
implementation("io.github.traunin:triangulation:1.1.0")
```


If you don't want to build the library yourself, you can download the jar file from the releases tab.

### Building

Clone the repository:

```sh
git clone https://github.com/traunin/triangulation
```

Or download the code manually and extract it in the current folder.

Go into the folder:

```sh
cd triangulation
```

Run:

```sh
.\gradlew build
```

The jar file is generated in /lib/build/libs/.


## Importing

### Gradle

In the `app` directory create the `lib` folder (or name as you wish) at the same level as the build file and the `src` folder. Place the jar file in that directory.

Then, modify the corresponding build file:

`build.gradle`:

```groovy
dependencies {
    # other dependencies above
    implementation files('lib/triangulation-1.1.0.jar')
}
```

`build.gradle.kts`:

```kts
dependencies {
    // other dependencies above
    implementation(files("lib/triangulation-1.1.0.jar"))
}
```

And rebuild the project. The package should be available for import.

## Usage

```java
// implement Vector2f interface in your vector
List<Vector2f> vertices = Arrays.asList(
    new Vector2f(0, 0),
    new Vector2f(1, 0),
    new Vector2f(1, 1),
    new Vector2f(0, 1)
);
List<Integer> vertexIndices = Arrays.asList(0, 1, 2, 3)
// ear clipping triangulation, suitable for concave polygons
List<int[]> triangles = Triangulation.earClippingTriangulate(vertices, vertexIndices)
// convex polygon triangulation, produces a fan triangulation
List<int[]> triangles = Triangulation.convexPolygonTriangulate(vertices, vertexIndices)
```
