(ns htmx.routes.home
  (:require
    [clj-htmx.core :as htmx]
    [hiccup.core :as hiccup]
    [hiccup.page :refer [html5]]
    [htmx.persistence.cv :as persistence.cv]
    [ring.util.http-response :refer [no-content]]))

(defn html-response [body]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body body})

(defn snippet-response [body]
  (if body
    (-> body hiccup/html html-response)
    (no-content)))
(defn html5-response [body]
  (html-response
    (html5
      [:head
       [:meta {:charset "utf-8"}]
       [:meta {:name "viewport"
               :content "width=device-width, initial-scale=1, shrink-to-fit=no"}]

       [:link {:rel "stylesheet"
               :href "https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/css/bootstrap.min.css"
               :integrity "sha384-TX8t27EcRE3e/ihU7zmQxVncDAy5uIKz4rEkgIXeMed4M0jlfIDPvg6uqKI2xXr2"
               :crossorigin "anonymous"}]]
      [:body
       body
       [:script {:src "https://code.jquery.com/jquery-3.5.1.slim.min.js"
                 :integrity "sha384-DfXdz2htPH0lsSSs5nCTpuj/zy4C+OGpamoFVy38MVBnE+IbbVYUew+OrCXaRkfj"
                 :crossorigin "anonymous"}]
       [:script {:src "https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/js/bootstrap.bundle.min.js"
                 :integrity "sha384-ho+j7jyWK8fNQe+A12Hb8AhRq26LrZ/JpcUGGOn+Y7RsweNrtN/tE3MoK7ZeZDyx"
                 :crossorigin "anonymous"}]
       [:script {:src "https://unpkg.com/htmx.org@0.4.0"}]])))

(def a (atom 0))

(defn form [req]
  [:form.mt-3
   [:h4 "Legal Roles"]
   [:button.btn.btn-default.btn-fixed.float-right
    {:type "button"
     :data-toggle "modal"
     :data-target "#newLegalRole"
     :title "Please add Legal Roles"}]])

(defn update-div [_]
  (snippet-response (swap! a inc)))

(defn home-routes []
  (htmx/make-routes
    ""
    (fn [req]
      (html5-response
        [:div {:hx-get update-div} "hi"]))))
