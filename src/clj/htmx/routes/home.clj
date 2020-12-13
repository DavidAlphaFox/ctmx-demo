(ns htmx.routes.home
  (:require
    [clj-htmx.core :as htmx]
    [htmx.persistence.cv :as persistence.cv]
    [htmx.render :as render]))

(def a (atom 0))

(defn legal-role-modal [req]
  (let [title-tooltip "If you held multiple titles, please list the final / most senior position."]
    (render/modal-large
      "newLegalRole"
      "Legal Role"
      [:div
       [:div {:data-toggle "tooltip" :title title-tooltip}
        (render/text "Enter Title")]])))

(defn form [req]
  [:form.mt-3
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
        [:div.container
         (form req)]))))
