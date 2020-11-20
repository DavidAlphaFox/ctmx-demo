# clj-htmx

generated using Luminus version "3.88"

clj-htmx demonstrates how to generate powerful react-like web apps without any explicit serverside javascript.  It uses the frontend [htmx](https://htmx.org/) library which is only 9kb zipped.  Pages are fast and light.

htmx is based on the observation that the `a` and `form` elements are special because they request to the server and replace the dom with whatever html is returned.  htmx enables all elements to behave in this way whenever they are marked by an `hx-post` attribute.

Using clojure we can abstract a step further when we generate html on the backend.

```clojure
(def a (atom 0))

[:div
  [:h2 "Click the number below"]
  (fn [req]
    ;; db operations etc here
    [:div (swap! a inc)])]
```

Anything that is wrapped inside a function becomes dynamic content.  clj-htmx automatically generates endpoints to update as needed.

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
