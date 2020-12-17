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

;; args takes the form req followed by params
(defmacro defcomponent [name args body]
  (let [expanded (walk/postwalk expand-components body)
        f (gensym)
        req (first args)
        bindings (subvec args 1)]
    `(def ~name
       (let [~f (fn [~req]
                  (let [{:keys ~bindings} (:params ~req)]
                    (render/snippet-response ~expanded)))]
         {:fn (fn ~args ~expanded)
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
