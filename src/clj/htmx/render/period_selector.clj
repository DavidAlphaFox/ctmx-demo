(ns htmx.render.period-selector
  (:require
    [clj-htmx.core :as htmx]
    [htmx.render :as render]
    [htmx.util :as util]))

(defn- select [label options]
  [:div.col
   [:label label]
   [:select.form-control
    {:required true}
    [:option {:value ""} "Please Select"]
    (for [option options]
      [:option {:value option} option])]])

(defn- input [title m]
  [:div.col
   [:label title]
   [:input.form-control m]])

(defn text [title]
  (input title {:type "text" :required true :placeholder title}))
(defn number
  ([title] (number title false))
  ([title disabled?]
   (input title {:type "number" :min 1900 :max 2100 :required true :placeholder title :disabled disabled?})))

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
     {:disabled present :required true}
     [:option {:value ""} "Please Select"]
     (for [option months-not-specified]
       [:option {:value option} option])]]
   (number "To Year" present)])

(htmx/defcomponent period-selector [req]
  [:div
   [:div.row {:style "margin-top: 15px"}
    (select "From Month" months-not-specified)
    (number "From Year")]
   (to-row req false)])

(htmx/defcomponent subrole-selector [req ^:number k details]
  (let [details-label "Details.  Paragraphs separated with a blank line become bullet points."]
    [:div
     (when (> k 1)
       [:button.btn.btn-primary.mb2
        {:type "button"}
        "Remove Subrole"])
     [:div.row
      (text "Subrole")]
     (period-selector req)
     [:div.row
      (text "Location (optional)")]
     [:div {:style "margin-top: 15px"}
      [:label "Please provide details on this legal subrole and some brief examples of your past
       transactions/deals, technical details, levels of responsibility and key customers (where
       possible)."]
      [:label "Paragraphs separated with a blank line become bullet points."]
      [:textarea.form-control
       {:placeholder details-label
        :value details
        :rows 6}]]]))
