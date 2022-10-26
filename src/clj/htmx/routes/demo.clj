(ns htmx.routes.demo
  (:require
    [ctmx.core :as ctmx :refer [defcomponent]]
    [ctmx.form :as form]
    [ctmx.response :as response]
    [ctmx.rt :as rt]
    [htmx.render :as render]
    [htmx.render.period-selector :as period-selector]
    [htmx.service.cv :as cv]
    [htmx.util :as util]))

(ctmx/defcomponent subrole-selector [req k _]
  (let [details-label "Details.  Paragraphs separated with a blank line become bullet points."]
    [:div
     (when (pos? k)
           [:button.btn.btn-primary.my-2
            {:type "button"
             :hx-patch "subroles"
             :hx-target (hash "../..")
             :hx-vals {:remove-subrole k}}
            "Remove Subrole"])
     [:div.row.mt-2
      (period-selector/text "Subrole" (path "title") (value "title") true)]
     (period-selector/period-selector req)
     [:div.row.mt-2
      (period-selector/text "Location (optional)" (path "location") (value "location") false)]
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

(defn remove-subrole [params to-remove]
  (-> params
      (update-in [:cv :legal-role-modal :legal-role-body :subroles] util/dissoc-i to-remove)
      (update :num-subroles #(-> % Long/parseLong dec))))

(defn remove-subrole-params [params _]
  (if-let [to-remove (-> params :remove-subrole rt/parse-long)]
    (form/apply-params params remove-subrole to-remove)
    params))

(defcomponent ^:endpoint subroles [req]
  (ctmx/update-params
    req remove-subrole-params
    (let [multiple-subroles ^:boolean (value "../multiple-subroles")
          num-subroles (or ^:long (value "num-subroles") 1)
          add-subrole ^:boolean (value "/add-subrole")
          num-subroles (if add-subrole (inc num-subroles) num-subroles)]
      (if multiple-subroles
        [:div {:id id}
         [:h4 "Subroles"]
         [:p "Please provide at least one subrole."]
         [:input {:type "hidden" :name (path "num-subroles") :value num-subroles}]
         (rt/map-indexed subrole-selector req (range num-subroles))
         [:br]
         [:button.btn.btn-primary
          {:type "button"
           :hx-patch "subroles"
           :hx-target (hash ".")
           :hx-vals {:add-subrole true}}
          "Add Subrole"]]
        [:div {:id id :style "margin-top: 15px"}
         [:label "Please provide details on this legal role and some brief examples of your past transactions/deals, technical details, levels of responsibility and key customers (where possible)."]
         [:label "Paragraphs separated with a blank line become bullet points."]
         [:textarea.form-control
          {:placeholder "Acting as lead lawyer, project management of corporate transactions, customer relationship management and supervision of junior staff."
           :rows "10"
           :required true
           :name (path "details")}
          (value "details")]]))))

(defn insert-cv [params index]
  (if-let [to-insert (get-in (cv/get-cv) [:previousLegalRoles index])]
    (assoc-in params [:cv :legal-role-modal :legal-role-body]
              (assoc to-insert :index index))
    params))
(defn insert-cv-params [index {:keys [request-method]} params]
  (if (and
        index
        (= :get request-method))
    (form/apply-params params insert-cv index)
    params))

(use 'clojure.pprint)
(defcomponent ^:endpoint legal-role-body [req ^:boolean multiple-subroles ^:long-option index]
  (ctmx/update-params
    req (fn [params _] (insert-cv-params index req params))
    (case (:request-method req)
      :post
      (let [role (-> req
                     :params
                     form/json-params-pruned
                     (dissoc :index))]
        (pprint role)
        (if index
          (cv/insert-legal-role index role)
          (cv/add-legal-role role))
        response/hx-refresh)
      :delete
      (do
        (cv/remove-legal-role index)
        response/hx-refresh)
      (let [title-tooltip "If you held multiple titles, please list the final / most senior position."
            subroles-tooltip "Multiple subroles may be due to holding various positions with one employer, or it may be due to multiple customer placements as a flexible legal consultant."]
        (list
          (when top-level?
            [:button.btn.btn-primary.float-right
             {:id (path "../save-button")
              :type "button"
              :onclick (render/submit (path "."))
              :hx-swap-oob "true"}
             (if index "Update" "Save")])
          (when top-level?
            (if index
              [:button.btn.btn-primary
               {:id (path "../delete")
                :type "button"
                :hx-delete "legal-role-body"
                :hx-vals {(path "index") index}
                :hx-confirm "Delete?"
                :hx-swap-oob "true"}
               "Delete"]
              [:div
               {:id (path "../delete")
                :style "display: none"
                :hx-swap-oob "true"}]))
          [:form {:id id
                  :hx-post "legal-role-body"}
           [:input {:name (path "index") :type "hidden" :value index}]
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
              multiple-subroles)]
           (subroles req)
           render/submit-hidden])))))

(defcomponent legal-role-modal [req]
  (render/modal-large
    "newLegalRole"
    "Legal Role"
    (legal-role-body req)
    [:div {:style "width: 100%"}
     ;; todo when editing only
     (render/placeholder (path "delete"))
     (render/placeholder (path "save-button"))]))

(defn- subrole-disp [s]
  [:li (pr-str s)])
(defn- detail-disp [s]
  [:li s])

(defcomponent legal-role [req i r]
  (let [{:keys [period-selector subroles]} r
        {:keys [from-month from-year to-row]} period-selector
        {:keys [present to-month to-year]} to-row
        from (if (= "Not Specified" from-month)
               from-year
               (str from-month " " from-year))
        to (cond
            present "Present"
            (= "Not Specified" to-month) to-year
            :else (str to-month " " to-year))]
    [:div
     [:button.btn.btn-primary.float-right
      {:type "button"
       :hx-get "legal-role-body"
       :hx-target (hash "../../legal-role-modal/legal-role-body")
       :hx-vals {(path "../../legal-role-modal/legal-role-body/index") i}
       :data-toggle "modal"
       :data-target "#newLegalRole"}
      "Edit"]
     [:div.mt-3
      [:div (:title r)]
      [:div (:company r) ", " (:location r)]
      [:div from " to " to]]
     [:ul
      (if-let [details (:details subroles)]
        (->> details render/para-split (map detail-disp)))]]))

(defcomponent ^:endpoint cv [req]
  (case (:request-method req)
        :delete
        (do
          (cv/cv-reset!)
          response/hx-refresh)
        (let [{:keys [previousLegalRoles]} (cv/get-cv)]
          [:div.mt-3
           [:h4 "Legal Roles"]
           [:button.btn.btn-primary
            {:type "button"
             :hx-delete "cv"} "Reset"]
           (legal-role-modal req)
           [:button.btn.btn-primary.float-right
            {:type "button"
             :hx-get "legal-role-body"
             :hx-target (hash "legal-role-modal/legal-role-body")
             :data-toggle "modal"
             :data-target "#newLegalRole"
             :title "Please add Legal Role"}
            "Add"]
           [:br]
           [:br]
           (rt/map-indexed legal-role req previousLegalRoles)])))

(defn demo-routes []
  (ctmx/make-routes
    "/demo"
    (fn [req]
      (render/html5-response
        "/home.js"
        [:div.container
         (cv req)]))))
