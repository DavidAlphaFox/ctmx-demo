(ns htmx.render.period-selector
  (:require
    [clj-htmx.core :as htmx]
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

(defn- input [title disabled?]
  [:div.col
   [:label title]
   [:input.form-control
    {:type "text"
     :placeholder title
     :disabled disabled?}]])

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

(htmx/defcomponent to-row [req ^:boolean present]
  [:div#to-row.row {:style "margin-top: 15px"}
   [:div.col
    [:label "To Month"]
    [:input.form-check.form-check-inline.ml-2
     {:type "checkbox"
      :hx-patch "to-row"
      :hx-target "#to-row"
      :hx-swap "outerHTML"
      :checked present
      :name "present"}]
    "Present"
    [:select.custom-select
     {:disabled present}
     [:option {:value "-2"} "Please Select"]
     (map-indexed
       (fn [i month]
         [:option {:value (str (dec i))} month])
       months-not-specified)]]
   (input "To Year" present)])

(htmx/defcomponent period-selector [req]
  [:div
   [:div.row {:style "margin-top: 15px"}
    (select "From Month" months-not-specified)
    (input "From Year" false)]
   (to-row req false)])
