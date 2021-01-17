(ns htmx.render.period-selector
  (:require
    [ctmx.core :as ctmx]
    [htmx.render :as render]
    [htmx.util :as util]))

(defn- select [label name options selected]
  [:div.col
   [:label label]
   [:select.form-control
    {:required true :name name}
    [:option {:value ""} "Please Select"]
    (for [option options]
      [:option {:value option :selected (= option selected)} option])]])

(defn- input [title m]
  [:div.col
   [:label title]
   [:input.form-control m]])

(defn text [title name value required?]
  (input title {:type "text"
                :name name
                :value value
                :required required?
                :placeholder title}))
(defn number
  ([title] (number title nil false))
  ([title name value disabled?]
   (input title {:type "number"
                 :min 1900
                 :max 2100
                 :name name
                 :value value
                 :required true
                 :placeholder title
                 :disabled disabled?})))

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

(ctmx/defcomponent ^:endpoint to-row [req ^:boolean present]
  [:div.row {:style "margin-top: 15px" :id id}
   [:div.col
    [:label "To Month"]
    [:input.form-check.form-check-inline.ml-2
     {:hx-get "to-row"
      :hx-target (hash ".")
      :hx-include (render/include-all id)
      :type "checkbox"
      :checked present
      :name (path "present")}]
    "Present"
    (when present ;; need to include this instead
      [:input {:type "hidden" :name (path "to-month") :value (value "to-month")}])
    [:select.custom-select
     {:disabled present :required true :name (path "to-month")}
     [:option {:value ""} "Please Select"]
     (ctmx/forall [option months-not-specified]
                  [:option {:value option :selected (= option (value "to-month"))} option])]]
   (when present ;; need to include this instead
     [:input {:type "hidden" :name (path "to-year") :value (value "to-year")}])
   (number "To Year" (path "to-year") (value "to-year") present)])

(ctmx/defcomponent period-selector [req]
  [:div
   [:div.row {:style "margin-top: 15px"}
    (select "From Month" (path "from-month") months-not-specified (value "from-month"))
    (number "From Year" (path "from-year") (value "from-year") false)]
   (to-row req (value "to-row_present"))])

(ctmx/defcomponent subrole-selector [req k]
  (let [details-label "Details.  Paragraphs separated with a blank line become bullet points."]
    [:div
     (when (> k 1)
       [:button.btn.btn-primary.my-2
        {:type "button"
         :hx-patch "subroles"
         :hx-target (hash "../..")
         :hx-vals (util/write-str {:remove-subrole k})}
        "Remove Subrole"])
     [:div.row
      (text "Subrole" (path "title") (value "title") true)]
     (period-selector req)
     [:div.row.mt-2
      (text "Location (optional)" (path "location") (value "location") false)]
     [:div {:style "margin-top: 15px"}
      [:label "Please provide details on this legal subrole and some brief examples of your past
       transactions/deals, technical details, levels of responsibility and key customers (where
       possible)."]
      [:label "Paragraphs separated with a blank line become bullet points."]
      [:textarea.form-control
       {:placeholder details-label
        :name (path "details")
        :rows 6}
       (value "details")]]]))
