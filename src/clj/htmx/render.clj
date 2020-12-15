(ns htmx.render
  (:require
    [hiccup.core :as hiccup]
    [hiccup.page :refer [html5]]
    [htmx.util :as util]
    [ring.util.http-response :refer [no-content]]))

(defn html-response [body]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body body})

(defn snippet-response [body]
  (if body
    (-> body hiccup/html html-response)
    (no-content)))

(defn html5-response [body]
  (html-response
    (html5
      [:head
       [:meta {:charset "utf-8"}]
       [:meta {:name "viewport"
               :content "width=device-width, initial-scale=1, shrink-to-fit=no"}]

       [:link {:rel "stylesheet"
               :href "https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/css/bootstrap.min.css"
               :integrity "sha384-TX8t27EcRE3e/ihU7zmQxVncDAy5uIKz4rEkgIXeMed4M0jlfIDPvg6uqKI2xXr2"
               :crossorigin "anonymous"}]]
      [:body
       body
       [:script {:src "https://code.jquery.com/jquery-3.5.1.slim.min.js"
                 :integrity "sha384-DfXdz2htPH0lsSSs5nCTpuj/zy4C+OGpamoFVy38MVBnE+IbbVYUew+OrCXaRkfj"
                 :crossorigin "anonymous"}]
       [:script {:src "https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/js/bootstrap.bundle.min.js"
                 :integrity "sha384-ho+j7jyWK8fNQe+A12Hb8AhRq26LrZ/JpcUGGOn+Y7RsweNrtN/tE3MoK7ZeZDyx"
                 :crossorigin "anonymous"}]
       [:script {:src "https://unpkg.com/htmx.org@0.4.0"}]
       [:script {:src "/js/helpers.js"}]])))

(defn modal-large
  ([id title body] (modal-large id title body nil))
  ([id title body footer]
   [:div.modal.fade
    {:id id
     :data-backdrop "static"}
    [:div.modal-dialog.modal-lg
     [:div.modal-content
      [:div.modal-header
       [:h5.modal-title title]
       [:button.close {:type "button" :data-dismiss "modal"}
        [:span "&times;"]]]
      [:div.modal-body body]
      [:div.modal-footer footer]]]]))

(defn input
  ([type title]
   (input type title title nil))
  ([type title placeholder]
   (input type title placeholder nil))
  ([type title placeholder value]
   [:div {:style "margin-top: 15px"}
    [:label title]
    [:input.form-control
     (util/filter-vals
       {:type type
        :placeholder placeholder
        :value value})]]))

(def text (partial input "text"))
(def email (partial input "email"))
(def number (partial input "number"))
(def password (partial input "password"))

(defn binary-radio [label]
  [:div {:style "margin-top: 15px"}
   [:label label]
   [:div.m-2
    [:input {:type "radio"
             :name "binary"
             :value "true"
             :hx-patch :form} "Yes"]]
   [:div.m-2
    [:input {:type "radio"
             :name "binary"
             :value "false"
             :hx-patch :form} "No"]]])
