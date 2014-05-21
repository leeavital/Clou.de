# ClouDe #
By Lee Avital

https://github.com/leeavital/Clou.de

## Install
Install javascript components (requires bower *and* npm)

    $ cd src/main/webapp/
    $ bower install
    $ npm install


Install scala dependencies:

    $ ./sbt compile

## Build & Run ##


    $ cd ClouDe
    $ ./sbt
    > container:start
    > browse
    > ~ ;copy-resources;aux-compil



## Testing ##

To test, run

    sbt test


If `browse` doesn't launch your browser, manually open [http://localhost:8080/](http://localhost:8080/) in your browser.
