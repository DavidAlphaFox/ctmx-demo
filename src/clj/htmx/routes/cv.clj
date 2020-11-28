(ns htmx.routes.cv
  (:require
    [clj-htmx.core :as htmx]
    [htmx.persistence.cv :as persistence.cv]))

(defn form []
  [:form.mt-3
   [:h4.cv-header "Legal Roles"]
;;    <h4 className="cv-header">Legal Roles</h4>
;;                 <button
;;                     type="button"
;;                     className="btn btn-default btn-fixed float-right"
;;                     data-toggle="modal"
;;                     data-target="#newLegalRole"
;;                     onClick={() => this.freshLegalRole()}
;;                     id="newLegalRoleButton"
;;                     title="Please add Legal Role"
;;                     data-placement="left"
;;                     data-trigger="manual"
;;                 >
   ])

(defn cv-routes []
  (htmx/make-routes
    "/cv"
    [:div.container
     (form)]))
