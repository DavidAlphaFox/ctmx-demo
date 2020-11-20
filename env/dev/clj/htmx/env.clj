(ns htmx.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [htmx.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[htmx started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[htmx has shut down successfully]=-"))
   :middleware wrap-dev})
