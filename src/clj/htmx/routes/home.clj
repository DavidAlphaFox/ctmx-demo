(ns htmx.routes.home
  (:require
    [clj-htmx.core :as htmx]
    [clj-htmx.form :as form]
    [clj-htmx.response :as response]
    [htmx.persistence.cv :as persistence.cv]
    [htmx.render :as render]
    [htmx.render.period-selector :as period-selector]
    [htmx.service.cv :as cv]
    [htmx.util :as util]))

(defn dissoc-i [v i]
  (->> (assoc v i nil)
       (filter identity)
       vec))

(defn remove-subrole [params to-remove]
  (-> params
      (update-in [:cv :legal-role-modal :legal-role-body :subroles] dissoc-i to-remove)
      (update :num-subroles #(-> % Integer/parseInt dec))))

(defn remove-subrole-params [params]
  (if-let [to-remove (-> params :remove-subrole htmx/parse-int)]
    (form/apply-params params remove-subrole to-remove)
    params))

(htmx/defcomponent ^:endpoint subroles [req]
  (htmx/update-params
    remove-subrole-params
    (let [multiple-subroles ^:boolean (value "../multiple-subroles")
          num-subroles (or ^:int (value "/num-subroles") 2)
          add-subrole ^:boolean (value "/add-subrole")
          num-subroles (if add-subrole (inc num-subroles) num-subroles)]
      (if multiple-subroles
        [:div {:id id}
         [:h4 "Subroles"]
         [:p "Please provide at least two subroles."]
         [:input {:type "hidden" :name "num-subroles" :value num-subroles}]
         (htmx/map-range period-selector/subrole-selector req num-subroles)
         [:br]
         [:button.btn.btn-primary
          {:type "button"
           :hx-patch "subroles"
           :hx-target (hash ".")
           :hx-vals (util/write-str {:add-subrole true})}
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

(htmx/defcomponent ^:endpoint legal-role-body [req ^:boolean multiple-subroles ^:int index]
  (htmx/update-params
    #(insert-cv-params index req %)
    (htmx/with-req req
      (if post?
        (let [json-params (form/json-params-pruned htmx/*params*)]
          (-> json-params
              (dissoc :index)
              cv/add-legal-role)
          response/hx-refresh)
        (let [title-tooltip "If you held multiple titles, please list the final / most senior position."
              subroles-tooltip "Multiple subroles may be due to holding various positions with one employer, or it may be due to multiple customer placements as a flexible legal consultant."]
          (list
            (when hx-request?
              [:button.btn.btn-primary.float-right
               {:id (path "../save-button")
                :type "button"
                :onclick (render/submit (path "legal-role-body"))
                :hx-swap-oob "true"}
               (if index "Update" "Save")])
            [:form {:id id
                    :hx-post "legal-role-body"}
             [:input {:id (path "index") :type "hidden" :value index}]
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
             render/submit-hidden]))))))

(htmx/defcomponent legal-role-modal [req]
  (render/modal-large
    "newLegalRole"
    "Legal Role"
    (legal-role-body req)
    [:div {:style "width: 100%"}
     ;; todo when editing only
     #_[:button.btn.btn-primary
        {:type "button"
         :hx-delete "legal-role-body"
         :hx-target (hash "legal-role-body")
         :hx-include (render/include-all (path "legal-role-body"))}
        "Delete"]
     [:button.btn.btn-primary.float-right
      {:id (path "save-button")
       :type "button"
       :onclick (render/submit (path "legal-role-body"))}
      "Save"]]))

(defn- subrole-disp [s]
  [:li (pr-str s)])
(defn- detail-disp [s]
  [:li s])

(htmx/defcomponent legal-role [req i r]
  (let [{:keys [period-selector subroles]} r
        {:keys [from-month from-year to-row]} period-selector
        from (if (= "Not Specified" from-month)
               from-year
               (str from-month " " from-year))
        to (if (:present to-row) "Present" "todo")]
    [:div
     [:button.btn.btn-primary.float-right
      {:type "button"
       :hx-get "legal-role-body"
       :hx-target (hash "../../legal-role-modal/legal-role-body")
       :hx-vals (util/write-str
                  {(path "../../legal-role-modal/legal-role-body/index") i})
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

(htmx/defcomponent cv [req]
  (let [{:keys [previousLegalRoles]} (cv/get-cv)]
    [:div.mt-3
     [:h4 "Legal Roles"]
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
     (htmx/map-indexed legal-role req previousLegalRoles)]))

(defn home-routes []
  (htmx/make-routes
    "/"
    (fn [req]
      (render/html5-response
        "/home.js"
        [:div.container
         (cv req)]))))
