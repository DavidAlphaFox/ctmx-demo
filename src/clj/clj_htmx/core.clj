(ns clj-htmx.core
  (:require
    [clojure.walk :as walk]
    [hiccup.core :as hiccup]
    [hiccup.page :refer [html5]]
    [ring.util.http-response :refer [no-content]]))

(defn html-response [body]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body body})

(defn html-response-snippet [body]
  (-> body hiccup/html html-response))
(defn html5-response [body]
  (->
    [:body
     body
     [:script {:src "https://unpkg.com/htmx.org@0.4.0"}]]
    html5
    html-response))

(defn encode-endpoints [f]
  (if (fn? f)
    (with-meta f {:endpoint (str "/" (gensym))})
    f))

(defn assoc-hx [form endpoint]
  (when (vector? form)
    (if (-> form second map?)
      (assoc-in form [1 :hx-post] endpoint)
      (vec
        (concat
          (take 1 form) [{:hx-post endpoint}] (rest form))))))

(defn expand-form [f req]
  (if (fn? f)
    (assoc-hx (f req) (-> f meta :endpoint))
    f))
(defn expand-content [f req]
  (walk/postwalk #(expand-form % req) f))

(defn vec* [& forms]
  (vec
    (apply list* forms)))

(defn wrap-f [f]
  (fn [req]
    (or
      (some-> req f (assoc-hx (-> f meta :endpoint)) html-response-snippet)
      (no-content))))

(defn extract-subforms [f]
  (cond
    (fn? f)
    [[(-> f meta :endpoint) {:post (wrap-f f)}]]
    (coll? f)
    (mapcat extract-subforms f)))

(defn make-routes [root f]
  (let [encoded (walk/postwalk encode-endpoints f)]
    (vec*
      root
      {:middleware []}
      ["/" {:get (fn [req] (-> encoded (expand-content req) html5-response))}]
      (extract-subforms encoded))))
