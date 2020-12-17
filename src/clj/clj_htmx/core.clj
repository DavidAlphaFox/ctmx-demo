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

(defn- extract-endpoints [m]
  (cond
    (coll? m) (mapcat extract-endpoints m)
    (component-macro? m) (-> m eval :endpoints)))

(def parsers
  {:int #(list 'Integer/parseInt %)
   :lower #(list 'some-> % '.trim '.toLowerCase)
   :trim #(list 'some-> % '.trim)
   :string #(list 'or % "")
   :boolean #(list 'contains? #{"true" "on"} %)})

(defn sym->form [sym]
  (when (symbol? sym)
    (some (fn [[k f]]
            (when (-> sym meta k)
              (f sym)))
          parsers)))

;; args takes the form req followed by params
(defmacro defcomponent [name args & body]
  (let [expanded (walk/postwalk expand-components body)
        f (gensym)
        req (first args)
        bindings (subvec args 1)]
    `(def ~name
       (let [~f (fn [~req]
                  (let [{:keys ~bindings} (:params ~req)
                        ~@(for [var bindings
                                :let [form (sym->form var)]
                                :when form
                                x [var form]] x)]
                    (render/snippet-response
                      (do ~@expanded))))]
         {:fn (fn ~args ~@expanded)
          :endpoints ~(vec
                        (conj
                          (extract-endpoints body)
                          [(str "/" name) f]))}))))

(defmacro make-routes [root f]
  `[~root
    [~(if (= "" root) "/" "") {:get ~(walk/postwalk expand-components f)}]
    ~@(extract-endpoints f)])

(defmacro with-req [req & body]
  `(let [{:keys [~'request-method]} ~req
         ~'get? (= :get ~'request-method)
         ~'post? (= :post ~'request-method)
         ~'put? (= :put ~'request-method)
         ~'patch? (= :patch ~'request-method)
         ~'delete? (= :delete ~'request-method)]
     ~@body))
