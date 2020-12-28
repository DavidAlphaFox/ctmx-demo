(ns htmx.service.cv
  (:require
    [htmx.persistence.cv :as cv]))

(def add-legal-role cv/add-legal-role)
(defn get-cv [] @cv/cv)
