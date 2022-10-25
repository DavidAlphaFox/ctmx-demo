(ns htmx.routes.home
  (:require
    [ctmx.core :as ctmx :refer [defcomponent]]
    [htmx.render :as render]))

(defcomponent ^:endpoint hello [req ^:simple my-name]
  [:div#hello "Hello " my-name])

(defn home-routes []
  (ctmx/make-routes
    "/"
    (fn [req]
      (render/html5-response
        [:div {:style "padding: 20px"}
         [:div.mb-3
          [:a {:href "demo"} "More complex demo"]]
         [:label {:style "margin-right: 10px"}
          "What is your name?"]
         [:input {:type "text"
                  :name "my-name"
                  :hx-patch "hello"
                  :hx-target "#hello"
                  :hx-swap "outerHTML"}]
         (hello req "")]))))
