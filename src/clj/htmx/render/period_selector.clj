(ns htmx.render.period-selector
  (:require
    [htmx.util :as util]))

(defn- select
  ([label options] (select label options nil))
  ([label options value]
   [:div.col
    [:label label]
    [:select.form-control
     (util/filter-vals
       {:value value})
     [:option "Please Select"]
     (for [option options]
       [:option {:value option} option])]]))

(defn- input [title]
  [:div.col
   [:label title]
   [:input.form-control
    {:type "text"
     :placeholder title
     ;:value value
     }]])

(def months '("January"
               "February"
               "March"
               "April"
               "May"
               "June"
               "July"
               "August"
               "September"
               "October"
               "November"
               "December"))
(def months-not-specified (conj months "Not Specified"))

(defn period-selector []
  [:div
   [:div.row {:style "margin-top: 15px"}
    (select "From Month" months-not-specified)
    (input "From Year")]
   [:div.row {:style "margin-top: 15px"}
    [:div.col
     [:label "To Month"]
     [:input.form-check.form-check-inline {:type "checkbox"}]
     "Present"
     [:select.custom-select
      [:option {:value "-2"} "Please Select"]
      (map-indexed
        (fn [i month]
          [:option {:value (str (dec i))} month])
        months-not-specified)]]
    (input "To Year")]])
