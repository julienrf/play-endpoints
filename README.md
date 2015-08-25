# play-endpoints

Turn endpoint definitions into first class citizens.

## Inspiration

- [spray-routing](http://spray.io/documentation/1.2.3/spray-routing/)
- [finagle/finch](https://github.com/finagle/finch/blob/master/docs/index.md)
- [play-navigator](https://github.com/teamon/play-navigator)

## Roadmap

- [x] One endpoint, whose path is fixed, e.g. `GET /foo`, with a description
- [x] Routing
- [x] Reverse routing
- [x] Abstract documentation
- [x] HTML documentation
- [x] Several HTTP verbs (`GET`, `POST`)
- [x] Two endpoints, with fixed paths, e.g. `GET /foo` and `GET /bar`
- [x] Query string _parameters_ of type `String`, with a description
- [x] Path _parameters_
- [ ] Parameters of any type
- [ ] Request body schema (i.e. model an endpoint as an `A => Unit` value)
- [ ] Response body schema (i.e. model an endpoint as an `A => Future[B]` value)
- [ ] Request headers schema
- [ ] JavaScript reverse routing
- [ ] HTTP client
