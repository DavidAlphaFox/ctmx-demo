(ns htmx.routes.home
  (:require
    [clj-htmx.core :as htmx]
    [htmx.persistence.cv :as persistence.cv]
    [htmx.render :as render]
    [htmx.render.period-selector :as period-selector]))

(htmx/defendpoint subroles [req ^:boolean multiple-subroles subroles]
  (if multiple-subroles
    [:div {:id id}
     [:h4 "Subroles"]
     [:p "Please provide at least two subroles."]
     (htmx/map-indexed-stack #(period-selector/subrole-selector req %1 %2) (or subroles ["" ""]))
     [:br]
     [:button.btn.btn-primary
      {:button "button"}
      "Add Subrole"]]
    [:div {:id id :style "margin-top: 15px"}
     [:label "Please provide details on this legal role and some brief examples of your past transactions/deals, technical details, levels of responsibility and key customers (where possible)."]
     [:label "Paragraphs separated with a blank line become bullet points."]
     [:textarea.form-control
      {:placeholder "Acting as lead lawyer, project management of corporate transactions, customer relationship management and supervision of junior staff."
       :rows "10"}]]))

(htmx/defcomponent legal-role-body [req]
  (let [title-tooltip "If you held multiple titles, please list the final / most senior position."
        subroles-tooltip "Multiple subroles may be due to holding various positions with one employer, or it may be due to multiple customer placements as a flexible legal consultant."]
    [:form#new-legal-role
     [:div {:data-toggle "tooltip" :title title-tooltip}
      (render/text "Job Title")]
     (render/text "Company Name")
     (period-selector/period-selector req)
     (render/text "Location")
     [:div {:data-toggle "tooltip" :title subroles-tooltip}
      (render/binary-radio
        "subroles"
        "multiple-subroles"
        (str id "subroles")
        "Did your work involve multiple subroles?"
        false)]
     (subroles req false ["" ""])]))

(htmx/defcomponent legal-role-modal [req]
  (render/modal-large
    "newLegalRole"
    "Legal Role"
    (legal-role-body req)
    [:div {:style "width: 100%"}
     ;; todo when editing only
     [:button.btn.btn-primary
      {:type "button"}
      "Delete"]
     [:input.btn.btn-primary.float-right
      {:type "submit"
       :form "new-legal-role"
       :value "Save"}]]))


(htmx/defcomponent form [req]
  [:div.mt-3
   [:h4 "Legal Roles"]
   (legal-role-modal req)
   [:button.btn.btn-primary.float-right
    {:type "button"
     :data-toggle "modal"
     :data-target "#newLegalRole"
     :title "Please add Legal Roles"}
    "Add"]])

(defn home-routes []
  (htmx/make-routes
    ""
    (fn [req]
      (render/html5-response
        "/home.js"
        [:div.container
         (form req)]))))
