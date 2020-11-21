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
  (->
    [:body
     body
     [:script {:src "https://unpkg.com/htmx.org@0.4.0"}]]
    html5
    html-response))

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
      (extract-subforms (drop 2 f)))
    (when (coll? f)
      (mapcat extract-subforms f))))

(defmacro make-routes [root f]
  (let [encoded (walk/postwalk encode-endpoints f)]
    `[~root
      {:middleware []}
      ["/" {:get (fn [req#] (-> ~encoded (expand-content req#) html5-response))}]
      ~@(extract-subforms encoded)]))
