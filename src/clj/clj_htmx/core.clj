(ns clj-htmx.core
  (:require
    [clj-htmx.render :as render]
    [clojure.walk :as walk]))

(def endpoints [:hx-get :hx-post :hx-put :hx-patch :hx-delete])
(def active-endpoints (rest endpoints))

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
  (if (and
        x
        (not (string? x))
        (not (keyword? x)))
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

(def ^:dynamic *form-endpoint*)

(defn expand-form-ref [x]
  (if (map? x)
    (if-let [active-endpoint
             (some #(when (-> % x (= :form)) %) active-endpoints)]
      (assoc x
        active-endpoint *form-endpoint*
        :hx-target (str "#" *form-endpoint*)
        :hx-swap "outerHTML")
      x)
    x))

(defmacro defform [name args body]
  (let [encoded (walk/postwalk assign-endpoints body)
        body (walk/postwalk expand-to-string encoded)
        f1 (gensym)
        f2 (gensym)
        endpoint (str (gensym))]
    `(def ~name
       (let [~f1 (fn ~args
                   (binding [*form-endpoint* ~endpoint]
                     (->> [:form {:id ~endpoint} ~body]
                          (walk/postwalk expand-form-ref))))
             ~f2 (fn ~args
                   (binding [*form-endpoint* ~endpoint]
                     (->> [:form {:id ~endpoint} ~body]
                          (walk/postwalk expand-form-ref)
                          render/snippet-response)))]
         {:fn ~f1
          :endpoints ~(vec
                        (conj
                          (extract-endpoints encoded)
                          [(str "/" endpoint) f2]))}))))

(defmacro make-routes [root f]
  (let [encoded (walk/postwalk assign-endpoints f)]
    `[~root
      [~(if (= "" root) "/" "") {:get ~(walk/postwalk expand-to-string encoded)}]
      ~@(extract-endpoints encoded)]))

(defmacro with-req [req & body]
  `(let [{:keys [~'request-method]} ~req
         ~'get? (= :get ~'request-method)
         ~'post? (= :post ~'request-method)
         ~'put? (= :put ~'request-method)
         ~'patch? (= :patch ~'request-method)
         ~'delete? (= :delete ~'request-method)]
     ~@body))
