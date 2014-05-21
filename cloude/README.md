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




## Usage ##

Right now, Clou.de only supports a singleton workspace/project. When you start the application,
you'll be dropped into the main window. From here you can:

- Create a file by clicking on the new file button
- Add and remove run configurations by clicking on the "configure" button
- Build the project
- Run the project using the current run configuration
