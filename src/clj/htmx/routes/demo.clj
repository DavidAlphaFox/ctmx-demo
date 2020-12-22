(ns htmx.routes.demo
  (:require
    [clj-htmx.core :as htmx]
    [htmx.render :as render]))

(htmx/defendpoint hello [req my-name]
  [:div#hello "Hello " my-name])

(defn demo-routes []
  (htmx/make-routes
    "/demo"
    (fn [req]
      (render/html5-response
        [:div {:style "padding: 20px"}
         [:label {:style "margin-right: 10px"}
          "What is your name?"]
         [:input {:type "text"
                  :name "my-name"
                  :hx-patch "hello"
                  :hx-target "#hello"
                  :hx-swap "outerHTML"}]
         (hello req "")]))))
