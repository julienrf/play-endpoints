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
- [ ] Abstract documentation
- [ ] Several HTTP verbs (`GET`, `POST`)
- [ ] HTML documentation
- [ ] Two endpoints, with fixed paths, e.g. `GET /foo` and `GET /bar`
- [ ] Query string _parameters_ of type `String`, with a description
- [ ] Path _parameters_
- [ ] Parameters of any type
- [ ] Request body schema
- [ ] Response body schema
- [ ] Request headers schema
- [ ] JavaScript reverse routing
- [ ] HTTP client
