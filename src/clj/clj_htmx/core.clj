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

;; we don't need to expand endpoints here
;; this is only tagging map values.
(defn assign-endpoint [x]
  (if (and x (not (string? x)))
    (with-meta x {:endpoint (str (gensym))})
    x))
(defn assign-endpoints [m]
  (if (map? m)
    (updates m assign-endpoint endpoints)
    m))

(defn- component-macro? [x]
  (and
    (symbol? x)
    (resolve x)
    (let [evaluated (-> x resolve var-get)]
      (and
        (map? evaluated)
        (contains? evaluated :fn)
        (contains? evaluated :endpoints)))))

(defn expand-to-string [x]
  (or
    (-> x meta :endpoint)
    (when (component-macro? x)
      `(:fn ~x))
    x))

(defn extract-endpoint [k v]
  (when-let [endpoint (-> v meta :endpoint)]
    [(str "/" endpoint) {(-> k name (.replace "hx-" "") keyword) v}]))
(defn extract-endpoints [m]
  (cond
    (map? m) (->> endpoints
                  (map #(extract-endpoint % (m %)))
                  (filter identity))
    (coll? m) (mapcat extract-endpoints m)
    (component-macro? m) (-> m eval :endpoints)))

(defmacro defcomponent [name & rest]
  (let [encoded (walk/postwalk assign-endpoints rest)]
    `(def ~name
       {:fn (fn ~@(walk/postwalk expand-to-string encoded))
        :endpoints ~(vec (extract-endpoints encoded))})))

(defmacro make-routes [root f]
  (let [encoded (walk/postwalk assign-endpoints f)]
    `[~root
      {:middleware []}
      [~(if (= "" root) "/" "") {:get ~(walk/postwalk expand-to-string encoded)}]
      ~@(extract-endpoints encoded)]))
