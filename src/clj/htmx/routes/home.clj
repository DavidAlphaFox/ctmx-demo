(ns htmx.routes.home
  (:require
    [clj-htmx.core :as htmx]))

(def a (atom 0))

(defn home-routes []
  (htmx/make-routes
    ""
    [:div
     [:h2 "Click the number below"]
     (fn [req]
       [:div (swap! a inc)])]))
