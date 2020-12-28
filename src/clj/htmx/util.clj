(ns htmx.util
  (:require
    [clojure.data.json :as json]))

(defn filter-vals
  ([m]
   (into {}
         (for [[k v] m :when v] [k v]))))

(defmacro $-> [binding & items]
  `(as-> ~binding ~'$
         ~@(for [item items]
             (if (symbol? item)
               `(~item ~'$)
               item))))

(def write-str json/write-str)

(defn dissoc-i [v i]
  (->> (assoc v i nil)
       (filter identity)
       vec))
