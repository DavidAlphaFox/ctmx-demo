(ns htmx.routes.demo
  (:require
    [ctmx.core :as ctmx]
    [htmx.render :as render]))

(ctmx/defcomponent ^:endpoint hello [req ^:simple my-name]
  [:div#hello "Hello " my-name])

(defn demo-routes []
  (ctmx/make-routes
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
