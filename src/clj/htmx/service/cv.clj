(ns htmx.service.cv
  (:require
    [htmx.persistence.cv :as cv]))

(def add-legal-role #'cv/add-legal-role)
(def insert-legal-role #'cv/insert-legal-role)
(def remove-legal-role #'cv/remove-legal-role)
(def cv-reset! #'cv/cv-reset!)

(defn get-cv [] @cv/cv)
