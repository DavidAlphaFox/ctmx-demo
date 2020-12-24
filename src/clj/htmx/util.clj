(ns htmx.util)

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
