(ns clj-htmx.core
  (:require
    [clj-htmx.render :as render]
    [clojure.walk :as walk]))

(defn- component-macro? [x]
  (and
    (symbol? x)
    (resolve x)
    (let [evaluated (-> x resolve var-get)]
      (and
        (map? evaluated)
        (contains? evaluated :fn)
        (contains? evaluated :endpoints)))))

(defn- expand-components [x]
  (if (component-macro? x)
    `(:fn ~x)
    x))

(defn- mapmerge [f s]
  (apply merge (map f s)))

(defn extract-endpoints [m]
  (cond
    (coll? m) (mapmerge extract-endpoints m)
    (component-macro? m) (-> m eval :endpoints)))

(def parsers
  {:int #(list 'Integer/parseInt %)
   :lower #(list 'some-> % '.trim '.toLowerCase)
   :trim #(list 'some-> % '.trim)
   :string #(list 'or % "")
   :boolean #(list 'contains? #{"true" "on"} %)})

(defn sym->f [sym]
  (or
    (some (fn [[k f]]
            (when (-> sym meta k)
              f))
          parsers)
    identity))

(defn- make-f [args expanded]
  (case (count args)
    0 `(fn this# ([] ~@expanded) ([_#] (this#)))
    1 `(fn ~args ~@expanded)
    `(fn this#
       (~(subvec args 0 1)
         (this#
           ~(args 0)
           ~@(for [arg (rest args)]
               ((sym->f arg) `(-> ~(args 0) :params ~(keyword arg))))))
       (~args ~@expanded))))


;; args takes the form req followed by params
(defmacro defcomponent [name args & body]
  (let [expanded (walk/postwalk expand-components body)
        f (gensym)]
    `(def ~name
       (let [~f ~(make-f args expanded)]
         {:fn ~f
          :endpoints ~(assoc (extract-endpoints body) (keyword name) f)}))))

(defn extract-endpoints-all [f]
  (for [[k f] (extract-endpoints f)]
    [(str "/" (name k)) `(fn [x#] (-> x# ~f render/snippet-response))]))

(defmacro make-routes [root f]
  `[~root
    [~(if (= "" root) "/" "") {:get ~(walk/postwalk expand-components f)}]
    ~@(extract-endpoints-all f)])

(defmacro with-req [req & body]
  `(let [{:keys [~'request-method]} ~req
         ~'get? (= :get ~'request-method)
         ~'post? (= :post ~'request-method)
         ~'put? (= :put ~'request-method)
         ~'patch? (= :patch ~'request-method)
         ~'delete? (= :delete ~'request-method)]
     ~@body))
