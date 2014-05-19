# ClouDe #
By Lee Avital

https://github.com/leeavital/Clou.de

## Install
Install javascript components (requires bower *and* npm)

```sh
$ cd src/main/webapp/
$ bower install
$ npm install
```

## Build & Run ##

```sh
$ cd ClouDe
$ ./sbt
> container:start
> browse
> ~ ;copy-resources;aux-compil
```


## Testing ##

To test, run
```sh
sbt test
```

If `browse` doesn't launch your browser, manually open [http://localhost:8080/](http://localhost:8080/) in your browser.
