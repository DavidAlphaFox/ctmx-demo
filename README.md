# clj-htmx

generated using Luminus version "3.88"

clj-htmx demonstrates how to generate powerful react-like web apps without any explicit serverside javascript.  It uses the frontend [htmx](https://htmx.org/) library which is only 9kb zipped.  Pages are fast and light.

htmx is based on the observation that the `a` and `form` elements are special because they request to the server and replace the dom with whatever html is returned.  htmx enables all elements to behave in this way whenever they are marked by `hx-*` attributes.

clj-htmx extends [hiccup](https://github.com/weavejester/hiccup) syntax to support htmx.  Simply place `:hx-get` `:hx-post` `:hx-put` `:hx-patch` `:hx-delete` functions within ordinary hiccup syntax.  The `htmx.core/make-routes` macro will move these functions into seperate endpoints and replace them with the associated url.

## Prerequisites

You will need [Leiningen][1] 2.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein run

Repl in as follows

    lein repl :connect 7000

## License

Copyright © 2020 Matthew Molloy
