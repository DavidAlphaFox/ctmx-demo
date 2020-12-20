(ns htmx.render.period-selector
  (:require
    [clj-htmx.core :as htmx]
    [htmx.render :as render]
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

(defn- input
  ([title] (input title false))
  ([title disabled?]
   [:div.col
    [:label title]
    [:input.form-control
     {:type "text"
      :placeholder title
      :disabled disabled?}]]))

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

(htmx/defendpoint to-row [req ^:boolean present]
  [:div.row {:style "margin-top: 15px" :id id}
   [:div.col
    [:label "To Month"]
    [:input.form-check.form-check-inline.ml-2
     (render/other-target
       "to-row"
       id
       :type "checkbox"
       :checked present
       :name "present")]
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

(htmx/defcomponent subrole-selector [req ^:number k details]
  (let [details-label "Details.  Paragraphs separated with a blank line become bullet points."]
    [:div
     (when (> k 1)
       [:button.btn.btn-primary.mb2
        {:type "button"}
        "Remove Subrole"])
     [:div.row
      (input "Subrole")]
     (period-selector req)
     [:div.row
      (input "Location (optional)")]
     [:div {:style "margin-top: 15px"}
      [:label "Please provide details on this legal subrole and some brief examples of your past
       transactions/deals, technical details, levels of responsibility and key customers (where
       possible)."]
      [:label "Paragraphs separated with a blank line become bullet points."]
      [:textarea.form-control
       {:placeholder details-label
        :value details
        :rows 6}]]]))
