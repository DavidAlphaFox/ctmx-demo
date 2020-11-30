(ns clj-htmx.core
  (:require
    [clojure.walk :as walk]))

(def endpoints [:hx-get :hx-post :hx-put :hx-patch :hx-delete])

(defn updates [m f ks]
  (reduce
    (fn [m k]
      (if (contains? m k)
        (update m k f)
        m))
    m
    ks))

(defn assign-endpoint [x]
  (if (and x (not (string? x)))
    (with-meta x {:endpoint (str (gensym))})
    x))
(defn assign-endpoints [m]
  (if (map? m)
    (updates m assign-endpoint endpoints)
    m))

(defn expand-to-string [x]
  (or (-> x meta :endpoint) x))

(defn extract-endpoint [k v]
  (when-let [endpoint (-> v meta :endpoint)]
    [(str "/" endpoint) {(-> k name (.replace "hx-" "") keyword) v}]))
(defn extract-endpoints [m]
  (cond
    (map? m) (map #(extract-endpoint % (m %)) endpoints)
    (coll? m) (mapcat extract-endpoints m)))

(defmacro make-routes [root f]
  (let [encoded (walk/postwalk assign-endpoints f)]
    `[~root
      {:middleware []}
      [~(if (= "" root) "/" "") {:get ~(walk/postwalk expand-to-string encoded)}]
      ~@(extract-endpoints encoded)]))
