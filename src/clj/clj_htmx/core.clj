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

(defn encode-endpoints [f]
  (if (and (seq? f) (= 'fn (first f)))
    (with-meta f {:endpoint (str "/" (gensym))})
    f))

(defn- hx-attrs [endpoint]
  {:hx-post endpoint :hx-swap "outerHTML"})
(defn assoc-hx [form endpoint]
  (when (vector? form)
    (if (-> form second map?)
      (update form 1 merge (hx-attrs endpoint))
      (vec
        (concat
          (take 1 form) [(hx-attrs endpoint)] (rest form))))))

(defn expand-form [f req]
  (if-let [endpoint (-> f meta :endpoint)]
    (-> req f (assoc-hx endpoint))
    f))
(defn expand-content [f req]
  (walk/prewalk #(expand-form % req) f))

(defn extract-subforms [f]
  (if-let [endpoint (-> f meta :endpoint)]
    (list*
      [endpoint {:post `(fn [req#]
                          (-> ~f (expand-content req#) snippet-response))}]
      (extract-subforms (rest f)))
    (when (coll? f)
      (mapcat extract-subforms f))))

(defmacro make-routes [root f]
  (let [encoded (walk/postwalk encode-endpoints f)]
    `[~root
      {:middleware []}
      ["" {:get (fn [req#] (-> ~encoded (expand-content req#) html5-response))}]
      ~@(extract-subforms encoded)]))
