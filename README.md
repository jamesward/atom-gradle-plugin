# Atom Gradle Plugin

## Usage

Add the plugin in your `build.gradle` file:

    plugins {
        id 'com.jamesward.atomgradleplugin' version '0.0.2'
    }

Download and launch Atom:

    ./gradlew atom

Optional Settings (`build.gradle`):

    atom.version = '1.0.0'               // default is the latest release from: https://github.com/atom/atom/releases
    atom.excludePrereleases = false      // default is true
    atom.os = 'Windows'                  // default is your OS
    atom.packages = ['heroku-tools']     // default is []
    atom.filesToOpen = ['README.md']     // default is ['./'] i.e. the project dir
    atom.home = file('/foo')             // default is ~/.atom

## Developer Info

Run the tests:

    ./gradlew -i test

Try the plugin in the test project:

    ./gradlew build
    cd test-project
    ./gradlew atom

Release:

    # update version in README.md
    # update version in build.gradle
    git tag v0.0.x
    git push --tags
    ./gradlew publishPlugins
