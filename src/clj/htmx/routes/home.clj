(ns htmx.routes.home
  (:require
    [clj-htmx.core :as htmx]
    [htmx.persistence.cv :as persistence.cv]
    [htmx.render :as render]
    [htmx.render.period-selector :as period-selector]
    [htmx.util :as util]))

(htmx/defcomponent ^:endpoint subroles [req ^:boolean multiple-subroles]
  (let [{:keys [num-subroles submit-type]} params
        num-subroles (util/$-> num-subroles
                               htmx/parse-int
                               (or $ 2)
                               (if (= "inc-subroles" submit-type) (inc $) $))]
    (if multiple-subroles
      [:div {:id id}
       [:h4 "Subroles"]
       [:p "Please provide at least two subroles."]
       [:input {:type "hidden" :name "num-subroles" :value num-subroles}]
       (htmx/map-range period-selector/subrole-selector req num-subroles)
       [:br]
       [:button.btn.btn-primary
        {:button "button"}
        "Add Subrole"]]
      [:div {:id id :style "margin-top: 15px"}
       [:label "Please provide details on this legal role and some brief examples of your past transactions/deals, technical details, levels of responsibility and key customers (where possible)."]
       [:label "Paragraphs separated with a blank line become bullet points."]
       [:textarea.form-control
        {:placeholder "Acting as lead lawyer, project management of corporate transactions, customer relationship management and supervision of junior staff."
         :rows "10"
         :required true
         :name (path "details")}
        (value "details")]])))

(htmx/defcomponent ^:endpoint legal-role-body [req]
  (let [title-tooltip "If you held multiple titles, please list the final / most senior position."
        subroles-tooltip "Multiple subroles may be due to holding various positions with one employer, or it may be due to multiple customer placements as a flexible legal consultant."
        multiple-subroles? (-> "multiple-subroles" value htmx/parse-boolean)]
    (prn 'params params)
    [:form
     {:id id
      :hx-post "legal-role-body"
      :hx-swap "outerHTML"}
     [:div {:data-toggle "tooltip" :title title-tooltip}
      (render/text "Job Title" (path "title") (value "title"))]
     (render/text "Company Name" (path "company") (value "company"))
     (period-selector/period-selector req)
     (render/text "Location" (path "location") (value "location"))
     [:div {:data-toggle "tooltip" :title subroles-tooltip}
      (render/binary-radio
        "subroles"
        (path "multiple-subroles")
        (path "subroles")
        "Did your work involve multiple subroles?"
        multiple-subroles?)]
     (subroles req multiple-subroles?)
     render/submit-hidden]))

(htmx/defcomponent legal-role-modal [req]
  (render/modal-large
    "newLegalRole"
    "Legal Role"
    (legal-role-body req)
    [:div {:style "width: 100%"}
     ;; todo when editing only
     [:button.btn.btn-primary
      {:type "button"
       :hx-delete "legal-role-body"
       :hx-swap "outerHTML"
       :hx-target (str "#" (path "legal-role-body"))
       :hx-include (format "#%s *" (path "legal-role-body"))}
      "Delete"]
     [:button.btn.btn-primary.float-right
      {:type "button"
       :onclick (render/submit (path "legal-role-body"))}
      "Save"]]))


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
    "/"
    (fn [req]
      (render/html5-response
        "/home.js"
        [:div.container
         (form req)]))))
